package ning.xyw.androidmanager.helper;

import android.os.AsyncTask;

import java.util.Objects;
import java.util.concurrent.Executors;

/**
 * Created by ning on 15-4-1.
 */
public class SyncTaskHelper extends AsyncTask {

    @Override
    protected Object doInBackground(Object[] params) {
        return null;
    }

    public AsyncTask execute(Objects... params) {
        return executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, params);
    }

}
