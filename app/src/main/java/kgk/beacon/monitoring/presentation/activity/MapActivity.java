package kgk.beacon.monitoring.presentation.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.MapView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import kgk.beacon.R;
import kgk.beacon.monitoring.DependencyInjection;
import kgk.beacon.monitoring.data.Configuration;
import kgk.beacon.monitoring.domain.model.MonitoringEntity;
import kgk.beacon.monitoring.domain.model.MonitoringEntityStatus;
import kgk.beacon.monitoring.domain.model.MonitoringManager;
import kgk.beacon.monitoring.domain.model.routereport.RouteReport;
import kgk.beacon.monitoring.presentation.adapter.GoogleMapAdapter;
import kgk.beacon.monitoring.presentation.adapter.MapAdapter;
import kgk.beacon.monitoring.presentation.model.MapType;
import kgk.beacon.monitoring.presentation.presenter.MapViewPresenter;
import kgk.beacon.monitoring.presentation.utils.SimpleGestureFilter;

public class MapActivity extends AppCompatActivity implements
        kgk.beacon.monitoring.presentation.view.MapView {

    // TODO Check ignition status

    // Views

        // Information bar
        private ImageView informationBarBackButtonImageView;
        private TextView informationBarDeviceTextView;
        private LinearLayout deviceInformationExpandedLayout;
        private TextView deviceStateTextView;
        private TextView deviceIgnitionTextView;
        private TextView deviceSpeedTextView;
        private TextView deviceSatellitesTextView;
        private TextView deviceGsmTextView;
        private TextView deviceUpdatedTextView;
        private ImageButton expandInformationButton;

        private boolean informationExpanded;

    private SlidingUpPanelLayout slider;
    private MapView googleMapView;
    private ImageButton trafficButton;
    private ImageButton zoomInButton;
    private ImageButton zoomOutButton;
    private ImageButton centerOnActiveButton;
    private ImageButton menuButton;
    private Button kgkMapMenuButton;
    private Button yandexMapMenuButton;
    private Button googleMapMenuButton;
    private Button satelliteMapMenuButton;
    private ImageButton hideMenuButton;
    private Button chooseVehicleMenuButton;
    private Button chooseVehicleGroupMenuButton;
    private Button profileMenuButton;
    private Button routeReportSettingsMenuButton;
    private Button helpMenuButton;
    private Button aboutMenuButton;
    private Button settingsMenuButton;
    private ScrollView menuButtonsLayout;
    private ImageButton quickReportButton;

    // Dialogs
    private ProgressDialog progressDialog;

    private MapViewPresenter presenter;
    private MapAdapter mapAdapter;
    private MonitoringEntity activeMonitoringEntity;
    private Button activeMenuMapButton;

    ////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monitoring_activity_map);

        initViews(savedInstanceState);
        initListeners();
        initMap();
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindPresenter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.requestMonitoringEntities();
        presenter.requestMonitoringEntityGroupsCount();
        presenter.requestMonitoringEntitiesUpdate();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindPresenter();
    }

    ////

    @Override
    public void showMonitoringEntities(List<MonitoringEntity> monitoringEntities) {
        if (monitoringEntities != null && monitoringEntities.size() > 0) {
            mapAdapter.clearMarkers();

            for (MonitoringEntity monitoringEntity : monitoringEntities) {
                if (monitoringEntity.isDisplayEnabled()) mapAdapter.showMapEntity(monitoringEntity);
            }
        }
    }

    @Override
    public MonitoringEntity getActiveMonitoringEntity() {
        return activeMonitoringEntity;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void setActiveMonitoringEntity(MonitoringEntity monitoringEntity) {
        activeMonitoringEntity = monitoringEntity;
        mapAdapter.centerOnCoordinatesAnimated(
                activeMonitoringEntity.getLatitude(),
                activeMonitoringEntity.getLongitude()
        );

        informationBarDeviceTextView.setText(String.format(
                "%s %s %s",
                activeMonitoringEntity.getMark(),
                activeMonitoringEntity.getModel(),
                activeMonitoringEntity.getStateNumber()
        ));

        deviceStateTextView.setText(String.format(
                "%s %s",
                getString(R.string.monitoring_entity_screen_status),
                makeStatusString(monitoringEntity.getStatus())));
        deviceIgnitionTextView.setText(String.format(
                "%s %s",
                getString(R.string.monitoring_entity_screen_ignition),
                activeMonitoringEntity.isEngineIgnited() ?
                        getString(R.string.monitoring_entity_screen_ignition_on) :
                        getString(R.string.monitoring_entity_screen_ignition_off)));
        deviceSpeedTextView.setText(String.format(
                "%s %s",
                getString(R.string.monitoring_entity_screen_speed),
                String.format(Locale.ROOT, "%.2f", monitoringEntity.getSpeed())));
        deviceSatellitesTextView.setText(String.format(
                Locale.ROOT,
                "%s %d",
                getString(R.string.monitoring_entity_screen_satellites),
                activeMonitoringEntity.getSatellites()));
        deviceGsmTextView.setText(String.format(
                "%s: %s",
                getString(R.string.monitoring_device_gsm),
        activeMonitoringEntity.getGsm()));
        deviceUpdatedTextView.setText(String.format(
                "%s %s",
                getString(R.string.monitoring_entity_screen_last_update),
                String.format("%s %s %s",
                        new SimpleDateFormat("dd.MM").format(activeMonitoringEntity.getLastUpdateTimestamp()),
                        getString(R.string.monitoring_entity_screen_at),
                        new SimpleDateFormat("HH:mm").format(activeMonitoringEntity.getLastUpdateTimestamp()))));
    }

    @Override
    public void toggleCenterOnActiveControl(boolean enabled) {
        int visibility = enabled ? View.VISIBLE : View.INVISIBLE;
        centerOnActiveButton.setVisibility(visibility);

        if (enabled) centerOnActiveButton.setClickable(true);
        else centerOnActiveButton.setClickable(false);
    }

    @Override
    public void monitoringEntityChosen(long id) {
        presenter.requestMonitoringEntityById(id);
    }

    @Override
    public void toggleChooseGroupMenuButton(boolean enabled) {
        int visibility = enabled ? View.VISIBLE : View.GONE;
        chooseVehicleGroupMenuButton.setVisibility(visibility);
    }

    @Override
    public void navigateToRouteReportView(RouteReport routeReport) {
        toggleProgressDialog(false);
        Intent intent = new Intent(this, RouteReportActivity.class);
        intent.putExtra(RouteReportActivity.EXTRA_ROUTE_REPORT, routeReport);
        if (routeReport != null) startActivity(intent);
    }

    @Override
    public void mapReadyForUse() {
        Configuration configuration = DependencyInjection.provideConfiguration();
        MapType mapType = configuration.loadDefaultMapType();
        mapAdapter.setMapType(mapType);

        switch (mapType) {
            case KGK:
                activeMenuMapButton = kgkMapMenuButton;
                break;
            case YANDEX:
                activeMenuMapButton = yandexMapMenuButton;
                break;
            case GOOGLE:
                activeMenuMapButton = googleMapMenuButton;
                break;
            case SATELLITE:
                activeMenuMapButton = satelliteMapMenuButton;
                break;
        }

        activeMenuMapButton.setBackgroundDrawable(
                getResources().getDrawable(
                        R.drawable.monitoring_menu_map_button_activated_background_selector));

        hackedMethod();
    }

    @Override
    public void notifyNoDataForRouteReport() {
        if (progressDialog != null) progressDialog.dismiss();

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage(R.string.monitoring_no_data_for_device)
                .setPositiveButton(android.R.string.ok, null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    ////

    private void initViews(Bundle savedInstanceState) {
        informationBarBackButtonImageView = (ImageView)
                findViewById(R.id.monitoring_activity_map_information_bar_back_button);
        informationBarDeviceTextView = (TextView)
                findViewById(R.id.monitoring_activity_map_information_bar_device);

        deviceInformationExpandedLayout = (LinearLayout) findViewById(R.id.information_bar_extended_layout);
        deviceStateTextView = (TextView) findViewById(R.id.device_state);
        deviceIgnitionTextView = (TextView) findViewById(R.id.device_ignition);
        deviceSpeedTextView = (TextView) findViewById(R.id.device_speed);
        deviceSatellitesTextView = (TextView) findViewById(R.id.device_satellites);
        deviceGsmTextView = (TextView) findViewById(R.id.device_gsm);
        deviceUpdatedTextView = (TextView) findViewById(R.id.device_updated);
        expandInformationButton = (ImageButton)
                findViewById(R.id.monitoring_activity_map_information_bar_hide_button);

        menuButtonsLayout = (ScrollView)
                findViewById(R.id.monitoring_activity_menu_buttons_layout);

        slider = (SlidingUpPanelLayout)
                findViewById(R.id.monitoring_activity_map_slider);
        slider.setDragView(R.id.monitoring_activity_menu_hide_button);
        slider.setScrollableView(menuButtonsLayout);
        slider.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

        googleMapView = (MapView) findViewById(R.id.monitoring_activity_map_google_map);
        assert googleMapView != null;
        googleMapView.onCreate(savedInstanceState);
        googleMapView.onResume();

        trafficButton = (ImageButton) findViewById(R.id.monitoring_activity_map_traffic_button);
        zoomInButton = (ImageButton) findViewById(R.id.monitoring_activity_map_zoom_in_button);
        zoomOutButton = (ImageButton) findViewById(R.id.monitoring_activity_map_zoom_out_button);

        menuButton = (ImageButton)
                findViewById(R.id.monitoring_activity_menu_button);

        kgkMapMenuButton = (Button)
                findViewById(R.id.monitoring_activity_menu_kgk_map_button);
        yandexMapMenuButton = (Button)
                findViewById(R.id.monitoring_activity_menu_yandex_map_button);
        googleMapMenuButton = (Button)
                findViewById(R.id.monitoring_activity_menu_google_map_button);
        satelliteMapMenuButton = (Button)
                findViewById(R.id.monitoring_activity_menu_satellite_map_button);

        hideMenuButton = (ImageButton)
                findViewById(R.id.monitoring_activity_menu_hide_button);
        chooseVehicleMenuButton = (Button)
                findViewById(R.id.monitoring_activity_menu_choose_vehicle_button);
        chooseVehicleGroupMenuButton = (Button)
                findViewById(R.id.monitoring_activity_menu_choose_vehicle_group_button);
        profileMenuButton = (Button)
                findViewById(R.id.monitoring_activity_menu_profile_button);
        routeReportSettingsMenuButton = (Button)
                findViewById(R.id.monitoring_activity_menu_route_report_settings_button);
        helpMenuButton = (Button)
                findViewById(R.id.monitoring_activity_menu_help_button);
        aboutMenuButton = (Button)
                findViewById(R.id.monitoring_activity_menu_about_button);
        settingsMenuButton = (Button)
                findViewById(R.id.monitoring_activity_menu_settings_button);

        centerOnActiveButton = (ImageButton)
                findViewById(R.id.monitoring_activity_center_on_active_button);

        quickReportButton = (ImageButton)
                findViewById(R.id.monitoring_activity_quick_report_button);
    }

    private void initListeners() {
        informationBarBackButtonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        informationBarDeviceTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (informationExpanded) collapseInformation();
                else expandInformation();
            }
        });

        final SimpleGestureFilter simpleGestureFilter = new SimpleGestureFilter(
                this,
                new SimpleGestureFilter.SimpleGestureListener() {

                    @Override
                    public void onSwipe(int direction) {
                        switch (direction) {
                            case SimpleGestureFilter.SWIPE_UP: collapseInformation(); break;
                        }
                    }

                    @Override
                    public void onDoubleTap() {

                    }
                });

        deviceInformationExpandedLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                simpleGestureFilter.onTouchEvent(motionEvent);
                return true;
            }
        });

        expandInformationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onExpandInfrormationButtonClick();
            }
        });

        final SimpleGestureFilter simpleGestureFilter1 = new SimpleGestureFilter(
                this,
                new SimpleGestureFilter.SimpleGestureListener() {

                    @Override
                    public void onSwipe(int direction) {
                        switch (direction) {
                            case SimpleGestureFilter.SWIPE_UP: collapseInformation(); break;
                            case SimpleGestureFilter.SWIPE_DOWN: expandInformation(); break;
                        }
                    }

                    @Override
                    public void onDoubleTap() {

                    }
                });

        expandInformationButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                simpleGestureFilter1.onTouchEvent(motionEvent);
                return false;
            }
        });

        slider.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(
                    View panel,
                    SlidingUpPanelLayout.PanelState previousState,
                    SlidingUpPanelLayout.PanelState newState) {

                if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED)
                    slider.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
            }
        });

        trafficButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTrafficButtonClick();
            }
        });
        zoomInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onZoomInButtonClick();
            }
        });
        zoomOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onZoomOutButtonClick();
            }
        });

        centerOnActiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCenterOnActiveButtonClick();
            }
        });

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMenuButtonClick();
            }
        });
        kgkMapMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickKgkMapMenuButton();
            }
        });

        yandexMapMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickYandexMapMenuButton();
            }
        });
        googleMapMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickGoogleMapMenuButton();
            }
        });
        satelliteMapMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSatelliteMapMenuButton();
            }
        });
        chooseVehicleMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChooseVehicleButtonClick();
            }
        });
        chooseVehicleGroupMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChooseVehicleGroupMenuButtonClick();
            }
        });
        profileMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onProfileMenuButtonClick();
            }
        });
        routeReportSettingsMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRouteReportSettingsMenuButtonClick();
            }
        });

        helpMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onHelpMenuButtonClick();
            }
        });
        aboutMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAboutMenuButtonClick();
            }
        });
        hideMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onHideMenuButtonClick();
            }
        });

        settingsMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSettingsMenuButtonClick();
            }
        });

        quickReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onQuickReportButtonClick();
            }
        });
    }

    private void initMap() {
        mapAdapter = new GoogleMapAdapter(this, googleMapView);
    }

    private void bindPresenter() {
        presenter = new MapViewPresenter(this);
    }

    private void unbindPresenter() {
        if (presenter != null)  {
            presenter.stopMonitoringEntitiesUpdate();
            presenter.unbindView();
        }
        presenter = null;
    }

    private void changeActiveMenuMapButton(Button activeButton) {
        if (activeMenuMapButton == activeButton) return;

        activeMenuMapButton.setBackgroundDrawable(
                getResources().getDrawable(
                        R.drawable.monitoring_general_background_selector
                )
        );

        activeButton.setBackgroundDrawable(
                getResources().getDrawable(
                        R.drawable.monitoring_menu_map_button_activated_background_selector
                )
        );

        activeMenuMapButton = activeButton;
    }

    // TODO Refactor hacked method
    private void hackedMethod() {
        MonitoringManager monitoringManager = MonitoringManager.getInstance();
        List<MonitoringEntity> monitoringEntities;
        if (monitoringManager.getActiveMonitoringEntityGroup() == null) {
            monitoringEntities = monitoringManager.getMonitoringEntities();
        } else {
            monitoringEntities = monitoringManager
                    .getActiveMonitoringEntityGroup()
                    .getMonitoringEntities();
        }

        if (monitoringEntities != null && monitoringEntities.size() > 0) {
            mapAdapter.clearMarkers();

            for (MonitoringEntity monitoringEntity : monitoringEntities) {
                if (monitoringEntity.isDisplayEnabled()) mapAdapter.showMapEntity(monitoringEntity);
            }
        }
    }

    private void toggleProgressDialog(boolean status) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.monitoring_downloading_data));
            progressDialog.setIndeterminate(true);
            progressDialog.setCanceledOnTouchOutside(false);
        }

        if (status) progressDialog.show();
        else progressDialog.dismiss();
    }

    private void notifyNoInternetAvailable() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage(R.string.no_internet_connection_message)
                .setPositiveButton(android.R.string.ok, null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private String makeStatusString(MonitoringEntityStatus status) {
        String statusString;

        switch (status) {
            case IN_MOTION:
                statusString = getString(R.string.monitoring_entity_screen_moving_status);
                break;
            case STOPPED:
                statusString = getString(R.string.monitoring_entity_screen_parking_status);
                break;
            case OFFLINE:
                statusString = getString(R.string.monitoring_entity_screen_offline_status);
                break;
            default:
                statusString = getString(R.string.monitoring_entity_screen_offline_status);
        }

        return statusString;
    }

    private void expandInformation() {
        deviceInformationExpandedLayout.measure(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        final int targetHeight = deviceInformationExpandedLayout.getMeasuredHeight();

        deviceInformationExpandedLayout.getLayoutParams().height = 1;
        deviceInformationExpandedLayout.setVisibility(View.VISIBLE);

        Animation animation = new Animation() {

            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                deviceInformationExpandedLayout.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                deviceInformationExpandedLayout.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        animation.setDuration(400);
        deviceInformationExpandedLayout.startAnimation(animation);

        informationExpanded = true;
    }

    private void collapseInformation() {
        final int initialHeight = deviceInformationExpandedLayout.getMeasuredHeight();

        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) deviceInformationExpandedLayout.setVisibility(View.GONE);
                else {
                    deviceInformationExpandedLayout.getLayoutParams().height =
                            initialHeight - (int) (initialHeight * interpolatedTime);
                    deviceInformationExpandedLayout.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        animation.setDuration(400);
        deviceInformationExpandedLayout.startAnimation(animation);

        informationExpanded = false;
    }

    //// Control callbacks

    private void onTrafficButtonClick() {
        mapAdapter.toggleTraffic();
    }

    private void onZoomInButtonClick() {
        mapAdapter.zoomIn();
    }

    private void onZoomOutButtonClick() {
        mapAdapter.zoomOut();
    }

    private void onCenterOnActiveButtonClick() {
        mapAdapter.centerOnCoordinatesAnimated(
                activeMonitoringEntity.getLatitude(),
                activeMonitoringEntity.getLongitude());
    }

    private void onMenuButtonClick() {
        slider.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
    }

    private void onClickKgkMapMenuButton() {
        mapAdapter.setMapType(MapType.KGK);
        presenter.saveDefaultMapType(MapType.KGK);
        changeActiveMenuMapButton(kgkMapMenuButton);
    }

    private void onClickYandexMapMenuButton() {
        mapAdapter.setMapType(MapType.YANDEX);
        presenter.saveDefaultMapType(MapType.YANDEX);
        changeActiveMenuMapButton(yandexMapMenuButton);
    }

    private void onClickGoogleMapMenuButton() {
        mapAdapter.setMapType(MapType.GOOGLE);
        presenter.saveDefaultMapType(MapType.GOOGLE);
        changeActiveMenuMapButton(googleMapMenuButton);
    }

    private void onClickSatelliteMapMenuButton() {
        mapAdapter.setMapType(MapType.SATELLITE);
        presenter.saveDefaultMapType(MapType.SATELLITE);
        changeActiveMenuMapButton(satelliteMapMenuButton);
    }

    private void onChooseVehicleButtonClick() {
        Intent intent = new Intent(MapActivity.this, MonitoringListActivity.class);
        startActivity(intent);
    }

    private void onChooseVehicleGroupMenuButtonClick() {
        Intent intent = new Intent(MapActivity.this, MonitoringGroupListActivity.class);
        startActivity(intent);
    }

    private void onProfileMenuButtonClick() {
        Intent intent = new Intent(MapActivity.this, ProfileActivity.class);
        startActivity(intent);
    }

    private void onRouteReportSettingsMenuButtonClick() {
        Intent intent = new Intent(this, RouteReportSettingsActivity.class);
        startActivity(intent);
    }

    private void onHelpMenuButtonClick() {
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }

    private void onAboutMenuButtonClick() {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    private void onSettingsMenuButtonClick() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void onHideMenuButtonClick() {
        slider.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
    }

    private void onQuickReportButtonClick() {
        if (!presenter.isInternetAvailable()) {
            notifyNoInternetAvailable();
            return;
        }

        toggleProgressDialog(true);
        presenter.requestQuickReport();
    }

    private void onExpandInfrormationButtonClick() {
        if (informationExpanded) collapseInformation();
        else expandInformation();
    }
}







































