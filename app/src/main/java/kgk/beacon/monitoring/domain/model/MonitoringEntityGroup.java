package kgk.beacon.monitoring.domain.model;

import java.util.ArrayList;
import java.util.List;

public class MonitoringEntityGroup {

    private final String name;
    private final List<MonitoringEntity> monitoringEntities;

    ////

    public MonitoringEntityGroup(String name) {
        this.name = name;
        this.monitoringEntities = new ArrayList<>();
    }

    ////

    public void add(MonitoringEntity monitoringEntity) {
        monitoringEntities.add(monitoringEntity);
    }
}
