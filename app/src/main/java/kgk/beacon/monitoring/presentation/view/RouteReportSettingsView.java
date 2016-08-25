package kgk.beacon.monitoring.presentation.view;

import kgk.beacon.monitoring.domain.model.routereport.RouteReport;

public interface RouteReportSettingsView {

    void navigateToRouteReportView(RouteReport routeReport);

    void notifyNoDataForRouteReport();
}
