package kgk.beacon.monitoring.presentation.presenter;

import kgk.beacon.monitoring.domain.interactor.GetRouteReport;
import kgk.beacon.monitoring.domain.interactor.InteractorThreadPool;
import kgk.beacon.monitoring.domain.model.routereport.RouteReport;
import kgk.beacon.monitoring.domain.model.routereport.RouteReportParameters;
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

    public void sendRouteReportRequest(RouteReportParameters parameters) {
        GetRouteReport interactor = new GetRouteReport(parameters);
        interactor.setListener(this);
        InteractorThreadPool.getInstance().execute(interactor);
    }

    ////

    @Override
    public void onRouteReportRetreived(final RouteReport routeReport) {
        AppController.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (view != null) {
                    if (routeReport.getDays().size() > 0 ) view.navigateToRouteReportView(routeReport);
                    else view.notifyNoDataForRouteReport();
                }
            }
        });
    }
}
