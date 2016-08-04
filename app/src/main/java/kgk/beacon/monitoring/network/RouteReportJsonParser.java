package kgk.beacon.monitoring.network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import kgk.beacon.monitoring.domain.model.routereport.RouteReport;
import kgk.beacon.monitoring.domain.model.routereport.RouteReportEvent;

public class RouteReportJsonParser {

    private final long monitoringEntityId;

    ////

    public RouteReportJsonParser(long monitoringEntityId) {
        this.monitoringEntityId = monitoringEntityId;
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
        List<RouteReportEvent> events = new ArrayList<>();

        for (int i = 0; i < eventList.length(); i++) {
            try {
                JSONObject eventJson = eventList.getJSONObject(i);
                String eventType = eventJson.getString("eventType");

                switch (eventType) {
                    case "Parking":
                        break;
                    case "Moving":
                        break;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    ////

    private RouteReportEvent parseParkingEvent(JSONObject json) throws JSONException {
        JSONArray track = json.getJSONArray("treck");
        JSONObject trackData = track.getJSONObject(0);

        return null;
    }

    private RouteReportEvent parseMovingEvent(JSONObject json) throws JSONException {


        return null;
    }
}
