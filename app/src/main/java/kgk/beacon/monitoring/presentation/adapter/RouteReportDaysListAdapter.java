package kgk.beacon.monitoring.presentation.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import kgk.beacon.R;
import kgk.beacon.monitoring.presentation.view.RouteReportView;

public class RouteReportDaysListAdapter
        extends RecyclerView.Adapter<RouteReportDaysListAdapter.ViewHolder> {

    private final RouteReportView view;
    private final List<Calendar> days;

    private long selectedDate;
    private RouteReportDaysListAdapter.ViewHolder selectedHolder;
    private boolean isAlreadyLaunched;

    ////

    public RouteReportDaysListAdapter(RouteReportView view, List<Calendar> days) {
        this.view = view;
        this.days = days;

        selectedDate = days.get(days.size() - 1).getTimeInMillis();
    }

    ////

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(
                                R.layout.monitroing_activity_route_report_days_list_item,
                                parent,
                                false
                        )
        );
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Calendar day = days.get(position);

        if (day.getTimeInMillis() == selectedDate) {
            selectedHolder = holder;
            holder.itemLayout.setBackgroundDrawable(
                    ((Activity) view).getResources().getDrawable(
                            R.drawable.monitoring_menu_map_button_activated_background_selector));
        } else {
            holder.itemLayout.setBackgroundDrawable(
                    ((Activity) view).getResources().getDrawable(
                            R.drawable.monitoring_general_background_selector));
        }

        holder.dayEnabledCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                view.toggleDayDisplay(day, isChecked);
            }
        });

        if (!isAlreadyLaunched && day.getTimeInMillis() == selectedDate) {
            isAlreadyLaunched = true;
            holder.dayEnabledCheckBox.setChecked(true);
        }

        holder.dateTextView.setText(
                new SimpleDateFormat("dd MMMM")
                        .format(day.getTime()));

        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!holder.dayEnabledCheckBox.isChecked()) holder.dayEnabledCheckBox.setChecked(true);
                onSelectButtonClick(day.getTimeInMillis(), holder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    ////

    public static class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout itemLayout;
        CheckBox dayEnabledCheckBox;
        TextView dateTextView;

        ////

        public ViewHolder(View itemView) {
            super(itemView);

            itemLayout = (LinearLayout) itemView
                    .findViewById(R.id.monitoring_activity_route_report_days_list_item_layout);

            dayEnabledCheckBox = (CheckBox) itemView
                    .findViewById(R.id.monitoring_activity_route_report_days_list_item_day_enabled_check_box);

            dateTextView = (TextView) itemView
                    .findViewById(R.id.monitoring_activity_route_report_days_list_item_date_text_view);
        }
    }

    ////

    private void onSelectButtonClick(
            long date,
            RouteReportDaysListAdapter.ViewHolder holder) {

        selectedHolder.itemLayout.setBackgroundDrawable(
                ((Activity) view).getResources().getDrawable(
                        R.drawable.monitoring_general_background_selector)
        );

        selectedHolder = holder;
        selectedHolder.itemLayout.setBackgroundDrawable(
                ((Activity) view).getResources().getDrawable(
                        R.drawable.monitoring_menu_map_button_activated_background_selector)
        );

        view.selectDay(date);
    }
}
