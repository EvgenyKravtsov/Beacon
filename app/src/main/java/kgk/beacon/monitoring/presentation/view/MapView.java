package kgk.beacon.monitoring.presentation.view;

import java.util.List;

import kgk.beacon.monitoring.domain.model.MonitoringEntity;
import kgk.beacon.monitoring.domain.model.routereport.RouteReport;

public interface MapView {

    void showMonitoringEntities(List<MonitoringEntity> monitoringEntities);

    MonitoringEntity getActiveMonitoringEntity();

    void setActiveMonitoringEntity(MonitoringEntity monitoringEntity);

    void toggleCenterOnActiveControl(boolean enabled);

    void monitoringEntityChosen(long id);

    void toggleChooseGroupMenuButton(boolean enabled);

    void navigateToRouteReportView(RouteReport routeReport);
}
