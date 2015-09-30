package kgk.beacon.actions;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;

import kgk.beacon.dispatcher.Dispatcher;
import kgk.beacon.model.Signal;


public class ActionCreator {

    private static final String TAG = ActionCreator.class.getSimpleName();

    public static final String KEY_LOGIN = "key_login";
    public static final String KEY_PASSWORD = "key_password";
    public static final String KEY_LAST_SIGNAL = "key_last_signal";
    public static final String KEY_DEVICES = "key_devices";
    public static final String KEY_SEARCH_MODE_STATUS = "key_search_mode_status";
    public static final String KEY_SETTINGS = "key_settings";
    public static final String KEY_SIGNAL = "key_signal";

    private static ActionCreator instance;

    final Dispatcher dispatcher;

    ActionCreator(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public static ActionCreator getInstance(Dispatcher dispatcher) {
        if (instance == null) {
            instance = new ActionCreator(dispatcher);
        }

        return instance;
    }

    //// Signal actions

    public void updateLastSignal(Signal signal) {
        dispatcher.dispatch(SignalActions.UPDATE_LAST_SIGNAL,
                                        KEY_LAST_SIGNAL, signal);
        Log.d(TAG, "dispatched");
    }

    public void refreshSignalsDisplayed() {
        dispatcher.dispatch(SignalActions.REFRESH_SIGNALS_DISPLAYED);
    }

    public void filterSignalsDisplayed(Date fromDate, Date toDate) {
        dispatcher.dispatch(SignalActions.FILTER_SIGNALS_DISPLAYED,
                "FROM_DATE", fromDate,
                "TO_DATE", toDate);
    }

    public void insertSignalToDatabase(Signal signal) {
        dispatcher.dispatch(SignalActions.INSERT_SIGNAL_TO_DATABASE,
                KEY_SIGNAL, signal);
    }

    //// Http Actions

    public void sendAuthenticationRequest(String login, String password) {
        dispatcher.dispatch(HttpActions.AUTHENTICATION_REQUEST,
                                        KEY_LOGIN, login,
                                        KEY_PASSWORD, password);
    }

    public void receiveDeviceListResponse(JSONArray devices) {
        dispatcher.dispatch(HttpActions.DEVICE_LIST_RESPONSE,
                                        KEY_DEVICES, devices);
    }

    public void sendQueryBeaconRequest() {
        dispatcher.dispatch(HttpActions.QUERY_BEACON_REQUEST);
    }

    public void sendGetLastStateRequest() {
        dispatcher.dispatch(HttpActions.GET_LAST_STATE_REQUEST);
    }

    public void sendToggleSearchModeRequest(boolean status) {
        dispatcher.dispatch(HttpActions.TOGGLE_SEARCH_MODE_REQUEST,
                                        KEY_SEARCH_MODE_STATUS, status);
    }

    public void sendSettingsRequest(JSONObject settings) {
        dispatcher.dispatch(HttpActions.SEND_SETTINGS_REQUEST,
                                        KEY_SETTINGS, settings);
    }
}
