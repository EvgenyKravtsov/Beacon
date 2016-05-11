package kgk.beacon.view.devices.presenter;

import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import kgk.beacon.R;
import kgk.beacon.actions.ActionCreator;
import kgk.beacon.dispatcher.Dispatcher;
import kgk.beacon.map.MapManager;
import kgk.beacon.map.event.MapChangeEvent;
import kgk.beacon.map.event.MapReadyForUseEvent;
import kgk.beacon.model.DataForDetailReportRequest;
import kgk.beacon.model.T5Packet;
import kgk.beacon.model.T6Packet;
import kgk.beacon.model.UniversalPacket;
import kgk.beacon.networking.event.DownloadDataInProgressEvent;
import kgk.beacon.stores.PacketsForDetailReportEvent;
import kgk.beacon.stores.PacketsForTrackReadyEvent;
import kgk.beacon.stores.T5Store;
import kgk.beacon.stores.T6Store;
import kgk.beacon.util.AppController;
import kgk.beacon.view.devices.MonitoringView;

public class MonitoringPresenter {

    private MonitoringView view;

    ////

    public MonitoringPresenter(MonitoringView view) {
        this.view = view;
        Dispatcher.getInstance(EventBus.getDefault()).register(this);
    }

    ////

    public void unsubscribe() {
        view = null;
        Dispatcher.getInstance(EventBus.getDefault()).unregister(this);
    }

    public void sendLastStateForDeviceRequest() {
        ActionCreator.getInstance(Dispatcher.getInstance(EventBus.getDefault()))
                .sendLastStateForDeviceRequest();
    }

    public void sendGetLastSignalsRequest(long dateFrom, long dateTo) {
        ActionCreator.getInstance(Dispatcher.getInstance(EventBus.getDefault()))
                .getLastSignalsRequest(dateFrom, dateTo);
    }

    public void sendDetailReportRequest(DataForDetailReportRequest data) {
        ActionCreator.getInstance(Dispatcher.getInstance(EventBus.getDefault()))
                .sendDetailReportRequest(data);
    }

    ////

    public void onEvent(MapReadyForUseEvent event) {
        view.setMap(event.getMap());

        int direction = 0;
        double latitude = 0;
        double longitude = 0;

        switch (AppController.getInstance().getActiveDeviceType()) {
            case AppController.T6_DEVICE_TYPE:
                T6Store t6Store = T6Store.getInstance(Dispatcher.getInstance(EventBus.getDefault()));
                direction = t6Store.getLastStatePacket().getDirection();
                latitude = t6Store.getLastStatePacket().getLatitude();
                longitude = t6Store.getLastStatePacket().getLongitude();
                break;
            case AppController.T5_DEVICE_TYPE:
                T5Store t5Store = T5Store.getInstance(Dispatcher.getInstance(EventBus.getDefault()));
                direction = t5Store.getLastStatePacket().getDirection();
                latitude = t5Store.getLastStatePacket().getLatitude();
                longitude = t5Store.getLastStatePacket().getLongitude();
                break;
        }

        view.addCustomMarkerPoint(direction, latitude, longitude);

        LatLng cameraCoordinates = MapManager.getCameraCoordinates();
        int cameraZoom = MapManager.getCameraZoom();
        if (cameraCoordinates != null && cameraZoom != 0) {
            view.moveCamera(cameraCoordinates.latitude, cameraCoordinates.longitude, cameraZoom);
        } else {
            view.moveCamera(latitude, longitude, 13);
        }
    }

    public void onEvent(MapChangeEvent event) {
        view.changeMap(event.getPreferredMap());
        ActionCreator.getInstance(Dispatcher.getInstance(EventBus.getDefault()))
                .sendLastStateForDeviceRequest();
    }

    public void onEvent(T6Store.T6StoreChangeEvent event) {
        if (!view.isTrackShown()) {
            T6Store t6Store = T6Store.getInstance(Dispatcher.getInstance(EventBus.getDefault()));
            int direction = t6Store.getLastStatePacket().getDirection();
            double latitude = t6Store.getLastStatePacket().getLatitude();
            double longitude = t6Store.getLastStatePacket().getLongitude();
            view.clearMap();
            view.addCustomMarkerPoint(direction, latitude, longitude);
            LatLng cameraCoordinates = MapManager.getCameraCoordinates();
            int cameraZoom = MapManager.getCameraZoom();
            if (cameraCoordinates != null && cameraZoom != 0) {
                view.moveCamera(cameraCoordinates.latitude, cameraCoordinates.longitude, cameraZoom);
            } else {
                view.moveCamera(latitude, longitude, 13);
            }
        }
    }

