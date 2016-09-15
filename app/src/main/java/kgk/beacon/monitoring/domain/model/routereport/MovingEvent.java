package kgk.beacon.monitoring.domain.model.routereport;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.List;

public class MovingEvent extends RouteReportEvent {

    private final String details;
    private final double latitude;
    private final double longitude;
    private final double averageSpeed;
    private final double maxSpeed;
    private final double mileage;
    private final double maxMileage;
    private final List<MovingEventSignal> signals;

    ////

    public MovingEvent(long startTime,
                       long endTime,
                       long duration,
                       String details,
                       double latitude,
                       double longitude,
                       double averageSpeed,
                       double maxSpeed,
                       double mileage,
                       double maxMileage,
                       List<MovingEventSignal> signals) {

        super(startTime, endTime, duration);
        this.details = details;
        this.latitude = latitude;
        this.longitude = longitude;
        this.averageSpeed = averageSpeed;
        this.maxSpeed = maxSpeed;
        this.mileage = mileage;
        this.maxMileage = maxMileage;
        this.signals = signals;
    }

    ////

    public String getDetails() {
        return details;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getAverageSpeed() {
        return averageSpeed;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public double getMileage() {
        return mileage;
    }

    public double getMaxMileage() {
        return maxMileage;
    }

    public List<MovingEventSignal> getSignals() {
        return signals;
    }

    ////

    @Override
    public LatLng getCoordinates() {
        return new LatLng(latitude, longitude);
    }

    ////

    @Override
    public String toString() {
        String str = "== Moving Event ==\n" +
                "start - " + new Date(startTime) + "\n" +
                "end - " + new Date(endTime) + "\n" +
                "duration - " + duration + "\n" +
                "details - " + details + "\n" +
                "latitude - " + latitude + "\n" +
                "longitude - " + longitude + "\n";
        for (MovingEventSignal signal : signals) str += signal.toString();
        return str;
    }
}
