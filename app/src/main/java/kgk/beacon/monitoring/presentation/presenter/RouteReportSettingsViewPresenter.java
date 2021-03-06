package kgk.beacon.monitoring.presentation.presenter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import kgk.beacon.monitoring.domain.interactor.GetRouteReport;
import kgk.beacon.monitoring.domain.interactor.InteractorThreadPool;
import kgk.beacon.monitoring.domain.model.routereport.RouteReport;
import kgk.beacon.monitoring.domain.model.routereport.RouteReportParametersPeriodSeparated;
import kgk.beacon.monitoring.presentation.view.RouteReportSettingsView;
import kgk.beacon.util.AppController;

public class RouteReportSettingsViewPresenter
        implements GetRouteReport.Listener {

    private RouteReportSettingsView view;

    ////

    public RouteReportSettingsViewPresenter(RouteReportSettingsView view) {
        this.view = view;
    }

    ////

    public void unbindView() {
        view = null;
    }

    public void sendRouteReportRequest(RouteReportParametersPeriodSeparated parameters) {
        GetRouteReport interactor = new GetRouteReport(parameters);
        interactor.setListener(this);
        InteractorThreadPool.getInstance().execute(interactor);
    }

    public boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                AppController.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    ////

    @Override
    public void onRouteReportRetreived(final RouteReport routeReport) {
        AppController.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RouteReport.RouteReportInstance = routeReport;
                view.navigateToRouteReportView();
            }
        });
    }
}
