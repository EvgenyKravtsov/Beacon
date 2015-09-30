package kgk.beacon.util;


import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import kgk.beacon.networking.VolleyHttpClient;

public class AppController extends Application {

    private static final String TAG = AppController.class.getSimpleName();

    private static AppController instance;

    private long activeDeviceId;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        VolleyHttpClient.getInstance(this);
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
}
