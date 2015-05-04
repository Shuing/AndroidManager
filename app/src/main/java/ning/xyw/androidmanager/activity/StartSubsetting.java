package ning.xyw.androidmanager.activity;

import android.os.Bundle;
import static ning.xyw.androidmanager.util.Util.*;

/**
 * Created by ning on 15-4-14.
 */
public class StartSubsetting extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean result = startSubsetting(this);
        finish();
    }
}
