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
import kgk.beacon.stores.ActisStore;
import kgk.beacon.stores.T5Store;
import kgk.beacon.stores.T6Store;

/**
 * Приложение разработано на базе многослойной архитектуры Flux, реализующий однонаправленный поток данных, как показано на схеме
 *
 * - Для реализации модуля диспетчера (Dispatcher) используется стандартная шина событий (Event Bus). Сам диспетчер является по сути оберткой вокруг шины
 *
 * - Коммуникация между слоями осуществляется с помощью простых DTO, созданием и распеределением которых занимается модуль Action Creator
 *
 * - Вся бизнес-логика инкапсулирована в оперативном хранилище (Actis Store), Предполагается, что последующее расширение приложения для поддержки других типов устройств будет происходить путем добавления новых модулей Store
 *
 * - Кроме того, Store одновременно реализует шаблон ViewModel предоставляя все необходимые поля данных, которые необходимо отбразать на экране, Оповещение модуля View осуществляется так же при помощи объектов Action (DTO)
 */

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

    // TODO Change google maps key before release

    private static final String TAG = AppController.class.getSimpleName();
    private static final String APPLICATION_PREFERENCES = "application_preferences";

    public static final String ACTIS_DEVICE_NAME = "Actis";
    // TODO Delete test code
    public static final String TEST_GENERATOR_DEVICE_NAME = "Test Generator";

    public static final String ACTIS_DEVICE_TYPE = "Actis";
    public static final String T6_DEVICE_TYPE = "T6";
    public static final String T5_DEVICE_TYPE = "T5";

    private static AppController instance;

    private long activeDeviceId;
    private String activeDeviceType;
    private String activeDeviceModel;

    private boolean demoMode;

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

        ActisStore actisStore = ActisStore.getInstance(dispatcher);
        T6Store t6Store = T6Store.getInstance(dispatcher);
        T5Store t5Store = T5Store.getInstance(dispatcher);

        dispatcher.register(actisStore);
        dispatcher.register(t6Store);
        dispatcher.register(t5Store);
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

    public String getActiveDeviceType() {
        return activeDeviceType;
    }

    public void setActiveDeviceType(String activeDeviceType) {
        this.activeDeviceType = activeDeviceType;
    }

    public String getActiveDeviceModel() {
        return activeDeviceModel;
    }

    public void setActiveDeviceModel(String activeDeviceModel) {
        this.activeDeviceModel = activeDeviceModel;
    }

    /** Проверка наличия на устройстве интернет-соединения */
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }

    public boolean isDemoMode() {
        return demoMode;
    }

    public void setDemoMode(boolean demoMode) {
        this.demoMode = demoMode;
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

    /** Получить литеру направления в зависимости от градусного значения */
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
