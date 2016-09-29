package kgk.beacon.monitoring.domain.model.routereport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class RouteReport implements Serializable {

    public static final RouteReport emptyRouteReport = new RouteReport(new TreeMap<Long, List<RouteReportEvent>>());

    private final SortedMap<Long, List<RouteReportEvent>> days;
    private final List<Long> timestamps;

    ////

    public RouteReport(SortedMap<Long, List<RouteReportEvent>> days) {
        this.days = days;

        timestamps = new ArrayList<>();
        for (Map.Entry<Long, List<RouteReportEvent>> entry : days.entrySet())
            timestamps.add(entry.getKey());
    }

    ////

    public SortedMap<Long, List<RouteReportEvent>> getDays() {
        return days;
    }

    public List<Long> getTimestamps() {
        return timestamps;
    }

    ////

    @Override
    public String toString() {
        String str = "";
        for (Map.Entry<Long, List<RouteReportEvent>> entry : days.entrySet()) str += entry.getKey() + " ";
        return str;
    }
}
