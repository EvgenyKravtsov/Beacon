package kgk.beacon.monitoring.presentation.model;

import kgk.beacon.monitoring.domain.model.MonitoringEntity;

public class MapEntity {

    private double latitude;
    private double longitude;

    ////

    public MapEntity(MonitoringEntity monitoringEntity) {
        this.latitude = monitoringEntity.getLatitude();
        this.longitude = monitoringEntity.getLongitude();
    }

    ////

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
