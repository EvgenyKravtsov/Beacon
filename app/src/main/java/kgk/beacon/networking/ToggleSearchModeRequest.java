package kgk.beacon.networking;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import kgk.beacon.util.AppController;

/**
 * Абстракция запроса на отправку комманды на включение/выключение режима Поиск устройства Actis,
 * реализованная в рамках требований библиотеки Volley
 */
public class ToggleSearchModeRequest extends StringRequest {

    private static final String TAG = ToggleSearchModeRequest.class.getSimpleName();

    private String phpSessId;

    public ToggleSearchModeRequest(int method,
                              String url,
                              Response.Listener<String> listener,
                              Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    public void setPhpSessId(String phpSessId) {
        this.phpSessId = phpSessId;
    }

    public static String makeUrl(String baseUrl, boolean searchModeStatus) {
        return baseUrl + "?"
                + "device" + "=" + AppController.getInstance().getActiveDeviceId() + "&"
                + "search" + "=" + searchModeStatus;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<>();
        headers.put("Cookie", phpSessId);
        return headers;
    }
}
