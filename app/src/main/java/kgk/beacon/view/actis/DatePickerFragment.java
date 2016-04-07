package kgk.beacon.view.actis;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import kgk.beacon.R;
import kgk.beacon.actions.ActionCreator;
import kgk.beacon.dispatcher.Dispatcher;
import kgk.beacon.stores.ActisStore;
import kgk.beacon.util.DateFormatter;

public class DatePickerFragment extends Fragment {

    @Bind(R.id.fragmentDatePicker_fromDateButton) Button fromDateButton;
    @Bind(R.id.fragmentDatePicker_fromTimeButton) Button fromTimeButton;
    @Bind(R.id.fragmentDatePicker_toDateButton) Button toDateButton;
    @Bind(R.id.fragmentDatePicker_toTimeButton) Button toTimeButton;

    public static final String FROM_DATE_DIALOG = "fromDateDialog";
    public static final String FROM_TIME_DIALOG = "fromTimeDialog";
    public static final String TO_DATE_DIALOG = "toDateDialog";
    public static final String TO_TIME_DIALOG = "toTimeDialog";
    public static final String FROM_DATE = "fromDateResult";
    public static final String TO_DATE = "toDateResult";
    public static final int REQUEST_FROM_DATE = 0;
    public static final int REQUEST_FROM_TIME = 1;
    public static final int REQUEST_TO_DATE = 2;
    public static final int REQUEST_TO_TIME = 3;

    private Calendar fromDate;
    private Calendar toDate;

    private Dispatcher dispatcher;
    private ActionCreator actionCreator;
    private ActisStore actisStore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_date_picker, container, false);
        ButterKnife.bind(this, view);
        initFluxDependencies();

        fromDate = Calendar.getInstance();
        toDate = Calendar.getInstance();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case REQUEST_FROM_DATE:
                int[] fromDateValues = (int[]) data.getSerializableExtra(FromDatePickerDialog.EXTRA_FROM_DATE);
                fromDate.set(Calendar.YEAR, fromDateValues[0]);
                fromDate.set(Calendar.MONTH, fromDateValues[1]);
                fromDate.set(Calendar.DAY_OF_MONTH, fromDateValues[2]);
                fromDateButton.setText(DateFormatter.formatDate(fromDate.getTime()));

                if (fromTimeButton.getText().toString().equals(getString(R.string.time_button_label))) {
                    fromDate.set(Calendar.HOUR_OF_DAY, 0);
                    fromDate.set(Calendar.MINUTE, 0);
                    fromTimeButton.setText(DateFormatter.formatTime(fromDate.getTime()));
                }

                break;
            case REQUEST_FROM_TIME:
                int[] fromTimeValues = (int[]) data.getSerializableExtra(FromTimePickerDialog.EXTRA_FROM_TIME);
                fromDate.set(Calendar.HOUR_OF_DAY, fromTimeValues[0]);
                fromDate.set(Calendar.MINUTE, fromTimeValues[1]);
                fromTimeButton.setText(DateFormatter.formatTime(fromDate.getTime()));
                break;
            case REQUEST_TO_DATE:
                int[] toDateValues = (int[]) data.getSerializableExtra(ToDatePickerDialog.EXTRA_TO_DATE);
                toDate.set(Calendar.YEAR, toDateValues[0]);
                toDate.set(Calendar.MONTH, toDateValues[1]);
                toDate.set(Calendar.DAY_OF_MONTH, toDateValues[2]);
                toDateButton.setText(DateFormatter.formatDate(toDate.getTime()));

                if (toTimeButton.getText().toString().equals(getString(R.string.time_button_label))) {
                    toDate.set(Calendar.HOUR_OF_DAY, 23);
                    toDate.set(Calendar.MINUTE, 59);
                    toTimeButton.setText(DateFormatter.formatTime(toDate.getTime()));
                }

                break;
            case REQUEST_TO_TIME:
                int[] toTimeValues = (int[]) data.getSerializableExtra(ToTimePickerDialog.EXTRA_TO_TIME);
                toDate.set(Calendar.HOUR_OF_DAY, toTimeValues[0]);
                toDate.set(Calendar.MINUTE, toTimeValues[1]);
                toTimeButton.setText(DateFormatter.formatTime(toDate.getTime()));
                break;
        }
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void initFluxDependencies() {
        dispatcher = Dispatcher.getInstance(EventBus.getDefault());
        actionCreator = ActionCreator.getInstance(dispatcher);
        actisStore = ActisStore.getInstance(dispatcher);
    }

    @OnClick(R.id.fragmentDatePicker_fromDateButton)
    public void onPressFromDateButton(View view) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FromDatePickerDialog dialog = new FromDatePickerDialog();
        dialog.setTargetFragment(this, REQUEST_FROM_DATE);
        dialog.show(fragmentManager, FROM_DATE_DIALOG);
    }

    @OnClick(R.id.fragmentDatePicker_fromTimeButton)
    public void onPressFromTimeButton(View view) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FromTimePickerDialog dialog = new FromTimePickerDialog();
        dialog.setTargetFragment(this, REQUEST_FROM_TIME);
        dialog.show(fragmentManager, FROM_TIME_DIALOG);
    }

    @OnClick(R.id.fragmentDatePicker_toDateButton)
    public void onPressToDateButton(View view) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        ToDatePickerDialog dialog = new ToDatePickerDialog();
        dialog.setTargetFragment(this, REQUEST_TO_DATE);
        dialog.show(fragmentManager, TO_DATE_DIALOG);
    }

    @OnClick(R.id.fragmentDatePicker_toTimeButton)
    public void onPressToTimeButton(View view) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        ToTimePickerDialog dialog = new ToTimePickerDialog();
        dialog.setTargetFragment(this, REQUEST_TO_TIME);
        dialog.show(fragmentManager, TO_TIME_DIALOG);
    }

    @OnClick(R.id.fragmentDatePicker_okButton)
    public void onPressOkButton(View view) {

        if (fromDateButton.getText().equals(getString(R.string.date_button_label)) ||
                fromTimeButton.getText().equals(getString(R.string.time_button_label)) ||
                toDateButton.getText().equals(getString(R.string.date_button_label)) ||
                toTimeButton.getText().equals(getString(R.string.time_button_label))) {

            Toast.makeText(getActivity(), getString(R.string.not_all_fields_selected_message), Toast.LENGTH_LONG).show();
        } else if (fromDate.getTime().getTime() >= toDate.getTime().getTime()) {
            Toast.makeText(getActivity(), getString(R.string.wrong_period_message), Toast.LENGTH_LONG).show();
        } else {
            actionCreator.getSignalsByPeriod(fromDate.getTime(), toDate.getTime());
            getActivity().finish();
        }
    }
}
