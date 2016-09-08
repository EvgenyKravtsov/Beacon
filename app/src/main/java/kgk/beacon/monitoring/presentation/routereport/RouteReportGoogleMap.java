package kgk.beacon.monitoring.presentation.routereport;

import static kgk.beacon.monitoring.presentation.routereport.RouteReportContract.*;

public class RouteReportGoogleMap implements Map {

    private Presenter presenter;

    ////

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }
}
