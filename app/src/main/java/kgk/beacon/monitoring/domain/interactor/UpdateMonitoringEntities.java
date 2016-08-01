package kgk.beacon.monitoring.domain.interactor;

import java.util.List;

import kgk.beacon.monitoring.MonitoringHttpClient;
import kgk.beacon.monitoring.domain.model.MonitoringEntity;
import kgk.beacon.monitoring.domain.model.MonitoringManager;
import kgk.beacon.monitoring.domain.model.User;
import kgk.beacon.networking.VolleyHttpClient;
import kgk.beacon.util.AppController;

public class UpdateMonitoringEntities implements Interactor {

    public interface Listener {

        void onMonitoringEntitiesUpdated(List<MonitoringEntity> monitoringEntities);
    }

    ////

    private Listener listener;

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
        });

        monitoringHttpClient.updateMonitoringEntities(
                monitoringManager.getActiveMonitoringEntityGroup() == null ?
                monitoringManager.getMonitoringEntities() :
                monitoringManager.getActiveMonitoringEntityGroup()
                        .getMonitoringEntities());
    }
}
