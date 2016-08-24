package kgk.beacon.monitoring.domain.model.routereport;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

public class RouteReport implements Serializable {

    private final SortedMap<Long, List<RouteReportEvent>> days;

    ////

    public RouteReport(SortedMap<Long, List<RouteReportEvent>> days) {
        this.days = days;
    }

    ////

    public SortedMap<Long, List<RouteReportEvent>> getDays() {
        return days;
    }

    ////

    @Override
    public String toString() {
        String str = "";
        for (Map.Entry<Long, List<RouteReportEvent>> entry : days.entrySet()) str += entry.getKey() + " ";
        return str;
    }
}
