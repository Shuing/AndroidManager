package ning.xyw.androidmanager.activity;

import android.os.Bundle;

import static ning.xyw.androidmanager.util.Util.startAppOps;

/**
 * Created by ning on 15-2-5.
 */
public class StartAppOpsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean result = startAppOps(this);
        finish();
    }

}
