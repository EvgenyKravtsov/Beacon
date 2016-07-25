package kgk.beacon.view.devices.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kgk.beacon.R;
import kgk.beacon.model.DataForDetailReportRequest;
import kgk.beacon.view.devices.DetailReportView;
import kgk.beacon.view.devices.presenter.DetailReportPresenter;

public class DetailReportActivity extends AppCompatActivity implements DetailReportView {

    private static final int MINIMAL_DELTA = 30;

    ////

    @Bind(R.id.actisApp_toolbar) Toolbar toolbar;
    @Bind(R.id.toolbar_title) TextView toolbarTitle;
    @Bind(R.id.detail_report_activity_from_date_button) Button fromDateButton;
    @Bind(R.id.detail_report_activity_from_time_button) Button fromTimeButton;
    @Bind(R.id.detail_report_activity_to_date_button) Button toDateButton;
    @Bind(R.id.detail_report_activity_to_time_button) Button toTimeButton;
    @Bind(R.id.detail_report_activity_delta_field) EditText deltaField;
    @Bind(R.id.detail_report_activity_offset_utc_field) EditText offsetUtcField;

    ////

    public static final String RESULT_INTENT_DETAIL_REPORT = "result_intent_detail_report";

    private static final String TAG = DetailReportActivity.class.getSimpleName();

    private DetailReportPresenter presenter;
    private Calendar fromCalendar;
    private Calendar toCalendar;

