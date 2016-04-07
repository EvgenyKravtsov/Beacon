package kgk.beacon.view.actis;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import kgk.beacon.R;
import kgk.beacon.dispatcher.Dispatcher;
import kgk.beacon.map.Map;
import kgk.beacon.map.MapClickListener;
import kgk.beacon.map.MapManager;
import kgk.beacon.map.MarkerClickListener;
import kgk.beacon.map.event.MapChangeEvent;
import kgk.beacon.map.event.MapReadyForUseEvent;
import kgk.beacon.model.Signal;
import kgk.beacon.stores.ActisStore;

public class MapCustomActivity extends AppCompatActivity implements MapClickListener,
                                                                    MarkerClickListener {

    private static final String TAG = MapCustomActivity.class.getSimpleName();

    public static final String EXTRA_SIGNAL = "extra_signal";

    private Dispatcher dispatcher;
    private ActisStore actisStore;

    private Signal signal;
    private Map map;

    @Bind(R.id.batteryView) TextView batteryView;
    @Bind(R.id.helpToolbarButton) ImageButton helpToolbarButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fragment);
        ButterKnife.bind(this);
        prepareToolbar();
        initFluxDependencies();
        setFragment(new MapManager().loadPreferredMapFragment());
    }

    @Override
    public void onResume() {
        super.onResume();
        dispatcher.register(this);
        updateToolbarContent();
    }

    @Override
    public void onPause() {
        super.onPause();
        dispatcher.unregister(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void prepareToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.actisApp_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.actis_navigation_menu_icon));
    }

    private void initFluxDependencies() {
        dispatcher = Dispatcher.getInstance(EventBus.getDefault());
        actisStore = ActisStore.getInstance(dispatcher);
    }

    private void setFragment(Fragment fragmentToSet) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragmentContainer);

        if (fragment == null) {
            fragmentManager.beginTransaction()
                    .add(R.id.fragmentContainer, fragmentToSet)
                    .commit();
        }
    }

    private void updateToolbarContent() {
        if (batteryView != null) {
            batteryView.setVisibility(View.VISIBLE);

            int charge = actisStore.getSignal().getCharge();

            if (charge >= 70) {
                batteryView.setBackgroundDrawable(getResources().getDrawable(R.drawable.battery_icon_high));
                batteryView.setTextColor(getResources().getColor(R.color.actis_app_green_accent));
            } else if (charge >= 35 && charge < 70) {
                batteryView.setBackgroundDrawable(getResources().getDrawable(R.drawable.battery_icon_average));
                batteryView.setTextColor(getResources().getColor(R.color.actis_app_yellow_accent));
            } else {
                batteryView.setBackgroundDrawable(getResources().getDrawable(R.drawable.battery_icon_low));
                batteryView.setTextColor(getResources().getColor(R.color.actis_app_red_accent));
            }

            batteryView.setText(charge + "%");
        }
    }

    ////

    @Override
    public void onMapClick(double latitude, double longitude) {
        map.clear();
        map.addCustomMarkerPoint(signal.getDirection(), signal.getLatitude(), signal.getLongitude());
    }

    @Override
    public void onMarkerClick(double latitude, double longitude) {
        // map.clear();
        map.addSignalInfoMarker(signal);
    }

    ////

    public void onEvent(MapReadyForUseEvent event) {
        map = event.getMap();
        map.setOnMapClickListener(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.signal = extras.getParcelable(EXTRA_SIGNAL);
        } else {
            this.signal = actisStore.getSignal();
        }

        assert signal != null;
        map.addCustomMarkerPoint(signal.getDirection(), signal.getLatitude(), signal.getLongitude());

        map.moveCamera(signal.getLatitude(), signal.getLongitude(), 13);

//        LatLng cameraCoordinates = MapManager.getCameraCoordinates();
//        int cameraZoom = MapManager.getCameraZoom();
//        if (cameraCoordinates != null && cameraZoom != 0) {
//            map.moveCamera(cameraCoordinates.latitude, cameraCoordinates.longitude, cameraZoom);
//        } else {
//            map.moveCamera(signal.getLatitude(), signal.getLongitude(), 13);
//        }

        map.setOnMarkerClickListener(this);
    }

    public void onEvent(MapChangeEvent event) {
        MapManager mapManager = new MapManager();
        mapManager.savePreferredMap(event.getPreferredMap());

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment mapFragment = fragmentManager
                .findFragmentById(R.id.fragmentContainer);

        mapFragment = new MapManager().loadPreferredMapFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, mapFragment)
                .commit();
    }
}
