package kgk.beacon.monitoring.domain.model.routereport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class RouteReport implements Serializable {

    private final Map<Long, List<RouteReportEvent>> days;

    ////

    public RouteReport(Map<Long, List<RouteReportEvent>> days) {
        this.days = days;
    }

    ////

    public List<Calendar> getDays() {

        // TODO Delete test code
        List<Calendar> days = new ArrayList<>();
        days.add(Calendar.getInstance());
        days.add(Calendar.getInstance());
        days.add(Calendar.getInstance());
        return days;
    }
}
