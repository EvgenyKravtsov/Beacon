package kgk.beacon.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import de.greenrobot.event.EventBus;

public class Updater {

    public static final String TAG = Updater.class.getSimpleName();
    public static final String SERVER_URL = "http://monitor.kgk-global.com/api/android_api.php";

    private static Updater updater;

    private Context context;

    private Updater(Context context) {
        this.context = context;
    }

    public synchronized static Updater getInstance(Context context) {
        if (updater == null) {
            updater = new Updater(context);
        }
        return updater;
    }

    public void update() {
        new UpdateRequest(makeRequestUrl()).execute();
    }

    public static String getApkName(String url) {
        String[] parts = url.split("/");
        return parts[parts.length - 1];
    }

    private String makeRequestUrl() {
        String result = SERVER_URL +
                "?" +
                "name" +
                "=" +
                getApplicationName() +
                "&" +
                "code" +
                "=" +
                getVersionCode();
        Log.d(TAG, result);
        return result;
    }

    private int getVersionCode() {
        PackageManager packageManager = context.getPackageManager();

        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return 0;
        }
    }

    private String getApplicationName() {
        PackageManager packageManager = context.getPackageManager();

        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.packageName;
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }

    private InputStream openHttpConnection(String requestUrl) throws IOException {
        InputStream inputStream = null;
        int response = -1;

        URL url = new URL(requestUrl);
        URLConnection connection = url.openConnection();

        if (!(connection instanceof HttpURLConnection)) {
            throw new IOException("Not a HTTP connection");
        }

        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            response = httpURLConnection.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                inputStream = httpURLConnection.getInputStream();
            }
        } catch (Exception e) {
            throw new IOException("Error connecting");
        }

        return inputStream;
    }

    private String downloadAnswer(String requestUrl) {
        try {
            return readInputStream(openHttpConnection(requestUrl), 2000);
        } catch (Exception e) {
            return makeRequestUrl();
        }
    }

    private String readInputStream(InputStream stream, int length) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> jsonMap = mapper.readValue(stream, Map.class);

        for (Map.Entry<String, Object> entry : jsonMap.entrySet()) {
            Log.d("JSON PARSING", entry.getKey() + " | " + entry.getValue());
        }

        return (String) jsonMap.get("response");
    }

    private class UpdateRequest extends AsyncTask<String, Void, String> {

        private String requestUrl;

        public UpdateRequest(String requestUrl) {
            this.requestUrl = requestUrl;
        }

        @Override
        protected String doInBackground(String... urls) {
            return downloadAnswer(requestUrl);
        }

        @Override
        protected void onPostExecute(String result) {
            switch (result) {
                case "error":
                    break;
                case "last version":
                    break;
                default:
                    EventBus.getDefault().post(new DownloadUrlReceivedEvent(result));
                    break;
            }
        }
    }
}
