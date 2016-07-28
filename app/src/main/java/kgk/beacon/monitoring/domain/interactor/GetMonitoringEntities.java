package kgk.beacon.monitoring.domain.interactor;

import java.util.List;

import kgk.beacon.monitoring.domain.model.MonitoringEntity;
import kgk.beacon.monitoring.domain.model.MonitoringManager;

public class GetMonitoringEntities implements Interactor {

    public interface Listener {

        void onMonitoringEntitiesRetreived(List<MonitoringEntity> monitoringEntities);
    }

    ////

    private final MonitoringManager monitoringManager;

    private Listener listener;

    ////

    public GetMonitoringEntities(MonitoringManager monitoringManager) {
        this.monitoringManager = monitoringManager;
    }

    ////

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    ////

    @Override
    public void execute() {
        List<MonitoringEntity> monitoringEntities;
        if (monitoringManager.getActiveMonitoringEntityGroup() == null) {
            monitoringEntities = monitoringManager.getMonitoringEntities();
        } else {
            monitoringEntities = monitoringManager
                    .getActiveMonitoringEntityGroup()
                    .getMonitoringEntities();
        }

        if (listener != null) listener.onMonitoringEntitiesRetreived(monitoringEntities);
    }
}
