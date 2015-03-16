/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ning.xyw.androidmanager.shell;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import ning.xyw.androidmanager.util.L;

public class ShellProcess {
    private static final String TAG = "ShellProcess";
    private static ShellProcess instance;
    private Process mProcess;
    private OutputStream mOut;
    private InputStream mErrIn;
    private InputStream mIn;

    private ReadListener mListener;

    private Thread mReaderThread;
    private ByteQueue mReaderQueue;
    private byte[] mReceiveBuffer;

    private Thread mErrReaderThread;
    private ByteQueue mErrReaderQueue;
    private byte[] mErrReceiveBuffer;

    private Thread mWriterThread;
    private ByteQueue mWriteQueue;
    private Handler mWriterHandler;

    private static final int NEW_INPUT = 1;
    private static final int NEW_ERR_INPUT = 2;
    private static final int NEW_OUTPUT = 3;
    private static final int FINISH = 4;

    private boolean mIsRunning = false;
    private Handler mMsgHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!mIsRunning) {
                return;
            }
            switch (msg.what) {
                case NEW_INPUT:
                    readFromProcess();
                    break;
                case NEW_ERR_INPUT:
                    readErrFromProcess();
                    break;
            }
        }
    };

    public static ShellProcess getInstance() {
        if (null == instance) {
            instance = new ShellProcess();
            instance.write("su");
        }
        return instance;
    }

    private ShellProcess() {
        init();
    }

    public ShellProcess(ReadListener readListener) {
        init();
        this.mListener = readListener;
        write("su");
    }

    private void init() {
        try {
            mProcess = Runtime.getRuntime().exec("sh");
            mOut = mProcess.getOutputStream();
            mErrIn = mProcess.getErrorStream();
            mIn = mProcess.getInputStream();
            createReaderThread();
            createErrReaderThread();
            createWriterThread();
            mReaderThread.start();
            mErrReaderThread.start();
            mWriterThread.start();
        } catch (IOException e) {
            L.e(TAG, e.getMessage());
        }
    }

    private void createReaderThread() {
        mReaderQueue = new ByteQueue(4 * 1024);
        mReceiveBuffer = new byte[4 * 1024];
        mReaderThread = new Thread() {
            private byte[] mBuffer = new byte[4096];

            @Override
            public void run() {
                try {
                    mIsRunning = true;
                    while (mIsRunning) {
                        int read = mIn.read(mBuffer);
                        if (read == -1) {
                            mIsRunning = false;
                            // EOF -- process exited
                            return;
                        }
                        int offset = 0;
                        while (read > 0) {
                            int written = mReaderQueue.write(mBuffer,
                                    offset, read);
                            offset += written;
                            read -= written;
                            mMsgHandler.sendMessage(
                                    mMsgHandler.obtainMessage(NEW_INPUT));
                        }
                    }
                } catch (IOException e) {
                    L.e(TAG, e.getMessage());
                } catch (InterruptedException e) {
                    L.e(TAG, e.getMessage());
                }
            }
        };
    }

    private void createErrReaderThread() {
        mErrReaderQueue = new ByteQueue(4 * 1024);
        mErrReceiveBuffer = new byte[4 * 1024];
        mErrReaderThread = new Thread() {
            private byte[] mErrBuffer = new byte[4096];

            @Override
            public void run() {
                try {
                    mIsRunning = true;
                    while (mIsRunning) {
                        int readErr = mErrIn.read(mErrBuffer);
                        if (readErr == -1) {
                            // EOF -- process exited
                            mIsRunning = false;
                            return;
                        }
                        int offsetErr = 0;
                        while (readErr > 0) {
                            int writtenErr = mErrReaderQueue.write(mErrBuffer,
                                    offsetErr, readErr);
                            offsetErr += writtenErr;
                            readErr -= writtenErr;
                            mMsgHandler.sendMessage(
                                    mMsgHandler.obtainMessage(NEW_ERR_INPUT));
                        }
                    }
                } catch (IOException e) {
                    L.e(TAG, e.getMessage());
                } catch (InterruptedException e) {
                    L.e(TAG, e.getMessage());
                }
            }
        };
    }

    private void createWriterThread() {
        mWriteQueue = new ByteQueue(4096);
        mWriterThread = new Thread() {
            private byte[] mBuffer = new byte[4096];

            @Override
            public void run() {
                Looper.prepare();
                mWriterHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == NEW_OUTPUT) {
                            writeToOutput();
                        } else if (msg.what == FINISH) {
                            Looper.myLooper().quit();
                        }
                    }
                };
                // Drain anything in the queue from before we started
                writeToOutput();
                Looper.loop();
            }

            private void writeToOutput() {
                ByteQueue writeQueue = mWriteQueue;
                byte[] buffer = mBuffer;
                OutputStream termOut = mOut;
                int bytesAvailable = writeQueue.getBytesAvailable();
                int bytesToWrite = Math.min(bytesAvailable, buffer.length);
                if (bytesToWrite == 0) {
                    return;
                }
                try {
                    writeQueue.read(buffer, 0, bytesToWrite);
                    termOut.write(buffer, 0, bytesToWrite);
                    termOut.flush();

                } catch (IOException e) {
                    // Ignore exception
                    // We don't really care if the receiver isn't listening.
                    // We just make a best effort to answer the query.
                    L.e(TAG, e.getMessage());
                } catch (InterruptedException e) {
                    L.e(TAG, e.getMessage());
                }
            }
        };
    }

    public void write(String data) {
        data += "\n";
        try {
            byte[] bytes = data.getBytes("UTF-8");
            write(bytes, 0, bytes.length);
        } catch (UnsupportedEncodingException e) {
            L.e(TAG, e.getMessage());
        }
    }

    private void write(byte[] data, int offset, int count) {
        try {
            while (count > 0) {
                int written = mWriteQueue.write(data, offset, count);
                offset += written;
                count -= written;
                notifyNewOutput();
            }
        } catch (InterruptedException e) {
            L.e(TAG, e.getMessage());
        }
    }

    private void notifyNewOutput() {
        Handler writerHandler = mWriterHandler;
        if (writerHandler == null) {
            /* Writer thread isn't started -- will pick up data once it does */
            return;
        }
        writerHandler.sendEmptyMessage(NEW_OUTPUT);
    }

    private void notifyFinishOutput() {
        Handler writerHandler = mWriterHandler;
        if (writerHandler == null) {
            /* Writer thread isn't started -- will pick up data once it does */
            return;
        }
        writerHandler.sendEmptyMessage(FINISH);
    }

    private void readFromProcess() {
        int bytesAvailable = mReaderQueue.getBytesAvailable();
        int bytesToRead = Math.min(bytesAvailable, mReceiveBuffer.length);
        int bytesRead = 0;
        try {
            bytesRead = mReaderQueue.read(mReceiveBuffer, 0, bytesToRead);
        } catch (InterruptedException e) {
            L.e(TAG, e.getMessage());
            return;
        }
        if (null != mListener) {
            mListener.onRead(new String(mReceiveBuffer, 0, bytesRead));
        }
    }

    private void readErrFromProcess() {
        int bytesAvailableErr = mErrReaderQueue.getBytesAvailable();
        int bytesToReadErr = Math.min(bytesAvailableErr, mErrReceiveBuffer.length);
        int bytesReadErr = 0;
        try {
            bytesReadErr = mErrReaderQueue.read(mErrReceiveBuffer, 0, bytesToReadErr);
        } catch (InterruptedException e) {
            L.e(TAG, e.getMessage());
            return;
        }
        if (null != mListener) {
            mListener.onErrRead(new String(mErrReceiveBuffer, 0, bytesReadErr));
        }
    }

    public void close() {
        notifyFinishOutput();
        mIsRunning = false;
        mProcess.destroy();
    }

    public void setOnReadListener(ReadListener readListener) {
        mListener = readListener;
    }

    public interface ReadListener {
        public void onRead(String result);

        public void onErrRead(String result);
    }

}
