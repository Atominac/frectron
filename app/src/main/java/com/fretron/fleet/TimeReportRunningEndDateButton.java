package com.fretron.fleet;


import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.Button;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimeReportRunningEndDateButton extends DialogFragment implements TimePickerDialog.OnTimeSetListener  {


    public TimeReportRunningEndDateButton() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker

        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Button button = (Button)getActivity().findViewById(R.id.end_date_button);
        button.setText(button.getText().toString()+hourOfDay+":"+minute);

    }
}
