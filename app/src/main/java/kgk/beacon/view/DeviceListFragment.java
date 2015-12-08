package kgk.beacon.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import kgk.beacon.R;
import kgk.beacon.actions.ActionCreator;
import kgk.beacon.database.SignalDatabaseDao;
import kgk.beacon.dispatcher.Dispatcher;
import kgk.beacon.model.Device;
import kgk.beacon.stores.DeviceStore;
import kgk.beacon.stores.SignalStore;
import kgk.beacon.util.AppController;
import kgk.beacon.util.DownloadDataInProgressEvent;

public class DeviceListFragment extends ListFragment {

    private static final String TAG = DeviceListFragment.class.getSimpleName();

    private Dispatcher dispatcher;
    private DeviceStore deviceStore;

    private ProgressDialog downloadDataProgressDialog;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView listView = getListView();
        listView.setBackgroundResource(R.drawable.main_background);
    }

    @Override
    public void onResume() {
        super.onResume();
        initFluxDependencies();

        DeviceListAdapter adapter = new DeviceListAdapter(getActivity(),
                generateDataForAdapter());
        setListAdapter(adapter);
    }

    private void initFluxDependencies() {
        dispatcher = Dispatcher.getInstance(EventBus.getDefault());
        deviceStore = DeviceStore.getInstance(dispatcher);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    private ArrayList<String> generateDataForAdapter() {
        ArrayList<String> data = new ArrayList<>();

        if (deviceStore.getDevices() != null) {
            for (Device device : deviceStore.getDevices()) {
                data.add(device.getId());
            }
        }

        return data;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        long activeDevice = Long.parseLong(deviceStore.getDevices().get(position).getId());
        AppController.getInstance().setActiveDeviceId(activeDevice);
        updateLastSignalFromDatabase();

        ActionCreator.getInstance(Dispatcher.getInstance(EventBus.getDefault())).getLastSignalDateFromDatabase();
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

    ////

    public void onEventMainThread(DownloadDataInProgressEvent event) {
        switch (event.getStatus()) {
            case Started:
                Log.d(TAG, "Started");
                showDownloadDataProgressDialog();
                break;
            case Success:
                downloadDataProgressDialog.dismiss();
                Intent startInformationActivityIntent = new Intent(getActivity(), InformationActivity.class);
                startActivity(startInformationActivityIntent);
                break;
            case Error:
                downloadDataProgressDialog.dismiss();
                Toast.makeText(getActivity(), R.string.download_error_toast, Toast.LENGTH_SHORT).show();
                break;
            case noInternetConnection:
                Toast.makeText(AppController.getInstance().getApplicationContext(), R.string.no_internet_connection_message, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void showDownloadDataProgressDialog() {
        downloadDataProgressDialog = new ProgressDialog(getActivity());
        downloadDataProgressDialog.setTitle(getString(R.string.download_data_progress_dialog_title));
        downloadDataProgressDialog.setMessage(getString(R.string.download_data_progress_dialog_message));
        downloadDataProgressDialog.show();
    }
}
