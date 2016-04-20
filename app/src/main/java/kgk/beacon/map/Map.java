package kgk.beacon.map;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import kgk.beacon.model.Signal;

public interface Map {

    int DEFAULT_CIRCLE_ZONE_RADIUS = 500; // meters

    ////

    void setOnMapClickListener(MapClickListener listener);

    void setOnMarkerClickListener(MarkerClickListener listener);

    void addCustomMarkerPoint(int direction, double latitude, double longitude);

    void addSignalInfoMarker(Signal signal);

    void removeSignalInfoMarker();

    void addStopMarker(double latitude, double longitude);

    void addPolyline(ArrayList<LatLng> coordinates);

    void moveCamera(double latitude, double longitude, int zoom);

    void addCircleZone(double latitude, double longitude, int radius); // meters

    void clear();

    void turnOnFullscreenButton();
}
