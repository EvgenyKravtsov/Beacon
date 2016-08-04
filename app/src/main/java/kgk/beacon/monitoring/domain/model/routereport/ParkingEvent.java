package kgk.beacon.monitoring.domain.model.routereport;

public class ParkingEvent extends RouteReportEvent {

    private final String address;
    private final double latitude;
    private final double longitude;
    private final int csq;

    ////

    public ParkingEvent(
            long startTime,
            long endTime,
            long duration,
            String address,
            double latitude,
            double longitude,
            int csq) {
        super(startTime, endTime, duration);
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.csq = csq;
    }
}
