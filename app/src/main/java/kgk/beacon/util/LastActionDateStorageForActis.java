package kgk.beacon.util;

import java.util.Date;

public class LastActionDateStorageForActis {

    private static final String TAG = LastActionDateStorageForActis.class.getSimpleName();
    private static final String KEY_LAST_ACTION_DATE = "key_last_action_date";
    private static final String KEY_QUERY_REQUEST_DATE = "query_request_date";

    private static LastActionDateStorageForActis instance;

    private LastActionDateStorageForActis() {}

    public static LastActionDateStorageForActis getInstance() {
        if (instance == null) {
            instance = new LastActionDateStorageForActis();
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

    public void saveLastQueryRequestDate(long date) {
        long deviceId = AppController.getInstance().getActiveDeviceId();
        AppController.saveLongValueToSharedPreferences(KEY_QUERY_REQUEST_DATE + deviceId, date);
    }

    public long loadLastQueryRequestDate() {
        long deviceId = AppController.getInstance().getActiveDeviceId();
        return AppController.loadLongValueFromSharedPreferences(KEY_QUERY_REQUEST_DATE + deviceId);
    }
}
