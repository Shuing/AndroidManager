package ning.xyw.androidmanager.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import ning.xyw.androidmanager.App;
import ning.xyw.androidmanager.util.L;
import ning.xyw.androidmanager.view.FloatViewLayout;

/**
 * 浮窗工具栏
 *
 * @author gogozhao
 */
public class FloatViewService extends Service {

    private final static String TAG = "RecordingService";

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
    private static FloatViewLayout mRecordingFloatView;
    public static boolean IS_RUNNING = false;
    public static boolean IS_SHOWING = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void init() {
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
          /*  */
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | LayoutParams.FLAG_NOT_TOUCH_MODAL;
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
        wmParams.gravity = Gravity.START | Gravity.TOP;
        /*  */
        Display display = mWindowManager.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        wmParams.x = metrics.widthPixels;
        wmParams.y = metrics.heightPixels / 2;
          /*  */
        mRecordingFloatView = new FloatViewLayout(App.getContext(), mWindowManager,
                wmParams);
        addView(mRecordingFloatView, wmParams);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        init();
        IS_RUNNING = true;
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        removeView(mRecordingFloatView);
        mWindowManager = null;
        IS_RUNNING = false;
        super.onDestroy();
    }

    /**
     * 启动悬浮窗
     */
    public static void startService() {
        L.d(TAG, "startFloatViewService: ");
        if (IS_RUNNING) {
            L.d(TAG, "startFloatViewService:  floatviewservice already start.");
            return;
        }
        stopService();
        Intent service = new Intent(App.getContext(),
                FloatViewService.class);
        App.getContext().startService(service);
        IS_RUNNING = true;
    }

    /**
     * 停止悬浮窗
     */
    public static void stopService() {
        L.d(TAG, "stopFloatViewService: ");
        Intent service = new Intent(App.getContext(),
                FloatViewService.class);
        App.getContext().stopService(service);
        IS_RUNNING = false;
    }

    private void addView(View view, LayoutParams params) {
        mWindowManager.addView(view, params);
        IS_SHOWING = true;
    }

    private void removeView(View view) {
        if (IS_SHOWING) {
            try {
                mWindowManager.removeView(view);
            } catch (Exception e) {
                L.e(TAG, e.getMessage());
            }
        }
        IS_SHOWING = false;
    }

}
