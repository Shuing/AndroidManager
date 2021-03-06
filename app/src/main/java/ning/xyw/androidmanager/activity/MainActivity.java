/**
 *
 */

package ning.xyw.androidmanager.activity;

import android.app.LoaderManager;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import ning.xyw.androidmanager.R;
import ning.xyw.androidmanager.adapter.AppListAdapter;
import ning.xyw.androidmanager.bean.AppEntry;
import ning.xyw.androidmanager.helper.AppListLoader;
import ning.xyw.androidmanager.helper.DatabaseHelper;
import ning.xyw.androidmanager.util.L;
import ning.xyw.androidmanager.util.Util;

/**
 * @author ning
 */
public class MainActivity extends ActionBarActivity implements
        LoaderManager.LoaderCallbacks<List<AppEntry>>, OnItemClickListener, OnItemLongClickListener {
    private PackageManager mPm;
    private GridView mGridView;
    private AppListAdapter mAdapter;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView lvLeftMenu;
    private String[] lvs = {"List Item 01", "List Item 02", "List Item 03", "List Item 04"};
    private ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawerlayout);
        lvLeftMenu = (ListView) mDrawerLayout.findViewById(R.id.include_drawerlayout_lv_left_menu);
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle("mToolbar");//设置Toolbar标题
        mDrawerLayout.setVisibility(View.GONE);
        mToolbar.setVisibility(View.GONE);
        mToolbar.setTitleTextColor(Color.parseColor("#ffffff")); //设置标题颜色
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //创建返回键，并实现打开关/闭监听
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open, R.string.close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        //设置菜单列表
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, lvs);
        lvLeftMenu.setAdapter(arrayAdapter);
        mPm = getPackageManager();
        mGridView = (GridView) findViewById(R.id.main_grid);
        mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mAdapter = new AppListAdapter(this);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(this);
        mGridView.setOnItemLongClickListener(this);
        getLoaderManager().initLoader(0, null, this);
        test();
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
            Toast.makeText(this, String.format(getString(R.string.failed_launch), item.getLabel()), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void test() {
        Cursor cursor = new DatabaseHelper(this).query("com.hjwang.hospitalandroid");
        L.d("AAAAA   " + cursor.getCount());
    }

}
