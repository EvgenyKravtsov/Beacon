package kgk.beacon.networking;


import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Абстракция запроса на отправку настроек для устройства Actis,
 * реализованная в рамках требований библиотеки Volley
 */
public class SettingsRequest extends JsonObjectRequest {

    private static final String TAG = SettingsRequest.class.getSimpleName();

    private String phpSessId;

    public SettingsRequest(String url,
                           JSONObject jsonRequest,
                           Response.Listener<JSONObject> listener,
                           Response.ErrorListener errorListener) {
        super(url, jsonRequest, listener, errorListener);
    }

    public void setPhpSessId(String phpSessId) {
        this.phpSessId = phpSessId;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<>();
        headers.put("Cookie", phpSessId);
        return headers;
    }
}
