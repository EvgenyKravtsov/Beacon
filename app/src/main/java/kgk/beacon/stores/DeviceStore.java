package kgk.beacon.stores;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kgk.beacon.actions.Action;
import kgk.beacon.actions.ActionCreator;
import kgk.beacon.actions.HttpActions;
import kgk.beacon.dispatcher.Dispatcher;
import kgk.beacon.model.Device;
import kgk.beacon.util.AppController;

/**
 * Стандартная реализация базового элемента архитектуры FLUX - хранилища, в данном случае класс
 * абстрагирует состояние списка устройств пользователя
 */
public class DeviceStore extends Store {

    public static final String TAG = DeviceStore.class.getSimpleName();

    private static DeviceStore instance;

    private ArrayList<Device> devices;

    protected DeviceStore(Dispatcher dispatcher) {
        super(dispatcher);
    }

    public static DeviceStore getInstance(Dispatcher dispatcher) {
        if (instance == null) {
            instance = new DeviceStore(dispatcher);
        }

        return instance;
    }

    public ArrayList<Device> getDevices() {
        return devices;
    }

    public void onEvent(Action action) {
        onAction(action);
    }

    public void onAction(Action action) {
        switch (action.getType()) {
            case HttpActions.DEVICE_LIST_RESPONSE:
                JSONArray devicesJson = (JSONArray) action.getData().get(ActionCreator.KEY_DEVICES);
                // devices = generateDeviceListActisOnly(devicesJson);
                devices = generateDeviceList(devicesJson);
                break;
        }
    }

    /** Сгенерировать список устройств только из устройств Actis */
    private ArrayList<Device> generateDeviceListActisOnly(JSONArray devicesJson) {
        ArrayList<Device> devices = new ArrayList<>();

        for (int i = 0; i < devicesJson.length(); i++) {

            try {
                Device device = serializeDeviceFromJson((JSONObject) devicesJson.get(i));
                if (device.getType().equals(AppController.ACTIS_DEVICE_TYPE)) {
                    devices.add(device);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return devices;
    }

    /** Сгенерировать список устройств */
    private ArrayList<Device> generateDeviceList(JSONArray devicesJson) {
        ArrayList<Device> devices = new ArrayList<>();

        for (int i = 0; i < devicesJson.length(); i++) {

            try {
                devices.add(serializeDeviceFromJson((JSONObject) devicesJson.get(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // TODO Delete test code
        Device testGenerator = new Device();
        testGenerator.setId("0101010101");
        testGenerator.setModel("Test Generator");
        testGenerator.setCivilModel("DENZEL DB6000E");
        testGenerator.setMark("DENZEL");
        testGenerator.setStateNumber("DENZEL");

        ArrayList<String> testGeneratorGroupList = new ArrayList<>();
        testGeneratorGroupList.add("generator_test");
        testGenerator.setGroups(testGeneratorGroupList);

        testGenerator.setType(AppController.GENERATOR_DEVICE_TYPE);
        devices.add(testGenerator);

        return devices;
    }

    /** Демаршалинг входящих данных об устройстве в объект модели */
    private Device serializeDeviceFromJson(JSONObject deviceJson) {
        Device device = new Device();

        try {
            device.setId(deviceJson.getString("id"));
            device.setModel(deviceJson.getString("type_name"));
            device.setCivilModel(deviceJson.getString("model"));
            device.setMark(deviceJson.getString("marka"));
            device.setStateNumber(deviceJson.getString("gosnum"));

            JSONArray groupsArray = deviceJson.getJSONArray("groups");
            for (int i = 0; i < groupsArray.length(); i++) {
                String group = groupsArray.getJSONObject(i).getString("name");
                device.getGroups().add(group);
            }

            if (deviceJson.getString("type_name").equals(AppController.ACTIS_DEVICE_TYPE)) {
                device.setType(deviceJson.getString("type_name"));
            } else {
                device.setType(deviceJson.getBoolean("isT6") ? AppController.T6_DEVICE_TYPE : AppController.T5_DEVICE_TYPE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return device;
    }

    StoreChangeEvent changeEvent() {
        return new DeviceStoreChangeEvent();
    }

    public class DeviceStoreChangeEvent implements StoreChangeEvent {}
}
