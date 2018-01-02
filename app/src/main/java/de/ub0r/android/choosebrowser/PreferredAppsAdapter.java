package de.ub0r.android.choosebrowser;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.ub0r.android.logg0r.Log;

public class PreferredAppsAdapter extends RecyclerView.Adapter<PreferredAppsAdapter.ViewHolder> {

    private static final String TAG = "PreferredAppsAdapter";

    interface OnItemClickListener {
        void onItemClick(final ComponentName component);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        final View containerView;
        final TextView keyView;
        final TextView activityNameView;
        final ImageView iconView;

        ViewHolder(final View itemView) {
            super(itemView);
            keyView = itemView.findViewById(R.id.key);
            activityNameView = itemView.findViewById(R.id.activity_name);
            iconView = itemView.findViewById(R.id.activity_icon);
            containerView = itemView;
        }

    }

    static class ContentHolder {
        private final String mKey;
        private final ComponentName mComponent;
        private CharSequence mLabel;
        private Drawable mIcon;

        ContentHolder(final String key, final ComponentName component) {
            mKey = key;
            mComponent = component;
        }

        CharSequence getLabel(final PackageManager pm) {
            if (mLabel == null) {
                try {
                    mLabel = pm.getActivityInfo(mComponent, 0).loadLabel(pm);
                } catch (PackageManager.NameNotFoundException e) {
                    Log.e(TAG, "error finding activity ", mComponent.flattenToShortString(), e);
                    mLabel = mComponent.flattenToShortString();
                }
            }
            return mLabel;
        }

        Drawable getIcon(final PackageManager pm) {
            if (mIcon == null) {
                try {
                    mIcon = pm.getActivityInfo(mComponent, 0).loadIcon(pm);
                } catch (PackageManager.NameNotFoundException e) {
                    Log.e(TAG, "error finding activity ", mComponent.flattenToShortString(), e);
                }
            }
            return mIcon;
        }

        String getKey() {
            return mKey;
        }

        ComponentName getComponent() {
            return mComponent;
        }
    }

    private final LayoutInflater mInflater;
    private final PackageManager mPackageManager;
    private final OnItemClickListener mListener;
    private PreferenceStore mStore;
    private final List<ContentHolder> mItems;

    PreferredAppsAdapter(final Context context, final OnItemClickListener listener, final PreferenceStore store) {
        mInflater = LayoutInflater.from(context);
        mPackageManager = context.getPackageManager();
        mListener = listener;
        mStore = store;
        mItems = new ArrayList<>();
        for (final String key : mStore.list()) {
            mItems.add(new ContentHolder(key, mStore.get(key)));
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        View itemView = mInflater.inflate(R.layout.item_preferred_apps,
                parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ContentHolder content = mItems.get(position);
        holder.keyView.setText(content.getKey());
        holder.activityNameView.setText(content.getLabel(mPackageManager));
        holder.iconView.setImageDrawable(content.getIcon(mPackageManager));
        if (mListener != null) {
            holder.containerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    mListener.onItemClick(content.getComponent());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}
