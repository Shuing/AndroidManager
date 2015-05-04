package ning.xyw.androidmanager.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ning.xyw.androidmanager.R;
import ning.xyw.androidmanager.adapter.AppRecyclerViewAdapter;
import ning.xyw.androidmanager.bean.AppEntry;
import ning.xyw.androidmanager.fragment.ItemDialogFragment;
import ning.xyw.androidmanager.helper.AppListLoader;
import ning.xyw.androidmanager.listener.RecyclerItemClickListener;
import ning.xyw.androidmanager.util.Util;

/**
 * Created by ning on 15-4-29.
 */
public class RecyclerViewActivity extends BaseActivity implements
        LoaderManager.LoaderCallbacks<List<AppEntry>> {
    private static final int COUNT = 4;
    private PackageManager mPm;
    private List<AppEntry> mAppEntryList;
    private RecyclerView mRecyclerView;
    private AppRecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView lvLeftMenu;
    private String[] lvs = {"List Item 01", "List Item 02", "List Item 03", "List Item 04"};
    private ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_recyclerview);
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
        //
        mPm = getPackageManager();
        mAppEntryList = new ArrayList<>();
        mRecyclerView = (RecyclerView) findViewById(R.id.main_recyclerview);
        mAdapter = new AppRecyclerViewAdapter(mAppEntryList);
        mRecyclerView.setAdapter(mAdapter);
        mLayoutManager = new GridLayoutManager(this, COUNT);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                AppEntry item = mAppEntryList.get(position);
                itemClick(item);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                AppEntry item = mAppEntryList.get(position);
                itemLongClick(item);
            }
        }));
        //
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<List<AppEntry>> onCreateLoader(int id, Bundle args) {
        return new AppListLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<AppEntry>> loader, List<AppEntry> data) {
        mAppEntryList.clear();
        mAppEntryList.addAll(data);
        mAdapter.notifyDataSetChanged();
        mLayoutManager.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    @Override
    public void onLoaderReset(Loader<List<AppEntry>> loader) {
//        mAdapter.setData(null);
        mAppEntryList.clear();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        // android.os.Process.killProcess(android.os.Process.myPid());
        // System.exit(0);
        moveTaskToBack(true);
    }

    private void itemClick(AppEntry item) {
        try {
            startActivity(mPm.getLaunchIntentForPackage(item.getmPackageName()));
        } catch (Exception e) {
            Toast.makeText(this, String.format(getString(R.string.failed_launch), item.getLabel()), Toast.LENGTH_SHORT).show();
        }
    }

    private void itemLongClick(AppEntry item) {
        try {
            Util.getInstance().showInstalledAppDetails(this,
                    item.getmPackageName());
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
        }
    }

    private void showDialog() {
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        // Create and show the dialog.
        ItemDialogFragment newFragment = new ItemDialogFragment();
        newFragment.show(ft, "dialog");
    }

}
