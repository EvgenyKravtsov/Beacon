package kgk.beacon.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;
import kgk.beacon.actions.Action;
import kgk.beacon.actions.ActionCreator;
import kgk.beacon.actions.SignalActions;
import kgk.beacon.dispatcher.Dispatcher;
import kgk.beacon.model.Signal;
import kgk.beacon.stores.SignalStore;
import kgk.beacon.util.AppController;

public class SignalDatabaseDao {

    private static final String TAG = SignalDatabaseDao.class.getSimpleName();

    private static SignalDatabaseDao instance;

    private ActionCreator actionCreator;
    private Dispatcher dispatcher;
    private SQLiteDatabase database;
    private SignalDatabaseHelper signalDatabaseHelper;
    private String[] allColumns = {SignalDatabaseHelper.COLUMN_ID,
                                   SignalDatabaseHelper.COLUMN_DEVICE_ID,
                                   SignalDatabaseHelper.COLUMN_MODE,
                                   SignalDatabaseHelper.COLUMN_LATITUDE,
                                   SignalDatabaseHelper.COLUMN_LONGITUDE,
                                   SignalDatabaseHelper.COLUMN_DATE,
                                   SignalDatabaseHelper.COLUMN_VOLTAGE,
                                   SignalDatabaseHelper.COLUMN_BALANCE,
                                   SignalDatabaseHelper.COLUMN_SATELLITES,
                                   SignalDatabaseHelper.COLUMN_SPEED,
                                   SignalDatabaseHelper.COLUMN_CHARGE,
                                   SignalDatabaseHelper.COLUMN_DIRECTION,
                                   SignalDatabaseHelper.COLUMN_TEMPERATURE};

    private SignalDatabaseDao(Context context) {
        signalDatabaseHelper = SignalDatabaseHelper.getInstance(context);
        dispatcher = Dispatcher.getInstance(EventBus.getDefault());
        dispatcher.register(this);
        actionCreator = ActionCreator.getInstance(dispatcher);
    }

    public static SignalDatabaseDao getInstance(Context context) {
        if (instance == null) {
            instance = new SignalDatabaseDao(context);
        }
        return instance;
    }

    public void open() throws SQLException {
        database = signalDatabaseHelper.getWritableDatabase();
    }

    public void close() {
        signalDatabaseHelper.close();
    }

    public void insertSignal(Signal signal) {
        try {
            open();

            if (hasDuplicate(signal)) {
                Log.d(TAG, "Duplicate ignored");
                close();
                return;
            }

            ContentValues values = new ContentValues();
            values.put(SignalDatabaseHelper.COLUMN_DEVICE_ID, signal.getDeviceId());
            values.put(SignalDatabaseHelper.COLUMN_MODE, signal.getMode());
            values.put(SignalDatabaseHelper.COLUMN_LATITUDE, signal.getLatitude());
            values.put(SignalDatabaseHelper.COLUMN_LONGITUDE, signal.getLongitude());
            values.put(SignalDatabaseHelper.COLUMN_DATE, signal.getDate());
            values.put(SignalDatabaseHelper.COLUMN_VOLTAGE, signal.getVoltage());
            values.put(SignalDatabaseHelper.COLUMN_BALANCE, signal.getBalance());
            values.put(SignalDatabaseHelper.COLUMN_SATELLITES, signal.getSatellites());
            values.put(SignalDatabaseHelper.COLUMN_SPEED, signal.getSpeed());
            values.put(SignalDatabaseHelper.COLUMN_CHARGE, signal.getCharge());
            values.put(SignalDatabaseHelper.COLUMN_DIRECTION, signal.getDirection());
            values.put(SignalDatabaseHelper.COLUMN_TEMPERATURE, signal.getTemperature());
            database.insert(SignalDatabaseHelper.TABLE_SIGNAL, null, values);
            close();

            actionCreator.updateLastSignal(signal);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean hasDuplicate(Signal signal) {
        Cursor cursor = database.rawQuery(String.format(SignalDatabaseHelper
                .SignalDatabaseQuery.GET_SIGNALS_BY_DEVICE_ID_AND_DATE, signal.getDeviceId(), signal.getDate()), null);
        int duplicateCount  = cursor.getCount();
        cursor.close();
        return duplicateCount != 0;
    }

    public List<Signal> getSignalsByPeriod(long dateFrom, long dateTo) {
        List<Signal> signals = new ArrayList<>();

        try {
            open();
            Cursor cursor = database.rawQuery(String.format(SignalDatabaseHelper
                    .SignalDatabaseQuery.GET_SIGNALS_BY_PERIOD, dateFrom, dateTo, AppController.getInstance().getActiveDeviceId()), null);
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
            Cursor cursor = database.rawQuery(String.format(SignalDatabaseHelper
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
                    Log.d(TAG, numberOfSignals + " getLastSignalsByDeviceId");
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

        SignalStore.getInstance(Dispatcher.getInstance(EventBus.getDefault())).setSignalsDisplayed(signals);
        return signals;
    }

    public List<Signal> getAllSignals() {
        List<Signal> signals = new ArrayList<>();

        try {
            open();
            Cursor cursor = database.query(SignalDatabaseHelper.TABLE_SIGNAL, allColumns,
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
            database.execSQL(SignalDatabaseHelper.SignalDatabaseQuery.DELETE_ALL_SIGNALS);
            close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public long getLastSignalDate() {
        long lastSignalDate = 0;

        try {
            open();
            Cursor cursor = database.query(SignalDatabaseHelper.TABLE_SIGNAL, allColumns,
                    null, null, null, null, null);
            cursor.moveToLast();
            Signal signal = cursorToSignal(cursor);
            lastSignalDate = signal.getDate();
            Log.d(TAG, lastSignalDate + " getLastSignalDate()");
            close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (CursorIndexOutOfBoundsException e) {
            e.printStackTrace();
            lastSignalDate = 1444435200;
        }

        if (lastSignalDate == 0) {
            lastSignalDate = 1444435200;
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
        signal.setVoltage(cursor.getInt(6));
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
            case SignalActions.INSERT_SIGNAL_TO_DATABASE:
                Signal signal = (Signal) action.getData().get(ActionCreator.KEY_SIGNAL);
                insertSignal(signal);
                break;
            case SignalActions.GET_LAST_SIGNALS_BY_DEVICE_ID_FROM_DATABASE:
                int numberOfSignals = (int) action.getData().get(ActionCreator.KEY_NUMBER_OF_SIGNALS);
                getLastSignalsByDeviceId(numberOfSignals);
                break;
            case SignalActions.GET_SIGNALS_BY_PERIOD:
                long fromDate = ((Date) action.getData().get(ActionCreator.KEY_FROM_DATE)).getTime() / 1000;
                long toDate = ((Date) action.getData().get(ActionCreator.KEY_TO_DATE)).getTime() / 1000;
                getSignalsByPeriod(fromDate, toDate);
                break;
            case SignalActions.GET_LAST_SIGNAL_DATE_FROM_DATABASE:
                long lastSignalDate = getLastSignalDate();
                long now = Calendar.getInstance().getTimeInMillis() / 1000;
                actionCreator.getLastSignalsRequest(lastSignalDate, now);
        }
    }
}
