package kgk.beacon.actions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kgk.beacon.dispatcher.Dispatcher;
import kgk.beacon.model.DataForDetailReportRequest;
import kgk.beacon.model.Signal;

/**
 * Базовый компонент архитектуры FLUX, предоставляет интерфейс создания и отправки
 * действий для других объектов системы, реализован в виде синглтона.
 */
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
    public static final String KEY_DATA_FOR_DETAIL_REPORT_REQUEST = "key_data_for_detail_report_request";

    public static final String KEY_VALIDATION_SERVER_DATE = "key_validation_server_time";
    public static final String KEY_VALIDATION_MCC = "key_validation_mcc";
    public static final String KEY_VALIDATION_MNC = "key_validation_mnc";
    public static final String KEY_VALIDATION_CELL_ID = "key_validation_cell_id";
    public static final String KEY_VALIDATION_LAC = "key_validation_lac";

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

    /** Обновляет последний сигнал в оперативном хранилище сигналов (ActisSore) */
    public void updateLastSignal(Signal signal) {
        dispatcher.dispatch(DataActions.UPDATE_LAST_SIGNAL,
                                        KEY_LAST_SIGNAL, signal);
    }

    /** Сообщает хранилище сигналов о необходимости оповестить систему об обновлении списка сигналов,
     * предназначенных для отображения в истории и на треке
     * */
    public void refreshSignalsDisplayed() {
        dispatcher.dispatch(DataActions.REFRESH_SIGNALS_DISPLAYED);
    }

    /** Перезаписывает массив сигналов, предназначенных для отображения в истории и на треке */
    public void filterSignalsDisplayed(List<Signal> signals) {
        dispatcher.dispatch(DataActions.FILTER_SIGNALS_DISPLAYED,
                KEY_SIGNALS, signals);
    }

    /** Вставка сигнала в локальную базу данных */
    public void insertSignalToDatabase(Signal signal) {
        dispatcher.dispatch(DataActions.INSERT_SIGNAL_TO_DATABASE,
                KEY_SIGNAL, signal);
    }

    /** Запрос в базу данных устройства с целью получить список последних сигналов для активного id
     * устройства Actis
     * */
    public void getLastSignalsByDeviceIdFromDatabase(int numberOfSignals) {
        dispatcher.dispatch(DataActions.GET_LAST_SIGNALS_BY_DEVICE_ID_FROM_DATABASE,
                KEY_NUMBER_OF_SIGNALS, numberOfSignals);
    }

    /** Запрос в базу данных устройства с целью получить список сигнало за определенный период */
    public void getSignalsByPeriod(Date fromDate, Date toDate) {
       dispatcher.dispatch(DataActions.GET_SIGNALS_BY_PERIOD,
               KEY_FROM_DATE, fromDate,
               KEY_TO_DATE, toDate);
    }

    /** Запрос в базу данных устройства с целью получить дату последнего сигнала для активного id
     * устройства Actis
     */
    public void getLastSignalDateFromDatabase() {
        dispatcher.dispatch(DataActions.GET_LAST_SIGNAL_DATE_FROM_DATABASE);
    }

    /** Отправить http-запрос для получения списка сигналов за указанный период */
    public void getLastSignalsRequest(long fromDate, long toDate) {
        dispatcher.dispatch(HttpActions.GET_LAST_SIGNALS_REQUEST,
                KEY_FROM_DATE, fromDate,
                KEY_TO_DATE, toDate);
    }

    /** Отправить запрос на авторизацию в сервисе КГК Мониторинг */
    public void sendAuthenticationRequest(String login, String password) {
        dispatcher.dispatch(HttpActions.AUTHENTICATION_REQUEST,
                                        KEY_LOGIN, login,
                                        KEY_PASSWORD, password);
    }

    /** Обработать результат запроса на списко устройств пользователя */
    public void receiveDeviceListResponse(JSONArray devices) {
        dispatcher.dispatch(HttpActions.DEVICE_LIST_RESPONSE,
                                        KEY_DEVICES, devices);
    }

    /** Отправить запрос для разового определения координат маячка Actis */
    public void sendQueryBeaconRequest() {
        dispatcher.dispatch(HttpActions.QUERY_BEACON_REQUEST);
    }

    /** Отправить запрос на включение/выключение режима поиска маячка Actis */
    public void sendToggleSearchModeRequest(boolean status) {
        dispatcher.dispatch(HttpActions.TOGGLE_SEARCH_MODE_REQUEST,
                                        KEY_SEARCH_MODE_STATUS, status);
    }

    /** Отправить запрос на запись настроек для маячка Actis */
    public void sendSettingsRequest(JSONObject settings) {
        dispatcher.dispatch(HttpActions.SEND_SETTINGS_REQUEST,
                                        KEY_SETTINGS, settings);
    }

    /** Отправить запрос для получения текущих настроек маячка Actis */
    public void sendGetSettingsRequest() {
        dispatcher.dispatch(HttpActions.SEND_GET_SETTINGS_REQUEST);
    }

    /** Отпавить запрос на получение актуальных данных местоположения устроства КГК */
    public void sendLastStateForDeviceRequest() {
        dispatcher.dispatch(HttpActions.LAST_STATE_FOR_DEVICE_REQUEST);
    }

    /** Переместить пакет с данными местоположения устройства КГК в оперативное хранилище */
    public void transportLastStatePacketToStore(Object packet) {
        dispatcher.dispatch(DataActions.TRANSPORT_LAST_STATE_PACKET_TO_STORE,
                KEY_LAST_STATE_PACKET, packet);
    }

    /** Переместить список пакетов с данными местоположения устройства КГК,
     * предназначенных для построения трека, в оперативное хранилище
     * */
    public void transportPacketsForTrack(ArrayList packets) {
        dispatcher.dispatch(DataActions.TRANSPORT_PACKETS_FOR_TRACK,
                KEY_PACKETS_FOR_TRACK, packets);
    }

    /** Отправить запрос на получение детального отчета по устройству КГК */
    public void sendDetailReportRequest(DataForDetailReportRequest data) {
        dispatcher.dispatch(HttpActions.DETAIL_REPORT_REQUEST,
                KEY_DATA_FOR_DETAIL_REPORT_REQUEST, data);
    }

    /** Отправить запрос для получения координат по LBS данным */
    public void sendActisCoordinatesValidationRequest(long serverDate,
                                                      int mcc,
                                                      int mnc,
                                                      String cellIdHex,
                                                      String lacHex){
        dispatcher.dispatch(HttpActions.ACTIS_COORDINATES_VALIDATION_REQUEST,
                KEY_VALIDATION_SERVER_DATE, serverDate,
                KEY_VALIDATION_MCC, mcc,
                KEY_VALIDATION_MNC, mnc,
                KEY_VALIDATION_CELL_ID, cellIdHex,
                KEY_VALIDATION_LAC, lacHex);
    }

    public void sendGetUserInfoRequest() {
        dispatcher.dispatch(HttpActions.GET_USER_INFO_REQUEST);
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
