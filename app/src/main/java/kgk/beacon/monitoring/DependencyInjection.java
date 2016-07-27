package kgk.beacon.monitoring;

import kgk.beacon.monitoring.data.Configuration;
import kgk.beacon.monitoring.data.SharedPreferencesDao;
import kgk.beacon.monitoring.domain.model.MonitoringManager;

public class DependencyInjection {

    public static Configuration provideConfiguration() {
        return new SharedPreferencesDao();
    }

    public static MonitoringManager provideMonitoringManager() {
        return MonitoringManager.getInstance();
    }
}
