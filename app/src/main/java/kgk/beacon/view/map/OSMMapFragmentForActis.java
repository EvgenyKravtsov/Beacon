package kgk.beacon.view.map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.overlays.MapEventsOverlay;
import org.osmdroid.bonuspack.overlays.MapEventsReceiver;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import kgk.beacon.R;
import kgk.beacon.map.Map;
import kgk.beacon.map.MapClickListener;
import kgk.beacon.map.MapManager;
import kgk.beacon.map.MarkerClickListener;
import kgk.beacon.map.event.MapChangeEvent;
import kgk.beacon.map.event.MapReadyForUseEvent;
import kgk.beacon.map.osm.PointMarkerOverlayItem;
import kgk.beacon.map.osm.XYTileSourceKGK;
import kgk.beacon.model.Signal;
import kgk.beacon.util.DateFormatter;
import kgk.beacon.util.ImageProcessor;
import kgk.beacon.view.actis.event.FullscreenEvent;

public class OSMMapFragmentForActis extends Fragment implements Map {

    private static final String TAG = OSMMapFragmentForActis.class.getSimpleName();

    private MapView map;
    private ItemizedIconOverlay<OverlayItem> signalInfoMarker;
    private MapClickListener mapClickListener;
    private MarkerClickListener markerClickListener;
    private ImageButton fullscreenButton;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_osm_map, container, false);
        map = (MapView) view.findViewById(R.id.osMapFragment_mapView);

        ITileSource tileSource;
        switch (MapManager.PREFFERED_MAP) {
            case MapManager.OSM_KGK_MAP:
                tileSource = new XYTileSourceKGK("KGKMap",
                        3, 18, 256, ".png", new String[]{"http://map2.kgk-global.com/tiles/tile.py/"});
                break;
            case MapManager.OSM_MAP:
                tileSource = TileSourceFactory.MAPNIK;
                break;
            default:
                tileSource = new XYTileSourceKGK("KGKMap",
                        3, 18, 256, ".png", new String[]{"http://map2.kgk-global.com/tiles/tile.py/"});
        }

        map.setTileSource(tileSource);
        map.setMultiTouchControls(true);
        map.setBuiltInZoomControls(true);

        tuneOsmMapForAndroidM();

        Button changeMapButton = (Button) view.findViewById(R.id.fragmentOsmMap_changeMapButton);
        changeMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapManager.setCameraCoordinates(getCameraCoordinates());
                MapManager.setCameraZoom(getCameraZoom());
                showSelectMapDialog();
            }
        });

        fullscreenButton = (ImageButton) view.findViewById(R.id.fragmentOsmMap_fullscreenButton);
        fullscreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageButton button =  (ImageButton) view;
                FullscreenEvent event = new FullscreenEvent();
                event.setFullscreenButton(button);
                EventBus.getDefault().post(event);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().post(new MapReadyForUseEvent(this));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mapClickListener = null;
        markerClickListener = null;
    }

    ////

    @SuppressLint("SetTextI18n")
    private void updateMarkerContent(View layout, Signal signal) {
        SignalInfoMarkerViewHolder viewHolder = new SignalInfoMarkerViewHolder(layout);
        viewHolder.signalDate.setText(DateFormatter.formatDate(new Date(signal.getDate() * 1000)));
        viewHolder.signalTime.setText(DateFormatter.formatTime(new Date(signal.getDate() * 1000)));
        viewHolder.satellitesCountTextView.setText(String.valueOf(signal.getSatellites()) + getString(R.string.list_item_satellites_sign));
        viewHolder.speedCountTextView.setText(String.valueOf(signal.getSpeed()) + getString(R.string.list_item_speed_sign));
        viewHolder.chargeCountTextView.setText(String.valueOf(signal.getCharge()) + "%");
        viewHolder.temperatureCountTextView.setText(String.valueOf(signal.getTemperature()) + getString(R.string.list_item_temperature_sign));
    }

    private LatLng getCameraCoordinates() {
        IGeoPoint cameraPosition = map.getMapCenter();
        return new LatLng(cameraPosition.getLatitude(), cameraPosition.getLongitude());
    }

    private int getCameraZoom() {
        return map.getZoomLevel();
    }

    private void showSelectMapDialog() {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final RadioGroup dialogView = (RadioGroup) inflater.inflate(R.layout.dialog_select_map, null);

        final String preferredMapName = new MapManager().loadPreferredMapName();
        switch (preferredMapName) {
            case MapManager.OSM_KGK_MAP:
                RadioButton kgkMapRadioButton = (RadioButton) dialogView.findViewById(R.id.dialogSelectMap_kgkMap);
                kgkMapRadioButton.setChecked(true);
                break;
            case MapManager.OSM_MAP:
                RadioButton osmMapRadioButton = (RadioButton) dialogView.findViewById(R.id.dialogSelectMap_osmMap);
                osmMapRadioButton.setChecked(true);
                break;
            case MapManager.SATELLITE_MAP:
                RadioButton satelliteMapRadioButton = (RadioButton) dialogView.findViewById(R.id.dialogSelectMap_satelliteMap);
                satelliteMapRadioButton.setChecked(true);
                break;
            case MapManager.HYBRID_MAP:
                RadioButton hybridMapRadioButton = (RadioButton) dialogView.findViewById(R.id.dialogSelectMap_hybridMap);
                hybridMapRadioButton.setChecked(true);
                break;
            case MapManager.GOOGLE_MAP:
                RadioButton googleMapRadioButton = (RadioButton) dialogView.findViewById(R.id.dialogSelectMap_googleMap);
                googleMapRadioButton.setChecked(true);
                break;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.select_map_dialog_title))
                .setView(dialogView)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int mapId = dialogView.getCheckedRadioButtonId();
                        String mapName = "";
                        switch (mapId) {
                            case R.id.dialogSelectMap_kgkMap:
                                mapName = MapManager.OSM_KGK_MAP;
                                break;
                            case R.id.dialogSelectMap_osmMap:
                                mapName = MapManager.OSM_MAP;
                                break;
                            case R.id.dialogSelectMap_satelliteMap:
                                mapName = MapManager.SATELLITE_MAP;
                                break;
                            case R.id.dialogSelectMap_hybridMap:
                                mapName = MapManager.HYBRID_MAP;
                                break;
                            case R.id.dialogSelectMap_googleMap:
                                mapName = MapManager.GOOGLE_MAP;
                                break;
                        }

                        if (!mapName.equals(preferredMapName)) {
                            MapChangeEvent event = new MapChangeEvent();
                            event.setPreferredMap(mapName);
                            EventBus.getDefault().post(event);
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void tuneOsmMapForAndroidM() {
        int currentApiVersion = Build.VERSION.SDK_INT;
        if (currentApiVersion > Build.VERSION_CODES.LOLLIPOP) {
            String cachePath = getActivity().getCacheDir().getAbsolutePath();
            OpenStreetMapTileProviderConstants.setCachePath(cachePath);
        }
    }

    ////

    @Override
    public void setOnMapClickListener(final MapClickListener listener) {
        mapClickListener = listener;
        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(getActivity(),
                new MapEventsReceiver() {
                    @Override
                    public boolean singleTapConfirmedHelper(GeoPoint p) {
                        listener.onMapClick(p.getLatitude(), p.getLongitude());
                        return true;
                    }

                    @Override
                    public boolean longPressHelper(GeoPoint p) {
                        listener.onMapClick(p.getLatitude(), p.getLongitude());
                        return true;
                    }
                });
        map.getOverlays().add(0, mapEventsOverlay);
    }

    @Override
    public void setOnMarkerClickListener(final MarkerClickListener listener) {
        this.markerClickListener = listener;
    }

    @Override
    public void addCustomMarkerPoint(int direction, double latitude, double longitude) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.map_custom_marker_point, null);
        ImageView arrow = (ImageView) layout.findViewById(R.id.mapCustomMarkerPoint_arrow);
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            arrow.setRotation((float) direction);
        }
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, getResources().getDisplayMetrics());
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, getResources().getDisplayMetrics());
        Bitmap markerBitmap = ImageProcessor.bitmapFromView(layout, width, height);

        OverlayItem point = new PointMarkerOverlayItem(getResources(), markerBitmap, latitude, longitude);
        point.setMarkerHotspot(OverlayItem.HotspotPlace.CENTER);
        ArrayList<OverlayItem> overlayItemArray = new ArrayList<>();
        overlayItemArray.add(point);
        ItemizedIconOverlay<OverlayItem> currentPositionMarker = new ItemizedIconOverlay<>(getActivity(),
                overlayItemArray,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {

                    @Override public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        if (markerClickListener != null) {
                            markerClickListener.onMarkerClick(item.getPoint().getLatitude(),
                                    item.getPoint().getLongitude());
                        }
                        return true;
                    }

                    @Override public boolean onItemLongPress(final int index, final OverlayItem item) {
                        return false;
                    }
                });
        map.getOverlays().add(currentPositionMarker);
        map.invalidate();
    }

    @Override
    public void addSignalInfoMarker(Signal signal) {
        removeSignalInfoMarker();

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.map_custom_marker, null);
        updateMarkerContent(layout, signal);

        DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int markerWidth = dpWidth >= 600 ? 210 : 170;
        int markerHeight = dpWidth >= 600 ? 200 : 160;

        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, markerWidth, getResources().getDisplayMetrics());
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, markerHeight, getResources().getDisplayMetrics());
        Bitmap markerBitmap = ImageProcessor.bitmapFromView(layout, width, height);

        OverlayItem point = new PointMarkerOverlayItem(getResources(), markerBitmap, signal.getLatitude(), signal.getLongitude());
        point.setMarkerHotspot(OverlayItem.HotspotPlace.BOTTOM_CENTER);
        ArrayList<OverlayItem> overlayItemArray = new ArrayList<>();
        overlayItemArray.add(point);
        ItemizedIconOverlay<OverlayItem> signalInfoMarker = new ItemizedIconOverlay<>(getActivity(), overlayItemArray, null);
        this.signalInfoMarker = signalInfoMarker;
        map.getOverlays().add(signalInfoMarker);
        map.invalidate();
    }

    @Override
    public void removeSignalInfoMarker() {
        if (signalInfoMarker != null) {
            map.getOverlays().remove(signalInfoMarker);
        }
        map.invalidate();
    }

    @Override
    public void addStopMarker(double latitude, double longitude) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.map_stop_marker, null);

        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics());
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics());

        Bitmap markerBitmap = ImageProcessor.bitmapFromView(layout, width, height);

        OverlayItem point = new PointMarkerOverlayItem(getResources(), markerBitmap, latitude, longitude);
        point.setMarkerHotspot(OverlayItem.HotspotPlace.CENTER);
        ArrayList<OverlayItem> overlayItemArray = new ArrayList<>();
        overlayItemArray.add(point);
        ItemizedIconOverlay<OverlayItem> currentPositionMarker = new ItemizedIconOverlay<>(getActivity(), overlayItemArray, null);
        map.getOverlays().add(currentPositionMarker);
    }

    @Override
    public void addPolyline(ArrayList<LatLng> coordinates) {
        Polyline polyline = new Polyline(getActivity());
        polyline.setWidth(3);
        polyline.setColor(Color.BLUE);
        polyline.setGeodesic(false);

        List<GeoPoint> points = new ArrayList<>();
        for (LatLng coordinate : coordinates) {
            points.add(new GeoPoint(coordinate.latitude, coordinate.longitude));
        }
        polyline.setPoints(points);

        map.getOverlays().add(polyline);
        map.invalidate();
    }

    @Override
    public void moveCamera(double latitude, double longitude, int zoom) {
        IMapController mapController = map.getController();
        mapController.setZoom(zoom);
        GeoPoint geoPoint = new GeoPoint(latitude, longitude);
        mapController.setCenter(geoPoint);
    }

    @Override
    public void clear() {
        map.getOverlays().clear();
        if (mapClickListener != null) {
            setOnMapClickListener(mapClickListener);
        }
        map.invalidate();
    }

    @Override
    public void turnOnFullscreenButton() {
        fullscreenButton.setVisibility(View.VISIBLE);
    }

    ////

    static class SignalInfoMarkerViewHolder {

        @Bind(R.id.popupWindowPath_signalDate) TextView signalDate;
        @Bind(R.id.popupWindowPath_signalTime) TextView signalTime;
        @Bind(R.id.popupWindowPath_satellitesCountTextView) TextView satellitesCountTextView;
        @Bind(R.id.popupWindowPath_speedCountTextView) TextView speedCountTextView;
        @Bind(R.id.popupWindowPath_chargeCountTextView) TextView chargeCountTextView;
        @Bind(R.id.popupWindowPath_temperatureCountTextView) TextView temperatureCountTextView;

        public SignalInfoMarkerViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
