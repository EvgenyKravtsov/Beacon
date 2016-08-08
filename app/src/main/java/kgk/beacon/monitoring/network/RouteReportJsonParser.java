package kgk.beacon.monitoring.network;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import kgk.beacon.monitoring.domain.model.routereport.MovingEvent;
import kgk.beacon.monitoring.domain.model.routereport.MovingEventSignal;
import kgk.beacon.monitoring.domain.model.routereport.ParkingEvent;
import kgk.beacon.monitoring.domain.model.routereport.RouteReport;
import kgk.beacon.monitoring.domain.model.routereport.RouteReportEvent;

public class RouteReportJsonParser {

    private final long monitoringEntityId;

    private SimpleDateFormat dateFormat;
    private StringBuilder startDateString;
    private StringBuilder endDateString;

    ////

    public RouteReportJsonParser(long monitoringEntityId) {
        this.monitoringEntityId = monitoringEntityId;
        dateFormat = new SimpleDateFormat("dd.MM.yy|HH:mm:ss", Locale.ROOT);
        startDateString = new StringBuilder();
        endDateString = new StringBuilder();
    }

    ////

    public RouteReport parse(String rawData) throws JSONException {

        JSONObject dataJson = new JSONObject(rawData);
        JSONObject dataById = dataJson.getJSONObject(String.format(
                Locale.ROOT,
                "%d",
                monitoringEntityId)
        );

        JSONArray eventList = dataById.getJSONArray("rows");

        // TODO Delete test code
        for (int i = 0; i < eventList.length(); i++) Log.d("debug", eventList.getJSONObject(i).toString());

        List<RouteReportEvent> events = new ArrayList<>();

        for (int i = 0; i < eventList.length(); i++) {
            try {
                JSONObject eventJson = eventList.getJSONObject(i);
                String eventType = eventJson.getString("eventType");

                switch (eventType) {
                    case "Parking":
                        ParkingEvent parkingEvent = parseParkingEvent(eventJson);
                        events.add(parkingEvent);
                        break;
                    case "Moving":
                        MovingEvent movingEvent = parseMovingEvent(eventJson);
                        events.add(movingEvent);
                        break;
                }
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }
        }

        Map<Long, List<RouteReportEvent>> days = new HashMap<>();
        Calendar calendar = Calendar.getInstance();

        for (RouteReportEvent event : events) {
            calendar.setTime(new Date(event.getStartTime()));
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            long key = calendar.getTimeInMillis();

            if (days.get(key) == null) {
                days.put(key, new ArrayList<RouteReportEvent>());
                days.get(key).add(event);
            } else days.get(key).add(event);
        }

        return new RouteReport(days);
    }

    ////

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
                    signalJson.getDouble("lat"),
                    signalJson.getDouble("lng"),
                    signalJson.getDouble("speed"),
                    signalJson.getInt("csq")
            );

            signals.add(signal);
        }

        return new MovingEvent(
                startDate.getTime(),
                endDate.getTime(),
                duration,
                json.getString("data1"),
                track.getJSONObject(0).getDouble("lat"),
                track.getJSONObject(0).getDouble("lng"),
                signals
        );
    }
}
