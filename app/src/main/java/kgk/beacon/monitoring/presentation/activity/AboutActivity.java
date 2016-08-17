package kgk.beacon.monitoring.presentation.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import kgk.beacon.R;

public class AboutActivity extends AppCompatActivity {

    // Views
    private FrameLayout backButton;
    private TextView actionBarTitleTextView;
    private TextView versionTextView;

    ////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monitoring_activity_about);
        initViews();
        initListeners();
    }

    ////

    private void initViews() {
        backButton = (FrameLayout) findViewById(R.id.monitoring_action_bar_back_button);
        actionBarTitleTextView = (TextView) findViewById(R.id.monitoring_action_bar_title_text_view);
        actionBarTitleTextView.setText("About");

        versionTextView = (TextView) findViewById(R.id.monitoring_activity_about_version_text_view);
        versionTextView.setText(getAppVersion());
    }

    private void initListeners() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackButtonClick();
            }
        });
    }

    private String getAppVersion() {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return "----";
    }

    //// Control callbacks

    private void onBackButtonClick() {
        NavUtils.navigateUpFromSameTask(this);
    }
}
