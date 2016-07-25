package kgk.beacon.monitoring.presentation.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.MapView;

import java.util.List;

import kgk.beacon.R;
import kgk.beacon.monitoring.DependencyInjection;
import kgk.beacon.monitoring.data.Configuration;
import kgk.beacon.monitoring.presentation.adapter.GoogleMapAdapter;
import kgk.beacon.monitoring.presentation.adapter.MapAdapter;
import kgk.beacon.monitoring.presentation.model.MapEntity;
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

    private MapViewPresenter presenter;
    private MapAdapter mapAdapter;

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
        presenter.requestMapEntities();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindPresenter();
    }

    ////

    @Override
    public void showMapEntities(List<MapEntity> mapEntities) {
        if (mapEntities != null && mapEntities.size() > 0) {
            for (MapEntity mapEntity : mapEntities) mapAdapter.showMapEntity(mapEntity);
        }
    }

    @Override
    public void centerOnMapEntity(MapEntity mapEntity) {
        mapAdapter.centerOnCoordinates(mapEntity.getLatitude(), mapEntity.getLongitude());
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

        centerOnActiveButton = (Button)
                findViewById(R.id.monitoring_activity_center_on_active_button);
    }

    private void initListeners() {
        trafficButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapAdapter.toggleTraffic();
            }
        });
        zoomInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapAdapter.zoomIn();
            }
        });
        zoomOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapAdapter.zoomOut();
            }
        });
        centerOnActiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.requestActiveMapEntity();
            }
        });
    }

    private void initMap() {
        Configuration configuration = DependencyInjection.provideConfiguration();
        MapType mapType = configuration.loadDefaultMapType();

        switch (mapType) {
            case GOOGLE:
                googleMapView.setVisibility(View.VISIBLE);
                mapAdapter = new GoogleMapAdapter(googleMapView);
                break;
            default:
                googleMapView.setVisibility(View.VISIBLE);
                mapAdapter = new GoogleMapAdapter(googleMapView);
        }
    }

    private void bindPresenter() {
        presenter = new MapViewPresenter(this);
    }

    private void unbindPresenter() {
        if (presenter != null) presenter.unbindView();
        presenter = null;
    }
}
