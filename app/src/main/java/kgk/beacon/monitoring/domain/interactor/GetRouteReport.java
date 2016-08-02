package kgk.beacon.monitoring.domain.interactor;

import kgk.beacon.monitoring.MonitoringHttpClient;
import kgk.beacon.monitoring.domain.model.RouteReport;
import kgk.beacon.monitoring.domain.model.RouteReportParameters;
import kgk.beacon.networking.VolleyHttpClient;
import kgk.beacon.util.AppController;

public class GetRouteReport implements Interactor {

    public interface Listener {

        void onRouteReportRetreived(RouteReport routeReport);
    }

    ////

    private final RouteReportParameters parameters;

    private Listener listener;

    ////

    public GetRouteReport(RouteReportParameters parameters) {
        this.parameters = parameters;
    }

    ////

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    ////

    @Override
    public void execute() {
        MonitoringHttpClient httpClient = VolleyHttpClient.getInstance(AppController.getInstance());

        // TODO Delete test code
        httpClient.setListener(null);

        httpClient.requestRouteReport(parameters);
    }
}
