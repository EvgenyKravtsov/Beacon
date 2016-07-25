package kgk.beacon.monitoring.domain.interactor;

import kgk.beacon.monitoring.domain.model.MonitoringEntity;
import kgk.beacon.monitoring.domain.model.MonitoringManager;

public class GetActiveMonitoringEntity implements Interactor {

    public interface Listener {

        void onActiveMonitoringEntityRetreived(MonitoringEntity activeMonitoringEntity);
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
        MonitoringEntity activeMonitoringEntity = monitoringManager.getActiveMonitoringEntity();
        if (listener != null) listener.onActiveMonitoringEntityRetreived(activeMonitoringEntity);
    }
}

