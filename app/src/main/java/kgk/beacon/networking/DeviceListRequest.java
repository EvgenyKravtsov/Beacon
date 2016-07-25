package kgk.beacon.networking;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Абстракция запроса на получения списка устройств пользователя,
 * реализованная в рамках требований библиотеки Volley
 */
public class DeviceListRequest extends StringRequest {

    private static final String TAG = DeviceListRequest.class.getSimpleName();

    private String phpSessId;

    public DeviceListRequest(int method,
                             String url,
                             Response.Listener<String> listener,
                             Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    public void setPhpSessId(String phpSessId) {
        this.phpSessId = phpSessId;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {

        // TODO Delete test code
        Log.d(TAG, phpSessId);

        Map<String, String> headers = new HashMap<>();
        headers.put("Cookie", phpSessId);
        return headers;
    }
}