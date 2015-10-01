package kgk.beacon.test;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import kgk.beacon.actions.ActionCreator;
import kgk.beacon.dispatcher.Dispatcher;
import kgk.beacon.model.Signal;
import kgk.beacon.util.AppController;

public class SignalInformationTester {

    public static final String TAG = SignalInformationTester.class.getSimpleName();

    private Dispatcher dispatcher;
    private ActionCreator actionCreator;

    public SignalInformationTester(Dispatcher dispatcher, ActionCreator actionCreator) {
        this.dispatcher = dispatcher;
        this.actionCreator = actionCreator;
    }

    public void executeTestingRoutine() {
        Thread testingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Signal signal = new Signal();
                    long deviceId = AppController.getInstance().getActiveDeviceId();

                    Log.d(TAG, "First signal dispatched");
                    signal.setDeviceId(deviceId);
                    signal.setMode(1);
                    signal.setLatitude(55.64331);
                    signal.setLongitude(37.47085);
                    signal.setDate(1443484800);
                    signal.setVoltage(5);
                    signal.setBalance(100);
                    signal.setSatellites(4);
                    signal.setCharge(50);
                    signal.setSpeed(10);
                    signal.setDirection(2);
                    signal.setTemperature(28);
                    actionCreator.insertSignalToDatabase(signal);
                    TimeUnit.SECONDS.sleep(5);

                    Log.d(TAG, "Second signal dispatched");
                    signal.setDeviceId(deviceId);
                    signal.setMode(1);
                    signal.setLatitude(55.65367);
                    signal.setLongitude(37.47437);
                    signal.setDate(1443571200);
                    signal.setVoltage(4);
                    signal.setBalance(900);
                    signal.setSatellites(5);
                    signal.setCharge(60);
                    signal.setSpeed(20);
                    signal.setDirection(3);
                    signal.setTemperature(18);
                    actionCreator.insertSignalToDatabase(signal);
                    TimeUnit.SECONDS.sleep(5);

                    Log.d(TAG, "Third signal dispatched");
                    signal.setDeviceId(deviceId);
                    signal.setMode(1);
                    signal.setLatitude(55.66422);
                    signal.setLongitude(37.48733);
                    signal.setDate(1443657600);
                    signal.setVoltage(5);
                    signal.setBalance(100);
                    signal.setSatellites(4);
                    signal.setCharge(50);
                    signal.setSpeed(10);
                    signal.setDirection(2);
                    signal.setTemperature(28);
                    actionCreator.insertSignalToDatabase(signal);
                    TimeUnit.SECONDS.sleep(5);

                    Log.d(TAG, "Fourth signal dispatched");
                    signal.setDeviceId(deviceId);
                    signal.setMode(1);
                    signal.setLatitude(55.66539);
                    signal.setLongitude(37.53453);
                    signal.setDate(1443744000);
                    signal.setVoltage(3);
                    signal.setBalance(120);
                    signal.setSatellites(7);
                    signal.setCharge(30);
                    signal.setSpeed(126);
                    signal.setDirection(3);
                    signal.setTemperature(38);
                    actionCreator.insertSignalToDatabase(signal);
                    TimeUnit.SECONDS.sleep(5);

                    Log.d(TAG, "Fifth signal dispatched");
                    signal.setDeviceId(deviceId);
                    signal.setMode(1);
                    signal.setLatitude(55.6572);
                    signal.setLongitude(37.562);
                    signal.setDate(1443830400);
                    signal.setVoltage(5);
                    signal.setBalance(100);
                    signal.setSatellites(4);
                    signal.setCharge(50);
                    signal.setSpeed(10);
                    signal.setDirection(2);
                    signal.setTemperature(28);
                    actionCreator.insertSignalToDatabase(signal);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        testingThread.start();
    }
}
