package kgk.beacon.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import kgk.beacon.R;
import kgk.beacon.database.SignalDatabaseDao;
import kgk.beacon.dispatcher.Dispatcher;
import kgk.beacon.model.Device;
import kgk.beacon.stores.DeviceStore;
import kgk.beacon.stores.SignalStore;
import kgk.beacon.util.AppController;

public class DeviceListFragment extends ListFragment {

    private static final String TAG = DeviceListFragment.class.getSimpleName();

    private Dispatcher dispatcher;
    private DeviceStore deviceStore;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initFluxDependencies();

        DeviceListAdapter adapter = new DeviceListAdapter(getActivity(),
                                            generateDataForAdapter());
        setListAdapter(adapter);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView listView = getListView();
        listView.setBackgroundResource(R.drawable.main_background);
    }

    private void initFluxDependencies() {
        dispatcher = Dispatcher.getInstance(EventBus.getDefault());
        deviceStore = DeviceStore.getInstance(dispatcher);
    }

    private ArrayList<String> generateDataForAdapter() {
        ArrayList<String> data = new ArrayList<>();

        for (Device device : deviceStore.getDevices()) {
            data.add(device.getId());
        }

        return data;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        long activeDevice = Long.parseLong(deviceStore.getDevices().get(position).getId());
        AppController.getInstance().setActiveDeviceId(activeDevice);
        updateLastSignalFromDatabase();

        Intent startInformationActivityIntent = new Intent(getActivity(), InformationActivity.class);
        startActivity(startInformationActivityIntent);
    }

    public class DeviceListAdapter extends ArrayAdapter<String> {

        public DeviceListAdapter(Context context, List<String> items) {
            super(context, R.layout.list_item_devices, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolderDeviceListFragment viewHolder;

            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.list_item_devices, parent, false);

                viewHolder = new ViewHolderDeviceListFragment(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolderDeviceListFragment) convertView.getTag();
            }

            viewHolder.deviceTextView.setText("Actis  " + getItem(position));

            return convertView;
        }
    }

    public static class ViewHolderDeviceListFragment {

        @Bind(R.id.listItemDevices_deviceTextView) TextView deviceTextView;

        public ViewHolderDeviceListFragment(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private void updateLastSignalFromDatabase() {
        SignalStore signalStore = SignalStore.getInstance(Dispatcher.getInstance(EventBus.getDefault()));

        if (SignalDatabaseDao.getInstance(AppController.getInstance()).getLastSignalsByDeviceId(1).size() > 0) {
            signalStore.setSignal(SignalDatabaseDao.getInstance(AppController.getInstance()).getLastSignalsByDeviceId(1).get(0));
        }
    }
}
