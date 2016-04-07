package kgk.beacon.database;

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
                                   DatabaseHelper.COLUMN_VOLTAGE,
                                   DatabaseHelper.COLUMN_BALANCE,
                                   DatabaseHelper.COLUMN_SATELLITES,
                                   DatabaseHelper.COLUMN_SPEED,
                                   DatabaseHelper.COLUMN_CHARGE,
                                   DatabaseHelper.COLUMN_DIRECTION,
                                   DatabaseHelper.COLUMN_TEMPERATURE};

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

    public void open() throws SQLException {
        database = databaseHelper.getWritableDatabase();
    }

    public void close() {
        // Closing database in multithread environment can cause an exception
        // databaseHelper.close();
    }

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
            values.put(DatabaseHelper.COLUMN_VOLTAGE, signal.getVoltage());
            values.put(DatabaseHelper.COLUMN_BALANCE, signal.getBalance());
            values.put(DatabaseHelper.COLUMN_SATELLITES, signal.getSatellites());
            values.put(DatabaseHelper.COLUMN_SPEED, signal.getSpeed());
            values.put(DatabaseHelper.COLUMN_CHARGE, signal.getCharge());
            values.put(DatabaseHelper.COLUMN_DIRECTION, signal.getDirection());
            values.put(DatabaseHelper.COLUMN_TEMPERATURE, signal.getTemperature());
            database.insert(DatabaseHelper.TABLE_SIGNAL, null, values);
            close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean hasDuplicate(Signal signal) {
        Cursor cursor = database.rawQuery(String.format(DatabaseHelper
                .SignalDatabaseQuery.GET_SIGNALS_BY_DEVICE_ID_AND_DATE, signal.getDeviceId(), signal.getDate()), null);
        int duplicateCount  = cursor.getCount();
        cursor.close();
        return duplicateCount != 0;
    }

    public List<Signal> getSignalsByPeriod(long dateFrom, long dateTo) {
        List<Signal> signals = new ArrayList<>();

        try {
            open();
            Cursor cursor = database.rawQuery(String.format(DatabaseHelper
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

    public List<Signal> getLastSignalsByDeviceId(int numberOfSignals) {
        List<Signal> signals = new ArrayList<>();

        try {
            open();
            Cursor cursor = database.rawQuery(String.format(DatabaseHelper
                            .SignalDatabaseQuery.GET_SIGNALS_BY_DEVICE_ID,
                    AppController.getInstance().getActiveDeviceId()), null);

            if (numberOfSignals >= cursor.getCount()) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    Signal signal = cursorToSignal(cursor);
                    signals.add(signal);
                    cursor.moveToNext();
                }
            } else {
                cursor.moveToLast();
                while (numberOfSignals != 0) {
                    Signal signal = cursorToSignal(cursor);
                    signals.add(0, signal);
                    cursor.moveToPrevious();
                    numberOfSignals--;
                }
            }

            cursor.close();
            close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ActisStore.getInstance(Dispatcher.getInstance(EventBus.getDefault())).setSignalsDisplayed(signals);
        return signals;
    }

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

    public void deleteAllSignalsFromDatabase() {
        try {
            open();
            database.execSQL(DatabaseHelper.SignalDatabaseQuery.DELETE_ALL_SIGNALS);
            close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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

    private Signal cursorToSignal(Cursor cursor) {
        Signal signal = new Signal();
        signal.setDeviceId(cursor.getLong(1));
        signal.setMode(cursor.getInt(2));
        signal.setLatitude(cursor.getDouble(3));
        signal.setLongitude(cursor.getDouble(4));
        signal.setDate(cursor.getLong(5));
        signal.setVoltage(cursor.getDouble(6));
        signal.setBalance(cursor.getInt(7));
        signal.setSatellites(cursor.getInt(8));
        signal.setSpeed(cursor.getInt(9));
        signal.setCharge(cursor.getInt(10));
        signal.setDirection(cursor.getInt(11));
        signal.setTemperature(cursor.getInt(12));
        return signal;
    }

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
