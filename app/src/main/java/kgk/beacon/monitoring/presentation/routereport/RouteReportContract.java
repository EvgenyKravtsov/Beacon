package kgk.beacon.monitoring.presentation.routereport;

public interface RouteReportContract {

    interface View {

    }

    interface Map {

        void setPresenter(Presenter presenter);
    }

    interface Presenter {

        void attachView(View view);

        void detachView();
    }
}
