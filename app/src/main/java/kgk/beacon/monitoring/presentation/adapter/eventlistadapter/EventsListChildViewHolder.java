package kgk.beacon.monitoring.presentation.adapter.eventlistadapter;

import android.view.View;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;

import kgk.beacon.R;

public class EventsListChildViewHolder extends ChildViewHolder {

    public TextView typeTextView;
    public TextView informationTextView;
    public TextView startTextView;
    public TextView endTextView;
    public TextView duration;

    ////

    public EventsListChildViewHolder(View itemView) {
        super(itemView);

        typeTextView = (TextView)
                itemView.findViewById(
                        R.id.monitoring_activity_route_report_events_list_item_type_text_view);

        informationTextView = (TextView)
                itemView.findViewById(
                        R.id.monitoring_activity_route_report_events_list_item_information_text_view);

        startTextView = (TextView)
                itemView.findViewById(
                        R.id.monitoring_activity_route_report_events_list_start_text_view);

        endTextView = (TextView)
                itemView.findViewById(
                        R.id.monitoring_activity_route_report_events_list_end_text_view);

        duration = (TextView)
                itemView.findViewById(
                        R.id.monitoring_activity_route_report_events_list_duration_text_view);
    }
}
