package kgk.beacon.monitoring.domain.model.routereport;

import java.io.Serializable;

public class MovingEventSignal implements Serializable {

    private final long time;
    private final double latitude;
    private final double longitude;
    private final double speed;
    private final int csq;
    private final int direction;

    ////

    public MovingEventSignal(
            long time,
            double latitude,
            double longitude,
            double speed,
            int csq,
            int direction) {

        this.time = time;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.csq = csq;
        this.direction = direction;
    }


    ////

    public long getTime() {
        return time;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getSpeed() {
        return speed;
    }

    public int getCsq() {
        return csq;
    }

    public int getDirection() {
        return direction;
    }

    ////

    @Override
    public String toString() {
        return "**** Signal ****\n" +
                "latitude - " + latitude + "\n" +
                "longitude - " + longitude + "\n" +
                "speed - " + speed + "\n" +
                "csq - " + csq + "\n" +
                "direction - " + direction;
    }
}
