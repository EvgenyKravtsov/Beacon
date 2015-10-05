package kgk.beacon.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import kgk.beacon.R;
import kgk.beacon.actions.ActionCreator;
import kgk.beacon.database.SignalDatabaseDao;
import kgk.beacon.dispatcher.Dispatcher;
import kgk.beacon.model.Signal;
import kgk.beacon.stores.SignalStore;
import kgk.beacon.test.SignalInformationTester;
import kgk.beacon.util.AppController;
import kgk.beacon.util.DateFormatter;

public class InformationFragment extends Fragment {

    // TODO Upgrade search button

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

    private Dispatcher dispatcher;
    private ActionCreator actionCreator;
    private SignalStore signalStore;

    private boolean searchSwitch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_information, container, false);
        ButterKnife.bind(this, view);
        initFluxDependencies();

        displayGeneralInformation("Brand", "Model", "Number");
        searchSwitch = AppController.loadBooleanValueFromSharedPreferences(KEY_SEARCH_SWITCH);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        dispatcher.register(this);
        updateUI();

        // TODO Delete test code
        new SignalInformationTester(dispatcher, actionCreator).executeTestingRoutine();
    }

    @Override
    public void onPause() {
        super.onPause();
        dispatcher.unregister(this);
    }

    public void onEventMainThread(SignalStore.SignalStoreChangeEvent event) {
        updateUI();
    }

    private void displayGeneralInformation(String brand, String model, String number) {
        brandTextView.setText(brand);
        modelTextView.setText(model);
        numberTextView.setText(number);
    }

    private void displayInfoFieldsParameters() {
        lastActionTimeStamp.setText("DD.MM.YY 00:00");
        lastPositioningTimeStamp.setText("DD.MM.YY 00:00");
        satellitesCountTextView.setText("0");
        voltageCountTextView.setText("0 V");
        speedCountTextView.setText("0 km/h");
        chargeCountTextView.setText("0");
        directionCountTextView.setText("0");
        balanceCountTextView.setText("0 rub.");
        temperatureCountTextView.setText("0 C");
    }

    private void initFluxDependencies() {
        dispatcher = Dispatcher.getInstance(EventBus.getDefault());
        actionCreator = ActionCreator.getInstance(dispatcher);
        signalStore = SignalStore.getInstance(dispatcher);
    }

    private void updateUI() {
        Signal signal = signalStore.getSignal();

        if (signal == null) {
            Log.d(TAG, "Displaying default values");
            displayInfoFieldsParameters();
        } else {
            lastPositioningTimeStamp.setText(DateFormatter.formatDateAndTime(new Date(signal.getDate() * 1000)));
            satellitesCountTextView.setText(String.valueOf(signal.getSatellites()));
            voltageCountTextView.setText(String.valueOf(signal.getVoltage()));
            speedCountTextView.setText(String.valueOf(signal.getSpeed()));
            chargeCountTextView.setText(String.valueOf(signal.getCharge()) + "%");
            directionCountTextView.setText(String.valueOf(signal.getDirection()));
            balanceCountTextView.setText(String.valueOf(signal.getBalance()));
            temperatureCountTextView.setText(String.valueOf(signal.getTemperature()));
        }

        lastActionTimeStamp.setText(DateFormatter.loadLastActionDateString());

        if (searchSwitch) {
            searchButton.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.search_on_button));
        } else {
            searchButton.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.search_off_button));
        }
    }

    @OnClick(R.id.fragmentInformation_searchButton)
    public void onPressSearchButton(View view) {
        Button searchButton = (Button) view;

        if (searchSwitch) {
            if (AppController.getInstance().isNetworkAvailable()) {
                searchSwitch = false;
                AppController.saveBooleanValueToSharedPreferences(KEY_SEARCH_SWITCH, false);
                searchButton.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.search_off_button));
                actionCreator.sendToggleSearchModeRequest(false);
            } else {
                Toast.makeText(getActivity(), getString(R.string.no_internet_connection_message), Toast.LENGTH_SHORT).show();
            }
        } else {
            if (AppController.getInstance().isNetworkAvailable()) {
                searchSwitch = true;
                AppController.saveBooleanValueToSharedPreferences(KEY_SEARCH_SWITCH, true);
                searchButton.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.search_on_button));
                actionCreator.sendToggleSearchModeRequest(true);
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
            actionCreator.sendGetLastStateRequest();

            // TODO Delete test code
            List<Signal> signals = SignalDatabaseDao.getInstance(AppController.getInstance()).getAllSignals();
            for (Signal signal : signals) {
                Log.d(TAG, signal.toString());
            }
            SignalDatabaseDao.getInstance(AppController.getInstance()).deleteAllSignalsFromDatabase();
        } else {
            Toast.makeText(getActivity(), getString(R.string.no_internet_connection_message), Toast.LENGTH_SHORT).show();
        }
    }
}