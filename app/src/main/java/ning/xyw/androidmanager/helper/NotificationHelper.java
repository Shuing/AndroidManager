package ning.xyw.androidmanager.helper;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import ning.xyw.androidmanager.App;
import ning.xyw.androidmanager.R;
import ning.xyw.androidmanager.activity.MainActivity;
import ning.xyw.androidmanager.activity.StartAppOpsActivity;
import ning.xyw.androidmanager.receiver.MainReceiver;

/**
 * Created by ning on 15-2-3.
 */
public class NotificationHelper {
    private String autoCancelTicker = "'";
    private static final int NOTIFICATION_ID_QUICKBAR_SUCCEED = 0;

    /**
     * 显示快捷栏(可清除)
     */
    public void showAutoCancelNotification(Context context, String ticker) {
        if (null == ticker) {
            return;
        }
        if (autoCancelTicker.equals(ticker)) {
            autoCancelTicker = ticker + " ";
        } else {
            autoCancelTicker = ticker;
        }
        //
        PendingIntent pendingIntent2Receiver = PendingIntent.getBroadcast(context, 0, new Intent(MainReceiver.RECEIVER_START_APPOPS), PendingIntent.FLAG_CANCEL_CURRENT);
        // 通知栏图标
        Intent intent2Activity = new Intent(context, StartAppOpsActivity.class);
//        intent.putExtra(MainActivity.KEY_INTENT_FROM,
//                MainActivity.VALUE_INTENT_FROM_NOTIFICATION);
        intent2Activity.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent2Activity = PendingIntent.getActivity(context, 0,
                intent2Activity, PendingIntent.FLAG_UPDATE_CURRENT);
//
        NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder(
                context)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(autoCancelTicker).setTicker(autoCancelTicker).setContentIntent(pendingIntent2Activity)
                .setAutoCancel(true);
        NotificationManager mNotificationManager =
                (NotificationManager) App.getContext()
                        .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID_QUICKBAR_SUCCEED,
                mNotificationBuilder.build());
    }
}
