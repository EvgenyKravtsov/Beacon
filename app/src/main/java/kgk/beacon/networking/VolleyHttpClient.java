package kgk.beacon.networking;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import de.greenrobot.event.EventBus;
import kgk.beacon.R;
import kgk.beacon.actions.Action;
import kgk.beacon.actions.ActionCreator;
import kgk.beacon.actions.HttpActions;
import kgk.beacon.dispatcher.Dispatcher;
import kgk.beacon.stores.DeviceStore;
import kgk.beacon.util.AppController;
import kgk.beacon.util.LastActionDateStorage;
import kgk.beacon.util.StartActivityEvent;
import kgk.beacon.view.DeviceListActivity;

public class VolleyHttpClient implements Response.ErrorListener {

    // TODO Move last action data save commands to successful response block

    private static final String TAG = VolleyHttpClient.class.getSimpleName();
    private static final String AUTHENTICATION_URL = "http://dev.trezub.ru/api2/beacon/authorize";
    private static final String DEVICE_LIST_URL = "http://dev.trezub.ru/api2/beacon/getdeviceslist";
    private static final String GET_LAST_STATE_URL = "http://dev.trezub.ru/api2/beacon/getdeviceinfo";

    private static VolleyHttpClient instance;
    private Context context;
    private Dispatcher dispatcher;
    private RequestQueue requestQueue;

    private String phpSessId;

    private VolleyHttpClient(Context context) {
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
        dispatcher = Dispatcher.getInstance(EventBus.getDefault());
        dispatcher.register(this);
        dispatcher.register(DeviceStore.getInstance(dispatcher));
    }

    public static synchronized VolleyHttpClient getInstance(Context context) {
        if (instance == null) {
            instance = new VolleyHttpClient(context);
        }

        return instance;
    }

    public void onEvent(Action action) {
        onAction(action);
    }

    public void onAction(Action action) {
        switch (action.getType()) {
            case HttpActions.AUTHENTICATION_REQUEST:
                String login = ((String) action.getData().get(ActionCreator.KEY_LOGIN));
                String password = ((String) action.getData().get(ActionCreator.KEY_PASSWORD));
                authenticationRequest(login, password);
                break;
            case HttpActions.QUERY_BEACON_REQUEST:
                queryBeaconRequest();
                break;
            case HttpActions.TOGGLE_SEARCH_MODE_REQUEST:
                boolean searchModeStatus = (boolean) action.getData().get(ActionCreator.KEY_SEARCH_MODE_STATUS);
                toggleSearchModeRequest(searchModeStatus);
                break;
            case HttpActions.SEND_SETTINGS_REQUEST:
                JSONObject settingsJson = (JSONObject) action.getData().get(ActionCreator.KEY_SETTINGS);
                settingsRequest(settingsJson);
                break;
            case HttpActions.GET_LAST_STATE_REQUEST:
                getLastStateRequest();
                break;
        }
    }

    private void authenticationRequest(String login, final String password) {
        final AuthenticationRequest request = new AuthenticationRequest(Request.Method.POST,
                AUTHENTICATION_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Response: " + response);

                        try {
                            JSONObject responseJson = new JSONObject(response);
                            processAuthenticationResponseJson(responseJson);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, "Can't parse authentication response");
                        }
                    }
                },
                this);

        request.setLogin(login);
        request.setPassword(password);

        requestQueue.add(request);
    }

    private void deviceListRequest(String phpSessId) {
        DeviceListRequest request = new DeviceListRequest(Request.Method.GET,
                DEVICE_LIST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject responseJson = new JSONObject(response);
                            processDeviceListResponseJson(responseJson);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, "Can't parse device list response");
                        }
                    }
                },
                this);

        request.setPhpSessId(phpSessId);
        requestQueue.add(request);
    }

    private void queryBeaconRequest() {
        LastActionDateStorage.getInstance().save(Calendar.getInstance().getTime());
        QueryBeaconRequest request = new QueryBeaconRequest(Request.Method.GET,
                QueryBeaconRequest.makeUrl(""),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(context, R.string.on_command_send_toast, Toast.LENGTH_SHORT).show();
                    }
                },
                this);

        request.setPhpSessId(phpSessId);
        requestQueue.add(request);
    }

    private void getLastStateRequest() {
        GetLastStateRequest request = new GetLastStateRequest(Request.Method.GET,
                GetLastStateRequest.makeUrl(GET_LAST_STATE_URL),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response);
                    }
                },
                this);

        request.setPhpSessId(phpSessId);
        requestQueue.add(request);
    }

    private void toggleSearchModeRequest(boolean searchModeStatus) {
        LastActionDateStorage.getInstance().save(Calendar.getInstance().getTime());
        ToggleSearchModeRequest request = new ToggleSearchModeRequest(Request.Method.GET,
                ToggleSearchModeRequest.makeUrl("", searchModeStatus),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(context, R.string.on_command_send_toast, Toast.LENGTH_SHORT).show();
                    }
                },
                this);

        request.setPhpSessId(phpSessId);
        requestQueue.add(request);
    }

    private void settingsRequest(JSONObject settingsJson) {
        LastActionDateStorage.getInstance().save(Calendar.getInstance().getTime());

        try {
            settingsJson.put("id", AppController.getInstance().getActiveDeviceId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SettingsRequest request = new SettingsRequest("",
                settingsJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(context, R.string.on_command_send_toast, Toast.LENGTH_SHORT).show();
                    }
                },
                this);

        request.setPhpSessId(phpSessId);
        requestQueue.add(request);
    }

    private void processAuthenticationResponseJson(JSONObject responseJson) throws JSONException {
        if (responseJson.getBoolean("status")) {
            String[] parsedCookie = ((String) responseJson.get("Cookie")).split(";");
            phpSessId = parsedCookie[0];
            deviceListRequest(phpSessId);
        } else {
            StartActivityEvent event = new StartActivityEvent(DeviceListActivity.class);
            event.setLoginSuccessful(false);
            EventBus.getDefault().post(event);
            Toast.makeText(context, context.getString(R.string.wrong_login_or_password_label),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void processDeviceListResponseJson(JSONObject responseJson) throws JSONException {
        if (responseJson.getBoolean("status")) {
            JSONArray devices = responseJson.getJSONArray("data");
            ActionCreator.getInstance(dispatcher).receiveDeviceListResponse(devices);
            StartActivityEvent event = new StartActivityEvent(DeviceListActivity.class);
            event.setLoginSuccessful(true);
            EventBus.getDefault().post(event);
        } else {
            Log.d(TAG, "Can't get device list");
            deviceListRequest(phpSessId);
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.d(TAG, "Request didn't work\n" + error.getMessage());
    }
}
