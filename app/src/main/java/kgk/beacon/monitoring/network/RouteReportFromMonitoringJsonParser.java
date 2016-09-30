package kgk.beacon.monitoring.network;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;

import kgk.beacon.monitoring.domain.model.routereport.MovingEvent;
import kgk.beacon.monitoring.domain.model.routereport.MovingEventSignal;
import kgk.beacon.monitoring.domain.model.routereport.ParkingEvent;
import kgk.beacon.monitoring.domain.model.routereport.RouteReport;
import kgk.beacon.monitoring.domain.model.routereport.RouteReportEvent;

public class RouteReportFromMonitoringJsonParser {

    private SimpleDateFormat dateFormat;
    private StringBuilder startDateString;
    private StringBuilder endDateString;

    ////

    public RouteReportFromMonitoringJsonParser() {
        dateFormat = new SimpleDateFormat("dd.MM.yy|HH:mm:ss", Locale.ROOT);
        startDateString = new StringBuilder();
        endDateString = new StringBuilder();
    }

    ////

    public RouteReport parse(JSONArray routeReportEvents) {
        try {
            List<RouteReportEvent> events = new ArrayList<>();
            for (int i = 0; i < routeReportEvents.length(); i++) {
                RouteReportEvent event = parseRouteReportEntity(routeReportEvents.getJSONObject(i));
                if (event != null) events.add(event);
            }

            return new RouteReport(composeRouteReportDays(events));

        } catch (JSONException | ParseException e) { e.printStackTrace(); }

        return null;
    }

    ////

    private RouteReportEvent parseRouteReportEntity(JSONObject routeReportEntity)
            throws JSONException, ParseException {
        String eventType = routeReportEntity.getString("eventType");

        switch (eventType) {
            case "Parking": return parseParkingEvent(routeReportEntity);
            case "Moving": return parseMovingEvent(routeReportEntity);
        }

        return null;
    }

    private ParkingEvent parseParkingEvent(JSONObject json)
            throws JSONException, ParseException {

        JSONArray track = json.getJSONArray("treck");
        JSONObject trackData = track.getJSONObject(0);

        startDateString.setLength(0);
        endDateString.setLength(0);

        String date = json.getString("date");
        String start = json.getString("begTime");
        String end = json.getString("endTime");

        startDateString.append(date).append("|").append(start);
        endDateString.append(date).append("|").append(end);

        Date startDate = dateFormat.parse(startDateString.toString());
        Date endDate = dateFormat.parse(endDateString.toString());

        long duration = (endDate.getTime() - startDate.getTime()) / 1000;

        return new ParkingEvent(
                startDate.getTime(),
                endDate.getTime(),
                duration,
                json.getString("address"),
                trackData.getDouble("lat"),
                trackData.getDouble("lng"),
                trackData.getInt("csq")
        );
    }

    private MovingEvent parseMovingEvent(JSONObject json)
            throws JSONException, ParseException {

        startDateString.setLength(0);
        endDateString.setLength(0);

        String date = json.getString("date");
        String start = json.getString("begTime");
        String end = json.getString("endTime");

        startDateString.append(date).append("|").append(start);
        endDateString.append(date).append("|").append(end);

        Date startDate = dateFormat.parse(startDateString.toString());
        Date endDate = dateFormat.parse(endDateString.toString());

        long duration = (endDate.getTime() - startDate.getTime()) / 1000;

        JSONArray track = json.getJSONArray("treck");
        List<MovingEventSignal> signals = new ArrayList<>();

        for (int i = 1; i < track.length(); i++) {
            JSONObject signalJson = track.getJSONObject(i);

            MovingEventSignal signal = new MovingEventSignal(
                    signalJson.getLong("mktime") * 1000,
                    signalJson.getDouble("lat"),
                    signalJson.getDouble("lng"),
                    signalJson.getDouble("speed"),
                    signalJson.getInt("csq"),
                    signalJson.getInt("az")
            );

            signals.add(signal);
        }

        Collections.sort(signals, new Comparator<MovingEventSignal>() {
            @Override
            public int compare(MovingEventSignal movingEventSignal, MovingEventSignal t1) {
                if (movingEventSignal.getTime() > t1.getTime()) return 1;
                if (movingEventSignal.getTime() == t1.getTime()) return 0;
                return -1;
            }
        });

        return new MovingEvent(
                startDate.getTime(),
                endDate.getTime(),
                duration,
                json.getString("data1"),
                track.getJSONObject(0).getDouble("lat"),
                track.getJSONObject(0).getDouble("lng"),
                json.getDouble("speed"),
                json.getDouble("maxSpeed"),
                json.getDouble("race"),
                json.getDouble("sumRace"),
                signals
        );
    }

    private SortedMap<Long, List<RouteReportEvent>> composeRouteReportDays(List<RouteReportEvent> events) {
        Collections.sort(events, new Comparator<RouteReportEvent>() {
            @Override
            public int compare(RouteReportEvent e1, RouteReportEvent e2) {
                if (e1.getStartTime() > e2.getStartTime()) return 1;
                if (e1.getStartTime() == e2.getStartTime()) return 0;
                return -1;
            }
        });

        SortedMap<Long, List<RouteReportEvent>> routeReportDays = new TreeMap<>();
        Calendar calendar = Calendar.getInstance();

        for (RouteReportEvent event : events) {
            calendar.setTime(new Date(event.getStartTime()));
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            long key = calendar.getTimeInMillis();

            if (routeReportDays.get(key) == null) {
                routeReportDays.put(key, new ArrayList<RouteReportEvent>());
                routeReportDays.get(key).add(event);
            } else routeReportDays.get(key).add(event);
        }

        return routeReportDays;
    }
}
