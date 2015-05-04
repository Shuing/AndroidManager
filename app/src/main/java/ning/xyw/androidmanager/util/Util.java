/**
 *
 */

package ning.xyw.androidmanager.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.preference.PreferenceActivity;
import android.provider.Settings;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import ning.xyw.androidmanager.App;

/**
 * @author ning
 */
public class Util {
    private static final String TAG = "Utils";
    private static Util instance;

    public static Util getInstance() {
        if (null == instance) {
            instance = new Util();
        }
        return instance;
    }


    public int getAppCount(Context context) {
        return context.getPackageManager().getInstalledApplications(0).size();
    }

//    public boolean isEnabled(){
//        ApplicationInfo ai =
//                getActivity().getPackageManager().getApplicationInfo("your_package",0);
//
//        boolean appStatus = ai.enabled;
//    }

    public boolean isSystemed(PackageInfo packageInfo) {
        return packageInfo.applicationInfo.flags == ApplicationInfo.FLAG_SYSTEM;
    }

    /**
     * 判断某个应用程序是 不是三方的应用程序
     *
     * @param info
     * @return 如果是第三方应用程序则返回true，如果是系统程序则返回false
     */
    public boolean isSystemApp(ApplicationInfo info) {
        if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
            return false;
        } else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
            return false;
        }
        return true;
    }

    public void openApp(Context context, String packageName) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(
                packageName);
        if (null != intent) {
            context.startActivity(intent);
        }
    }

    public static String getCurTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        return formatter.format(curDate);
    }

    private static final String SCHEME = "package";
    /**
     * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.1及之前版本)
     */
    private static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
    /**
     * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.2)
     */
    private static final String APP_PKG_NAME_22 = "pkg";
    /**
     * InstalledAppDetails所在包名
     */
    private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
    /**
     * InstalledAppDetails类名
     */
    private static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";

    /**
     * 调用系统InstalledAppDetails界面显示已安装应用程序的详细信息。 对于Android 2.3（Api Level
     * 9）以上，使用SDK提供的接口； 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）。
     *
     * @param context
     * @param packageName 应用程序的包名
     */
    public void showInstalledAppDetails(Context context, String packageName) {
        Intent intent = new Intent();
        final int apiLevel = Build.VERSION.SDK_INT;
        if (apiLevel >= 9) { // 2.3（ApiLevel 9）以上，使用SDK提供的接口
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts(SCHEME, packageName, null);
            intent.setData(uri);
        } else { // 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）
            // 2.2和2.1中，InstalledAppDetails使用的APP_PKG_NAME不同。
            final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22
                    : APP_PKG_NAME_21);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName(APP_DETAILS_PACKAGE_NAME,
                    APP_DETAILS_CLASS_NAME);
            intent.putExtra(appPkgName, packageName);
        }
        context.startActivity(intent);
    }

    public static boolean startAppOps(Context context) {
        L.d("startAppOps()");
        try {
            Intent localIntent1 = new Intent();
            localIntent1.setAction("android.settings.APP_OPS_SETTINGS");
            context.startActivity(localIntent1);
            return true;
        } catch (Exception localException1) {
            L.d("localIntent1 Fiailed");
            try {
                Intent localIntent2 = new Intent();
                localIntent2.setClassName("com.android.settings", "com.android.settings.Settings$AppOpsSummaryActivity");
                context.startActivity(localIntent2);
                return true;
            } catch (Exception localException2) {
                L.d("localInten2 Fiailed");
                while (true) {
                    try {
                        Intent localIntent3 = new Intent();
                        localIntent3.setClassName("com.android.settings", "com.android.settings.Settings");
                        localIntent3.setAction("android.intent.action.MAIN");
                        localIntent3.addCategory("android.intent.category.DEFAULT");
                        localIntent3.setFlags(276856832);
                        localIntent3.putExtra(":android:show_fragment", "com.android.settings.applications.AppOpsSummary");
                        context.startActivity(localIntent3);
                        return true;
                    } catch (Exception localException3) {
                        L.d("localIntent3 Fiailed");
                        return false;
                    }
                }
            }
        } finally {
//            finish();
        }
    }

    /**
     * 权限不足
     *
     * @param context
     * @return
     */
    public static boolean startSubsetting(Context context) {
        L.d("startSubsetting");
        try {
            Intent intent = Intent.makeRestartActivityTask(
                    new ComponentName("com.android.settings", "com.android.settings.SubSettings"));
            intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, "com.android.settings.deviceinfo.Memory");
            intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT_TITLE, "title");
            intent.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true);
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            L.d("startSubsetting failed " + e);
            return false;
        }
    }

    public static void t(String msg) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Toast.makeText(App.getContext(), msg, Toast.LENGTH_SHORT).show();
        } else {
            L.d("Toast msg : " + msg);
        }
    }
}
