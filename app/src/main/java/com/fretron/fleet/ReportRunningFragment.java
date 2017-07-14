package com.fretron.fleet;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;;
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

public class ReportRunningFragment extends Fragment implements View.OnClickListener {
    protected View mView;
    int y, m, d;
    SelectedDate mSelectedDate,mSelectedDate2;
    int mHour,mHour2, mMinute, mMinute2;
    String mRecurrenceOption, mRecurrenceRule,mRecurrenceOption2, mRecurrenceRule2;
    RelativeLayout mRelativeLayout;
    List<RunningReportListItems> activityList = new ArrayList<>();
    Cursor c,c2;
    SQLiteDatabase sqLiteDatabase;
    public RecyclerView mRecyclerView , reportRecycler ;
    RecyclerView.Adapter mAdapter , runningAdapter ;
    int position = 0, positionn = 0, label = 0;
    ProgressBar progressBar;
    String start_date_epo , end_date_epo ;
    ArrayList<String> selected_vehicles = new ArrayList<>();
    ArrayList<String> current_vehicle_in_list = new ArrayList<>();
    int total_records = 0 ;
    Double total_distance = 0.0 , total_time ;
    TextView textViewTotalRecords,textViewTotalDistance,textViewTotalTime;
    String record = " Record(s)" , recordValue , netRecord;
    String readableDistance = "" ;
    ArrayList<String> list = new ArrayList<String>();
    String check;
    CheckBox[] cb = new CheckBox[1];
    RecyclerView recyclerView;
    double roundOff = 0.0 , averageSpeedDouble = 0.0 ;

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

        openDatabase();

        String query2 = "SELECT * FROM vehicle_list";
        c = sqLiteDatabase.rawQuery(query2, null);
        c.moveToFirst();

        Button button = (Button) mView.findViewById(R.id.start_date_button);
        button.setOnClickListener(this);

        Button button2 = (Button) mView.findViewById(R.id.end_date_button);
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

        int size_count = 0;

        if (c.moveToFirst()) {
            do {
                String vehicleRegNo = c.getString(0);
                //String vtsDeviceId = c.getString(1);
                vehicleRegistrationList.add(vehicleRegNo);
                size_count++;

            } while (c.moveToNext());
        }
        else {
            vehicleRegistrationList.add("No data available");
        }
        c.close();

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new HorizontalVehicleListAdapter(mContext, current_vehicle_in_list , ReportRunningFragment.this);
        mRecyclerView.setAdapter(mAdapter);

        mButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.add_vehicle_dialog);
                //dialog.setTitle("Select your vehicle");
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
                            mRecyclerView.scrollToPosition(position);

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

                            if (activityList != null){
                                activityList.clear();
                                runningAdapter = new RunningReportListAdapter(activityList);
                                runningAdapter.notifyDataSetChanged();
                                total_records = 0 ;
                            }

                            // JSON CALL STARTS HERE
                            makeJsonObjectRequest(start_date_epo, end_date_epo, list);
                            // List Loading starts from here

                        }

                    }
                });
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
            calendar.set(mSelectedDate.getStartDate().get(Calendar.YEAR), mSelectedDate.getStartDate().get(Calendar.MONTH)+1, mSelectedDate.getStartDate().get(Calendar.DAY_OF_MONTH),
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
            calendar.set(mSelectedDate2.getStartDate().get(Calendar.YEAR), mSelectedDate2.getStartDate().get(Calendar.MONTH)+1, mSelectedDate2.getStartDate().get(Calendar.DAY_OF_MONTH),
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

        Map<String, Object> data = new HashMap<String, Object>();
        data.put( "startTime", bi1 );
        data.put( "endTime", bi2 );
        data.put( "vehicleList", vts_id );

        Requester request = new Requester(Request.Method.POST,urlJsonArray,new JSONObject(data),new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {

                RunningReportListItems activityItems;
                for (int i = 0; i <=jsonArray.length(); i++) {
                    try {

                        JSONObject vehicleDetails = (JSONObject) jsonArray.get(i);
                        String vehicle_no =  vehicleDetails.get("imei").toString();
                        String drive_time = vehicleDetails.get("driveTime").toString();
                        String distance_total = vehicleDetails.get("distance").toString();

                        total_distance = total_distance + Double.parseDouble(distance_total);
                        total_distance = total_distance/1000 ;
                        roundOff = Math.round(total_distance * 100.0) / 100.0;

                        readableDistance = String.valueOf(roundOff) + " Kms";

                        String average = vehicleDetails.get("average").toString();
                        averageSpeedDouble =   Math.round(   (Double.parseDouble(average)/1000) * 100.0) / 100.0;

//                        JSONObject  start_position_object = (JSONObject) vehicleDetails.get("startPosition");
//                        Double lat = start_position_object.getDouble("lat");
//                        Double lng = start_position_object.getDouble("long");
//                        Geocoder gcd = new Geocoder(getActivity(), Locale.getDefault());
//                        List<Address> start_position_string = gcd.getFromLocation(lat, lng, 1);;

//                        String test = start_position_string.get(0).getAddressLine(0);

//                        JSONObject  end_position_object = (JSONObject) vehicleDetails.get("endPosition");
//                        Double lat2 = end_position_object.getDouble("lat");
//                        Double lng2 = end_position_object.getDouble("long");
//                        Geocoder gcd2 = new Geocoder(getActivity(), Locale.getDefault());
//                        List<Address> end_position_string = gcd2.getFromLocation(lat2, lng2, 1);
//
//                        String test2 = end_position_string.get(0).getAddressLine(0);

                        String stoppage_time = vehicleDetails.get("stoppageTime").toString();
                        String overspeed_duration = vehicleDetails.get("overSpeedDuration").toString();

                        activityItems = new RunningReportListItems(vehicle_no,"Initial Location", "20hrs",readableDistance,"final Location", String.valueOf(averageSpeedDouble) , stoppage_time, drive_time, overspeed_duration, "NA");
                        activityList.add(activityItems);
                        total_records ++;

                    } catch (JSONException e) {
                        Toast.makeText(getActivity(), "location Error.", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
                recordValue = String.valueOf(total_records);
                netRecord = recordValue + record ;
                textViewTotalRecords.setText(netRecord);
                textViewTotalDistance.setText(String.valueOf(Math.round(total_distance * 100.0) / 100.0));
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