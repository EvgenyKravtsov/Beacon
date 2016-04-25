package kgk.beacon.networking.gcm;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

/**
 * Сервис для приема Push-уведомления Google
 */
public class PushNotificationService extends GcmListenerService {

    private static final String TAG = PushNotificationService.class.getSimpleName();

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");

        // TODO Delete test code
        Log.d(TAG, "Push message - " + message);
    }
}
