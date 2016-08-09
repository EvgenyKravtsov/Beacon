package kgk.beacon.monitoring.presentation.view;

import java.util.Calendar;

public interface RouteReportView {

    void toggleDayDisplay(Calendar day, boolean enabled);

    void showEventDetails(
            String eventType,
            double speed,
            int csq,
            int satellites);

    void mapReadyForUse();
}
