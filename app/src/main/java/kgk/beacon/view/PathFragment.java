package kgk.beacon.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import kgk.beacon.util.AppController;
import kgk.beacon.util.DateFormatter;

public class PathFragment extends SupportMapFragment implements OnMapReadyCallback,
                                                GoogleMap.OnMapClickListener,
                                                GoogleMap.OnMarkerClickListener {

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
        currentMarker.remove();
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        if (currentMarker != null) {
            currentMarker.remove();
        }

        Signal currentSignal = null;
        for (Signal signal : signals) {
            if (signal.getLatitude() == marker.getPosition().latitude &&
                    signal.getLongitude() == marker.getPosition().longitude) {
                currentSignal = signal;
                Log.d(TAG, signal.getCharge() + "");
            }
        }

        currentMarker = addCustomMarker(currentSignal);
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

        for (Signal signal : signals) {
            googleMap.addMarker(addCustomMarkerPoint(signal));
        }
    }

    private MarkerOptions addCustomMarkerPoint(Signal signal) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.map_custom_marker_point, null);

        ImageView arrow = (ImageView) layout.findViewById(R.id.mapCustomMarkerPoint_arrow);
        arrow.setRotation((float) signal.getDirection());

        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, getResources().getDisplayMetrics());
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, getResources().getDisplayMetrics());

        Bitmap markerBitmap = bitmapFromView(layout, width, height);

        return new MarkerOptions()
                .position(new LatLng(signal.getLatitude(), signal.getLongitude()))
                .icon(BitmapDescriptorFactory.fromBitmap(markerBitmap));
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
        viewHolder.satellitesCountTextView.setText(String.valueOf(signal.getSatellites())
                + getString(R.string.list_item_satellites_sign));
        viewHolder.voltageCountTextView.setText(String.valueOf(signal.getVoltage())
                + getString(R.string.list_item_voltage_sign));
        viewHolder.speedCountTextView.setText(String.valueOf(signal.getSpeed())
                + getString(R.string.list_item_speed_sign));
        viewHolder.chargeCountTextView.setText(String.valueOf(signal.getCharge()) + "%");
        viewHolder.directionCountTextView.setText(AppController
                .getDirectionLetterFromDegrees(signal.getDirection()));
        viewHolder.balanceCountTextView.setText(String.valueOf(signal.getBalance())
                + getString(R.string.list_item_balance_sign));
        viewHolder.temperatureCountTextView.setText(String.valueOf(signal.getTemperature())
                + getString(R.string.list_item_temperature_sign));
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
