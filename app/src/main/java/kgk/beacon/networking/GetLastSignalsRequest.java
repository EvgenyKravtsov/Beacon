package kgk.beacon.networking;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import kgk.beacon.util.AppController;

/**
 * Абстракция запроса на получение свежих сигналов за указанный период,
 * реализованная в рамках требований библиотеки Volley
 */
public class GetLastSignalsRequest extends StringRequest {

    private static final String TAG = GetLastSignalsRequest.class.getSimpleName();

    private String phpSessId;

    public GetLastSignalsRequest(int method,
                                 String url,
                                 Response.Listener<String> listener,
                                 Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    public void setPhpSessId(String phpSessId) {
        this.phpSessId = phpSessId;
    }

    public static String makeUrl(String baseUrl, long fromDate, long toDate) {
        // TODO Delete test code
        Log.d(TAG, baseUrl + "?"
                + "device" + "=" + AppController.getInstance().getActiveDeviceId() + "&"
                + "from" + "=" + fromDate + "&"
                + "to" + "=" + toDate);
        return baseUrl + "?"
                + "device" + "=" + AppController.getInstance().getActiveDeviceId() + "&"
                + "from" + "=" + fromDate + "&"
                + "to" + "=" + toDate;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<>();
        headers.put("Cookie", phpSessId);
        return headers;
    }
}
