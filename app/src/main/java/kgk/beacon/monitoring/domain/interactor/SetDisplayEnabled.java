package kgk.beacon.monitoring.domain.interactor;

import kgk.beacon.monitoring.DependencyInjection;
import kgk.beacon.monitoring.data.Configuration;
import kgk.beacon.monitoring.domain.model.MonitoringEntity;

public class SetDisplayEnabled implements Interactor {

    private final MonitoringEntity monitoringEntity;
    private final boolean enabled;

    ////

    public SetDisplayEnabled(MonitoringEntity monitoringEntity, boolean enabled) {
        this.monitoringEntity = monitoringEntity;
        this.enabled = enabled;
    }

    ////

    @Override
    public void execute() {
        Configuration configuration = DependencyInjection.provideConfiguration();
        configuration.saveDisplayEnabled(monitoringEntity.getId(), enabled);
    }
}
