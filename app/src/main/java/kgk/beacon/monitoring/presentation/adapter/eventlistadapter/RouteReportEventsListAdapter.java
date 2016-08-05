package kgk.beacon.monitoring.presentation.adapter.eventlistadapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import kgk.beacon.R;
import kgk.beacon.monitoring.domain.model.routereport.MovingEvent;
import kgk.beacon.monitoring.domain.model.routereport.ParkingEvent;
import kgk.beacon.monitoring.domain.model.routereport.RouteReportEvent;

public class RouteReportEventsListAdapter
        extends ExpandableRecyclerAdapter<EventsListParentViewHolder, EventsListChildViewHolder> {

    private LayoutInflater inflater;

    ////

    public RouteReportEventsListAdapter(Context context, List<ParentObject> parentItemList) {
        super(context, parentItemList);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    ////


    @Override
    public EventsListParentViewHolder onCreateParentViewHolder(ViewGroup viewGroup) {
        View view = inflater.inflate(
                R.layout.monitoring_activity_route_report_events_list_header,
                viewGroup,
                false);
        return new EventsListParentViewHolder(view);
    }

    @Override
    public EventsListChildViewHolder onCreateChildViewHolder(ViewGroup viewGroup) {
        View view = inflater.inflate(
                R.layout.monitoring_activity_route_report_events_list_item,
                viewGroup,
                false);
        return new EventsListChildViewHolder(view);
    }

    @Override
    public void onBindParentViewHolder(
            EventsListParentViewHolder holder,
            int i,
            Object parentObject) {

        EventsListObject object = (EventsListObject) parentObject;
        holder.titleTextView.setText(timestampToDateString(object.getTimestamp()));
    }

    @Override
    public void onBindChildViewHolder(EventsListChildViewHolder holder, int i, Object childObject) {
        RouteReportEvent event = (RouteReportEvent) childObject;

        holder.startTextView.setText(timestampToTimeString(event.getStartTime()));
        holder.endTextView.setText(timestampToTimeString(event.getEndTime()));

        holder.duration.setText(
                String.format(Locale.ROOT, "%d seconds", event.getDuration())
        );

        if (event instanceof ParkingEvent) {
            holder.typeTextView.setText("Parking");
            holder.informationTextView.setText(((ParkingEvent) event).getAddress());
        }

        if (event instanceof MovingEvent) {
            holder.typeTextView.setText("Moving");
            holder.informationTextView.setText(((MovingEvent) event).getDetails());
        }
    }

    ////

    @SuppressLint("SimpleDateFormat")
    private String timestampToDateString(long timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM");
        Date date = new Date(timestamp * 1000);
        return dateFormat.format(date);
    }

    @SuppressLint("SimpleDateFormat")
    private String timestampToTimeString(long timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date(timestamp * 1000);
        return dateFormat.format(date);
    }
}
