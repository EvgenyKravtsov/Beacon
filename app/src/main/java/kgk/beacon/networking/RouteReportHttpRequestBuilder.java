package kgk.beacon.networking;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RouteReportHttpRequestBuilder {

    public final long fromDateTimestamp;
    public final long toDateTimestamp;
    public final int stopTime;
    public final int offsetUtc;
    public final long id;

    ////

    public RouteReportHttpRequestBuilder(
            long fromDateTimestamp,
            long toDateTimestamp,
            int stopTime,
            int offsetUtc,
            long id) {
        this.fromDateTimestamp = fromDateTimestamp;
        this.toDateTimestamp = toDateTimestamp;
        this.stopTime = stopTime;
        this.offsetUtc = offsetUtc;
        this.id = id;
    }

    ////

    public Map<String, String> prepareHeaders(String sessionId) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Cookie", sessionId);
        headers.put("X-Requested-With", "XMLHttpRequest");
        headers.put("Origin", "http://monitor.kgk-global.com");
        headers.put("Referer", "http://monitor.kgk-global.com/monitoring");
        return headers;
    }

    public Map<String, String> prepareBody() {
        Map<String, String> body = new HashMap<>();
        body.put("device_mode", "group");
        body.put("type", "Detailed");
        body.put("dateFrom", prepareFromDateString());
        body.put("timeFrom", prepareFromTimeString());
        body.put("dateTo", prepareToDateString());
        body.put("timeTo", prepareToTimeString());
        body.put("parkings", "on");
        body.put("delta", prepareDelta());
        body.put("speedTrackLimit", "100");
        body.put("moving", "on");
        body.put("timeout", "on");
        body.put("prizmaType2", "day");
        body.put("NASTdelta", "10");
        body.put("speed", "120");
        body.put("fuel_hour", "0");
        body.put("fuel_type", "1");
        body.put("minTerm", "");
        body.put("maxTerm", "");
        body.put("zoneRaceType", "full");
        body.put("startstopoption", "1");
        body.put("time_delta", "10");
        body.put("advancedreportoption", "1");
        body.put("waybill_timeout", "");
        body.put("waybill_timeon", "");
        body.put("waybill_startkm", "");
        body.put("waybill_stopkm", "");
        body.put("waybill_fuel", "");
        body.put("name", "");
        body.put("report_interval", "lastWeek");
        body.put("comboboxselect-1059-inputEl", "");
        body.put("report_format", "pdf");
        body.put("OffsetUTC", prepareOffsetUtc());
        body.put("groupId", "3117");
        body.put("deviceId", prepareId());
        body.put("infoWindow", "не установлен");
        body.put("infoWindow", "не установлен");
        body.put("infoWindow", "не установлен");
        body.put("infoWindow", "не установлен");
        body.put("infoWindow", "не установлен");
        body.put("infoWindow", "не установлен");
        body.put("infoWindow", "false");
        body.put("page", "1");
        body.put("start", "0");
        body.put("limit", "25");
        return body;
    }

    ////

    private String prepareFromDateString() {
        Date date = new Date(fromDateTimestamp * 1000);
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return dateFormat.format(date);
    }

    private String prepareFromTimeString() {
        Date date = new Date(fromDateTimestamp * 1000);
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(date);
    }

    private String prepareToDateString() {
        Date date = new Date(toDateTimestamp * 1000);
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return dateFormat.format(date);
    }

    private String prepareToTimeString() {
        Date date = new Date(toDateTimestamp * 1000);
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(date);
    }

    private String prepareDelta() {
        return Integer.toString(stopTime);
    }

    private String prepareOffsetUtc() {
        return Integer.toString(offsetUtc);
    }

    private String prepareId() {
        return Long.toString(id);
    }
}













































