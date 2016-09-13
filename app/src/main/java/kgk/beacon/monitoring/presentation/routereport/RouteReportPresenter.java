package kgk.beacon.monitoring.presentation.routereport;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;

import kgk.beacon.monitoring.DependencyInjection;
import kgk.beacon.monitoring.data.Configuration;
import kgk.beacon.monitoring.domain.model.MonitoringManager;
import kgk.beacon.monitoring.domain.model.routereport.MovingEvent;
import kgk.beacon.monitoring.domain.model.routereport.MovingEventSignal;
import kgk.beacon.monitoring.domain.model.routereport.ParkingEvent;
import kgk.beacon.monitoring.domain.model.routereport.RouteReport;
import kgk.beacon.monitoring.domain.model.routereport.RouteReportEvent;
import kgk.beacon.monitoring.domain.model.routereport.RouteReportEventType;

import static kgk.beacon.monitoring.presentation.routereport.RouteReportContract.*;
import static kgk.beacon.monitoring.presentation.routereport.RouteReportContract.Map;
import static kgk.beacon.monitoring.presentation.routereport.RouteReportContract.Presenter;
import static kgk.beacon.monitoring.presentation.routereport.RouteReportContract.View;

public class RouteReportPresenter implements Presenter {

    private View view;

    private Map map;
    private DaysView daysView;
    private RouteReport routeReport;
    private long activeDayTimestamp;

    private Configuration configuration;

    ////

    public RouteReportPresenter(Map map, DaysView daysView, RouteReport routeReport) {
        this.map = map;
        this.daysView = daysView;
        this.routeReport = routeReport;

        // Inner dependencies
        configuration = DependencyInjection.provideConfiguration();
    }

    ////

    public View getView() {
        return view;
    }

    ////

    @Override
    public void attachView(View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        view = null;
    }

    @Override
    public void onCreateView() {
        map.init(configuration.loadDefaultMapType(), configuration.loadZoomLevel());
        daysView.setTimestamps(routeReport.getTimestamps());
    }

    @Override
    public void onMapReady() {
        SortedMap<Long, List<RouteReportEvent>> routeReportDays = routeReport.getDays();
        List<RouteReportEvent> events = routeReportDays.get(routeReportDays.lastKey());
        RouteReportEvent event = events.get(0);
        activeDayTimestamp = routeReportDays.lastKey();

        map.displayRouteReportEvents(events);
        centerMapOnEvent(event);

        view.displayMark(MonitoringManager.getInstance().getActiveMonitoringEntity().getMark());
        view.displaySpeed(String.format(Locale.ROOT, "%.1f", getSpeedFromEvent(event)));
        view.displayGsmLevel(getGsmLevelFromEvent(event));
        view.displayEventDescription(
                getEventTypeFromEvent(event),
                String.format(
                        "%s-%s",
                        timestampToTimeString(event.getStartTime()),
                        timestampToTimeString(event.getEndTime())));

        daysView.setActiveDay(routeReportDays.lastKey());
    }

    @Override
    public void onTimeChosenFromTimeline(int timeInMilleseconds) {
        List<RouteReportEvent> events = routeReport.getDays().get(activeDayTimestamp);
        List<Long> timestampsForDay = new ArrayList<>();
        RouteReportEvent chosenEvent = null;

        // Update map
        for (RouteReportEvent event : events) {
            timestampsForDay.add(event.getStartTime());

            if (event instanceof MovingEvent)
                for (MovingEventSignal signal : ((MovingEvent) event).getSignals())
                    timestampsForDay.add(signal.getTime());
        }

        long closestEventTimestamp = getClosestTimestamp(
                activeDayTimestamp + timeInMilleseconds, timestampsForDay);

        for (RouteReportEvent event : events) {
            if (event.getStartTime() == closestEventTimestamp) {
                centerMapOnEvent(event);
                chosenEvent = event;
                break;
            }

            if (event instanceof MovingEvent) {
                for (MovingEventSignal signal : ((MovingEvent) event).getSignals()) {
                    if (signal.getTime() == closestEventTimestamp) {
                        map.centerOnMovingEventSignal(signal);
                        break;
                    }
                }
            }
        }

        // Update information panel
        if (chosenEvent == null) return;
        view.displayMark(MonitoringManager.getInstance().getActiveMonitoringEntity().getMark());
        view.displaySpeed(String.format(Locale.ROOT, "%.1f", getSpeedFromEvent(chosenEvent)));
        view.displayGsmLevel(getGsmLevelFromEvent(chosenEvent));
        view.displayEventDescription(
                getEventTypeFromEvent(chosenEvent),
                String.format(
                        "%s-%s",
                        timestampToTimeString(chosenEvent.getStartTime()),
                        timestampToTimeString(chosenEvent.getEndTime())));
    }

    @Override
    public void onDayChosen(long dayTimestamp) {
        List<RouteReportEvent> events = routeReport.getDays().get(dayTimestamp);
        RouteReportEvent event = events.get(0);

        activeDayTimestamp = dayTimestamp;
        view.resetTimeline();

        map.displayRouteReportEvents(events);
        centerMapOnEvent(event);

        view.displayMark(MonitoringManager.getInstance().getActiveMonitoringEntity().getMark());
        view.displaySpeed(String.format(Locale.ROOT, "%.1f", getSpeedFromEvent(event)));
        view.displayGsmLevel(getGsmLevelFromEvent(event));
        view.displayEventDescription(
                getEventTypeFromEvent(event),
                String.format(
                        "%s-%s",
                        timestampToTimeString(event.getStartTime()),
                        timestampToTimeString(event.getEndTime())));

        daysView.setActiveDay(dayTimestamp);
    }

    ////

    private void centerMapOnEvent(RouteReportEvent event) {
        if (event instanceof ParkingEvent) map.centerOnParkingEvent((ParkingEvent) event);
        if (event instanceof MovingEvent) map.centerOnMovingEvent((MovingEvent) event);
    }

    private double getSpeedFromEvent(RouteReportEvent event) {
        if (event instanceof ParkingEvent) return 0;
        if (event instanceof MovingEvent) return ((MovingEvent) event).getSignals().get(0).getSpeed();
        return 0;
    }

    private int getGsmLevelFromEvent(RouteReportEvent event) {
        if (event instanceof ParkingEvent) return ((ParkingEvent) event).getCsq();
        if (event instanceof MovingEvent) return ((MovingEvent) event).getSignals().get(0).getCsq();
        return 0;
    }

    private RouteReportEventType getEventTypeFromEvent(RouteReportEvent event) {
        if (event instanceof MovingEvent) return RouteReportEventType.Moving;
        else return RouteReportEventType.Parking;
    }

    private String timestampToTimeString(long timestamp) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

        Date date = new Date(timestamp);
        return dateFormat.format(date);
    }

    private long getClosestTimestamp(long timestamp, List<Long> timestamps) {
        int lo = 0;
        int hi = timestamps.size() - 1;

        long lastTimestamp = 0;

        while (lo <= hi) {
            int mid = (lo + hi) / 2;
            lastTimestamp = timestamps.get(mid);

            if (timestamp < lastTimestamp) hi = mid - 1;
            else if (timestamp > lastTimestamp) lo = mid + 1;
            else return lastTimestamp;
        }

        return lastTimestamp;
    }
}
