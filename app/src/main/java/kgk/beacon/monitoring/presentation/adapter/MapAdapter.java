package kgk.beacon.monitoring.presentation.adapter;

import kgk.beacon.monitoring.presentation.model.MapEntity;

public interface MapAdapter {

    void showMapEntity(MapEntity mapEntity);

    void toggleTraffic();

    void zoomIn();

    void zoomOut();

    void centerOnCoordinates(double latitude, double longitude);
}
