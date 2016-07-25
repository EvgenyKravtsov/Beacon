package kgk.beacon.monitoring.domain.model;

public class MonitoringEntity {

    private double latitude;
    private double longitude;

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
