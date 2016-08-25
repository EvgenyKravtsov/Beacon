package kgk.beacon.monitoring.presentation.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.Locale;

import kgk.beacon.R;
import kgk.beacon.monitoring.domain.model.User;
import kgk.beacon.monitoring.presentation.presenter.ProfileViewPresenter;
import kgk.beacon.monitoring.presentation.view.ProfileView;
import kgk.beacon.view.general.LoginActivity;

public class ProfileActivity extends AppCompatActivity implements ProfileView {

    // Views
    private FrameLayout backButton;
    private TextView actionBarTitleTextView;
    private TextView loginTextView;
    private TextView contactsTextView;
    private TextView balanceTextView;
    private Button signOutButton;

    private ProfileViewPresenter presenter;

    ////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monitoring_activity_profile);
        initViews();
        initListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindPresenter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.requestUser();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindPresenter();
    }

    ////

    @Override
    public void showUser(User user) {
        loginTextView.setText(user.getLogin());

        // TODO Delete test code
        contactsTextView.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam " +
                "tincidunt nisl at leo facilisis euismod. Maecenas bibendum aliquam scelerisque. " +
                "Cras pharetra eros a aliquam volutpat. Ut nec suscipit augue, vitae ullamcorper mi.");
        //contactsTextView.setText(user.getContacts());
        balanceTextView.setText(String.format(Locale.ROOT,
                "%.2f %s",
                user.getBalance(),
                getString(R.string.monitoring_profile_screen_rub)));
    }

    ////

    private void initViews() {
        backButton = (FrameLayout) findViewById(R.id.monitoring_action_bar_back_button);
        actionBarTitleTextView = (TextView) findViewById(R.id.monitoring_action_bar_title_text_view);
        actionBarTitleTextView.setText(R.string.monitoring_profile_screen_title);

        loginTextView = (TextView)
                findViewById(R.id.monitoring_activity_profile_login_text_view);
        contactsTextView = (TextView)
                findViewById(R.id.monitoring_activity_profile_contacts_text_view);
        balanceTextView = (TextView)
                findViewById(R.id.monitoring_activity_profile_balance_text_view);
        signOutButton = (Button)
                findViewById(R.id.monitoring_activity_profile_sign_out_button);
    }

    private void initListeners() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackButtonClick();
            }
        });
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignOutButtonClicked();
            }
        });
    }

    private void bindPresenter() {
        presenter = new ProfileViewPresenter(this);
    }

    private void unbindPresenter() {
        if (presenter != null) presenter.unbindView();
        presenter = null;
    }

    //// Control callbacks

    private void onBackButtonClick() {
        NavUtils.navigateUpFromSameTask(this);
    }

    private void onSignOutButtonClicked() {
        Intent intent = new Intent(this, LoginActivity.class);
        ComponentName componentName = intent.getComponent();
        Intent mainIntent = IntentCompat.makeRestartActivityTask(componentName);
        startActivity(mainIntent);
    }
}
