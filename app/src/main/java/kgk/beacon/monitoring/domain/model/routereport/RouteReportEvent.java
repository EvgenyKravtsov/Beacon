package kgk.beacon.monitoring.domain.model.routereport;

import java.io.Serializable;

public abstract class RouteReportEvent implements Serializable {

    protected final long startTime; // unix timestamp
    protected final long endTime; // unix timestamp
    protected final long duration; // seconds

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
