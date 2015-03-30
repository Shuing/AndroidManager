package ning.xyw.androidmanager.adapter;

import android.content.Context;
import android.graphics.Color;
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
    private ExecutorService mPool;

    public AppListAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_2);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mPool = Executors.newFixedThreadPool(10);
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
            convertView = mInflater.inflate(R.layout.item, null);
            viewHolder.itemIcon = (ImageView) convertView.findViewById(R.id.item_icon);
            viewHolder.itemLockIcon = (ImageView) convertView.findViewById(R.id.item_icon_enabled);
            viewHolder.itemName = (TextView) convertView.findViewById(R.id.item_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        AppEntry item = getItem(position);
        viewHolder.itemIcon.setImageDrawable(item.getIcon());
        viewHolder.itemName.setText(item.getLabel());
        if (item.isSystemed()) {
            viewHolder.itemName.setTextColor(Color.RED);
        } else {
            viewHolder.itemName.setTextColor(Color.WHITE);
        }
        if (item.isEnabled()) {
            viewHolder.itemLockIcon.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.itemLockIcon.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    class ViewHolder {
        private ImageView itemIcon;
        private ImageView itemLockIcon;
        private TextView itemName;
    }

}
