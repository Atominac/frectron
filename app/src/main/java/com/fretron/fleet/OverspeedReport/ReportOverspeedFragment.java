package com.fretron.fleet.OverspeedReport;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate;
import com.appeaser.sublimepickerlibrary.recurrencepicker.SublimeRecurrencePicker;
import com.fretron.fleet.Timeline.Orientation;
import com.fretron.fleet.R;
import com.fretron.fleet.Essentials.VolleyRequester;
import com.fretron.fleet.Essentials.SublimePickerFragment;
import com.fretron.fleet.Essentials.VolleyMain;
import com.fretron.fleet.dashboard.DashBoard;
import com.fretron.fleet.dashboard.DashMainFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.android.gms.plus.internal.PlusCommonExtras.TAG;

public class ReportOverspeedFragment extends Fragment implements View.OnClickListener {
    protected View mView;
    private Orientation mOrientation;
    SelectedDate mSelectedDate,mSelectedDate2;
    int mHour,mHour2, mMinute, mMinute2;
    String mRecurrenceOption, mRecurrenceRule,mRecurrenceOption2, mRecurrenceRule2;
    String start_date_epo , end_date_epo ;
    ProgressBar progressBar;
    SQLiteDatabase sqLiteDatabase;
    Cursor c , c2 , vehicleNamefindCursor ;
    ArrayList<String> list = new ArrayList<>();
    private RecyclerView horizontalRecyclerView ;
    ArrayList<String> selected_vehicles = new ArrayList<>();
    int position = 0;
    List<OverspeedParentListDetails> parentItemList = new ArrayList<>();
    OverspeedParentAdapter mAdapter;
    ArrayAdapter<CharSequence> arrayAdapter;
    Calendar calendar;
    String check;
    CheckBox[] cb = new CheckBox[1];
    RecyclerView recyclerView;
    ArrayList<String> current_vehicle_in_list = new ArrayList<>();
    Double total_distance = 0.0 ,totalSubDistance , averageSpeed = 0.0 ,parentTime = 0.0 , neTime = 0.0 ;
    TextView textViewTotalRecords,textViewTotalDistance,textViewTotalTime;
    String record = " Record(s)" , recordValue , netRecord;
    int total_records = 0 ;
    Double calculatedDistance = 0.0 ;

    public ReportOverspeedFragment() {
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
        final View view = inflater.inflate(R.layout.fragment_report_overspeed, container, false);
        this.mView = view;

        ((DashBoard) getActivity())
                .setActionBarTitle("Report -> Overspeed");

        final Button button = (Button) mView.findViewById(R.id.overspeeding_start_date_button);
        button.setOnClickListener(this);

        final Button button2 = (Button) mView.findViewById(R.id.overspeeding_end_date_button);
        button2.setOnClickListener(this);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!button.getText().equals("Start Date")&& !button2.getText().equals("End Date")
                        && !current_vehicle_in_list.isEmpty()) {
                    LayoutInflater lil = LayoutInflater.from(getActivity());
                    View promptsView = lil.inflate(R.layout.report_filter_duration, null);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setView(promptsView);

                    Spinner spinner = (Spinner) promptsView.findViewById(R.id.report_filter_spinner);
                    arrayAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.timeline_filter_status, android.R.layout.simple_spinner_item);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(arrayAdapter);


                    alertDialogBuilder.setTitle("Duration Filter");

                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {


                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                }

