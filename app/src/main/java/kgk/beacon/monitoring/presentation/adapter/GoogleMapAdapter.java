package kgk.beacon.monitoring.presentation.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import kgk.beacon.R;
import kgk.beacon.monitoring.DependencyInjection;
import kgk.beacon.monitoring.data.Configuration;
import kgk.beacon.monitoring.domain.model.MonitoringEntity;
import kgk.beacon.monitoring.presentation.model.MapType;
import kgk.beacon.util.AppController;
import kgk.beacon.util.ImageProcessor;

public class GoogleMapAdapter implements
        OnMapReadyCallback,
        MapAdapter,
        GoogleMap.OnCameraChangeListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapClickListener {

    private static final int MARKER_SIZE = 18;

    private final kgk.beacon.monitoring.presentation.view.MapView mapView;
    private final MapView googleMapView;

    private Configuration configuration;
    private GoogleMap map;
    private TileOverlay kgkTileOverlay;
    private TileOverlay yandexTileOverlay;
    private TileOverlay osmTileOverlay;
    private boolean trafficEnabled;
    private List<MonitoringEntityGoogleMarker> markers;
    private boolean markersExtended;

    ////

    public GoogleMapAdapter(
            kgk.beacon.monitoring.presentation.view.MapView mapView,
            MapView googleMapView) {

        this.mapView = mapView;
        this.googleMapView = googleMapView;
        this.configuration = DependencyInjection.provideConfiguration();
        this.markers = new ArrayList<>();
        this.googleMapView.getMapAsync(this);

        markersExtended = configuration.loadMarkerInformationEnabled();
    }

    ////

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnCameraChangeListener(this);
        map.setOnMarkerClickListener(this);
        map.setOnMapClickListener(this);
        mapView.mapReadyForUse();
    }

    ////

    @Override
    public void showMapEntity(MonitoringEntity monitoringEntity) {
        long id = monitoringEntity.getId();

        MarkerOptions markerOptions;

        if (markersExtended) markerOptions = generateCustomMarkerExtended(monitoringEntity);
        else markerOptions = generateCustomMarker(monitoringEntity);

        if (markers != null) {
            boolean redrawn = false;
            for (MonitoringEntityGoogleMarker marker : markers) {
                if (marker.getId() == id) {
                    marker.getMarker().remove();
                    marker.setMarker(map.addMarker(markerOptions));

                    redrawn = true;
                }
            }

            if (!redrawn)
            markers.add(new MonitoringEntityGoogleMarker(
                    id,
                    map.addMarker(markerOptions)));
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
        map.moveCamera(CameraUpdateFactory
                .newLatLngZoom(new LatLng(latitude, longitude),
                        configuration.loadZoomLevel()));
    }

    @Override
    public void centerOnCoordinatesAnimated(double latitude, double longitude) {
        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(configuration.loadZoomLevel());
        map.moveCamera(zoom);
        map.animateCamera(center);
        mapView.toggleCenterOnActiveControl(false);
    }

    @Override
    public void centerCameraBearing() {
        CameraPosition cameraPosition = map.getCameraPosition();
        map.animateCamera(CameraUpdateFactory
                .newCameraPosition(new CameraPosition.Builder()
                        .target(cameraPosition.target)
                        .zoom(cameraPosition.zoom)
                        .bearing(0.0f)
                        .build()));
        mapView.toggleCompassButton(false);
    }

    ////

    @Override
    public void setMapType(MapType mapType) {
        if (map == null) return;
        if (kgkTileOverlay != null) kgkTileOverlay.remove();
        if (osmTileOverlay != null) osmTileOverlay.remove();

        switch (mapType) {
            case KGK:
                map.setMapType(GoogleMap.MAP_TYPE_NONE);
                kgkTileOverlay = map.addTileOverlay(prepareKgkMap());
                break;
            case OSM:
                map.setMapType(GoogleMap.MAP_TYPE_NONE);
                osmTileOverlay = map.addTileOverlay(prepapreOsmMap());
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
        LatLng center = cameraPosition.target;
        LatLng monitoringEntityPosition = new LatLng(
                mapView.getActiveMonitoringEntity().getLatitude(),
                mapView.getActiveMonitoringEntity().getLongitude()
        );

        double centerLatitude = roundCoordinate(center.latitude, 5);
        double centerLongitude = roundCoordinate(center.longitude, 5);
        double pointLatitude = roundCoordinate(monitoringEntityPosition.latitude, 5);
        double pointLongitude = roundCoordinate(monitoringEntityPosition.longitude, 5);

        if (centerLatitude == pointLatitude && centerLongitude == pointLongitude)
            mapView.toggleCenterOnActiveControl(false);
        else mapView.toggleCenterOnActiveControl(true);

        if (cameraPosition.bearing != 0) mapView.toggleCompassButton(true);

        configuration.saveZoomLevel(cameraPosition.zoom);
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

    @Override
    public void onMapClick(LatLng latLng) {
        mapView.onMapClick();
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

    private TileOverlayOptions prepapreOsmMap() {
        TileProvider tileProvider = new UrlTileProvider(256, 256) {
            @Override
            public URL getTileUrl(int x, int y, int zoom) {

                String urlString = String.format(
                        Locale.ROOT,
                        "http://a.tile.openstreetmap.org/%s/%s/%s.png",
                        zoom, x, y);

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

    private BitmapDescriptor generateMarkerIcon(MonitoringEntity monitoringEntity) {
        Context context = AppController.getInstance();
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout;
        ImageView arrow;

        switch (monitoringEntity.getStatus()) {
            case IN_MOTION:
                layout = inflater.inflate(R.layout.map_custom_marker_point, null);

                arrow = (ImageView) layout.findViewById(R.id.mapCustomMarkerPoint_arrow);
                if (android.os.Build.VERSION.SDK_INT >= 11)
                    arrow.setRotation((float) monitoringEntity.getDirection());

                break;
            case STOPPED:
                layout = inflater.inflate(R.layout.map_custom_marker_point, null);

                arrow = (ImageView) layout.findViewById(R.id.mapCustomMarkerPoint_arrow);
                arrow.setVisibility(View.GONE);

                break;
            case OFFLINE:
                layout = inflater.inflate(R.layout.map_custom_marker_point_offline, null);
                break;
            default: layout = inflater.inflate(R.layout.map_custom_marker_point_offline, null);
        }

        int width = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                MARKER_SIZE,
                context.getResources().getDisplayMetrics());
        int height = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                MARKER_SIZE,
                context.getResources().getDisplayMetrics());

        Bitmap markerBitmap = ImageProcessor.bitmapFromView(layout, width, height);
        return BitmapDescriptorFactory.fromBitmap(markerBitmap);
    }

    private BitmapDescriptor generateMarkerExtendedIcon(MonitoringEntity monitoringEntity) {
        Context context = AppController.getInstance();
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout;
        ImageView arrow;
        TextView informationTextView;

        switch (monitoringEntity.getStatus()) {
            case IN_MOTION:
                layout = inflater.inflate(R.layout.monitoring_activity_map_marker_extended, null);

                arrow = (ImageView) layout
                        .findViewById(R.id.monitoring_activity_map_marker_extended_arrow_image_view);
                if (android.os.Build.VERSION.SDK_INT >= 11)
                    arrow.setRotation((float) monitoringEntity.getDirection());

                informationTextView = (TextView) layout
                        .findViewById(R.id.monitoring_activity_map_marker_extended_information_text_view);
                informationTextView.setText(String.format(
                        "%s %s",
                        monitoringEntity.getModel(),
                        monitoringEntity.getStateNumber()));

                break;
            case STOPPED:
                layout = inflater.inflate(R.layout.monitoring_activity_map_marker_extended, null);

                arrow = (ImageView) layout
                        .findViewById(R.id.monitoring_activity_map_marker_extended_arrow_image_view);
                arrow.setVisibility(View.GONE);

                informationTextView = (TextView) layout
                        .findViewById(R.id.monitoring_activity_map_marker_extended_information_text_view);
                informationTextView.setText(String.format(
                        "%s %s",
                        monitoringEntity.getModel(),
                        monitoringEntity.getStateNumber()));

                break;
            case OFFLINE:
                layout = inflater.inflate(R.layout.monitoring_activity_map_marker_offline_extended, null);

                informationTextView = (TextView) layout
                        .findViewById(R.id.monitoring_activity_map_marker_offline_extended_information_text_view);
                informationTextView.setText(String.format(
                        "%s %s",
                        monitoringEntity.getModel(),
                        monitoringEntity.getStateNumber()));

                break;
            default: layout = inflater.inflate(R.layout.map_custom_marker_point_offline, null);
        }

        int width = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                500,
                context.getResources().getDisplayMetrics());
        int height = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                MARKER_SIZE,
                context.getResources().getDisplayMetrics());

        Bitmap markerBitmap = ImageProcessor.bitmapFromView(layout, width, height);
        return BitmapDescriptorFactory.fromBitmap(markerBitmap);
    }

    private MarkerOptions generateCustomMarker(MonitoringEntity monitoringEntity) {
        return new MarkerOptions()
                .position(new LatLng(
                                monitoringEntity.getLatitude(),
                                monitoringEntity.getLongitude()))
                .icon(generateMarkerIcon(monitoringEntity))
                .anchor(0.5f, 0.5f);
    }

    private MarkerOptions generateCustomMarkerExtended(MonitoringEntity monitoringEntity) {
        return new MarkerOptions()
                .position(new LatLng(
                        monitoringEntity.getLatitude(),
                        monitoringEntity.getLongitude()))
                .icon(generateMarkerExtendedIcon(monitoringEntity))
                .anchor(0.05f, 0.5f);
    }

    private double roundCoordinate(double coordinate, int decimalPoints) {
        BigDecimal bigDecimal = new BigDecimal(coordinate);
        bigDecimal = bigDecimal.setScale(decimalPoints, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
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





























