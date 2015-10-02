package kgk.beacon.view;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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
            LatLng coordinates = new LatLng(signal.getLatitude(), signal.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(coordinates);

            googleMap.addMarker(markerOptions);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 13));
        }
    }

    private void initFluxDependencies() {
        dispatcher = Dispatcher.getInstance(EventBus.getDefault());
        actionCreator = ActionCreator.getInstance(dispatcher);
        signalStore = SignalStore.getInstance(dispatcher);
    }

    public void onEventMainThread(SignalStore.SignalStoreChangeEvent event) {
        Log.d(TAG, "InformationActivity onEvent");
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
}
