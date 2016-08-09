package kgk.beacon.monitoring.presentation.adapter;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import kgk.beacon.monitoring.domain.model.routereport.RouteReportEvent;

public interface RouteReportMapAdapter {

    void zoomIn();

    void zoomOut();

    void centerMap(LatLng coordinates);

    void showRouteReportDay(long date, List<RouteReportEvent> events);

    void clearRouteReportDay(long date);
}
