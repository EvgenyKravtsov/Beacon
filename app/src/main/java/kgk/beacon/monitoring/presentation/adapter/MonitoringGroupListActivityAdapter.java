package kgk.beacon.monitoring.presentation.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import kgk.beacon.R;
import kgk.beacon.monitoring.domain.interactor.SetActiveMonitoringEntityGroup;
import kgk.beacon.monitoring.domain.model.MonitoringEntityGroup;
import kgk.beacon.monitoring.presentation.activity.MonitoringListActivity;

public class MonitoringGroupListActivityAdapter extends
        RecyclerView.Adapter<MonitoringGroupListActivityAdapter.ViewHolder> {

    private List<MonitoringEntityGroup> groups;
    private MonitoringEntityGroup activeGroup;
    private Activity activity;

    ////

    public MonitoringGroupListActivityAdapter(Activity activity) {
        this.activity = activity;
    }

    ////

    public void setGroups(List<MonitoringEntityGroup> group) {
        this.groups = group;
    }

    public void setActiveGroup(MonitoringEntityGroup activeGroup) {
        this.activeGroup = activeGroup;
    }

    ////

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext())
                .inflate(R.layout.monitoring_activity_group_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final MonitoringEntityGroup group = groups.get(holder.getAdapterPosition());

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onListItemClick(group);
            }
        });

        holder.layout.setBackgroundDrawable(makeBackground(
                activeGroup != null &&
                        activeGroup.getName().equals(group.getName())
        ));

        holder.nameTextView.setText(group.getName());
        holder.countTextView.setText(
                String.format(
                        Locale.ROOT, "%d %s",
                        group.getMonitoringEntities().size(),
                        activity.getString(R.string.monitoring_choose_vehicle_group_screen_devices)
                )
        );
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    ////

    private void onListItemClick(MonitoringEntityGroup group) {
        // Making changes in model in UI thread is nessesary here, because
        // i need to guarantee, that active entity have been chaned before going
        // to monitoring entity list activity
        SetActiveMonitoringEntityGroup interactor =
                new SetActiveMonitoringEntityGroup(group);
        interactor.execute();
        Intent intent = new Intent(activity, MonitoringListActivity.class);
        activity.startActivity(intent);
    }

    private Drawable makeBackground(boolean active) {
        if (active) return activity.getResources()
                .getDrawable(R.drawable.monitoring_list_item_active_background_selector);
        else return activity.getResources()
                .getDrawable(R.drawable.monitoring_general_background_selector);
    }

    ////

    public static class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout layout;
        TextView nameTextView;
        TextView countTextView;

        ////

        public ViewHolder(View itemView) {
            super(itemView);

            layout = (LinearLayout)
                    itemView.findViewById(R.id.monitoring_activity_group_list_item_layout);
            nameTextView = (TextView)
                    itemView.findViewById(R.id.monitoring_activity_group_list_item_name_text_view);
            countTextView = (TextView)
                    itemView.findViewById(R.id.monitoring_activity_group_list_item_count_text_view);
        }
    }
}
