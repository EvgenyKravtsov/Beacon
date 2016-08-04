package kgk.beacon.monitoring.domain.model.routereport;

public class MovingEventSignal {

    private final double latitude;
    private final double longitude;
    private final double speed;
    private final int csq;

    ////

    public MovingEventSignal(double latitude, double longitude, double speed, int csq) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.csq = csq;
    }
}
