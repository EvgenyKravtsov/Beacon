package kgk.beacon.view.general;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import kgk.beacon.R;
import kgk.beacon.model.product.Product;
import kgk.beacon.model.product.ProductFactory;
import kgk.beacon.model.product.ProductType;
import kgk.beacon.monitoring.domain.model.MonitoringEntity;
import kgk.beacon.monitoring.domain.model.MonitoringEntityStatus;
import kgk.beacon.monitoring.domain.model.MonitoringManager;
import kgk.beacon.monitoring.presentation.activity.MapActivity;
import kgk.beacon.util.AppController;
import kgk.beacon.view.general.adapter.ProductListAdapter;

public class ProductActivity extends AppCompatActivity {

    @Bind(R.id.actisAppToolbar) Toolbar toolbar;
    @Bind(R.id.toolbarTitle) TextView toolbarTitle;
    @Bind(R.id.productList) RecyclerView productListRecyclerView;

    ////

    public static final String KEY_PRODUCT_TYPE = "product_type";

    private static final String TAG = ProductActivity.class.getSimpleName();

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

                                // TODO Delete mock data
                                MonitoringManager.getInstance().init(prepareMockListMonitoringEntities25());

                                startActivityIntent = new Intent(ProductActivity.this, MapActivity.class);
                                break;
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

    // TODO Delete test method
    private List<MonitoringEntity> prepareMockListMonitoringEntities5() {
        List<MonitoringEntity> monitoringEntities = new ArrayList<>();

        MonitoringEntity monitoringEntity1 = new MonitoringEntity(
                1,
                "Mercedes-Benz",
                "S600 AMG",
                "E844EB177"
        );
        monitoringEntity1.setLatitude(32.314991);
        monitoringEntity1.setLongitude(43.170776);
        monitoringEntity1.setStatus(MonitoringEntityStatus.OFFLINE);
        monitoringEntity1.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity1.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity1);

