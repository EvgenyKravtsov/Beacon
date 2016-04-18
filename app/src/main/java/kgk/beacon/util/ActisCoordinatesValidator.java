package kgk.beacon.util;

import android.util.Log;

import java.util.Iterator;
import java.util.List;

import de.greenrobot.event.EventBus;
import kgk.beacon.actions.ActionCreator;
import kgk.beacon.dispatcher.Dispatcher;
import kgk.beacon.model.Signal;

public class ActisCoordinatesValidator {

    private static final String TAG = ActisCoordinatesValidator.class.getSimpleName();

    private ActionCreator actionCreator;
    private List<Signal> signals;

    ////

    public ActisCoordinatesValidator(List<Signal> signals) {
        // EventBus.getDefault().register(this);

        actionCreator = ActionCreator.getInstance(Dispatcher.getInstance(EventBus.getDefault()));
        this.signals = signals;
    }

    ////

    public void validate() {
        // excludeEarliestDateFromSignals();

        for (Signal signal : signals) {
            try {
                actionCreator.sendActisCoordinatesValidationRequest(signal.getDate(),
                        signal.getMcc(),
                        signal.getMnc(),
                        signal.getCellId(),
                        signal.getLac());
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "Old formatted packet without LBS data");
            }
        }
    }

    ////

    private void excludeEarliestDateFromSignals() {
        long minDate = signals.get(0).getDate();
        for (Signal signal : signals) {
            if (signal.getDate() < minDate) {
                minDate = signal.getDate();
            }
        }

        Iterator<Signal> iterator = signals.iterator();
        while (iterator.hasNext()) {
            Signal signal = iterator.next();
            if (signal.getDate() == minDate) {
                iterator.remove();
            }
        }
    }

    private void sendValidationRequests() {

    }
}
