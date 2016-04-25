package kgk.beacon.networking;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

/**
 * Абстракция запроса в Yandex на определение координат по базовым станциям,
 * реализованная в рамках требований библиотеки Volley
 */
public class YandexLBSLocationRequest extends StringRequest {

    private static final String TAG = OpenCellIdRequest.class.getSimpleName();

    ////

    public YandexLBSLocationRequest(int method,
                             String url,
                             Response.Listener<String> listener,
                             Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    ////

    public static String makeUrl(String baseUrl,
                                 int mcc,
                                 int mnc,
                                 int cellId,
                                 int lac) {
        return baseUrl + "?"
                + "&cellid=" + cellId
                + "&operatorid=" + mnc
                + "&countrycode=" + mcc
                + "&lac=" + lac;
    }
}
