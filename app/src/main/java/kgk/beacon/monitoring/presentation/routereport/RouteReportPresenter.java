package kgk.beacon.monitoring.presentation.routereport;

import kgk.beacon.monitoring.domain.model.routereport.RouteReport;

import static kgk.beacon.monitoring.presentation.routereport.RouteReportContract.*;

public class RouteReportPresenter implements Presenter {

    private View view;

    private RouteReport routeReport;

    ////

    public RouteReportPresenter(RouteReport routeReport) {
        this.routeReport = routeReport;
    }

    ////

    public View getView() {
        return view;
    }

    ////

    @Override
    public void attachView(View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        view = null;
    }

    ////

    private boolean isViewAttached() {
        return view != null;
    }
}
