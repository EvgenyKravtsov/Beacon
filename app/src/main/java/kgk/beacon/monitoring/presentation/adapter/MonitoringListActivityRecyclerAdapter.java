package kgk.beacon.monitoring.presentation.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import kgk.beacon.R;
import kgk.beacon.monitoring.DependencyInjection;
import kgk.beacon.monitoring.domain.model.MonitoringEntity;
import kgk.beacon.monitoring.domain.model.MonitoringEntityStatus;

public class MonitoringListActivityRecyclerAdapter extends
        RecyclerView.Adapter<MonitoringListActivityRecyclerAdapter.ViewHolder> {

    private List<MonitoringEntity> monitoringEntities;
    private Activity activity;

    ////

    public MonitoringListActivityRecyclerAdapter(Activity activity) {
        this.activity = activity;
    }

    ////

    public void setMonitoringEntities(List<MonitoringEntity> monitoringEntities) {
        this.monitoringEntities = new ArrayList<>(monitoringEntities);
    }

    public void sortByAlphabet() {
        Comparator<MonitoringEntity> alphabetical = new Comparator<MonitoringEntity>() {
            @Override
            public int compare(MonitoringEntity lhs, MonitoringEntity rhs) {
                int result =String.CASE_INSENSITIVE_ORDER.compare(
                        lhs.getMark(), rhs.getMark());
                return  result == 0 ?
                        lhs.getMark().compareTo(rhs.getMark()) :
                        result;
            }
        };
        Collections.sort(monitoringEntities, alphabetical);
    }

    public void sortByStatus() {
        List<MonitoringEntity> inMotion = new ArrayList<>();
        List<MonitoringEntity> stopped = new ArrayList<>();
        List<MonitoringEntity> offline = new ArrayList<>();

        for (MonitoringEntity monitoringEntity : monitoringEntities) {
            switch (monitoringEntity.getStatus()) {
                case IN_MOTION:
                    inMotion.add(monitoringEntity);
                    break;
                case STOPPED:
                    stopped.add(monitoringEntity);
                    break;
                case OFFLINE:
                    offline.add(monitoringEntity);
                    break;
            }
        }

        monitoringEntities.clear();
        monitoringEntities.addAll(inMotion);
        monitoringEntities.addAll(stopped);
        monitoringEntities.addAll(offline);
    }

    ////

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext())
                .inflate(R.layout.monitoring_activity_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final MonitoringEntity monitoringEntity = monitoringEntities.get(holder.getAdapterPosition());
        MonitoringEntity activeMonitoringEntity = DependencyInjection.provideMonitoringManager()
                .getActiveMonitoringEntity();

        holder.nameTextView.setText(
                String.format("%s %s %s",
                        monitoringEntity.getMark(),
                        monitoringEntity.getModel(),
                        monitoringEntity.getStateNumber()));
        holder.dateTextView.setText(
                new SimpleDateFormat("dd:MM:yyyy", Locale.ROOT)
                        .format(new Date(monitoringEntity.getLastUpdateTimestamp())));


        holder.statusTextView.setText(makeStatusString(monitoringEntity.getStatus()));
        holder.hideButton.setText(monitoringEntity.isDisplayEnabled() ? "D" : "N");
        holder.hideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monitoringEntity.setDisplayEnabled(!monitoringEntity.isDisplayEnabled());
                holder.hideButton.setText(monitoringEntity.isDisplayEnabled() ? "D" : "N");
            }
        });

        holder.informationLayout.setBackgroundColor(makeBackgroundColor(
                monitoringEntity.getId() == activeMonitoringEntity.getId()));
        holder.informationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Making cahnges in model in UI thread is nessesary here, because
                // i need to guarantee, that active entity have been chaned before going
                // to map activity
                DependencyInjection.provideMonitoringManager()
                        .setActiveMonitoringEntity(monitoringEntity);
                monitoringEntity.setDisplayEnabled(true);
                activity.finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return monitoringEntities.size();
    }

    ////

    public static class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout informationLayout;
        TextView nameTextView;
        TextView dateTextView;
        TextView statusTextView;
        Button hideButton;

        ////

        public ViewHolder(View itemView) {
            super(itemView);

            informationLayout = (LinearLayout)
                    itemView.findViewById(R.id.monitoring_activity_list_item_linear_information_layout);
            nameTextView = (TextView)
                    itemView.findViewById(R.id.monitoring_activity_list_item_name_text_view);
            dateTextView = (TextView)
                    itemView.findViewById(R.id.monitoring_activity_list_item_date_text_view);
            statusTextView = (TextView)
                    itemView.findViewById(R.id.monitoring_activity_list_item_status_text_view);
            hideButton = (Button)
                    itemView.findViewById(R.id.monitoring_activity_list_item_hide_button);
        }
    }

    ////

    private String makeStatusString(MonitoringEntityStatus status) {
        String statusString;
        switch (status) {
            case IN_MOTION:
                statusString = "M";
                break;
            case STOPPED:
                statusString = "S";
                break;
            case OFFLINE:
                statusString = "O";
                break;
            default:
                statusString = "O";
        }
        return statusString;
    }

    private int makeBackgroundColor(boolean active) {
        if (active) return activity.getResources().getColor(android.R.color.darker_gray);
        else return activity.getResources().getColor(android.R.color.white);
    }
}