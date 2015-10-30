package kgk.beacon.view;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import de.greenrobot.event.EventBus;
import kgk.beacon.R;
import kgk.beacon.actions.ActionCreator;
import kgk.beacon.dispatcher.Dispatcher;
import kgk.beacon.model.Signal;
import kgk.beacon.stores.SignalStore;
import kgk.beacon.util.AppController;

public class InformationActivity extends AppCompatActivity implements OnMapReadyCallback,
                                                    GoogleMap.OnMapClickListener {

    private static final String TAG = InformationActivity.class.getSimpleName();

    private GoogleMap googleMap;

    private Dispatcher dispatcher;
    private ActionCreator actionCreator;
    private SignalStore signalStore;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        initFluxDependencies();

        setInformationFragment();

        try {
            initializeMap();
        } catch (Exception e) {
            e.printStackTrace();
        }

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        dispatcher.register(this);
        initializeMap();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        dispatcher.unregister(this);
    }

    private void setInformationFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragmentContainer);

        if (fragment == null) {
            fragment = new InformationFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.fragmentContainer, fragment)
                    .commit();
        }
    }

    private void initializeMap() {
        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.activityInformation_map);
        if (fragment != null) {
            fragment.getMapAsync(this);
        }
    }

    private void updateMap() {
        Signal signal = signalStore.getSignal();
        googleMap.clear();

        if (signal != null) {
            googleMap.addMarker(addCustomMarkerPoint(signal));
            googleMap.moveCamera(CameraUpdateFactory
                    .newLatLngZoom(new LatLng(signal.getLatitude(), signal.getLongitude()), 13));
        }
    }

    private void initFluxDependencies() {
        dispatcher = Dispatcher.getInstance(EventBus.getDefault());
        actionCreator = ActionCreator.getInstance(dispatcher);
        signalStore = SignalStore.getInstance(dispatcher);
    }

    public void onEventMainThread(SignalStore.SignalStoreChangeEvent event) {
        updateMap();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setOnMapClickListener(this);
        this.googleMap = googleMap;
        updateMap();
    }

    @Override
    public void onMapClick(LatLng clickCoordinates) {
        if (signalStore.getSignal() != null) {
            Intent mapIntent = new Intent(this, MapCustomActivity.class);
            startActivity(mapIntent);
        } else {
            Toast.makeText(AppController.getInstance(), R.string.no_signals_toast, Toast.LENGTH_SHORT).show();
        }
    }

    private MarkerOptions addCustomMarkerPoint(Signal signal) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

    private Bitmap bitmapFromView(View layout, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        layout.setDrawingCacheEnabled(true);

        layout.measure(View.MeasureSpec.makeMeasureSpec(canvas.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(canvas.getHeight(), View.MeasureSpec.EXACTLY));

        layout.layout(0, 0, layout.getMeasuredWidth(), layout.getMeasuredHeight());

        canvas.drawBitmap(layout.getDrawingCache(), 0, 0, new Paint());
        return bitmap;
    }
}
