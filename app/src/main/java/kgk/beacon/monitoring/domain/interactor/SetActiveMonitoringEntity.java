package kgk.beacon.monitoring.domain.interactor;

import kgk.beacon.monitoring.DependencyInjection;
import kgk.beacon.monitoring.domain.model.MonitoringEntity;
import kgk.beacon.monitoring.domain.model.MonitoringManager;

public class SetActiveMonitoringEntity implements Interactor {

    private final MonitoringEntity monitoringEntity;

    ////

    public SetActiveMonitoringEntity(MonitoringEntity monitoringEntity) {
        this.monitoringEntity = monitoringEntity;
    }

    ////

    @Override
    public void execute() {
        MonitoringManager monitoringManager = MonitoringManager.getInstance();
        monitoringManager.setActiveMonitoringEntity(monitoringEntity);
        DependencyInjection.provideConfiguration().saveActiveMonitoringEntity(monitoringEntity.getId());
    }
}
