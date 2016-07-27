package kgk.beacon.monitoring.presentation.adapter;

import kgk.beacon.monitoring.domain.model.MonitoringEntity;

public interface MapAdapter {

    void showMapEntity(MonitoringEntity monitoringEntity);

    void toggleTraffic();

    void zoomIn();

    void zoomOut();

    void centerOnCoordinates(double latitude, double longitude);

    void clearMap();
}
