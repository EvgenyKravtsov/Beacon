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
import kgk.beacon.database.SignalDatabaseDao;
import kgk.beacon.dispatcher.Dispatcher;
import kgk.beacon.model.Signal;
import kgk.beacon.stores.DeviceStore;
import kgk.beacon.util.AppController;
import kgk.beacon.util.DownloadDataInProgressEvent;
import kgk.beacon.util.LastActionDateStorage;
import kgk.beacon.util.StartActivityEvent;
import kgk.beacon.util.ToggleSearchModeEvent;
import kgk.beacon.view.DeviceListActivity;

public class VolleyHttpClient implements Response.ErrorListener {

    private static final String TAG = VolleyHttpClient.class.getSimpleName();
    private static final String AUTHENTICATION_URL = "http://dev.trezub.ru/api2/beacon/authorize";
    private static final String DEVICE_LIST_URL = "http://dev.trezub.ru/api2/beacon/getdeviceslist";
    private static final String GET_LAST_STATE_URL = "http://dev.trezub.ru/api2/beacon/getdeviceinfo";
    private static final String GET_LAST_SIGNALS_URL = "http://dev.trezub.ru/api2/beacon/getpackets";
    private static final String QUERY_BEACON_REQUEST_URL = "http://dev.trezub.ru/api2/beacon/cmdrequestinfo";
    private static final String TOGGLE_SEARCH_MODE_REQUEST_URL = "http://dev.trezub.ru/api2/beacon/cmdtogglefind";
    private static final String SETTINGS_REQUEST_URL = "http://dev.trezub.ru/api2/beacon/cmdsetsettings";

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
            case HttpActions.GET_LAST_SIGNALS_REQUEST:
                long fromDate = (long) action.getData().get(ActionCreator.KEY_FROM_DATE);
                long toDate = (long) action.getData().get(ActionCreator.KEY_TO_DATE);
                getLastSignalsRequest(fromDate, toDate);
                break;
        }
    }

    private void authenticationRequest(String login, final String password) {
        final AuthenticationRequest request = new AuthenticationRequest(Request.Method.POST,
                AUTHENTICATION_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject responseJson = new JSONObject(response);
                            processAuthenticationResponseJson(responseJson);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        StartActivityEvent event = new StartActivityEvent(DeviceListActivity.class);
                        event.setLoginSuccessful(false);
                        EventBus.getDefault().post(event);
                        Toast.makeText(context, context.getString(R.string.on_command_send_error_toast),
                                Toast.LENGTH_SHORT).show();
                    }
                });

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
                QueryBeaconRequest.makeUrl(QUERY_BEACON_REQUEST_URL),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject responseJson = new JSONObject(response);
                            if (responseJson.getBoolean("status")) {
                                Toast.makeText(context, R.string.on_command_send_toast, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, R.string.on_command_send_error_toast, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
                        processLastStateResponse(response);
                    }
                },
                this);

        request.setPhpSessId(phpSessId);
        requestQueue.add(request);
    }

    private void toggleSearchModeRequest(boolean searchModeStatus) {
        LastActionDateStorage.getInstance().save(Calendar.getInstance().getTime());
        ToggleSearchModeRequest request = new ToggleSearchModeRequest(Request.Method.GET,
                ToggleSearchModeRequest.makeUrl(TOGGLE_SEARCH_MODE_REQUEST_URL, searchModeStatus),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject responseJson = new JSONObject(response);
                            if (responseJson.getBoolean("status")) {
                                Toast.makeText(context, R.string.on_command_send_toast, Toast.LENGTH_SHORT).show();
                                EventBus.getDefault().post(new ToggleSearchModeEvent());
                            } else {
                                Toast.makeText(context, R.string.on_command_send_error_toast, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

        SettingsRequest request = new SettingsRequest(SETTINGS_REQUEST_URL,
                settingsJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // TODO Delete test code
                        Log.d(TAG, response.toString());
                        try {
                            if (response.getBoolean("status")) {
                                Toast.makeText(context, R.string.on_command_send_toast, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, R.string.on_command_send_error_toast, Toast.LENGTH_SHORT).show();
                                Log.d(TAG, response.toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                this);

        request.setPhpSessId(phpSessId);
        requestQueue.add(request);
    }

    private void getLastSignalsRequest(long fromDate, long toDate) {
        if (!AppController.getInstance().isNetworkAvailable()) {
            DownloadDataInProgressEvent event = new DownloadDataInProgressEvent();
            event.setStatus(DownloadDataStatus.noInternetConnection);
            EventBus.getDefault().post(event);
            return;
        }

        GetLastSignalsRequest request = new GetLastSignalsRequest(Request.Method.POST,
                GetLastSignalsRequest.makeUrl(GET_LAST_SIGNALS_URL, fromDate, toDate),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        processLastSignalsResponse(response);
                    }
                },
                this);

        request.setPhpSessId(phpSessId);
        requestQueue.add(request);
        // TODO Delete test code
        Log.d(TAG, request.toString());

        DownloadDataInProgressEvent event = new DownloadDataInProgressEvent();
        event.setStatus(DownloadDataStatus.Started);
        EventBus.getDefault().post(event);
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
        // TODO Delete test code
        Log.d(TAG, responseJson.toString());

        if (responseJson.getBoolean("status")) {
            try {
                JSONArray devices = responseJson.getJSONArray("data");
                ActionCreator.getInstance(dispatcher).receiveDeviceListResponse(devices);
                StartActivityEvent event = new StartActivityEvent(DeviceListActivity.class);
                event.setLoginSuccessful(true);
                EventBus.getDefault().post(event);
            } catch (JSONException e) {
                Toast.makeText(AppController.getInstance().getApplicationContext(), R.string.no_registered_devices_toast, Toast.LENGTH_LONG).show();
                StartActivityEvent event = new StartActivityEvent(DeviceListActivity.class);
                event.setLoginSuccessful(false);
                EventBus.getDefault().post(event);
            }
        } else {
            Toast.makeText(AppController.getInstance().getApplicationContext(), R.string.server_error, Toast.LENGTH_LONG).show();
            StartActivityEvent event = new StartActivityEvent(DeviceListActivity.class);
            event.setLoginSuccessful(false);
            EventBus.getDefault().post(event);
        }
    }

    private void processLastSignalsResponse(String response) {
        Log.d(TAG, response);

        DownloadDataInProgressEvent event = new DownloadDataInProgressEvent();
        try {
            JSONObject responseJson = new JSONObject(response);
            if (responseJson.getBoolean("status")) {
                JSONArray responseDataJson = responseJson.getJSONArray("data");

                for (int i = 0; i < responseDataJson.length(); i++) {
                    JSONObject signalJson = responseDataJson.getJSONObject(i);
                    Signal signal = Signal.signalFromJson(signalJson);
                    SignalDatabaseDao.getInstance(AppController.getInstance()).insertSignal(signal);
                }

                event.setStatus(DownloadDataStatus.Success);
                EventBus.getDefault().post(event);
            } else {
                event.setStatus(DownloadDataStatus.Error);
                EventBus.getDefault().post(event);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void processLastStateResponse(String response) {
        try {
            JSONObject responseJson = new JSONObject(response);
            if (responseJson.getBoolean("status")) {
                JSONObject signalJson = responseJson.getJSONObject("data");
                Signal signal = Signal.signalFromJsonForLastState(signalJson);
                SignalDatabaseDao.getInstance(AppController.getInstance()).insertSignal(signal);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(context, R.string.on_command_send_error_toast, Toast.LENGTH_SHORT).show();
    }
}
