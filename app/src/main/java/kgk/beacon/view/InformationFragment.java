package kgk.beacon.view;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import kgk.beacon.R;
import kgk.beacon.actions.ActionCreator;
import kgk.beacon.dispatcher.Dispatcher;
import kgk.beacon.model.Signal;
import kgk.beacon.stores.SignalStore;
import kgk.beacon.util.AppController;
import kgk.beacon.util.DateFormatter;
import kgk.beacon.util.DownloadDataInProgressEvent;
import kgk.beacon.util.ToggleSearchModeEvent;

public class InformationFragment extends Fragment implements DialogInterface.OnClickListener {

    public static final String TAG = InformationFragment.class.getSimpleName();

    private static final String KEY_SEARCH_SWITCH = "key_search_switch";

    @Bind(R.id.fragmentInformation_brandTextView) TextView brandTextView;
    @Bind(R.id.fragmentInformation_modelTextView) TextView modelTextView;
    @Bind(R.id.fragmentInformation_numberTextView) TextView numberTextView;
    @Bind(R.id.fragmentInformation_lastActionTimeStamp) TextView lastActionTimeStamp;
    @Bind(R.id.fragmentInformation_lastPositioningTimeStamp) TextView lastPositioningTimeStamp;
    @Bind(R.id.fragmentInformation_satellitesCountTextView) TextView satellitesCountTextView;
    @Bind(R.id.fragmentInformation_voltageCountTextView) TextView voltageCountTextView;
    @Bind(R.id.fragmentInformation_speedCountTextView) TextView speedCountTextView;
    @Bind(R.id.fragmentInformation_chargeCountTextView) TextView chargeCountTextView;
    @Bind(R.id.fragmentInformation_directionCountTextView) TextView directionCountTextView;
    @Bind(R.id.fragmentInformation_balanceCountTextView) TextView balanceCountTextView;
    @Bind(R.id.fragmentInformation_temperatureCountTextView) TextView temperatureCountTextView;
    @Bind(R.id.fragmentInformation_searchButton) Button searchButton;

    private ProgressDialog downloadDataProgressDialog;

    private Dispatcher dispatcher;
    private ActionCreator actionCreator;
    private SignalStore signalStore;

