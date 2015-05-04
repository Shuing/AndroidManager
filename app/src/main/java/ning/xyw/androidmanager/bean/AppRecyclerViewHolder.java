package ning.xyw.androidmanager.bean;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ning.xyw.androidmanager.R;

/**
 * Created by ning on 15-4-29.
 */
public class AppRecyclerViewHolder extends RecyclerView.ViewHolder {
    private TextView nameTextView;
    private ImageView iconImageView;

    public AppRecyclerViewHolder(View itemView) {
        super(itemView);
        nameTextView = (TextView) itemView.findViewById(R.id.name);
        iconImageView = (ImageView) itemView.findViewById(R.id.icon);
    }

    public TextView getNameTextView() {
        return nameTextView;
    }

    public void setNameTextView(TextView nameTextView) {
        this.nameTextView = nameTextView;
    }

    public ImageView getIconImageView() {
        return iconImageView;
    }

    public void setIconImageView(ImageView iconImageView) {
        this.iconImageView = iconImageView;
    }

}
