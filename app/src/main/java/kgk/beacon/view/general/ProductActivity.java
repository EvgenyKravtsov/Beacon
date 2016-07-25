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
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import kgk.beacon.R;
import kgk.beacon.model.product.Product;
import kgk.beacon.model.product.ProductFactory;
import kgk.beacon.model.product.ProductType;
import kgk.beacon.monitoring.domain.model.MonitoringEntity;
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
                                MonitoringManager.getInstance().init(prepareMockListMonitoringEntities());

                                startActivityIntent = new Intent(ProductActivity.this, MapActivity.class);
                                break;
                            case Generator:
                                AppController.getInstance().setActiveProductType(ProductType.Generator);
                                startActivityIntent = new Intent(ProductActivity.this, DeviceListActivity.class);
                                break;
                            default:
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
    private List<MonitoringEntity> prepareMockListMonitoringEntities() {
        List<MonitoringEntity> monitoringEntities = new ArrayList<>();

        MonitoringEntity monitoringEntity1 = new MonitoringEntity();
        monitoringEntity1.setLatitude(32.314991);
        monitoringEntity1.setLongitude(43.170776);
        monitoringEntities.add(monitoringEntity1);

        MonitoringEntity monitoringEntity2 = new MonitoringEntity();
        monitoringEntity2.setLatitude(32.417066);
        monitoringEntity2.setLongitude(48.658447);
        monitoringEntities.add(monitoringEntity2);

        MonitoringEntity monitoringEntity3 = new MonitoringEntity();
        monitoringEntity3.setLatitude(38.117272);
        monitoringEntity3.setLongitude(46.461182);
        monitoringEntities.add(monitoringEntity3);

        MonitoringEntity monitoringEntity4 = new MonitoringEntity();
        monitoringEntity4.setLatitude(35.960223);
        monitoringEntity4.setLongitude(59.919434);
        monitoringEntities.add(monitoringEntity4);

        MonitoringEntity monitoringEntity5 = new MonitoringEntity();
        monitoringEntity5.setLatitude(41.574361);
        monitoringEntity5.setLongitude(76.398926);
        monitoringEntities.add(monitoringEntity5);

        return monitoringEntities;
    }
}
