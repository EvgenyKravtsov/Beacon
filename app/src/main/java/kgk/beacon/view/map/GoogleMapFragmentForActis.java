package kgk.beacon.view.map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Date;

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
import kgk.beacon.model.Signal;
import kgk.beacon.util.DateFormatter;
import kgk.beacon.util.ImageProcessor;
import kgk.beacon.view.actis.event.FullscreenEvent;

public class GoogleMapFragmentForActis extends Fragment implements Map,
                                                                   OnMapReadyCallback {

    private static final String TAG = GoogleMapFragmentForActis.class.getSimpleName();

    private GoogleMap googleMap;
    private Marker signalInfoMarker;
    private ImageButton fullscreenButton;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_google_map, container, false);
        MapView googleMapView = (MapView) view.findViewById(R.id.googleMapFragment_mapView);
        googleMapView.onCreate(savedInstanceState);
        googleMapView.onResume();
        googleMapView.getMapAsync(this);

        Button changeMapButton = (Button) view.findViewById(R.id.fragmentGoogleMap_changeMapButton);
        changeMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapManager.setCameraCoordinates(getCameraCoordinates());
                MapManager.setCameraZoom(getCameraZoom());
                showSelectMapDialog();
            }
        });

        fullscreenButton = (ImageButton) view.findViewById(R.id.fragmentGoogleMap_fullscreenButton);
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
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(this.getActivity());
        this.googleMap = googleMap;
        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        switch (MapManager.PREFFERED_MAP) {
            case MapManager.SATELLITE_MAP:
                this.googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case MapManager.HYBRID_MAP:
                this.googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
        }
        EventBus.getDefault().post(new MapReadyForUseEvent(this));
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
        CameraPosition cameraPosition = googleMap.getCameraPosition();
        return cameraPosition.target;
    }

    private int getCameraZoom() {
        CameraPosition cameraPosition = googleMap.getCameraPosition();
        return (int) cameraPosition.zoom;
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

    ////

    @Override
    public void setOnMapClickListener(final MapClickListener listener) {
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                listener.onMapClick(latLng.latitude, latLng.longitude);
            }
        });
    }

    @Override
    public void setOnMarkerClickListener(final MarkerClickListener listener) {
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                listener.onMarkerClick(marker.getPosition().latitude,
                        marker.getPosition().longitude);
                return true;
            }
        });
    }

    @Override
    public void addCustomMarkerPoint(int number, double latitude, double longitude) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.actis_path_marker, null);
        TextView markerNumber = (TextView) layout.findViewById(R.id.actisPathMarker_number);
        markerNumber.setText(Integer.toString(number));

        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());

        Bitmap markerBitmap = ImageProcessor.bitmapFromView(layout, width, height);

        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .icon(BitmapDescriptorFactory.fromBitmap(markerBitmap))
                .anchor(0.5f, 0.5f));

//        LatLng coordinates = new LatLng(latitude, longitude);;
//
//        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View layout = inflater.inflate(R.layout.map_custom_marker_point, null);
//
//        ImageView arrow = (ImageView) layout.findViewById(R.id.mapCustomMarkerPoint_arrow);
//
//        if (android.os.Build.VERSION.SDK_INT >= 11) {
//            arrow.setRotation((float) direction);
//        }
//
//        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, getResources().getDisplayMetrics());
//        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, getResources().getDisplayMetrics());
//
//        Bitmap markerBitmap = ImageProcessor.bitmapFromView(layout, width, height);
//
//        googleMap.addMarker(new MarkerOptions()
//                .position(new LatLng(latitude, longitude))
//                .icon(BitmapDescriptorFactory.fromBitmap(markerBitmap))
//                .anchor(0.5f, 0.5f));
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

        signalInfoMarker = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(signal.getLatitude(), signal.getLongitude()))
                .icon(BitmapDescriptorFactory.fromBitmap(markerBitmap))
                .anchor(0.5f, 1));
    }

    @Override
    public void removeSignalInfoMarker() {
        if (signalInfoMarker != null) {
            signalInfoMarker.remove();
        }
    }

    @Override
    public void addStopMarker(double latitude, double longitude) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.map_stop_marker, null);

        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics());
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics());

        Bitmap markerBitmap = ImageProcessor.bitmapFromView(layout, width, height);

        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .icon(BitmapDescriptorFactory.fromBitmap(markerBitmap)));
    }

    @Override
    public void addPolyline(ArrayList<LatLng> coordinates) {
        PolylineOptions path = new PolylineOptions();
        path.addAll(coordinates);
        path.width(3);
        path.color(Color.BLUE);
        path.geodesic(false);
        googleMap.addPolyline(path);
    }

    @Override
    public void moveCamera(double latitude, double longitude, int zoom) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), zoom));
    }

    @Override
    public void addCircleZone(double latitude, double longitude, int radius) {
        googleMap.addCircle(new CircleOptions()
                .center(new LatLng(latitude, longitude))
                .radius(radius)
                .strokeColor(getResources().getColor(R.color.main_brand_blue))
                .strokeWidth(2.0f)
                .fillColor(getResources().getColor(R.color.main_barnd_blue_transparent)));
    }

    @Override
    public void clear() {
        googleMap.clear();
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
