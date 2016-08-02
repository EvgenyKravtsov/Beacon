package kgk.beacon.monitoring.presentation.presenter;

import kgk.beacon.monitoring.domain.interactor.GetRouteReport;
import kgk.beacon.monitoring.domain.interactor.InteractorThreadPool;
import kgk.beacon.monitoring.domain.model.RouteReport;
import kgk.beacon.monitoring.domain.model.RouteReportParameters;
import kgk.beacon.monitoring.presentation.view.RouteReportSettingsView;

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
    public void onRouteReportRetreived(RouteReport routeReport) {

    }
}
