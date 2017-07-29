package com.fretron.fleet.RunningReport;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView ;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate;
import com.appeaser.sublimepickerlibrary.recurrencepicker.SublimeRecurrencePicker;
import com.fretron.fleet.R;
import com.fretron.fleet.Essentials.VolleyRequester;
import com.fretron.fleet.Essentials.SublimePickerFragment;
import com.fretron.fleet.Essentials.VolleyMain;
import com.fretron.fleet.dashboard.DashBoard;
import com.fretron.fleet.dashboard.DashMainFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import static com.google.android.gms.plus.internal.PlusCommonExtras.TAG;

public class ReportRunningFragment extends Fragment implements View.OnClickListener {
    protected View mView;
    int y, m, d;
    SelectedDate mSelectedDate,mSelectedDate2;
    int mHour,mHour2, mMinute, mMinute2;
    String mRecurrenceOption, mRecurrenceRule,mRecurrenceOption2, mRecurrenceRule2;
    RelativeLayout mRelativeLayout;
    List<RunningReportListItems> activityList = new ArrayList<>();
    Cursor c,c2, vehicleNamefindCursor ;
    SQLiteDatabase sqLiteDatabase;
    public RecyclerView mRecyclerView  ;
    RecyclerView.Adapter mAdapter , runningAdapter ;
    int position = 0 ;
    ProgressBar progressBar;
    String start_date_epo , end_date_epo ;
    ArrayList<String> selected_vehicles = new ArrayList<>();
    ArrayList<String> current_vehicle_in_list = new ArrayList<>();
    int total_records = 0 ;
    Double total_distance = 0.0 , subDistance = 0.0, total_time = 0.0  ;
    TextView textViewTotalRecords,textViewTotalDistance,textViewTotalTime;
    String record = " Record(s)" , recordValue , netRecord;
    ArrayList<String> list = new ArrayList<>();
    String check;
    CheckBox[] cb = new CheckBox[1];
    RecyclerView recyclerView;
    double averageSpeedDouble = 0.0 ;

    public ReportRunningFragment() {
        // Required empty public constructor
    }

    SublimePickerFragment.Callback mFragmentCallback = new SublimePickerFragment.Callback() {

        @Override
        public void onCancelled() {
        }

        @Override
        public void onDateTimeRecurrenceSet(SelectedDate selectedDate, int hourOfDay, int minute,
                                            SublimeRecurrencePicker.RecurrenceOption recurrenceOption,
                                            String recurrenceRule) throws ParseException {

            mSelectedDate = selectedDate;
            mHour = hourOfDay;
            mMinute = minute;

            mRecurrenceOption = recurrenceOption != null ?
                    recurrenceOption.name() : "n/a";
            mRecurrenceRule = recurrenceRule != null ?
                    recurrenceRule : "n/a";


            updateInfoView();
        }
    };
    SublimePickerFragment.Callback mFragmentCallback2 = new SublimePickerFragment.Callback() {

        @Override
        public void onCancelled() {
        }

        @Override
        public void onDateTimeRecurrenceSet(SelectedDate selectedDate, int hourOfDay, int minute,
                                            SublimeRecurrencePicker.RecurrenceOption recurrenceOption,
                                            String recurrenceRule) throws ParseException {

            mSelectedDate2 = selectedDate;
            mHour2 = hourOfDay;
            mMinute2 = minute;

            mRecurrenceOption2 = recurrenceOption != null ?
                    recurrenceOption.name() : "n/a";
            mRecurrenceRule2 = recurrenceRule != null ?
                    recurrenceRule : "n/a";

            updateInfoView2();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_report_running, container, false);
        this.mView = view;

        textViewTotalRecords = (TextView)mView.findViewById(R.id.running_report_total_records);
        textViewTotalDistance = (TextView)mView.findViewById(R.id.running_report_total_distance);
        textViewTotalTime = (TextView)mView.findViewById(R.id.running_report_total_time);
        recyclerView = (RecyclerView) mView.findViewById(R.id.running_report_recycler);

        ((DashBoard) getActivity())
                .setActionBarTitle("Report -> Running");

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);

        openDatabase();

        String query2 = "SELECT * FROM vehicle_list";
        c = sqLiteDatabase.rawQuery(query2, null);
        c.moveToFirst();

        final Button button = (Button) mView.findViewById(R.id.start_date_button);
        button.setOnClickListener(this);

        final Button button2 = (Button) mView.findViewById(R.id.end_date_button);
        button2.setOnClickListener(this);

