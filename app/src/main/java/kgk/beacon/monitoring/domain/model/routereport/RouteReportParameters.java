package kgk.beacon.monitoring.domain.model.routereport;

public class RouteReportParameters {

    private final long fromDateTimestamp;
    private final long toDateTimestamp;
    private final int stopTime; // seconds
    private final int offsetUtc;
    private final long id;

    ////

    public RouteReportParameters(
            long fromDateTimestamp,
            long toDateTimestamp,
            int stopTime,
            int offsetUtc,
            long id) {
        this.fromDateTimestamp = fromDateTimestamp;
        this.toDateTimestamp = toDateTimestamp;
        this.stopTime = stopTime;
        this.offsetUtc = -offsetUtc;
        this.id = id;
    }

    ////

    public long getFromDateTimestamp() {
        return fromDateTimestamp;
    }

    public long getToDateTimestamp() {
        return toDateTimestamp;
    }

    public int getStopTime() {
        return stopTime;
    }

    public int getOffsetUtc() {
        return offsetUtc;
    }

    public long getId() {
        return id;
    }
}
