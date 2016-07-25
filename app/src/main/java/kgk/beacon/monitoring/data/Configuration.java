package kgk.beacon.monitoring.data;

import kgk.beacon.monitoring.presentation.model.MapType;

public interface Configuration {

    MapType loadDefaultMapType();

    void saveDefaultMapType(MapType mapType);
}
