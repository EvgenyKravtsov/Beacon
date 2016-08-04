package kgk.beacon.monitoring.domain.model.routereport;

import java.util.List;

public class MovingEvent extends RouteReportEvent {

    private final String details;
    private final double latitude;
    private final double longitude;
    private final List<MovingEventSignal> signals;

    ////

    public MovingEvent(long startTime,
                       long endTime,
                       long duration,
                       String details,
                       double latitude,
                       double longitude,
                       List<MovingEventSignal> signals) {
        super(startTime, endTime, duration);
        this.details = details;
        this.latitude = latitude;
        this.longitude = longitude;
        this.signals = signals;
    }
}
