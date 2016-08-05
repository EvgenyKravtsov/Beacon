package kgk.beacon.monitoring.presentation.adapter.eventlistadapter;

import android.view.View;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;

import kgk.beacon.R;

public class EventsListParentViewHolder extends ParentViewHolder {

    public TextView titleTextView;

    ////

    public EventsListParentViewHolder(View itemView) {
        super(itemView);

        titleTextView = (TextView)
                itemView.findViewById(R.id.monitoring_activty_route_report_list_header_title);
    }
}
