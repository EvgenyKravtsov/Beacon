package kgk.beacon.monitoring.presentation.view;

import java.util.List;

import kgk.beacon.monitoring.presentation.model.MapEntity;

public interface MapView {

    void showMapEntities(List<MapEntity> mapEntities);

    void centerOnMapEntity(MapEntity mapEntity);
}
