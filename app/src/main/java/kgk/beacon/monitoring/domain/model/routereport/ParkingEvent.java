package kgk.beacon.monitoring.domain.model.routereport;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

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

    ////

    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getCsq() {
        return csq;
    }

    ////

    @Override
    public LatLng getCoordinates() {
        return new LatLng(latitude, longitude);
    }

    ////

    @Override
    public String toString() {
        return "== Parking Event ==\n" +
                "start - " + new Date(startTime) + "\n" +
                "end - " + new Date(endTime) + "\n" +
                "duration - " + duration + "\n" +
                "address - " + address + "\n" +
                "latitude - " + latitude + "\n" +
                "longitude - " + longitude + "\n" +
                "csq - " + csq;
    }
}
