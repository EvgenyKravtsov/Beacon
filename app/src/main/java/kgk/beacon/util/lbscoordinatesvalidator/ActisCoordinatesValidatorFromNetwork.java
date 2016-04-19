package kgk.beacon.util.lbscoordinatesvalidator;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.greenrobot.event.EventBus;
import kgk.beacon.actions.ActionCreator;
import kgk.beacon.database.ActisDatabaseDao;
import kgk.beacon.dispatcher.Dispatcher;
import kgk.beacon.model.Signal;
import kgk.beacon.networking.event.ValidatedCoordinatesReceivedEvent;
import kgk.beacon.util.AppController;

public class ActisCoordinatesValidatorFromNetwork implements LbsCoordinatesValidator {

    private static final String TAG = ActisCoordinatesValidatorFromNetwork.class.getSimpleName();

    // Module dependencies
    private ActionCreator actionCreator;
    private ActisDatabaseDao actisDatabaseDao;

    private List<Signal> signals;
    private List<Signal> signalsToValidate;
    private int signalsToValidateCount;
    private int signalsValidated;

    ////

    public ActisCoordinatesValidatorFromNetwork(List<Signal> signals) {
        actionCreator = ActionCreator.getInstance(Dispatcher.getInstance(EventBus.getDefault()));
        actisDatabaseDao = ActisDatabaseDao.getInstance(AppController.getInstance().getApplicationContext());
        this.signals = signals;

        // TODO Delete test code
        Log.d(TAG, "Raw signals");
        for (Signal signal : signals) {
            Log.d(TAG, signal.toString());
        }
    }

    ////

    @Override
    public void validate() {
        sortSignals();
        signalsToValidate = extractBadSignals();
        writeNormalSignalsToDatabase();
        sendValidationRequests();
    }

    ////

    private void sortSignals() {
        Collections.sort(signals, new Comparator<Signal>() {
            @Override
            public int compare(Signal lhs, Signal rhs) {
                if (lhs.getDate() < rhs.getDate()) return -1;
                if (lhs.getDate() > rhs.getDate()) return 1;
                if (lhs.getDate() == rhs.getDate()) return 0;
                return 0;
            }
        });
    }

    private List<Signal> extractBadSignals() {
        List<Signal> signalsToValidate = new ArrayList<>();

        Signal signalToCompare = null;

        try {
            signalToCompare = actisDatabaseDao.getLastSignalsByDeviceId(1).get(0);
        } catch (IndexOutOfBoundsException iobe) {
            iobe.printStackTrace();
        }

        if (signalToCompare != null) {
            Signal firstSignal = signals.get(0);
            if (firstSignal.getActisDate() == signalToCompare.getActisDate()) {
                if (firstSignal.getDate() != signalToCompare.getDate()) {
                    signalsToValidate.add(firstSignal);

                    // TODO Delete test code
                    Log.d(TAG, "first signal added to validation queue");
                } else {
                    // TODO Delete test code
                    Log.d(TAG, "Nevermind");
                }
            }
        } else {
            // TODO Delete test code
            Log.d(TAG, "Signal to compare is null");
        }

        for (int i = 0; i < signals.size() - 1; i++) {
            Signal signal = signals.get(i);
            Signal nextSignal = signals.get(i + 1);
            if (signal.getActisDate() == nextSignal.getActisDate() && nextSignal.getCellId() != null) {
                signalsToValidate.add(nextSignal);
            }
        }
        signals.removeAll(signalsToValidate);
        return signalsToValidate;
    }

    private void writeNormalSignalsToDatabase() {
        Thread databaseWriterThread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (Signal signal : signals) {
                    actisDatabaseDao.insertSignal(signal);
                }
                actionCreator.updateLastSignal(actisDatabaseDao.getLastSignalsByDeviceId(1).get(0));
            }
        });
        databaseWriterThread.start();
    }

    private void sendValidationRequests() {
        signalsToValidateCount = signalsToValidate.size();
        signalsValidated = 0;
        EventBus.getDefault().register(this);

        for (Signal signal : signalsToValidate) {
            actionCreator.sendActisCoordinatesValidationRequest(signal.getDate(),
                    signal.getMcc(),
                    signal.getMnc(),
                    signal.getCellId(),
                    signal.getLac());
        }
    }

    ////

    public void onEventBackgroundThread(ValidatedCoordinatesReceivedEvent event) {
        signalsValidated++;

        Signal signalToChange = null;
        for (Signal signal : signalsToValidate) {
            if (signal.getDate() == event.getServerDate()) {
                signalToChange = signal;
                signalToChange.setLatitude(event.getLatitude());
                signalToChange.setLongitude(event.getLongitude());
            }
        }

        if (signalToChange != null) {
            actisDatabaseDao.insertSignal(signalToChange);
        }

        if (signalsValidated == signalsToValidateCount) {
            signalsValidated = 0;
            signalsToValidateCount = 0;
            EventBus.getDefault().unregister(this);
            actionCreator.updateLastSignal(actisDatabaseDao.getLastSignalsByDeviceId(1).get(0));
        }
    }
}
