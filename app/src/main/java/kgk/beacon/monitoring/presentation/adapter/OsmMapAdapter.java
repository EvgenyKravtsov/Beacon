package kgk.beacon.monitoring.presentation.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;

import org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.List;

import kgk.beacon.R;
import kgk.beacon.map.osm.PointMarkerOverlayItem;
import kgk.beacon.monitoring.domain.model.MonitoringEntity;
import kgk.beacon.util.ImageProcessor;

public class OsmMapAdapter implements MapAdapter {

    private final kgk.beacon.monitoring.presentation.view.MapView mapView;
    private final MapView osmMapView;
    private final Context context;

    private boolean trafficEnabled;
    private List<MonitoringEntityOsmMarker> markers;

    ////

    public OsmMapAdapter(
            kgk.beacon.monitoring.presentation.view.MapView mapView,
            MapView osmMapView,
            Context context) {
        this.mapView = mapView;
        this.osmMapView = osmMapView;
        this.context = context;

        initMap();
    }

    ////

    @Override
    public void showMapEntity(MonitoringEntity monitoringEntity) {
        long id = monitoringEntity.getId();
        ItemizedIconOverlay marker = prepareMarkerOverlay(
                monitoringEntity.getLatitude(),
                monitoringEntity.getLongitude());
        osmMapView.getOverlays().add(marker);
        osmMapView.invalidate();

        markers.add(new MonitoringEntityOsmMarker(id, marker));
    }

    @Override
    public void toggleTraffic() {

    }

    @Override
    public void zoomIn() {

    }

    @Override
    public void zoomOut() {

    }

    @Override
    public void centerOnCoordinates(double latitude, double longitude) {

    }

    @Override
    public void clearMap() {

    }

    ////

    private void initMap() {
        ITileSource tileSource = TileSourceFactory.MAPNIK;

        osmMapView.setTileSource(tileSource);
        osmMapView.setMultiTouchControls(true);

        tuneOsmForAndroidM();
    }

    private void tuneOsmForAndroidM() {
        int currentApiVersion = Build.VERSION.SDK_INT;
        if (currentApiVersion > Build.VERSION_CODES.LOLLIPOP) {
            String cachePath = context.getCacheDir().getAbsolutePath();
            OpenStreetMapTileProviderConstants.setCachePath(cachePath);
        }
    }

    private ItemizedIconOverlay prepareMarkerOverlay(double latitude, double longitude) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        @SuppressLint("InflateParams") View markerLayout =
                inflater.inflate(R.layout.map_custom_marker_point, null);

        int width = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                25,
                context.getResources().getDisplayMetrics());
        int height = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                25,
                context.getResources().getDisplayMetrics());

        Bitmap markerBitmap = ImageProcessor.bitmapFromView(markerLayout, width, height);

        OverlayItem markerOverlayItem = new PointMarkerOverlayItem(
                context.getResources(),
                markerBitmap,
                latitude,
                longitude);

        markerOverlayItem.setMarkerHotspot(OverlayItem.HotspotPlace.CENTER);

        ArrayList<OverlayItem> overlayItemArray = new ArrayList<>();
        overlayItemArray.add(markerOverlayItem);

        return new ItemizedIconOverlay<>(context, overlayItemArray, null);
    }

    ////

    public class MonitoringEntityOsmMarker {

        private final long id;
        private final ItemizedIconOverlay marker;

        ////

        public MonitoringEntityOsmMarker(long id, ItemizedIconOverlay marker) {
            this.id = id;
            this.marker = marker;
        }

        ////

        public long getId() {
            return id;
        }

        public ItemizedIconOverlay getMarker() {
            return marker;
        }
    }
}
