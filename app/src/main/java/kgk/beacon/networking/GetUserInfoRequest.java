package kgk.beacon.networking;


import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GetUserInfoRequest extends JsonObjectRequest {

    private static final String TAG = GetUserInfoRequest.class.getSimpleName();

    private String phpSessId;

    ////

    public GetUserInfoRequest(String url,
                              JSONObject jsonRequest,
                              Response.Listener<JSONObject> listener,
                              Response.ErrorListener errorListener) {
        super(url, jsonRequest, listener, errorListener);
    }

    ////

    public void setPhpSessId(String phpSessId) {
        this.phpSessId = phpSessId;
    }

    ////

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<>();
        headers.put("Cookie", phpSessId);
        return headers;
    }
}
