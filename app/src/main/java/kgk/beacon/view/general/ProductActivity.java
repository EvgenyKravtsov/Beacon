package kgk.beacon.view.general;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import kgk.beacon.R;
import kgk.beacon.model.product.Product;
import kgk.beacon.model.product.ProductFactory;
import kgk.beacon.model.product.ProductType;
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
        productListRecyclerView.setLayoutManager(layoutManager);
        ProductListAdapter adapter = new ProductListAdapter(this,
                ProductFactory.provideProductList(),
                new ProductListAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(Product product) {
                        Intent startDeviceListActivityIntent = new Intent(ProductActivity.this, DeviceListActivity.class);

                        switch (product.getProductType()) {
                            case Actis:
                                startDeviceListActivityIntent.putExtra(KEY_PRODUCT_TYPE, ProductType.Actis);
                                break;
                            case Monitoring:
                                startDeviceListActivityIntent.putExtra(KEY_PRODUCT_TYPE, ProductType.Monitoring);
                                break;
                            case Generator:
                                startDeviceListActivityIntent.putExtra(KEY_PRODUCT_TYPE, ProductType.Generator);
                                break;
                        }

                        if (product.getStatus()) {
                            startActivity(startDeviceListActivityIntent);
                        } else {
                            Toast.makeText(ProductActivity.this, "У вас нет соответствующих устройтсв", Toast.LENGTH_LONG).show();
                        }
                    }
                });
        productListRecyclerView.setAdapter(adapter);
        productListRecyclerView.addOnItemTouchListener(adapter);
    }
}
