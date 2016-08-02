package kgk.beacon.monitoring.domain.interactor;

import java.util.List;

import kgk.beacon.monitoring.MonitoringHttpClient;
import kgk.beacon.monitoring.domain.model.MonitoringEntity;
import kgk.beacon.monitoring.domain.model.MonitoringManager;
import kgk.beacon.monitoring.domain.model.RouteReport;
import kgk.beacon.monitoring.domain.model.User;
import kgk.beacon.networking.VolleyHttpClient;
import kgk.beacon.util.AppController;

public class UpdateMonitoringEntities implements Interactor {

    public interface Listener {

        void onMonitoringEntitiesUpdated(List<MonitoringEntity> monitoringEntities);
    }

    ////

    private Listener listener;
    private List<MonitoringEntity> monitoringEntities;

    ////

    public UpdateMonitoringEntities() {}

    public UpdateMonitoringEntities(List<MonitoringEntity> monitoringEntities) {
        this.monitoringEntities = monitoringEntities;
    }

    ////

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    ////

    @Override
    public void execute() {
        final MonitoringManager monitoringManager = MonitoringManager.getInstance();
        MonitoringHttpClient monitoringHttpClient = VolleyHttpClient.getInstance(
                AppController.getInstance());

        monitoringHttpClient.setListener(new MonitoringHttpClient.Listener() {
            @Override
            public void onUserRetreived(User user) {

            }

            @Override
            public void onMonitoringEntitiesUpdated() {
                if (listener != null) listener.onMonitoringEntitiesUpdated(
                        monitoringManager.getActiveMonitoringEntityGroup() == null ?
                        monitoringManager.getMonitoringEntities() :
                        monitoringManager.getActiveMonitoringEntityGroup()
                                .getMonitoringEntities()
                );
            }

            @Override
            public void onRouteReportReceived(RouteReport routeReport) {

            }
        });

        if (monitoringEntities != null) {
            monitoringHttpClient.updateMonitoringEntities(monitoringEntities);
            return;
        }

        monitoringHttpClient.updateMonitoringEntities(
                monitoringManager.getActiveMonitoringEntityGroup() == null ?
                monitoringManager.getMonitoringEntities() :
                monitoringManager.getActiveMonitoringEntityGroup()
                        .getMonitoringEntities());
    }
}
