package kgk.beacon.stores;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import kgk.beacon.actions.Action;
import kgk.beacon.actions.ActionCreator;
import kgk.beacon.actions.DataActions;
import kgk.beacon.database.ActisDatabaseDao;
import kgk.beacon.dispatcher.Dispatcher;
import kgk.beacon.model.Signal;
import kgk.beacon.util.AppController;

public class ActisStore extends Store {

    public static final String TAG = ActisStore.class.getSimpleName();

    private static ActisStore instance;
    private ActisDatabaseDao actisDatabaseDao;
    private ActionCreator actionCreator;

    private List<Signal> signalsDisplayed;
    private Signal signal;

    protected ActisStore(Dispatcher dispatcher) {
        super(dispatcher);
        actionCreator = ActionCreator.getInstance(Dispatcher.getInstance(EventBus.getDefault()));
        signalsDisplayed = new ArrayList<>();
        actisDatabaseDao = ActisDatabaseDao.getInstance(AppController.getInstance());
    }

    public static ActisStore getInstance(Dispatcher dispatcher) {
        if (instance == null) {
            instance = new ActisStore(dispatcher);
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
        return new ActisStoreChangeEvent();
    }

    public void onEvent(Action action) {
        onAction(action);
    }

    public void onAction(Action action) {
        switch (action.getType()) {
            case DataActions.UPDATE_LAST_SIGNAL:
                updateLastSignal((Signal) action.getData().get(ActionCreator.KEY_LAST_SIGNAL));
                emitStoreChange();
                break;
            case DataActions.REFRESH_SIGNALS_DISPLAYED:
                emitStoreChange();
                break;
            case DataActions.FILTER_SIGNALS_DISPLAYED:
                signalsDisplayed.clear();
                signalsDisplayed = (List<Signal>) action.getData().get(ActionCreator.KEY_SIGNALS);
                emitStoreChange();
                break;
        }
    }

    private void updateLastSignal(Signal signal) {
        this.signal = signal;
    }

    public class ActisStoreChangeEvent implements StoreChangeEvent {}
}





































