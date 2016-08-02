package kgk.beacon.monitoring;

import java.util.List;

import kgk.beacon.monitoring.domain.model.MonitoringEntity;
import kgk.beacon.monitoring.domain.model.RouteReport;
import kgk.beacon.monitoring.domain.model.RouteReportParameters;
import kgk.beacon.monitoring.domain.model.User;

public interface MonitoringHttpClient {

    interface Listener {

        void onUserRetreived(User user);

        void onMonitoringEntitiesUpdated();

        void onRouteReportReceived(RouteReport routeReport);
    }

    ////

    void setListener(Listener listener);

    void requestUser();

    void updateMonitoringEntities(List<MonitoringEntity> monitoringEntities);

    void requestRouteReport(RouteReportParameters parameters);
}
