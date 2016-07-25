package kgk.beacon.monitoring.presentation.adapter;

import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import kgk.beacon.monitoring.presentation.model.MapEntity;

public class GoogleMapAdapter implements OnMapReadyCallback,
        MapAdapter, GoogleMap.OnCameraChangeListener {

    private final MapView googleMapView;

    private GoogleMap map;
    private boolean trafficEnabled;

    ////

    public GoogleMapAdapter(MapView googleMapView) {
        this.googleMapView = googleMapView;
        this.googleMapView.getMapAsync(this);
    }

    ////

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnCameraChangeListener(this);
    }

    ////

    @Override
    public void showMapEntity(MapEntity mapEntity) {
        LatLng latLng = new LatLng(mapEntity.getLatitude(), mapEntity.getLongitude());
        map.addMarker(new MarkerOptions().position(latLng));
    }

    @Override
    public void toggleTraffic() {
        trafficEnabled = !trafficEnabled;
        map.setTrafficEnabled(trafficEnabled);
    }

    @Override
    public void zoomIn() {
        map.animateCamera(CameraUpdateFactory.zoomIn());
    }

    @Override
    public void zoomOut() {
        map.animateCamera(CameraUpdateFactory.zoomOut());
    }

    @Override
    public void centerOnCoordinates(double latitude, double longitude) {
        map.animateCamera(CameraUpdateFactory
                .newLatLngZoom(new LatLng(latitude, longitude), map.getCameraPosition().zoom));
    }

    ////

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

        // TODO Delete test code
        Log.d("MON", "Camera changed");
    }
}
