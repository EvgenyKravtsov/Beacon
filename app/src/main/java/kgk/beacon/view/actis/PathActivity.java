package kgk.beacon.view.actis;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
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

public class PathActivity extends AppCompatActivity implements MapClickListener,
                                                               MarkerClickListener {

    private static final String TAG = PathActivity.class.getSimpleName();

    private Dispatcher dispatcher;
    private ActisStore actisStore;

    private List<Signal> signals;

    private Map map;
    @Bind(R.id.batteryView) TextView batteryView;
    @Bind(R.id.helpToolbarButton) ImageButton helpToolbarButton;
    @Bind(R.id.toolbar_title) TextView toolbarTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fragment);
        ButterKnife.bind(this);
        prepareToolbar();
        initFluxDependencies();

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragmentContainer);

        if (fragment == null) {
            fragment = new MapManager().loadPreferredMapFragmentForActis();
            fragmentManager.beginTransaction()
                    .add(R.id.fragmentContainer, fragment)
                    .commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        dispatcher.register(this);
        updateBatteryView(actisStore.getSignal().getCharge());
        updateToolbarButtons();
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
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void prepareToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.actisApp_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.actis_navigation_menu_icon));
        toolbarTitle.setText(getString(R.string.path_action_bar_label));
    }

    private void initFluxDependencies() {
        dispatcher = Dispatcher.getInstance(EventBus.getDefault());
        actisStore = ActisStore.getInstance(dispatcher);
    }

    private void drawPath() {
        ArrayList<LatLng> coordinates = new ArrayList<>();
        signals = actisStore.getSignalsDisplayed();

        for (Signal signal : signals) {
            double latitude = signal.getLatitude();
            double longitude = signal.getLongitude();
            coordinates.add(new LatLng(latitude, longitude));
        }

        map.addPolyline(coordinates);

        for (int i = 0; i < signals.size(); i++) {
            Signal signal = signals.get(i);
            map.addCustomMarkerPoint(signal.getDirection(), signal.getLatitude(), signal.getLongitude());
        }

        map.moveCamera(coordinates.get(0).latitude, coordinates.get(0).longitude, 13);

//        LatLng cameraCoordinates = MapManager.getCameraCoordinates();
//        int cameraZoom = MapManager.getCameraZoom();
//        if (cameraCoordinates != null && cameraZoom != 0) {
//            map.moveCamera(cameraCoordinates.latitude, cameraCoordinates.longitude, cameraZoom);
//        } else {
//            map.moveCamera(coordinates.get(0).latitude, coordinates.get(0).longitude, 13);
//        }
    }

    private void updateBatteryView(int charge) {
        if (batteryView != null) {
            batteryView.setVisibility(View.VISIBLE);

            batteryView.setBackgroundDrawable(getResources().getDrawable(R.drawable.battery_icon_general));
            batteryView.setTextColor(getResources().getColor(android.R.color.white));

//            if (charge >= 70) {
//                batteryView.setBackgroundDrawable(getResources().getDrawable(R.drawable.battery_icon_high));
//                batteryView.setTextColor(getResources().getColor(R.color.actis_app_green_accent));
//            } else if (charge >= 35 && charge < 70) {
//                batteryView.setBackgroundDrawable(getResources().getDrawable(R.drawable.battery_icon_average));
//                batteryView.setTextColor(getResources().getColor(R.color.actis_app_yellow_accent));
//            } else {
//                batteryView.setBackgroundDrawable(getResources().getDrawable(R.drawable.battery_icon_low));
//                batteryView.setTextColor(getResources().getColor(R.color.actis_app_red_accent));
//            }

            batteryView.setText(charge + "%");
        }
    }

    private void updateToolbarButtons() {
        if (helpToolbarButton != null) {
            helpToolbarButton.setVisibility(View.VISIBLE);
        }
    }


    ////

    @Override
    public void onMapClick(double latitude, double longitude) {
        map.removeSignalInfoMarker();
    }

    @Override
    public void onMarkerClick(double latitude, double longitude) {
        for (Signal signal : signals) {
            if (signal.getLatitude() == latitude && signal.getLongitude() == longitude) {
                map.addSignalInfoMarker(signal);
            }
        }
    }

    ////

    public void onEvent(MapReadyForUseEvent event) {
        map = event.getMap();
        drawPath();

        map.setOnMapClickListener(this);
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

    ////

    @OnClick(R.id.helpToolbarButton)
    public void onClickHelpToolbarButton(View view) {
        Intent helpScreenIntent = new Intent(this, HelpActivity.class);
        helpScreenIntent.putExtra(HelpActivity.KEY_SCREEN_NAME, HelpActivity.TRACK_SCREEN);
        startActivity(helpScreenIntent);
    }
}