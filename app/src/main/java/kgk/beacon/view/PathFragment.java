package kgk.beacon.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import kgk.beacon.R;
import kgk.beacon.dispatcher.Dispatcher;
import kgk.beacon.model.Signal;
import kgk.beacon.stores.SignalStore;
import kgk.beacon.util.DateFormatter;

public class PathFragment extends SupportMapFragment implements OnMapReadyCallback,
                                                GoogleMap.OnMapClickListener,
                                                GoogleMap.OnMarkerClickListener {

    // TODO Proper deleting marker on outside click

    public static final String TAG = PathFragment.class.getSimpleName();

    private GoogleMap googleMap;
    private PolylineOptions path;
    private Marker currentMarker;
    private Paint paint;
    private List<Signal> signals;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
        paint = new Paint();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getMapAsync(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setOnMapClickListener(this);
        googleMap.setOnMarkerClickListener(this);
        UiSettings settings = googleMap.getUiSettings();
        settings.setZoomControlsEnabled(true);
        drawPath(googleMap);
    }

    @Override
    public void onMapClick(LatLng clickCoordinates) {
        for (LatLng pathCoordinates : path.getPoints()) {
            float[] results = new float[1];
            Location.distanceBetween(clickCoordinates.latitude, clickCoordinates.longitude,
                    pathCoordinates.latitude, pathCoordinates.longitude, results);

            if (results[0] < (20 * (22 - googleMap.getCameraPosition().zoom))) {
                if (currentMarker != null) {
                    currentMarker.remove();
                }

                Signal currentSignal = null;
                for (Signal signal : signals) {
                    if (signal.getLatitude() == pathCoordinates.latitude &&
                            signal.getLongitude() == pathCoordinates.longitude) {
                        currentSignal = signal;
                        Log.d(TAG, signal.getCharge() + "");
                    }
                }

                currentMarker = addCustomMarker(currentSignal);
            }
        }
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        Log.d("Marker", "clicked");

        if (currentMarker != null) {
            currentMarker.remove();
        }
        onMapClick(marker.getPosition());

        return true;
    }

    private void drawPath(GoogleMap googleMap) {
        ArrayList<LatLng> coordinates = new ArrayList<>();
        signals = SignalStore.getInstance(Dispatcher.getInstance(EventBus.getDefault())).getSignalsDisplayed();
        for (Signal signal : signals) {
            double latitude = signal.getLatitude();
            double longitude = signal.getLongitude();
            coordinates.add(new LatLng(latitude, longitude));
        }

        path = new PolylineOptions();
        path.addAll(coordinates);
        path.width(3);
        path.color(Color.RED);
        path.geodesic(false);

        googleMap.addPolyline(path);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates.get(0), 13));

        for (LatLng latLng : coordinates) {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng);
            googleMap.addMarker(markerOptions);
        }
    }

    private Marker addCustomMarker(Signal signal) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.map_custom_marker, null);
        updateMarkerContent(layout, signal);

        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 280, getResources().getDisplayMetrics());
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 185, getResources().getDisplayMetrics());

        Bitmap markerBitmap = bitmapFromView(layout, width, height);

        MarkerOptions markerOptions = new MarkerOptions()
                .position(new LatLng(signal.getLatitude(), signal.getLongitude()))
                .icon(BitmapDescriptorFactory.fromBitmap(markerBitmap))
                .anchor(0.5f, 1);
        return googleMap.addMarker(markerOptions);
    }

    private void updateMarkerContent(View layout, Signal signal) {
        ViewHolderPathFragment viewHolder = new ViewHolderPathFragment(layout);

        viewHolder.lastActionTimeStamp.setText(DateFormatter.loadLastActionDateString());
        viewHolder.lastPositionTimeStamp.setText(DateFormatter.formatDateAndTime(new Date(signal.getDate() * 1000)));
        viewHolder.satellitesCountTextView.setText(String.valueOf(signal.getSatellites()));
        viewHolder.voltageCountTextView.setText(String.valueOf(signal.getVoltage()));
        viewHolder.speedCountTextView.setText(String.valueOf(signal.getSpeed()));
        Log.d(TAG, signal.getCharge() + "");
        viewHolder.chargeCountTextView.setText(String.valueOf(signal.getCharge()) + "%");
        viewHolder.directionCountTextView.setText(String.valueOf(signal.getDirection()));
        viewHolder.balanceCountTextView.setText(String.valueOf(signal.getBalance()));
        viewHolder.temperatureCountTextView.setText(String.valueOf(signal.getTemperature()));
    }

    private Bitmap bitmapFromView(View layout, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        layout.setDrawingCacheEnabled(true);

        layout.measure(View.MeasureSpec.makeMeasureSpec(canvas.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(canvas.getHeight(), View.MeasureSpec.EXACTLY));

        layout.layout(0, 0, layout.getMeasuredWidth(), layout.getMeasuredHeight());

        canvas.drawBitmap(layout.getDrawingCache(), 0, 0, paint);
        return bitmap;
    }

    static class ViewHolderPathFragment {

        @Bind(R.id.popupWindowPath_lastActionTimeStamp) TextView lastActionTimeStamp;
        @Bind(R.id.popupWindowPath_lastPositioningTimeStamp) TextView lastPositionTimeStamp;
        @Bind(R.id.popupWindowPath_satellitesCountTextView) TextView satellitesCountTextView;
        @Bind(R.id.popupWindowPath_voltageCountTextView) TextView voltageCountTextView;
        @Bind(R.id.popupWindowPath_speedCountTextView) TextView speedCountTextView;
        @Bind(R.id.popupWindowPath_chargeCountTextView) TextView chargeCountTextView;
        @Bind(R.id.popupWindowPath_directionCountTextView) TextView directionCountTextView;
        @Bind(R.id.popupWindowPath_balanceCountTextView) TextView balanceCountTextView;
        @Bind(R.id.popupWindowPath_temperatureCountTextView) TextView temperatureCountTextView;

        public ViewHolderPathFragment(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
