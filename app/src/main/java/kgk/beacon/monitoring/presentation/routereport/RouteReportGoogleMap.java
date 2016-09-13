package kgk.beacon.monitoring.presentation.routereport;

import android.annotation.SuppressLint;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import kgk.beacon.R;
import kgk.beacon.monitoring.domain.model.routereport.MovingEvent;
import kgk.beacon.monitoring.domain.model.routereport.MovingEventSignal;
import kgk.beacon.monitoring.domain.model.routereport.ParkingEvent;
import kgk.beacon.monitoring.domain.model.routereport.RouteReportEvent;
import kgk.beacon.monitoring.presentation.model.MapType;
import kgk.beacon.monitoring.presentation.model.MovingEventMapObject;
import kgk.beacon.monitoring.presentation.model.ParkingEventMapObject;
import kgk.beacon.monitoring.presentation.model.RouteReportMapObject;
import kgk.beacon.util.AppController;
import kgk.beacon.util.ImageProcessor;

import static kgk.beacon.monitoring.presentation.routereport.RouteReportContract.Map;
import static kgk.beacon.monitoring.presentation.routereport.RouteReportContract.Presenter;

public class RouteReportGoogleMap implements Map, OnMapReadyCallback {

    private static final int MARKER_SIZE = 13;

    private Presenter presenter;
    private MapView googleMapView;
    private GoogleMap googleMap;
    private MapType mapType;
    private float zoomLevel;
    private TileOverlay kgkTileOverlay;
    private TileOverlay yandexTileOverlay;
    private List<RouteReportMapObject> mapEvents;
    private Marker centeredEventMarker;
    private ParkingEventMapObject activeParkingEventMapObject;

    private boolean justLaunched = true;

    ////

    public RouteReportGoogleMap(MapView googleMapView) {
        this.googleMapView = googleMapView;
    }

    ////

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void init(MapType mapType, float zoomLevel) {
        this.mapType = mapType;
        this.zoomLevel = zoomLevel;
        googleMapView.getMapAsync(this);
    }

    @Override
    public void displayRouteReportEvents(List<RouteReportEvent> events) {
        if (mapEvents == null) mapEvents = new ArrayList<>();
        else mapEvents.clear();

        googleMap.clear();

        for (RouteReportEvent event : events) {
            if (event instanceof ParkingEvent) {
                RouteReportMapObject mapObject = new ParkingEventMapObject(googleMap, event);
                mapEvents.add(mapObject);
                mapObject.draw();
            }

            if (event instanceof MovingEvent) {
                RouteReportMapObject mapObject = new MovingEventMapObject(googleMap, event);
                mapEvents.add(mapObject);
                mapObject.draw();
            }
        }
    }

    @Override
    public void centerOnParkingEvent(ParkingEvent event) {
        LatLng latLng = new LatLng(event.getLatitude(), event.getLongitude());
        CameraUpdate coordinates = CameraUpdateFactory.newLatLng(latLng);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(zoomLevel);
        moveCamera(coordinates, zoom);

        if (centeredEventMarker != null) centeredEventMarker.remove();
        if (activeParkingEventMapObject != null) activeParkingEventMapObject.stopFadeOut();

        for (RouteReportMapObject mapObject : mapEvents)
            if (mapObject.getEvent() == event) {
                activeParkingEventMapObject = (ParkingEventMapObject) mapObject;
                ((ParkingEventMapObject) mapObject).startFadeOut();
            }
    }

    @Override
    public void centerOnMovingEvent(MovingEvent event) {
        LatLng latLng = new LatLng(event.getLatitude(), event.getLongitude());
        CameraUpdate coordinates = CameraUpdateFactory.newLatLng(latLng);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(zoomLevel);
        moveCamera(coordinates, zoom);

        if (centeredEventMarker != null) centeredEventMarker.remove();
        if (activeParkingEventMapObject != null) activeParkingEventMapObject.stopFadeOut();

        centeredEventMarker = googleMap.addMarker(generateMovingEventMarker(
                event.getLatitude(),
                event.getLongitude(),
                event.getSignals().get(0).getDirection()
        ));
    }

    @Override
    public void centerOnMovingEventSignal(MovingEventSignal event) {
        LatLng latLng = new LatLng(event.getLatitude(), event.getLongitude());
        CameraUpdate coordinates = CameraUpdateFactory.newLatLng(latLng);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(zoomLevel);
        moveCamera(coordinates, zoom);

        if (centeredEventMarker != null) centeredEventMarker.remove();
        if (activeParkingEventMapObject != null) activeParkingEventMapObject.stopFadeOut();

        centeredEventMarker = googleMap.addMarker(generateMovingEventMarker(
                event.getLatitude(),
                event.getLongitude(),
                event.getDirection()
        ));
    }

    ////

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        setMapType(mapType);
        presenter.onMapReady();
    }

    ////

    private void setMapType(MapType mapType) {
        if (googleMap == null) return;
        if (kgkTileOverlay != null) kgkTileOverlay.remove();
        if (yandexTileOverlay != null) yandexTileOverlay.remove();

        switch (mapType) {
            case KGK:
                googleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                kgkTileOverlay = googleMap.addTileOverlay(prepareKgkMapType());
                break;

            case YANDEX: // TODO Change Yandex map to OSM standard map break;
            case GOOGLE: googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);break;
            case SATELLITE: googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE); break;
            default: setMapType(MapType.GOOGLE);
        }
    }

    private TileOverlayOptions prepareKgkMapType() {
        TileProvider tileProvider = new UrlTileProvider(256, 256) {
            @Override
            public URL getTileUrl(int x, int y, int zoom) {

                String urlString = String.format(
                        Locale.ROOT,
                        "http://map2.kgk-global.com/tiles/tile.py/get?z=%d&x=%d&y=%d",
                        zoom, x, y);

                int minZoom = 1;
                int maxZoom = 18;
                if (zoom < minZoom || zoom > maxZoom) return null;

                try { return new URL(urlString); }
                catch (MalformedURLException e) { throw new AssertionError(e); }
            }
        };

        TileOverlayOptions tileOverlayOptions = new TileOverlayOptions().tileProvider(tileProvider);
        tileOverlayOptions.fadeIn(false);
        return tileOverlayOptions;
    }

    private BitmapDescriptor generateMovingEventMarkerIcon(int direction) {
        Context context = AppController.getInstance();
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View layout = inflater
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

    private void moveCamera(CameraUpdate coordinated, CameraUpdate zoom) {
        googleMap.moveCamera(zoom);
        if (justLaunched) { googleMap.moveCamera(coordinated); justLaunched = false; }
        else googleMap.animateCamera(coordinated);
    }
}
