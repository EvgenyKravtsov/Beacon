package kgk.beacon.view.devices;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import kgk.beacon.R;
import kgk.beacon.actions.ActionCreator;
import kgk.beacon.dispatcher.Dispatcher;
import kgk.beacon.map.Map;
import kgk.beacon.map.MapManager;
import kgk.beacon.map.event.MapChangeEvent;
import kgk.beacon.map.event.MapReadyForUseEvent;
import kgk.beacon.model.T5Packet;
import kgk.beacon.model.T6Packet;
import kgk.beacon.networking.event.DownloadDataInProgressEvent;
import kgk.beacon.stores.PacketsForDetailReportEvent;
import kgk.beacon.stores.PacketsForTrackReadyEvent;
import kgk.beacon.stores.T5Store;
import kgk.beacon.stores.T6Store;
import kgk.beacon.util.AppController;
import kgk.beacon.util.DateFormatter;

public class DeviceCurrentLocationActivity extends AppCompatActivity {

    private  static final String TAG = DeviceCurrentLocationActivity.class.getSimpleName();

    @Bind(R.id.activityDeviceCurrentLocation_drawerLayout) DrawerLayout drawerLayout;
    @Bind(R.id.activityDeviceCurrentLocation_trackButton) Button trackButton;
    @Bind(R.id.activityDeviceCurrentLocation_deviceModelLabel) TextView deviceModelLabel;
    @Bind(R.id.activityDeviceCurrentLocation_deviceIdLabel) TextView deviceIdLabel;