        final Calendar calendar = Calendar.getInstance();
        y = calendar.get(Calendar.YEAR);
        m = calendar.get(Calendar.MONTH);
        d = calendar.get(Calendar.DAY_OF_MONTH);

        Context mContext = getActivity().getApplicationContext();
        mRelativeLayout = (RelativeLayout) mView.findViewById(R.id.vehicle_horizontal_list_parent_layout);
        Button mButtonAdd = (Button) mView.findViewById(R.id.button_add_vehicle);
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.vehicle_horizontal_list);
        final ArrayList<String> vehicleRegistrationList = new ArrayList<>();


        if (c.moveToFirst()) {
            do {
                String vehicleRegNo = c.getString(0);
                //String vtsDeviceId = c.getString(1);
                vehicleRegistrationList.add(vehicleRegNo);

            } while (c.moveToNext());
        }
        else {
            vehicleRegistrationList.add("No data available");
        }
        c.close();

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new HorizonVehicleListAdapter(mContext, current_vehicle_in_list , ReportRunningFragment.this);
        mRecyclerView.setAdapter(mAdapter);

            mButtonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (!button.getText().equals("Start Date")&& !button2.getText().equals("End Date") )
                    {

                    final Dialog dialog = new Dialog(getActivity());
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.add_vehicle_dialog);
                    //dialog.setTitle("Select your vehicle");
                    final ListView listView = (ListView) dialog.findViewById(R.id.list);
                    Button btn = (Button) dialog.findViewById(R.id.button_ok);

                    ArrayAdapter<String> ad = new ArrayAdapter<>(getActivity(), R.layout.add_vehicle_dialog_list,
                            R.id.add_vehicle_list_item_text, vehicleRegistrationList);
                    listView.setAdapter(ad);
                    dialog.show();

                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            for (int x = 0; x < listView.getChildCount(); x++) {
                                cb[0] = (CheckBox) listView.getChildAt(x).findViewById(R.id.add_vehicle_list_item_text);
                                if (cb[0].isChecked()) {
                                    if (current_vehicle_in_list.contains(cb[0].getText().toString())) {
                                        Toast.makeText(getActivity(), "Vehicle already added", Toast.LENGTH_LONG).show();
                                        check = "false";
                                        break;
                                    } else {
                                        selected_vehicles.add(cb[0].getText().toString());
                                        check = "true";
                                    }

                                }
                            }

                            if (check.equals("true")) {
                                dialog.dismiss();

                                for (int i = 0; i < selected_vehicles.size(); i++) {
                                    if (!current_vehicle_in_list.contains(selected_vehicles.get(i))) {
                                        current_vehicle_in_list.add(selected_vehicles.get(i));
                                    }
                                }
                                mRecyclerView.scrollToPosition(position);

                                String query2 = "SELECT * FROM vehicle_list";
                                c2 = sqLiteDatabase.rawQuery(query2, null);
                                c2.moveToFirst();
                                if (c2.moveToFirst()) {
                                    do {

                                        for (int i = 0; i < current_vehicle_in_list.size(); i++) {
                                            if (c2.getString(0).equals(current_vehicle_in_list.get(i))) {
                                                if (!list.contains(c2.getString(1)))
                                                    list.add(c2.getString(1));
                                            }
                                        }
                                    } while (c2.moveToNext());
                                }
                                c2.close();

                                Set<String> hs = new HashSet<>();
                                hs.addAll(list);
                                list.clear();
                                list.addAll(hs);

                                if (activityList != null) {
                                    activityList.clear();
                                    runningAdapter = new RunningReportListAdapter(activityList);
                                    runningAdapter.notifyDataSetChanged();
                                    total_records = 0;
                                }

                                // JSON CALL STARTS HERE
                                makeJsonObjectRequest(start_date_epo, end_date_epo, list);
                                // List Loading starts from here

                            }

                        }
                    });

                }

                    else
                        Toast.makeText(getActivity(),"Please select both dates" , Toast.LENGTH_SHORT).show();

                }
            });


        // setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.start_date_button:

                SublimePickerFragment pickerFrag = new SublimePickerFragment();
                pickerFrag.setCallback(mFragmentCallback2);
                pickerFrag.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
                pickerFrag.show(getFragmentManager(), "SUBLIME_PICKER");
                break;

            case R.id.end_date_button:

                SublimePickerFragment pickerFrag2 = new SublimePickerFragment();
                pickerFrag2.setCallback(mFragmentCallback);
                pickerFrag2.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
                pickerFrag2.show(getFragmentManager(), "SUBLIME_PICKER");
                break;
        }

    }

    private void updateInfoView(){
        if (mSelectedDate != null) {
            String y = String.valueOf(mSelectedDate.getStartDate().get(Calendar.YEAR));
            String m = String.valueOf(mSelectedDate.getStartDate().get(Calendar.MONTH)+1);
            String d = String.valueOf(mSelectedDate.getStartDate().get(Calendar.DAY_OF_MONTH));
            Button button2 = (Button) mView.findViewById(R.id.end_date_button);
            String h = String.valueOf(mHour);
            String min = String.valueOf(mMinute);
            button2.setText((d + "/" + m + "/" + y + "  " + h + ":" + min));
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
            calendar.set(mSelectedDate.getStartDate().get(Calendar.YEAR), mSelectedDate.getStartDate().get(Calendar.MONTH), mSelectedDate.getStartDate().get(Calendar.DAY_OF_MONTH),
                    mHour,mMinute, 0);
            long endTime = calendar.getTimeInMillis();
            end_date_epo = Long.toString(endTime);
        }

    }

    private void updateInfoView2() {
        if (mSelectedDate2 != null) {
            String y = String.valueOf(mSelectedDate2.getStartDate().get(Calendar.YEAR));
            String m = String.valueOf(mSelectedDate2.getStartDate().get(Calendar.MONTH)+1);
            String d = String.valueOf(mSelectedDate2.getStartDate().get(Calendar.DAY_OF_MONTH));
            Button button2 = (Button) mView.findViewById(R.id.start_date_button);
            String h = String.valueOf(mHour2);
            String min = String.valueOf(mMinute2);
            button2.setText((d + "/" + m + "/" + y + "  " + h + ":" + min));
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
            calendar.set(mSelectedDate2.getStartDate().get(Calendar.YEAR), mSelectedDate2.getStartDate().get(Calendar.MONTH), mSelectedDate2.getStartDate().get(Calendar.DAY_OF_MONTH),
                    mHour2,mMinute2, 0);
            long startTime = calendar.getTimeInMillis();
            start_date_epo = Long.toString(startTime);
        }

    }

    protected void openDatabase() {
        sqLiteDatabase = getActivity().openOrCreateDatabase("vehicle_details", Context.MODE_PRIVATE, null);
    }

    public void makeJsonObjectRequest(String startTime,String endTime , final ArrayList vts_id) {
        showpProgress();

        String urlJsonArray = "http://35.189.189.215:8094/runningReport";
        BigInteger bi1 =  new BigInteger(startTime);
        BigInteger bi2 =  new BigInteger(endTime);

        Map<String, Object> data = new HashMap<>();
        data.put( "startTime", bi1 );
        data.put( "endTime", bi2 );
        data.put( "vehicleList", vts_id );

        VolleyRequester request = new VolleyRequester(Request.Method.POST,urlJsonArray,new JSONObject(data),new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {

                long total_seconds, total_minutes, total_hours, total_days;
                String totalTimeString = "" , overspeedTimeString = "" , driveTimeString = "" ,
                                                stoppageTimeString = "" , journeyTotalTime = "" ;

                String test = " Loading " , test2 = " Loading " ;

                RunningReportListItems activityItems;
                for (int i = 0; i <=jsonArray.length(); i++) {
                    try {

                        subDistance = 0.0 ;

                        JSONObject vehicleDetails = (JSONObject) jsonArray.get(i);
                        String vehicle_no =  vehicleDetails.get("imei").toString();
                        String drive_time = vehicleDetails.get("driveTime").toString();

                        long drive_x = Long.parseLong(drive_time);
                        long drive_seconds , drive_minutes , drive_hours , drive_days ;

                        drive_x = drive_x / 1000 ;
                        drive_seconds = drive_x % 60 ;
                        drive_x /= 60 ;
                        drive_minutes = drive_x % 60 ;
                        drive_x /= 60 ;
                        drive_hours = drive_x % 24 ;
                        drive_x /= 24 ;
                        drive_days = drive_x ;

                        if (drive_days>0){
                            driveTimeString = String.valueOf(drive_days) + " D   " + String.valueOf(drive_hours) + " Hrs  ";
                        }

                        else if(drive_days == 0 && drive_hours>0){
                            driveTimeString = String.valueOf(drive_hours) + " Hrs  "+
                                    String.valueOf(drive_minutes) + " min  " ;
                        }

                        else if (drive_days==0 && drive_hours==0 && drive_minutes>0){
                            driveTimeString = String.valueOf(drive_minutes) + " min   " + String.valueOf(drive_seconds) + " sec  ";
                        }


                        String distance_total = vehicleDetails.get("distance").toString();

                        subDistance = Math.round(( (Double.parseDouble(distance_total))) * 100.0) / 100.0;

                        total_distance = total_distance + subDistance ;

                        String average = vehicleDetails.get("average").toString();
                        averageSpeedDouble =   Math.round(   (Double.parseDouble(average)) * 100.0) / 100.0;

                        JSONObject  start_position_object = (JSONObject) vehicleDetails.get("startPosition");
                        Double lat = start_position_object.getDouble("lat");
                        Double lng = start_position_object.getDouble("long");
                        Geocoder gcd = new Geocoder(getActivity(), Locale.getDefault());
                        List<Address> start_position_string = gcd.getFromLocation(lat, lng, 1);

                        if (start_position_string.size()!=0){
                            String city = start_position_string.get(0).getLocality();
                            String state = start_position_string.get(0).getAdminArea();
                            test  = start_position_string.get(0).getAddressLine(0) + "\n" +
                                    city + "\n" + state;
                        }

                        JSONObject  end_position_object = (JSONObject) vehicleDetails.get("endPosition");
                        Double lat2 = end_position_object.getDouble("lat");
                        Double lng2 = end_position_object.getDouble("long");
                        Geocoder gcd2 = new Geocoder(getActivity(), Locale.getDefault());
                        List<Address> end_position_string = gcd2.getFromLocation(lat2, lng2, 1);

                        if (start_position_string.size()!=0){
                            String city2 = end_position_string.get(0).getLocality();
                            String state2 = end_position_string.get(0).getAdminArea();
                            test2  = end_position_string.get(0).getAddressLine(0) + "\n" +
                                    city2 + "\n" + state2;
                        }

                        String stoppage_time = vehicleDetails.get("stoppageTime").toString();

                        long x = Long.parseLong(stoppage_time);
                        long seconds , minutes , hours , days ;

                        x = x / 1000 ;
                        seconds = x % 60 ;
                        x /= 60 ;
                        minutes = x % 60 ;
                        x /= 60 ;
                        hours = x % 24 ;
                        x /= 24 ;
                        days = x ;

                        if (days>0){
                            stoppageTimeString = String.valueOf(days) + " D   " + String.valueOf(hours) + " Hrs  ";
                        }

                        else if(days == 0 && hours>0){
                            stoppageTimeString = String.valueOf(hours) + " Hrs  "+
                                    String.valueOf(minutes) + " min  " ;
                        }

                        else if (days==0 && hours==0 && minutes>0){
                            stoppageTimeString = String.valueOf(minutes) + " min   " + String.valueOf(seconds) + " sec  ";
                        }

                        String overspeed_duration = vehicleDetails.get("overSpeedDuration").toString();

                        long over_x = Long.parseLong(overspeed_duration);
                        long over_seconds , over_minutes , over_hours , over_days ;

                        over_x = over_x / 1000 ;
                        over_seconds = over_x % 60 ;
                        over_x /= 60 ;
                        over_minutes = over_x % 60 ;
                        over_x /= 60 ;
                        over_hours = over_x % 24 ;
                        over_x /= 24 ;
                        over_days = over_x ;

                        if (over_days>0){
                            overspeedTimeString = String.valueOf(over_days) + " D   " + String.valueOf(over_hours) + " Hrs  ";
                        }

                        else if(over_days == 0 && over_hours>0){
                            overspeedTimeString = String.valueOf(over_hours) + " Hrs  "+
                                    String.valueOf(over_minutes) + " min  " ;
                        }

                        else if (over_days==0 && over_hours==0 && over_minutes>0){
                            overspeedTimeString = String.valueOf(over_minutes) + " min   " + String.valueOf(over_seconds) + " sec  ";
                        }

                        Double journeyTime = subDistance/averageSpeedDouble ;

                        long journey_x = Math.round(journeyTime);
                        long journey_seconds , journey_minutes , journey_hours , journey_days ;

                        journey_x = journey_x / 1000 ;
                        journey_seconds = journey_x % 60 ;
                        journey_x /= 60 ;
                        journey_minutes = journey_x % 60 ;
                        journey_x /= 60 ;
                        journey_hours = journey_x % 24 ;
                        journey_x /= 24 ;
                        journey_days = journey_x ;

                        if (journey_days>0){
                            journeyTotalTime = String.valueOf(journey_days) + " D   " + String.valueOf(journey_hours) + " Hrs  ";
                        }

                        else if(journey_days == 0 && journey_hours>0){
                            journeyTotalTime = String.valueOf(journey_hours) + " Hrs  "+
                                    String.valueOf(journey_minutes) + " min " ;
                        }

                        else if (journey_days==0 && journey_hours==0 && journey_minutes>0){
                            journeyTotalTime = String.valueOf(journey_minutes) + " min   " + String.valueOf(journey_seconds) + " sec ";
                        }

                        else if (journey_days==0 && journey_hours==0 && journey_minutes==0){
                            journeyTotalTime = String.valueOf(journey_seconds) + " sec " ;
                        }

                        total_time = total_time + journeyTime ;

                        String findVehicleNameQuery = "SELECT vehicleRegistrationNo FROM vehicle_list where vtsId =" + vehicle_no;
                        vehicleNamefindCursor = sqLiteDatabase.rawQuery(findVehicleNameQuery, null);
                        vehicleNamefindCursor.moveToFirst();

                        String vehicleRegNo = " N-A- " ;
                        if (vehicleNamefindCursor.moveToFirst()) {
                            do {
                                vehicleRegNo = vehicleNamefindCursor.getString(0);

                            } while (vehicleNamefindCursor.moveToNext());
                        }
                        vehicleNamefindCursor.close();

                        activityItems = new RunningReportListItems("\t" + vehicleRegNo,test , journeyTotalTime ,
                                String.valueOf(Math.round( (subDistance/1000) * 100.0) / 100.0) + " Kms", test2 , String.valueOf(averageSpeedDouble) + " kmph " ,
                                stoppageTimeString , driveTimeString , overspeedTimeString , "NA ");

                        activityList.add(activityItems);
                        total_records ++;

                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }

                }
                recordValue = String.valueOf(total_records);
                netRecord = recordValue + record ;
                textViewTotalRecords.setText(netRecord);

                long total_x = Math.round(total_time);

                total_x = total_x / 1000 ;
                total_seconds = total_x % 60 ;
                total_x /= 60 ;
                total_minutes = total_x % 60 ;
                total_x /= 60 ;
                total_hours = total_x % 24 ;
                total_x /= 24 ;
                total_days = total_x ;

                if (total_days>0){
                    totalTimeString = String.valueOf(total_days) + " D   " + String.valueOf(total_hours) + " Hrs ";
                }

                else if(total_days == 0 && total_hours>0){
                    totalTimeString = String.valueOf(total_hours) + " Hrs  "+
                            String.valueOf(total_minutes) + " min " ;
                }

                else if (total_days==0 && total_hours==0 && total_minutes>0){
                    totalTimeString = String.valueOf(total_minutes) + " min   " + String.valueOf(total_seconds) + " sec ";
                }

                else if (total_days==0 && total_hours==0 && total_minutes==0){
                    totalTimeString = String.valueOf(total_seconds) + " sec ";
                }


                textViewTotalTime.setText(totalTimeString);

                textViewTotalDistance.setText(String.valueOf(Math.round( (total_distance/1000) * 100.0) / 100.0)+ " Kms");
                runningAdapter = new RunningReportListAdapter(activityList);
                LinearLayoutManager layoutManager2
                        = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                recyclerView.setLayoutManager(layoutManager2);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(runningAdapter);

                hidepProgress();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        })
        {
            @Override
            public String getBodyContentType()
            {
                return "application/json";
            }
        };

        VolleyMain.getInstance().addToRequestQueue(request);
    }

    private void showpProgress() {
        LinearLayout linearLayout = (LinearLayout)mView.findViewById(R.id.magic_linear_layout);
        progressBar = (ProgressBar)mView.findViewById(R.id.progressBar_ruunning_activity);
        linearLayout.setVisibility(View.GONE);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
    }
    private void hidepProgress() {
        LinearLayout linearLayout = (LinearLayout)mView.findViewById(R.id.magic_linear_layout);
        progressBar = (ProgressBar)mView.findViewById(R.id.progressBar_ruunning_activity);
        linearLayout.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(false);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onResume(){
        super.onResume();
        if(getView() == null){
            return;
        }

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){


                    DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    } else {

                        DashMainFragment fragment = new DashMainFragment();
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container,fragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();

                    }

                    return true;
                }
                return false;
            }
        });

    }

}