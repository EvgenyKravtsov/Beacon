package kgk.beacon.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // TODO Increment database version before release

    private static final String DATABASE_NAME = "signal_database";
    private static final int DATABASE_VERSION = 9;

    static final String TABLE_SIGNAL = "table_signal";
    static final String COLUMN_ID = "_id";
    static final String COLUMN_DEVICE_ID = "device_id";
    static final String COLUMN_MODE = "mode";
    static final String COLUMN_LATITUDE = "latitude";
    static final String COLUMN_LONGITUDE = "longitude";
    static final String COLUMN_DATE = "date";
    static final String COLUMN_ACTIS_DATE = "actis_date";
    static final String COLUMN_VOLTAGE = "voltage";
    static final String COLUMN_BALANCE = "balance";
    static final String COLUMN_SATELLITES = "satellites";
    static final String COLUMN_SPEED = "speed";
    static final String COLUMN_CHARGE = "charge";
    static final String COLUMN_DIRECTION = "direction";
    static final String COLUMN_TEMPERATURE = "temperature";
    static final String COLUMN_MCC = "mcc";
    static final String COLUMN_MNC = "mnc";
    static final String COLUMN_CELL_ID = "cell_id";
    static final String COLUMN_LAC = "lac";

    private static DatabaseHelper instance;

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(SignalDatabaseQuery.DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL(SignalDatabaseQuery.DROP_SIGNAL_TABLE);
        onCreate(database);
    }

    public static class SignalDatabaseQuery {

        public static final String DATABASE_CREATE = "CREATE TABLE "
                + TABLE_SIGNAL + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_DEVICE_ID + " INTEGER NOT NULL, "
                + COLUMN_MODE + " INTEGER NOT NULL, "
                + COLUMN_LATITUDE + " REAL NOT NULL, "
                + COLUMN_LONGITUDE + " REAL NOT NULL, "
                + COLUMN_DATE + " INTEGER NOT NULL, "
                + COLUMN_ACTIS_DATE + " INTEGER NOT NULL, "
                + COLUMN_VOLTAGE + " REAL NOT NULL, "
                + COLUMN_BALANCE + " INTEGER NOT NULL, "
                + COLUMN_SATELLITES + " INTEGER NOT NULL, "
                + COLUMN_SPEED + " INTEGER NOT NULL, "
                + COLUMN_CHARGE + " INTEGER NOT NULL, "
                + COLUMN_DIRECTION + " INTEGER NOT NULL, "
                + COLUMN_TEMPERATURE + " INTEGER NOT NULL, "
                + COLUMN_MCC + " INTEGER, "
                + COLUMN_MNC + " INTEGER, "
                + COLUMN_CELL_ID + " TEXT, "
                + COLUMN_LAC + " TEXT);";

        public static final String DROP_SIGNAL_TABLE = "DROP TABLE IF EXISTS "
                + TABLE_SIGNAL;

        public static final String GET_SIGNALS_BY_PERIOD = "SELECT * FROM " + TABLE_SIGNAL
                + " WHERE " + COLUMN_DATE + " >= %d AND "
                + COLUMN_DATE + " <= %d AND "
                + COLUMN_DEVICE_ID + " = %d";

        public static final String DELETE_ALL_SIGNALS = "DELETE FROM " + TABLE_SIGNAL;

        public static final String GET_SIGNALS_BY_DEVICE_ID_AND_DATE = "SELECT * FROM " + TABLE_SIGNAL
                + " WHERE " + COLUMN_DEVICE_ID + " = %d AND "
                + COLUMN_DATE + " = %d";

        public static final String GET_SIGNALS_BY_DEVICE_ID = "SELECT * FROM " + TABLE_SIGNAL
                + " WHERE " + COLUMN_DEVICE_ID + " = %d ORDER BY " + COLUMN_ACTIS_DATE + " DESC";
    }
}
