package kgk.beacon.monitoring.domain.interactor;

import kgk.beacon.monitoring.DependencyInjection;
import kgk.beacon.monitoring.data.Configuration;
import kgk.beacon.monitoring.presentation.model.MapType;

public class SetDefaultMapTypeSetting implements Interactor {

    private MapType mapType;

    ////

    public SetDefaultMapTypeSetting(MapType mapType) {
        this.mapType = mapType;
    }

    ////

    @Override
    public void execute() {
        Configuration configuration = DependencyInjection.provideConfiguration();
        configuration.saveDefaultMapType(mapType);
    }
}
