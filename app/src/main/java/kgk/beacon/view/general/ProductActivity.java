package kgk.beacon.view.general;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import kgk.beacon.R;
import kgk.beacon.dispatcher.Dispatcher;
import kgk.beacon.model.Device;
import kgk.beacon.model.product.Product;
import kgk.beacon.model.product.ProductFactory;
import kgk.beacon.model.product.ProductType;
import kgk.beacon.monitoring.DependencyInjection;
import kgk.beacon.monitoring.data.Configuration;
import kgk.beacon.monitoring.domain.interactor.InteractorThreadPool;
import kgk.beacon.monitoring.domain.interactor.UpdateMonitoringEntities;
import kgk.beacon.monitoring.domain.model.MonitoringEntity;
import kgk.beacon.monitoring.domain.model.MonitoringEntityGroup;
import kgk.beacon.monitoring.domain.model.MonitoringEntityStatus;
import kgk.beacon.monitoring.domain.model.MonitoringManager;
import kgk.beacon.monitoring.domain.model.User;
import kgk.beacon.monitoring.network.MonitoringHttpClient;
import kgk.beacon.monitoring.presentation.activity.MapActivity;
import kgk.beacon.networking.VolleyHttpClient;
import kgk.beacon.stores.DeviceStore;
import kgk.beacon.util.AppController;
import kgk.beacon.view.general.adapter.ProductListAdapter;

public class ProductActivity extends AppCompatActivity {

    // TODO Do not reupdate monitoring entities on relaunch module during one session

    @Bind(R.id.actisAppToolbar) Toolbar toolbar;
    @Bind(R.id.toolbarTitle) TextView toolbarTitle;
    @Bind(R.id.productList) RecyclerView productListRecyclerView;

    private static boolean monitoringModuleInitialized;

    private ProgressDialog progressDialog;

    ////

    public static final String KEY_PRODUCT_TYPE = "product_type";

    private static final String TAG = ProductActivity.class.getSimpleName();

    private MonitoringManager monitoringManager;

    ////

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        ButterKnife.bind(this);

