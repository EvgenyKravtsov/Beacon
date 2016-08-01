package kgk.beacon.monitoring.domain.interactor;

import kgk.beacon.monitoring.DependencyInjection;
import kgk.beacon.monitoring.domain.model.MonitoringEntityGroup;
import kgk.beacon.monitoring.domain.model.MonitoringManager;

public class SetActiveMonitoringEntityGroup implements Interactor {

    private final MonitoringEntityGroup monitoringEntityGroup;

    ////

    public SetActiveMonitoringEntityGroup(MonitoringEntityGroup monitoringEntityGroup) {
        this.monitoringEntityGroup = monitoringEntityGroup;
    }

    ////

    @Override
    public void execute() {
        MonitoringManager monitoringManager = MonitoringManager.getInstance();
        monitoringManager.setActiveMonitoringEntityGroup(monitoringEntityGroup);
        DependencyInjection.provideConfiguration()
                .saveActiveMonitoringEntityGroup(monitoringEntityGroup.getName());
    }
}
