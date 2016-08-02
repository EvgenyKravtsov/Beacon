package kgk.beacon.monitoring.presentation.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Locale;

import kgk.beacon.R;
import kgk.beacon.monitoring.DependencyInjection;
import kgk.beacon.monitoring.domain.model.MonitoringManager;
import kgk.beacon.monitoring.domain.model.RouteReportParameters;
import kgk.beacon.monitoring.presentation.presenter.RouteReportSettingsViewPresenter;
import kgk.beacon.monitoring.presentation.view.RouteReportSettingsView;

public class RouteReportSettingsActivity extends AppCompatActivity
        implements RouteReportSettingsView {

    private static final int MINIMAL_STOP_TIME = 30; // seconds

    // Views
    private Button fromDateButton;
    private Button fromTimeButton;
    private Button toDateButton;
    private Button toTimeButton;
    private Button extendedSettingsButton;
    private LinearLayout extendedSettingsLayout;
    private EditText stopTimeEditText;
    private CheckBox noDataCheckBox;
    private Button makeReportButton;

    private RouteReportSettingsViewPresenter presenter;
    private boolean extendedSettingsDisplayed;

    private Calendar fromDate;
    private Calendar toDate;

    ////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monitoring_activity_route_report_settings);

        fromDate = Calendar.getInstance();
        toDate = Calendar.getInstance();

        initViews();
        initListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindPresenter();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindPresenter();
    }

    ////

    private void initViews() {
        fromDateButton = (Button)
                findViewById(R.id.monitoring_activity_route_report_settings_from_date_button);
        fromTimeButton = (Button)
                findViewById(R.id.monitoring_activity_route_report_settings_from_time_button);

        setFromDate(0, 0);
        setFromDate(
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        toDateButton = (Button)
                findViewById(R.id.monitoring_activity_route_report_settings_to_date_button);
        toTimeButton = (Button)
                findViewById(R.id.monitoring_activity_route_report_settings_to_time_button);

        setToDate(23, 59);
        setToDate(
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        extendedSettingsButton = (Button)
                findViewById(R.id.monitoring_activity_route_report_extended_settings_button);

        extendedSettingsLayout = (LinearLayout)
                findViewById(R.id.monitoring_activity_route_report_extended_settings_layout);

        assert extendedSettingsLayout != null;
        extendedSettingsLayout.setVisibility(
                extendedSettingsDisplayed ? View.VISIBLE : View.GONE);

        stopTimeEditText = (EditText)
                findViewById(R.id.monitoring_activity_route_report_stop_time_edit_text);
        stopTimeEditText.setText(String.format(Locale.ROOT, "%d", MINIMAL_STOP_TIME));

        noDataCheckBox = (CheckBox)
                findViewById(R.id.monitoring_activity_route_report_no_data_check_box);

        assert noDataCheckBox != null;
        noDataCheckBox.setChecked(true);

        makeReportButton = (Button)
                findViewById(R.id.monitoring_activity_route_report_make_report_button);
    }

    private void initListeners() {
        fromDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFromDateButtonClick();
            }
        });

        fromTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFromTimeButtonClick();
            }
        });

        toDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onToDateButtonClick();
            }
        });

        toTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onToTimeButtonClick();
            }
        });

        extendedSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onExtendedSettingsButtonClick();
            }
        });

        makeReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMakeReportButtonClick();
            }
        });
    }

    private void bindPresenter() {
        presenter = new RouteReportSettingsViewPresenter(this);
    }

    private void unbindPresenter() {
        if (presenter != null) presenter.unbindView();
        presenter = null;
    }

    private void setFromDate(int year, int month, int day) {
        fromDateButton.setText(String.format(Locale.ROOT, "%s.%s.%d",
                (day >= 10 ? day : "0" + day),
                (month >= 10 ? (month + 1) : "0" + (month + 1)),
                year));
        fromDate.set(Calendar.YEAR, year);
        fromDate.set(Calendar.MONTH, month);
        fromDate.set(Calendar.DAY_OF_MONTH, day);
    }

    private void setFromDate(int hour, int minute) {
        fromTimeButton.setText(String.format(Locale.ROOT, "%s:%s",
                (hour >= 10 ? hour : "0" + hour),
                (minute >= 10 ? minute : "0" + minute)));
        fromDate.set(Calendar.HOUR_OF_DAY, hour);
        fromDate.set(Calendar.MINUTE, minute);
    }

    private void setToDate(int year, int month, int day) {
        toDateButton.setText(String.format(Locale.ROOT, "%s.%s.%d",
                (day >= 10 ? day : "0" + day),
                (month >= 10 ? (month + 1) : "0" + (month + 1)),
                year));
        toDate.set(Calendar.YEAR, year);
        toDate.set(Calendar.MONTH, month);
        toDate.set(Calendar.DAY_OF_MONTH, day);
    }

    private void setToDate(int hour, int minute) {
        toTimeButton.setText(String.format(Locale.ROOT, "%s:%s",
                (hour >= 10 ? hour : "0" + hour),
                (minute >= 10 ? minute : "0" + minute)));
        toDate.set(Calendar.HOUR_OF_DAY, hour);
        toDate.set(Calendar.MINUTE, minute);
    }

    private boolean isDatesValid() {
        return fromDate.getTimeInMillis() < toDate.getTimeInMillis();
    }

    //// Control callbacks

    private void onFromDateButtonClick() {
        @SuppressLint("InflateParams") View datePicketView = getLayoutInflater()
                .inflate(R.layout.monitoring_date_picker, null);
        final DatePicker datePicker = (DatePicker)
                datePicketView.findViewById(R.id.monitoring_date_picker);

        new AlertDialog.Builder(this)
                .setView(datePicketView)
                .setTitle("From Date")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int year = datePicker.getYear();
                        int month = datePicker.getMonth();
                        int day = datePicker.getDayOfMonth();
                        setFromDate(year, month, day);
                    }
                })
                .create()
                .show();
    }

    private void onFromTimeButtonClick() {
        @SuppressLint("InflateParams") View timePicketView = getLayoutInflater()
                .inflate(R.layout.monitoring_time_picker, null);
        final TimePicker timePicker = (TimePicker)
                timePicketView.findViewById(R.id.monitoring_time_picker);
        timePicker.setIs24HourView(true);
        timePicker.setCurrentHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));

        new AlertDialog.Builder(this)
                .setView(timePicketView)
                .setTitle(getString(R.string.time_picker_dialog_title))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int hour = timePicker.getCurrentHour();
                        int minute = timePicker.getCurrentMinute();
                        setFromDate(hour, minute);
                    }
                })
                .create()
                .show();
    }

    private void onToDateButtonClick() {
        @SuppressLint("InflateParams") View datePicketView = getLayoutInflater()
                .inflate(R.layout.monitoring_date_picker, null);
        final DatePicker datePicker = (DatePicker)
                datePicketView.findViewById(R.id.monitoring_date_picker);

        new AlertDialog.Builder(this)
                .setView(datePicketView)
                .setTitle("To Date")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int year = datePicker.getYear();
                        int month = datePicker.getMonth();
                        int day = datePicker.getDayOfMonth();
                        setToDate(year, month, day);
                    }
                })
                .create()
                .show();
    }

    private void onToTimeButtonClick() {
        @SuppressLint("InflateParams") View timePicketView = getLayoutInflater()
                .inflate(R.layout.monitoring_time_picker, null);
        final TimePicker timePicker = (TimePicker)
                timePicketView.findViewById(R.id.monitoring_time_picker);
        timePicker.setIs24HourView(true);
        timePicker.setCurrentHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));

        new AlertDialog.Builder(this)
                .setView(timePicketView)
                .setTitle(getString(R.string.time_picker_dialog_title))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int hour = timePicker.getCurrentHour();
                        int minute = timePicker.getCurrentMinute();
                        setToDate(hour, minute);
                    }
                })
                .create()
                .show();
    }

    private void onExtendedSettingsButtonClick() {
        extendedSettingsDisplayed = !extendedSettingsDisplayed;
        extendedSettingsLayout.setVisibility(
                extendedSettingsDisplayed ? View.VISIBLE : View.GONE);
    }

    private void onMakeReportButtonClick() {

        // TODO Notify user - dates not valid
        if (!isDatesValid()) return;

        RouteReportParameters parameters = new RouteReportParameters(
                fromDate.getTimeInMillis() / 1000,
                toDate.getTimeInMillis() / 1000,
                Integer.parseInt(stopTimeEditText.getText().toString()),
                DependencyInjection.provideConfiguration().calculateOffsetUtc(),
                MonitoringManager.getInstance().getActiveMonitoringEntity().getId());

        presenter.sendRouteReportRequest(parameters);
    }
}





























