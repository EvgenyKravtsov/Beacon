package kgk.beacon.view.general;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import kgk.beacon.R;
import kgk.beacon.actions.ActionCreator;
import kgk.beacon.dispatcher.Dispatcher;
import kgk.beacon.util.AppController;
import kgk.beacon.view.general.event.StartActivityEvent;

public class LoginFragment extends Fragment {

    public static final String TAG = LoginFragment.class.getSimpleName();

    public static final String APPLICATION_PREFERENCES = "application-preferences";
    public static final String LOGIN_RETRIEVE_KEY = "Login";
    public static final String PASSWORD_RETRIEVE_KEY = "Password";
    public static final String REMEMBER_ME_OPTION_KEY = "Remember me";

    public static final String KEY_LOGIN = "login";
    public static final String KEY_PASSWORD = "password";

    private Dispatcher dispatcher;
    private ActionCreator actionCreator;
    private ProgressDialog loginProgressDialog;

    @Bind(R.id.fragmentLogin_loginField) EditText loginField;
    @Bind(R.id.fragmentLogin_passwordField) EditText passwordField;
    @Bind(R.id.fragmentLogin_rememberMeCheckBox) CheckBox rememberMeCheckBox;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);
        initFluxDependencies();
        loadLoginParametersFromSharedPreferences();

        return view;
    }

    private void initFluxDependencies() {
        dispatcher = Dispatcher.getInstance(EventBus.getDefault());
        dispatcher.register(this);
        actionCreator = ActionCreator.getInstance(dispatcher);
    }

    private void saveLoginParametersToSharedPreferences() {
        SharedPreferences loginAndPassword = getActivity().getSharedPreferences(APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = loginAndPassword.edit();

        editor.putBoolean(REMEMBER_ME_OPTION_KEY, rememberMeCheckBox.isChecked());
        if (rememberMeCheckBox.isChecked()) {
            editor.putString(LOGIN_RETRIEVE_KEY, loginField.getText().toString());
            editor.putString(PASSWORD_RETRIEVE_KEY, passwordField.getText().toString());
        }
        editor.apply();
    }

    private void loadLoginParametersFromSharedPreferences() {
        SharedPreferences loginAndPassword = getActivity().getSharedPreferences(APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        rememberMeCheckBox.setChecked(loginAndPassword.getBoolean(REMEMBER_ME_OPTION_KEY, false));

        if (rememberMeCheckBox.isChecked()) {
            if (loginAndPassword.contains(LOGIN_RETRIEVE_KEY) && loginAndPassword.contains(PASSWORD_RETRIEVE_KEY)) {
                loginField.setText(loginAndPassword.getString("Login", ""));
                passwordField.setText(loginAndPassword.getString("Password", ""));
            }
        }
    }

    @OnClick(R.id.fragmentLogin_loginButton)
    public void onPressLoginButton(View view) {
        if (!AppController.getInstance().isNetworkAvailable()) {
            Toast.makeText(getActivity(), getString(R.string.no_internet_connection_message), Toast.LENGTH_SHORT).show();
            return;
        }

        saveLoginParametersToSharedPreferences();
        actionCreator.sendAuthenticationRequest(loginField.getText().toString(),
                passwordField.getText().toString());
        AppController.getInstance().setDemoMode(false);
        showLoginProgressDialog();
    }

    @OnClick(R.id.fragmentLogin_demoButton)
    public void onPressDemoButton(View view) {
        if (!AppController.getInstance().isNetworkAvailable()) {
            Toast.makeText(getActivity(), getString(R.string.no_internet_connection_message), Toast.LENGTH_SHORT).show();
            return;
        }

        saveLoginParametersToSharedPreferences();
        actionCreator.sendAuthenticationRequest("321", "321");
        AppController.getInstance().setDemoMode(true);
        showLoginProgressDialog();
    }

    public void onEvent(StartActivityEvent event) {
        loginProgressDialog.dismiss();

        if (event.isLoginSuccessful()) {
            Intent startActivityIntent = new Intent(getActivity(), event.getActivityClass());
            startActivity(startActivityIntent);
        }
    }

    private void showLoginProgressDialog() {
        loginProgressDialog = new ProgressDialog(getActivity());
        loginProgressDialog.setTitle(getString(R.string.login_progress_dialog_title));
        loginProgressDialog.setMessage(getString(R.string.login_progress_dialog_message));
        loginProgressDialog.show();
    }
}
