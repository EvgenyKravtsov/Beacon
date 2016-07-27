package kgk.beacon.monitoring.domain.model;

import java.util.List;

public class MonitoringManager {

    private static MonitoringManager instance;

    private User user;
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
        user = new User("FakeUser");
        user.setContacts("+1 (111) 111-22-33");
        user.setBalance(666.01);

        this.monitoringEntities = monitoringEntities;
        activeMonitoringEntity = this.monitoringEntities.get(this.monitoringEntities.size() - 1);
    }

    public User getUser() {
        return user;
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
