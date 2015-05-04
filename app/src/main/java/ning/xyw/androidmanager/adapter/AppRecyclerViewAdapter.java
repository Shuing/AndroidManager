package ning.xyw.androidmanager.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ning.xyw.androidmanager.R;
import ning.xyw.androidmanager.bean.AppEntry;
import ning.xyw.androidmanager.bean.AppRecyclerViewHolder;

/**
 * Created by ning on 15-4-29.
 */
public class AppRecyclerViewAdapter extends RecyclerView.Adapter<AppRecyclerViewHolder> {

    private List<AppEntry> mAppEntryList;
    private ItemListener mItemListener;

    public AppRecyclerViewAdapter(List<AppEntry> appEntryList) {
        mAppEntryList = appEntryList;
    }

    @Override
    public AppRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        // create a new view
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_recyclerview, viewGroup, false);
        AppRecyclerViewHolder appRecyclerViewHolder = new AppRecyclerViewHolder(view);
        return appRecyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(AppRecyclerViewHolder appRecyclerViewHolder, int i) {
        AppEntry appEntry = mAppEntryList.get(i);
        appRecyclerViewHolder.getIconImageView().setImageDrawable(appEntry.getIcon());
        appRecyclerViewHolder.getNameTextView().setText(appEntry.getLabel());
    }

    @Override
    public int getItemCount() {
        return null == mAppEntryList ? 0 : mAppEntryList.size();
    }

    public interface ItemListener {
        void onItemClickListener(AppRecyclerViewHolder viewHolder);

        boolean onItemLongClickListener(AppRecyclerViewHolder viewHolder);
    }

}
