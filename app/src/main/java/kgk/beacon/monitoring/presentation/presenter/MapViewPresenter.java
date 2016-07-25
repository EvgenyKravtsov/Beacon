package kgk.beacon.monitoring.presentation.presenter;

import java.util.ArrayList;
import java.util.List;

import kgk.beacon.monitoring.domain.interactor.GetActiveMonitoringEntity;
import kgk.beacon.monitoring.domain.interactor.GetMonitoringEntities;
import kgk.beacon.monitoring.domain.interactor.InteractorThreadPool;
import kgk.beacon.monitoring.domain.model.MonitoringEntity;
import kgk.beacon.monitoring.presentation.model.MapEntity;
import kgk.beacon.monitoring.presentation.view.MapView;
import kgk.beacon.util.AppController;

public class MapViewPresenter implements
        GetMonitoringEntities.Listener,
        GetActiveMonitoringEntity.Listener {

    private MapView view;

    ////

    public MapViewPresenter(MapView mapView) {
        this.view = mapView;
    }

    ////

    public void unbindView() {
        this.view = null;
    }

    public void requestMapEntities() {
        GetMonitoringEntities getMonitoringEntities = new GetMonitoringEntities();
        getMonitoringEntities.setListener(this);
        InteractorThreadPool.getInstance().execute(getMonitoringEntities);
    }

    public void requestActiveMapEntity() {
        GetActiveMonitoringEntity getActiveMonitoringEntity = new GetActiveMonitoringEntity();
        getActiveMonitoringEntity.setListener(this);
        InteractorThreadPool.getInstance().execute(getActiveMonitoringEntity);
    }

    ////

    @Override
    public void onMonitoringEntitiesRetreived(List<MonitoringEntity> monitoringEntities) {
        final List<MapEntity> mapEntities = new ArrayList<>();
        for (MonitoringEntity monitoringEntity : monitoringEntities) {
            mapEntities.add(new MapEntity(monitoringEntity));
        }

        AppController.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.showMapEntities(mapEntities);
            }
        });

        requestActiveMapEntity();
    }

    @Override
    public void onActiveMonitoringEntityRetreived(final MonitoringEntity activeMonitoringEntity) {
        AppController.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.centerOnMapEntity(new MapEntity(activeMonitoringEntity));
            }
        });
    }
}
