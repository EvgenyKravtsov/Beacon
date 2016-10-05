package kgk.beacon.monitoring.presentation.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import biz.kasual.materialnumberpicker.MaterialNumberPicker;
import kgk.beacon.R;
import kgk.beacon.monitoring.DependencyInjection;
import kgk.beacon.monitoring.domain.model.MonitoringManager;
import kgk.beacon.monitoring.domain.model.routereport.RouteReportParametersPeriodSeparated;
import kgk.beacon.monitoring.presentation.presenter.RouteReportSettingsViewPresenter;
import kgk.beacon.monitoring.presentation.routereport.RouteReportActivity;
import kgk.beacon.monitoring.presentation.view.RouteReportSettingsView;

public class RouteReportSettingsActivity extends AppCompatActivity
        implements RouteReportSettingsView {

    private static final int MINIMAL_STOP_TIME = 30; // seconds

    // Views
    private LinearLayout mainLayout;
    private FrameLayout backButton;
    private TextView actionBarTitleTextView;
    private CheckBox noDataCheckBox;
    private Button makeReportButton;

    private MaterialNumberPicker fromDatePicker;
    private MaterialNumberPicker fromHourPicker;
    private MaterialNumberPicker fromMinutePicker;
    private MaterialNumberPicker toDatePicker;
    private MaterialNumberPicker toHourPicker;
    private MaterialNumberPicker toMinutePicker;

    private ImageView parkingHoursAddImageView;
    private TextView parkingHoursTextView;
    private ImageView parkingHoursRemoveImageView;
    private ImageView parkingMinutesAddImageView;
    private TextView parkingMinutesTextView;
    private ImageView parkingMinutesRemoveImageView;
    private ImageView parkingSecondsAddImageView;
    private TextView parkingSecondsTextView;
    private ImageView parkingSecondsRemoveImageView;

    private int parkingHours;
    private int parkingMinutes;
    private int parkingSeconds;


    // Dialogs
    private ProgressDialog progressDialog;

    private RouteReportSettingsViewPresenter presenter;

    private Long[] dates;

    ////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monitoring_activity_route_report_settings);
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

    @Override
    public void navigateToRouteReportView() {
        toggleProgressDialog(false);
        Intent intent = new Intent(this, RouteReportActivity.class);
        startActivity(intent);
    }

    @Override
    public void notifyNoDataForRouteReport() {
        if (progressDialog != null) progressDialog.dismiss();

        android.support.v7.app.AlertDialog.Builder builder =
                new android.support.v7.app.AlertDialog.Builder(this)
                .setMessage(getString(R.string.monitoring_no_data_for_device))
                .setPositiveButton(android.R.string.ok, null);

        android.support.v7.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    ////

    private void initViews() {
        mainLayout = (LinearLayout)
                findViewById(R.id.monitoring_activity_route_report_settings_main_layout);

        backButton = (FrameLayout) findViewById(R.id.monitoring_action_bar_back_button);
        actionBarTitleTextView = (TextView) findViewById(R.id.monitoring_action_bar_title_text_view);
        actionBarTitleTextView.setText(R.string.monitoring_route_report_settings_screen_title);

        noDataCheckBox = (CheckBox)
                findViewById(R.id.monitoring_activity_route_report_no_data_check_box);

        assert noDataCheckBox != null;
        noDataCheckBox.setChecked(true);

        makeReportButton = (Button)
                findViewById(R.id.monitoring_activity_route_report_make_report_button);

        initPickers();
        initMaxParkingTimeControl();
    }

    private void initPickers() {
        dates = new Long[730];

        Calendar date = Calendar.getInstance();
        dates[729] = date.getTimeInMillis();

        for (int i = 728; i >= 0; i--) {
            date.add(Calendar.DAY_OF_MONTH, -1);
            dates[i] = date.getTimeInMillis();
        }

        initFromDatePicker();
        initFromHourPicker();
        initFromMinutePicker();

        initToDatePicker();
        initToHourPicker();
        initToMinutePicker();
    }

    private void initFromDatePicker() {
        fromDatePicker = (MaterialNumberPicker)
                findViewById(R.id.monitoring_route_report_settings_from_date_picker);

        fromDatePicker.setWrapSelectorWheel(false);
        fromDatePicker.setFormatter(new NumberPicker.Formatter() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public String format(int i) {
                return new SimpleDateFormat("dd.MM.yyyy").format(new Date(dates[i]));
            }
        });

        fromDatePicker.setValue(729);
    }

    private void initFromHourPicker() {
        fromHourPicker = (MaterialNumberPicker)
                findViewById(R.id.monitoring_route_report_settings_from_hour_picker);

        fromHourPicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                if (i < 10) return "0" + i;
                else return Integer.toString(i);
            }
        });

        fromHourPicker.setValue(0);
    }

    private void initFromMinutePicker() {
        fromMinutePicker = (MaterialNumberPicker)
                findViewById(R.id.monitoring_route_report_settings_from_minute_picker);

        fromMinutePicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                if (i < 2) return "0" + (i * 5);
                else return Integer.toString(i * 5);
            }
        });

        fromMinutePicker.setValue(0);
    }

    private void initToDatePicker() {
        toDatePicker = (MaterialNumberPicker)
                findViewById(R.id.monitoring_route_report_settings_to_date_picker);

        toDatePicker.setWrapSelectorWheel(false);
        toDatePicker.setFormatter(new NumberPicker.Formatter() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public String format(int i) {
                return new SimpleDateFormat("dd.MM.yyyy").format(new Date(dates[i]));
            }
        });

        toDatePicker.setValue(729);
    }

    private void initToHourPicker() {
        toHourPicker = (MaterialNumberPicker)
                findViewById(R.id.monitoring_route_report_settings_to_hour_picker);

        toHourPicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                if (i < 10) return "0" + i;
                else return Integer.toString(i);
            }
        });

        toHourPicker.setValue(23);
    }

    private void initToMinutePicker() {
        toMinutePicker = (MaterialNumberPicker)
                findViewById(R.id.monitoring_route_report_settings_to_minute_picker);

        toMinutePicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                if (i < 2) return "0" + (i * 5);
                else if (i == 11) return Integer.toString(59);
                else return Integer.toString(i * 5);
            }
        });

        toMinutePicker.setValue(11);
    }

    private void initMaxParkingTimeControl() {
        parkingHours = 0;
        parkingMinutes = 0;
        parkingSeconds = 30;

        parkingHoursAddImageView = (ImageView)
                findViewById(R.id.monitoring_activity_route_report_settings_parking_hours_add);
        parkingHoursAddImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parkingHours++;
                if (parkingHours == 99) parkingHours = 0;
                refreshParkingTime();
            }
        });

        parkingHoursTextView = (TextView)
                findViewById(R.id.monitoring_activity_route_report_settings_parking_hours);

        parkingHoursRemoveImageView = (ImageView)
                findViewById(R.id.monitoring_activity_route_report_settings_parking_hours_remove);
        parkingHoursRemoveImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parkingHours--;
                if (parkingHours < 0) parkingHours = 99;
                refreshParkingTime();
            }
        });

        parkingMinutesAddImageView = (ImageView)
                findViewById(R.id.monitoring_activity_route_report_settings_parking_minutes_add);
        parkingMinutesAddImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parkingMinutes++;
                if (parkingMinutes == 60) parkingMinutes = 0;
                refreshParkingTime();
            }
        });

        parkingMinutesTextView = (TextView)
                findViewById(R.id.monitoring_activity_route_report_settings_parking_minutes);

        parkingMinutesRemoveImageView = (ImageView)
                findViewById(R.id.monitoring_activity_route_report_settings_parking_minutes_remove);
        parkingMinutesRemoveImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parkingMinutes--;
                if (parkingMinutes < 0) parkingMinutes = 59;
                refreshParkingTime();
            }
        });

        parkingSecondsAddImageView = (ImageView)
                findViewById(R.id.monitoring_activity_route_report_settings_parking_seconds_add);
        parkingSecondsAddImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parkingSeconds++;
                if (parkingSeconds == 60) parkingSeconds = 0;
                refreshParkingTime();
            }
        });

        parkingSecondsTextView = (TextView)
                findViewById(R.id.monitoring_activity_route_report_settings_parking_seconds);

        parkingSecondsRemoveImageView = (ImageView)
                findViewById(R.id.monitoring_activity_route_report_settings_parking_seconds_remove);
        parkingSecondsRemoveImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parkingSeconds--;
                if (parkingSeconds < 0) parkingSeconds = 59;
                refreshParkingTime();
            }
        });

        refreshParkingTime();
    }

    private void initListeners() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackButtonClick();
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

    private boolean isPeriodValid(long fromDate, long toDate) {
        return fromDate < toDate;
    }

    private boolean isPeriodLessThen30Days(long fromDate, long toDate) {
        return toDate - fromDate <= (long) 30 * 24 * 60 * 60 * 1000;
    }

    private void toggleProgressDialog(boolean status) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.monitoring_downloading_data));
            progressDialog.setIndeterminate(true);
            progressDialog.setCanceledOnTouchOutside(false);
        }

        if (status) progressDialog.show();
        else progressDialog.dismiss();
    }

    private void showSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(mainLayout, message, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    private void notifyNoInternetAvailable() {
        showSnackbar(getString(R.string.no_internet_connection_message));
    }

    private void refreshParkingTime() {
        String hours = timeToString(parkingHours);
        String minutes = timeToString(parkingMinutes);
        String seconds = timeToString(parkingSeconds);

        parkingHoursTextView.setText(hours);
        parkingMinutesTextView.setText(minutes);
        parkingSecondsTextView.setText(seconds);
    }

    private String timeToString(int time) {
        return time < 10 ? "0" + time : Integer.toString(time);
    }

    //// Control callbacks

    private void onBackButtonClick() {
        NavUtils.navigateUpFromSameTask(this);
    }

    @SuppressLint("SimpleDateFormat")
    private void onMakeReportButtonClick() {
        DateFormat format = new SimpleDateFormat("dd.MM.yyyy.HH.mm");

        long fromDateLong = dates[fromDatePicker.getValue()];
        String fromDateString = new SimpleDateFormat("dd.MM.yyyy").format(new Date(fromDateLong))
                + "." + fromHourPicker.getValue()
                + "." + (fromMinutePicker.getValue() * 5);

        Date fromDate = null;
        try { fromDate = format.parse(fromDateString); }
        catch (ParseException e) { e.printStackTrace(); }

        long toDateLong = dates[toDatePicker.getValue()];
        String toDateString = new SimpleDateFormat("dd.MM.yyyy").format(new Date(toDateLong))
                + "." + toHourPicker.getValue()
                + "." + (toMinutePicker.getValue() * 5);

        Date toDate = null;
        try { toDate = format.parse(toDateString); }
        catch (ParseException e) { e.printStackTrace(); }

        if (fromDate == null || toDate == null) {
            showSnackbar(getString(R.string.monitoring_route_report_settings_screen_dates_not_valid));
            return;
        }

        if (!isPeriodValid(fromDate.getTime(), toDate.getTime())) {
            showSnackbar(getString(R.string.monitoring_route_report_settings_screen_dates_not_valid));
            return;
        }

        if (!isPeriodLessThen30Days(fromDate.getTime(), toDate.getTime())) {
            showSnackbar(getString(R.string.monitoring_route_report_settings_screen_dates_limit));
            return;
        }

        int parkingTime = parkingHours * 3600 + parkingMinutes * 60 + parkingSeconds;
        RouteReportParametersPeriodSeparated parameters = new RouteReportParametersPeriodSeparated(
                fromDate.getTime() / 1000,
                toDate.getTime() / 1000,
                parkingTime,
                DependencyInjection.provideConfiguration().calculateOffsetUtc(),
                MonitoringManager.getInstance().getActiveMonitoringEntity().getId());

        toggleProgressDialog(true);
        presenter.sendRouteReportRequest(parameters);
    }
}





























