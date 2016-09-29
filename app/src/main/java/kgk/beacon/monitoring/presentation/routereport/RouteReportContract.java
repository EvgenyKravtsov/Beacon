package kgk.beacon.monitoring.presentation.routereport;

import java.util.List;

import kgk.beacon.monitoring.domain.model.routereport.MovingEvent;
import kgk.beacon.monitoring.domain.model.routereport.MovingEventSignal;
import kgk.beacon.monitoring.domain.model.routereport.ParkingEvent;
import kgk.beacon.monitoring.domain.model.routereport.RouteReportEvent;
import kgk.beacon.monitoring.domain.model.routereport.RouteReportEventType;
import kgk.beacon.monitoring.presentation.model.MapType;

public interface RouteReportContract {

    interface View {

        void displayMark(String mark);

        void displaySpeed(String speed);

        void displaySatellites(String satellites);

        void displayGsmLevel(int gsmLevel);

        void displayEventDescription(RouteReportEventType eventType, String eventTime);

        void moveTimeline(long timestamp);

        void resetTimeline();

        void scrollEventsListToPosition(int position);

        void collapseSlider();
    }

    interface Map {

        void setPresenter(Presenter presenter);

        void init(MapType mapType, float zoomLevel);

        void displayRouteReportEvents(List<RouteReportEvent> events);

        void centerOnParkingEvent(ParkingEvent event);

        void centerOnMovingEvent(MovingEvent event);

        void centerOnMovingEventSignal(MovingEventSignal event);

        void zoomIn();

        void zoomOut();

        void setMapLayer(MapType mapType);
    }

    interface DaysView {

        void setPresenter(Presenter presenter);

        void setTimestamps(List<Long> timestamps);

        void setActiveDay(long timestamp);
    }

    interface EventsView {

        void setPresenter(Presenter presenter);

        void setEvents(List<RouteReportEvent> events);
    }

    interface Presenter {

        void attachView(View view);

        void detachView();

        void onCreateView();

        void onMapReady();

        void onTimeChosenFromTimeline(int timeInMilleseconds);

        void onDayChosen(long dayTimestamp);

        void onEventChosen(RouteReportEvent event);

        void onMapZoomInButtonClick();

        void onMapZoomOutButtonClick();

        void onMapZoomLevelChanged(float currentZoom);

        void onMapLayerSelected(MapType mapType);

        void onMapClick();
    }
}
