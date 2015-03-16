package ning.xyw.androidmanager.receiver;

/**
 * Created by ning on 15-2-3.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import ning.xyw.androidmanager.App;
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
        switch (intent.getAction()) {
            case Intent.ACTION_BOOT_COMPLETED:
                L.d("开机完成");
                break;
            case Intent.ACTION_PACKAGE_ADDED://接收安装广播
                L.d("安装了:" + intent.getDataString());
                new NotificationHelper().showAutoCancelNotification(App.getContext(), "安装了:" + intent.getDataString());
                break;
            case Intent.ACTION_PACKAGE_REMOVED://接收卸载广播
                String packageName = intent.getDataString();
                L.d("卸载了:" + packageName);
//                new NotificationHelper().showAutoCancelNotification(App.getContext(), "卸载了:" + intent.getDataString());
                break;
            case RECEIVER_START_APPOPS://点击通知栏
                L.d("点击通知栏");
                Util.startAppOps(context);
                break;
        }
    }

}
