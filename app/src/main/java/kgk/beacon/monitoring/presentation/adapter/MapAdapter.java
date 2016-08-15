package kgk.beacon.monitoring.presentation.adapter;

import kgk.beacon.monitoring.domain.model.MonitoringEntity;
import kgk.beacon.monitoring.presentation.model.MapType;

public interface MapAdapter {

    void showMapEntity(MonitoringEntity monitoringEntity);

    void toggleTraffic();

    void zoomIn();

    void zoomOut();

    void centerOnCoordinates(double latitude, double longitude);

    void centerOnCoordinatesAnimated(double latitude, double longitude);

    void setMapType(MapType mapType);

    void clearMap();

    void clearMarkers();
}
