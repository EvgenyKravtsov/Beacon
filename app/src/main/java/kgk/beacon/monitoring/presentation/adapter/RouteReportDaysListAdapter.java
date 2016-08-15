package kgk.beacon.monitoring.presentation.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

            holder.itemLayout.setBackgroundColor(
                    ((Activity) view).getResources().getColor(android.R.color.darker_gray)
            );
        } else {
            holder.itemLayout.setBackgroundColor(
                    ((Activity) view).getResources().getColor(android.R.color.white)
            );
        }

        holder.dayTextView.setText(
                new SimpleDateFormat("dd MMMM")
                .format(day.getTime()));

        holder.dayEnabledCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                view.toggleDayDisplay(day, isChecked);
            }
        });

        holder.selectButton.setOnClickListener(new View.OnClickListener() {
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
        TextView dayTextView;
        Button selectButton;

        ////

        public ViewHolder(View itemView) {
            super(itemView);

            itemLayout = (LinearLayout)
                    itemView.findViewById(R.id.monitoring_activity_route_report_days_list_item_layout);

            dayEnabledCheckBox = (CheckBox)
                    itemView.findViewById(R.id.monitoring_activity_route_report_days_list_item_day_enabled_check_box);
            dayEnabledCheckBox.setChecked(true);

            dayTextView = (TextView)
                    itemView.findViewById(R.id.monitoring_activity_route_report_days_list_item_day_text_view);

            selectButton = (Button)
                    itemView.findViewById(R.id.monitoring_activity_route_report_days_list_item_day_select_button);
        }
    }

    ////

    private void onSelectButtonClick(
            long date,
            RouteReportDaysListAdapter.ViewHolder holder) {

        selectedHolder.itemLayout.setBackgroundColor(
                ((Activity) view).getResources().getColor(android.R.color.white)
        );

        selectedHolder = holder;
        holder.itemLayout.setBackgroundColor(
                ((Activity) view).getResources().getColor(android.R.color.darker_gray)
        );

        view.selectDay(date);
    }
}
