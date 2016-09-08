package kgk.beacon.monitoring.presentation.routereport;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import kgk.beacon.R;
import kgk.beacon.monitoring.domain.model.routereport.RouteReport;

import static kgk.beacon.monitoring.presentation.routereport.RouteReportContract.*;

public class RouteReportActivity extends AppCompatActivity
        implements View {

    private static final String EXTRA_ROUTE_REPORT = "extra_route_report";

    private Presenter presenter;

    ////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_report_mvp);
        attachPresenter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    ////

    private void attachPresenter() {
        Intent startingIntent = getIntent();
        RouteReport routeReport = (RouteReport) startingIntent.getSerializableExtra(EXTRA_ROUTE_REPORT);
        presenter = new RouteReportPresenter(routeReport);
        presenter.attachView(this);
    }
}
