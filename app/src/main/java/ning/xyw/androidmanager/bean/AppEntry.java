/**
 *
 */

package ning.xyw.androidmanager.bean;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;

import java.io.File;

import ning.xyw.androidmanager.helper.AppListLoader;
import ning.xyw.androidmanager.util.Util;

/**
 * This class holds the per-item data in our Loader.
 */
public class AppEntry {

    private final AppListLoader mLoader;
    private final PackageInfo mPackageInfo;
    private final ApplicationInfo mInfo;
    private final File mApkFile;
    private String mLabel;
    private Drawable mIcon;
    private boolean mMounted;
    private long firstInstallTime;
    private long lastUpdateTime;
    private String mPackageName;
    private boolean systemed;
    private boolean enabled;

    public AppEntry(AppListLoader loader, PackageInfo packageInfo) {
        mLoader = loader;
        mPackageInfo = packageInfo;
        mInfo = packageInfo.applicationInfo;
        mApkFile = new File(packageInfo.applicationInfo.sourceDir);
        firstInstallTime = packageInfo.firstInstallTime;
        lastUpdateTime = packageInfo.lastUpdateTime;
        mPackageName = packageInfo.packageName;
        systemed = Util.getInstance().isSystemApp(mInfo);
        enabled = mInfo.enabled;
    }

    public ApplicationInfo getApplicationInfo() {
        return mInfo;
    }

    /**
     * @return the firstInstallTime
     */
    public long getFirstInstallTime() {
        return firstInstallTime;
    }

    /**
     * @return the lastUpdateTime
     */
    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    /**
     * @return the mPackageName
     */
    public String getmPackageName() {
        return mPackageName;
    }

    public String getLabel() {
        return mLabel;
    }

    /**
     * @return the systemed
     */
    public boolean isSystemed() {
        return systemed;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Drawable getIcon() {
        if (mIcon == null) {
            if (mApkFile.exists()) {
                mIcon = mInfo.loadIcon(mLoader.mPm);
                return mIcon;
            } else {
                mMounted = false;
            }
        } else if (!mMounted) {
            // If the app wasn't mounted but is now mounted, reload
            // its icon.
            if (mApkFile.exists()) {
                mMounted = true;
                mIcon = mInfo.loadIcon(mLoader.mPm);
                return mIcon;
            }
        } else {
            return mIcon;
        }

        return mLoader.getContext().getResources().getDrawable(
                android.R.drawable.sym_def_app_icon);
    }

    @Override
    public String toString() {
        return mLabel;
    }

    public void loadLabel(Context context) {
        if (mLabel == null || !mMounted) {
            if (!mApkFile.exists()) {
                mMounted = false;
                mLabel = mInfo.packageName;
            } else {
                mMounted = true;
                CharSequence label = mInfo.loadLabel(context.getPackageManager());
                mLabel = label != null ? label.toString() : mInfo.packageName;
            }
        }
    }

    public void setmLabel(String mLabel) {
        this.mLabel = mLabel;
    }
}
