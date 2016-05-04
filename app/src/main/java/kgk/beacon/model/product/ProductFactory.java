package kgk.beacon.model.product;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.greenrobot.event.EventBus;
import kgk.beacon.R;
import kgk.beacon.dispatcher.Dispatcher;
import kgk.beacon.model.Device;
import kgk.beacon.stores.DeviceStore;
import kgk.beacon.util.AppController;

public class ProductFactory {

    public static List<Product> provideProductList() {
        List<Product> products = new ArrayList<>();

        List<Device> devices = DeviceStore.getInstance(Dispatcher.getInstance(EventBus.getDefault()))
                .getDevices();
        Set<String> deviceTypes = new HashSet<>();
        for (Device device : devices) {
            deviceTypes.add(device.getType());
        }

        Product actisProduct = new Product();
        actisProduct.setProductType(ProductType.Actis);
        actisProduct.setImage(AppController.getInstance()
                .getResources().getDrawable(R.drawable.dummy_image_placeholder));
        actisProduct.setTitle("ACTIS PRODUCT TITLE");
        actisProduct.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit." +
                " Ut gravida eros tortor, at dapibus nulla commodo tincidunt. Phasellus posuere " +
                "ornare ex, nec elementum enim cursus vitae. Pellentesque habitant morbi tristique " +
                "senectus et netus et malesuada fames ac turpis egestas. Aenean ut finibus sem. " +
                "Pellentesque eget fermentum nisl.");
        if (deviceTypes.contains(AppController.ACTIS_DEVICE_TYPE)) {
            actisProduct.setStatus(true);
        }
        products.add(actisProduct);

        Product monitoringProduct = new Product();
        monitoringProduct.setProductType(ProductType.Monitoring);
        monitoringProduct.setImage(AppController.getInstance()
                .getResources().getDrawable(R.drawable.dummy_image_placeholder));
        monitoringProduct.setTitle("MONITORING PRODUCT TITLE");
        monitoringProduct.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit." +
                " Ut gravida eros tortor, at dapibus nulla commodo tincidunt. Phasellus posuere " +
                "ornare ex, nec elementum enim cursus vitae. Pellentesque habitant morbi tristique " +
                "senectus et netus et malesuada fames ac turpis egestas. Aenean ut finibus sem. " +
                "Pellentesque eget fermentum nisl.");
        if (deviceTypes.contains(AppController.T5_DEVICE_TYPE) ||
                deviceTypes.contains(AppController.T6_DEVICE_TYPE)) {
            monitoringProduct.setStatus(true);
        }
        products.add(monitoringProduct);

        Product generatorProduct = new Product();
        generatorProduct.setProductType(ProductType.Generator);
        generatorProduct.setImage(AppController.getInstance()
                .getResources().getDrawable(R.drawable.dummy_image_placeholder));
        generatorProduct.setTitle("GENERATOR PRODUCT TITLE");
        generatorProduct.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit." +
                " Ut gravida eros tortor, at dapibus nulla commodo tincidunt. Phasellus posuere " +
                "ornare ex, nec elementum enim cursus vitae. Pellentesque habitant morbi tristique " +
                "senectus et netus et malesuada fames ac turpis egestas. Aenean ut finibus sem. " +
                "Pellentesque eget fermentum nisl.");
        if (deviceTypes.contains(AppController.GENERATOR_DEVICE_TYPE)) {
            generatorProduct.setStatus(true);
        }
        products.add(generatorProduct);

        return products;
    }
}
