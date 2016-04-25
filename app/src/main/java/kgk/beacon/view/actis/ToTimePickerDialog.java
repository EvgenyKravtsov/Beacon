package kgk.beacon.view.actis;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.TimePicker;

import butterknife.Bind;
import butterknife.ButterKnife;
import kgk.beacon.R;

/**
 * Диалог выбора времени
 */
public class ToTimePickerDialog extends DialogFragment {

    public static final String EXTRA_TO_TIME = "toTime";

    @Bind(R.id.pickerToTime_timePicker) TimePicker timePicker;

    //// DialogFragment methods

    @Override @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.picker_to_time, null);
        ButterKnife.bind(this, view);
        timePicker.setIs24HourView(true);

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(getString(R.string.time_picker_dialog_title))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK);
                    }
                })
                .create();
    }

    //// Private methods

    private void sendResult(int resultCode) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_TO_TIME, getDateFromTimePicker(timePicker));

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

    private int[] getDateFromTimePicker(TimePicker timePicker) {
        int hour = timePicker.getCurrentHour();
        int minute = timePicker.getCurrentMinute();

        return new int[]{hour, minute};
    }
}
