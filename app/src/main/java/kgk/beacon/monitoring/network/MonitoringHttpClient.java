package kgk.beacon.monitoring.network;

import java.util.List;

import kgk.beacon.monitoring.domain.model.MonitoringEntity;
import kgk.beacon.monitoring.domain.model.routereport.RouteReport;
import kgk.beacon.monitoring.domain.model.routereport.RouteReportParameters;
import kgk.beacon.monitoring.domain.model.User;

public interface MonitoringHttpClient {

    // TODO Write logic for entity status (Moving, Parking, etc.)

    interface Listener {

        void onUserRetreived(User user);

        void onMonitoringEntitiesUpdated();
    }

    interface RouteReportListener {

        void onRouteReportReceived(RouteReport routeReport);
    }

    ////

    void setListener(Listener listener);

    void setRouteReportListener(RouteReportListener listener);

    void requestUser();

    void updateMonitoringEntities(List<MonitoringEntity> monitoringEntities);

    void requestRouteReport(RouteReportParameters parameters);
}
