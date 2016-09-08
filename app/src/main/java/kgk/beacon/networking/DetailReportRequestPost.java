package kgk.beacon.networking;

import android.annotation.SuppressLint;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import kgk.beacon.monitoring.domain.model.routereport.RouteReportParameters;

public class DetailReportRequestPost extends StringRequest {

    private static final String TAG = DetailReportRequestPost.class.getSimpleName();

    private String phpSessId;
    private RouteReportParameters parameters;

    public DetailReportRequestPost(
            int method,
            String url,
            Response.Listener<String> listener,
            Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    public void setPhpSessId(String phpSessId) {
        this.phpSessId = phpSessId;
    }

    public void setParameters(RouteReportParameters parameters) {
        this.parameters = parameters;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<>();
        headers.put("Cookie", phpSessId + "; kgklang=ru;");

        // TODO Delete test code
        Log.d("debug", phpSessId);

        headers.put("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
        headers.put("X-Requested-With", "XMLHttpRequest");
        headers.put("Origin", "http://monitor.kgk-global.com");
        headers.put("Referer", "http://monitor.kgk-global.com/monitoring");
        headers.put("Host", "monitor.kgk-global.com");
        headers.put("Accept-Encoding", "gzip, deflate");
        headers.put("Accept", "*/*");
        headers.put("Connection", "keep-alive");
        headers.put("Accept-Language", "en-US,en;q=0.8");
        return headers;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    protected Map<String, String> getParams() {
        Map<String, String> postParams = new HashMap<>();

        Date fromDate = new Date(parameters.getFromDateTimestamp() * 1000);
        Date toDate = new Date(parameters.getToDateTimestamp() * 1000);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String fromDateString = dateFormat.format(fromDate);
        String fromTimeString = timeFormat.format(fromDate);
        String toDateString = dateFormat.format(toDate);
        String toTimeString = timeFormat.format(toDate);

//        postParams.put("type", "Detailed");
//        postParams.put("dateFrom", fromDateString);
//        postParams.put("timeFrom", fromTimeString);
//        postParams.put("dateTo", toDateString);
//        postParams.put("timeTo", toTimeString);
//        postParams.put("parking", "on");
//        postParams.put("delta", Integer.toString(parameters.getStopTime()));
//        postParams.put("speedTrackLimit", "100");
//        postParams.put("moving", "on");
//        postParams.put("OffsetUTC", Integer.toString(parameters.getOffsetUtc()));
//        postParams.put("deviceId", Long.toString(parameters.getId()));
//        postParams.put("groupId", "3117");
//        postParams.put("device_mode", "group");

        postParams.put("device_mode", "group");
        postParams.put("type", "Detailed");
        postParams.put("dateFrom", "07-09-2016");
        postParams.put("timeFrom", "00:00");
        postParams.put("dateTo", "07-09-2016");
        postParams.put("timeTo", "23:59");
        postParams.put("parkings", "on");
        postParams.put("delta", "180");
        postParams.put("speedTrackLimit", "100");
        postParams.put("moving", "on");
        postParams.put("timeout", "on");
        postParams.put("prizmaType2", "day");
        postParams.put("NASTdelta", "10");
        postParams.put("speed", "120");
        postParams.put("fuel_hour", "0");
        postParams.put("fuel_type", "1");
        postParams.put("minTerm", "");
        postParams.put("maxTerm", "");
        postParams.put("zoneRaceType", "full");
        postParams.put("startstopoption", "1");
        postParams.put("time_delta", "10");
        postParams.put("advancedreportoption", "1");
        postParams.put("waybill_timeout", "");
        postParams.put("waybill_timeon", "");
        postParams.put("waybill_startkm", "");
        postParams.put("waybill_stopkm", "");
        postParams.put("waybill_fuel", "");
        postParams.put("name", "");
        postParams.put("report_interval", "lastWeek");
        postParams.put("comboboxselect-1059-inputEl", "");
        postParams.put("report_format", "pdf");
        postParams.put("OffsetUTC", "-180");
        postParams.put("groupId", "3117");
        postParams.put("deviceId", "5040682918");
        postParams.put("infoWindow", "не установлен");
        postParams.put("infoWindow", "не установлен");
        postParams.put("infoWindow", "не установлен");
        postParams.put("infoWindow", "не установлен");
        postParams.put("infoWindow", "не установлен");
        postParams.put("infoWindow", "не установлен");
        postParams.put("infoWindow", "false");
        postParams.put("page", "1");
        postParams.put("start", "0");
        postParams.put("limit", "25");

        return postParams;
    }
}































