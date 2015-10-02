package kgk.beacon.util;

import java.util.Date;

public class LastActionDateStorage {

    private static final String TAG = LastActionDateStorage.class.getSimpleName();
    private static final String KEY_LAST_ACTION_DATE = "key_last_action_date";

    private static LastActionDateStorage instance;

    private LastActionDateStorage() {}

    public static LastActionDateStorage getInstance() {
        if (instance == null) {
            instance = new LastActionDateStorage();
        }
        return instance;
    }

    public void save(Date date) {
        long deviceId = AppController.getInstance().getActiveDeviceId();
        AppController.saveLongValueToSharedPreferences(KEY_LAST_ACTION_DATE + deviceId,
                date.getTime());
    }

    public Date load() {
        long deviceId = AppController.getInstance().getActiveDeviceId();
        long date = AppController.loadLongValueFromSharedPreferences(KEY_LAST_ACTION_DATE + deviceId);
        return new Date(date);
    }
}
