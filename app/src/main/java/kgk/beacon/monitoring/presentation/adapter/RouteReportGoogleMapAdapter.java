package kgk.beacon.monitoring.presentation.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import kgk.beacon.R;
import kgk.beacon.monitoring.DependencyInjection;
import kgk.beacon.monitoring.data.Configuration;
import kgk.beacon.monitoring.domain.model.routereport.MovingEvent;
import kgk.beacon.monitoring.domain.model.routereport.MovingEventSignal;
import kgk.beacon.monitoring.domain.model.routereport.ParkingEvent;
import kgk.beacon.monitoring.domain.model.routereport.RouteReportEvent;
import kgk.beacon.monitoring.presentation.model.MapType;
import kgk.beacon.monitoring.presentation.model.MovingEventMapObject;
import kgk.beacon.monitoring.presentation.model.ParkingEventMapObject;
import kgk.beacon.monitoring.presentation.model.RouteReportMapObject;
import kgk.beacon.monitoring.presentation.view.RouteReportView;
import kgk.beacon.util.AppController;
import kgk.beacon.util.ImageProcessor;

public class RouteReportGoogleMapAdapter implements
        RouteReportMapAdapter,
        OnMapReadyCallback,
        GoogleMap.OnCameraChangeListener {

    private static final int MARKER_SIZE = 13;

    private RouteReportView view;
    private MapView mapView;
    private GoogleMap map;
    private TileOverlay kgkTileOverlay;
    private TileOverlay yandexTileOverlay;
    private Configuration configuration;
    private Marker centeredEventMarker;
    private ParkingEventMapObject activeParkingEventMapObject;
    private Map<Long, List<RouteReportMapObject>> mapObjectsByDay;

    ////

    public RouteReportGoogleMapAdapter(RouteReportView view, MapView mapView) {
        configuration = DependencyInjection.provideConfiguration();
        mapObjectsByDay = new HashMap<>();

        this.view = view;
        this.mapView = mapView;
        this.mapView.getMapAsync(this);
    }

    ////

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        setMapType(configuration.loadDefaultMapType());
        map.setOnCameraChangeListener(this);
        view.mapReadyForUse();
    }

    ////

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
    public void centerOnParkingEvent(ParkingEvent event) {
        LatLng coordinates = new LatLng(event.getLatitude(), event.getLongitude());
        CameraUpdate center = CameraUpdateFactory.newLatLng(coordinates);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(configuration.loadZoomLevel());
        map.moveCamera(zoom);
        map.animateCamera(center);

        if (centeredEventMarker != null) centeredEventMarker.remove();
        if (activeParkingEventMapObject != null) activeParkingEventMapObject.stopFadeOut();

        for (Map.Entry<Long, List<RouteReportMapObject>> entry : mapObjectsByDay.entrySet()) {
            for (RouteReportMapObject mapObject : entry.getValue()) {
                if (mapObject.getEvent() == event) {
                    activeParkingEventMapObject = (ParkingEventMapObject) mapObject;
                    ((ParkingEventMapObject) mapObject).startFadeOut();
                }
            }
        }
    }

    @Override
    public void centerOnMovingEvent(MovingEvent event) {
        LatLng coordinates = new LatLng(event.getLatitude(), event.getLongitude());
        CameraUpdate center = CameraUpdateFactory.newLatLng(coordinates);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(configuration.loadZoomLevel());
        map.moveCamera(zoom);
        map.animateCamera(center);

        if (centeredEventMarker != null) centeredEventMarker.remove();
        if (activeParkingEventMapObject != null) activeParkingEventMapObject.stopFadeOut();

        centeredEventMarker = map.addMarker(generateMovingEventMarker(
                event.getLatitude(),
                event.getLongitude(),
                event.getSignals().get(0).getDirection()
        ));
    }

    @Override
    public void centerOnMovingEventSignal(MovingEventSignal signal) {
        LatLng coordinates = new LatLng(signal.getLatitude(), signal.getLongitude());
        CameraUpdate center = CameraUpdateFactory.newLatLng(coordinates);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(configuration.loadZoomLevel());
        map.moveCamera(zoom);
        map.animateCamera(center);

        if (centeredEventMarker != null) centeredEventMarker.remove();
        if (activeParkingEventMapObject != null) activeParkingEventMapObject.stopFadeOut();

        centeredEventMarker = map.addMarker(generateMovingEventMarker(
                signal.getLatitude(),
                signal.getLongitude(),
                signal.getDirection()
        ));
    }

    @Override
    public void showRouteReportDay(long date, List<RouteReportEvent> events) {

        List<RouteReportMapObject> mapObjects = new ArrayList<>();
        for (RouteReportEvent event : events) {

            if (event instanceof ParkingEvent) {
                RouteReportMapObject mapObject = new ParkingEventMapObject(map, event);
                mapObject.draw();
                mapObjects.add(mapObject);
            }

            if (event instanceof MovingEvent) {
                RouteReportMapObject mapObject = new MovingEventMapObject(map, event);
                mapObject.draw();
                mapObjects.add(mapObject);
            }
        }

        mapObjectsByDay.put(date, mapObjects);
    }

    @Override
    public void clearRouteReportDay(long date) {
        List<RouteReportMapObject> mapObjects = mapObjectsByDay.get(date);
        for (RouteReportMapObject mapObject : mapObjects) mapObject.clear();
        mapObjectsByDay.remove(date);
    }

    ////

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        configuration.saveZoomLevel(cameraPosition.zoom);
    }

    ////

    private void setMapType(MapType mapType) {
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

    private BitmapDescriptor generateMovingEventMarkerIcon(int direction) {
        Context context = AppController.getInstance();
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater
                .inflate(R.layout.map_custom_marker_point, null);

        ImageView arrow = (ImageView) layout
                .findViewById(R.id.mapCustomMarkerPoint_arrow);
        if (android.os.Build.VERSION.SDK_INT >= 11)
            arrow.setRotation((float) direction);

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

    private MarkerOptions generateMovingEventMarker(
            double latitude,
            double longitude,
            int direction) {

        return new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .icon(generateMovingEventMarkerIcon(direction))
                .anchor(0.5f, 0.5f);
    }
}
