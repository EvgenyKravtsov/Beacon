package kgk.beacon.monitoring.presentation.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.MapView;

import java.util.List;

import kgk.beacon.R;
import kgk.beacon.monitoring.DependencyInjection;
import kgk.beacon.monitoring.data.Configuration;
import kgk.beacon.monitoring.domain.model.MonitoringEntity;
import kgk.beacon.monitoring.presentation.adapter.GoogleMapAdapter;
import kgk.beacon.monitoring.presentation.adapter.MapAdapter;
import kgk.beacon.monitoring.presentation.model.MapType;
import kgk.beacon.monitoring.presentation.presenter.MapViewPresenter;

public class MapActivity extends AppCompatActivity implements
        kgk.beacon.monitoring.presentation.view.MapView {

    // Views
    private MapView googleMapView;
    private Button trafficButton;
    private Button zoomInButton;
    private Button zoomOutButton;
    private Button centerOnActiveButton;
    private Button menuButton;
    private Button kgkMapMenuButton;
    private Button yandexMapMenuButton;
    private Button googleMapMenuButton;
    private Button satelliteMapMenuButton;
    private Button hideMenuButton;
    private Button chooseVehicleMenuButton;
    private Button chooseVehicleGroupMenuButton;
    private Button profileMenuButton;
    private Button routeReportSettingsMenuButton;
    private Button helpMenuButton;
    private Button aboutMenuButton;
    private Button settingsMenuButton;
    private TextView activeTextView;
    private LinearLayout menuLayout;
    private Button quickReportButton;

    // Dialogs
    private ProgressDialog downloadDataProgressDialog;
    private boolean initialyLoaded;

    private MapViewPresenter presenter;
    private MapAdapter mapAdapter;
    private MonitoringEntity activeMonitoringEntity;
    private boolean menuEnabled;

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

        if (!initialyLoaded) toggleDownloadDataProgressDialog(true);

        menuLayout.setVisibility(View.GONE);
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

        if (!initialyLoaded) {

            Configuration configuration = DependencyInjection.provideConfiguration();
            MapType mapType = configuration.loadDefaultMapType();
            mapAdapter.setMapType(mapType);

            mapAdapter.centerOnCoordinates(
                    activeMonitoringEntity.getLatitude(),
                    activeMonitoringEntity.getLongitude()
            );
        }

        toggleDownloadDataProgressDialog(false);
        initialyLoaded = true;
    }

    @Override
    public MonitoringEntity getActiveMonitoringEntity() {
        return activeMonitoringEntity;
    }

    @Override
    public void setActiveMonitoringEntity(MonitoringEntity monitoringEntity) {
        activeMonitoringEntity = monitoringEntity;
        mapAdapter.centerOnCoordinates(
                activeMonitoringEntity.getLatitude(),
                activeMonitoringEntity.getLongitude()
        );

        activeTextView.setText(activeMonitoringEntity.getStateNumber());
    }

    @Override
    public void toggleCenterOnActiveControl(boolean enabled) {
        int visibility = enabled ? View.VISIBLE : View.GONE;
        centerOnActiveButton.setVisibility(visibility);
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

    ////

    private void initViews(Bundle savedInstanceState) {
        googleMapView = (MapView) findViewById(R.id.monitoring_activity_map_google_map);
        assert googleMapView != null;
        googleMapView.onCreate(savedInstanceState);
        googleMapView.onResume();

        trafficButton = (Button) findViewById(R.id.monitoring_activity_map_traffic_button);
        zoomInButton = (Button) findViewById(R.id.monitoring_activity_map_zoom_in_button);
        zoomOutButton = (Button) findViewById(R.id.monitoring_activity_map_zoom_out_button);

        menuButton = (Button)
                findViewById(R.id.monitoring_activity_menu_button);
        kgkMapMenuButton = (Button)
                findViewById(R.id.monitoring_activity_menu_kgk_map_button);
        yandexMapMenuButton = (Button)
                findViewById(R.id.monitoring_activity_menu_yandex_map_button);
        googleMapMenuButton = (Button)
                findViewById(R.id.monitoring_activity_menu_google_map_button);
        satelliteMapMenuButton = (Button)
                findViewById(R.id.monitoring_activity_menu_satellite_map_button);
        hideMenuButton = (Button)
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

        centerOnActiveButton = (Button)
                findViewById(R.id.monitoring_activity_center_on_active_button);

        activeTextView = (TextView) findViewById(R.id.monitoring_activity_active_text_view);
        menuLayout = (LinearLayout) findViewById(R.id.monitoring_activity_menu_layout);

        quickReportButton = (Button)
                findViewById(R.id.monitoring_activity_quick_report_button);
    }

    private void initListeners() {
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

    private void toggleDownloadDataProgressDialog(boolean enabled) {
        if (enabled) {
            if (downloadDataProgressDialog == null) {
                downloadDataProgressDialog = new ProgressDialog(this);
            }

            downloadDataProgressDialog.setMessage("Downloading data");
            downloadDataProgressDialog.setCanceledOnTouchOutside(false);
            downloadDataProgressDialog.show();

        } else {
            if (downloadDataProgressDialog != null) downloadDataProgressDialog.hide();
        }
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
        mapAdapter.centerOnCoordinates(
                activeMonitoringEntity.getLatitude(),
                activeMonitoringEntity.getLongitude());
    }

    private void onMenuButtonClick() {
        menuEnabled = !menuEnabled;
        int visibility = menuEnabled ? View.VISIBLE : View.GONE;
        menuLayout.setVisibility(visibility);
    }

    private void onClickKgkMapMenuButton() {
        mapAdapter.setMapType(MapType.KGK);
        presenter.saveDefaultMapType(MapType.KGK);
    }

    private void onClickYandexMapMenuButton() {
        mapAdapter.setMapType(MapType.YANDEX);
        presenter.saveDefaultMapType(MapType.YANDEX);
    }

    private void onClickGoogleMapMenuButton() {
        mapAdapter.setMapType(MapType.GOOGLE);
        presenter.saveDefaultMapType(MapType.GOOGLE);
    }

    private void onCLickSatelliteMapMenuButton() {
        mapAdapter.setMapType(MapType.SATELLITE);
        presenter.saveDefaultMapType(MapType.SATELLITE);
    }

    private void onChooseVehicleButtonClick() {
        menuEnabled = false;
        Intent intent = new Intent(MapActivity.this, MonitoringListActivity.class);
        startActivity(intent);
    }

    private void onChooseVehicleGroupMenuButtonClick() {
        menuEnabled = false;
        Intent intent = new Intent(MapActivity.this, MonitoringGroupListActivity.class);
        startActivity(intent);
    }

    private void onProfileMenuButtonClick() {
        menuEnabled = false;
        Intent intent = new Intent(MapActivity.this, ProfileActivity.class);
        startActivity(intent);
    }

    private void onRouteReportSettingsMenuButtonClick() {
        Intent intent = new Intent(this, RouteReportSettingsActivity.class);
        startActivity(intent);
    }

    private void onHelpMenuButtonClick() {
        menuEnabled = false;
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }

    private void onAboutMenuButtonClick() {
        menuEnabled = false;
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    private void onSettingsMenuButtonClick() {
        menuEnabled = false;
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void onHideMenuButtonClick() {
        menuEnabled = !menuEnabled;
        int visibility = menuEnabled ? View.VISIBLE : View.GONE;
        menuLayout.setVisibility(visibility);
    }

    private void onActiveTextViewClick() {
        Intent intent = new Intent(this, MonitoringEntityActivity.class);
        startActivity(intent);
    }

    private void onQuickReportButtonClick() {
        presenter.requestQuickReport();
    }
}







































