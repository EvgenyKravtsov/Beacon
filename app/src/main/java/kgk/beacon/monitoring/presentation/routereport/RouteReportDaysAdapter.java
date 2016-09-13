package kgk.beacon.monitoring.presentation.routereport;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.BindDrawable;
import butterknife.ButterKnife;
import kgk.beacon.R;

import static kgk.beacon.monitoring.presentation.routereport.RouteReportContract.*;
import static kgk.beacon.monitoring.presentation.routereport.RouteReportContract.DaysView;

public class RouteReportDaysAdapter
        extends RecyclerView.Adapter<RouteReportDaysAdapter.ViewHolder>
        implements DaysView {

    private Presenter presenter;
    private List<Long> timestamps;
    private long activeDayTimestamp;

    ////

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setTimestamps(List<Long> timestamps) {
        this.timestamps = timestamps;
        notifyDataSetChanged();
    }

    @Override
    public void setActiveDay(long timestamp) {
        activeDayTimestamp = timestamp;
        notifyDataSetChanged();
    }

    ////

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.list_item_route_report_days,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final long timestamp = timestamps.get(position);
        Date date = new Date(timestamp);
        String dateString = new SimpleDateFormat("d.MM").format(date);
        holder.dateTextView.setText(dateString);

        if (timestamp == activeDayTimestamp)
            holder.containerFrameLayout.setBackgroundDrawable(holder.backgroundPressedDrawable);
        else holder.containerFrameLayout.setBackgroundDrawable(holder.backgroundDrawable);

        holder.containerFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDayClick(timestamp);
            }
        });
    }

    @Override
    public int getItemCount() {
        return timestamps.size();
    }

    ////

    private void onDayClick(long timestamp) {
        presenter.onDayChosen(timestamp);
    }

    ////

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.route_report_days_container)
        FrameLayout containerFrameLayout;
        @Bind(R.id.route_report_days_date)
        TextView dateTextView;

        @BindDrawable(R.drawable.route_report_days_list_item_background)
        Drawable backgroundDrawable;
        @BindDrawable(R.drawable.route_report_days_list_item_background_pressed)
        Drawable backgroundPressedDrawable;

        ////

        public ViewHolder(android.view.View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
