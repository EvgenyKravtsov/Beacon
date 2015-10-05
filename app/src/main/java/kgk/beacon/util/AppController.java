package kgk.beacon.util;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import de.greenrobot.event.EventBus;
import kgk.beacon.dispatcher.Dispatcher;
import kgk.beacon.networking.VolleyHttpClient;
import kgk.beacon.stores.SignalStore;

public class AppController extends Application {

    private static final String TAG = AppController.class.getSimpleName();
    private static final String APPLICATION_PREFERENCES = "application_preferences";

    private static AppController instance;

    private long activeDeviceId;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        VolleyHttpClient.getInstance(this);
        registerSignalStore();
    }

    private void registerSignalStore() {
        Dispatcher dispatcher = Dispatcher.getInstance(EventBus.getDefault());
        SignalStore signalStore = SignalStore.getInstance(dispatcher);
        dispatcher.register(signalStore);
    }

    public static synchronized AppController getInstance() {
        return instance;
    }

    public long getActiveDeviceId() {
        return activeDeviceId;
    }

    public void setActiveDeviceId(long activeDeviceId) {
        this.activeDeviceId = activeDeviceId;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }

    public static void saveBooleanValueToSharedPreferences(String key, boolean value) {
        SharedPreferences sharedPreferences = instance.getSharedPreferences(APPLICATION_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean loadBooleanValueFromSharedPreferences(String key) {
        SharedPreferences sharedPreferences = instance.getSharedPreferences(APPLICATION_PREFERENCES, MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, false);
    }

    public static void saveLongValueToSharedPreferences(String key, long value) {
        SharedPreferences sharedPreferences = instance.getSharedPreferences(APPLICATION_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putLong(key, value);
        editor.apply();
    }

    public static long loadLongValueFromSharedPreferences(String key) {
        SharedPreferences sharedPreferences = instance.getSharedPreferences(APPLICATION_PREFERENCES, MODE_PRIVATE);
        return sharedPreferences.getLong(key, 0);
    }

    public static void saveStringValueToSharedPreferences(String key, String value) {
        SharedPreferences sharedPreferences = instance.getSharedPreferences(APPLICATION_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(key, value);
        editor.apply();
    }

    public static String loadStringValueFromSharedPreferences(String key) {
        SharedPreferences sharedPreferences = instance.getSharedPreferences(APPLICATION_PREFERENCES, MODE_PRIVATE);
        return sharedPreferences.getString(key, "default");
    }
}
