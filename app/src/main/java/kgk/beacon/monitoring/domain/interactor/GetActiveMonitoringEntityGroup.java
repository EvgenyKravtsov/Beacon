package kgk.beacon.monitoring.domain.interactor;

import kgk.beacon.monitoring.domain.model.MonitoringEntityGroup;
import kgk.beacon.monitoring.domain.model.MonitoringManager;

public class GetActiveMonitoringEntityGroup implements Interactor {

    public interface Listener {

        void onActiveMonitoringEntityGroupRetreived(MonitoringEntityGroup group);
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
        MonitoringEntityGroup group = monitoringManager.getActiveMonitoringEntityGroup();
        if (listener != null) listener.onActiveMonitoringEntityGroupRetreived(group);
    }
}
