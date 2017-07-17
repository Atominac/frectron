package com.fretron.fleet;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.icu.text.TimeZoneFormat;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.Button;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.TimeZone;

public class DateDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    private ViewTimelineLocation viewTimelineLocation ;


    public DateDialog(ViewTimelineLocation viewTimelineLocation) {
        this.viewTimelineLocation = viewTimelineLocation;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), this, yy, mm, dd);
    }


    @Override
    public void onDateSet(DatePicker view, int yy, int mm, int dd) {
        populateSetDate(yy, mm+1, dd);
    }

    public void populateSetDate(int year, int month, int day) {
        viewTimelineLocation.mDataList.clear();
        viewTimelineLocation.mTimeLineAdapter.notifyDataSetChanged();
        Button button = (Button)getActivity().findViewById(R.id.location_date_selector_button);
        button.setText(day+"/"+month+"/"+year);

        String string = button.getText().toString();
        String[] parts = string.split("/");
        day = Integer.parseInt(parts[0]);
        month = Integer.parseInt(parts[1]);
        year = Integer.parseInt(parts[2]);
        // Toast.makeText(getActivity(),date + month + year , Toast.LENGTH_LONG ).show();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
        calendar.set(year, month-1, day, 0,0,0);
        long startTime = calendar.getTimeInMillis();
        String selected_startTime = Long.toString(startTime);

        Calendar calendar2 = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        calendar2.set(year, month-1, day, 23,59, 59);
        long endTime = calendar2.getTimeInMillis();
        String selected_endTime = Long.toString(endTime);
        viewTimelineLocation.makeJsonObjectRequest(selected_startTime,selected_endTime,viewTimelineLocation.vehicle_Id);
    }

}
