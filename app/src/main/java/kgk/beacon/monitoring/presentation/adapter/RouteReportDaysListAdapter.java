package kgk.beacon.monitoring.presentation.adapter;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

    ////

    public RouteReportDaysListAdapter(RouteReportView view, List<Calendar> days) {
        this.view = view;
        this.days = days;
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Calendar day = days.get(position);

        holder.dayTextView.setText(
                new SimpleDateFormat("dd MMMM")
                .format(day.getTime()));

        holder.dayEnabledCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                view.toggleDayDisplay(day, isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    ////

    public static class ViewHolder extends RecyclerView.ViewHolder {

        CheckBox dayEnabledCheckBox;
        TextView dayTextView;

        ////

        public ViewHolder(View itemView) {
            super(itemView);

            dayEnabledCheckBox = (CheckBox)
                    itemView.findViewById(R.id.monitoring_activity_route_report_days_list_item_day_enabled_check_box);
            dayTextView = (TextView)
                    itemView.findViewById(R.id.monitoring_activity_route_report_days_list_item_day_text_view);
        }
    }
}
