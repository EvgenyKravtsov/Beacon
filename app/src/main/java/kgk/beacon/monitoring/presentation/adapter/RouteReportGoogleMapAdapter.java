package kgk.beacon.monitoring.presentation.adapter;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import kgk.beacon.monitoring.DependencyInjection;
import kgk.beacon.monitoring.data.Configuration;
import kgk.beacon.monitoring.domain.model.routereport.MovingEvent;
import kgk.beacon.monitoring.domain.model.routereport.ParkingEvent;
import kgk.beacon.monitoring.domain.model.routereport.RouteReportEvent;
import kgk.beacon.monitoring.presentation.model.MapType;
import kgk.beacon.monitoring.presentation.model.MovingEventMapObject;
import kgk.beacon.monitoring.presentation.model.ParkingEventMapObject;
import kgk.beacon.monitoring.presentation.model.RouteReportMapObject;
import kgk.beacon.monitoring.presentation.view.RouteReportView;

public class RouteReportGoogleMapAdapter
        implements RouteReportMapAdapter, OnMapReadyCallback {

    private RouteReportView view;
    private MapView mapView;
    private GoogleMap map;
    private TileOverlay kgkTileOverlay;
    private TileOverlay yandexTileOverlay;
    private Configuration configuration;
    private Marker centeredEventMarker;
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
    public void centerMap(LatLng coordinates) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                coordinates,
                configuration.loadZoomLevel()));

        if (centeredEventMarker != null) centeredEventMarker.remove();
        centeredEventMarker = map.addMarker(new MarkerOptions()
                .position(coordinates)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
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
}
