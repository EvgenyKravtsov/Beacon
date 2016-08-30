package kgk.beacon.monitoring.network;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import kgk.beacon.dispatcher.Dispatcher;
import kgk.beacon.model.Device;
import kgk.beacon.monitoring.DependencyInjection;
import kgk.beacon.monitoring.data.Configuration;
import kgk.beacon.monitoring.domain.interactor.InteractorThreadPool;
import kgk.beacon.monitoring.domain.interactor.UpdateMonitoringEntities;
import kgk.beacon.monitoring.domain.model.MonitoringEntity;
import kgk.beacon.monitoring.domain.model.MonitoringEntityGroup;
import kgk.beacon.monitoring.domain.model.MonitoringManager;
import kgk.beacon.monitoring.domain.model.User;
import kgk.beacon.networking.VolleyHttpClient;
import kgk.beacon.stores.DeviceStore;
import kgk.beacon.util.AppController;

public class MonitoringManagerInitializer implements
        MonitoringHttpClient.Listener,
        UpdateMonitoringEntities.Listener {

    public interface Listener {

        public void onMonitoringManagerInitialized();
    }

    ////

    private MonitoringManager monitoringManager;
    private Configuration configuration;
    private DeviceStore deviceStore;
    private MonitoringHttpClient monitoringHttpClient;

    private Listener listener;

    ////

    public MonitoringManagerInitializer() {
        monitoringManager = MonitoringManager.getInstance();
        configuration = DependencyInjection.provideConfiguration();
        deviceStore = DeviceStore.getInstance(Dispatcher.getInstance(EventBus.getDefault()));

        monitoringHttpClient = VolleyHttpClient.getInstance(AppController.getInstance());
        monitoringHttpClient.setListener(this);
    }

    ////

    public void init() {
        requestUser();
    }

    ////

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    ////

    @Override
    public void onUserRetreived(User user) {
        monitoringManager.init(user,
                getMonitoringEntitiesFromDeviceList(getDeviceList()));

        setActiveMonitoringEntityGroup();
        setActiveMonitoringEntity();
        requestMonitoringEntitiesUpdate();
    }

    @Override
    public void onMonitoringEntitiesUpdated() {

        // Not required in this implementation
    }

    ////

    @Override
    public void onMonitoringEntitiesUpdated(List<MonitoringEntity> monitoringEntities) {
        listener.onMonitoringManagerInitialized();
    }

    ////

    private List<Device> getDeviceList() {
        return deviceStore.getDevices();
    }

    private List<MonitoringEntity> getMonitoringEntitiesFromDeviceList(List<Device> devices) {
        List<MonitoringEntity> monitoringEntities = new ArrayList<>();

        for (Device device : devices) {
            if (device.getType().equals(AppController.T5_DEVICE_TYPE) ||
                    device.getType().equals(AppController.T6_DEVICE_TYPE)) {
                MonitoringEntity monitoringEntity = initMonitoringEntity(device);
                monitoringEntities.add(monitoringEntity);
            }
        }

        return monitoringEntities;
    }

    private MonitoringEntity initMonitoringEntity(Device device) {
        MonitoringEntity monitoringEntity = new MonitoringEntity(
                Long.parseLong(device.getId()),
                device.getMark(),
                device.getCivilModel(),
                device.getStateNumber(),
                device.getGroups());

        monitoringEntity.setDisplayEnabled(
                configuration.loadDisplayEnabled(monitoringEntity.getId()));

        return monitoringEntity;
    }

    private void requestUser() {
        monitoringHttpClient.requestUser();
    }

    private void requestMonitoringEntitiesUpdate() {
        UpdateMonitoringEntities interactor = new UpdateMonitoringEntities(
                monitoringManager.getActiveMonitoringEntityGroup().getMonitoringEntities()
        );

        interactor.setListener(this);
        InteractorThreadPool.getInstance().execute(interactor);
    }

    private void setActiveMonitoringEntityGroup() {
        String activeMonitoringEntityGroupName = configuration.loadActiveMonitoringEntityGroup();

        for (MonitoringEntityGroup group : monitoringManager.getMonitoringEntityGroups())
            if (group.getName().equals(activeMonitoringEntityGroupName))
                monitoringManager.setActiveMonitoringEntityGroup(group);

        if (activeMonitoringEntityGroupName == null)
            monitoringManager.setActiveMonitoringEntityGroup(
                    monitoringManager.getMonitoringEntityGroups().get(0)
            );
    }

    private void setActiveMonitoringEntity() {
        for (MonitoringEntity entity :
                monitoringManager.getActiveMonitoringEntityGroup().getMonitoringEntities())
            if (entity.isDisplayEnabled()) {
                monitoringManager.setActiveMonitoringEntity(entity);
                break;
            }
    }
}
