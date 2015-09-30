package kgk.beacon.stores;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kgk.beacon.actions.Action;
import kgk.beacon.actions.ActionCreator;
import kgk.beacon.actions.HttpActions;
import kgk.beacon.dispatcher.Dispatcher;
import kgk.beacon.model.Device;

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
                devices = generateDeviceList(devicesJson);

                for (Device device : devices) {
                    Log.d(TAG, device.toString());
                }

                break;
        }
    }

    private ArrayList<Device> generateDeviceList(JSONArray devicesJson) {
        ArrayList<Device> devices = new ArrayList<>();

        for (int i = 0; i < devicesJson.length(); i++) {

            try {
                devices.add(serializeDeviceFromJson((JSONObject) devicesJson.get(i)));
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d(TAG, "Error parsing from JSON array of devices");
            }
        }

        return devices;
    }

    private Device serializeDeviceFromJson(JSONObject deviceJson) {
        Device device = new Device();

        try {
            device.setId(deviceJson.getString("id"));
            device.setModel(deviceJson.getString("model"));
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "Error serializing device from JSON");
        }

        return device;
    }

    StoreChangeEvent changeEvent() {
        return new DeviceStoreChangeEvent();
    }

    public class DeviceStoreChangeEvent implements StoreChangeEvent {}
}
