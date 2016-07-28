package kgk.beacon.monitoring.presentation.presenter;

import java.util.List;

import kgk.beacon.monitoring.DependencyInjection;
import kgk.beacon.monitoring.domain.interactor.GetActiveMonitoringEntity;
import kgk.beacon.monitoring.domain.interactor.GetMonitoringEntities;
import kgk.beacon.monitoring.domain.interactor.InteractorThreadPool;
import kgk.beacon.monitoring.domain.model.MonitoringEntity;
import kgk.beacon.monitoring.presentation.view.MonitoringListView;
import kgk.beacon.util.AppController;

public class MonitoringListViewPresenter implements
        GetMonitoringEntities.Listener,
        GetActiveMonitoringEntity.Listener {

    private MonitoringListView view;
    private List<MonitoringEntity> monitoringEntities;

    ////

    public MonitoringListViewPresenter(MonitoringListView view) {
        this.view = view;
    }

    ////

    public void unbindView() {
        view = null;
    }

    public void requestMonitoringEntities() {
        GetMonitoringEntities interactor =
                new GetMonitoringEntities(DependencyInjection.provideMonitoringManager());
        interactor.setListener(this);
        InteractorThreadPool.getInstance().execute(interactor);
    }

    ////

    @Override
    public void onMonitoringEntitiesRetreived(final List<MonitoringEntity> monitoringEntities) {
        MonitoringListViewPresenter.this.monitoringEntities = monitoringEntities;
        requestActiveMonitroingEntity();
    }

    @Override
    public void onActiveMonitoringEntityRetreived(final MonitoringEntity activeMonitoringEntity) {
        AppController.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.showMonitoringEntities(monitoringEntities, activeMonitoringEntity);
            }
        });
    }

    ////

    private void requestActiveMonitroingEntity() {
        GetActiveMonitoringEntity interactor = new GetActiveMonitoringEntity();
        interactor.setListener(this);
        InteractorThreadPool.getInstance().execute(interactor);
    }
}
