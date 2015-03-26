package ning.xyw.androidmanager;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import ning.xyw.androidmanager.service.FloatViewService;

/**
 * Created by ning on 15-2-3.
 */
public class App extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
//        FloatViewService.startService();
    }

    @Override
    public void onTerminate() {
        FloatViewService.stopService();
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        FloatViewService.stopService();
        super.onLowMemory();
    }

    public static Context getContext() {
        return mContext;
    }

    public static void startAppOpsActivity(Context context) {
        try {
            Intent localIntent1 = new Intent();
            localIntent1.setAction("android.settings.APP_OPS_SETTINGS");
            context.startActivity(localIntent1);
            return;
        } catch (Exception localException1) {
            localException1.printStackTrace();
            try {
                Intent localIntent2 = new Intent();
                localIntent2.setClassName("com.android.settings", "com.android.settings.Settings$AppOpsSummaryActivity");
                context.startActivity(localIntent2);
                return;
            } catch (Exception localException2) {
                while (true) {
                    localException2.printStackTrace();
                    try {
                        Intent localIntent3 = new Intent();
                        localIntent3.setClassName("com.android.settings", "com.android.settings.Settings");
                        localIntent3.setAction("android.intent.action.MAIN");
                        localIntent3.addCategory("android.intent.category.DEFAULT");
                        localIntent3.setFlags(276856832);
                        localIntent3.putExtra(":android:show_fragment", "com.android.settings.applications.AppOpsSummary");
                        context.startActivity(localIntent3);
                    } catch (Exception localException3) {
                    }
                }
            }
        } finally {
//            finish();
        }
    }

}
