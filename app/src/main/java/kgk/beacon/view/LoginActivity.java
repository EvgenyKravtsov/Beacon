package kgk.beacon.view;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RadioGroup;

import java.util.Locale;

import kgk.beacon.R;
import kgk.beacon.util.AppController;

public class LoginActivity extends SingleFragmentActivity {

    private static final String LANGUAGE_ENGLISH = "en";
    private static final String LANGUAGE_RUSSIAN = "ru";
    private static final String LANGUAGE_UKRAINIAN = "uk";
    private static final String KEY_LANGUAGE = "key_language";

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
}
