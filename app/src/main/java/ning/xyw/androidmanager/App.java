package ning.xyw.androidmanager;

import android.app.Application;
import android.content.Context;
import android.os.Debug;

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
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    public static Context getContext() {
        return mContext;
    }

}
