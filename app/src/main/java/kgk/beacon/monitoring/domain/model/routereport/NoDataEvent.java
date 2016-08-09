package kgk.beacon.monitoring.domain.model.routereport;

import com.google.android.gms.maps.model.LatLng;

public class NoDataEvent extends RouteReportEvent {

    public NoDataEvent(long startTime, long endTime, long duration) {
        super(startTime, endTime, duration);
    }

    ////

    @Override
    public LatLng getCoordinates() {
        return null;
    }
}
