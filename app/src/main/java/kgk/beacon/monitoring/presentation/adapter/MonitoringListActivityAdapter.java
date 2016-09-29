package kgk.beacon.monitoring.presentation.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import kgk.beacon.R;
import kgk.beacon.monitoring.domain.interactor.InteractorThreadPool;
import kgk.beacon.monitoring.domain.interactor.SetActiveMonitoringEntity;
import kgk.beacon.monitoring.domain.interactor.SetDisplayEnabled;
import kgk.beacon.monitoring.domain.model.MonitoringEntity;
import kgk.beacon.monitoring.presentation.activity.MapActivity;

public class MonitoringListActivityAdapter extends
        RecyclerView.Adapter<MonitoringListActivityAdapter.ViewHolder> {

    // Drawables
    private Drawable monitoringEntityDisplayedIcon;
    private Drawable monitoringEntityNotDisplayedIcon;

    private List<MonitoringEntity> monitoringEntities;
    private MonitoringEntity activeMonitoringEntity;
    private Activity activity;

    ////

    public MonitoringListActivityAdapter(Activity activity) {
        this.activity = activity;

        monitoringEntityDisplayedIcon = activity.getResources().getDrawable(
                R.drawable.monitoring_entity_displayed_icon
        );
        monitoringEntityNotDisplayedIcon = activity.getResources().getDrawable(
                R.drawable.monitoring_entity_not_displayed_icon
        );
    }

    ////

    public void setMonitoringEntities(List<MonitoringEntity> monitoringEntities) {
        this.monitoringEntities = new ArrayList<>(monitoringEntities);
    }

    public void setActiveMonitoringEntity(MonitoringEntity activeMonitoringEntity) {
        this.activeMonitoringEntity = activeMonitoringEntity;
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
        final MonitoringEntity monitoringEntity = monitoringEntities.get(position);

        switch (monitoringEntity.getStatus()) {
            case IN_MOTION:
                holder.directionImageView.setImageDrawable(
                    activity.getResources().getDrawable(R.drawable.monitoring_entity_direction_icon_moving)
                );
                holder.speedTextView.setText(String.format(
                        Locale.ROOT,
                        "%.1f %s",
                        monitoringEntity.getSpeed(),
                        activity.getString(R.string.list_item_speed_sign)));
                break;
            case STOPPED:
                holder.speedTextView.setVisibility(View.GONE);
                break;
            case OFFLINE:
                holder.directionImageView.setImageDrawable(
                        activity.getResources().getDrawable(R.drawable.monitoring_entity_direction_icon_offline)
                );
                holder.speedTextView.setVisibility(View.GONE);
                break;
        }

        holder.nameTextView.setText(
                String.format("%s %s",
                        monitoringEntity.getMark(),
                        monitoringEntity.getStateNumber()));
        holder.dateTextView.setText(
                generateSubStringForListItem(
                        monitoringEntity.getLastUpdateTimestamp()
                ));

        prepareDirectionLayout(holder, monitoringEntity.getDirection());

        holder.hideButton.setImageDrawable(
                monitoringEntity.isDisplayEnabled() ?
                        monitoringEntityDisplayedIcon :
                        monitoringEntityNotDisplayedIcon);

        holder.hideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monitoringEntity.setDisplayEnabled(!monitoringEntity.isDisplayEnabled());

                SetDisplayEnabled interactor = new SetDisplayEnabled(
                        monitoringEntity,
                        monitoringEntity.isDisplayEnabled());
                InteractorThreadPool.getInstance().execute(interactor);

                holder.hideButton.setImageDrawable(
                        monitoringEntity.isDisplayEnabled() ?
                                monitoringEntityDisplayedIcon :
                                monitoringEntityNotDisplayedIcon);

                holder.informationLayout.setBackgroundResource(
                        monitoringEntity.isDisplayEnabled() ?
                                R.drawable.monitoring_general_background_selector :
                                R.drawable.monitoring_general_background_selector);

                holder.informationLayout.setPadding(8, 8, 8, 8);
            }
        });

        holder.informationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickInformationLayout(monitoringEntity);
            }
        });

        holder.informationLayout.setBackgroundResource(
                monitoringEntity.isDisplayEnabled() ?
                R.drawable.monitoring_general_background_selector :
                R.drawable.monitoring_general_background_selector);
        holder.informationLayout.setPadding(8, 8, 8, 8);
    }

    @Override
    public int getItemCount() {
        return monitoringEntities.size();
    }

    ////

    private int makeBackground(boolean active) {
        if (active) return R.drawable.monitoring_menu_map_button_activated_background_selector;
        else return R.drawable.monitoring_general_background_selector;
    }

    private void onClickInformationLayout(MonitoringEntity monitoringEntity) {
        // Making changes in model in UI thread is nessesary here, because
        // i need to guarantee, that active entity have been chaned before going
        // to map activity
        SetActiveMonitoringEntity interactor = new SetActiveMonitoringEntity(monitoringEntity);
        interactor.execute();
        monitoringEntity.setDisplayEnabled(true);
        Intent intent = new Intent(activity, MapActivity.class);
        activity.startActivity(intent);
    }

    @SuppressLint("SimpleDateFormat")
    private String generateSubStringForListItem(long lastUpdateTimestamp) {
        Date date = new Date(lastUpdateTimestamp);
        return String.format("%s %s",
                new SimpleDateFormat("HH:mm:ss").format(date),
                new SimpleDateFormat("dd.MM").format(date));
    }

    private void prepareDirectionLayout(ViewHolder holder, int direction) {
        String[] directionLabes = {
                activity.getString(R.string.monitoring_choose_vehicle_screen_north_east),
                activity.getString(R.string.monitoring_choose_vehicle_screen_east),
                activity.getString(R.string.monitoring_choose_vehicle_screen_south_east),
                activity.getString(R.string.monitoring_choose_vehicle_screen_south),
                activity.getString(R.string.monitoring_choose_vehicle_screen_south_west),
                activity.getString(R.string.monitoring_choose_vehicle_screen_west),
                activity.getString(R.string.monitoring_choose_vehicle_screen_north_west),
                activity.getString(R.string.monitoring_choose_vehicle_screen_north)};

        int degrees = 22;
        if (direction >= 0 && direction < 22) direction += 360;

        for (int i = 0; degrees <= 337; i++) {
            int degreesLimit = degrees + 45;

            if (direction >= degrees && direction < degreesLimit) {
                holder.directionTextView.setText(directionLabes[i]);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                    holder.directionImageView.setRotation(degreesLimit - (45 / 2));
                else holder.directionImageView.setVisibility(View.GONE);
            }

            degrees += 45;
        }
    }

    ////

    public static class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout informationLayout;
        TextView nameTextView;
        TextView dateTextView;
        ImageView directionImageView;
        TextView directionTextView;
        ImageButton hideButton;
        TextView speedTextView;

        ////

        public ViewHolder(View itemView) {
            super(itemView);

            informationLayout = (RelativeLayout) itemView
                    .findViewById(R.id.monitoring_activity_list_item_linear_information_layout);
            nameTextView = (TextView) itemView
                    .findViewById(R.id.monitoring_activity_list_item_name_text_view);
            dateTextView = (TextView) itemView
                    .findViewById(R.id.monitoring_activity_list_item_date_text_view);
            directionImageView = (ImageView) itemView
                    .findViewById(R.id.monitoring_activity_list_item_direction_image_view);
            directionTextView = (TextView) itemView
                    .findViewById(R.id.monitoring_activity_list_item_direction_text_view);
            hideButton = (ImageButton) itemView
                    .findViewById(R.id.monitoring_activity_list_item_hide_button);
            speedTextView = (TextView) itemView
                    .findViewById(R.id.monitoring_activity_list_item_speed);
        }
    }
}



























