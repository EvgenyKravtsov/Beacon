package kgk.beacon.monitoring.domain.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MonitoringManager {

    private static MonitoringManager instance;

    private User user;
    private List<MonitoringEntityGroup> monitoringEntityGroups;
    private MonitoringEntityGroup activeMonitoringEntityGroup;
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

    public void init(User user, List<MonitoringEntity> monitoringEntities) {
        this.user = user;
        this.monitoringEntities = monitoringEntities;
        this.monitoringEntityGroups = sortMonitoringEntitiesByGroup(monitoringEntities);
    }

    public User getUser() {
        return user;
    }

    public List<MonitoringEntityGroup> getMonitoringEntityGroups() {
        return monitoringEntityGroups;
    }

    public MonitoringEntityGroup getActiveMonitoringEntityGroup() {
        return activeMonitoringEntityGroup;
    }

    public void setActiveMonitoringEntityGroup(MonitoringEntityGroup activeMonitoringEntityGroup) {
        this.activeMonitoringEntityGroup = activeMonitoringEntityGroup;
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

    ////

    private List<MonitoringEntityGroup> sortMonitoringEntitiesByGroup(
            List<MonitoringEntity> monitoringEntities) {

        Set<String> groupNames = new HashSet<>();
        for (MonitoringEntity monitoringEntity : monitoringEntities) {
            for (String groupName : monitoringEntity.getGroupNames()) {
                groupNames.add(groupName);
            }
        }

        List<MonitoringEntityGroup> groups = new ArrayList<>();
        for (String groupName : groupNames) {
            MonitoringEntityGroup group = new MonitoringEntityGroup(groupName);
            groups.add(group);
        }

        for (MonitoringEntity monitoringEntity : monitoringEntities) {
            for (MonitoringEntityGroup group : groups) {
                if (monitoringEntity.getGroupNames().contains(group.getName())) {
                    group.add(monitoringEntity);
                }
            }
        }

        return groups;
    }
}
