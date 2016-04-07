package kgk.beacon.view.generator.presenter;

import de.greenrobot.event.EventBus;
import kgk.beacon.actions.ActionCreator;
import kgk.beacon.dispatcher.Dispatcher;
import kgk.beacon.view.generator.activity.MainView;

public class MainViewPresenter {

    private MainView view;

    public MainViewPresenter(MainView view) {
        this.view = view;
    }

    public void sendManualModeCommand() {
        ActionCreator.getInstance(Dispatcher.getInstance(EventBus.getDefault()))
                .sendManualModeCommandToGenerator();
    }

    public void sendAutoModeCommand() {
        ActionCreator.getInstance(Dispatcher.getInstance(EventBus.getDefault()))
                .sendAutoModeCommandToGenerator();
    }

    public void sendEmergencyStopCommand() {
        ActionCreator.getInstance(Dispatcher.getInstance(EventBus.getDefault()))
                .sendEmergencyStopCommandToGenerator();
    }

    public void sendSwitchOnCommand() {
        ActionCreator.getInstance(Dispatcher.getInstance(EventBus.getDefault()))
                .sendSwitchOnCommandToGenerator();
    }

    public void sendSwitchOffCommand() {
        ActionCreator.getInstance(Dispatcher.getInstance(EventBus.getDefault()))
                .sendSwitchOffCommandToGenerator();
    }
}
