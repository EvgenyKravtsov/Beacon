package kgk.beacon.monitoring.domain.model.routereport;

public abstract class RouteReportEvent {

    private final long startTime; // unix timestamp
    private final long endTime; // unix timestamp
    private final long duration; // seconds

    ////

    public RouteReportEvent(long startTime, long endTime, long duration) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
    }

    ////

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public long getDuration() {
        return duration;
    }
}
