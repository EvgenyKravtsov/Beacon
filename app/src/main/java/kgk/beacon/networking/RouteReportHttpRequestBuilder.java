package kgk.beacon.networking;


import java.util.HashMap;
import java.util.Map;

public class RouteReportHttpRequestBuilder {

    public Map<String, String> prepareHeaders(String sessionId) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Cookie", sessionId);
        headers.put("X-Requested-With", "XMLHttpRequest");
        headers.put("Origin", "http://monitor.kgk-global.com");
        headers.put("Referer", "http://monitor.kgk-global.com/monitoring");
        return headers;
    }

    public Map<String, String> prepareBody() {
        return null;
    }

    ////
}
