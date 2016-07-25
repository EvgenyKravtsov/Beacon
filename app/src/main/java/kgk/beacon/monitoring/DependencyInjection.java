package kgk.beacon.monitoring;

import kgk.beacon.monitoring.data.Configuration;
import kgk.beacon.monitoring.data.SharedPreferencesDao;

public class DependencyInjection {

    public static Configuration provideConfiguration() {
        return new SharedPreferencesDao();
    }
}
