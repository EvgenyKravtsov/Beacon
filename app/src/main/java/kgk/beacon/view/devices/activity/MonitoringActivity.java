package kgk.beacon.view.devices.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kgk.beacon.R;
import kgk.beacon.map.Map;
import kgk.beacon.map.MapManager;
import kgk.beacon.model.DataForDetailReportRequest;
import kgk.beacon.model.UniversalPacket;
import kgk.beacon.util.AppController;
import kgk.beacon.view.devices.MonitoringView;
import kgk.beacon.view.devices.presenter.MonitoringPresenter;

/**
 * Экран с текущим местоположеним устройства, предполагается как главный экран мобильного мониторинга
 */
public class MonitoringActivity extends AppCompatActivity implements MonitoringView {

    private  static final String TAG = MonitoringActivity.class.getSimpleName();

    @Bind(R.id.activityDeviceCurrentLocation_drawerLayout) DrawerLayout drawerLayout;
    // @Bind(R.id.activityDeviceCurrentLocation_trackButton) Button trackButton;
    @Bind(R.id.activityDeviceCurrentLocation_deviceModelLabel) TextView deviceModelLabel;
    @Bind(R.id.activityDeviceCurrentLocation_deviceIdLabel) TextView deviceIdLabel;
    @Bind(R.id.actisApp_toolbar) Toolbar toolbar;
    @Bind(R.id.toolbar_title) TextView toolbarTitle;

    private MonitoringPresenter presenter;

    private Map map;
    private ProgressDialog downloadDataProgressDialog;
    private boolean isTrackShown;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring);
        ButterKnife.bind(this);

        toolbar = (Toolbar) findViewById(R.id.actisApp_toolbar);
        setSupportActionBar(toolbar);
        setNavigationButtonIcon();
        toolbarTitle.setText(generateActivityTitle());

        // drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        initializeMap();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (presenter == null) {
            presenter = new MonitoringPresenter(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        deviceModelLabel.setText(AppController.getInstance().getActiveDeviceModel());
        deviceIdLabel.setText(String.valueOf(AppController.getInstance().getActiveDeviceId()));
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unsubscribe();
        presenter = null;
    }

    ////

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
                drawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    ////

    @Override
    public void setMap(Map map) {
        this.map = map;
    }

    @Override
    public void addCustomMarkerPoint(int direction, double latitude, double longitude) {
        map.addCustomMarkerPoint(direction, latitude, longitude);
    }

    @Override
    public void moveCamera(double latitude, double longitude, int zoom) {
        map.moveCamera(latitude, longitude, zoom);
    }

    @Override
    public void changeMap(String prefferedMapName) {
        MapManager mapManager = new MapManager();
        mapManager.savePreferredMap(prefferedMapName);

        FragmentManager fragmentManager = getSupportFragmentManager();
        // Fragment mapFragment = fragmentManager
        //        .findFragmentById(R.id.activityDeviceCurrentLocation_mapContainer);

        Fragment mapFragment = new MapManager().loadPreferredMapFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.activityDeviceCurrentLocation_mapContainer, mapFragment)
                .commit();
    }

    @Override
    public boolean isTrackShown() {
        return isTrackShown;
    }

    @Override
    public void clearMap() {
        map.clear();
    }

    @Override
    public void setTrackShown(boolean trackStatus) {
        isTrackShown = trackStatus;
    }

    @Override
    public void drawPath(List<UniversalPacket> packets) {
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

    @Override
    public void addPolyline(ArrayList<LatLng> coordinates) {
        map.addPolyline(coordinates);
    }

    @Override
    public void addStopMarker(double latitude, double longitude) {
        map.addStopMarker(latitude, longitude);
    }

    @Override
    public void showDownloadDataProgressDialog() {
        downloadDataProgressDialog = new ProgressDialog(this);
        downloadDataProgressDialog.setTitle(getString(R.string.download_data_progress_dialog_title));
        downloadDataProgressDialog.setMessage(getString(R.string.download_data_progress_dialog_message));
        downloadDataProgressDialog.show();
    }

    @Override
    public void dismissDownloadProgressDialog() {
        if (downloadDataProgressDialog != null) {
            downloadDataProgressDialog.dismiss();
        }
    }

    @Override
    public void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void centerMap(double latitude, double longitude) {
        map.moveCamera(latitude, longitude, map.getCurrentZoom());
    }

    ////

//    @OnClick(R.id.activityDeviceCurrentLocation_locationButton)
//    public void onLocationButtonClick(View view) {
//        isTrackShown = false;
//        map.clear();
//        presenter.sendLastStateForDeviceRequest();
//        drawerLayout.closeDrawers();
//    }
//
//    @OnClick(R.id.activityDeviceCurrentLocation_trackButton)
//    public void onTrackButtonClick(final View view) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        final DatePicker picker = new DatePicker(this);
//
//        if (android.os.Build.VERSION.SDK_INT >= 11) {
//            picker.setCalendarViewShown(false);
//        }
//
//        builder.setTitle(getString(R.string.date_picker_dialog_title))
//                .setView(picker)
//                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        int day = picker.getDayOfMonth();
//                        int month = picker.getMonth();
//                        int year = picker.getYear();
//                        long[] dates = DateFormatter.generateDateInterval(year, month, day);
//                        presenter.sendGetLastSignalsRequest(dates[0], dates[1]);
//                        drawerLayout.closeDrawers();
//                    }
//                })
//                .setNegativeButton(android.R.string.cancel, null);
//
//        builder.show();
//    }

    @OnClick(R.id.activityDeviceCurrentLocation_reportButton)
    public void onReportButtonClick(View view) {
        Intent startDetailtReportActivity = new Intent(this, DetailReportActivity.class);
        startActivityForResult(startDetailtReportActivity, 1);
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    ////

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    DataForDetailReportRequest dataForDetailReportRequest = (DataForDetailReportRequest) data
                            .getSerializableExtra(DetailReportActivity.RESULT_INTENT_DETAIL_REPORT);

                    if (presenter == null) {
                        presenter = new MonitoringPresenter(this);
                    }

                    presenter.sendDetailReportRequest(dataForDetailReportRequest);
                }
            }
        }
    }

    ////

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

    protected void setNavigationButtonIcon() {
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.actis_navigation_menu_icon));
    }

    private String generateActivityTitle() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        String divider = dpWidth >= 600 ? " " : "\n";

        String deviceInfoSingleString = AppController.generateDeviceLabel(AppController.getInstance().getActiveDevice());
        String[] deviceInfo = deviceInfoSingleString.split(" ");
        String title = "";
        for (int i = 0; i < deviceInfo.length; i++) {
            if (i != deviceInfo.length - 1) {
                title += deviceInfo[i] + divider;
            }
        }

         return title.trim();
    }
}
