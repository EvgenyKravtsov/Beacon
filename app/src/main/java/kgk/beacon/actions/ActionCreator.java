package kgk.beacon.actions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    public static final String KEY_SIGNALS = "key_signals";
    public static final String KEY_NUMBER_OF_SIGNALS = "key_number_of_signals";
    public static final String KEY_FROM_DATE = "key_from_date";
    public static final String KEY_TO_DATE = "key_to_date";
    public static final String KEY_LAST_STATE_PACKET = "key_last_state_packet";
    public static final String KEY_PACKETS_FOR_TRACK = "key_packets_for_track";

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

    public void updateLastSignal(Signal signal) {
        dispatcher.dispatch(DataActions.UPDATE_LAST_SIGNAL,
                                        KEY_LAST_SIGNAL, signal);
    }

    public void refreshSignalsDisplayed() {
        dispatcher.dispatch(DataActions.REFRESH_SIGNALS_DISPLAYED);
    }

    public void filterSignalsDisplayed(List<Signal> signals) {
        dispatcher.dispatch(DataActions.FILTER_SIGNALS_DISPLAYED,
                KEY_SIGNALS, signals);
    }

    public void insertSignalToDatabase(Signal signal) {
        dispatcher.dispatch(DataActions.INSERT_SIGNAL_TO_DATABASE,
                KEY_SIGNAL, signal);
    }

    public void getLastSignalsByDeviceIdFromDatabase(int numberOfSignals) {
        dispatcher.dispatch(DataActions.GET_LAST_SIGNALS_BY_DEVICE_ID_FROM_DATABASE,
                KEY_NUMBER_OF_SIGNALS, numberOfSignals);
    }

    public void getSignalsByPeriod(Date fromDate, Date toDate) {
       dispatcher.dispatch(DataActions.GET_SIGNALS_BY_PERIOD,
               KEY_FROM_DATE, fromDate,
               KEY_TO_DATE, toDate);
    }

    public void getLastSignalDateFromDatabase() {
        dispatcher.dispatch(DataActions.GET_LAST_SIGNAL_DATE_FROM_DATABASE);
    }

    public void getLastSignalsRequest(long fromDate, long toDate) {
        dispatcher.dispatch(HttpActions.GET_LAST_SIGNALS_REQUEST,
                KEY_FROM_DATE, fromDate,
                KEY_TO_DATE, toDate);
    }

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

    public void sendGetSettingsRequest() {
        dispatcher.dispatch(HttpActions.SEND_GET_SETTINGS_REQUEST);
    }

    public void sendLastStateForDeviceRequest() {
        dispatcher.dispatch(HttpActions.LAST_STATE_FOR_DEVICE_REQUEST);
    }

    public void transportLastStatePacketToStore(Object packet) {
        dispatcher.dispatch(DataActions.TRANSPORT_LAST_STATE_PACKET_TO_STORE,
                KEY_LAST_STATE_PACKET, packet);
    }

    public void transportPacketsForTrack(ArrayList packets) {
        dispatcher.dispatch(DataActions.TRANSPORT_PACKETS_FOR_TRACK,
                KEY_PACKETS_FOR_TRACK, packets);
    }

    public void sendDetailReportRequest() {
        dispatcher.dispatch(HttpActions.DETAIL_REPORT_REQUEST);
    }

    //// Generator actions

    public void sendManualModeCommandToGenerator() {
        dispatcher.dispatch(HttpActions.GENERATOR_SEND_MANUAL_MODE_COMMAND);
    }

    public void sendAutoModeCommandToGenerator() {
        dispatcher.dispatch(HttpActions.GENERATOR_SEND_AUTO_MODE_COMMAND);
    }

    public void sendEmergencyStopCommandToGenerator() {
        dispatcher.dispatch(HttpActions.GENERATOR_SEND_EMERGENCY_STOP_COMMAND);
    }

    public void sendSwitchOnCommandToGenerator() {
        dispatcher.dispatch(HttpActions.GENERATOR_SEND_SWITCH_ON_COMMAND);
    }

    public void sendSwitchOffCommandToGenerator() {
        dispatcher.dispatch(HttpActions.GENERATOR_SEND_SWITCH_OFF_COMMAND);
    }
}
