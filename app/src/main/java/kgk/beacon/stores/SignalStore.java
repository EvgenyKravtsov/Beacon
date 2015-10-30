package kgk.beacon.stores;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import kgk.beacon.actions.Action;
import kgk.beacon.actions.ActionCreator;
import kgk.beacon.actions.SignalActions;
import kgk.beacon.database.SignalDatabaseDao;
import kgk.beacon.dispatcher.Dispatcher;
import kgk.beacon.model.Signal;
import kgk.beacon.util.AppController;

public class SignalStore extends Store {

    public static final String TAG = SignalStore.class.getSimpleName();

    private static SignalStore instance;
    private SignalDatabaseDao signalDatabaseDao;
    private ActionCreator actionCreator;

    private List<Signal> signalsDisplayed;
    private Signal signal;

    protected SignalStore(Dispatcher dispatcher) {
        super(dispatcher);
        actionCreator = ActionCreator.getInstance(Dispatcher.getInstance(EventBus.getDefault()));
        signalsDisplayed = new ArrayList<>();
        signalDatabaseDao = SignalDatabaseDao.getInstance(AppController.getInstance());
    }

    public static SignalStore getInstance(Dispatcher dispatcher) {
        if (instance == null) {
            instance = new SignalStore(dispatcher);
        }

        return instance;
    }

    public List<Signal> getSignalsDisplayed() {
        return signalsDisplayed;
    }

    public void setSignalsDisplayed(List<Signal> signalsDisplayed) {
        this.signalsDisplayed = signalsDisplayed;
    }

    public Signal getSignal() {
        return signal;
    }

    public void setSignal(Signal signal) {
        this.signal = signal;
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
                signalsDisplayed.clear();
                signalsDisplayed = (List<Signal>) action.getData().get(ActionCreator.KEY_SIGNALS);
                emitStoreChange();
                break;
        }
    }

    private void updateLastSignal(Signal signal) {
        this.signal = signal;
    }

    public class SignalStoreChangeEvent implements StoreChangeEvent {}
}





































