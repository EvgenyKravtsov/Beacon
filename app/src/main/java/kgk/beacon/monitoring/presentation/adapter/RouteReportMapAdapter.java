package kgk.beacon.monitoring.presentation.adapter;

import java.util.List;

import kgk.beacon.monitoring.domain.model.routereport.MovingEvent;
import kgk.beacon.monitoring.domain.model.routereport.MovingEventSignal;
import kgk.beacon.monitoring.domain.model.routereport.ParkingEvent;
import kgk.beacon.monitoring.domain.model.routereport.RouteReportEvent;

public interface RouteReportMapAdapter {

    void zoomIn();

    void zoomOut();

    void centerOnParkingEvent(ParkingEvent event);

    void centerOnMovingEvent(MovingEvent event);

    void centerOnMovingEventSignal(MovingEventSignal signal);

    void showRouteReportDay(long date, List<RouteReportEvent> events);

    void clearRouteReportDay(long date);
}
