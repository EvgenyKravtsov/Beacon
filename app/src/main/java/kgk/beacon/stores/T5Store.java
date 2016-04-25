package kgk.beacon.stores;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import kgk.beacon.actions.Action;
import kgk.beacon.actions.ActionCreator;
import kgk.beacon.actions.DataActions;
import kgk.beacon.dispatcher.Dispatcher;
import kgk.beacon.model.T5Packet;
import kgk.beacon.util.AppController;

/**
 * Стандартная реализация базового элемента архитектуры FLUX - хранилища, в данном случае класс
 * абстрагирует состояние устройства типа Т5
 */
public class T5Store extends Store {

    public static final String TAG = T5Store.class.getSimpleName();

    private static T5Store instance;
    private ActionCreator actionCreator;

    private T5Packet lastStatePacket;
    private ArrayList<T5Packet> packetsForTrack;

    protected T5Store(Dispatcher dispatcher) {
        super(dispatcher);
        actionCreator = ActionCreator.getInstance(Dispatcher.getInstance(EventBus.getDefault()));
    }

    public static T5Store getInstance(Dispatcher dispatcher) {
        if (instance == null) {
            instance = new T5Store(dispatcher);
        }

        return instance;
    }

    public T5Packet getLastStatePacket() {
        return lastStatePacket;
    }

    public ArrayList<T5Packet> getPacketsForTrack() {
        return packetsForTrack;
    }

    public void onEvent(Action action) {
        onAction(action);
    }

    public void onAction(Action action) {
        switch (action.getType()) {
            case DataActions.TRANSPORT_LAST_STATE_PACKET_TO_STORE:
                if (AppController.getInstance().getActiveDeviceType().equals(AppController.T5_DEVICE_TYPE)) {
                    lastStatePacket = (T5Packet) action.getData().get(ActionCreator.KEY_LAST_STATE_PACKET);
                    emitStoreChange();
                }
                break;
            case DataActions.TRANSPORT_PACKETS_FOR_TRACK:
                if (AppController.getInstance().getActiveDeviceType().equals(AppController.T5_DEVICE_TYPE)) {
                    packetsForTrack = (ArrayList<T5Packet>) action.getData().get(ActionCreator.KEY_PACKETS_FOR_TRACK);
                    sortPacketsByDate(0, packetsForTrack.size() - 1);
                    EventBus.getDefault().post(new PacketsForTrackReadyEvent());
                    break;
                }
        }
    }

    StoreChangeEvent changeEvent() {
        return new T5StoreChangeEvent();
    }

    public class T5StoreChangeEvent implements StoreChangeEvent {}

    private void sortPacketsByDate(int start, int end) {
        if (start >= end) {
            return;
        }

        int i = start;
        int j = end;
        int cur = i - (i - j) / 2;

        while (i < j) {
            while (i < cur &&
                    packetsForTrack.get(i).getPacketDate() <= packetsForTrack.get(cur).getPacketDate()) {
                i++;
            }
            while (j > cur &&
                    packetsForTrack.get(cur).getPacketDate() <= packetsForTrack.get(j).getPacketDate()) {
                j--;
            }

            if (i < j) {
                long temp = packetsForTrack.get(i).getPacketDate();
                packetsForTrack.get(i).setPacketDate(packetsForTrack.get(j).getPacketDate());
                packetsForTrack.get(j).setPacketDate(temp);

                if (i == cur) {
                    cur = j;
                } else if (j == cur) {
                    cur = i;
                }
            }
        }

        sortPacketsByDate(start, cur);
        sortPacketsByDate(cur + 1, end);
    }
}
