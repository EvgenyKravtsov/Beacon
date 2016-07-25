package kgk.beacon.data;

import android.content.Context;
import android.content.SharedPreferences;

import kgk.beacon.util.AppController;

public class StorageSharedPreferences implements AppDataStorage {

    private static final String KEY_LOGIN = "Login";
    private static final String DEFAULT_LOGIN = "";
    private static final String KEY_PASSWORD = "Password";
    private static final String DEFAULT_PASSWORD = "";
    private static final String KEY_REMEMBER_ME = "Remember me";
    private static final boolean DEFAULT_REMEMBER_ME = false;

    // TODO Dont' forget a proper storage name

    private SharedPreferences sharedPreferences;

    ////

    public StorageSharedPreferences() {
        sharedPreferences = AppController.getInstance()
                .getSharedPreferences("", Context.MODE_PRIVATE);
    }

    ////

    @Override
    public void saveLogin(String login) {
        saveString(KEY_LOGIN, login);
    }

    @Override
    public String loadLogin() {
        return loadString(KEY_LOGIN, DEFAULT_LOGIN);
    }

    @Override
    public void savePassword(String password) {
        saveString(KEY_PASSWORD, password);
    }

    @Override
    public String loadPassword() {
        return loadString(KEY_PASSWORD, DEFAULT_PASSWORD);
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
