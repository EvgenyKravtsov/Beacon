package kgk.beacon.monitoring.domain.model.routereport;

public class NoDataEvent extends RouteReportEvent {

    public NoDataEvent(long startTime, long endTime, long duration) {
        super(startTime, endTime, duration);
    }
}