                else
                    Toast.makeText(getActivity(),"Please select both dates\nand add some vehicles" , Toast.LENGTH_SHORT).show();



            }
        });

        fab.setVisibility(View.VISIBLE);

        Context mContext = getActivity().getApplicationContext();

        openDatabase();
        String query2 = "SELECT * FROM vehicle_list";
        c = sqLiteDatabase.rawQuery(query2, null);
        c.moveToFirst();

        textViewTotalRecords = (TextView)mView.findViewById(R.id.running_report_total_records);
        textViewTotalDistance = (TextView)mView.findViewById(R.id.running_report_total_distance);
        textViewTotalTime = (TextView)mView.findViewById(R.id.running_report_total_time);
        recyclerView = (RecyclerView) mView.findViewById(R.id.recyclerView_overspeed_list);

        final ArrayList<String> vehicleRegistrationList = new ArrayList<>();

        if (c.moveToFirst()) {
            do {
                String vehicleRegNo = c.getString(0);
                //String vtsDeviceId = c.getString(1);
                vehicleRegistrationList.add(vehicleRegNo);

            } while (c.moveToNext());
        }
        c.close();

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        horizontalRecyclerView = (RecyclerView)mView.findViewById(R.id.vehicle_horizontal_list);
        horizontalRecyclerView.setLayoutManager(layoutManager);

        RecyclerView.Adapter horizontalListAdapter = new HorizontalVehicleListAdapterOverspeed(mContext, current_vehicle_in_list, ReportOverspeedFragment.this);
        horizontalRecyclerView.setAdapter(horizontalListAdapter);

        Button mButtonAdd = (Button) mView.findViewById(R.id.button_add_vehicle);

        mButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!button.getText().equals("Start Date")&& !button2.getText().equals("End Date") )
                {

                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.add_vehicle_dialog);
                final ListView listView = (ListView) dialog.findViewById(R.id.list);
                Button btn = (Button) dialog.findViewById(R.id.button_ok);

                ArrayAdapter<String> ad = new ArrayAdapter<>(getActivity(), R.layout.add_vehicle_dialog_list,
                        R.id.add_vehicle_list_item_text,vehicleRegistrationList);
                listView.setAdapter(ad);
                dialog.show();

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        for (int x = 0; x<listView.getChildCount();x++){
                            cb[0] = (CheckBox)listView.getChildAt(x).findViewById(R.id.add_vehicle_list_item_text);
                            if(cb[0].isChecked()){
                                if (current_vehicle_in_list.contains(cb[0].getText().toString())){
                                    Toast.makeText(getActivity(),"Vehicle already added",Toast.LENGTH_LONG).show();
                                    check = "false" ;
                                    break;
                                }
                                else {
                                    selected_vehicles.add(cb[0].getText().toString());
                                    check = "true";
                                }

                            }
                        }

                        if (check.equals("true")){
                            dialog.dismiss();

                            for (int i=0;i<selected_vehicles.size();i++){
                                if (!current_vehicle_in_list.contains(selected_vehicles.get(i))){
                                    current_vehicle_in_list.add(selected_vehicles.get(i));
                                }
                            }
                            horizontalRecyclerView.scrollToPosition(position);

                            String query2 = "SELECT * FROM vehicle_list";
                            c2 = sqLiteDatabase.rawQuery(query2, null);
                            c2.moveToFirst();
                            if (c2.moveToFirst()) {
                                do {

                                    for ( int i = 0 ; i<current_vehicle_in_list.size();i++ ){
                                        if (c2.getString(0).equals(current_vehicle_in_list.get(i))){
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

                            if (parentItemList != null){
                                parentItemList.clear();
                                mAdapter = new OverspeedParentAdapter(parentItemList  , getActivity());
                                mAdapter.notifyDataSetChanged();
                                total_records = 0 ;
                                total_distance = 0.0 ;
                                neTime = 0.0 ;

                            }

                            // JSON CALL STARTS HERE
                            makeJsonObjectRequest(start_date_epo,end_date_epo,list);
                        }
                    }
                });

                }

                else
                    Toast.makeText(getActivity(),"Please select both dates" , Toast.LENGTH_SHORT).show();

            }
        });

        return view;
    }

    private LinearLayoutManager getLinearLayoutManager() {
        if (mOrientation == Orientation.HORIZONTAL) {
            return new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        } else {
            return new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.overspeeding_start_date_button:

                SublimePickerFragment pickerFrag = new SublimePickerFragment();
                pickerFrag.setCallback(mFragmentCallback2);
                pickerFrag.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
                pickerFrag.show(getFragmentManager(), "SUBLIME_PICKER");
                break;

            case R.id.overspeeding_end_date_button:

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
            Button button2 = (Button) mView.findViewById(R.id.overspeeding_end_date_button);
            String h = String.valueOf(mHour);
            String min = String.valueOf(mMinute);
            button2.setText((d + "/" + m + "/" + y + "  " + h + ":" + min));
            Calendar calendar = Calendar.getInstance();
            calendar.set(mSelectedDate.getStartDate().get(Calendar.YEAR), mSelectedDate.getStartDate().get(Calendar.MONTH), mSelectedDate.getStartDate().get(Calendar.DAY_OF_MONTH),
                    mHour,mMinute, 0);
            long startTime = calendar.getTimeInMillis();
            end_date_epo = Long.toString(startTime);
        }

    }

    private void updateInfoView2() {
        if (mSelectedDate2 != null) {
            String y = String.valueOf(mSelectedDate2.getStartDate().get(Calendar.YEAR));
            String m = String.valueOf(mSelectedDate2.getStartDate().get(Calendar.MONTH)+1);
            String d = String.valueOf(mSelectedDate2.getStartDate().get(Calendar.DAY_OF_MONTH));
            Button button2 = (Button) mView.findViewById(R.id.overspeeding_start_date_button);
            String h = String.valueOf(mHour2);
            String min = String.valueOf(mMinute2);
            button2.setText((d + "/" + m + "/" + y + "  " + h + ":" + min));
            Calendar calendar = Calendar.getInstance();
            calendar.set(mSelectedDate2.getStartDate().get(Calendar.YEAR), mSelectedDate2.getStartDate().get(Calendar.MONTH), mSelectedDate2.getStartDate().get(Calendar.DAY_OF_MONTH),
                    mHour2,mMinute2, 0);
            long startTime = calendar.getTimeInMillis();
            start_date_epo = Long.toString(startTime);
        }

    }

    protected void openDatabase() {
        sqLiteDatabase = getActivity().openOrCreateDatabase("vehicle_details", Context.MODE_PRIVATE, null);
    }

    public void makeJsonObjectRequest(String startTime, String endTime, final ArrayList vts_id) {
        showpProgress();

        String urlJsonArray = "http://35.189.189.215:8094/overspeedReport";
        BigInteger bi1 =  new BigInteger(startTime);
        BigInteger bi2 =  new BigInteger(endTime);
        Map<String, Object> data = new HashMap<>();
        data.put( "startTime", bi1 );
        data.put( "endTime", bi2 );
        data.put( "vehicleList", vts_id );

        VolleyRequester request = new VolleyRequester(Request.Method.POST,urlJsonArray,new JSONObject(data),new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {

                String over_speed_speed, overspeedTimeString = "" , parentTimeString = "" ,
                        netTimeString = "" ;
                for (int i = 0; i <= jsonArray.length(); i++) {
                    try {
                        parentTime = 0.0 ;
                        totalSubDistance = 0.0;
                        JSONObject vehicleDetails = (JSONObject) jsonArray.get(i);
                        String imei_no =  vehicleDetails.get("imei").toString();

                        OverspeedParentListDetails p = new OverspeedParentListDetails();

                        JSONArray  start_position_object = (JSONArray) vehicleDetails.get("value");
                        ArrayList<OverspeedingChildListDetails> childs = new ArrayList<>();
                        for (int j=0 ; j<=start_position_object.length()-1;j++){

                            averageSpeed =0.0;

                            JSONObject inner_json = (JSONObject) start_position_object.get(j);
                            String over_speed_start_time = inner_json.getString("startTime");

                            calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(Long.parseLong(over_speed_start_time));

                            int yy = calendar.get(Calendar.YEAR);
                            int mm = calendar.get(Calendar.MONTH);
                            int dd = calendar.get(Calendar.DAY_OF_MONTH);
                            int hh = calendar.get(Calendar.HOUR_OF_DAY);
                            int mi = calendar.get(Calendar.MINUTE);

                            String netDateTime = String.valueOf(dd)+ "/" +
                                    String.valueOf(mm)+  "/" +
                                    String.valueOf(yy)+  " " +
                                    String.valueOf(hh)+  ":" +
                                    String.valueOf(mi);


                            JSONObject  position_object = (JSONObject) inner_json.get("startPosition");
                            Double lat = position_object.getDouble("lat");
                            Double lng = position_object.getDouble("long");

                            String over_speed_duration = inner_json.getString("duration");

                            long x = Long.parseLong(over_speed_duration);
                            long milliseconds ,seconds , minutes , hours , days ;


                            milliseconds = x % 1000;
                            x = x / 1000 ;
                            seconds = x % 60 ;
                            x /= 60 ;
                            minutes = x % 60 ;
                            x /= 60 ;
                            hours = x % 24 ;
                            x /= 24 ;
                            days = x ;

                            if (days>0){
                                overspeedTimeString = String.valueOf(days) + " D\n" + String.valueOf(hours) + " Hrs";
                            }

                            else if(days == 0 && hours>0){
                                overspeedTimeString = String.valueOf(hours) + " Hrs\n"+
                                        String.valueOf(minutes) + " min" ;
                            }

                            else if (days==0 && hours==0 && minutes>0){
                                overspeedTimeString = String.valueOf(minutes) + " min\n" + String.valueOf(seconds) + " sec";
                            }

                            else if (days==0 && hours==0 && minutes==0 && seconds>0){
                                overspeedTimeString = String.valueOf(seconds) + " sec\n" + String.valueOf(x) + " msec";

                            }

                            else if (days==0 && hours==0 && minutes==0 && seconds==0){
                                overspeedTimeString = String.valueOf(milliseconds) + " msec\n";

                            }

                            parentTime = parentTime + Double.parseDouble(over_speed_duration);

                            over_speed_speed = inner_json.getString("averageSpeed");

                            if (!over_speed_speed.equals("null")){
                                averageSpeed= Math.round((Double.parseDouble(over_speed_speed)/1000) * 100.0) / 100.0;
                                calculatedDistance = (Double.parseDouble(over_speed_duration)/3600000)*Double.parseDouble(over_speed_speed);
                                totalSubDistance = totalSubDistance + calculatedDistance;
                            }
                            else {
                                averageSpeed= 0.0;
                                calculatedDistance = 0.0;
                                totalSubDistance = 0.0;
                            }

                            OverspeedingChildListDetails activityItems = new OverspeedingChildListDetails(netDateTime, overspeedTimeString ,String.valueOf(averageSpeed)+ "\nKmph","Loading" ,lat,lng);
                            childs.add(activityItems);
                        }

                        total_distance = total_distance + totalSubDistance ;

                        String findVehicleNameQuery = "SELECT vehicleRegistrationNo FROM vehicle_list where vtsId =" + imei_no;
                        vehicleNamefindCursor = sqLiteDatabase.rawQuery(findVehicleNameQuery, null);
                        vehicleNamefindCursor.moveToFirst();

                        String vehicleRegNo = " N-A- " ;
                        if (vehicleNamefindCursor.moveToFirst()) {
                            do {
                                vehicleRegNo = vehicleNamefindCursor.getString(0);

                            } while (vehicleNamefindCursor.moveToNext());
                        }
                        vehicleNamefindCursor.close();

                        long x = Math.round(parentTime);
                        long milliseconds ,seconds , minutes , hours , days ;

                        milliseconds = x % 1000;
                        x = x / 1000 ;
                        seconds = x % 60 ;
                        x /= 60 ;
                        minutes = x % 60 ;
                        x /= 60 ;
                        hours = x % 24 ;
                        x /= 24 ;
                        days = x ;

                        if (days>0){
                            parentTimeString = String.valueOf(days) + " D  " + String.valueOf(hours) + " Hrs";
                        }

                        else if(days == 0 && hours>0){
                            parentTimeString = String.valueOf(hours) + " Hrs  "+
                                    String.valueOf(minutes) + " min" ;
                        }

                        else if (days==0 && hours==0 && minutes>0){
                            parentTimeString = String.valueOf(minutes) + " min  " + String.valueOf(seconds) + " sec";
                        }

                        else if (days==0 && hours==0 && minutes==0 && seconds>0){
                            parentTimeString = String.valueOf(seconds) + " sec  " + String.valueOf(x) + " msec";

                        }

                        else if (days==0 && hours==0 && minutes==0 && seconds==0){
                            parentTimeString = String.valueOf(milliseconds) + " msec";

                        }

                        p.setSpeed(parentTimeString);
                        p.setTitle(vehicleRegNo);
                        p.setDistance(String.valueOf(Math.round(totalSubDistance))+ " Kms");
                        p.childListDetailses = childs;
                        parentItemList.add(p);
                        total_records ++;
                        neTime = neTime + parentTime;
                    }

                    catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                hidepProgress();
                recordValue = String.valueOf(total_records);
                netRecord = recordValue + record ;
                textViewTotalRecords.setText(netRecord);
                textViewTotalDistance.setText(String.valueOf(Math.round(total_distance)) + " Kms");

                long x = Math.round(neTime);
                long milliseconds ,seconds , minutes , hours , days ;

                milliseconds = x % 1000;
                x = x / 1000 ;
                seconds = x % 60 ;
                x /= 60 ;
                minutes = x % 60 ;
                x /= 60 ;
                hours = x % 24 ;
                x /= 24 ;
                days = x ;

                if (days>0){
                    netTimeString = String.valueOf(days) + " D  " + String.valueOf(hours) + " Hrs";
                }

                else if(days == 0 && hours>0){
                    netTimeString = String.valueOf(hours) + " Hrs  "+
                            String.valueOf(minutes) + " min" ;
                }

                else if (days==0 && hours==0 && minutes>0){
                    netTimeString = String.valueOf(minutes) + " min  " + String.valueOf(seconds) + " sec";
                }

                else if (days==0 && hours==0 && minutes==0 && seconds>0){
                    netTimeString = String.valueOf(seconds) + " sec  " + String.valueOf(x) + " msec";

                }

                else if (days==0 && hours==0 && minutes==0 && seconds==0){
                    netTimeString = String.valueOf(milliseconds) + " msec";

                }

                textViewTotalTime.setText(netTimeString);

                mAdapter = new OverspeedParentAdapter(parentItemList , getActivity());
                mAdapter.setActivityList(parentItemList , getActivity());
                recyclerView.setLayoutManager(getLinearLayoutManager());
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(mAdapter);

            }
        }
                , new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });

        VolleyMain.getInstance().addToRequestQueue(request);
    }

    private void showpProgress() {
        LinearLayout linearLayout = (LinearLayout)mView.findViewById(R.id.magic_linear_layout2);
        progressBar = (ProgressBar)mView.findViewById(R.id.progressBar_overspeed_activity);
        linearLayout.setVisibility(View.GONE);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
    }
    private void hidepProgress() {
        LinearLayout linearLayout = (LinearLayout)mView.findViewById(R.id.magic_linear_layout2);
        progressBar = (ProgressBar)mView.findViewById(R.id.progressBar_overspeed_activity);
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