        prepareToolbar();
        prepareProductList();
    }

    ////

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    ////

    private void prepareToolbar() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.actis_navigation_menu_icon));
        toolbarTitle.setText(getString(R.string.product_activity_title));
    }

    private void prepareProductList() {
        productListRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        // TODO Set proper layout manager
        productListRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        ProductListAdapter adapter = new ProductListAdapter(this,
                ProductFactory.provideProductList(),
                new ProductListAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(Product product) {
                        Intent startActivityIntent = null;
                        switch (product.getProductType()) {
                            case Actis:
                                AppController.getInstance().setActiveProductType(ProductType.Actis);
                                startActivityIntent = new Intent(ProductActivity.this, DeviceListActivity.class);
                                break;
                            case Monitoring:
                                AppController.getInstance().setActiveProductType(ProductType.Monitoring);

                                /**
                                 * Monitoring manager initialization sequence:
                                 *
                                 * 1) Get monitroing entites from device store
                                 * 2) Get user data from server
                                 * 3) Update location data from server
                                 * 4) Start map activity
                                 */

                                toggleProgressDialog(true);
                                prepareMonitoringManager();
                                return;
                            case Generator:
                                AppController.getInstance().setActiveProductType(ProductType.Generator);
                                startActivityIntent = new Intent(ProductActivity.this, DeviceListActivity.class);
                                break;
                        }

                        if (product.getStatus()) {
                            startActivity(startActivityIntent);
                        } else {
                            Toast.makeText(ProductActivity.this, "У вас нет соответствующих устройтсв", Toast.LENGTH_LONG).show();
                        }
                    }
                });
        productListRecyclerView.setAdapter(adapter);
        productListRecyclerView.addOnItemTouchListener(adapter);
    }

    private void prepareMonitoringManager() {
        if (monitoringModuleInitialized) {
            toggleProgressDialog(false);
            Intent intent = new Intent(this, MapActivity.class);
            startActivity(intent);
            return;
        }

        final Configuration configuration = DependencyInjection.provideConfiguration();

        DeviceStore deviceStore = DeviceStore.getInstance(
                Dispatcher.getInstance(EventBus.getDefault()));
        List<Device> devices = deviceStore.getDevices();

        final List<MonitoringEntity> monitoringEntities = new ArrayList<>();
        for (Device device : devices) {
            if (device.getType().equals(AppController.T5_DEVICE_TYPE) ||
                    device.getType().equals(AppController.T6_DEVICE_TYPE)) {

                MonitoringEntity monitoringEntity = new MonitoringEntity(
                        Long.parseLong(device.getId()),
                        device.getMark(),
                        device.getCivilModel(),
                        device.getStateNumber(),
                        device.getGroups());

                monitoringEntity.setDisplayEnabled(
                        configuration.loadDisplayEnabled(monitoringEntity.getId()));
                monitoringEntities.add(monitoringEntity);
            }
        }

        MonitoringHttpClient monitoringHttpClient = VolleyHttpClient.getInstance(this);
        monitoringHttpClient.setListener(new MonitoringHttpClient.Listener() {
            @Override
            public void onUserRetreived(User user) {

                monitoringManager = MonitoringManager.getInstance();
                monitoringManager.init(user, monitoringEntities);

                if (monitoringManager != null) {

                    UpdateMonitoringEntities interactor = new UpdateMonitoringEntities(
                            monitoringManager.getMonitoringEntities());

                    interactor.setListener(new UpdateMonitoringEntities.Listener() {
                        @Override
                        public void onMonitoringEntitiesUpdated(List<MonitoringEntity> monitoringEntities) {
                            for (MonitoringEntityGroup group : monitoringManager.getMonitoringEntityGroups()) {
                                if (group.getName().equals(configuration.loadActiveMonitoringEntityGroup()))
                                    monitoringManager.setActiveMonitoringEntityGroup(group);
                            }

                            long activeMonitoringEntityId = configuration.loadActiveMonitoringEntity();

                            if (activeMonitoringEntityId == 0) {
                                if (monitoringManager.getActiveMonitoringEntityGroup() == null) {
                                    monitoringManager.setActiveMonitoringEntity(
                                            monitoringManager.getMonitoringEntities().get(0)
                                    );
                                } else {
                                    monitoringManager.setActiveMonitoringEntity(
                                            monitoringManager.getMonitoringEntityGroups().get(0)
                                                    .getMonitoringEntities().get(0)
                                    );
                                }
                            } else {
                                for (MonitoringEntity monitoringEntity : monitoringManager.getMonitoringEntities()) {
                                    if (monitoringEntity.getId() == activeMonitoringEntityId) {
                                        monitoringManager.setActiveMonitoringEntity(monitoringEntity);
                                    }
                                }
                            }

                            monitoringModuleInitialized = true;
                            toggleProgressDialog(false);
                            Intent intent = new Intent(ProductActivity.this, MapActivity.class);
                            startActivity(intent);
                        }
                    });

                    InteractorThreadPool.getInstance().execute(interactor);

                } else {
                    // TODO Notify user
                    Log.d("debug", "Monitoring manager is not properly initialized");
                }
            }

            @Override
            public void onMonitoringEntitiesUpdated() {}
        });

        monitoringHttpClient.requestUser();
    }

    private void toggleProgressDialog(boolean status) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Downloading data");
            progressDialog.setIndeterminate(true);
            progressDialog.setCanceledOnTouchOutside(false);
        }

        if (status) progressDialog.show();
        else progressDialog.dismiss();
    }

    // TODO Delete test method
    private List<MonitoringEntity> prepareMockListMonitoringEntities5() {
        List<MonitoringEntity> monitoringEntities = new ArrayList<>();

        List<String> groupNames1 = new ArrayList<>();
        groupNames1.add("test_group_1");

        MonitoringEntity monitoringEntity1 = new MonitoringEntity(
                1,
                "Mercedes-Benz",
                "S600 AMG",
                "E844EB177",
                groupNames1
        );
        monitoringEntity1.setLatitude(32.314991);
        monitoringEntity1.setLongitude(43.170776);
        monitoringEntity1.setStatus(MonitoringEntityStatus.OFFLINE);
        monitoringEntity1.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity1.setSpeed(30);
        monitoringEntity1.setGsm("GOOD");
        monitoringEntity1.setSatellites(7);
        monitoringEntity1.setEngineIgnited(true);
        monitoringEntity1.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity1);

        MonitoringEntity monitoringEntity2 = new MonitoringEntity(
                2,
                "Opel",
                "Astra",
                "K333KK09",
                groupNames1
        );
        monitoringEntity2.setLatitude(32.417066);
        monitoringEntity2.setLongitude(48.658447);
        monitoringEntity2.setStatus(MonitoringEntityStatus.IN_MOTION);
        monitoringEntity2.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity2.setSpeed(30);
        monitoringEntity2.setGsm("GOOD");
        monitoringEntity2.setSatellites(7);
        monitoringEntity2.setEngineIgnited(true);
        monitoringEntity2.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity2);

        MonitoringEntity monitoringEntity3 = new MonitoringEntity(
                3,
                "VAZ",
                "Седан Баклажан",
                "A001MP100",
                groupNames1
        );
        monitoringEntity3.setLatitude(38.117272);
        monitoringEntity3.setLongitude(46.461182);
        monitoringEntity3.setStatus(MonitoringEntityStatus.STOPPED);
        monitoringEntity3.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity3.setSpeed(30);
        monitoringEntity3.setGsm("GOOD");
        monitoringEntity3.setSatellites(7);
        monitoringEntity3.setEngineIgnited(true);
        monitoringEntity3.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity3);

        MonitoringEntity monitoringEntity4 = new MonitoringEntity(
                4,
                "Bugatti",
                "Veyron Super Sport",
                "H387TB99",
                groupNames1
        );
        monitoringEntity4.setLatitude(35.960223);
        monitoringEntity4.setLongitude(59.919434);
        monitoringEntity4.setStatus(MonitoringEntityStatus.STOPPED);
        monitoringEntity4.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity4.setSpeed(30);
        monitoringEntity4.setGsm("GOOD");
        monitoringEntity4.setSatellites(7);
        monitoringEntity4.setEngineIgnited(true);
        monitoringEntity4.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity4);

        MonitoringEntity monitoringEntity5 = new MonitoringEntity(
                5,
                "Kawasaki",
                "Ninja 300",
                "C834TX199",
                groupNames1
        );
        monitoringEntity5.setLatitude(41.574361);
        monitoringEntity5.setLongitude(76.398926);
        monitoringEntity5.setStatus(MonitoringEntityStatus.IN_MOTION);
        monitoringEntity5.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity5.setSpeed(30);
        monitoringEntity5.setGsm("GOOD");
        monitoringEntity5.setSatellites(7);
        monitoringEntity5.setEngineIgnited(true);
        monitoringEntity5.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity5);

        return monitoringEntities;
    }

    // TODO Delete test method
    private List<MonitoringEntity> prepareMockListMonitoringEntities25() {
        List<MonitoringEntity> monitoringEntities = new ArrayList<>();

        List<String> groupNames1 = new ArrayList<>();
        groupNames1.add("test_group_1");
        List<String> groupNames2 = new ArrayList<>();
        groupNames2.add("test_group_2");
        List<String> groupNames3 = new ArrayList<>();
        groupNames3.add("test_group_3");
        List<String> groupNames4 = new ArrayList<>();
        groupNames4.add("test_group_4");
        List<String> groupNames5 = new ArrayList<>();
        groupNames5.add("test_group_1");
        groupNames5.add("test_group_5");


        MonitoringEntity monitoringEntity1 = new MonitoringEntity(
                1,
                "Mercedes-Benz",
                "S600 AMG",
                "E844EB177",
                groupNames1
        );
        monitoringEntity1.setLatitude(32.314991);
        monitoringEntity1.setLongitude(43.170776);
        monitoringEntity1.setStatus(MonitoringEntityStatus.OFFLINE);
        monitoringEntity1.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity1.setSpeed(30);
        monitoringEntity1.setGsm("GOOD");
        monitoringEntity1.setSatellites(7);
        monitoringEntity1.setEngineIgnited(true);
        monitoringEntity1.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity1);

        MonitoringEntity monitoringEntity2 = new MonitoringEntity(
                2,
                "Opel",
                "Astra",
                "K333KK09",
                groupNames1
        );
        monitoringEntity2.setLatitude(32.417066);
        monitoringEntity2.setLongitude(48.658447);
        monitoringEntity2.setStatus(MonitoringEntityStatus.IN_MOTION);
        monitoringEntity2.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity2.setSpeed(30);
        monitoringEntity2.setGsm("GOOD");
        monitoringEntity2.setSatellites(7);
        monitoringEntity2.setEngineIgnited(true);
        monitoringEntity2.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity2);

        MonitoringEntity monitoringEntity3 = new MonitoringEntity(
                3,
                "VAZ",
                "Седан Баклажан",
                "A001MP100",
                groupNames1
        );
        monitoringEntity3.setLatitude(38.117272);
        monitoringEntity3.setLongitude(46.461182);
        monitoringEntity3.setStatus(MonitoringEntityStatus.STOPPED);
        monitoringEntity3.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity3.setSpeed(30);
        monitoringEntity3.setGsm("GOOD");
        monitoringEntity3.setSatellites(7);
        monitoringEntity3.setEngineIgnited(true);
        monitoringEntity3.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity3);

        MonitoringEntity monitoringEntity4 = new MonitoringEntity(
                4,
                "Bugatti",
                "Veyron Super Sport",
                "H387TB99",
                groupNames1
        );
        monitoringEntity4.setLatitude(35.960223);
        monitoringEntity4.setLongitude(59.919434);
        monitoringEntity4.setStatus(MonitoringEntityStatus.STOPPED);
        monitoringEntity4.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity4.setSpeed(30);
        monitoringEntity4.setGsm("GOOD");
        monitoringEntity4.setSatellites(7);
        monitoringEntity4.setEngineIgnited(true);
        monitoringEntity4.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity4);

        MonitoringEntity monitoringEntity5 = new MonitoringEntity(
                5,
                "Kawasaki",
                "Ninja 300",
                "C834TX199",
                groupNames1
        );
        monitoringEntity5.setLatitude(41.574361);
        monitoringEntity5.setLongitude(76.398926);
        monitoringEntity5.setStatus(MonitoringEntityStatus.IN_MOTION);
        monitoringEntity5.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity5.setSpeed(30);
        monitoringEntity5.setGsm("GOOD");
        monitoringEntity5.setSatellites(7);
        monitoringEntity5.setEngineIgnited(true);
        monitoringEntity5.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity5);

        MonitoringEntity monitoringEntity6 = new MonitoringEntity(
                6,
                "Chevrolet",
                "Lacetti",
                "E844HB03",
                groupNames2
        );
        monitoringEntity6.setLatitude(42.851806);
        monitoringEntity6.setLongitude(43.324585);
        monitoringEntity6.setStatus(MonitoringEntityStatus.IN_MOTION);
        monitoringEntity6.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity6.setSpeed(30);
        monitoringEntity6.setGsm("GOOD");
        monitoringEntity6.setSatellites(7);
        monitoringEntity6.setEngineIgnited(true);
        monitoringEntity6.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity6);

        MonitoringEntity monitoringEntity7 = new MonitoringEntity(
                7,
                "Opel",
                "Mokka",
                "K333CC09",
                groupNames2
        );
        monitoringEntity7.setLatitude(43.826601);
        monitoringEntity7.setLongitude(41.325073);
        monitoringEntity7.setStatus(MonitoringEntityStatus.STOPPED);
        monitoringEntity7.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity7.setSpeed(30);
        monitoringEntity7.setGsm("GOOD");
        monitoringEntity7.setSatellites(7);
        monitoringEntity7.setEngineIgnited(true);
        monitoringEntity7.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity7);

        MonitoringEntity monitoringEntity8 = new MonitoringEntity(
                8,
                "Alfa Romeo",
                "4C",
                "C001CC111",
                groupNames2
        );
        monitoringEntity8.setLatitude(44.134913);
        monitoringEntity8.setLongitude(43.346558);
        monitoringEntity8.setStatus(MonitoringEntityStatus.OFFLINE);
        monitoringEntity8.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);monitoringEntity1.setSpeed(30);
        monitoringEntity8.setGsm("GOOD");
        monitoringEntity8.setSatellites(7);
        monitoringEntity8.setEngineIgnited(true);
        monitoringEntity8.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity8);

        MonitoringEntity monitoringEntity9 = new MonitoringEntity(
                9,
                "Chevrolet",
                "Corvette",
                "A687TT99",
                groupNames2
        );
        monitoringEntity9.setLatitude(45.652448);
        monitoringEntity9.setLongitude(46.367798);
        monitoringEntity9.setStatus(MonitoringEntityStatus.IN_MOTION);
        monitoringEntity9.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity9.setSpeed(30);
        monitoringEntity9.setGsm("GOOD");
        monitoringEntity9.setSatellites(7);
        monitoringEntity9.setEngineIgnited(true);
        monitoringEntity9.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity9);

        MonitoringEntity monitoringEntity10 = new MonitoringEntity(
                10,
                "Honda",
                "NSX",
                "A999AA77",
                groupNames2
        );
        monitoringEntity10.setLatitude(47.761484);
        monitoringEntity10.setLongitude(43.192749);
        monitoringEntity10.setStatus(MonitoringEntityStatus.IN_MOTION);
        monitoringEntity10.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity10.setSpeed(30);
        monitoringEntity10.setGsm("GOOD");
        monitoringEntity10.setSatellites(7);
        monitoringEntity10.setEngineIgnited(true);
        monitoringEntity10.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity10);

        MonitoringEntity monitoringEntity11 = new MonitoringEntity(
                11,
                "Lamborghini",
                "Huracan",
                "E834EA177",
                groupNames3
        );
        monitoringEntity11.setLatitude(49.030665);
        monitoringEntity11.setLongitude(49.11438);
        monitoringEntity11.setStatus(MonitoringEntityStatus.OFFLINE);
        monitoringEntity11.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity11.setSpeed(30);
        monitoringEntity11.setGsm("GOOD");
        monitoringEntity11.setSatellites(7);
        monitoringEntity11.setEngineIgnited(true);
        monitoringEntity11.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity11);

        MonitoringEntity monitoringEntity12 = new MonitoringEntity(
                12,
                "Jeep",
                "Wrangler",
                "K333BB09",
                groupNames3
        );
        monitoringEntity12.setLatitude(53.034607);
        monitoringEntity12.setLongitude(46.598511);
        monitoringEntity12.setStatus(MonitoringEntityStatus.STOPPED);
        monitoringEntity12.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity12.setSpeed(30);
        monitoringEntity12.setGsm("GOOD");
        monitoringEntity12.setSatellites(7);
        monitoringEntity12.setEngineIgnited(true);
        monitoringEntity12.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity12);

        MonitoringEntity monitoringEntity13 = new MonitoringEntity(
                13,
                "Land Rover",
                "Defender Classic",
                "E003KX102",
                groupNames3
        );
        monitoringEntity13.setLatitude(53.540307);
        monitoringEntity13.setLongitude(49.509888);
        monitoringEntity13.setStatus(MonitoringEntityStatus.STOPPED);
        monitoringEntity13.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity13.setSpeed(30);
        monitoringEntity13.setGsm("GOOD");
        monitoringEntity13.setSatellites(7);
        monitoringEntity13.setEngineIgnited(true);
        monitoringEntity13.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity13);

        MonitoringEntity monitoringEntity14 = new MonitoringEntity(
                14,
                "Ford",
                "F150",
                "C387CC99",
                groupNames3
        );
        monitoringEntity14.setLatitude(52.908902);
        monitoringEntity14.setLongitude(55.090942);
        monitoringEntity14.setStatus(MonitoringEntityStatus.IN_MOTION);
        monitoringEntity14.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity14.setSpeed(30);
        monitoringEntity14.setGsm("GOOD");
        monitoringEntity14.setSatellites(7);
        monitoringEntity14.setEngineIgnited(true);
        monitoringEntity14.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity14);

        MonitoringEntity monitoringEntity15 = new MonitoringEntity(
                15,
                "Yamaha",
                "YBR 150",
                "C134HH199",
                groupNames3
        );
        monitoringEntity15.setLatitude(56.212814);
        monitoringEntity15.setLongitude(54.860229);
        monitoringEntity15.setStatus(MonitoringEntityStatus.OFFLINE);
        monitoringEntity15.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity15.setSpeed(30);
        monitoringEntity15.setGsm("GOOD");
        monitoringEntity15.setSatellites(7);
        monitoringEntity15.setEngineIgnited(true);
        monitoringEntity15.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity15);

        MonitoringEntity monitoringEntity16 = new MonitoringEntity(
                16,
                "ВАЗ",
                "2105",
                "A874AA177",
                groupNames4
        );
        monitoringEntity16.setLatitude(58.390197);
        monitoringEntity16.setLongitude(57.55188);
        monitoringEntity16.setStatus(MonitoringEntityStatus.IN_MOTION);
        monitoringEntity16.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity16.setSpeed(30);
        monitoringEntity16.setGsm("GOOD");
        monitoringEntity16.setSatellites(7);
        monitoringEntity16.setEngineIgnited(true);
        monitoringEntity16.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity16);

        MonitoringEntity monitoringEntity17 = new MonitoringEntity(
                17,
                "Jaguar",
                "XJ",
                "A333BC09",
                groupNames4
        );
        monitoringEntity17.setLatitude(60.228903);
        monitoringEntity17.setLongitude(61.452026);
        monitoringEntity17.setStatus(MonitoringEntityStatus.IN_MOTION);
        monitoringEntity17.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity17.setSpeed(30);
        monitoringEntity17.setGsm("GOOD");
        monitoringEntity17.setSatellites(7);
        monitoringEntity17.setEngineIgnited(true);
        monitoringEntity17.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity17);

        MonitoringEntity monitoringEntity18 = new MonitoringEntity(
                18,
                "Jaguar",
                "F-Type",
                "A999MP100",
                groupNames4
        );
        monitoringEntity18.setLatitude(62.764783);
        monitoringEntity18.setLongitude(72.279053);
        monitoringEntity18.setStatus(MonitoringEntityStatus.OFFLINE);
        monitoringEntity18.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity18.setSpeed(30);
        monitoringEntity18.setGsm("GOOD");
        monitoringEntity18.setSatellites(7);
        monitoringEntity18.setEngineIgnited(true);
        monitoringEntity18.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity18);

        MonitoringEntity monitoringEntity19 = new MonitoringEntity(
                19,
                "Lotus",
                "Elise",
                "T143OP99",
                groupNames4
        );
        monitoringEntity19.setLatitude(66.748577);
        monitoringEntity19.setLongitude(85.638428);
        monitoringEntity19.setStatus(MonitoringEntityStatus.STOPPED);
        monitoringEntity19.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity19.setSpeed(30);
        monitoringEntity19.setGsm("GOOD");
        monitoringEntity19.setSatellites(7);
        monitoringEntity19.setEngineIgnited(true);
        monitoringEntity19.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity19);

        MonitoringEntity monitoringEntity20 = new MonitoringEntity(
                20,
                "Aston Martin",
                "DB9",
                "E777EE203",
                groupNames4
        );
        monitoringEntity20.setLatitude(71.698194);
        monitoringEntity20.setLongitude(95.83374);
        monitoringEntity20.setStatus(MonitoringEntityStatus.IN_MOTION);
        monitoringEntity20.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity20.setSpeed(30);
        monitoringEntity20.setGsm("GOOD");
        monitoringEntity20.setSatellites(7);
        monitoringEntity20.setEngineIgnited(true);
        monitoringEntity20.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity20);

        MonitoringEntity monitoringEntity21 = new MonitoringEntity(
                21,
                "Mercedes-Benz",
                "A45",
                "E321EH177",
                groupNames5
        );
        monitoringEntity21.setLatitude(75.039013);
        monitoringEntity21.setLongitude(96.295166);
        monitoringEntity21.setStatus(MonitoringEntityStatus.OFFLINE);
        monitoringEntity21.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity21.setSpeed(30);
        monitoringEntity21.setGsm("GOOD");
        monitoringEntity21.setSatellites(7);
        monitoringEntity21.setEngineIgnited(true);
        monitoringEntity21.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity21);

        MonitoringEntity monitoringEntity22 = new MonitoringEntity(
                22,
                "Dacia",
                "Duster",
                "K444CC09",
                groupNames5
        );
        monitoringEntity22.setLatitude(72.906723);
        monitoringEntity22.setLongitude(109.610596);
        monitoringEntity22.setStatus(MonitoringEntityStatus.IN_MOTION);
        monitoringEntity22.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity22.setSpeed(30);
        monitoringEntity22.setGsm("GOOD");
        monitoringEntity22.setSatellites(7);
        monitoringEntity22.setEngineIgnited(true);
        monitoringEntity22.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity22);

        MonitoringEntity monitoringEntity23 = new MonitoringEntity(
                23,
                "ZAZ",
                "Sub-Zero",
                "B002MP100",
                groupNames5
        );
        monitoringEntity23.setLatitude(68.479926);
        monitoringEntity23.setLongitude(114.620361);
        monitoringEntity23.setStatus(MonitoringEntityStatus.STOPPED);
        monitoringEntity23.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity23.setSpeed(30);
        monitoringEntity23.setGsm("GOOD");
        monitoringEntity23.setSatellites(7);
        monitoringEntity23.setEngineIgnited(true);
        monitoringEntity23.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity23);

        MonitoringEntity monitoringEntity24 = new MonitoringEntity(
                24,
                "McLaren",
                "P13",
                "H987KK99",
                groupNames5
        );
        monitoringEntity24.setLatitude(54.316523);
        monitoringEntity24.setLongitude(94.932861);
        monitoringEntity24.setStatus(MonitoringEntityStatus.STOPPED);
        monitoringEntity24.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity24.setSpeed(30);
        monitoringEntity24.setGsm("GOOD");
        monitoringEntity24.setSatellites(7);
        monitoringEntity24.setEngineIgnited(true);
        monitoringEntity24.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity24);

        MonitoringEntity monitoringEntity25 = new MonitoringEntity(
                25,
                "KTM",
                "Duke 250",
                "H556TX177",
                groupNames5
        );
        monitoringEntity25.setLatitude(46.362093);
        monitoringEntity25.setLongitude(87.154541);
        monitoringEntity25.setStatus(MonitoringEntityStatus.IN_MOTION);
        monitoringEntity25.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity25.setSpeed(30);
        monitoringEntity25.setGsm("GOOD");
        monitoringEntity25.setSatellites(7);
        monitoringEntity25.setEngineIgnited(true);
        monitoringEntity25.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity25);

        return monitoringEntities;
    }
}

















































