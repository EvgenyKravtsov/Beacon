package kgk.beacon.monitoring.presentation.activity;

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
    private Button hideMenuButton;
    private Button chooseVehicleMenuButton;
    private TextView activeTextView;
    private LinearLayout menuLayout;

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
            mapAdapter.clearMap();
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

    ////

    private void initViews(Bundle savedInstanceState) {
        googleMapView = (MapView) findViewById(R.id.monitoring_activity_map_google_map);
        assert googleMapView != null;
        googleMapView.onCreate(savedInstanceState);
        googleMapView.onResume();

        trafficButton = (Button) findViewById(R.id.monitoring_activity_map_traffic_button);
        zoomInButton = (Button) findViewById(R.id.monitoring_activity_map_zoom_in_button);
        zoomOutButton = (Button) findViewById(R.id.monitoring_activity_map_zoom_out_button);
        menuButton = (Button) findViewById(R.id.monitoring_activity_menu_button);
        hideMenuButton = (Button) findViewById(R.id.monitoring_activity_menu_hide_button);

        chooseVehicleMenuButton = (Button)
                findViewById(R.id.monitoring_activity_menu_choose_vehicle_button);
        centerOnActiveButton = (Button)
                findViewById(R.id.monitoring_activity_center_on_active_button);

        activeTextView = (TextView) findViewById(R.id.monitoring_activity_active_text_view);
        menuLayout = (LinearLayout) findViewById(R.id.monitoring_activity_menu_layout);
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
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMenuButtonClick();
            }
        });
        hideMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onHideMenuButtonClick();
            }
        });
        chooseVehicleMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChooseVehicleButtonClick();
            }
        });
    }

    private void initMap() {
        Configuration configuration = DependencyInjection.provideConfiguration();
        MapType mapType = configuration.loadDefaultMapType();

        switch (mapType) {
            case GOOGLE:
                googleMapView.setVisibility(View.VISIBLE);
                mapAdapter = new GoogleMapAdapter(this, googleMapView);
                break;
            default:
                googleMapView.setVisibility(View.VISIBLE);
                mapAdapter = new GoogleMapAdapter(this, googleMapView);
        }
    }

    private void bindPresenter() {
        presenter = new MapViewPresenter(this);
    }

    private void unbindPresenter() {
        if (presenter != null) presenter.unbindView();
        presenter = null;
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

    private void onHideMenuButtonClick() {
        menuEnabled = !menuEnabled;
        int visibility = menuEnabled ? View.VISIBLE : View.GONE;
        menuLayout.setVisibility(visibility);
    }

    private void onChooseVehicleButtonClick() {
        menuEnabled = false;
        Intent intent = new Intent(MapActivity.this, MonitoringListActivity.class);
        startActivity(intent);
    }
}






































