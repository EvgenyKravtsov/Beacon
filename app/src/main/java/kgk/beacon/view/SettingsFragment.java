package kgk.beacon.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import kgk.beacon.R;
import kgk.beacon.actions.ActionCreator;
import kgk.beacon.dispatcher.Dispatcher;
import kgk.beacon.util.AppController;

public class SettingsFragment extends Fragment {

    private static final String TAG = SettingsFragment.class.getSimpleName();

    private static final String APPLICATION_PREFERENCES = "application-preferences";
    private static final String KEY_CONNECTION_PERIOD = "key-connection-period";
    private static final String KEY_NUMBER_SMS_OMISSIONS = "key-number-sms-omissions";
    private static final String KEY_GPS_POSITIONING_PERIOD = "key-gps-positioning-period";
    private static final String KEY_BALANCE_CONTROL_LIMIT = "key-balance-control-limit";
    private static final String KEY_BALANCE_REQUEST_COMMAND = "key-balance-request-command";
    private static final String KEY_GPRS_STATUS = "key-gprs-status";

    @Bind(R.id.fragmentSettings_connectionPeriodEditText) EditText connectionPeriodEditText;
    @Bind(R.id.fragmentSettings_numberSmsOmissionsEditText) EditText numberSmsOmissionsEditText;
    @Bind(R.id.fragmentSettings_gpsPositioningPeriodEditText) EditText gpsPositioningPeriodEditText;
    @Bind(R.id.fragmentSettings_balanceControlLimitEditText) EditText balanceControlLimitEditText;
    @Bind(R.id.fragmentSettings_balanceRequestCommandEditText) EditText balanceRequestCommandEditText;
    @Bind(R.id.fragmentSettings_gprsStatusSpinner) Spinner gprsStatusSpinner;

    Dispatcher dispatcher;
    ActionCreator actionCreator;

    private List<String> gprsSpinnerOptions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, view);
        initFluxDependencies();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item,
                generateGprsSpinnerOptions());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gprsStatusSpinner.setAdapter(adapter);

        loadSettingsFromSharedPreferences();

        return view;
    }

    private void initFluxDependencies() {
        dispatcher = Dispatcher.getInstance(EventBus.getDefault());
        actionCreator = ActionCreator.getInstance(dispatcher);
    }

    private List<String> generateGprsSpinnerOptions() {
        if (gprsSpinnerOptions == null) {
            gprsSpinnerOptions = new ArrayList<>();
            gprsSpinnerOptions.add(getString(R.string.settings_gprs_spinner_on));
            gprsSpinnerOptions.add(getString(R.string.settings_gprs_spinner_off));
        }

        return gprsSpinnerOptions;
    }

    private void saveSettingsToSharedPreferences() {
        SharedPreferences settings = getActivity().getSharedPreferences(APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        String deviceId = String.valueOf(AppController.getInstance().getActiveDeviceId());

        editor.putInt(deviceId + KEY_CONNECTION_PERIOD, Integer.parseInt(connectionPeriodEditText.getText().toString()));
        editor.putInt(deviceId + KEY_NUMBER_SMS_OMISSIONS, Integer.parseInt(numberSmsOmissionsEditText.getText().toString()));
        editor.putInt(deviceId + KEY_GPS_POSITIONING_PERIOD, Integer.parseInt(gpsPositioningPeriodEditText.getText().toString()));
        editor.putInt(deviceId + KEY_BALANCE_CONTROL_LIMIT, Integer.parseInt(balanceControlLimitEditText.getText().toString()));
        editor.putString(deviceId + KEY_BALANCE_REQUEST_COMMAND, balanceRequestCommandEditText.getText().toString());
        editor.putBoolean(deviceId + KEY_GPRS_STATUS, gprsStatusSpinner.getSelectedItem().toString().equals("On"));
        editor.apply();
    }

    private void loadSettingsFromSharedPreferences() {
        SharedPreferences settings = getActivity().getSharedPreferences(APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        String deviceId = String.valueOf(AppController.getInstance().getActiveDeviceId());

        connectionPeriodEditText.setText(String.valueOf(settings.getInt(deviceId + KEY_CONNECTION_PERIOD, 1440)));
        numberSmsOmissionsEditText.setText(String.valueOf(settings.getInt(deviceId + KEY_NUMBER_SMS_OMISSIONS, 0)));
        gpsPositioningPeriodEditText.setText(String.valueOf(settings.getInt(deviceId + KEY_GPS_POSITIONING_PERIOD, 5)));
        balanceControlLimitEditText.setText(String.valueOf(settings.getInt(deviceId + KEY_BALANCE_CONTROL_LIMIT, 100)));
        balanceRequestCommandEditText.setText(settings.getString(deviceId + KEY_BALANCE_REQUEST_COMMAND, "*100#"));

        boolean gprsStatus = settings.getBoolean(deviceId + KEY_GPRS_STATUS, false);
        gprsStatusSpinner.setSelection(gprsStatus ? 0 : 1);
    }

    @OnClick(R.id.fragmentSettings_sendSettingsButton)
    public void onPressSendSettingsButton(View view) {
        if (!validateFields()) {
            return;
        }

        saveSettingsToSharedPreferences();

        if (AppController.getInstance().isNetworkAvailable()) {
            JSONObject settings = settingsToJson();
            actionCreator.sendSettingsRequest(settings);
        } else {
            Toast.makeText(getActivity(), getString(R.string.no_internet_connection_message), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateFields() {
        if (balanceRequestCommandEditText.getText().toString().length() > 10) {
            Toast.makeText(getActivity(), getString(R.string.balance_request_command_wrong_field_message), Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private JSONObject settingsToJson() {
        JSONObject settingsJson = new JSONObject();

        try {
            settingsJson.put("connection_period",
                    Integer.parseInt(connectionPeriodEditText.getText().toString()));
            settingsJson.put("sms_omissions",
                    Integer.parseInt(numberSmsOmissionsEditText.getText().toString()));
            settingsJson.put("gps_period",
                    Integer.parseInt(gpsPositioningPeriodEditText.getText().toString()));
            settingsJson.put("balance_limit",
                    Integer.parseInt(balanceControlLimitEditText.getText().toString()));
            settingsJson.put("balance_command",
                    balanceRequestCommandEditText.getText().toString());
            settingsJson.put("gprs_status",
                    gprsStatusSpinner.getSelectedItem().toString().equals("On"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return settingsJson;
    }
}
