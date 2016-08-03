package kgk.beacon.monitoring.domain.interactor;

import kgk.beacon.monitoring.MonitoringHttpClient;
import kgk.beacon.monitoring.domain.model.RouteReport;
import kgk.beacon.monitoring.domain.model.RouteReportParameters;
import kgk.beacon.monitoring.domain.model.User;
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

        httpClient.setListener(new MonitoringHttpClient.Listener() {
            @Override
            public void onUserRetreived(User user) {

            }

            @Override
            public void onMonitoringEntitiesUpdated() {

            }

            @Override
            public void onRouteReportReceived(RouteReport routeReport) {
                if (listener != null) listener.onRouteReportRetreived(routeReport);
            }
        });

        httpClient.requestRouteReport(parameters);
    }
}
