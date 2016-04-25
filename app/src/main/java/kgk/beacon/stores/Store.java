package kgk.beacon.stores;

import kgk.beacon.actions.Action;
import kgk.beacon.dispatcher.Dispatcher;

/**
 * Стандартная реализация базового элемента архитектуры FLUX - абстрактного хранилища
 */
public abstract class Store {

    final Dispatcher dispatcher;

    //// Constructors

    protected Store(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    //// Public methods

    /**
     * Engages reaction on specific action
     */
    public abstract void onAction(Action action);

    //// Package methods

    void emitStoreChange() {
        dispatcher.emitChange(changeEvent());
    }

    abstract StoreChangeEvent changeEvent();

    //// Public interfaces

    /**
     * Objects of this type are events, associated with changes of the store state
     * View classes reacts on this type of events
     */
    public interface StoreChangeEvent {}
}
