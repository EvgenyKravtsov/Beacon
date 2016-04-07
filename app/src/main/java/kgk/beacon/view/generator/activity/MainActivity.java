package kgk.beacon.view.generator.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import kgk.beacon.R;
import kgk.beacon.view.generator.presenter.MainViewPresenter;

public class MainActivity extends AppCompatActivity implements MainView {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Bind(R.id.activityGeneratorMain_toolbar) Toolbar toolbar;
    @Bind(R.id.toolbarGeneratorMain_historyButtonIcon) ImageView historyButtonIcon;
    @Bind(R.id.activityGeneratorMain_manualButtonLayout) LinearLayout manualButtonLayout;
    @Bind(R.id.activityGeneratorMain_manualButtonIcon) ImageView manualButtonIcon;
    @Bind(R.id.activityGeneratorMain_autoButtonLayout) LinearLayout autoButtonLayout;
    @Bind(R.id.activityGeneratorMain_autoButtonIcon) ImageView autoButtonIcon;
    @Bind(R.id.activityGeneratorMain_switchButton) ImageButton switchButton;

    private boolean isSwitchButtonOn;
    private boolean isEmergencyStopButtonOn;
    private boolean isManualButtonOn;
    private boolean isAutoButtonOn;

    private MainViewPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generator_main);
        presenter = new MainViewPresenter(this);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onDestroy() {
        presenter = null;
        super.onDestroy();
    }

    private void turnManualButtonOn() {
        manualButtonLayout.setBackgroundResource(R.drawable.generator_main_buttons_frame_green);
        manualButtonIcon.setImageResource(R.drawable.generator_list_image_manual_on);
    }

    private void turnManualButtonOff() {
        manualButtonLayout.setBackgroundResource(R.drawable.generator_main_buttons_frame_grey);
        manualButtonIcon.setImageResource(R.drawable.generator_list_image_manual_off);
    }

    private void turnAutoButtonOn() {
        autoButtonLayout.setBackgroundResource(R.drawable.generator_main_buttons_frame_green);
        autoButtonIcon.setImageResource(R.drawable.generator_list_image_auto_on);
    }

    private void turnAutoButtonOff() {
        autoButtonLayout.setBackgroundResource(R.drawable.generator_main_buttons_frame_grey);
        autoButtonIcon.setImageResource(R.drawable.generator_list_image_auto_off);
    }

    //// Buttons

    @OnTouch(R.id.activityGeneratorMainToolbar_historyButton)
    public boolean onTouchHistoryButton(View view, MotionEvent event) {
        Button historyButton = (Button) view;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "Button down");
                historyButton.setTextColor(Color.rgb(0x09, 0xd6, 0x92));
                historyButtonIcon
                        .setBackgroundResource(R.drawable.generator_history_button_image_green);
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "Button up");
                historyButton.setTextColor(Color.rgb(0xff, 0xff, 0xff));
                historyButtonIcon
                        .setBackgroundResource(R.drawable.generator_history_button_image);
                Intent goToHistoryActivity = new Intent(this, HistoryActivity.class);
                startActivity(goToHistoryActivity);
                break;
        }


        return true;
    }

    @OnClick(R.id.activityGeneratorMain_manualButtonLayout)
    public void onClickManualButtonLayout(View view) {
        if (!isManualButtonOn) {
            turnManualButtonOn();
            isManualButtonOn = true;
            turnAutoButtonOff();
            isAutoButtonOn = false;
            presenter.sendManualModeCommand();
        }
    }

    @OnClick(R.id.activityGeneratorMain_manualButton)
    public void onClickManualButton(View view) {
        onClickManualButtonLayout(view);
    }

    @OnClick(R.id.activityGeneratorMain_autoButtonLayout)
    public void onClickAutoButtonLayout(View view) {
        if (!isAutoButtonOn) {
            turnAutoButtonOn();
            isAutoButtonOn = true;
            turnManualButtonOff();
            isManualButtonOn = false;
            presenter.sendAutoModeCommand();
        }
    }

    @OnClick(R.id.activityGeneratorMain_autoButton)
    public void onClickAutoButton(View view) {
        onClickAutoButtonLayout(view);
    }

    @OnClick(R.id.activityGeneratorMain_emergencyButton)
    public void onClickEmergencyButton(View view) {
        Button emergencyStopButton = (Button) view;

        if (isEmergencyStopButtonOn) {
            emergencyStopButton.setBackgroundResource(R.drawable.generator_emergency_stop_button_off);
            isEmergencyStopButtonOn = false;
        } else {
            emergencyStopButton.setBackgroundResource(R.drawable.generator_emergency_stop_button_on);
            presenter.sendEmergencyStopCommand();
            isEmergencyStopButtonOn = true;
        }
    }

    @OnClick(R.id.activityGeneratorMain_trueSwitchButton)
    public void onClickSwitchButton(View view) {
        if (isSwitchButtonOn) {
            switchButton.setImageResource(R.drawable.generator_switch_button_off);
            presenter.sendSwitchOffCommand();
            isSwitchButtonOn = false;
        } else {
            switchButton.setImageResource(R.drawable.generator_switch_button_on);
            presenter.sendSwitchOnCommand();
            isSwitchButtonOn = true;
        }
    }
}
