package kgk.beacon.view;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RadioGroup;

import java.io.File;
import java.util.Locale;

import de.greenrobot.event.EventBus;
import kgk.beacon.R;
import kgk.beacon.util.AppController;
import kgk.beacon.util.DownloadUrlReceivedEvent;
import kgk.beacon.util.Updater;

public class LoginActivity extends SingleFragmentActivity {

    private static final String LANGUAGE_ENGLISH = "en";
    private static final String LANGUAGE_RUSSIAN = "ru";
    private static final String LANGUAGE_UKRAINIAN = "uk";
    private static final String KEY_LANGUAGE = "key_language";

    private long enqueue;
    private DownloadManager downloadManager;
    private String apkNameForUpdate;

    @Override
    protected Fragment createFragment() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String locale = AppController.loadStringValueFromSharedPreferences(KEY_LANGUAGE);
        if (!locale.equals("default")) {
            changeLocale(locale);
        }

        EventBus.getDefault().register(this);
//        registerBroadcastReceiverForUpdating();
//        if (AppController.getInstance().isNetworkAvailable()) {
//            Updater.getInstance(this).update();
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_login, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuLogin_languageSettings:
                showLanguagePickerDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void registerBroadcastReceiverForUpdating() {
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(enqueue);
                    Cursor cursor = downloadManager.query(query);

                    if (cursor.moveToFirst()) {
                        int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);

                        if (cursor.getInt(columnIndex) == DownloadManager.STATUS_SUCCESSFUL) {
                            Intent installIntent = new Intent(Intent.ACTION_VIEW);
                            installIntent.setDataAndType(Uri.fromFile(new File(
                                    Environment.getExternalStorageDirectory() + "/download/" + apkNameForUpdate)), "application/vnd.android.package-archive");
                            installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(installIntent);
                        }
                    }
                }
            }
        };

        registerReceiver(broadcastReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private void showLanguagePickerDialog() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final RadioGroup dialogView = (RadioGroup) inflater.inflate(R.layout.dialog_language_picker, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.language_picker_dialog_title))
                .setView(dialogView)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int languageId = dialogView.getCheckedRadioButtonId();
                        switchLanguage(languageId);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void switchLanguage(int languageId) {
        switch (languageId) {
            case R.id.dialogLanguagePicker_englishButton:
                changeLocale(LANGUAGE_ENGLISH);
                AppController.saveStringValueToSharedPreferences(KEY_LANGUAGE, LANGUAGE_ENGLISH);
                break;
            case R.id.dialogLanguagePicker_russianButton:
                changeLocale(LANGUAGE_RUSSIAN);
                AppController.saveStringValueToSharedPreferences(KEY_LANGUAGE, LANGUAGE_RUSSIAN);
                break;
            case R.id.dialogLanguagePicker_ukrainianButton:
                changeLocale(LANGUAGE_UKRAINIAN);
                AppController.saveStringValueToSharedPreferences(KEY_LANGUAGE, LANGUAGE_UKRAINIAN);
        }

        finish();
        startActivity(getIntent());
    }

    private void changeLocale(String localeIndex) {
        Locale locale = new Locale(localeIndex);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration,
                getBaseContext().getResources().getDisplayMetrics());
    }

    public void onEvent(final DownloadUrlReceivedEvent event) {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder
                .setTitle(getString(R.string.updating_label))
                .setMessage(getString(R.string.new_version_available_label))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.download_label), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startDownload(event.getDownloadUrl());
                    }
                });
        android.app.AlertDialog dialog = alertDialogBuilder.create();
        dialog.show();
    }

    private void startDownload(String url) {
        downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

        apkNameForUpdate = Updater.getApkName(url);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, apkNameForUpdate);
        enqueue = downloadManager.enqueue(request);
    }
}
