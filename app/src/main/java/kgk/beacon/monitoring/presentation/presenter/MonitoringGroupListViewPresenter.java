package kgk.beacon.monitoring.presentation.presenter;

import java.util.List;

import kgk.beacon.monitoring.domain.interactor.GetActiveMonitoringEntityGroup;
import kgk.beacon.monitoring.domain.interactor.GetMonitroingEntityGroups;
import kgk.beacon.monitoring.domain.interactor.InteractorThreadPool;
import kgk.beacon.monitoring.domain.model.MonitoringEntityGroup;
import kgk.beacon.monitoring.presentation.view.MonitoringGroupListView;
import kgk.beacon.util.AppController;

public class MonitoringGroupListViewPresenter
        implements GetMonitroingEntityGroups.Listener,
                GetActiveMonitoringEntityGroup.Listener {

    private MonitoringGroupListView view;
    private List<MonitoringEntityGroup> groups;

    ////

    public MonitoringGroupListViewPresenter(MonitoringGroupListView view) {
        this.view = view;
    }

    ////

    public void unbindView() {
        view = null;
    }

    public void requestMonitoringEntityGroups() {
        GetMonitroingEntityGroups interactor = new GetMonitroingEntityGroups();
        interactor.setListener(this);
        InteractorThreadPool.getInstance().execute(interactor);
    }

    ////

    @Override
    public void onMonitoringEntityGroupsRetreived(List<MonitoringEntityGroup> groups) {
        this.groups = groups;
        requestActiveMonitoringEntityGroup();
    }

    @Override
    public void onActiveMonitoringEntityGroupRetreived(final MonitoringEntityGroup group) {
        AppController.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.showMonitoringEntityGroups(groups, group);
            }
        });
    }

    ////

    private void requestActiveMonitoringEntityGroup() {
        GetActiveMonitoringEntityGroup interactor = new GetActiveMonitoringEntityGroup();
        interactor.setListener(this);
        InteractorThreadPool.getInstance().execute(interactor);
    }
}
