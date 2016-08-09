package kgk.beacon.monitoring.presentation.model;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import kgk.beacon.monitoring.domain.model.routereport.ParkingEvent;
import kgk.beacon.monitoring.domain.model.routereport.RouteReportEvent;

public class ParkingEventMapObject extends RouteReportMapObject {

    private final GoogleMap map;
    private final RouteReportEvent event;
    private Marker parkingMarker;

    ////

    public ParkingEventMapObject(GoogleMap map, RouteReportEvent event) {
        this.map = map;
        this.event = event;
    }

    ////

    @Override
    public void draw() {
        ParkingEvent event = (ParkingEvent) this.event;
        parkingMarker = map.addMarker(new MarkerOptions()
                .position(new LatLng(event.getLatitude(), event.getLongitude())));
    }

    @Override
    public void clear() {
        parkingMarker.remove();
    }
}
