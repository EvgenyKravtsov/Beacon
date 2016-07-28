package kgk.beacon.monitoring.presentation.presenter;

import java.util.List;

import kgk.beacon.monitoring.DependencyInjection;
import kgk.beacon.monitoring.domain.interactor.GetActiveMonitoringEntity;
import kgk.beacon.monitoring.domain.interactor.GetMonitoringEntities;
import kgk.beacon.monitoring.domain.interactor.GetMonitoringEntityById;
import kgk.beacon.monitoring.domain.interactor.GetMonitroingEntityGroups;
import kgk.beacon.monitoring.domain.interactor.InteractorThreadPool;
import kgk.beacon.monitoring.domain.model.MonitoringEntity;
import kgk.beacon.monitoring.domain.model.MonitoringEntityGroup;
import kgk.beacon.monitoring.presentation.view.MapView;
import kgk.beacon.util.AppController;

public class MapViewPresenter implements
        GetMonitoringEntities.Listener,
        GetActiveMonitoringEntity.Listener,
        GetMonitoringEntityById.Listener,
        GetMonitroingEntityGroups.Listener {

    private MapView view;

    ////

    public MapViewPresenter(MapView mapView) {
        this.view = mapView;
    }

    ////

    public void unbindView() {
        this.view = null;
    }

    public void requestMonitoringEntities() {
        GetMonitoringEntities interactor =
                new GetMonitoringEntities(DependencyInjection.provideMonitoringManager());
        interactor.setListener(this);
        InteractorThreadPool.getInstance().execute(interactor);
    }

    public void requestActiveMonitoringEntity() {
        GetActiveMonitoringEntity interactor = new GetActiveMonitoringEntity();
        interactor.setListener(this);
        InteractorThreadPool.getInstance().execute(interactor);
    }

    public void requestMonitoringEntityById(long id) {
        GetMonitoringEntityById interactor = new GetMonitoringEntityById(
                id,
                DependencyInjection.provideMonitoringManager()
        );

        interactor.setListener(this);
        InteractorThreadPool.getInstance().execute(interactor);
    }

    public void requestMonitoringEntityGroupsCount() {
        GetMonitroingEntityGroups interactor = new GetMonitroingEntityGroups();
        interactor.setListener(this);
        InteractorThreadPool.getInstance().execute(interactor);
    }

    ////

    @Override
    public void onMonitoringEntitiesRetreived(final List<MonitoringEntity> monitoringEntities) {
        AppController.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.showMonitoringEntities(monitoringEntities);
            }
        });

        requestActiveMonitoringEntity();
    }

    @Override
    public void onActiveMonitoringEntityRetreived(final MonitoringEntity activeMonitoringEntity) {
        AppController.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.setActiveMonitoringEntity(activeMonitoringEntity);
            }
        });
    }

    @Override
    public void onMonitoringEntityRetreived(final MonitoringEntity monitoringEntity) {
        DependencyInjection.provideMonitoringManager().setActiveMonitoringEntity(monitoringEntity);
        AppController.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.setActiveMonitoringEntity(monitoringEntity);
            }
        });
    }

    @Override
    public void onMonitoringEntityGroupRetreived(List<MonitoringEntityGroup> groups) {
        final int count = groups.size();

        AppController.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.toggleChooseGroupMenuButton(count > 1);
            }
        });
    }
}
