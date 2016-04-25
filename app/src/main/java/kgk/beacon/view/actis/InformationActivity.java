package kgk.beacon.view.actis;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

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
import kgk.beacon.networking.DownloadDataStatus;
import kgk.beacon.networking.event.DownloadDataInProgressEvent;
import kgk.beacon.networking.gcm.GCMClientManager;
import kgk.beacon.stores.ActisStore;
import kgk.beacon.util.AppController;
import kgk.beacon.view.actis.event.FullscreenEvent;

/**
 * Главный экран приложения
 */
public class InformationActivity extends AppCompatActivity implements MapClickListener,
                                                                        MarkerClickListener {

    private static final String TAG = InformationActivity.class.getSimpleName();

    private Dispatcher dispatcher;
    private ActisStore actisStore;

    private Map map;

    @Bind(R.id.batteryView) TextView batteryView;
    @Bind(R.id.helpToolbarButton) ImageButton helpToolbarButton;
    @Bind(R.id.fragmentContainer) FrameLayout fragmentContainer;
    @Bind(R.id.toolbar_title) TextView toolbarTitle;

    private ProgressDialog downloadDataDialog;

    private boolean fullscreenStatus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        ButterKnife.bind(this);
        prepareToolbar();
        initFluxDependencies();

        setInformationFragment();

        try {
            initializeMap();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // TODO Delete test code
        GCMClientManager gcmClientManager = new GCMClientManager(this, "320735936006");
        gcmClientManager.registerIfNeeded(new GCMClientManager.RegistrationCompletedHandler() {
            @Override
            public void onSuccess(String registrationId, boolean isNewRegistration) {
                Log.d(TAG, "Registration id - " + registrationId);
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            try {
                if (extras.getString("key_target").equals("from_device_list")) {
                    showDownloadDataDialog();
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        dispatcher.register(this);
        initializeMap();

        Signal signal = actisStore.getSignal();
        if (signal != null) {
            updateBatteryView(signal.getCharge());
        } else {
            updateBatteryView(0);
        }

        updateToolbarButtons();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                actisStore.setSignal(null);
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

    private void prepareToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.actisApp_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.actis_navigation_menu_icon));

        String title = AppController.getInstance().getActiveDeviceId() + "";

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }

        toolbarTitle.setText(title);
    }

    private void initFluxDependencies() {
        dispatcher = Dispatcher.getInstance(EventBus.getDefault());
        actisStore = ActisStore.getInstance(dispatcher);
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

    /** Инициализация карты */
    private void initializeMap() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment mapFragment = fragmentManager
                .findFragmentById(R.id.activityInformation_mapContainer);

        if (mapFragment == null) {
            mapFragment = new MapManager().loadPreferredMapFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.activityInformation_mapContainer, mapFragment)
                    .commit();
        }
    }

    /** Обновить карту */
    private void updateMap() {
        Signal signal = actisStore.getSignal();
        map.clear();
        map.setOnMapClickListener(this);

        if (signal != null) {
            map.addCustomMarkerPoint(signal.getDirection(), signal.getLatitude(), signal.getLongitude());
            map.moveCamera(signal.getLatitude(), signal.getLongitude(), 13);
        }

        map.addSignalInfoMarker(signal);
    }

    /** Обновить показания значка батареи */
    private void updateBatteryView(int charge) {
        batteryView.setVisibility(View.VISIBLE);

        batteryView.setBackgroundDrawable(getResources().getDrawable(R.drawable.battery_icon_general));
        batteryView.setTextColor(getResources().getColor(android.R.color.white));

//        if (charge >= 70) {
//            batteryView.setBackgroundDrawable(getResources().getDrawable(R.drawable.battery_icon_high));
//            batteryView.setTextColor(getResources().getColor(R.color.battery_text_color));
//        } else if (charge >= 35 && charge < 70) {
//            batteryView.setBackgroundDrawable(getResources().getDrawable(R.drawable.battery_icon_average));
//            batteryView.setTextColor(getResources().getColor(R.color.actis_app_yellow_accent));
//        } else {
//            batteryView.setBackgroundDrawable(getResources().getDrawable(R.drawable.battery_icon_low));
//            batteryView.setTextColor(getResources().getColor(R.color.actis_app_red_accent));
//        }

        batteryView.setText(charge + "%");
    }

    private void updateToolbarButtons() {
        if (helpToolbarButton != null) {
            helpToolbarButton.setVisibility(View.VISIBLE);
        }
    }

    private void showDownloadDataDialog() {
        downloadDataDialog = new ProgressDialog(this);
        downloadDataDialog.setTitle(getString(R.string.download_data_progress_dialog_title));
        downloadDataDialog.setMessage(getString(R.string.download_data_progress_dialog_message));
        downloadDataDialog.show();
    }

    ////

    @Override
    public void onMapClick(double latitude, double longitude) {
        map.removeSignalInfoMarker();
    }

    @Override
    public void onMarkerClick(double latitude, double longitude) {
        Signal signal = actisStore.getSignal();
        map.addSignalInfoMarker(signal);
    }

    ////

    public void onEvent(MapReadyForUseEvent event) {
        map = event.getMap();
        map.setOnMarkerClickListener(this);
        map.turnOnFullscreenButton();
        updateMap();
    }

    public void onEvent(MapChangeEvent event) {
        MapManager mapManager = new MapManager();
        mapManager.savePreferredMap(event.getPreferredMap());

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment mapFragment = fragmentManager
                .findFragmentById(R.id.activityInformation_mapContainer);

        mapFragment = new MapManager().loadPreferredMapFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.activityInformation_mapContainer, mapFragment)
                .commit();

        updateMap();
    }

    public void onEventMainThread(ActisStore.ActisStoreChangeEvent event) {
        updateMap();
        updateBatteryView(actisStore.getSignal().getCharge());
    }

    public void onEventMainThread(FullscreenEvent event) {
        ImageButton fullscreenButton = event.getFullscreenButton();
        fullscreenStatus = !fullscreenStatus;
        if (fullscreenStatus) {
            fullscreenButton.setImageDrawable(getResources().getDrawable(R.drawable.fullscreen_exit_icon_grey));
            fragmentContainer.setVisibility(View.GONE);
        } else {
            fullscreenButton.setImageDrawable(getResources().getDrawable(R.drawable.fullscreen_enter_icon_grey));
            fragmentContainer.setVisibility(View.VISIBLE);
        }
    }

    public void onEventMainThread(DownloadDataInProgressEvent event) {
        if (event.getStatus() == DownloadDataStatus.Success ||
                event.getStatus() == DownloadDataStatus.noInternetConnection) {
            if (downloadDataDialog != null) {
                downloadDataDialog.dismiss();
            }
        }
    }

    ////

    @OnClick(R.id.helpToolbarButton)
    public void onClickHelpToolbarButton(View view) {
        Intent helpScreenIntent = new Intent(this, HelpActivity.class);
        helpScreenIntent.putExtra(HelpActivity.KEY_SCREEN_NAME, HelpActivity.INFORMATION_SCREEN);
        startActivity(helpScreenIntent);
    }
}
