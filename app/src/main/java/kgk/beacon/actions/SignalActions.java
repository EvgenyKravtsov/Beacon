package kgk.beacon.actions;

public interface SignalActions {
    String UPDATE_LAST_SIGNAL = "update_last_signal";

    String REFRESH_SIGNALS_DISPLAYED = "refresh_signal_display";

    String FILTER_SIGNALS_DISPLAYED = "filter_signals_displayed";

    String INSERT_SIGNAL_TO_DATABASE = "database_insert_signal";

    String GET_LAST_SIGNALS_BY_DEVICE_ID_FROM_DATABASE = "get_last_signals_by_device_id_from_database";

    String GET_SIGNALS_BY_PERIOD = "get_signals_by_period";

    String GET_LAST_SIGNAL_DATE_FROM_DATABASE = "get_last_signal_date_from_database";
}
