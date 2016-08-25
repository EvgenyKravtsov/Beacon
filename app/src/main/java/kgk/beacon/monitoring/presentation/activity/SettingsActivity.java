package kgk.beacon.monitoring.presentation.activity;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.Map;

import kgk.beacon.R;
import kgk.beacon.monitoring.presentation.model.MapType;
import kgk.beacon.monitoring.presentation.presenter.SettingsViewPresenter;
import kgk.beacon.monitoring.presentation.view.SettingsView;

public class SettingsActivity extends AppCompatActivity implements SettingsView {

    // Views
    private FrameLayout backButton;
    private TextView actionBarTitleTextView;
    private RadioGroup mapRadioGroup;
    private CheckBox markerInformationCheckBox;

    private SettingsViewPresenter presenter;

    ////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monitoring_activity_settings);
        initViews();
        initListeners();
    }

    ////

    @Override
    protected void onStart() {
        super.onStart();
        bindPresenter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.requestSettingsData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindPresenter();
    }

    ////

    @Override
    public void showSettings(Map<String, Object> dataMap) {
        MapType defaultMapType = (MapType)
                dataMap.get(SettingsViewPresenter.DEFAULT_MAP_TYPE_KEY);
        boolean markerInformationEnabled =
                (boolean) dataMap.get(SettingsViewPresenter.MERKER_INFORMATION_ENABLED);

        switch (defaultMapType) {
            case KGK:
                mapRadioGroup.check(R.id.monitoring_activity_settings_marker_kgk_radio_button);
                break;
            case YANDEX:
                mapRadioGroup.check(R.id.monitoring_activity_settings_marker_yandex_radio_button);
                break;
            case GOOGLE:
                mapRadioGroup.check(R.id.monitoring_activity_settings_marker_google_radio_button);
                break;
            case SATELLITE:
                mapRadioGroup.check(R.id.monitoring_activity_settings_marker_satellite_radio_button);
                break;
        }

        markerInformationCheckBox.setChecked(markerInformationEnabled);
    }

    ////

    private void initViews() {
        backButton = (FrameLayout) findViewById(R.id.monitoring_action_bar_back_button);
        actionBarTitleTextView = (TextView) findViewById(R.id.monitoring_action_bar_title_text_view);
        actionBarTitleTextView.setText(R.string.monitoring_settings_screen_title);

        mapRadioGroup = (RadioGroup)
                findViewById(R.id.monitoring_activity_settings_marker_map_radio_group);
        markerInformationCheckBox = (CheckBox)
                findViewById(R.id.monitoring_activity_settings_marker_information_checkbox);
    }

    private void initListeners() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackButtonClick();
            }
        });

        mapRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                onMapRadioGroupItemChecked(checkedId);
            }
        });

        markerInformationCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onMarkerInformationCheckBoxCheckedChanged(isChecked);
            }
        });
    }

    private void bindPresenter() {
        presenter = new SettingsViewPresenter(this);
    }

    private void unbindPresenter() {
        if (presenter != null) presenter.unbindView();
        presenter = null;
    }

    //// Control callbacks

    private void onBackButtonClick() {
        NavUtils.navigateUpFromSameTask(this);
    }

    private void onMapRadioGroupItemChecked(int checkedId) {
        switch (checkedId) {
            case R.id.monitoring_activity_settings_marker_kgk_radio_button:
                presenter.saveDefaultMapType(MapType.KGK);
                break;
            case R.id.monitoring_activity_settings_marker_yandex_radio_button:
                presenter.saveDefaultMapType(MapType.YANDEX);
                break;
            case R.id.monitoring_activity_settings_marker_google_radio_button:
                presenter.saveDefaultMapType(MapType.GOOGLE);
                break;
            case R.id.monitoring_activity_settings_marker_satellite_radio_button:
                presenter.saveDefaultMapType(MapType.SATELLITE);
                break;
        }
    }

    private void onMarkerInformationCheckBoxCheckedChanged(boolean isChecked) {
        presenter.saveMarkerInformationEnabled(isChecked);
    }
}
