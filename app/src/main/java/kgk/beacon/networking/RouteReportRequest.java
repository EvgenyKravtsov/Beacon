package kgk.beacon.networking;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class RouteReportRequest extends Request<JSONObject> {

    private Response.Listener<JSONObject> responseListener;
    private Map<String, String> params;
    private String phpSessId;

    ////

    public RouteReportRequest(
            String url,
            Map<String, String> params,
            Response.Listener<JSONObject> responseListener,
            Response.ErrorListener errorListener) {

        super(Method.GET, url, errorListener);
        this.params = params;
        this.responseListener = responseListener;
    }

    public RouteReportRequest(
            int method,
            String url,
            Map<String, String> params,
            Response.Listener<JSONObject> responseListener,
            Response.ErrorListener errorListener) {

        super(method, url, errorListener);
        this.params = params;
        this.responseListener = responseListener;
    }

    ////

    public void setPhpSessId(String phpSessId) {
        this.phpSessId = phpSessId;
    }

    ////

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
        headers.put("Accept-Encoding", "gzip, deflate");
        headers.put("Accept", "*/*");
        headers.put("Connection", "keep-alive");
        headers.put("Accept-Language", "en-US,en;q=0.8");
        headers.put("User-Agent", "Mozilla/5.0");
        return headers;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(
                    response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            return Response.success(
                    new JSONObject(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) { return Response.error(new ParseError(e)); }
          catch (JSONException e) { return Response.error(new ParseError(e)); }
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        responseListener.onResponse(response);
    }
}
