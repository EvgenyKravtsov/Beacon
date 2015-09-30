package kgk.beacon.stores;

import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

import kgk.beacon.actions.Action;
import kgk.beacon.actions.ActionCreator;
import kgk.beacon.actions.SignalActions;
import kgk.beacon.database.SignalDatabaseDao;
import kgk.beacon.dispatcher.Dispatcher;
import kgk.beacon.model.Signal;
import kgk.beacon.test.DateBank;
import kgk.beacon.util.AppController;
import kgk.beacon.util.DateFormatter;
import kgk.beacon.view.HistoryFragment;

public class SignalStore extends Store {

    public static final String TAG = SignalStore.class.getSimpleName();

    private static SignalStore instance;
    private SignalDatabaseDao signalDatabaseDao;

    private ArrayList<Signal> signalsDisplayed;
    private Signal signal;

    protected SignalStore(Dispatcher dispatcher) {
        super(dispatcher);
        signalsDisplayed = new ArrayList<>();
        getDefaultSignals(HistoryFragment.DEFAULT_NUMBER_OF_SIGNALS);
        signalDatabaseDao = SignalDatabaseDao.getInstance(AppController.getInstance());
    }

    public static SignalStore getInstance(Dispatcher dispatcher) {
        if (instance == null) {
            instance = new SignalStore(dispatcher);
        }

        return instance;
    }

    public ArrayList<Signal> getSignalsDisplayed() {
        return signalsDisplayed;
    }

    public Signal getSignal() {
        return signal;
    }

    StoreChangeEvent changeEvent() {
        return new SignalStoreChangeEvent();
    }


    public void onEvent(Action action) {
        onAction(action);
    }

    public void onAction(Action action) {
        switch (action.getType()) {
            case SignalActions.UPDATE_LAST_SIGNAL:
                updateLastSignal((Signal) action.getData().get(ActionCreator.KEY_LAST_SIGNAL));
                emitStoreChange();
                break;
            case SignalActions.REFRESH_SIGNALS_DISPLAYED:
                emitStoreChange();
                break;
            case SignalActions.FILTER_SIGNALS_DISPLAYED:
                filterSignalsDisplayed((Date) action.getData().get("FROM_DATE"), (Date) action.getData().get("TO_DATE"));
                emitStoreChange();
                break;
        }
    }


    private void getDefaultSignals(int numberOfSignals) {
        ArrayList<Signal> signals = DateBank.getInstance().getSignals();
        for (int i = 0; i < numberOfSignals; i++) {
            signalsDisplayed.add(signals.get(signals.size() - (i + 1)));
        }
    }

    private void updateLastSignal(Signal signal) {
        this.signal = signal;
        Log.d(TAG, "Store changes emitted");
    }

    private void filterSignalsDisplayed(Date fromDate, Date toDate) {
        signalsDisplayed.clear();

        ArrayList<Signal> signals = DateBank.getInstance().getSignals();

        for (Signal signal : signals) {
            Date signalDate = new Date(signal.getDate());
            if (signalDate.compareTo(fromDate) >= 0 && signalDate.compareTo(toDate) <= 0) {
                signalsDisplayed.add(signal);
            }
        }

        for (Signal signal : signalsDisplayed) {
            Log.d(TAG, DateFormatter.formatDateAndTime(new Date(signal.getDate())));
        }
    }

    public class SignalStoreChangeEvent implements StoreChangeEvent {}
}





































