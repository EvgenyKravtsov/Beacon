package kgk.beacon.monitoring.presentation.adapter;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import kgk.beacon.R;
import kgk.beacon.monitoring.domain.model.routereport.MovingEvent;
import kgk.beacon.monitoring.domain.model.routereport.MovingEventSignal;
import kgk.beacon.monitoring.domain.model.routereport.ParkingEvent;
import kgk.beacon.monitoring.domain.model.routereport.RouteReportEvent;
import kgk.beacon.monitoring.presentation.view.RouteReportView;

public class RouteReportEventsListAdapter
        extends RecyclerView.Adapter<RouteReportEventsListAdapter.ViewHolder> {

    private RouteReportView view;
    private List<RouteReportEvent> events;
    private boolean isAlreadyLaunched;

    ////

    public RouteReportEventsListAdapter(
            RouteReportView view,
            List<RouteReportEvent> events) {
        this.view = view;
        this.events = events;
    }

    ////

    public void setEvents(List<RouteReportEvent> events) {
        this.events = events;
    }

    ////

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(
                                R.layout.monitoring_activity_route_report_events_list_item,
                                parent,
                                false
                        )
        );
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final RouteReportEvent event = (RouteReportEvent) events.get(position);

        holder.startTextView.setText(timestampToTimeString(event.getStartTime()));
        holder.endTextView.setText(timestampToTimeString(event.getEndTime()));

        holder.durationTextView.setText(
                String.format(Locale.ROOT, "%d seconds", event.getDuration())
        );

        if (event instanceof ParkingEvent) {
            holder.typeTextView.setText("Parking:");
            holder.informationTextView.setText(((ParkingEvent) event).getAddress());
        }

        if (event instanceof MovingEvent) {
            holder.typeTextView.setText("Moving:");
            holder.informationTextView.setText(((MovingEvent) event).getDetails());
        }

        if (!isAlreadyLaunched && events.indexOf(event) == 0) {
            isAlreadyLaunched = true;

            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            String timeStartString = dateFormat.format(new Date(event.getStartTime()));
            String timeEndString = dateFormat.format(new Date(event.getEndTime()));

            if (event instanceof ParkingEvent) {
                view.showEventDetails(
                        "Parking",
                        timeStartString + " - " + timeEndString,
                        0,
                        ((ParkingEvent) event).getCsq(),
                        0
                );
            }


            if (event instanceof MovingEvent) {
                MovingEventSignal signal = ((MovingEvent) event).getSignals().get(0);
                view.showEventDetails(
                        "Moving",
                        timeStartString + " - " + timeEndString,
                        signal.getSpeed(),
                        signal.getCsq(),
                        0
                );
            }
        }

        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick(event);
            }
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    ////

    @SuppressLint("SimpleDateFormat")
    public void eventSelectedByTime(long time) {
        RouteReportEvent selectedEvent = null;
        for (RouteReportEvent event : events)
            if (time >= event.getStartTime() && time < event.getEndTime()) selectedEvent = event;

        if (selectedEvent == null) {
            view.clearEventDetails();
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String timeStartString = dateFormat.format(new Date(selectedEvent.getStartTime()));
        String timeEndString = dateFormat.format(new Date(selectedEvent.getEndTime()));

        if (selectedEvent instanceof ParkingEvent) {
            view.showEventDetails(
                    "Parking",
                    timeStartString + " - " + timeEndString,
                    0,
                    ((ParkingEvent) selectedEvent).getCsq(),
                    0
            );
        }


        if (selectedEvent instanceof MovingEvent) {
            MovingEventSignal signal = ((MovingEvent) selectedEvent).getSignals().get(0);
            view.showEventDetails(
                    "Moving",
                    timeStartString + " - " + timeEndString,
                    signal.getSpeed(),
                    signal.getCsq(),
                    0
            );
        }
    }

    ////

    public static class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout itemLayout;
        TextView typeTextView;
        TextView informationTextView;
        TextView startTextView;
        TextView endTextView;
        TextView durationTextView;

        ////

        public ViewHolder(View itemView) {
            super(itemView);

            itemLayout = (LinearLayout)
                    itemView.findViewById(
                            R.id.monitoring_activity_route_report_events_list_item_layout);

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

            durationTextView = (TextView)
                    itemView.findViewById(
                            R.id.monitoring_activity_route_report_events_list_duration_text_view);
        }
    }

    ////

    @SuppressLint("SimpleDateFormat")
    private String timestampToTimeString(long timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date(timestamp);
        return dateFormat.format(date);
    }

    @SuppressLint("SimpleDateFormat")
    private void onItemClick(RouteReportEvent event) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String timeStartString = dateFormat.format(new Date(event.getStartTime()));
        String timeEndString = dateFormat.format(new Date(event.getEndTime()));

        if (event instanceof ParkingEvent) {
            view.centerOnParkingEvent((ParkingEvent) event);
            view.showEventDetails(
                    "Parking",
                    timeStartString + " - " + timeEndString,
                    0,
                    ((ParkingEvent) event).getCsq(),
                    0
            );
        }


        if (event instanceof MovingEvent) {
            MovingEventSignal signal = ((MovingEvent) event).getSignals().get(0);

            view.centerOnMovingEvent((MovingEvent) event);
            view.showEventDetails(
                    "Moving",
                    timeStartString + " - " + timeEndString,
                    signal.getSpeed(),
                    signal.getCsq(),
                    0
            );
        }

        view.showEventStartTime(event.getStartTime());
    }
}
