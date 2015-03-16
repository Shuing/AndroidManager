package ning.xyw.androidmanager.util;

import android.util.Log;

/**
 * @author ning
 */
public class L {
    public static void d(Object obj) {
        Log.d("Ning", Util.getCurTime() + obj.toString());
    }

    public static void d(String tag, Object obj) {
        Log.d("Ning", tag + "   " + Util.getCurTime() + obj.toString());
    }

    public static void e(Object obj) {
        Log.e("Ning", Util.getCurTime() + obj.toString());
    }

    public static void e(String tag, Object obj) {
        Log.e("Ning", tag + "   " + Util.getCurTime() + obj.toString());
    }
}
