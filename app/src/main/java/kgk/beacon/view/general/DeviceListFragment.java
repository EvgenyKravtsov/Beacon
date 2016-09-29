package kgk.beacon.view.general;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatRadioButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import kgk.beacon.R;
import kgk.beacon.actions.ActionCreator;
import kgk.beacon.database.ActisDatabaseDao;
import kgk.beacon.dispatcher.Dispatcher;
import kgk.beacon.model.Device;
import kgk.beacon.model.product.ProductType;
import kgk.beacon.networking.event.DownloadDataInProgressEvent;
import kgk.beacon.stores.ActisStore;
import kgk.beacon.stores.DeviceStore;
import kgk.beacon.util.AppController;
import kgk.beacon.view.actis.InformationActivity;
import kgk.beacon.view.general.adapter.DeviceListAdapter;
import kgk.beacon.view.generator.activity.MainActivity;

/**
 * Контроллер экрана списка устройств
 */
public class DeviceListFragment extends android.support.v4.app.Fragment
                implements DeviceListScreen {

    private static final String TAG = DeviceListFragment.class.getSimpleName();

    private static final String ANDROID_DEVICE_TYPE_LABEL = "Android";
    private static final String KEY_PREFERRED_GROUPING_CHOICE = "key_preferred_grouping_choice";

    @Bind(R.id.fragmentDeviceList_deviceListView) ExpandableListView deviceListView;
    @Bind(R.id.fragmentDeviceList_typeButton) AppCompatRadioButton typeButton;
    @Bind(R.id.fragmentDeviceList_groupButton) AppCompatRadioButton groupButton;

    private Dispatcher dispatcher;
    private DeviceStore deviceStore;

    private ProductType productType;

    private ProgressDialog downloadDataProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO For Actis only release
        // AppController.getInstance().setActiveProductType(ProductType.Actis);

        productType = AppController.getInstance().getActiveProductType();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device_list, container, false);
        ButterKnife.bind(this, view);
        setPreferredGropingForList();
        setRadioButtonsTint();
        typeButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppController.saveBooleanValueToSharedPreferences(KEY_PREFERRED_GROUPING_CHOICE, isChecked);
                setPreferredAdapter();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initFluxDependencies();
        setPreferredAdapter();

        if (productType == ProductType.Actis || productType == ProductType.Generator) {
            deviceListView.expandGroup(0);
        }

        // showDevelopmentProgressDialog();
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Override
    public void onListItemClick(String deviceInfo, Device chosenDevice) {
        AppController.getInstance().setActiveDevice(chosenDevice);
        AppController.getInstance().setActiveDeviceId(Long.parseLong(chosenDevice.getId()));
        AppController.getInstance().setActiveDeviceType(chosenDevice.getType());
        AppController.getInstance().setActiveDeviceModel(chosenDevice.getModel());

        String model = chosenDevice.getModel();
        if (model.equals(AppController.ACTIS_DEVICE_NAME)) {
            updateLastSignalFromDatabase();
            //ActionCreator.getInstance(Dispatcher.getInstance(EventBus.getDefault())).getLastSignalDateFromDatabase();
            startAssociatedUI();
        } else if (model.equals(AppController.TEST_GENERATOR_DEVICE_NAME)) {    // TODO Delete test code
            Intent generatorAppIntent = new Intent(getActivity(), MainActivity.class);
            getActivity().startActivity(generatorAppIntent);
        } else {
            // showDevelopmentProgressDialog();
            ActionCreator.getInstance(dispatcher).sendLastStateForDeviceRequest();
        }
    }

    private void setRadioButtonsTint() {
        ColorStateList colorStateList = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_checked},
                        new int[]{android.R.attr.state_checked}
                },
                new int[] {
                        getResources().getColor(R.color.main_brand_blue), // unchecked
                        Color.WHITE // checked
                }
        );
        typeButton.setSupportButtonTintList(colorStateList);
        groupButton.setSupportButtonTintList(colorStateList);
    }

    private void setPreferredGropingForList() {
        boolean isTypeGroupingChecked = AppController.loadBooleanValueFromSharedPreferences(KEY_PREFERRED_GROUPING_CHOICE);
        typeButton.setChecked(isTypeGroupingChecked);
        if (!isTypeGroupingChecked) {
            typeButton.setChecked(true);
        }
    }

    private void initFluxDependencies() {
        dispatcher = Dispatcher.getInstance(EventBus.getDefault());
        deviceStore = DeviceStore.getInstance(dispatcher);
        EventBus.getDefault().register(this);
    }

    /** Загрузить необходимый адаптер списк с группировкой по типам или группам */
    private void setPreferredAdapter() {
        DeviceListAdapter adapter;

        if (!typeButton.isChecked()) {
            adapter = prepareAdapterByType();
        } else {
            adapter = prepareAdapterByGroup();
        }

        deviceListView.setAdapter(adapter);
        deviceListView.setOnChildClickListener(adapter);
    }

    /** Подгтовить адаптер с группировкой по типам устройств */
    private DeviceListAdapter prepareAdapterByType() {
        List<String> typeList = generateDeviceTypeList();
        HashMap<String, List<String>> devicesByType = generateDeviceListByType(typeList);
        return new DeviceListAdapter(getActivity(), this, typeList, devicesByType);
    }

    /** Подгтовить адаптер с группировкой по пользовательским группам */
    private DeviceListAdapter prepareAdapterByGroup() {
        List<String> groupList = generateDeviceGroupList();
        HashMap<String, List<String>> devicesByGroup = generateDeviceListByGroup(groupList);
        return new DeviceListAdapter(getActivity(), this, groupList, devicesByGroup);
    }

    /** Сгенерировать список типов устройств */
    private List<String> generateDeviceTypeList() {
        Set<String> typeSet = new HashSet<>();

        if (deviceStore.getDevices() != null) {
            for (Device device : deviceStore.getDevices()) {
                if (device.getModel().contains(ANDROID_DEVICE_TYPE_LABEL)) {
                    if (filterProductType(device.getType())) {
                        typeSet.add(ANDROID_DEVICE_TYPE_LABEL);
                    }
                } else {
                    if (filterProductType(device.getType())) {
                        typeSet.add(device.getType());
                    }
                }
            }
        }

        ArrayList<String> typeList = new ArrayList<>();
        typeList.addAll(typeSet);
        Collections.sort(typeList, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return lhs.compareTo(rhs);
            }
        });
        return typeList;
    }

    /** Сгенерировать список устройств по типам */
    private HashMap<String, List<String>> generateDeviceListByType(List<String> typeList) {
        HashMap<String, List<String>> deviceMap = new HashMap<>();

        ArrayList<Device> devices = new ArrayList<>();
        for (Device device : deviceStore.getDevices()) {
            if (filterProductType(device.getType())) {
                devices.add(device);
            }
        }

        List<String> androidDevices = new ArrayList<>();
        Iterator<Device> iterator = devices.iterator();
        while (iterator.hasNext()) {
            Device device = iterator.next();
            if (device.getModel().contains(ANDROID_DEVICE_TYPE_LABEL)) {
                androidDevices.add(AppController.generateDeviceLabel(device));
                iterator.remove();
            }
        }
        deviceMap.put(ANDROID_DEVICE_TYPE_LABEL, androidDevices);

        for (String type : typeList) {
            if (!type.equals(ANDROID_DEVICE_TYPE_LABEL)) {
                List<String> deviceGroup = new ArrayList<>();

                for (Device device : devices) {
                    if (device.getType().equals(type)) {
                        deviceGroup.add(AppController.generateDeviceLabel(device));
                    }
                }

                deviceMap.put(type, deviceGroup);
            }
        }

        return deviceMap;
    }

    /** Сгенерировать список пользовательских групп */
    private List<String> generateDeviceGroupList() {
        Set<String> groupSet = new HashSet<>();

        ArrayList<Device> devices = deviceStore.getDevices();
        if (devices != null) {

            for (Device device : devices) {

                if (filterProductType(device.getType())) {

                    for (String groupName : device.getGroups()) {
                        groupSet.add(groupName);
                    }
                }
            }
        }

        ArrayList<String> groupList = new ArrayList<>();
        groupList.addAll(groupSet);
        Collections.sort(groupList, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return lhs.compareTo(rhs);
            }
        });
        return groupList;
    }

    /** Сгенерировать список устройств по типам */
    private HashMap<String, List<String>> generateDeviceListByGroup(List<String> groupList) {
        List<Device> devices = deviceStore.getDevices();
        HashMap<String, List<String>> deviceMap = new HashMap<>();

        for (String group : groupList) {
            List<String> deviceGroup = new ArrayList<>();

            for (Device device : devices) {
                for (String groupName : device.getGroups()) {
                    if (groupName.equals(group)) {
                        if (filterProductType(device.getType())) {
                            deviceGroup.add(AppController.generateDeviceLabel(device));
                        }
                    }
                }
            }

            deviceMap.put(group, deviceGroup);
        }

        return deviceMap;
    }

    private void updateLastSignalFromDatabase() {
        ActisStore actisStore = ActisStore.getInstance(Dispatcher.getInstance(EventBus.getDefault()));

        if (ActisDatabaseDao.getInstance(AppController.getInstance()).getLastSignalsByDeviceId(1).size() > 0) {
            actisStore.setSignal(ActisDatabaseDao.getInstance(AppController.getInstance()).getLastSignalsByDeviceId(1).get(0));
        }
    }

    private void showDownloadDataProgressDialog() {
        downloadDataProgressDialog = new ProgressDialog(getActivity());
        downloadDataProgressDialog.setTitle(getString(R.string.download_data_progress_dialog_title));
        downloadDataProgressDialog.setMessage(getString(R.string.download_data_progress_dialog_message));
        downloadDataProgressDialog.show();
    }

    private void showDevelopmentProgressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.ActisAlertDialogStyle);
        builder.setTitle(getString(R.string.development_progress_dialog_title))
                .setMessage(getString(R.string.development_progress_dialog_message))
                .setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /** Загрузить интерфейс, соответствующий типу выбранного устройства */
    private void startAssociatedUI() {
        switch (AppController.getInstance().getActiveDeviceType()) {
            case AppController.ACTIS_DEVICE_TYPE:
                Intent startInformationActivityIntent = new Intent(getActivity(), InformationActivity.class);
                startInformationActivityIntent.putExtra("key_target", "from_device_list");
                startActivity(startInformationActivityIntent);
                break;
        }
    }

    private boolean filterProductType(String deviceType) {
        switch (productType) {
            case Actis:
                if (deviceType.equals(AppController.ACTIS_DEVICE_TYPE)) {
                    return true;
                }
                break;
            case Monitoring:
                if (deviceType.equals(AppController.T5_DEVICE_TYPE) ||
                        deviceType.equals(AppController.T6_DEVICE_TYPE)) {
                    return true;
                }
                break;
            case Generator:
                if (deviceType.equals(AppController.GENERATOR_DEVICE_TYPE)) {
                    return true;
                }
                break;
        }

        return false;
    }

    ////

    public void onEventMainThread(DownloadDataInProgressEvent event) {
        switch (event.getStatus()) {
            case Started:
                showDownloadDataProgressDialog();
                break;
            case Success:
                downloadDataProgressDialog.dismiss();
                startAssociatedUI();
                break;
            case Error:
                downloadDataProgressDialog.dismiss();
                Toast.makeText(getActivity(), R.string.download_error_toast, Toast.LENGTH_SHORT).show();
                break;
            case noInternetConnection:
                Toast.makeText(AppController.getInstance().getApplicationContext(), R.string.no_internet_connection_message, Toast.LENGTH_SHORT).show();
                break;
            case DeviceNotFound:
                downloadDataProgressDialog.dismiss();
                startAssociatedUI();
        }
    }
}
