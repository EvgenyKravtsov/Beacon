package kgk.beacon.dispatcher;

import de.greenrobot.event.EventBus;
import kgk.beacon.actions.Action;
import kgk.beacon.stores.Store;

public class Dispatcher {

    private static final String TAG = Dispatcher.class.getSimpleName();

    private static Dispatcher instance;

    private final EventBus bus;

    //// Constructors

    private Dispatcher(EventBus bus) {
        this.bus = bus;
    }

    public static Dispatcher getInstance(EventBus bus) {
        if (instance == null) {
            instance = new Dispatcher(bus);
        }

        return instance;
    }

    //// Public methods

    public void register(final Object cls) {
        bus.register(cls);
    }

    public void unregister(final Object cls) {
        bus.unregister(cls);
    }

    /**
     * Creates an Action object and put it into event bus
     */
    public void dispatch(String type, Object... data) {
        if (isEmpty(type)) {
            throw new IllegalArgumentException("Type must not be empty");
        }

        if (data.length % 2 != 0) {
            throw new IllegalArgumentException("Data must be a valid key/value pairs");
        }

        Action.Builder actionBuilder = Action.type(type);
        int i = 0;
        while (i < data.length) {
            String key = (String) data[i++];
            Object value = data[i++];
            actionBuilder.bundle(key, value);
        }

        post(actionBuilder.build());
    }

    /**
     * Posting a unified event, when state of store is changed and associated view should also change
     */
    public void emitChange(Store.StoreChangeEvent event) {
        post(event);
    }

    //// Private methods

    private void post(final Object event) {
        bus.post(event);
    }

    private boolean isEmpty(String type) {
        return type == null || type.isEmpty();
    }
}







































