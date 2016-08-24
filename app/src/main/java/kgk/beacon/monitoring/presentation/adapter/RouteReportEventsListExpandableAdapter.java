package kgk.beacon.monitoring.presentation.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import kgk.beacon.R;
import kgk.beacon.monitoring.domain.model.routereport.MovingEvent;
import kgk.beacon.monitoring.domain.model.routereport.ParkingEvent;
import kgk.beacon.monitoring.domain.model.routereport.RouteReportEvent;
import kgk.beacon.monitoring.presentation.view.RouteReportView;

public class RouteReportEventsListExpandableAdapter extends BaseExpandableListAdapter
        implements ExpandableListView.OnChildClickListener {

    private RouteReportView view;
    private LayoutInflater inflater;
    private List<Long> expandableListTitle;
    private Map<Long, List<RouteReportEvent>> expandableListDetail;

    ////

    public RouteReportEventsListExpandableAdapter(
            RouteReportView view,
            List<Long> expandableListTitle,
            Map<Long, List<RouteReportEvent>> expandableListDetail) {

        inflater = (LayoutInflater)
                ((Context) view).getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.view = view;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
    }

    ////

    public void setExpandableListTitle(List<Long> expandableListTitle) {
        this.expandableListTitle = expandableListTitle;
    }

    public void setExpandableListDetail(Map<Long, List<RouteReportEvent>> expandableListDetail) {
        this.expandableListDetail = expandableListDetail;
    }

    ////

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return expandableListDetail.get(expandableListTitle.get(groupPosition)).get(childPosition);
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
            View convertView,
            ViewGroup parent) {

        RouteReportEvent event = (RouteReportEvent) getChild(groupPosition, childPosition);

        if (convertView == null) {
            convertView = inflater.inflate(
                    R.layout.monitoring_activity_route_report_events_list_item,
                    null);
        }

        TextView typeTextView = (TextView)
                convertView.findViewById(
                        R.id.monitoring_activity_route_report_events_list_item_type_text_view);

        TextView informationTextView = (TextView)
                convertView.findViewById(
                        R.id.monitoring_activity_route_report_events_list_item_information_text_view);

        TextView startTextView = (TextView)
                convertView.findViewById(
                        R.id.monitoring_activity_route_report_events_list_start_text_view);

        TextView endTextView = (TextView)
                convertView.findViewById(
                        R.id.monitoring_activity_route_report_events_list_end_text_view);

        TextView duration = (TextView)
                convertView.findViewById(
                        R.id.monitoring_activity_route_report_events_list_duration_text_view);

        startTextView.setText(timestampToTimeString(event.getStartTime()));
        endTextView.setText(timestampToTimeString(event.getEndTime()));

        duration.setText(
                String.format(Locale.ROOT, "%d seconds", event.getDuration())
        );

        if (event instanceof ParkingEvent) {
            typeTextView.setText("Parking");
            informationTextView.setText(((ParkingEvent) event).getAddress());
        }

        if (event instanceof MovingEvent) {
            typeTextView.setText("Moving");
            informationTextView.setText(((MovingEvent) event).getDetails());
        }

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return expandableListDetail.get(expandableListTitle.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return expandableListTitle.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return expandableListTitle.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(
            int groupPosition,
            boolean isExpanded,
            View convertView, ViewGroup parent) {

        String listTitle = timestampToDateString((long) getGroup(groupPosition));

        if (convertView == null) {
            convertView = inflater.inflate(
                    R.layout.monitoring_activity_route_report_events_list_header,
                    null);
        }

        TextView titleTextView = (TextView)
                convertView.findViewById(R.id.monitoring_activty_route_report_list_header_title);

        titleTextView.setText(listTitle);

        return convertView;
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

    @SuppressLint("SimpleDateFormat")
    @Override
    public boolean onChildClick(
            ExpandableListView parent,
            View v,
            int groupPosition,
            int childPosition,
            long id) {

        RouteReportEvent event = (RouteReportEvent) getChild(groupPosition, childPosition);

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String timeStartString = dateFormat.format(new Date(event.getStartTime()));
        String timeEndString = dateFormat.format(new Date(event.getEndTime()));

//        if (event instanceof ParkingEvent) {
//            view.centerOnChosenEvent(
//                    ((ParkingEvent) event).getLatitude(),
//                    ((ParkingEvent) event).getLongitude());
//            view.showEventDetails(
//                    "Parking " + timeStartString + " - " + timeEndString,
//                    0,
//                    ((ParkingEvent) event).getCsq(),
//                    0
//            );
//        }


//        if (event instanceof MovingEvent) {
//            MovingEventSignal signal = ((MovingEvent) event).getSignals().get(0);
//
//            view.centerOnChosenEvent(
//                    ((MovingEvent) event).getLatitude(),
//                    ((MovingEvent) event).getLongitude());
//            view.showEventDetails(
//                    "Moving " + timeStartString + " - " + timeEndString,
//                    signal.getSpeed(),
//                    signal.getCsq(),
//                    0
//            );
//        }

        return false;
    }


    ////

    @SuppressLint("SimpleDateFormat")
    private String timestampToDateString(long timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM");
        Date date = new Date(timestamp);
        return dateFormat.format(date);
    }

    @SuppressLint("SimpleDateFormat")
    private String timestampToTimeString(long timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date(timestamp);
        return dateFormat.format(date);
    }
}
