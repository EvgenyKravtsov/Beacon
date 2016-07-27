package kgk.beacon.monitoring.presentation.presenter;

import java.util.List;

import kgk.beacon.monitoring.domain.interactor.GetMonitoringEntities;
import kgk.beacon.monitoring.domain.interactor.InteractorThreadPool;
import kgk.beacon.monitoring.domain.model.MonitoringEntity;
import kgk.beacon.monitoring.presentation.view.MonitoringListView;
import kgk.beacon.util.AppController;

public class MonitoringListViewPresenter implements GetMonitoringEntities.Listener {

    private MonitoringListView view;

    ////

    public MonitoringListViewPresenter(MonitoringListView view) {
        this.view = view;
    }

    ////

    public void unbindView() {
        view = null;
    }

    public void requestMonitoringEntities() {
        GetMonitoringEntities interactor = new GetMonitoringEntities();
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
    }
}
