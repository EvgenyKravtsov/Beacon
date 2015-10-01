package kgk.beacon.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
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

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import kgk.beacon.R;
import kgk.beacon.dispatcher.Dispatcher;
import kgk.beacon.model.Signal;
import kgk.beacon.stores.SignalStore;
import kgk.beacon.util.DateFormatter;

public class MapCustomFragment extends SupportMapFragment implements OnMapReadyCallback,
                                                                GoogleMap.OnMarkerClickListener {

    private static final String TAG = MapCustomFragment.class.getSimpleName();

    private Signal signal;
    private GoogleMap googleMap;
    private Paint paint;
    private Marker marker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        paint = new Paint();
        signal = cloneSignal(SignalStore.getInstance(Dispatcher.getInstance(EventBus.getDefault())).getSignal());
    }

    private Signal cloneSignal(Signal signal) {
        Signal resultSignal = new Signal();
        resultSignal.setDeviceId(signal.getDeviceId());
        resultSignal.setMode(signal.getMode());
        resultSignal.setLatitude(signal.getLatitude());
        resultSignal.setLongitude(signal.getLongitude());
        resultSignal.setDate(signal.getDate());
        resultSignal.setVoltage(signal.getVoltage());
        resultSignal.setBalance(signal.getBalance());
        resultSignal.setSatellites(signal.getSatellites());
        resultSignal.setCharge(signal.getCharge());
        resultSignal.setSpeed(signal.getSpeed());
        resultSignal.setDirection(signal.getDirection());
        resultSignal.setTemperature(signal.getTemperature());
        return resultSignal;
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getMapAsync(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setOnMarkerClickListener(this);
        UiSettings settings = googleMap.getUiSettings();
        settings.setZoomControlsEnabled(true);

        if (signal != null) {
            LatLng coordinates = new LatLng(signal.getLatitude(), signal.getLongitude());
            marker = addCustomMarker(coordinates);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 13));
        }
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        Log.d(TAG, "MapCustomFragment onMarkerClick");
        LatLng coordinates = new LatLng(signal.getLatitude(), signal.getLongitude());

        if (this.marker != null) {
            marker.remove();
            this.marker = null;

            coordinates = new LatLng(signal.getLatitude(), signal.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(coordinates);

            googleMap.addMarker(markerOptions);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 13));
        } else {
            marker.remove();
            this.marker = addCustomMarker(coordinates);
        }

        return true;
    }

    private Marker addCustomMarker(LatLng coordinates) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.map_custom_marker, null);
        updateMarkerContent(layout);

        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, getResources().getDisplayMetrics());
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 170, getResources().getDisplayMetrics());
        Bitmap markerBitmap = bitmapFromView(layout, width, height);

        MarkerOptions markerOptions = new MarkerOptions()
                .position(coordinates)
                .icon(BitmapDescriptorFactory.fromBitmap(markerBitmap))
                .anchor(0.5f, 1);
        return googleMap.addMarker(markerOptions);
    }

    private void updateMarkerContent(View layout) {
        ViewHolderMapCustomFragment viewHolder = new ViewHolderMapCustomFragment(layout);
        viewHolder.lastActionTimeStamp.setText(DateFormatter.formatDateAndTime(new Date(signal.getDate() * 1000)));
        viewHolder.lastPositionTimeStamp.setText(DateFormatter.formatDateAndTime(new Date(signal.getDate() * 1000)));
        viewHolder.satellitesCountTextView.setText(String.valueOf(signal.getSatellites()));
        viewHolder.voltageCountTextView.setText(String.valueOf(signal.getVoltage()));
        viewHolder.speedCountTextView.setText(String.valueOf(signal.getSpeed()));
        viewHolder.chargeCountTextView.setText(String.valueOf(signal.getCharge()));
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

    static class ViewHolderMapCustomFragment {

        @Bind(R.id.popupWindowPath_lastActionTimeStamp) TextView lastActionTimeStamp;
        @Bind(R.id.popupWindowPath_lastPositioningTimeStamp) TextView lastPositionTimeStamp;
        @Bind(R.id.popupWindowPath_satellitesCountTextView) TextView satellitesCountTextView;
        @Bind(R.id.popupWindowPath_voltageCountTextView) TextView voltageCountTextView;
        @Bind(R.id.popupWindowPath_speedCountTextView) TextView speedCountTextView;
        @Bind(R.id.popupWindowPath_chargeCountTextView) TextView chargeCountTextView;
        @Bind(R.id.popupWindowPath_directionCountTextView) TextView directionCountTextView;
        @Bind(R.id.popupWindowPath_balanceCountTextView) TextView balanceCountTextView;
        @Bind(R.id.popupWindowPath_temperatureCountTextView) TextView temperatureCountTextView;

        public ViewHolderMapCustomFragment(View view) {
            ButterKnife.bind(this, view);
        }
    }
}