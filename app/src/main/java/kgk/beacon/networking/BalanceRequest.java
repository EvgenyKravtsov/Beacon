package kgk.beacon.networking;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class BalanceRequest extends StringRequest {

    private static final String TAG = AuthenticationRequest.class.getSimpleName();

    private String phpSessId;

    //// CONSTRUCTORS

    public BalanceRequest(int method,
                          String url,
                          Response.Listener<String> listener,
                          Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    //// PUBLIC METHODS

    public void setPhpSessId(String phpSessId) {
        this.phpSessId = phpSessId;
    }

    //// VOLLEY REQUEST INTERFACE

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<>();
        headers.put("Cookie", phpSessId);
        return headers;
    }
}
