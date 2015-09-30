package kgk.beacon.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.DatePicker;

import butterknife.Bind;
import butterknife.ButterKnife;
import kgk.beacon.R;

public class ToDatePickerDialog extends DialogFragment {

    public static final String EXTRA_TO_DATE = "toDate";

    @Bind(R.id.pickerToDate_datePicker) DatePicker datePicker;

    //// DialogFragment methods

    @Override @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.picker_to_date, null);
        ButterKnife.bind(this, view);

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(getString(R.string.date_picker_dialog_title))
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
        intent.putExtra(EXTRA_TO_DATE, getDateFromDatePicker(datePicker));

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

    private int[] getDateFromDatePicker(DatePicker datePicker) {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();

        return new int[]{year, month, day};
    }
}
