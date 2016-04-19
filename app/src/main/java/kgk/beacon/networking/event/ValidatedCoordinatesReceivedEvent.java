package kgk.beacon.networking.event;

public class ValidatedCoordinatesReceivedEvent {

    private long serverDate;
    private double latitude;
    private double longitude;

    ////


    public long getServerDate() {
        return serverDate;
    }

    public void setServerDate(long serverDate) {
        this.serverDate = serverDate;
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
}
