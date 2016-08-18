package kgk.beacon.monitoring.presentation.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import kgk.beacon.R;
import kgk.beacon.monitoring.domain.model.MonitoringEntity;
import kgk.beacon.monitoring.domain.model.MonitoringEntityStatus;
import kgk.beacon.monitoring.presentation.presenter.MonitoringEntityViewPresenter;
import kgk.beacon.monitoring.presentation.view.MonitoringEntityView;

public class MonitoringEntityActivity extends AppCompatActivity implements MonitoringEntityView {

    // Views
    private FrameLayout backButton;
    private TextView actionBarTitleTextView;
    private TextView markTextView;
    private TextView modelTextView;
    private TextView stateNumberTextView;
    private TextView idTextView;
    private TextView statusTextView;
    private TextView speedTextView;
    private TextView lastUpdateTextView;
    private TextView gsmTextView;
    private TextView satellitesTextView;
    private TextView ignitionTextView;
    private Button routeReportButton;

    private MonitoringEntityViewPresenter presenter;

    ////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monitoring_activity_monitoring_entity);
        initViews();
        initListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindPresenter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.requestActiveMonitoringEntity();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindPresenter();
    }

    ////

    @SuppressLint("SimpleDateFormat")
    @Override
    public void showActiveMonitoringEntity(MonitoringEntity monitoringEntity) {
        markTextView.setText(monitoringEntity.getMark());
        modelTextView.setText(monitoringEntity.getModel());
        stateNumberTextView.setText(monitoringEntity.getStateNumber());
        idTextView.setText(String.format(Locale.ROOT, "%d", monitoringEntity.getId()));
        statusTextView.setText(makeStatusString(monitoringEntity.getStatus()));
        speedTextView.setText(String.format(Locale.ROOT, "%.2f", monitoringEntity.getSpeed()));

        Date date = new Date(monitoringEntity.getLastUpdateTimestamp());
        lastUpdateTextView.setText(String.format("%s at %s",
                new SimpleDateFormat("dd.MM").format(date),
                new SimpleDateFormat("HH:mm").format(date)));

        gsmTextView.setText(monitoringEntity.getGsm());
        satellitesTextView.setText(String.format(Locale.ROOT, "%d", monitoringEntity.getSatellites()));
        ignitionTextView.setText(monitoringEntity.isEngineIgnited() ? "ON" : "OFF");
    }

    ////

    private void initViews() {
        backButton = (FrameLayout) findViewById(R.id.monitoring_action_bar_back_button);
        actionBarTitleTextView = (TextView) findViewById(R.id.monitoring_action_bar_title_text_view);
        actionBarTitleTextView.setText("Information");

        markTextView = (TextView)
                findViewById(R.id.monitoring_activity_monitoring_entity_mark_text_view);
        modelTextView = (TextView)
                findViewById(R.id.monitoring_activity_monitoring_entity_model_text_view);
        stateNumberTextView = (TextView)
                findViewById(R.id.monitoring_activity_monitoring_entity_state_number_text_view);
        idTextView = (TextView)
                findViewById(R.id.monitoring_activity_monitoring_entity_id_text_view);
        statusTextView = (TextView)
                findViewById(R.id.monitoring_activity_monitoring_entity_status_text_view);
        speedTextView = (TextView)
                findViewById(R.id.monitoring_activity_monitoring_entity_speed_text_view);
        lastUpdateTextView = (TextView)
                findViewById(R.id.monitoring_activity_monitoring_entity_last_update_text_view);
        gsmTextView = (TextView)
                findViewById(R.id.monitoring_activity_monitoring_entity_gsm_text_view);
        satellitesTextView = (TextView)
                findViewById(R.id.monitoring_activity_monitoring_entity_satellites_text_view);
        ignitionTextView = (TextView)
                findViewById(R.id.monitoring_activity_monitoring_entity_ignition_text_view);
        routeReportButton = (Button)
                findViewById(R.id.monitoring_activity_monitoring_entity_route_report_button);
    }

    private void initListeners() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackButtonClick();
            }
        });

        routeReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRouteReportButtonClick();
            }
        });
    }

    private void bindPresenter() {
        presenter = new MonitoringEntityViewPresenter(this);
    }

    private void unbindPresenter() {
        if (presenter != null) presenter.unbindView();
        presenter = null;
    }

    private String makeStatusString(MonitoringEntityStatus status) {
        String statusString;
        switch (status) {
            case IN_MOTION:
                statusString = "Moving";
                break;
            case STOPPED:
                statusString = "Parking";
                break;
            case OFFLINE:
                statusString = "Offline";
                break;
            default:
                statusString = "Offline";
        }
        return statusString;
    }

    //// Control callbacks

    private void onBackButtonClick() {
        NavUtils.navigateUpFromSameTask(this);
    }

    private void onRouteReportButtonClick() {
        Intent intent = new Intent(this, RouteReportSettingsActivity.class);
        startActivity(intent);
    }
}
