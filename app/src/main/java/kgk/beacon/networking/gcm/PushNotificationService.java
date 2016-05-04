package kgk.beacon.networking.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import kgk.beacon.R;
import kgk.beacon.view.general.LoginActivity;

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

        Intent resultIntent = new Intent(this, LoginActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this,
                0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setSmallIcon(R.drawable.kgk_logo);
        notificationBuilder.setContentTitle(getString(R.string.app_name));
        notificationBuilder.setContentText("Message text");
        notificationBuilder.setContentIntent(resultPendingIntent);

        int notificationId = 101;
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, notificationBuilder.build());
    }
}
