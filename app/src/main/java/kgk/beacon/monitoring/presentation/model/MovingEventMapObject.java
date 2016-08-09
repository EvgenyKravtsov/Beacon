package kgk.beacon.monitoring.presentation.model;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import kgk.beacon.monitoring.domain.model.routereport.MovingEvent;
import kgk.beacon.monitoring.domain.model.routereport.MovingEventSignal;
import kgk.beacon.monitoring.domain.model.routereport.RouteReportEvent;

public class MovingEventMapObject extends RouteReportMapObject {

    private final GoogleMap map;
    private final RouteReportEvent event;
    private Polyline movingPolyline;

    ////

    public MovingEventMapObject(GoogleMap map, RouteReportEvent event) {
        this.map = map;
        this.event = event;
    }

    ////

    @Override
    public void draw() {
        MovingEvent event = (MovingEvent) this.event;
        List<LatLng> coordinates = new ArrayList<>();

        for (MovingEventSignal signal : event.getSignals())
            coordinates.add(new LatLng(signal.getLatitude(), signal.getLongitude()));

        movingPolyline = map.addPolyline(new PolylineOptions().addAll(coordinates));
        movingPolyline.setZIndex(1000);
    }

    @Override
    public void clear() {
        movingPolyline.remove();
    }
}