    public void onEvent(T5Store.T5StoreChangeEvent event) {
        if (!view.isTrackShown()) {
            T5Store t5Store = T5Store.getInstance(Dispatcher.getInstance(EventBus.getDefault()));
            int direction = t5Store.getLastStatePacket().getDirection();
            double latitude = t5Store.getLastStatePacket().getLatitude();
            double longitude = t5Store.getLastStatePacket().getLongitude();
            view.clearMap();
            view.addCustomMarkerPoint(direction, latitude, longitude);
            LatLng cameraCoordinates = MapManager.getCameraCoordinates();
            int cameraZoom = MapManager.getCameraZoom();
            if (cameraCoordinates != null && cameraZoom != 0) {
                view.moveCamera(cameraCoordinates.latitude, cameraCoordinates.longitude, cameraZoom);
            } else {
                view.moveCamera(latitude, longitude, 13);
            }
        }
    }

    public void onEvent(PacketsForTrackReadyEvent event) {
        view.setTrackShown(true);

        ArrayList<UniversalPacket> packets = new ArrayList<>();
        switch (AppController.getInstance().getActiveDeviceType()) {
            case AppController.T5_DEVICE_TYPE:
                for (T5Packet packet : T5Store.getInstance(Dispatcher.getInstance(EventBus.getDefault())).getPacketsForTrack()) {
                    UniversalPacket universalPacket = new UniversalPacket();
                    universalPacket.setLatitude(packet.getLatitude());
                    universalPacket.setLongitude(packet.getLongitude());
                    universalPacket.setDirection(packet.getDirection());
                    if (packet.getLatitude() != 0 && packet.getLongitude() != 0
                            && packet.getSatellites() != 0) {
                        packets.add(universalPacket);
                    }
                }
                view.drawPath(packets);
                break;
            case AppController.T6_DEVICE_TYPE:
                for (T6Packet packet : T6Store.getInstance(Dispatcher.getInstance(EventBus.getDefault())).getPacketsForTrack()) {
                    UniversalPacket universalPacket = new UniversalPacket();
                    universalPacket.setLatitude(packet.getLatitude());
                    universalPacket.setLongitude(packet.getLongitude());
                    universalPacket.setDirection(packet.getDirection());
                    if (packet.getLatitude() != 0 && packet.getLongitude() != 0
                            && packet.getSatellites() != 0) {
                        packets.add(universalPacket);
                    }
                }
                view.drawPath(packets);
                break;
        }
    }

    public void onEvent(PacketsForDetailReportEvent event) {
        view.setTrackShown(true);
        view.clearMap();
        view.addPolyline(event.getCoordinatesForMoving());

        for (LatLng stopCoordinate : event.getCoordinatesForStops()) {
            view.addStopMarker(stopCoordinate.latitude, stopCoordinate.longitude);
        }

        LatLng coordinatesForCameraPoint = event.getCoordinatesForMoving().get(0);
        view.moveCamera(coordinatesForCameraPoint.latitude, coordinatesForCameraPoint.longitude, 13);
    }

    public void onEventMainThread(DownloadDataInProgressEvent event) {
        Context context = AppController.getInstance().getApplicationContext();

        switch (event.getStatus()) {
            case Started:
                view.showDownloadDataProgressDialog();
                break;
            case Success:
                view.dismissDownloadProgressDialog();
                break;
            case Error:
                view.dismissDownloadProgressDialog();
                view.showToastMessage(context.getString(R.string.download_error_toast));
                break;
            case noInternetConnection:
                Toast.makeText(AppController.getInstance().getApplicationContext(), R.string.no_internet_connection_message, Toast.LENGTH_SHORT).show();
                break;
            case DeviceNotFound:
                view.dismissDownloadProgressDialog();
                view.showToastMessage(context.getString(R.string.no_data_for_device_message));
                break;
        }
    }
}
