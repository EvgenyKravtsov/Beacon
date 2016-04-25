package kgk.beacon.dispatcher;

import de.greenrobot.event.EventBus;
import kgk.beacon.actions.Action;
import kgk.beacon.stores.Store;

/**
 * Стандартная реализация диспетчера, базового компонента архитектуры FLUX
 */
public class Dispatcher {

    private static final String TAG = Dispatcher.class.getSimpleName();

    private static Dispatcher instance;

    private final EventBus bus;

    private Dispatcher(EventBus bus) {
        this.bus = bus;
    }

    public static Dispatcher getInstance(EventBus bus) {
        if (instance == null) {
            instance = new Dispatcher(bus);
        }

        return instance;
    }

    public void register(final Object cls) {
        bus.register(cls);
    }

    public void unregister(final Object cls) {
        bus.unregister(cls);
    }

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

    public void emitChange(Store.StoreChangeEvent event) {
        post(event);
    }

    private void post(final Object event) {
        bus.post(event);
    }

    private boolean isEmpty(String type) {
        return type == null || type.isEmpty();
    }
}







































