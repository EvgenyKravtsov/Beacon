package kgk.beacon.networking;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import kgk.beacon.util.AppController;

public class QueryBeaconRequest extends StringRequest {

    private static final String TAG = QueryBeaconRequest.class.getSimpleName();

    private String phpSessId;

    public QueryBeaconRequest(int method,
                              String url,
                              Response.Listener<String> listener,
                              Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    public void setPhpSessId(String phpSessId) {
        this.phpSessId = phpSessId;
    }

    public static String makeUrl(String baseUrl) {
        return baseUrl + "?"
                + "device" + "=" + AppController.getInstance().getActiveDeviceId();
                // + "query" + "=" + "true";
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<>();
        headers.put("Cookie", phpSessId);
        return headers;
    }
}
