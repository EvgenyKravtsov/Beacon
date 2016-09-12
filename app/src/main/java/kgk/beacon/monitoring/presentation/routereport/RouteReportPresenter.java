package kgk.beacon.monitoring.presentation.routereport;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;

import kgk.beacon.monitoring.DependencyInjection;
import kgk.beacon.monitoring.data.Configuration;
import kgk.beacon.monitoring.domain.model.MonitoringManager;
import kgk.beacon.monitoring.domain.model.routereport.MovingEvent;
import kgk.beacon.monitoring.domain.model.routereport.ParkingEvent;
import kgk.beacon.monitoring.domain.model.routereport.RouteReport;
import kgk.beacon.monitoring.domain.model.routereport.RouteReportEvent;
import kgk.beacon.monitoring.domain.model.routereport.RouteReportEventType;

import static kgk.beacon.monitoring.presentation.routereport.RouteReportContract.Map;
import static kgk.beacon.monitoring.presentation.routereport.RouteReportContract.Presenter;
import static kgk.beacon.monitoring.presentation.routereport.RouteReportContract.View;

public class RouteReportPresenter implements Presenter {

    private View view;

    private Map map;
    private RouteReport routeReport;

    private Configuration configuration;

    ////

    public RouteReportPresenter(Map map, RouteReport routeReport) {
        this.map = map;
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
        map.init(
                configuration.loadDefaultMapType(),
                configuration.loadZoomLevel());
    }

    @Override
    public void onMapReady() {
        SortedMap<Long, List<RouteReportEvent>> routeReportDays = routeReport.getDays();
        List<RouteReportEvent> events = routeReportDays.get(routeReportDays.lastKey());
        RouteReportEvent event = events.get(0);

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
}
