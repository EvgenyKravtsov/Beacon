package kgk.beacon.monitoring.domain.model.routereport;


import java.util.ArrayList;
import java.util.List;

public class RouteReportParametersPeriodSeparated {

    public final List<RouteReportPeriod> periods;
    public final int stopTime; // seconds
    public final int offsetUtc; // seconds
    public final long id;

    ////

    public RouteReportParametersPeriodSeparated(
            long fromDateTimestamp,
            long toDateTimestamp,
            int stopTime,
            int offsetUtc,
            long id) {
        this.periods = composePeriods(fromDateTimestamp, toDateTimestamp);
        this.stopTime = stopTime;
        this.offsetUtc = -offsetUtc;
        this.id = id;
    }

    ////

    private List<RouteReportPeriod> composePeriods(
            long fromDateTimestamp,
            long toDateTimestamp) {

        List<RouteReportPeriod> periods = new ArrayList<>();
        long dayInSeconds = 24 * 60 * 60;

        if (toDateTimestamp - fromDateTimestamp <= dayInSeconds) {
            RouteReportPeriod period = new RouteReportPeriod(fromDateTimestamp, toDateTimestamp);
            periods.add(period);
        } else {
            long dayPeriod = fromDateTimestamp;
            while (dayPeriod + dayInSeconds <= toDateTimestamp) {
                RouteReportPeriod period = new RouteReportPeriod(dayPeriod, dayPeriod + dayInSeconds - 1);
                periods.add(period);
                dayPeriod += dayInSeconds;
            }

            RouteReportPeriod period = new RouteReportPeriod(dayPeriod, toDateTimestamp);
            periods.add(period);
        }

        return periods;
    }

    ////

    public class RouteReportPeriod {

        public final long fromDateTimestamp;
        public final long toDateTimestamp;

        ////

        public RouteReportPeriod(long fromDateTimestamp, long toDateTimestamp) {
            this.fromDateTimestamp = fromDateTimestamp;
            this.toDateTimestamp = toDateTimestamp;
        }
    }
}
