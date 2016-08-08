package kgk.beacon.monitoring.presentation.adapter;

import java.util.List;

import kgk.beacon.monitoring.domain.model.routereport.RouteReportEvent;

public interface RouteReportMapAdapter {

    void zoomIn();

    void zoomOut();

    void showEvents(List<RouteReportEvent> events);
}
