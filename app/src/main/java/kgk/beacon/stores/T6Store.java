package kgk.beacon.stores;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import kgk.beacon.actions.Action;
import kgk.beacon.actions.ActionCreator;
import kgk.beacon.actions.DataActions;
import kgk.beacon.dispatcher.Dispatcher;
import kgk.beacon.model.T6Packet;
import kgk.beacon.util.AppController;

/**
 * Стандартная реализация базового элемента архитектуры FLUX - хранилища, в данном случае класс
 * абстрагирует состояние устройства типа Т6
 */
public class T6Store extends Store {

    public static final String TAG = T6Store.class.getSimpleName();

    private static T6Store instance;
    private ActionCreator actionCreator;

    private T6Packet lastStatePacket;
    private ArrayList<T6Packet> packetsForTrack;

    protected T6Store(Dispatcher dispatcher) {
        super(dispatcher);
        actionCreator = ActionCreator.getInstance(Dispatcher.getInstance(EventBus.getDefault()));
    }

    public static T6Store getInstance(Dispatcher dispatcher) {
        if (instance == null) {
            instance = new T6Store(dispatcher);
        }

        return instance;
    }

    public T6Packet getLastStatePacket() {
        return lastStatePacket;
    }

    public ArrayList<T6Packet> getPacketsForTrack() {
        return packetsForTrack;
    }

    public void onEvent(Action action) {
        onAction(action);
    }

    public void onAction(Action action) {
        switch (action.getType()) {
            case DataActions.TRANSPORT_LAST_STATE_PACKET_TO_STORE:
                if (AppController.getInstance().getActiveDeviceType().equals(AppController.T6_DEVICE_TYPE)) {
                    lastStatePacket = (T6Packet) action.getData().get(ActionCreator.KEY_LAST_STATE_PACKET);
                    emitStoreChange();
                }
                break;
            case DataActions.TRANSPORT_PACKETS_FOR_TRACK:
                if (AppController.getInstance().getActiveDeviceType().equals(AppController.T6_DEVICE_TYPE)) {
                    packetsForTrack = (ArrayList<T6Packet>) action.getData().get(ActionCreator.KEY_PACKETS_FOR_TRACK);
                    sortPacketsByDate(0, packetsForTrack.size() - 1);
                    EventBus.getDefault().post(new PacketsForTrackReadyEvent());
                    break;
                }
        }
    }

    StoreChangeEvent changeEvent() {
        return new T6StoreChangeEvent();
    }

    public class T6StoreChangeEvent implements StoreChangeEvent {}

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
