package kgk.beacon.networking;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AuthenticationRequest extends StringRequest {

    private static final String TAG = AuthenticationRequest.class.getSimpleName();

    private String login;
    private String password;

    public AuthenticationRequest(int method,
                                 String url,
                                 Response.Listener<String> listener,
                                 Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    protected Map<String, String> getParams() {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("login", login);
        parameters.put("password", password);
        return parameters;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String sessionId = response.headers.get("Set-Cookie");
        String responseData = "";

        try {
            JSONObject responseJson = new JSONObject(new String(response.data));
            responseJson.put("Cookie", sessionId);
            responseData = responseJson.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return Response.success(responseData, HttpHeaderParser.parseCacheHeaders(response));
    }
}
