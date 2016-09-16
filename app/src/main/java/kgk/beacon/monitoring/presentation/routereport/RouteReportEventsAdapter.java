package kgk.beacon.monitoring.presentation.routereport;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import kgk.beacon.R;
import kgk.beacon.monitoring.domain.model.routereport.MovingEvent;
import kgk.beacon.monitoring.domain.model.routereport.ParkingEvent;
import kgk.beacon.monitoring.domain.model.routereport.RouteReportEvent;
import kgk.beacon.util.AppController;

import static kgk.beacon.monitoring.presentation.routereport.RouteReportContract.EventsView;
import static kgk.beacon.monitoring.presentation.routereport.RouteReportContract.Presenter;

public class RouteReportEventsAdapter extends BaseExpandableListAdapter implements EventsView,
        ExpandableListView.OnGroupExpandListener {

    private LayoutInflater inflater;
    private Presenter presenter;
    private List<RouteReportEvent> events;
    private java.util.Map<RouteReportEvent, RouteReportEvent> eventsMap;

    private Drawable collapsedGroupBackgroundDrawable;
    private Drawable expandedGroubBackgroundDrawable;
    private Drawable collapseDrawable;
    private Drawable expandDrawable;

    ////

    public RouteReportEventsAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
        events = new ArrayList<>();
        eventsMap = new HashMap<>();

        collapsedGroupBackgroundDrawable = AppController.getInstance().getResources()
                .getDrawable(R.drawable.monitoring_general_background);
        expandedGroubBackgroundDrawable = AppController.getInstance().getResources()
                .getDrawable(R.drawable.monitoring_events_list_item_expanded_background);
        collapseDrawable = AppController.getInstance().getResources()
                .getDrawable(R.drawable.monitoring_event_list_collapse_button_icon);
        expandDrawable = AppController.getInstance().getResources()
                .getDrawable(R.drawable.monitoring_event_list_expand_button_icon);
    }

    ////

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setEvents(List<RouteReportEvent> events) {
        this.events = events;
        for (RouteReportEvent event : events) eventsMap.put(event, event);
        notifyDataSetChanged();
    }

    ////

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return eventsMap.get(events.get(groupPosition));
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(
            int groupPosition,
            int childPosition,
            boolean isLastChild,
            View view,
            ViewGroup parent) {

        RouteReportEvent event = (RouteReportEvent) getChild(groupPosition, childPosition);

        if (event instanceof ParkingEvent)
            return prepareChildViewForParkingEvent(view, (ParkingEvent) event);
        else return prepareChildViewForMovingEvent(view, (MovingEvent) event);
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return events.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(
            int groupPosition,
            boolean isExpanded,
            View view,
            ViewGroup parent) {

        RouteReportEvent event = (RouteReportEvent) getGroup(groupPosition);
        if (view == null) view = inflater.inflate(R.layout.list_item_route_report_events, null);
        TextView descriptionTextView = (TextView) view.findViewById(R.id.description);
        descriptionTextView.setText(String.format(
                "%s %s - %s",
                getEventLabelFromEvent(event),
                timestampToTimeString(event.getStartTime()),
                timestampToTimeString(event.getEndTime())
        ));

        ImageView expandImageView = (ImageView) view.findViewById(R.id.expand);
        if (isExpanded) {
            view.setBackgroundDrawable(expandedGroubBackgroundDrawable);
            expandImageView.setImageDrawable(collapseDrawable);
        } else {
            view.setBackgroundDrawable(collapsedGroupBackgroundDrawable);
            expandImageView.setImageDrawable(expandDrawable);
        }

        if (AppController.isBelowLollopop()) view.setPadding(8, 8, 8, 8);
        return view;
    }

    @Override
    public int getGroupCount() {
        return events.size();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    ////

    @Override
    public void onGroupExpand(int groupPosition) {
        RouteReportEvent event = (RouteReportEvent) getGroup(groupPosition);
        presenter.onEventChosen(event);
    }

    ////

    @SuppressLint("InflateParams")
    private View prepareChildViewForParkingEvent(View convertView, ParkingEvent event) {
        convertView = inflater.inflate(
                R.layout.list_item_route_report_events_parking,
                null);

        TextView detailsTextView = (TextView) convertView
                .findViewById(R.id.parking_details);

        detailsTextView.setText(event.getAddress());
        return convertView;
    }

    @SuppressLint("InflateParams")
    private View prepareChildViewForMovingEvent(View convertView, MovingEvent event) {
        convertView = inflater.inflate(
                R.layout.list_item_route_report_events_moving,
                null);

        TextView mileageTextView = (TextView) convertView.findViewById(R.id.moving_details_mileage);
        TextView speedTextView = (TextView) convertView.findViewById(R.id.moving_details_speed);
        TextView addressTextView = (TextView) convertView.findViewById(R.id.moving_details_address);

        mileageTextView.setText(String.format(
                Locale.ROOT,
                "Пробег: %d км(всего %d км)",
                (int) event.getMileage(),
                (int) event.getMaxMileage()
        ));

        speedTextView.setText(String.format(
                Locale.ROOT,
                "Скорость: %.1f км/ч(макс. %.1f км/ч)",
                event.getAverageSpeed(),
                event.getMaxSpeed()
        ));

        addressTextView.setText(String.format(
                Locale.ROOT,
                "Адрес: %s",
                event.getDetails()
        ));

        return convertView;
    }

    private String getEventLabelFromEvent(RouteReportEvent event) {
        Context context = AppController.getInstance();
        if (event instanceof MovingEvent)
            return context.getString(R.string.monitoring_route_report_moving);
        else return context.getString(R.string.monitoring_route_report_parking);
    }

    private String timestampToTimeString(long timestamp) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

        Date date = new Date(timestamp);
        return dateFormat.format(date);
    }
}
