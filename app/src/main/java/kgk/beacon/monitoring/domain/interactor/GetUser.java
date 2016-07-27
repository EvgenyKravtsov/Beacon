package kgk.beacon.monitoring.domain.interactor;

import kgk.beacon.monitoring.domain.model.MonitoringManager;
import kgk.beacon.monitoring.domain.model.User;

public class GetUser implements Interactor {

    public interface Listener {

        void onUserRetreived(User user);
    }

    ////

    private Listener listener;

    ////

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void execute() {
        MonitoringManager monitoringManager = MonitoringManager.getInstance();
        User user = monitoringManager.getUser();
        if (listener != null) listener.onUserRetreived(user);
    }
}
