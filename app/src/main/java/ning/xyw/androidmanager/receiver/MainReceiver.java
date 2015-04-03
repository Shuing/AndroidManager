package ning.xyw.androidmanager.receiver;

/**
 * Created by ning on 15-2-3.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import ning.xyw.androidmanager.App;
import ning.xyw.androidmanager.bean.AppBean;
import ning.xyw.androidmanager.helper.DatabaseHelper;
import ning.xyw.androidmanager.helper.NotificationHelper;
import ning.xyw.androidmanager.util.L;
import ning.xyw.androidmanager.util.Util;

public class MainReceiver extends BroadcastReceiver {
    private static final String TAG = "NingReceiver";
    public static final String RECEIVER_START_APPOPS = "RECEIVER_START_APPOPS";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (null == context) {
            L.d("null == context");
            return;
        }
        if (null == intent) {
            L.d("null == intent");
            return;
        }
        if (TextUtils.isEmpty(intent.getAction())) {
            L.d("TextUtils.isEmpty(intent.getAction())");
            return;
        }
        String packageName;
        switch (intent.getAction()) {
            case Intent.ACTION_BOOT_COMPLETED:
                L.d("开机完成");
                break;
            case Intent.ACTION_PACKAGE_ADDED://接收安装广播
                packageName = intent.getDataString().split(":")[1];
                L.d("安装了:" + packageName);
//                insertAppInfo2DB(packageName);
                new NotificationHelper().showAutoCancelNotification(App.getContext(), "安装了:" + packageName);
                break;
            case Intent.ACTION_PACKAGE_REMOVED://接收卸载广播
//                packageName = intent.getDataString().split(":")[1];
//                L.d("卸载了:" + packageName);
                break;
            case RECEIVER_START_APPOPS://点击通知栏
                L.d("点击通知栏");
                Util.startAppOps(context);
                break;
        }
    }

    private void insertAppInfo2DB(String packagename) {
        if (TextUtils.isEmpty(packagename)) {
            return;
        }
        PackageManager pm = App.getContext().getPackageManager();
        try {
            ApplicationInfo ai = pm.getApplicationInfo(packagename, 0);
            String label = ai.loadLabel(pm).toString();
            DatabaseHelper.getInstance().insert(new AppBean(label, packagename));
        } catch (PackageManager.NameNotFoundException e) {
        }
    }

}
