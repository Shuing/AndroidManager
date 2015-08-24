package ning.xyw.androidmanager.helper;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ning.xyw.androidmanager.bean.AppEntry;
import ning.xyw.androidmanager.receiver.PackageIntentReceiver;

/**
 * A custom Loader that loads all of the installed applications.
 */
public class AppListLoader extends AsyncTaskLoader<List<AppEntry>> {
    final InterestingConfigChanges mLastConfig = new InterestingConfigChanges();
    public final PackageManager mPm;

    List<AppEntry> mApps;
    PackageIntentReceiver mPackageObserver;
    /**
     * Perform alphabetical comparison of application entry objects.
     */
    public static final Comparator<AppEntry> ALPHA_COMPARATOR = new Comparator<AppEntry>() {
        private final Collator sCollator = Collator.getInstance();

        @Override
        public int compare(AppEntry object1, AppEntry object2) {
            return sCollator.compare("" + object1.getLastUpdateTime(),
                    "" + object2.getLastUpdateTime());
        }
    };

    public AppListLoader(Context context) {
        super(context);

        // Retrieve the package manager for later use; note we don't
        // use 'context' directly but instead the save global application
        // context returned by getContext().
        mPm = getContext().getPackageManager();
    }

    /**
     * @param pm
     * @param flags
     * @return
     * @see http://stackoverflow.com/questions/13235793/transactiontoolargeeception-when-trying-to-get-a-list-of-applications-installed/30062632#30062632
     */
    public static List<PackageInfo> getInstalledPackages(PackageManager pm, int flags) {
        //if it's Android 5.1, no need to do any special work
//        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP_MR1)
//            return pm.getInstalledPackages(flags);
        //else, protect against exception, and use a fallback if needed:
        try {
            return pm.getInstalledPackages(flags);
        } catch (Exception ignored) {
            //we don't care why it didn't succeed. We'll do it using an alternative way instead
        }
        // use fallback:
        Process process;
        List<PackageInfo> result = new ArrayList<>();
        BufferedReader bufferedReader = null;
        try {
            process = Runtime.getRuntime().exec("pm list packages");
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                final String packageName = line.substring(line.indexOf(':') + 1);
                final PackageInfo packageInfo = pm.getPackageInfo(packageName, flags);
                result.add(packageInfo);
            }
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null)
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return result;
    }

    /**
     * This is where the bulk of our work is done. This function is called in a
     * background thread and should generate a new set of data to be published
     * by the loader.
     */
    @Override
    public List<AppEntry> loadInBackground() {
        // Retrieve all known applications.
//        List<ApplicationInfo> apps = mPm.getInstalledApplications(
//                PackageManager.GET_UNINSTALLED_PACKAGES |
//                        PackageManager.GET_DISABLED_COMPONENTS);
        List<PackageInfo> apps2 = getInstalledPackages(mPm, 0);
//        if (apps == null) {
//            apps = new ArrayList<ApplicationInfo>();
//        }
        if (apps2 == null) {
            apps2 = new ArrayList<>();
        }

        final Context context = getContext();

        // Create corresponding array of entries and load their labels.
        List<AppEntry> entries = new ArrayList<>(apps2.size());
        for (int i = 0; i < apps2.size(); i++) {
            AppEntry entry = new AppEntry(this, apps2.get(i));
            entry.loadLabel(context);
            entries.add(entry);
        }

        // Sort the list.
        Collections.sort(entries, ALPHA_COMPARATOR);
        // Collections.reverse(entries);

        // Done!
        return entries;
    }

    /**
     * Called when there is new data to deliver to the client. The super class
     * will take care of delivering it; the implementation here just adds a
     * little more logic.
     */
    @Override
    public void deliverResult(List<AppEntry> apps) {
        if (isReset()) {
            // An async query came in while the loader is stopped. We
            // don't need the result.
            if (apps != null) {
                onReleaseResources(apps);
            }
        }
        List<AppEntry> oldApps = apps;
        mApps = apps;

        if (isStarted()) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(apps);
        }

        // At this point we can release the resources associated with
        // 'oldApps' if needed; now that the new result is delivered we
        // know that it is no longer in use.
        if (oldApps != null) {
            onReleaseResources(oldApps);
        }
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override
    protected void onStartLoading() {
        if (mApps != null) {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(mApps);
        }

        // Start watching for changes in the app data.
        if (mPackageObserver == null) {
            mPackageObserver = new PackageIntentReceiver(this);
        }

        // Has something interesting in the configuration changed since we
        // last built the app list?
        boolean configChange = mLastConfig.applyNewConfig(getContext().getResources());

        if (takeContentChanged() || mApps == null || configChange) {
            // If the data has changed since the last time it was loaded
            // or is not currently available, start a load.
            forceLoad();
        }
    }

    /**
     * Handles a request to stop the Loader.
     */
    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    /**
     * Handles a request to cancel a load.
     */
    @Override
    public void onCanceled(List<AppEntry> apps) {
        super.onCanceled(apps);

        // At this point we can release the resources associated with 'apps'
        // if needed.
        onReleaseResources(apps);
    }

    /**
     * Handles a request to completely reset the Loader.
     */
    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        // At this point we can release the resources associated with 'apps'
        // if needed.
        if (mApps != null) {
            onReleaseResources(mApps);
            mApps = null;
        }

        // Stop monitoring for changes.
        if (mPackageObserver != null) {
            getContext().unregisterReceiver(mPackageObserver);
            mPackageObserver = null;
        }
    }

    /**
     * Helper function to take care of releasing resources associated with an
     * actively loaded data set.
     */
    protected void onReleaseResources(List<AppEntry> apps) {
        // For a simple List<> there is nothing to do. For something
        // like a Cursor, we would close it here.
    }
}
