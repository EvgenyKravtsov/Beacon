package kgk.beacon.monitoring.domain.interactor;

import kgk.beacon.monitoring.network.MonitoringHttpClient;
import kgk.beacon.monitoring.domain.model.routereport.RouteReport;
import kgk.beacon.monitoring.domain.model.routereport.RouteReportParameters;
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

        httpClient.setRouteReportListener(new MonitoringHttpClient.RouteReportListener() {
            @Override
            public void onRouteReportReceived(RouteReport routeReport) {
                if (listener != null) listener.onRouteReportRetreived(routeReport);
            }
        });

        httpClient.requestRouteReport(parameters);
    }
}
