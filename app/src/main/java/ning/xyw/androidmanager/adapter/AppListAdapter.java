
package ning.xyw.androidmanager.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ning.xyw.androidmanager.R;
import ning.xyw.androidmanager.bean.AppEntry;

public class AppListAdapter extends ArrayAdapter<AppEntry> {
    private final LayoutInflater mInflater;
    private ExecutorService mThreadPools;

    public AppListAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_2);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mThreadPools = Executors.newFixedThreadPool(8);
    }

    public void setData(List<AppEntry> data) {
        clear();
        if (data != null) {
            addAll(data);
        }
    }

    /**
     * Populate new items in the list.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item, parent, false);
            viewHolder.itemIcon = (ImageView) convertView.findViewById(R.id.item_icon);
            viewHolder.itemName = (TextView) convertView.findViewById(R.id.item_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        AppEntry item = getItem(position);
        viewHolder.itemName.setText(item.getLabel());
        new LoadIconTask(item, viewHolder.itemIcon).executeOnExecutor(mThreadPools);
        if (item.isSystemed()) {
            viewHolder.itemName.setTextColor(Color.RED);
        } else {
            viewHolder.itemName.setTextColor(Color.WHITE);
        }
        return convertView;
    }

    class ViewHolder {
        private ImageView itemIcon;
        private TextView itemName;
    }

    class LoadIconTask extends AsyncTask<Void, Void, Void> {
        private AppEntry appEntry;
        private ImageView icon;
        private Drawable iconDrawable;

        LoadIconTask(AppEntry appEntry, ImageView icon) {
            this.appEntry = appEntry;
            this.icon = icon;
        }

        @Override
        protected void onPreExecute() {
            icon.setImageResource(R.drawable.phiz10);
            icon.setTag(appEntry.getLabel());
        }

        @Override
        protected Void doInBackground(Void... params) {
            iconDrawable = appEntry.getIcon();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (appEntry.getLabel().equals(icon.getTag())) {
                icon.setImageDrawable(iconDrawable);
            }
        }
    }

}
