package ning.xyw.androidmanager;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;

import ning.xyw.androidmanager.helper.DatabaseHelper;
import ning.xyw.androidmanager.service.FloatViewService;
import ning.xyw.androidmanager.util.L;

/**
 * Created by ning on 15-2-3.
 */
public class App extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        Cursor cursor = new DatabaseHelper(this).query("com.terry");
        int count = cursor.getCount();
        L.d("AAAAAAA    " + count);
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

}
