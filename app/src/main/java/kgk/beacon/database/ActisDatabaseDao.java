package kgk.beacon.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;
import kgk.beacon.actions.Action;
import kgk.beacon.actions.ActionCreator;
import kgk.beacon.actions.DataActions;
import kgk.beacon.dispatcher.Dispatcher;
import kgk.beacon.model.Signal;
import kgk.beacon.stores.ActisStore;
import kgk.beacon.util.AppController;

/**
 * Стандартный Data Access Object для работы с локальной базой данных
 */
public class ActisDatabaseDao {

    private static final String TAG = ActisDatabaseDao.class.getSimpleName();

    private static ActisDatabaseDao instance;

    private ActionCreator actionCreator;
    private Dispatcher dispatcher;
    private SQLiteDatabase database;
    private DatabaseHelper databaseHelper;
    private String[] allColumns = {DatabaseHelper.COLUMN_ID,
                                   DatabaseHelper.COLUMN_DEVICE_ID,
                                   DatabaseHelper.COLUMN_MODE,
                                   DatabaseHelper.COLUMN_LATITUDE,
                                   DatabaseHelper.COLUMN_LONGITUDE,
                                   DatabaseHelper.COLUMN_DATE,
                                   DatabaseHelper.COLUMN_ACTIS_DATE,
                                   DatabaseHelper.COLUMN_VOLTAGE,
                                   DatabaseHelper.COLUMN_BALANCE,
                                   DatabaseHelper.COLUMN_SATELLITES,
                                   DatabaseHelper.COLUMN_SPEED,
                                   DatabaseHelper.COLUMN_CHARGE,
                                   DatabaseHelper.COLUMN_DIRECTION,
                                   DatabaseHelper.COLUMN_TEMPERATURE,
                                   DatabaseHelper.COLUMN_MCC,
                                   DatabaseHelper.COLUMN_MNC,
                                   DatabaseHelper.COLUMN_CELL_ID,
                                   DatabaseHelper.COLUMN_LAC,
                                   DatabaseHelper.COLUMN_LBS_DETECTED};

    private ActisDatabaseDao(Context context) {
        databaseHelper = DatabaseHelper.getInstance(context);
        dispatcher = Dispatcher.getInstance(EventBus.getDefault());
        dispatcher.register(this);
        actionCreator = ActionCreator.getInstance(dispatcher);
    }

    public static ActisDatabaseDao getInstance(Context context) {
        if (instance == null) {
            instance = new ActisDatabaseDao(context);
        }
        return instance;
    }

    /** Открыть соединение с базой данных */
    public void open() throws SQLException {
        database = databaseHelper.getWritableDatabase();
    }

    public void close() {
        // Closing database in multithread environment can cause an exception
        // databaseHelper.close();
    }

