/**
 *
 */

package ning.xyw.androidmanager.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * 命令行接口
 * 
 * @author gogozhao
 */
public class CommanderHelper {

    private final static String TAG = "CommanderHelper";

    /**
     * 基本权限在shell中执行本地命令
     * 
     * @param cmd 本地命令
     * @param directory 命令执行的当前目录
     * @return
     */
    public static boolean execShell(String cmd, String directory) {
        ProcessBuilder pb = new ProcessBuilder("/system/bin/sh");
        // java.lang.ProcessBuilder: Creates operating system processes.
        pb.directory(new File(directory));// 设置shell的当前目录。
        Process proc = null;
        BufferedReader in = null;
        BufferedReader err = null;
        PrintWriter out = null;
        try {
            proc = pb.start();
            // 获取输入流，可以通过它获取SHELL的输出。
            in = new BufferedReader(
                    new InputStreamReader(proc.getInputStream()));
            err = new BufferedReader(new InputStreamReader(
                    proc.getErrorStream()));
            // 获取输出流，可以通过它向SHELL发送命令。
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                    proc.getOutputStream())), true);
            out.println(cmd);
            out.println("exit");
            String line = "";
            while ((line = in.readLine()) != null) {
                L.d(TAG, "execShell: " + line);
            }
            while ((line = err.readLine()) != null) {
                L.e(TAG, "execShell: " + line);
            }
            return true;
        } catch (Exception e) {
            L.e(TAG, "execShell: " + e.toString());
            return false;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    L.e(TAG, "execShell: " + e.toString());
                }
            }
            if (err != null) {
                try {
                    err.close();
                } catch (IOException e) {
                    L.e(TAG, "execShell: " + e.toString());
                }
            }
            if (out != null) {
                out.close();
            }
            if (proc != null) {
                proc.destroy();
            }
        }
    }

    /**
     * root权限执行本地命令
     * 
     * @param cmd
     * @return
     */
    public static boolean execSU(String cmd) {
        Process process = null;
        DataOutputStream dos = null;
        BufferedReader br = null;
        try {
            process = Runtime.getRuntime().exec("su");
            dos = new DataOutputStream(process.getOutputStream());
            dos.writeBytes(cmd + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            process.waitFor();
            int exitValue = process.exitValue();
            L.d(TAG, "execSU: " + cmd + ": " + exitValue);
            // 返回值0表示命令执行成功
            if (0 == exitValue) {
                br = new BufferedReader(new InputStreamReader(
                        process.getInputStream()));
                String string = "";
                StringBuffer result = new StringBuffer();
                while ((string = br.readLine()) != null) {
                    // L.d(TAG, "execSU: " + string);
                    result.append(string + ";");
                }
                L.d("sb = " + result);
                return true;
            } else {
                br = new BufferedReader(new InputStreamReader(
                        process.getErrorStream()));
                String string = "";
                while ((string = br.readLine()) != null) {
                    L.e(TAG, "execSU: " + string);
                }
                return false;
            }
        } catch (IOException e) {
            L.e(TAG, "execSU: " + e.toString());
            return false;
        } catch (InterruptedException e) {
            L.e(TAG, "execSU: " + e.toString());
            return false;
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    L.e(TAG, "execSU: " + e.toString());
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    L.e(TAG, "execSU: " + e.toString());
                }
            }
        }
    }

    public static boolean freezeApp(String packageName, boolean enable) {
        return execSU("pm " + (enable ? " enable " : " disable ") + packageName);
    }
}
