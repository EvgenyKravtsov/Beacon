package kgk.beacon.monitoring.domain.model;

public class MonitoringEntity {

    private final long id;
    private final String mark;
    private final String model;
    private final String stateNumber;

    private double latitude;
    private double longitude;
    private long lastUpdateTimestamp; // unix seconds
    private MonitoringEntityStatus status;
    private boolean displayEnabled;



    ////

    public MonitoringEntity(long id,
                            String mark,
                            String model,
                            String stateNumber) {
        this.id = id;
        this.mark = mark;
        this.model = model;
        this.stateNumber = stateNumber;
    }

    ////

    public long getId() {
        return id;
    }

    public String getMark() {
        return mark;
    }

    public String getModel() {
        return model;
    }

    public String getStateNumber() {
        return stateNumber;
    }

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

    public long getLastUpdateTimestamp() {
        return lastUpdateTimestamp;
    }

    public void setLastUpdateTimestamp(long lastUpdateTimestamp) {
        this.lastUpdateTimestamp = lastUpdateTimestamp;
    }

    public MonitoringEntityStatus getStatus() {
        return status;
    }

    public void setStatus(MonitoringEntityStatus status) {
        this.status = status;
    }

    public boolean isDisplayEnabled() {
        return displayEnabled;
    }

    public void setDisplayEnabled(boolean displayEnabled) {
        this.displayEnabled = displayEnabled;
    }

    ////

    @Override
    public String toString() {
        return "Monitroing Entity: Mark = "
                + mark + " Model = "
                + model + " StateNumber = "
                + stateNumber;
    }
}