    /** Вставка данных сигнала в базу данных */
    public void insertSignal(Signal signal) {
        try {
            open();

            if (hasDuplicate(signal)) {
                close();
                return;
            }

            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_DEVICE_ID, signal.getDeviceId());
            values.put(DatabaseHelper.COLUMN_MODE, signal.getMode());
            values.put(DatabaseHelper.COLUMN_LATITUDE, signal.getLatitude());
            values.put(DatabaseHelper.COLUMN_LONGITUDE, signal.getLongitude());
            values.put(DatabaseHelper.COLUMN_DATE, signal.getDate());
            values.put(DatabaseHelper.COLUMN_ACTIS_DATE, signal.getActisDate());
            values.put(DatabaseHelper.COLUMN_VOLTAGE, signal.getVoltage());
            values.put(DatabaseHelper.COLUMN_BALANCE, signal.getBalance());
            values.put(DatabaseHelper.COLUMN_SATELLITES, signal.getSatellites());
            values.put(DatabaseHelper.COLUMN_SPEED, signal.getSpeed());
            values.put(DatabaseHelper.COLUMN_CHARGE, signal.getCharge());
            values.put(DatabaseHelper.COLUMN_DIRECTION, signal.getDirection());
            values.put(DatabaseHelper.COLUMN_TEMPERATURE, signal.getTemperature());
            values.put(DatabaseHelper.COLUMN_MCC, signal.getMcc());
            values.put(DatabaseHelper.COLUMN_MNC, signal.getMnc());
            values.put(DatabaseHelper.COLUMN_CELL_ID, signal.getCellId());
            values.put(DatabaseHelper.COLUMN_LAC, signal.getLac());
            values.put(DatabaseHelper.COLUMN_LBS_DETECTED, signal.isLbsDeteceted() ? 1 : 0);
            database.insert(DatabaseHelper.TABLE_SIGNAL, null, values);
            close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** Проверака данных сигнала на уникальность в базе данных */
    private boolean hasDuplicate(Signal signal) {
        @SuppressLint("DefaultLocale") Cursor cursor = database.rawQuery(String.format(DatabaseHelper
                .SignalDatabaseQuery.GET_SIGNALS_BY_DEVICE_ID_AND_DATE, signal.getDeviceId(), signal.getDate()), null);
        int duplicateCount  = cursor.getCount();
        cursor.close();
        return duplicateCount != 0;
    }

    /** Выбрать данные о сигналах за указанный период */
    public List<Signal> getSignalsByPeriod(long dateFrom, long dateTo) {
        List<Signal> signals = new ArrayList<>();

        try {
            open();
            @SuppressLint("DefaultLocale") Cursor cursor = database.rawQuery(String.format(DatabaseHelper
                    .SignalDatabaseQuery.GET_SIGNALS_BY_PERIOD, dateFrom, dateTo,
                    AppController.getInstance().getActiveDeviceId()), null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Signal signal = cursorToSignal(cursor);
                signals.add(signal);
                cursor.moveToNext();
            }
            cursor.close();
            close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        actionCreator.filterSignalsDisplayed(signals);
        return signals;
    }

    /** Выбрать желаемое количество сигналов для указанного id устройства, начиная с последнего */
    public List<Signal> getLastSignalsByDeviceId(int numberOfSignals) {
        List<Signal> signals = new ArrayList<>();

        try {
            open();
            @SuppressLint("DefaultLocale") Cursor cursor = database.rawQuery(String.format(DatabaseHelper
                            .SignalDatabaseQuery.GET_SIGNALS_BY_DEVICE_ID,
                    AppController.getInstance().getActiveDeviceId()), null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Signal signal = cursorToSignal(cursor);
                signals.add(signal);
                cursor.moveToNext();
            }

//            if (numberOfSignals >= cursor.getCount()) {
//                cursor.moveToFirst();
//                while (!cursor.isAfterLast()) {
//                    Signal signal = cursorToSignal(cursor);
//                    signals.add(signal);
//                    cursor.moveToNext();
//                }
//            } else {
//                cursor.moveToLast();
//                while (numberOfSignals != 0) {
//                    Signal signal = cursorToSignal(cursor);
//                    signals.add(0, signal);
//                    cursor.moveToPrevious();
//                    numberOfSignals--;
//                }
//            }

            cursor.close();
            close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ActisStore.getInstance(Dispatcher.getInstance(EventBus.getDefault())).setSignalsDisplayed(signals);
        return signals;
    }

    /** Выбрать все данные о сигналах для всех id */
    public List<Signal> getAllSignals() {
        List<Signal> signals = new ArrayList<>();

        try {
            open();
            Cursor cursor = database.query(DatabaseHelper.TABLE_SIGNAL, allColumns,
                    null, null, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Signal signal = cursorToSignal(cursor);
                signals.add(signal);
                cursor.moveToNext();
            }
            cursor.close();
            close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return signals;
    }

    /** Удалить все данные о сигналах из базы данных */
    public void deleteAllSignalsFromDatabase() {
        try {
            open();
            database.execSQL(DatabaseHelper.SignalDatabaseQuery.DELETE_ALL_SIGNALS);
            close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** Выбрать дату последнего сигнала для активного id устройства */
    public long getLastSignalDate() {
        long lastSignalDate = 0;

        try {
            open();
            Cursor cursor = database.rawQuery("SELECT * FROM "
                                            + DatabaseHelper.TABLE_SIGNAL
                                            + " WHERE "
                                            + DatabaseHelper.COLUMN_DEVICE_ID
                                            + " = "
                                            + AppController.getInstance().getActiveDeviceId(), null);
            cursor.moveToLast();
            Signal signal = cursorToSignal(cursor);
            lastSignalDate = signal.getDate();
            close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (CursorIndexOutOfBoundsException e) {
            e.printStackTrace();
            lastSignalDate = 1444435200; // 10 October 2015
        }

        if (lastSignalDate == 0) {
            lastSignalDate = 1444435200; // 10 October 2015
        }

        return lastSignalDate;
    }

    /** Обновить поля, содержащие координаты, для указанного серверного времени сигнала */
    public void updateSignalCoordinatesByServerDate(long serverDate, double latitude, double longitude) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_LATITUDE, latitude);
        values.put(DatabaseHelper.COLUMN_LONGITUDE, longitude);
        values.put(DatabaseHelper.COLUMN_ACTIS_DATE, serverDate);

        try {
            open();
            database.update(DatabaseHelper.TABLE_SIGNAL, values, DatabaseHelper.COLUMN_DATE + " = " + serverDate, null);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    /** Обновить фактическое время сигнала */
    public void updateSignalActisDate(long serverDate) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ACTIS_DATE, serverDate);

        try {
            open();
            database.update(DatabaseHelper.TABLE_SIGNAL, values, DatabaseHelper.COLUMN_DATE + " = " + serverDate, null);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    /** Демаршалинг курсора SQLite в объект модели приложения */
    private Signal cursorToSignal(Cursor cursor) {
        Signal signal = new Signal();
        signal.setDeviceId(cursor.getLong(1));
        signal.setMode(cursor.getInt(2));
        signal.setLatitude(cursor.getDouble(3));
        signal.setLongitude(cursor.getDouble(4));
        signal.setDate(cursor.getLong(5));
        signal.setActisDate(cursor.getLong(6));
        signal.setVoltage(cursor.getDouble(7));
        signal.setBalance(cursor.getInt(8));
        signal.setSatellites(cursor.getInt(9));
        signal.setSpeed(cursor.getInt(10));
        signal.setCharge(cursor.getInt(11));
        signal.setDirection(cursor.getInt(12));
        signal.setTemperature(cursor.getInt(13));
        signal.setMcc(cursor.getInt(14));
        signal.setMnc(cursor.getInt(15));
        signal.setCellId(cursor.getString(16));
        signal.setLac(cursor.getString(17));
        signal.setLbsDeteceted(cursor.getInt(18) == 1);
        return signal;
    }

    ////

    public void onEventBackgroundThread(Action action) {
        onAction(action);
    }

    private void onAction(Action action) {
        switch (action.getType()) {
            case DataActions.INSERT_SIGNAL_TO_DATABASE:
                Signal signal = (Signal) action.getData().get(ActionCreator.KEY_SIGNAL);
                insertSignal(signal);
                break;
            case DataActions.GET_LAST_SIGNALS_BY_DEVICE_ID_FROM_DATABASE:
                int numberOfSignals = (int) action.getData().get(ActionCreator.KEY_NUMBER_OF_SIGNALS);
                getLastSignalsByDeviceId(numberOfSignals);
                break;
            case DataActions.GET_SIGNALS_BY_PERIOD:
                long fromDate = ((Date) action.getData().get(ActionCreator.KEY_FROM_DATE)).getTime() / 1000;
                long toDate = ((Date) action.getData().get(ActionCreator.KEY_TO_DATE)).getTime() / 1000;
                getSignalsByPeriod(fromDate, toDate);
                break;
            case DataActions.GET_LAST_SIGNAL_DATE_FROM_DATABASE:
                long lastSignalDate = getLastSignalDate();
                long now = Calendar.getInstance().getTimeInMillis() / 1000;
                actionCreator.getLastSignalsRequest(lastSignalDate, now);
        }
    }
}
