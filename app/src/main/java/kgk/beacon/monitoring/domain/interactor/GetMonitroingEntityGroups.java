package kgk.beacon.monitoring.domain.interactor;

import java.util.List;

import kgk.beacon.monitoring.domain.model.MonitoringEntityGroup;
import kgk.beacon.monitoring.domain.model.MonitoringManager;

public class GetMonitroingEntityGroups implements Interactor {

    public interface Listener {

        void onMonitoringEntityGroupsRetreived(List<MonitoringEntityGroup> groups);
    }

    ////

    private Listener listener;

    ////

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    ////

    @Override
    public void execute() {
        MonitoringManager monitoringManager = MonitoringManager.getInstance();
        List<MonitoringEntityGroup> groups = monitoringManager.getMonitoringEntityGroups();
        if (listener != null) listener.onMonitoringEntityGroupsRetreived(groups);
    }
}
