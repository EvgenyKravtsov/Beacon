package kgk.beacon.monitoring.presentation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.MapView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.List;

import kgk.beacon.R;
import kgk.beacon.monitoring.DependencyInjection;
import kgk.beacon.monitoring.data.Configuration;
import kgk.beacon.monitoring.domain.model.MonitoringEntity;
import kgk.beacon.monitoring.domain.model.routereport.RouteReport;
import kgk.beacon.monitoring.presentation.adapter.GoogleMapAdapter;
import kgk.beacon.monitoring.presentation.adapter.MapAdapter;
import kgk.beacon.monitoring.presentation.model.MapType;
import kgk.beacon.monitoring.presentation.presenter.MapViewPresenter;

public class MapActivity extends AppCompatActivity implements
        kgk.beacon.monitoring.presentation.view.MapView {

    // Views
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
    private TextView activeTextView;
    private ScrollView menuButtonsLayout;
    private ImageButton quickReportButton;

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
        //menuLayout.setVisibility(View.GONE);
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

    @Override
    public void setActiveMonitoringEntity(MonitoringEntity monitoringEntity) {
        activeMonitoringEntity = monitoringEntity;
        mapAdapter.centerOnCoordinatesAnimated(
                activeMonitoringEntity.getLatitude(),
                activeMonitoringEntity.getLongitude()
        );

        activeTextView.setText(activeMonitoringEntity.getStateNumber());
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
    }

    ////

    private void initViews(Bundle savedInstanceState) {
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

        activeTextView = (TextView) findViewById(R.id.monitoring_activity_active_text_view);

        quickReportButton = (ImageButton)
                findViewById(R.id.monitoring_activity_quick_report_button);
    }

    private void initListeners() {
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

        activeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onActiveTextViewClick();
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
                onCLickSatelliteMapMenuButton();
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

    private void onCLickSatelliteMapMenuButton() {
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

    private void onActiveTextViewClick() {
        Intent intent = new Intent(this, MonitoringEntityActivity.class);
        startActivity(intent);
    }

    private void onQuickReportButtonClick() {
        // TODO Show download indicator
        presenter.requestQuickReport();
    }
}







































