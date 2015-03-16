package ning.xyw.androidmanager.activity;

import android.app.Activity;
import android.os.Bundle;

import ning.xyw.androidmanager.util.Util;

/**
 * Created by ning on 15-2-5.
 */
public class StartAppOpsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean result = Util.startAppOps(this);
        finish();
    }

}
