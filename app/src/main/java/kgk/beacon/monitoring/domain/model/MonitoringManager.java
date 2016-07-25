package kgk.beacon.monitoring.domain.model;

import java.util.List;

public class MonitoringManager {

    private static MonitoringManager instance;

    private List<MonitoringEntity> monitoringEntities;
    private MonitoringEntity activeMonitoringEntity;

    ////

    private MonitoringManager() {}

    public static MonitoringManager getInstance() {
        if (instance == null) {
            instance = new MonitoringManager();
        }
        return instance;
    }

    ////

    public void init(List<MonitoringEntity> monitoringEntities) {
        this.monitoringEntities = monitoringEntities;
        activeMonitoringEntity = this.monitoringEntities.get(this.monitoringEntities.size() - 1);
    }

    public List<MonitoringEntity> getMonitoringEntities() {
        return monitoringEntities;
    }

    public MonitoringEntity getActiveMonitoringEntity() {
        return activeMonitoringEntity;
    }

    public void setActiveMonitoringEntity(MonitoringEntity activeMonitoringEntity) {
        this.activeMonitoringEntity = activeMonitoringEntity;
    }
}
