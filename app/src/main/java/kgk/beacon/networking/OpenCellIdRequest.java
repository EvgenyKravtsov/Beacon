package kgk.beacon.networking;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

/**
 * Абстракция запроса в OpenCellId на определение координат по базовым станциям,
 * реализованная в рамках требований библиотеки Volley
 */
public class OpenCellIdRequest extends StringRequest {

    private static final String TAG = OpenCellIdRequest.class.getSimpleName();

    ////

    public OpenCellIdRequest(int method,
                             String url,
                             Response.Listener<String> listener,
                             Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    ////

    public static String makeUrl(String baseUrl,
                                 String apiKey,
                                 int mcc,
                                 int mnc,
                                 int cellId,
                                 int lac) {
        return baseUrl + "?"
                + "key=" + apiKey
                + "&mcc=" + mcc
                + "&mnc=" + mnc
                + "&cellid=" + cellId
                + "&lac=" + lac
                + "&format=json";
    }
}
