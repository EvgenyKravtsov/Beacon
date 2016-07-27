package kgk.beacon.monitoring.presentation.presenter;

import kgk.beacon.monitoring.domain.interactor.GetActiveMonitoringEntity;
import kgk.beacon.monitoring.domain.interactor.InteractorThreadPool;
import kgk.beacon.monitoring.domain.model.MonitoringEntity;
import kgk.beacon.monitoring.presentation.view.MonitoringEntityView;
import kgk.beacon.util.AppController;

public class MonitoringEntityViewPresenter implements GetActiveMonitoringEntity.Listener {

    private MonitoringEntityView view;

    ////

    public MonitoringEntityViewPresenter(MonitoringEntityView view) {
        this.view = view;
    }

    ////

    public void unbindView() {
        view = null;
    }

    public void requestActiveMonitoringEntity() {
        GetActiveMonitoringEntity interactor = new GetActiveMonitoringEntity();
        interactor.setListener(this);
        InteractorThreadPool.getInstance().execute(interactor);
    }

    ////

    @Override
    public void onActiveMonitoringEntityRetreived(final MonitoringEntity activeMonitoringEntity) {
        AppController.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.showActiveMonitoringEntity(activeMonitoringEntity);
            }
        });
    }
}
