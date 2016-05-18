package kgk.beacon.actions;

/**
 * Список типов действий для работы с сетевыми запросами
 */
public interface HttpActions {

    String AUTHENTICATION_REQUEST = "authentication_request";

    String DEVICE_LIST_RESPONSE = "device_list_response";

    String QUERY_BEACON_REQUEST = "query_beacon_request";

    String TOGGLE_SEARCH_MODE_REQUEST = "toggle_search_mode_request";

    String SEND_SETTINGS_REQUEST = "send_settings_request";

    String SEND_GET_SETTINGS_REQUEST = "send_get_settings_request";

    String GET_LAST_STATE_REQUEST = "get_last_state_request";

    String GET_LAST_SIGNALS_REQUEST = "get_last_signals_request";

    String LAST_STATE_FOR_DEVICE_REQUEST = "send_last_state_for_device_request";

    String DETAIL_REPORT_REQUEST = "detail_report_request";

    String ACTIS_COORDINATES_VALIDATION_REQUEST = "actis_coordinates_validation_request";

    String GET_USER_INFO_REQUEST = "get_user_info_request";

    String GENERATOR_SEND_MANUAL_MODE_COMMAND = "generator_send_manual_mode_command";

    String GENERATOR_SEND_AUTO_MODE_COMMAND = "generator_send_auto_mode_command";

    String GENERATOR_SEND_EMERGENCY_STOP_COMMAND = "generator_send_emergency_stop_command";

    String GENERATOR_SEND_SWITCH_ON_COMMAND = "generator_send_switch_on_command";

    String GENERATOR_SEND_SWITCH_OFF_COMMAND = "generator_send_switch_off_command";
}