    private boolean searchSwitch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_information, container, false);
        ButterKnife.bind(this, view);
        initFluxDependencies();
        // actionCreator.getLastSignalDateFromDatabase();
        displayGeneralInformation("KGK", "Actis", Long.toString(AppController.getInstance().getActiveDeviceId()));

        String deviceId = Long.toString(AppController.getInstance().getActiveDeviceId());
        searchSwitch = AppController.loadBooleanValueFromSharedPreferences(KEY_SEARCH_SWITCH + deviceId);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        dispatcher.register(this);
        updateUI();
    }

    @Override
    public void onPause() {
        super.onPause();
        dispatcher.unregister(this);
    }

    public void onEventMainThread(SignalStore.SignalStoreChangeEvent event) {
        updateUI();
    }

    public void onEventMainThread(ToggleSearchModeEvent event) {
        String deviceId = Long.toString(AppController.getInstance().getActiveDeviceId());

        if (searchSwitch) {
            searchSwitch = false;
            AppController.saveBooleanValueToSharedPreferences(KEY_SEARCH_SWITCH + deviceId, false);
            searchButton.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.search_off_button));
            searchButton.setText(getString(R.string.search_off_button_label));
        } else {
            searchSwitch = true;
            AppController.saveBooleanValueToSharedPreferences(KEY_SEARCH_SWITCH + deviceId, true);
            searchButton.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.search_on_button));
            searchButton.setText(getString(R.string.search_on_button_label));
        }
    }

    public void onEventMainThread(DownloadDataInProgressEvent event) {
        switch (event.getStatus()) {
            case Started:
                showDownloadDataProgressDialog();
                break;
            case Success:
                downloadDataProgressDialog.dismiss();
                break;
            case Error:
                downloadDataProgressDialog.dismiss();
                Toast.makeText(getActivity(), R.string.download_error_toast, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void displayGeneralInformation(String brand, String model, String number) {
        brandTextView.setText(brand);
        modelTextView.setText(model);
        numberTextView.setText(number);
    }

    private void displayInfoFieldsParameters() {
        lastActionTimeStamp.setText("DD.MM.YY 00:00");
        lastPositioningTimeStamp.setText("DD.MM.YY 00:00");
        satellitesCountTextView.setText("0" + getString(R.string.list_item_satellites_sign));
        voltageCountTextView.setText("0 " + getString(R.string.list_item_voltage_sign));
        speedCountTextView.setText("0 " + getString(R.string.list_item_speed_sign));
        chargeCountTextView.setText("0%");
        directionCountTextView.setText("0");
        balanceCountTextView.setText("0 " + getString(R.string.list_item_balance_sign));
        temperatureCountTextView.setText("0 " + getString(R.string.list_item_temperature_sign));
    }

    private void initFluxDependencies() {
        dispatcher = Dispatcher.getInstance(EventBus.getDefault());
        actionCreator = ActionCreator.getInstance(dispatcher);
        signalStore = SignalStore.getInstance(dispatcher);
    }

    private void updateUI() {
        Signal signal = signalStore.getSignal();

        if (signal == null) {
            displayInfoFieldsParameters();
        } else {
            lastPositioningTimeStamp.setText(DateFormatter.formatDateAndTime(new Date(signal.getDate() * 1000)));
            satellitesCountTextView.setText(String.valueOf(signal.getSatellites())
                + getString(R.string.list_item_satellites_sign));
            voltageCountTextView.setText(String.valueOf(signal.getVoltage())
                    + getString(R.string.list_item_voltage_sign));
            speedCountTextView.setText(String.valueOf(signal.getSpeed())
                    + getString(R.string.list_item_speed_sign));
            chargeCountTextView.setText(String.valueOf(signal.getCharge()) + "%");
            directionCountTextView.setText(AppController.getDirectionLetterFromDegrees(signal.getDirection()));
            balanceCountTextView.setText(String.valueOf(signal.getBalance())
                    + getString(R.string.list_item_balance_sign));
            temperatureCountTextView.setText(String.valueOf(signal.getTemperature())
                    + getString(R.string.list_item_temperature_sign));
        }

        lastActionTimeStamp.setText(DateFormatter.loadLastActionDateString());

        if (searchSwitch) {
            searchButton.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.search_on_button));
            searchButton.setText(getString(R.string.search_on_button_label));
        } else {
            searchButton.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.search_off_button));
            searchButton.setText(getString(R.string.search_off_button_label));
        }
    }

    private void showDownloadDataProgressDialog() {
        downloadDataProgressDialog = new ProgressDialog(getActivity());
        downloadDataProgressDialog.setTitle(getString(R.string.download_data_progress_dialog_title));
        downloadDataProgressDialog.setMessage(getString(R.string.download_data_progress_dialog_message));
        downloadDataProgressDialog.show();
    }

    @OnClick(R.id.fragmentInformation_searchButton)
    public void onPressSearchButton(View view) {

        Button searchButton = (Button) view;

        if (!searchSwitch) {
            if (AppController.getInstance().isNetworkAvailable()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getString(R.string.search_mode_dialog_title))
                        .setMessage(getString(R.string.search_mode_dialog_on))
                        .setPositiveButton(android.R.string.ok, this)
                        .setNegativeButton(android.R.string.cancel, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                Toast.makeText(getActivity(), getString(R.string.no_internet_connection_message), Toast.LENGTH_SHORT).show();
            }
        } else {
            if (AppController.getInstance().isNetworkAvailable()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getString(R.string.search_mode_dialog_title))
                        .setMessage(getString(R.string.search_mode_dialog_off))
                        .setPositiveButton(android.R.string.ok, this)
                        .setNegativeButton(android.R.string.cancel, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                Toast.makeText(getActivity(), getString(R.string.no_internet_connection_message), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @OnClick(R.id.fragmentInformation_historyButton)
    public void onPressHistoryButton(View view) {
        actionCreator.getLastSignalsByDeviceIdFromDatabase(HistoryFragment.DEFAULT_NUMBER_OF_SIGNALS);

        Intent intent = new Intent(getActivity(), HistoryActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.fragmentInformation_mapButton)
    public void onPressMapButton(View view) {
        if (signalStore.getSignal() != null) {
            Intent mapIntent = new Intent(getActivity(), MapCustomActivity.class);
            startActivity(mapIntent);
        } else {
            Toast.makeText(AppController.getInstance(), R.string.no_signals_toast, Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.fragmentInformation_settingsButton)
    public void onPressSettingsButton(View view) {
        Intent settingsIntent = new Intent(getActivity(), SettingsActivity.class);
        startActivity(settingsIntent);
    }

    @OnClick(R.id.fragmentInformation_queryButton)
    public void onPressQueryButton(View view) {
        if (AppController.getInstance().isNetworkAvailable()) {
            actionCreator.sendQueryBeaconRequest();
            actionCreator.getLastSignalDateFromDatabase();
            // actionCreator.sendGetLastStateRequest();

            // TODO Delete test code
            sendSms();
        } else {
            Toast.makeText(getActivity(), getString(R.string.no_internet_connection_message), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (searchSwitch) {
            actionCreator.sendToggleSearchModeRequest(false);
        } else {
            actionCreator.sendToggleSearchModeRequest(true);
        }
    }

    private void sendSms() {
        String phoneNo = "89680240490";
        String message = "Hello, World";
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, message, null, null);

            Toast.makeText(getActivity(), "Message send", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}