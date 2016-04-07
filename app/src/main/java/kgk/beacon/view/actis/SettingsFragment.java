package kgk.beacon.view.actis;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
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

    public static final String APPLICATION_PREFERENCES = "application-preferences";
    public static final String KEY_CONNECTION_PERIOD = "key-connection-period";
    public static final String KEY_IS_AWAITING_SETTINGS_CONFIRMATION = "key-is-awaiting-settings-confirmation";
    public static final String KEY_SETTINGS_CONFIRMATION_CONTROL_DATE = "key-settings_confirmation_control_date";

    private static final String KEY_NUMBER_SMS_OMISSIONS = "key-number-sms-omissions";
    private static final String KEY_GPS_POSITIONING_PERIOD = "key-gps-positioning-period";
    private static final String KEY_BALANCE_CONTROL_LIMIT = "key-balance-control-limit";
    private static final String KEY_BALANCE_REQUEST_COMMAND = "key-balance-request-command";
    private static final String KEY_GPRS_STATUS = "key-gprs-status";
    private static final String KEY_PERIODIC_RANGE_SPINNER_CHOICE = "key_periodic_range_spinner_choice";

    @Bind(R.id.fragmentSettings_connectionPeriodEditText) TextView connectionPeriodEditText;
    @Bind(R.id.fragmentSettings_numberSmsOmissionsEditText) EditText numberSmsOmissionsEditText;
    @Bind(R.id.fragmentSettings_gpsPositioningPeriodEditText) EditText gpsPositioningPeriodEditText;
    @Bind(R.id.fragmentSettings_balanceControlLimitEditText) EditText balanceControlLimitEditText;
    @Bind(R.id.fragmentSettings_balanceRequestCommandEditText) EditText balanceRequestCommandEditText;
    @Bind(R.id.fragmentSettings_gprsStatusSpinner) Spinner gprsStatusSpinner;
    @Bind(R.id.settingsFragment_periodicRangeSpinner) Spinner periodicRangeSpinner;
    @Bind(R.id.settingsFragment_seekBar) SeekBar periodSeekBar;

    Dispatcher dispatcher;
    ActionCreator actionCreator;

    private List<String> gprsSpinnerOptions;
    private List<String> periodicRangeSpinnerOptions;

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

        preparePeriodicRangeSpinner();

        loadSettingsFromSharedPreferences();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void initFluxDependencies() {
        dispatcher = Dispatcher.getInstance(EventBus.getDefault());
        actionCreator = ActionCreator.getInstance(dispatcher);
    }

    private void preparePeriodicRangeSpinner() {
        ArrayAdapter<String> periodicRangeAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.actis_settings_spinner_item,
                generatePeriodicRangeSpinnerOptions());
        periodicRangeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        periodicRangeSpinner.setAdapter(periodicRangeAdapter);
        periodicRangeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        periodSeekBar.setProgress(0);
                        preparePeriodicSeekBarForDay();
                        break;
                    case 1:
                        periodSeekBar.setProgress(0);
                        preparePeriodicSeekBarForWeek();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void preparePeriodicSeekBarForDay() {
        periodSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int value;

                try {
                    value = progress / (100 / 96);
                } catch (Exception e) {
                    value = 0;
                }

                if (value > 96) {
                    value = 96;
                }

                connectionPeriodEditText.setText(Integer.toString(value));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void preparePeriodicSeekBarForWeek() {
        periodSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double value;

                try {
                    value = progress / (100 / 672.0);
                } catch (Exception e) {
                    value = 0;
                }

                if (value > 672) {
                    value = 672;
                }

                connectionPeriodEditText.setText(Integer.toString((int) value));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private List<String> generateGprsSpinnerOptions() {
        if (gprsSpinnerOptions == null) {
            gprsSpinnerOptions = new ArrayList<>();
            gprsSpinnerOptions.add(getString(R.string.settings_gprs_spinner_on));
            gprsSpinnerOptions.add(getString(R.string.settings_gprs_spinner_off));
        }

        return gprsSpinnerOptions;
    }

    private List<String> generatePeriodicRangeSpinnerOptions() {
        if (periodicRangeSpinnerOptions == null) {
            periodicRangeSpinnerOptions = new ArrayList<>();
            periodicRangeSpinnerOptions.add(getString(R.string.connection_per_day_label));
            periodicRangeSpinnerOptions.add(getString(R.string.connection_per_week_label));
        }

        return periodicRangeSpinnerOptions;
    }

    private void saveSettingsToSharedPreferences() {
        SharedPreferences settings = getActivity().getSharedPreferences(APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        String deviceId = String.valueOf(AppController.getInstance().getActiveDeviceId());

        editor.putInt(deviceId + KEY_PERIODIC_RANGE_SPINNER_CHOICE,
                periodicRangeSpinner.getSelectedItemPosition());

        if (periodicRangeSpinner.getSelectedItemPosition() == 0) {
            editor.putInt(deviceId + KEY_CONNECTION_PERIOD, signalsPerDayToMinutes(
                    Integer.parseInt(connectionPeriodEditText.getText().toString())));
        } else if (periodicRangeSpinner.getSelectedItemPosition() == 1) {
            editor.putInt(deviceId + KEY_CONNECTION_PERIOD, signalsPerWeekToMinutes(
                    Integer.parseInt(connectionPeriodEditText.getText().toString())));
        }

        editor.putInt(deviceId + KEY_NUMBER_SMS_OMISSIONS,
                Integer.parseInt(numberSmsOmissionsEditText.getText().toString()));
        editor.putInt(deviceId + KEY_GPS_POSITIONING_PERIOD,
                Integer.parseInt(gpsPositioningPeriodEditText.getText().toString()));
        editor.putInt(deviceId + KEY_BALANCE_CONTROL_LIMIT,
                Integer.parseInt(balanceControlLimitEditText.getText().toString()));
        editor.putString(deviceId + KEY_BALANCE_REQUEST_COMMAND,
                balanceRequestCommandEditText.getText().toString());
        editor.putBoolean(deviceId + KEY_GPRS_STATUS,
                gprsStatusSpinner.getSelectedItem().toString().equals("On"));
        editor.apply();
    }

    private void loadSettingsFromSharedPreferences() {
        SharedPreferences settings = getActivity().getSharedPreferences(APPLICATION_PREFERENCES,
                Context.MODE_PRIVATE);
        String deviceId = String.valueOf(AppController.getInstance().getActiveDeviceId());

        periodicRangeSpinner.setSelection(settings
                .getInt(deviceId + KEY_PERIODIC_RANGE_SPINNER_CHOICE, 0));


        int signalsCount = 0;
        if (periodicRangeSpinner.getSelectedItemPosition() == 0) {
            signalsCount = minutesToSignalsPerDay(settings
                    .getInt(deviceId + KEY_CONNECTION_PERIOD, 1440));
            setPeriodSeekBarProgressForDay(signalsCount);
        } else if (periodicRangeSpinner.getSelectedItemPosition() == 1){
            signalsCount = minutesToSignalsPerWeek(settings
                    .getInt(deviceId + KEY_CONNECTION_PERIOD, 1440));
            setPeriodSeekBarProgressForWeek(signalsCount);
        }
        connectionPeriodEditText.setText(String.valueOf(signalsCount));


        numberSmsOmissionsEditText.setText(String.valueOf(settings
                .getInt(deviceId + KEY_NUMBER_SMS_OMISSIONS, 0)));
        gpsPositioningPeriodEditText.setText(String.valueOf(settings
                .getInt(deviceId + KEY_GPS_POSITIONING_PERIOD, 5)));
        balanceControlLimitEditText.setText(String.valueOf(settings
                .getInt(deviceId + KEY_BALANCE_CONTROL_LIMIT, 100)));
        balanceRequestCommandEditText.setText(settings
                .getString(deviceId + KEY_BALANCE_REQUEST_COMMAND, "*100#"));

        boolean gprsStatus = settings.getBoolean(deviceId + KEY_GPRS_STATUS, false);
        gprsStatusSpinner.setSelection(gprsStatus ? 0 : 1);
    }

    private int minutesToSignalsPerDay(int minutes) {
        return 1440 / minutes;
    }

    private int signalsPerDayToMinutes(int signalsPerDay) {
        return 1440 / signalsPerDay;
    }

    private int minutesToSignalsPerWeek(int minutes) {
        return 10080 / minutes;
    }

    private int signalsPerWeekToMinutes(int signalsPerWeek) {
        return 10080 / signalsPerWeek;
    }

    private void setPeriodSeekBarProgressForDay(int signalsPerDay) {
        int progress = (int) (signalsPerDay * (100 / 96.0));
        if (progress > 100) {
            progress = 100;
        }

        Log.d(TAG, "progress = " + progress);

        periodSeekBar.setProgress(progress);
    }

    private void setPeriodSeekBarProgressForWeek(int signalsPerWeek) {
        int progress = (int) (signalsPerWeek * (100 / 672.0));
        if (progress > 100) {
            progress = 100;
        }
        periodSeekBar.setProgress(progress);
    }

    @OnClick(R.id.fragmentSettings_sendSettingsButton)
    public void onPressSendSettingsButton(View view) {
        if ((Calendar.getInstance().getTimeInMillis() / 1000) - AppController.loadLongValueFromSharedPreferences(KEY_SETTINGS_CONFIRMATION_CONTROL_DATE)
                >= (24 * 60 * 60)) {
            AppController.saveBooleanValueToSharedPreferences(KEY_IS_AWAITING_SETTINGS_CONFIRMATION, false);
        }

        if (AppController.loadBooleanValueFromSharedPreferences(KEY_IS_AWAITING_SETTINGS_CONFIRMATION)) {
            showAwaitingConfirmationDialog();
            return;
        }

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

    private void showAwaitingConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.ActisAlertDialogStyle);
        builder.setTitle(getString(R.string.development_progress_dialog_title))
                .setMessage(getString(R.string.awaiting_confirmation_dialog_message))
                .setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private JSONObject settingsToJson() {
        JSONObject settingsJson = new JSONObject();

        try {
            if (periodicRangeSpinner.getSelectedItemPosition() == 0) {
                settingsJson.put("connection_period", signalsPerDayToMinutes(
                        Integer.parseInt(connectionPeriodEditText.getText().toString())));
            } else if (periodicRangeSpinner.getSelectedItemPosition() == 1) {
                settingsJson.put("connection_period", signalsPerWeekToMinutes(
                        Integer.parseInt(connectionPeriodEditText.getText().toString())));
            }

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

    public static JSONObject generateStandardSettingsJson() {
        JSONObject settingsJson = new JSONObject();

        try {
            settingsJson.put("connection_period", 60);
            settingsJson.put("sms_omissions", 23);
            settingsJson.put("gps_period", 5);
            settingsJson.put("balance_limit", 100);
            settingsJson.put("balance_command", "*100#");
            settingsJson.put("gprs_status", false);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return settingsJson;
    }
}
