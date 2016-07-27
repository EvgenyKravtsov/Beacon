package kgk.beacon.monitoring.domain.interactor;

import kgk.beacon.monitoring.DependencyInjection;
import kgk.beacon.monitoring.data.Configuration;

public class SetMarkerInformationEnabledSetting implements Interactor {

    private boolean markerInformationEnabled;

    ////

    public SetMarkerInformationEnabledSetting(boolean markerInformationEnabled) {
        this.markerInformationEnabled = markerInformationEnabled;
    }

    ////

    @Override
    public void execute() {
        Configuration configuration = DependencyInjection.provideConfiguration();
        configuration.saveMarkerInformationEnabled(markerInformationEnabled);
    }
}
