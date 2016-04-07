package kgk.beacon.view.actis;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.OnTouch;
import de.greenrobot.event.EventBus;
import kgk.beacon.R;
import kgk.beacon.actions.ActionCreator;
import kgk.beacon.actions.event.ToggleSearchModeEvent;
import kgk.beacon.database.ActisDatabaseDao;
import kgk.beacon.dispatcher.Dispatcher;
import kgk.beacon.networking.event.SearchModeStatusEvent;
import kgk.beacon.stores.ActisStore;
import kgk.beacon.util.AppController;

public class InformationFragment extends Fragment implements DialogInterface.OnClickListener,
        CompoundButton.OnCheckedChangeListener {

    // TODO Add periodic unlock for query command (like with Search command)
    // TODO Update help desription

    public static final String TAG = InformationFragment.class.getSimpleName();

    public static final String KEY_QUERY_CONTROL_DATE = "key_query_control_date";
    public static final String KEY_QUERY_EXPIRE_DATE = "key_query_expire_date";
    public static final String KEY_SEARCH_EXPIRE_DATE = "key_search_expire_date";
    public static final long COMMAND_EXPIRATION_PERIOD = 24 * 60 * 60; // 24 hours in seconds;

    private static final String KEY_SEARCH_SWITCH = "key_search_switch";
    private static final String KEY_SEARCH_MODE_AWAITING = "key_search_mode_awaiting";

    @Bind(R.id.informationFragment_searchButton) RelativeLayout searchButtonFrame;
    @Bind(R.id.switchCustomSearch) SwitchCompat switchCustomSearch;
    @Bind(R.id.searchButtonTextView) TextView searchButtonTextView;

    private Dispatcher dispatcher;
    private ActionCreator actionCreator;
    private ActisStore actisStore;

    private boolean searchSwitch;
    private boolean searchSwitchActivated;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_information, container, false);
        ButterKnife.bind(this, view);
        initFluxDependencies();
        setupSearchButton();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        dispatcher.register(this);
        ActionCreator.getInstance(Dispatcher.getInstance(EventBus.getDefault())).getLastSignalDateFromDatabase();
        actionCreator.sendGetSettingsRequest();
    }

    @Override
    public void onPause() {
        super.onPause();
        dispatcher.unregister(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    ////

    public void onEventMainThread(ToggleSearchModeEvent event) {
        if (!event.getResult()) {
            changeStateSearchButton(!switchCustomSearch.isChecked());
        } else {
            long deviceId = AppController.getInstance().getActiveDeviceId();
            AppController.saveBooleanValueToSharedPreferences(deviceId + KEY_SEARCH_MODE_AWAITING, true);
            AppController.saveLongValueToSharedPreferences(deviceId + KEY_SEARCH_EXPIRE_DATE,
                    Calendar.getInstance().getTimeInMillis() / 1000 + COMMAND_EXPIRATION_PERIOD);
            AppController.saveBooleanValueToSharedPreferences(deviceId + KEY_SEARCH_SWITCH, switchCustomSearch.isChecked());
        }
    }

    public void onEventMainThread(SearchModeStatusEvent event) {
        long deviceId = AppController.getInstance().getActiveDeviceId();

        if (Calendar.getInstance().getTimeInMillis() / 1000 >
                AppController.loadLongValueFromSharedPreferences(deviceId + KEY_SEARCH_EXPIRE_DATE)) {
            AppController.saveBooleanValueToSharedPreferences(deviceId + KEY_SEARCH_MODE_AWAITING, false);
        }

        if (!AppController.loadBooleanValueFromSharedPreferences(deviceId + KEY_SEARCH_MODE_AWAITING)) {
            changeStateSearchButton(event.getStatus());
            return;
        }

        if (event.getStatus() == switchCustomSearch.isChecked()) {
            changeStateSearchButton(event.getStatus());
            AppController.saveBooleanValueToSharedPreferences(deviceId + KEY_SEARCH_MODE_AWAITING, false);
        }
    }

    ////

    private void initFluxDependencies() {
        dispatcher = Dispatcher.getInstance(EventBus.getDefault());
        actionCreator = ActionCreator.getInstance(dispatcher);
        actisStore = ActisStore.getInstance(dispatcher);
    }

    private void showDemoWarningDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.ActisAlertDialogStyle);
        builder.setTitle(getString(R.string.development_progress_dialog_title))
                .setMessage(getString(R.string.demo_warning_dialog_message))
                .setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showQueryTooEarlyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.ActisAlertDialogStyle);
        builder.setTitle(getString(R.string.development_progress_dialog_title))
                .setMessage(getString(R.string.query_too_early_dialog_message))
                .setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setupSearchButton() {
        switchCustomSearch.setOnCheckedChangeListener(this);
        long deviceId = AppController.getInstance().getActiveDeviceId();
        if (Calendar.getInstance().getTimeInMillis() / 1000 >
                AppController.loadLongValueFromSharedPreferences(deviceId + KEY_SEARCH_EXPIRE_DATE)) {
            AppController.saveBooleanValueToSharedPreferences(deviceId + KEY_SEARCH_MODE_AWAITING, false);
        }
        if (AppController.loadBooleanValueFromSharedPreferences(deviceId + KEY_SEARCH_MODE_AWAITING)) {
            changeStateSearchButton(
                    AppController.loadBooleanValueFromSharedPreferences(deviceId + KEY_SEARCH_SWITCH));
            searchButtonFrame.setBackgroundDrawable(getResources()
                    .getDrawable(R.drawable.actis_search_button_frame_yellow));
            switchCustomSearch.setThumbDrawable(getResources()
                    .getDrawable(R.drawable.search_custom_switch_yellow_thumb));
        }
    }

    private void changeStateSearchButton(boolean state) {
        switchCustomSearch.setOnCheckedChangeListener(null);
        switchCustomSearch.setChecked(state);
        switchCustomSearch.setThumbDrawable(getResources()
                .getDrawable(state ?
                        R.drawable.search_custom_switch_green_thumb :
                        R.drawable.search_custom_switch_grey_thumb));
        searchButtonFrame.setBackgroundDrawable(getResources()
                .getDrawable(state ?
                        R.drawable.actis_search_button_frame_green :
                        R.drawable.actis_menu_button_background));
        switchCustomSearch.setOnCheckedChangeListener(this);
    }

    ////

    @OnClick(R.id.fragmentInformation_historyButton)
    public void onPressHistoryButton(View view) {
        actionCreator.getLastSignalsByDeviceIdFromDatabase(HistoryFragment.DEFAULT_NUMBER_OF_SIGNALS);

        Intent intent = new Intent(getActivity(), HistoryActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.informationFragment_searchButton)
    public void onClickSearchButton(View view) {
        if (AppController.getInstance().isDemoMode()) {
            showDemoWarningDialog();
            return;
        }

        long deviceId = AppController.getInstance().getActiveDeviceId();

        if (AppController.loadLongValueFromSharedPreferences(deviceId + KEY_QUERY_CONTROL_DATE)
                == ActisDatabaseDao.getInstance(getActivity()).getLastSignalDate()) {
            if (Calendar.getInstance().getTimeInMillis() / 1000 <
                    AppController.loadLongValueFromSharedPreferences(deviceId + KEY_QUERY_EXPIRE_DATE)) {
                showQueryTooEarlyDialog();
                return;
            }
        }

        actionCreator.sendQueryBeaconRequest();
    }

    @OnLongClick(R.id.informationFragment_searchButton)
    public boolean onLongClickSearchButton(View view) {
        if (AppController.getInstance().isDemoMode()) {
            showDemoWarningDialog();
            return false;
        }

        Vibrator vibe = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE) ;
        vibe.vibrate(50);

        if (AppController.loadBooleanValueFromSharedPreferences(
                AppController.getInstance().getActiveDeviceId() + KEY_SEARCH_MODE_AWAITING)) {
            showQueryTooEarlyDialog();
            return false;
        }

        switchCustomSearch.setClickable(true);
        searchSwitchActivated = true;

        return true;
    }

    @OnTouch(R.id.informationFragment_searchButton)
    public boolean onTouchSearchButton(MotionEvent event) {
        if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
            searchButtonTextView.setTextColor(getResources().getColor(R.color.actis_app_green_accent));
        } else {
            searchButtonTextView.setTextColor(getResources().getColor(R.color.main_brand_black));
        }

        if (AppController.getInstance().isDemoMode() ||
                AppController.loadBooleanValueFromSharedPreferences(
                        AppController.getInstance().getActiveDeviceId() + KEY_SEARCH_MODE_AWAITING)) {
            return false;
        }

        switchCustomSearch.onTouchEvent(event);
        if (searchSwitchActivated) {
            if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_UP) {
                switchCustomSearch.setClickable(false);
                searchSwitchActivated = false;
            }
        }
        return false;
    }

    ////

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (searchSwitch) {
            actionCreator.sendToggleSearchModeRequest(false);
        } else {
            actionCreator.sendToggleSearchModeRequest(true);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        actionCreator.sendToggleSearchModeRequest(isChecked);

        searchButtonFrame.setBackgroundDrawable(getResources()
                .getDrawable(R.drawable.actis_search_button_frame_yellow));
        switchCustomSearch.setThumbDrawable(getResources()
                .getDrawable(R.drawable.search_custom_switch_yellow_thumb));
    }
}