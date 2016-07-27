package kgk.beacon.monitoring.domain.interactor;

import java.util.List;

import kgk.beacon.monitoring.domain.model.MonitoringEntity;
import kgk.beacon.monitoring.domain.model.MonitoringManager;

public class GetMonitoringEntityById implements Interactor {

    public interface Listener {

        void onMonitoringEntityRetreived(MonitoringEntity monitoringEntity);
    }

    ////

    private final long id;
    private final MonitoringManager monitoringManager;

    private Listener listener;

    ////

    public GetMonitoringEntityById(long id, MonitoringManager monitoringManager) {
        this.id = id;
        this.monitoringManager = monitoringManager;
    }

    ////

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    ////

    @Override
    public void execute() {
        List<MonitoringEntity> monitoringEntities = monitoringManager.getMonitoringEntities();
        for (MonitoringEntity monitoringEntity : monitoringEntities) {
            if (monitoringEntity.getId() == id) {
                if (listener != null) listener.onMonitoringEntityRetreived(monitoringEntity);
            }
        }
    }
}