    private Map map;
    private ProgressDialog downloadDataProgressDialog;
    private boolean isTrackShown;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_current_location);
        ButterKnife.bind(this);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeMap();
    }

    @Override
    public void onResume() {
        super.onResume();
        Dispatcher.getInstance(EventBus.getDefault()).register(this);

        deviceModelLabel.setText(AppController.getInstance().getActiveDeviceModel());
        deviceIdLabel.setText(String.valueOf(AppController.getInstance().getActiveDeviceId()));
    }

    @Override
    public void onPause() {
        Dispatcher.getInstance(EventBus.getDefault()).unregister(this);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_device_current_location, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                AppController.getInstance().setActiveDeviceType(null);
                finish();
                return true;
            case R.id.menuDeviceCurrentLocation_menuButton:
                drawerLayout.openDrawer(GravityCompat.END);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initializeMap() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment mapFragment = fragmentManager
                .findFragmentById(R.id.activityDeviceCurrentLocation_mapContainer);

        if (mapFragment == null) {
            mapFragment = new MapManager().loadPreferredMapFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.activityDeviceCurrentLocation_mapContainer, mapFragment)
                    .commit();
        }
    }

    private void drawPath(ArrayList<UniversalPacket> packets) {
        map.clear();

        ArrayList<LatLng> coordinates = new ArrayList<>();
        for (UniversalPacket packet : packets) {
            double latitude = packet.getLatitude();
            double longitude = packet.getLongitude();
            coordinates.add(new LatLng(latitude, longitude));
        }

        map.addPolyline(coordinates);
        map.moveCamera(coordinates.get(0).latitude, coordinates.get(0).longitude, 13);
    }

    private void showDownloadDataProgressDialog() {
        downloadDataProgressDialog = new ProgressDialog(this);
        downloadDataProgressDialog.setTitle(getString(R.string.download_data_progress_dialog_title));
        downloadDataProgressDialog.setMessage(getString(R.string.download_data_progress_dialog_message));
        downloadDataProgressDialog.show();
    }

    ////

    @OnClick(R.id.activityDeviceCurrentLocation_locationButton)
    public void onLocationButtonClick(View view) {
        isTrackShown = false;
        map.clear();
        ActionCreator.getInstance(Dispatcher.getInstance(EventBus.getDefault())).sendLastStateForDeviceRequest();
        drawerLayout.closeDrawers();
    }

    @OnClick(R.id.activityDeviceCurrentLocation_trackButton)
    public void onTrackButtonClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final DatePicker picker = new DatePicker(this);

        if (android.os.Build.VERSION.SDK_INT >= 11) {
            picker.setCalendarViewShown(false);
        }

        builder.setTitle(getString(R.string.date_picker_dialog_title))
                .setView(picker)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int day = picker.getDayOfMonth();
                        int month = picker.getMonth();
                        int year = picker.getYear();
                        long[] dates = DateFormatter.generateDateInterval(year, month, day);
                        ActionCreator.getInstance(Dispatcher.getInstance(EventBus.getDefault())).getLastSignalsRequest(dates[0], dates[1]);
                        drawerLayout.closeDrawers();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null);

        builder.show();
    }

    @OnClick(R.id.activityDeviceCurrentLocation_reportButton)
    public void onReportButtonClick(View view) {
        isTrackShown = true;
        ActionCreator.getInstance(Dispatcher.getInstance(EventBus.getDefault()))
                .sendDetailReportRequest();
    }

    ////

    public void onEvent(MapReadyForUseEvent event) {
        this.map = event.getMap();

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

        map.addCustomMarkerPoint(direction, latitude, longitude);

        LatLng cameraCoordinates = MapManager.getCameraCoordinates();
        int cameraZoom = MapManager.getCameraZoom();
        if (cameraCoordinates != null && cameraZoom != 0) {
            map.moveCamera(cameraCoordinates.latitude, cameraCoordinates.longitude, cameraZoom);
        } else {
            map.moveCamera(latitude, longitude, 13);
        }
    }

    public void onEvent(MapChangeEvent event) {
        MapManager mapManager = new MapManager();
        mapManager.savePreferredMap(event.getPreferredMap());

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment mapFragment = fragmentManager
                .findFragmentById(R.id.activityDeviceCurrentLocation_mapContainer);

        mapFragment = new MapManager().loadPreferredMapFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.activityDeviceCurrentLocation_mapContainer, mapFragment)
                .commit();

        ActionCreator.getInstance(Dispatcher.getInstance(EventBus.getDefault()))
                .sendLastStateForDeviceRequest();
    }

    public void onEvent(T6Store.T6StoreChangeEvent event) {
        if (!isTrackShown) {
            T6Store t6Store = T6Store.getInstance(Dispatcher.getInstance(EventBus.getDefault()));
            int direction = t6Store.getLastStatePacket().getDirection();
            double latitude = t6Store.getLastStatePacket().getLatitude();
            double longitude = t6Store.getLastStatePacket().getLongitude();
            map.clear();
            map.addCustomMarkerPoint(direction, latitude, longitude);
            LatLng cameraCoordinates = MapManager.getCameraCoordinates();
            int cameraZoom = MapManager.getCameraZoom();
            if (cameraCoordinates != null && cameraZoom != 0) {
                map.moveCamera(cameraCoordinates.latitude, cameraCoordinates.longitude, cameraZoom);
            } else {
                map.moveCamera(latitude, longitude, 13);
            }
        }
    }

    public void onEvent(T5Store.T5StoreChangeEvent event) {
        if (!isTrackShown) {
            T5Store t5Store = T5Store.getInstance(Dispatcher.getInstance(EventBus.getDefault()));
            int direction = t5Store.getLastStatePacket().getDirection();
            double latitude = t5Store.getLastStatePacket().getLatitude();
            double longitude = t5Store.getLastStatePacket().getLongitude();
            map.clear();
            map.addCustomMarkerPoint(direction, latitude, longitude);
            LatLng cameraCoordinates = MapManager.getCameraCoordinates();
            int cameraZoom = MapManager.getCameraZoom();
            if (cameraCoordinates != null && cameraZoom != 0) {
                map.moveCamera(cameraCoordinates.latitude, cameraCoordinates.longitude, cameraZoom);
            } else {
                map.moveCamera(latitude, longitude, 13);
            }
        }
    }

    public void onEvent(PacketsForTrackReadyEvent event) {
        isTrackShown = true;

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
                drawPath(packets);
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
                drawPath(packets);
                break;
        }
    }

    public void onEvent(PacketsForDetailReportEvent event) {
        map.clear();
        map.addPolyline(event.getCoordinatesForMoving());

        for (LatLng stopCoordinate : event.getCoordinatesForStops()) {
            map.addStopMarker(stopCoordinate.latitude, stopCoordinate.longitude);
        }

        LatLng coordinatesForCameraPoint = event.getCoordinatesForMoving().get(0);
        map.moveCamera(coordinatesForCameraPoint.latitude, coordinatesForCameraPoint.longitude, 13);
    }

    public void onEventMainThread(DownloadDataInProgressEvent event) {
        switch (event.getStatus()) {
            case Started:
                showDownloadDataProgressDialog();
                break;
            case Success:
                if (downloadDataProgressDialog != null) {
                    downloadDataProgressDialog.dismiss();
                }
                break;
            case Error:
                downloadDataProgressDialog.dismiss();
                Toast.makeText(this, R.string.download_error_toast, Toast.LENGTH_SHORT).show();
                break;
            case noInternetConnection:
                Toast.makeText(AppController.getInstance().getApplicationContext(), R.string.no_internet_connection_message, Toast.LENGTH_SHORT).show();
                break;
            case DeviceNotFound:
                downloadDataProgressDialog.dismiss();
                Toast.makeText(AppController.getInstance().getApplicationContext(), R.string.no_data_for_device_message, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    ////

    class UniversalPacket {

        private double latitude;
        private double longitude;
        private int direction;

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public int getDirection() {
            return direction;
        }

        public void setDirection(int direction) {
            this.direction = direction;
        }

        @Override
        public String toString() {
            return "Packet for track: "
                    + this.getLatitude() + " | "
                    + this.getLongitude();
        }
    }
}
