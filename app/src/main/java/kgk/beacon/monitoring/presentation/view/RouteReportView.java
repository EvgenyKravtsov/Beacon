package kgk.beacon.monitoring.presentation.view;

import java.util.Calendar;

import kgk.beacon.monitoring.domain.model.routereport.MovingEvent;
import kgk.beacon.monitoring.domain.model.routereport.MovingEventSignal;
import kgk.beacon.monitoring.domain.model.routereport.ParkingEvent;

public interface RouteReportView {

    void toggleDayDisplay(Calendar day, boolean enabled);

    void showEventDetails(
            String eventType,
            String eventDate,
            double speed,
            int csq,
            int satellites);

    void showEventStartTime(long date);

    void centerOnParkingEvent(ParkingEvent event);

    void centerOnMovingEvent(MovingEvent event);

    void centerOnMovingEventSignal(MovingEventSignal signal);

    void selectDay(long date);

    void mapReadyForUse();

    void clearEventDetails();
}
