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
import android.widget.PopupWindow;
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
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import kgk.beacon.R;

public class PathFragment extends SupportMapFragment implements OnMapReadyCallback,
                                                GoogleMap.OnMapClickListener,
                                                GoogleMap.OnMarkerClickListener {

    public static final String TAG = PathFragment.class.getSimpleName();

    private GoogleMap googleMap;
    private PolylineOptions path;
    private Marker currentMarker;
    private Paint paint;

    //// Fragment methods

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

    //// Map methods

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

            if (results[0] < (40 * (22 - googleMap.getCameraPosition().zoom))) {
                if (currentMarker != null) {
                    currentMarker.remove();
                }
                //currentMarker = googleMap.addMarker(new MarkerOptions().position(new LatLng(pathCoordinates.latitude, pathCoordinates.longitude)));
                currentMarker = addCustomMarker(new LatLng(pathCoordinates.latitude, pathCoordinates.longitude));
            }
        }
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        Log.d("Marker", "clicked");
        currentMarker.remove();
        return true;
    }


    //// Private methods

    private void drawPath(GoogleMap googleMap) {
        ArrayList<LatLng> coordinates = new ArrayList<>();

        coordinates.add(new LatLng(55.64331, 37.47085));
        coordinates.add(new LatLng(55.65367, 37.47437));
        coordinates.add(new LatLng(55.66422, 37.48733));
        coordinates.add(new LatLng(55.66539, 37.53453));
        coordinates.add(new LatLng(55.6572, 37.562));

        path = new PolylineOptions();
        path.addAll(coordinates);
        path.width(5);
        path.color(Color.RED);
        path.geodesic(false);

        googleMap.addPolyline(path);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates.get(2), 13));
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
        Random random = new Random();

        ViewHolderPathFragment viewHolder = new ViewHolderPathFragment(layout);

        viewHolder.lastActionTimeStamp.setText("DD.MM.YY 00:00");
        viewHolder.lastPositionTimeStamp.setText("DD.MM.YY 00:00");
        viewHolder.satellitesCountTextView.setText("0");
        viewHolder.voltageCountTextView.setText("0 V");
        viewHolder.speedCountTextView.setText("0 km/h");
        viewHolder.numOfSignalsCountTextView.setText("0");
        viewHolder.directionCountTextView.setText("0");
        viewHolder.balanceCountTextView.setText("0 rub.");
        viewHolder.temperatureCountTextView.setText(random.nextInt(50) + "");
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

    //// Inner classes

    static class ViewHolderPathFragment {

        @Bind(R.id.popupWindowPath_lastActionTimeStamp) TextView lastActionTimeStamp;
        @Bind(R.id.popupWindowPath_lastPositioningTimeStamp) TextView lastPositionTimeStamp;
        @Bind(R.id.popupWindowPath_satellitesCountTextView) TextView satellitesCountTextView;
        @Bind(R.id.popupWindowPath_voltageCountTextView) TextView voltageCountTextView;
        @Bind(R.id.popupWindowPath_speedCountTextView) TextView speedCountTextView;
        @Bind(R.id.popupWindowPath_numOfSignalsCountTextView) TextView numOfSignalsCountTextView;
        @Bind(R.id.popupWindowPath_directionCountTextView) TextView directionCountTextView;
        @Bind(R.id.popupWindowPath_balanceCountTextView) TextView balanceCountTextView;
        @Bind(R.id.popupWindowPath_temperatureCountTextView) TextView temperatureCountTextView;

        public ViewHolderPathFragment(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
