package kgk.beacon.util;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;

import de.greenrobot.event.EventBus;
import kgk.beacon.R;
import kgk.beacon.dispatcher.Dispatcher;
import kgk.beacon.networking.VolleyHttpClient;
import kgk.beacon.stores.SignalStore;

@ReportsCrashes(
        formUri = "https://evgenykravtsov.cloudant.com/acra-beacon/_design/acra-storage/_update/report",
        reportType = HttpSender.Type.JSON,
        httpMethod = HttpSender.Method.POST,
        formUriBasicAuthLogin = "tagondshatingstrienewhos",
        formUriBasicAuthPassword = "72915071fb6bc423b41fe3cafdb8ca29b95607e0",
        formKey = "", // This is required for backward compatibility but not used
        customReportContent = {
                ReportField.APP_VERSION_CODE,
                ReportField.APP_VERSION_NAME,
                ReportField.ANDROID_VERSION,
                ReportField.PACKAGE_NAME,
                ReportField.REPORT_ID,
                ReportField.BUILD,
                ReportField.STACK_TRACE
        },
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast_text
)

public class AppController extends Application {

    // TODO Delete all Log.d calls

    private static final String TAG = AppController.class.getSimpleName();
    private static final String APPLICATION_PREFERENCES = "application_preferences";

    private static AppController instance;

    private long activeDeviceId;

    @Override
    public void onCreate() {
        super.onCreate();
        ACRA.init(this);
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

    public static String getDirectionLetterFromDegrees(int degrees) {
        if (degrees > 337 && degrees <= 22) {
            return AppController.getInstance().getString(R.string.direction_north);
        } else if (degrees > 22 && degrees <= 67) {
            return AppController.getInstance().getString(R.string.direction_north_east);
        } else if (degrees > 67 && degrees <= 112) {
            return AppController.getInstance().getString(R.string.direction_east);
        } else if (degrees > 112 && degrees <= 157) {
            return AppController.getInstance().getString(R.string.direction_south_east);
        } else if (degrees > 157 && degrees <= 202) {
            return AppController.getInstance().getString(R.string.direction_south);
        } else if (degrees > 202 && degrees <= 247) {
            return AppController.getInstance().getString(R.string.direction_south_west);
        } else if (degrees > 247 && degrees <= 292) {
            return AppController.getInstance().getString(R.string.direction_west);
        } else if (degrees > 292 && degrees <= 337) {
            return AppController.getInstance().getString(R.string.direction_north_west);
        }

        return AppController.getInstance().getString(R.string.direction_north);
    }
}
