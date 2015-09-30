package kgk.beacon.test;

import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

import kgk.beacon.model.Signal;
import kgk.beacon.util.DateFormatter;

public class DateBank {

    public static final String TAG = DateBank.class.getSimpleName();

    private static DateBank instance;

    private ArrayList<Signal> signals;

    private DateBank() {
        signals = new ArrayList<>();

        for (int i = 0; i < 40; i++) {
            Signal signal = new Signal();

            Date date = new Date();
            date.setTime(date.getTime() + (i * 36_000_000));
            signal.setDate(date.getTime());

            signals.add(signal);
        }
    }

    public static DateBank getInstance() {
        if (instance == null) {
            instance = new DateBank();
        }

        return instance;
    }

    public ArrayList<Signal> getSignals() {
        return signals;
    }

    public void logList() {
        for (Signal signal : signals) {
            Log.d(TAG, DateFormatter.formatDateAndTime(new Date(signal.getDate())));
        }
    }
}
