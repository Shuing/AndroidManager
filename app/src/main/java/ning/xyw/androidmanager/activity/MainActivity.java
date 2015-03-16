/**
 *
 */

package ning.xyw.androidmanager.activity;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.Toast;

import java.util.List;

import ning.xyw.androidmanager.R;
import ning.xyw.androidmanager.adapter.AppListAdapter;
import ning.xyw.androidmanager.bean.AppEntry;
import ning.xyw.androidmanager.helper.AppListLoader;
import ning.xyw.androidmanager.util.Util;

/**
 * @author ning
 */
public class MainActivity extends Activity implements
        LoaderManager.LoaderCallbacks<List<AppEntry>>, OnItemClickListener, OnItemLongClickListener {
    private PackageManager mPm;
    private GridView mGridView;
    private AppListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mPm = getPackageManager();
        mGridView = (GridView) findViewById(R.id.main_grid);
        mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mAdapter = new AppListAdapter(this);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(this);
        mGridView.setOnItemLongClickListener(this);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<List<AppEntry>> onCreateLoader(int id, Bundle args) {
        return new AppListLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<AppEntry>> loader, List<AppEntry> data) {
        mAdapter.setData(data);
    }

    @Override
    public void onLoaderReset(Loader<List<AppEntry>> loader) {
        mAdapter.setData(null);
    }

    @Override
    public void onBackPressed() {
        // android.os.Process.killProcess(android.os.Process.myPid());
        // System.exit(0);
        moveTaskToBack(true);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AppEntry item = (AppEntry) parent.getAdapter().getItem(position);
        try {
            startActivity(mPm.getLaunchIntentForPackage(item.getmPackageName()));
        } catch (Exception e) {
            Toast.makeText(this, "启动 " + item.getLabel() + " 失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            AppEntry item = (AppEntry) parent.getAdapter().getItem(position);
            Util.getInstance().showInstalledAppDetails(this,
                    item.getmPackageName());
            return true;
        } catch (Exception e) {
        }
        return false;
    }

}
