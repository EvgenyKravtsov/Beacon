package kgk.beacon.monitoring.domain.interactor;

import java.util.HashMap;
import java.util.Map;

import kgk.beacon.monitoring.DependencyInjection;
import kgk.beacon.monitoring.data.Configuration;
import kgk.beacon.monitoring.presentation.model.MapType;
import kgk.beacon.monitoring.presentation.presenter.SettingsViewPresenter;

public class GetConfigurationData implements Interactor {

    public interface Listener {

        void onConfigurationDataRetreived(Map<String, Object> configurationData);
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
        Configuration configuration = DependencyInjection.provideConfiguration();
        MapType defaultMapType = configuration.loadDefaultMapType();
        boolean markerInformationEnabled = configuration.loadMarkerInformationEnabled();

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put(SettingsViewPresenter.DEFAULT_MAP_TYPE_KEY, defaultMapType);
        dataMap.put(SettingsViewPresenter.MERKER_INFORMATION_ENABLED, markerInformationEnabled);

        if (listener != null) listener.onConfigurationDataRetreived(dataMap);
    }
}
