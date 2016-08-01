package kgk.beacon.monitoring.data;

import kgk.beacon.monitoring.presentation.model.MapType;

public interface Configuration {

    MapType loadDefaultMapType();

    void saveDefaultMapType(MapType mapType);

    boolean loadMarkerInformationEnabled();

    void saveMarkerInformationEnabled(boolean enabled);

    String loadActiveMonitoringEntityGroup();

    void saveActiveMonitoringEntityGroup(String groupName);

    long loadActiveMonitoringEntity();

    void saveActiveMonitoringEntity(long id);

    boolean loadDisplayEnabled(long id);

    void saveDisplayEnabled(long id, boolean enabled);
}
