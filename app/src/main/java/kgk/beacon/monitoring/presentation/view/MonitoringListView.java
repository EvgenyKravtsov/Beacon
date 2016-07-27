package kgk.beacon.monitoring.presentation.view;

import java.util.List;

import kgk.beacon.monitoring.domain.model.MonitoringEntity;

public interface MonitoringListView {

    void showMonitoringEntities(List<MonitoringEntity> monitoringEntities);
}
