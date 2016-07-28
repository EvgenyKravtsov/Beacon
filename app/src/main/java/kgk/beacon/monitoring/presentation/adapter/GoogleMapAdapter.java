package kgk.beacon.monitoring.presentation.adapter;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import kgk.beacon.monitoring.domain.model.MonitoringEntity;

public class GoogleMapAdapter implements OnMapReadyCallback,
        MapAdapter, GoogleMap.OnCameraChangeListener, GoogleMap.OnMarkerClickListener {

    private final kgk.beacon.monitoring.presentation.view.MapView mapView;
    private final MapView googleMapView;

    private GoogleMap map;
    private boolean trafficEnabled;
    private List<MonitoringEntityGoogleMarker> markers;

    ////

    public GoogleMapAdapter(
            kgk.beacon.monitoring.presentation.view.MapView mapView,
            MapView googleMapView) {
        this.mapView = mapView;
        this.googleMapView = googleMapView;
        this.markers = new ArrayList<>();
        this.googleMapView.getMapAsync(this);
    }

    ////

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnCameraChangeListener(this);
        map.setOnMarkerClickListener(this);
    }

    ////

    @Override
    public void showMapEntity(MonitoringEntity monitoringEntity) {
        long id = monitoringEntity.getId();
        LatLng latLng = new LatLng(monitoringEntity.getLatitude(), monitoringEntity.getLongitude());
        Marker marker = map.addMarker(new MarkerOptions().position(latLng));
        markers.add(new MonitoringEntityGoogleMarker(id, marker));
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
                .newLatLngZoom(new LatLng(latitude, longitude), 7));
    }

    @Override
    public void clearMap() {
        if (map != null) map.clear();
    }

    ////

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;
        LatLng latLng = new LatLng(
                mapView.getActiveMonitoringEntity().getLatitude(),
                mapView.getActiveMonitoringEntity().getLongitude()
        );

        if (bounds.contains(latLng)) mapView.toggleCenterOnActiveControl(false);
        else mapView.toggleCenterOnActiveControl(true);
    }

    ////

    @Override
    public boolean onMarkerClick(Marker marker) {
        for (MonitoringEntityGoogleMarker monitoringEntityGoogleMarker : markers) {
            if (monitoringEntityGoogleMarker.getMarker().equals(marker)) {
                mapView.monitoringEntityChosen(monitoringEntityGoogleMarker.getId());
            }
        }
        return true;
    }

    ////

    public class MonitoringEntityGoogleMarker {

        private final long id;
        private final Marker marker;

        ////

        public MonitoringEntityGoogleMarker(long id, Marker marker) {
            this.id = id;
            this.marker = marker;
        }

        ////

        public long getId() {
            return id;
        }

        public Marker getMarker() {
            return marker;
        }
    }
}





























