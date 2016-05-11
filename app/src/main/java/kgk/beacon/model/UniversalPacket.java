package kgk.beacon.model;

/**
 * Абстрактый пакет с данными от устройства
 */
public class UniversalPacket {

    private double latitude;
    private double longitude;
    private int direction;

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

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    ////

    @Override
    public String toString() {
        return "Packet for track: "
                + this.getLatitude() + " | "
                + this.getLongitude();
    }
}