        MonitoringEntity monitoringEntity2 = new MonitoringEntity(
                2,
                "Opel",
                "Astra",
                "K333KK09"
        );
        monitoringEntity2.setLatitude(32.417066);
        monitoringEntity2.setLongitude(48.658447);
        monitoringEntity2.setStatus(MonitoringEntityStatus.IN_MOTION);
        monitoringEntity2.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity2.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity2);

        MonitoringEntity monitoringEntity3 = new MonitoringEntity(
                3,
                "VAZ",
                "Седан Баклажан",
                "A001MP100"
        );
        monitoringEntity3.setLatitude(38.117272);
        monitoringEntity3.setLongitude(46.461182);
        monitoringEntity3.setStatus(MonitoringEntityStatus.STOPPED);
        monitoringEntity3.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity3.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity3);

        MonitoringEntity monitoringEntity4 = new MonitoringEntity(
                4,
                "Bugatti",
                "Veyron Super Sport",
                "H387TB99"
        );
        monitoringEntity4.setLatitude(35.960223);
        monitoringEntity4.setLongitude(59.919434);
        monitoringEntity4.setStatus(MonitoringEntityStatus.STOPPED);
        monitoringEntity4.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity4.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity4);

        MonitoringEntity monitoringEntity5 = new MonitoringEntity(
                5,
                "Kawasaki",
                "Ninja 300",
                "C834TX199"
        );
        monitoringEntity5.setLatitude(41.574361);
        monitoringEntity5.setLongitude(76.398926);
        monitoringEntity5.setStatus(MonitoringEntityStatus.IN_MOTION);
        monitoringEntity5.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity5.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity5);

        return monitoringEntities;
    }

    // TODO Delete test method
    private List<MonitoringEntity> prepareMockListMonitoringEntities25() {
        List<MonitoringEntity> monitoringEntities = new ArrayList<>();

        MonitoringEntity monitoringEntity1 = new MonitoringEntity(
                1,
                "Mercedes-Benz",
                "S600 AMG",
                "E844EB177"
        );
        monitoringEntity1.setLatitude(32.314991);
        monitoringEntity1.setLongitude(43.170776);
        monitoringEntity1.setStatus(MonitoringEntityStatus.OFFLINE);
        monitoringEntity1.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity1.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity1);

        MonitoringEntity monitoringEntity2 = new MonitoringEntity(
                2,
                "Opel",
                "Astra",
                "K333KK09"
        );
        monitoringEntity2.setLatitude(32.417066);
        monitoringEntity2.setLongitude(48.658447);
        monitoringEntity2.setStatus(MonitoringEntityStatus.IN_MOTION);
        monitoringEntity2.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity2.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity2);

        MonitoringEntity monitoringEntity3 = new MonitoringEntity(
                3,
                "VAZ",
                "Седан Баклажан",
                "A001MP100"
        );
        monitoringEntity3.setLatitude(38.117272);
        monitoringEntity3.setLongitude(46.461182);
        monitoringEntity3.setStatus(MonitoringEntityStatus.STOPPED);
        monitoringEntity3.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity3.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity3);

        MonitoringEntity monitoringEntity4 = new MonitoringEntity(
                4,
                "Bugatti",
                "Veyron Super Sport",
                "H387TB99"
        );
        monitoringEntity4.setLatitude(35.960223);
        monitoringEntity4.setLongitude(59.919434);
        monitoringEntity4.setStatus(MonitoringEntityStatus.STOPPED);
        monitoringEntity4.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity4.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity4);

        MonitoringEntity monitoringEntity5 = new MonitoringEntity(
                5,
                "Kawasaki",
                "Ninja 300",
                "C834TX199"
        );
        monitoringEntity5.setLatitude(41.574361);
        monitoringEntity5.setLongitude(76.398926);
        monitoringEntity5.setStatus(MonitoringEntityStatus.IN_MOTION);
        monitoringEntity5.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity5.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity5);

        MonitoringEntity monitoringEntity6 = new MonitoringEntity(
                6,
                "Chevrolet",
                "Lacetti",
                "E844HB03"
        );
        monitoringEntity6.setLatitude(42.851806);
        monitoringEntity6.setLongitude(43.324585);
        monitoringEntity6.setStatus(MonitoringEntityStatus.IN_MOTION);
        monitoringEntity6.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity6.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity6);

        MonitoringEntity monitoringEntity7 = new MonitoringEntity(
                7,
                "Opel",
                "Mokka",
                "K333CC09"
        );
        monitoringEntity7.setLatitude(43.826601);
        monitoringEntity7.setLongitude(41.325073);
        monitoringEntity7.setStatus(MonitoringEntityStatus.STOPPED);
        monitoringEntity7.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity7.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity7);

        MonitoringEntity monitoringEntity8 = new MonitoringEntity(
                8,
                "Alfa Romeo",
                "4C",
                "C001CC111"
        );
        monitoringEntity8.setLatitude(44.134913);
        monitoringEntity8.setLongitude(43.346558);
        monitoringEntity8.setStatus(MonitoringEntityStatus.OFFLINE);
        monitoringEntity8.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity8.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity8);

        MonitoringEntity monitoringEntity9 = new MonitoringEntity(
                9,
                "Chevrolet",
                "Corvette",
                "A687TT99"
        );
        monitoringEntity9.setLatitude(45.652448);
        monitoringEntity9.setLongitude(46.367798);
        monitoringEntity9.setStatus(MonitoringEntityStatus.IN_MOTION);
        monitoringEntity9.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity9.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity9);

        MonitoringEntity monitoringEntity10 = new MonitoringEntity(
                10,
                "Honda",
                "NSX",
                "A999AA77"
        );
        monitoringEntity10.setLatitude(47.761484);
        monitoringEntity10.setLongitude(43.192749);
        monitoringEntity10.setStatus(MonitoringEntityStatus.IN_MOTION);
        monitoringEntity10.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity10.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity10);

        MonitoringEntity monitoringEntity11 = new MonitoringEntity(
                11,
                "Lamborghini",
                "Huracan",
                "E834EA177"
        );
        monitoringEntity11.setLatitude(49.030665);
        monitoringEntity11.setLongitude(49.11438);
        monitoringEntity11.setStatus(MonitoringEntityStatus.OFFLINE);
        monitoringEntity11.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity11.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity11);

        MonitoringEntity monitoringEntity12 = new MonitoringEntity(
                12,
                "Jeep",
                "Wrangler",
                "K333BB09"
        );
        monitoringEntity12.setLatitude(53.034607);
        monitoringEntity12.setLongitude(46.598511);
        monitoringEntity12.setStatus(MonitoringEntityStatus.STOPPED);
        monitoringEntity12.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity12.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity12);

        MonitoringEntity monitoringEntity13 = new MonitoringEntity(
                13,
                "Land Rover",
                "Defender Classic",
                "E003KX102"
        );
        monitoringEntity13.setLatitude(53.540307);
        monitoringEntity13.setLongitude(49.509888);
        monitoringEntity13.setStatus(MonitoringEntityStatus.STOPPED);
        monitoringEntity13.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity13.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity13);

        MonitoringEntity monitoringEntity14 = new MonitoringEntity(
                14,
                "Ford",
                "F150",
                "C387CC99"
        );
        monitoringEntity14.setLatitude(52.908902);
        monitoringEntity14.setLongitude(55.090942);
        monitoringEntity14.setStatus(MonitoringEntityStatus.IN_MOTION);
        monitoringEntity14.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity14.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity14);

        MonitoringEntity monitoringEntity15 = new MonitoringEntity(
                15,
                "Yamaha",
                "YBR 150",
                "C134HH199"
        );
        monitoringEntity15.setLatitude(56.212814);
        monitoringEntity15.setLongitude(54.860229);
        monitoringEntity15.setStatus(MonitoringEntityStatus.OFFLINE);
        monitoringEntity15.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity15.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity15);

        MonitoringEntity monitoringEntity16 = new MonitoringEntity(
                16,
                "ВАЗ",
                "2105",
                "A874AA177"
        );
        monitoringEntity16.setLatitude(58.390197);
        monitoringEntity16.setLongitude(57.55188);
        monitoringEntity16.setStatus(MonitoringEntityStatus.IN_MOTION);
        monitoringEntity16.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity16.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity16);

        MonitoringEntity monitoringEntity17 = new MonitoringEntity(
                17,
                "Jaguar",
                "XJ",
                "A333BC09"
        );
        monitoringEntity17.setLatitude(60.228903);
        monitoringEntity17.setLongitude(61.452026);
        monitoringEntity17.setStatus(MonitoringEntityStatus.IN_MOTION);
        monitoringEntity17.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity17.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity17);

        MonitoringEntity monitoringEntity18 = new MonitoringEntity(
                18,
                "Jaguar",
                "F-Type",
                "A999MP100"
        );
        monitoringEntity18.setLatitude(62.764783);
        monitoringEntity18.setLongitude(72.279053);
        monitoringEntity18.setStatus(MonitoringEntityStatus.OFFLINE);
        monitoringEntity18.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity18.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity18);

        MonitoringEntity monitoringEntity19 = new MonitoringEntity(
                19,
                "Lotus",
                "Elise",
                "T143OP99"
        );
        monitoringEntity19.setLatitude(66.748577);
        monitoringEntity19.setLongitude(85.638428);
        monitoringEntity19.setStatus(MonitoringEntityStatus.STOPPED);
        monitoringEntity19.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity19.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity19);

        MonitoringEntity monitoringEntity20 = new MonitoringEntity(
                20,
                "Aston Martin",
                "DB9",
                "E777EE203"
        );
        monitoringEntity20.setLatitude(71.698194);
        monitoringEntity20.setLongitude(95.83374);
        monitoringEntity20.setStatus(MonitoringEntityStatus.IN_MOTION);
        monitoringEntity20.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity20.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity20);

        MonitoringEntity monitoringEntity21 = new MonitoringEntity(
                21,
                "Mercedes-Benz",
                "A45",
                "E321EH177"
        );
        monitoringEntity21.setLatitude(75.039013);
        monitoringEntity21.setLongitude(96.295166);
        monitoringEntity21.setStatus(MonitoringEntityStatus.OFFLINE);
        monitoringEntity21.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity21.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity21);

        MonitoringEntity monitoringEntity22 = new MonitoringEntity(
                22,
                "Dacia",
                "Duster",
                "K444CC09"
        );
        monitoringEntity22.setLatitude(72.906723);
        monitoringEntity22.setLongitude(109.610596);
        monitoringEntity22.setStatus(MonitoringEntityStatus.IN_MOTION);
        monitoringEntity22.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity22.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity22);

        MonitoringEntity monitoringEntity23 = new MonitoringEntity(
                23,
                "ZAZ",
                "Sub-Zero",
                "B002MP100"
        );
        monitoringEntity23.setLatitude(68.479926);
        monitoringEntity23.setLongitude(114.620361);
        monitoringEntity23.setStatus(MonitoringEntityStatus.STOPPED);
        monitoringEntity23.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity23.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity23);

        MonitoringEntity monitoringEntity24 = new MonitoringEntity(
                24,
                "McLaren",
                "P13",
                "H987KK99"
        );
        monitoringEntity24.setLatitude(54.316523);
        monitoringEntity24.setLongitude(94.932861);
        monitoringEntity24.setStatus(MonitoringEntityStatus.STOPPED);
        monitoringEntity24.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity24.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity24);

        MonitoringEntity monitoringEntity25 = new MonitoringEntity(
                25,
                "KTM",
                "Duke 250",
                "H556TX177"
        );
        monitoringEntity25.setLatitude(46.362093);
        monitoringEntity25.setLongitude(87.154541);
        monitoringEntity25.setStatus(MonitoringEntityStatus.IN_MOTION);
        monitoringEntity25.setLastUpdateTimestamp(Calendar.getInstance().getTimeInMillis() / 1000);
        monitoringEntity25.setDisplayEnabled(true);
        monitoringEntities.add(monitoringEntity25);

        return monitoringEntities;
    }
}
