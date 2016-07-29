package kgk.beacon.monitoring.data;

import android.content.Context;
import android.content.SharedPreferences;

import kgk.beacon.monitoring.presentation.model.MapType;
import kgk.beacon.util.AppController;

public class SharedPreferencesDao implements Configuration {

    private static final String SHARED_PREFERENCES = "monitoring_shared_preferences";

    private static final String DEFAULT_MAP_KEY = "default_map";
    private static final int DEFAULT_MAP_DEFAULT = 1;
    private static final int KGK_MAP_CODE = 1;
    private static final int YANDEX_MAP_CODE = 2;
    private static final int GOOGLE_MAP_CODE = 3;
    private static final int SATELLITE_MAP_CODE = 4;
    private static final String MARKER_INFORMATION_ENABLED_KEY = "marker_information_enabled";
    private static final boolean DEFAULT_MARKER_INFORMATION_ENABLED = true;

    private SharedPreferences sharedPreferences;

    ////

    public SharedPreferencesDao() {
        sharedPreferences = AppController.getInstance()
                .getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    ////

    @Override
    public MapType loadDefaultMapType() {
        int mapCode = loadInt(DEFAULT_MAP_KEY, DEFAULT_MAP_DEFAULT);
        switch (mapCode) {
            case KGK_MAP_CODE:
                return MapType.KGK;
            case YANDEX_MAP_CODE:
                return MapType.YANDEX;
            case GOOGLE_MAP_CODE:
                return MapType.GOOGLE;
            case SATELLITE_MAP_CODE:
                return MapType.SATELLITE;
            default:
                return MapType.KGK;
        }
    }

    @Override
    public void saveDefaultMapType(MapType mapType) {
        int mapCode;
        switch (mapType) {
            case KGK:
                mapCode = KGK_MAP_CODE;
                break;
            case YANDEX:
                mapCode = YANDEX_MAP_CODE;
                break;
            case GOOGLE:
                mapCode = GOOGLE_MAP_CODE;
                break;
            case SATELLITE:
                mapCode = SATELLITE_MAP_CODE;
                break;
            default:
                mapCode = KGK_MAP_CODE;
                break;
        }
        saveInt(DEFAULT_MAP_KEY, mapCode);
    }

    @Override
    public boolean loadMarkerInformationEnabled() {
        return loadBoolean(MARKER_INFORMATION_ENABLED_KEY, DEFAULT_MARKER_INFORMATION_ENABLED);
    }

    @Override
    public void saveMarkerInformationEnabled(boolean enabled) {
        saveBoolean(MARKER_INFORMATION_ENABLED_KEY, enabled);
    }

    ////

    private void saveLong(String key, long value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    private long loadLong(String key, long defaultValue) {
        return sharedPreferences.getLong(key, defaultValue);
    }

    private void saveInt(String key, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    private int loadInt(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    private void saveBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    private boolean loadBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    private void saveString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private String loadString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }
}
