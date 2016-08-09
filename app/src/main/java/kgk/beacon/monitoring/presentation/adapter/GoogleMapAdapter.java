package kgk.beacon.monitoring.presentation.adapter;

import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import kgk.beacon.monitoring.DependencyInjection;
import kgk.beacon.monitoring.data.Configuration;
import kgk.beacon.monitoring.domain.model.MonitoringEntity;
import kgk.beacon.monitoring.presentation.model.MapType;
import kgk.beacon.util.YandexMapUtils;

public class GoogleMapAdapter implements OnMapReadyCallback,
        MapAdapter, GoogleMap.OnCameraChangeListener, GoogleMap.OnMarkerClickListener {

    private final kgk.beacon.monitoring.presentation.view.MapView mapView;
    private final MapView googleMapView;

    private Configuration configuration;
    private GoogleMap map;
    private TileOverlay kgkTileOverlay;
    private TileOverlay yandexTileOverlay;
    private boolean trafficEnabled;
    private List<MonitoringEntityGoogleMarker> markers;

    ////

    public GoogleMapAdapter(
            kgk.beacon.monitoring.presentation.view.MapView mapView,
            MapView googleMapView) {
        this.mapView = mapView;
        this.googleMapView = googleMapView;
        this.configuration = DependencyInjection.provideConfiguration();
        this.markers = new ArrayList<>();
        this.googleMapView.getMapAsync(this);
    }

    ////

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnCameraChangeListener(this);
        map.setOnMarkerClickListener(this);
        mapView.mapReadyForUse();
    }

    ////

    @Override
    public void showMapEntity(MonitoringEntity monitoringEntity) {
        long id = monitoringEntity.getId();
        LatLng latLng = new LatLng(monitoringEntity.getLatitude(), monitoringEntity.getLongitude());

        if (markers != null) {
            boolean redrawn = false;
            for (MonitoringEntityGoogleMarker marker : markers) {
                if (marker.getId() == id) {
                    marker.getMarker().remove();
                    marker.setMarker(map.addMarker(new MarkerOptions().position(latLng)));
                    redrawn = true;
                }
            }

            if (!redrawn)
            markers.add(new MonitoringEntityGoogleMarker(
                    id,
                    map.addMarker(new MarkerOptions().position(latLng))));
        }
    }

    @Override
    public void toggleTraffic() {
        trafficEnabled = !trafficEnabled;
        map.setTrafficEnabled(trafficEnabled);
    }

    @Override
    public void zoomIn() {
        map.animateCamera(CameraUpdateFactory.zoomIn());
        configuration.saveZoomLevel(map.getCameraPosition().zoom);
    }

    @Override
    public void zoomOut() {
        map.animateCamera(CameraUpdateFactory.zoomOut());
        configuration.saveZoomLevel(map.getCameraPosition().zoom);
    }

    @Override
    public void centerOnCoordinates(double latitude, double longitude) {
        map.animateCamera(CameraUpdateFactory
                .newLatLngZoom(new LatLng(latitude, longitude),
                        configuration.loadZoomLevel()));
    }

    @Override
    public void setMapType(MapType mapType) {
        if (map == null) return;
        if (kgkTileOverlay != null) kgkTileOverlay.remove();
        if (yandexTileOverlay != null) yandexTileOverlay.remove();

        switch (mapType) {
            case KGK:
                map.setMapType(GoogleMap.MAP_TYPE_NONE);
                kgkTileOverlay = map.addTileOverlay(prepareKgkMap());
                break;
            case YANDEX:
                map.setMapType(GoogleMap.MAP_TYPE_NONE);

                // TODO Delete test code
                map.clear();

                yandexTileOverlay = map.addTileOverlay(prepareYandexMap());

                // TODO Delete test code
                for (MonitoringEntityGoogleMarker googleMarker : markers) {
                    LatLng latLng = googleMarker.getMarker().getPosition();
                    double[] geoCoordinates = new double[] {latLng.latitude, latLng.longitude};
                    double[] mercCoordinates = YandexMapUtils.mercatorToGeo(geoCoordinates);
                    LatLng mercLatLng = new LatLng(mercCoordinates[0], mercCoordinates[1]);
                    Log.d("debug", "lat = " + mercLatLng.latitude + " | long = " + mercLatLng.longitude);
                    Marker marker = map.addMarker(new MarkerOptions().position(mercLatLng));
                }

                break;
            case GOOGLE:
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case SATELLITE:
                map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            default:
                setMapType(MapType.KGK);
        }
    }

    @Override
    public void clearMap() {
        if (map != null) map.clear();
    }

    @Override
    public void clearMarkers() {

        if (markers == null) return;

        for (MonitoringEntityGoogleMarker marker : markers) {
            marker.getMarker().remove();
        }

        markers.clear();
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

    private TileOverlayOptions prepareKgkMap() {
        TileProvider tileProvider = new UrlTileProvider(256, 256) {
            @Override
            public URL getTileUrl(int x, int y, int zoom) {

                String urlString = String.format(
                        Locale.ROOT,
                        "http://map2.kgk-global.com/tiles/tile.py/get?z=%d&x=%d&y=%d",
                        zoom, x, y);

                if (!checkTileExists(x, y, zoom)) {
                    return null;
                }

                try {
                    return new URL(urlString);
                } catch (MalformedURLException e) {
                    throw new AssertionError(e);
                }
            }
        };

        TileOverlayOptions tileOverlayOptions = new TileOverlayOptions().tileProvider(tileProvider);
        tileOverlayOptions.fadeIn(false);
        return tileOverlayOptions;
    }

    private TileOverlayOptions prepareYandexMap() {
        TileProvider tileProvider = new UrlTileProvider(256, 256) {
            @Override
            public URL getTileUrl(int x, int y, int zoom) {

                String urlString = String.format(
                        Locale.ROOT,
                        "http://vec02.maps.yandex.net/tiles?l=map&v=4.4.9&x=%d&y=%d&z=%d&lang=ru-RU",
                        x, y, zoom);

                if (!checkTileExists(x, y, zoom)) {
                    return null;
                }

                try {
                    return new URL(urlString);
                } catch (MalformedURLException e) {
                    throw new AssertionError(e);
                }
            }
        };

        TileOverlayOptions tileOverlayOptions = new TileOverlayOptions().tileProvider(tileProvider);
        tileOverlayOptions.fadeIn(false);
        return tileOverlayOptions;
    }

    private boolean checkTileExists(int x, int y, int zoom) {
        int minZoom = 1;
        int maxZoom = 18;
        return !(zoom < minZoom || zoom > maxZoom);
    }

    ////

    public class MonitoringEntityGoogleMarker {

        private final long id;

        private Marker marker;

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

        public void setMarker(Marker marker) {
            this.marker = marker;
        }
    }
}





