    ////

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_report);
        ButterKnife.bind(this);
        prepareToolbar();

        fromCalendar = Calendar.getInstance();
        toCalendar = Calendar.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter = new DetailReportPresenter();
        maintainInitialState();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter = null;
    }

    ////

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    ////

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        View v = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (v instanceof EditText) {
            View w = getCurrentFocus();
            int scrCoordinates[] = new int[2];
            w.getLocationOnScreen(scrCoordinates);
            float x = event.getRawX() + w.getLeft() - scrCoordinates[0];
            float y = event.getRawY() + w.getTop() - scrCoordinates[1];

            if (event.getAction() == MotionEvent.ACTION_UP && (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w.getBottom()) ) {

                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        }
        return ret;
    }

    ////

    @OnClick(R.id.detail_report_activity_from_date_button)
    public void onClickFromDateButton() {
        @SuppressLint("InflateParams") View datePicketView = getLayoutInflater().inflate(R.layout.picker_date, null);
        final DatePicker datePicker = (DatePicker) datePicketView.findViewById(R.id.detail_report_activity_date_picker);

        new AlertDialog.Builder(this)
                .setView(datePicketView)
                .setTitle(getString(R.string.date_picker_dialog_title))
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

    @OnClick(R.id.detail_report_activity_from_time_button)
    public void onClickFromTimeButton() {
        @SuppressLint("InflateParams") View timePicketView = getLayoutInflater().inflate(R.layout.picker_time, null);
        final TimePicker timePicker = (TimePicker)  timePicketView.findViewById(R.id.detail_report_activity_time_picker);
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
                        setFromTime(hour, minute);
                    }
                })
                .create()
                .show();
    }

    @OnClick(R.id.detail_report_activity_to_date_button)
    public void onClickToDateButton() {
        @SuppressLint("InflateParams") View datePicketView = getLayoutInflater().inflate(R.layout.picker_date, null);
        final DatePicker datePicker = (DatePicker) datePicketView.findViewById(R.id.detail_report_activity_date_picker);

        new AlertDialog.Builder(this)
                .setView(datePicketView)
                .setTitle(getString(R.string.date_picker_dialog_title))
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

    @OnClick(R.id.detail_report_activity_to_time_button)
    public void onClickToTimeButton() {
        @SuppressLint("InflateParams") View timePicketView = getLayoutInflater().inflate(R.layout.picker_time, null);
        final TimePicker timePicker = (TimePicker)  timePicketView.findViewById(R.id.detail_report_activity_time_picker);
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
                        setToTime(hour, minute);
                    }
                })
                .create()
                .show();
    }

    @OnClick(R.id.detail_report_activity_ok_button)
    public void onClickOkButton() {
        if (!validateDataFields()) {
            Toast.makeText(this,
                    getString(R.string.not_all_fields_selected_message),
                    Toast.LENGTH_SHORT).show();
        } else {
            DataForDetailReportRequest data = new DataForDetailReportRequest();
            Map<String, String> dataMap = data.getDataMap();
            dataMap.put(DataForDetailReportRequest.DATAKEY_DATE_FROM, fromCalendar.getTimeInMillis() / 1000 + "");
            dataMap.put(DataForDetailReportRequest.DATAKEY_DATE_TO, toCalendar.getTimeInMillis() / 1000 + "");
            dataMap.put(DataForDetailReportRequest.DATAKEY_DELTA, deltaField.getText().toString());
            dataMap.put(DataForDetailReportRequest.DATAKEY_OFFSET_UTC, offsetUtcField.getText().toString());

            Intent resultIntent = new Intent();
            resultIntent.putExtra(RESULT_INTENT_DETAIL_REPORT, data);
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }

    ////

    private void prepareToolbar() {
        setSupportActionBar(toolbar);
        toolbarTitle.setText(getString(R.string.detail_report_activity_toolbar_title));
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.actis_navigation_menu_icon));
    }

    @SuppressLint("SetTextI18n")
    private void maintainInitialState() {
        Calendar currentDate = Calendar.getInstance();

        int currentYear = currentDate.get(Calendar.YEAR);
        int currentMonth = currentDate.get(Calendar.MONTH);
        int currentDay = currentDate.get(Calendar.DAY_OF_MONTH);

        setFromDate(currentYear, currentMonth, currentDay);
        setToDate(currentYear, currentMonth, currentDay);

        deltaField.setText(Integer.toString(MINIMAL_DELTA));
        offsetUtcField.setText(Integer.toString(presenter.calculateOffsetUtc()));

    }

    private void setFromDate(int year, int month, int day) {
        fromDateButton.setText(String.format(Locale.ROOT, "%s.%s.%d",
                (day >= 10 ? day : "0" + day),
                (month >= 10 ? (month + 1) : "0" + (month + 1)),
                year));
        fromCalendar.set(Calendar.YEAR, year);
        fromCalendar.set(Calendar.MONTH, month);
        fromCalendar.set(Calendar.DAY_OF_MONTH, day);

        if (isFromTimeButtonDefault()) setFromTime(0, 0);
    }

    private void setFromTime(int hour, int minute) {
        fromTimeButton.setText(String.format(Locale.ROOT, "%s:%s",
                (hour >= 10 ? hour : "0" + hour),
                (minute >= 10 ? minute : "0" + minute)));
        fromCalendar.set(Calendar.HOUR_OF_DAY, hour);
        fromCalendar.set(Calendar.MINUTE, minute);
    }

    private void setToDate(int year, int month, int day) {
        toDateButton.setText(String.format(Locale.ROOT, "%s.%s.%d",
                (day >= 10 ? day : "0" + day),
                (month >= 10 ? (month + 1) : "0" + (month + 1)),
                year));
        toCalendar.set(Calendar.YEAR, year);
        toCalendar.set(Calendar.MONTH, month);
        toCalendar.set(Calendar.DAY_OF_MONTH, day);

        if (isToTimeButtonDefault()) setToTime(23, 59);
    }

    private void setToTime(int hour, int minute) {
        toTimeButton.setText(String.format(Locale.ROOT, "%s:%s",
                (hour >= 10 ? hour : "0" + hour),
                (minute >= 10 ? minute : "0" + minute)));
        toCalendar.set(Calendar.HOUR_OF_DAY, hour);
        toCalendar.set(Calendar.MINUTE, minute);
    }

    private boolean isFromDateButtonDefault() {
        return fromDateButton.getText().toString().equals(getString(R.string.date_button_label));
    }

    private boolean isFromTimeButtonDefault() {
        return fromTimeButton.getText().toString().equals(getString(R.string.time_button_label));
    }

    private boolean isToDateButtonDefault() {
        return toDateButton.getText().toString().equals(getString(R.string.date_button_label));
    }

    private boolean isToTimeButtonDefault() {
        return toTimeButton.getText().toString().equals(getString(R.string.time_button_label));
    }

    private boolean validateDataFields() {
        if (isFromDateButtonDefault()) {
            return false;
        }

        if (isFromTimeButtonDefault()) {
            return false;
        }

        if (isToDateButtonDefault()) {
            return false;
        }

        if (isToTimeButtonDefault()) {
            return false;
        }

        if (deltaField.getText().toString().isEmpty()) {
            return false;
        }

        if (offsetUtcField.getText().toString().isEmpty()) {
            return false;
        }

        return true;
    }
}



































