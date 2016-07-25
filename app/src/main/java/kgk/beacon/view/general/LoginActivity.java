package kgk.beacon.view.general;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import kgk.beacon.R;
import kgk.beacon.util.AppController;
import kgk.beacon.view.actis.SingleFragmentActivity;

/**
 * Экран авторизации
 */
public class LoginActivity extends SingleFragmentActivity {

    private static final String LANGUAGE_ENGLISH = "en";
    private static final String LANGUAGE_RUSSIAN = "ru";
    private static final String LANGUAGE_UKRAINIAN = "uk";
    private static final String KEY_LANGUAGE = "key_language";

    ////

    @Bind(R.id.helpToolbarButton) ImageButton helpToolbarButton;
    @Bind(R.id.toolbar_title) TextView toolbarTitle;

    ////

    @Override
    protected Fragment createFragment() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        prepareToolbar();
        String locale = AppController.loadStringValueFromSharedPreferences(KEY_LANGUAGE);
        if (!locale.equals("default")) {
            changeLocale(locale);
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater menuInflater = getMenuInflater();
//        menuInflater.inflate(R.menu.menu_login, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.menuLogin_languageSettings:
//                showLanguagePickerDialog();
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    protected void setNavigationButtonIcon() {
        // Skip navigation button in first app screen
    }

    private void prepareToolbar() {
        toolbar.setOverflowIcon(getResources().getDrawable(R.drawable.actis_login_menu_icon));
        helpToolbarButton.setVisibility(View.GONE);
        toolbarTitle.setText(getString(R.string.app_name));
    }

    private void showLanguagePickerDialog() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final RadioGroup dialogView = (RadioGroup) inflater.inflate(R.layout.dialog_language_picker, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.ActisAlertDialogStyle);
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

    /** Запомнить языковой выбор пользователя */
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

    /** Изменить локаль приложения */
    private void changeLocale(String localeIndex) {
        Locale locale = new Locale(localeIndex);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration,
                getBaseContext().getResources().getDisplayMetrics());
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        View v = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (v instanceof EditText) {
            View w = getCurrentFocus();
            int scrCoordinates[] = new int[2];
            w.getLocationOnScreen(scrCoordinates);
            float x = event.getRawX() + w.getLeft() - scrCoordinates[0];
            float y = event.getRawY() + w.getTop() - scrCoordinates[1];

            if (event.getAction() == MotionEvent.ACTION_UP && (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w.getBottom()) ) {
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        }
        return ret;
    }
}
