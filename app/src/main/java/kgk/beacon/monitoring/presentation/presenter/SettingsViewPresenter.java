package kgk.beacon.monitoring.presentation.presenter;

import java.util.Map;

import kgk.beacon.monitoring.domain.interactor.GetConfigurationData;
import kgk.beacon.monitoring.domain.interactor.InteractorThreadPool;
import kgk.beacon.monitoring.domain.interactor.SetDefaultMapTypeSetting;
import kgk.beacon.monitoring.domain.interactor.SetMarkerInformationEnabledSetting;
import kgk.beacon.monitoring.presentation.model.MapType;
import kgk.beacon.monitoring.presentation.view.SettingsView;
import kgk.beacon.util.AppController;

public class SettingsViewPresenter implements GetConfigurationData.Listener {

    public static final String DEFAULT_MAP_TYPE_KEY = "default_map_type";
    public static final String MERKER_INFORMATION_ENABLED = "marker_information_enabled";

    private SettingsView view;

    ////

    public SettingsViewPresenter(SettingsView view) {
        this.view = view;
    }

    ////

    public void unbindView() {
        view = null;
    }

    public void requestSettingsData() {
        GetConfigurationData interactor = new GetConfigurationData();
        interactor.setListener(this);
        InteractorThreadPool.getInstance().execute(interactor);
    }

    public void saveDefaultMapType(MapType mapType) {
        SetDefaultMapTypeSetting interactor = new SetDefaultMapTypeSetting(mapType);
        InteractorThreadPool.getInstance().execute(interactor);
    }

    public void saveMarkerInformationEnabled(boolean enabled) {
        SetMarkerInformationEnabledSetting interactor = new SetMarkerInformationEnabledSetting(enabled);
        InteractorThreadPool.getInstance().execute(interactor);
    }

    ////

    @Override
    public void onConfigurationDataRetreived(final Map<String, Object> configurationData) {
        AppController.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.showSettings(configurationData);
            }
        });
    }
}
