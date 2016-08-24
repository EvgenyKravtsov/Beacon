package kgk.beacon.monitoring.presentation.model;

import kgk.beacon.monitoring.domain.model.routereport.RouteReportEvent;

public abstract class RouteReportMapObject {

    public abstract RouteReportEvent getEvent();

    public abstract void draw();

    public abstract void clear();
}
