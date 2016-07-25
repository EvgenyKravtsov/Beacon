package kgk.beacon.monitoring.domain.interactor;

import java.util.List;

import kgk.beacon.monitoring.domain.model.MonitoringEntity;
import kgk.beacon.monitoring.domain.model.MonitoringManager;

public class GetMonitoringEntities implements Interactor {

    public interface Listener {

        void onMonitoringEntitiesRetreived(List<MonitoringEntity> monitoringEntities);
    }

    private Listener listener;

    ////

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    ////

    @Override
    public void execute() {
        MonitoringManager monitoringManager = MonitoringManager.getInstance();
        List<MonitoringEntity> monitoringEntities = monitoringManager.getMonitoringEntities();
        if (listener != null) listener.onMonitoringEntitiesRetreived(monitoringEntities);
    }
}
