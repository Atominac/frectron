package com.sample.project_frectron;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigInteger;
import java.sql.Array;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.google.android.gms.plus.internal.PlusCommonExtras.TAG;

public class ReportRunningFragment extends Fragment implements View.OnClickListener {
    protected View mView;
    int y, m, d;
    SelectedDate mSelectedDate,mSelectedDate2;
    int mHour,mHour2, mMinute, mMinute2;
    String mRecurrenceOption, mRecurrenceRule,mRecurrenceOption2, mRecurrenceRule2;
    RelativeLayout mRelativeLayout;
    private List<RunningReportListItems> activityList = new ArrayList<>();
    Cursor c;
    private SQLiteDatabase sqLiteDatabase;
    private RecyclerView mRecyclerView , reportRecycler ;
    private RecyclerView.Adapter mAdapter , runningAdapter ;
    int position = 0, positionn = 0, label = 0;
    ProgressBar progressBar;
    String start_date_epo , end_date_epo ;
    ArrayList<String> selected_vehicles = new ArrayList<>();
    int total_records = 0,total_distance = 0, total_time = 0;
    TextView textViewTotalRecords,textViewTotalDistance,textViewTotalTime;
    String record = " Record(s)" , recordValue , netRecord;

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

        final ArrayList<String> current_vehicle_in_list = new ArrayList<>(size_count);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new HorizontalVehicleListAdapter(mContext, current_vehicle_in_list);
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

                int size = vehicleRegistrationList.size();

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                                int positio, long id) {

                            // Saving selected items to arrayList
                        if (selected_vehicles.contains(vehicleRegistrationList.get(positio)))
                                Toast.makeText(getActivity(),"Vehicle already added",Toast.LENGTH_LONG).show();

                        else {
                                parent.getChildAt(positio).setBackgroundColor(getResources().getColor(R.color.colorAccent));
                                selected_vehicles.add(vehicleRegistrationList.get(positio));
                        }
                    }

                });

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //Displaying horizontal list aside of add button
                        if (selected_vehicles.isEmpty()){
                            Toast.makeText(getActivity(),"Please select a vehicle",Toast.LENGTH_LONG).show();
                        }

                        else {
                            dialog.dismiss();

                            while (positionn<selected_vehicles.size()){
                                current_vehicle_in_list.add(positionn, selected_vehicles.get(position));
                                mAdapter.notifyItemInserted(position);
                                position++;
                                positionn++;
                            }

                            mRecyclerView.scrollToPosition(position);
                            final ArrayList<String> list = new ArrayList<String>();
                            //list.add("123");
                            list.add("1234");

                            // JSON CALL STARTS HERE
                            makeJsonObjectRequest(start_date_epo, end_date_epo, list);
                            // List Loading starts from here
                            RecyclerView recyclerView = (RecyclerView) mView.findViewById(R.id.running_report_recycler);
                            runningAdapter = new RunningReportListAdapter(activityList);
                            LinearLayoutManager layoutManager2
                                    = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                            recyclerView.setLayoutManager(layoutManager2);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(runningAdapter);
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

    private void makeJsonObjectRequest(String startTime,String endTime , final ArrayList vts_id) {
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
                for (int i = 0; i <=1; i++) {
                    try {

                        total_records ++;

                        JSONObject vehicleDetails = (JSONObject) jsonArray.get(i);
                        String vehicle_no =  vehicleDetails.get("imei").toString();
                        String drive_time = vehicleDetails.get("driveTime").toString();
                        String distance_total = vehicleDetails.get("distance").toString();

                        total_distance = + Integer.parseInt(distance_total);

                        String average = vehicleDetails.get("average").toString();

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

                        activityItems = new RunningReportListItems(vehicle_no,"Initial Location", "20hrs", distance_total,"final Location", average, stoppage_time, drive_time, overspeed_duration, "");
                        activityList.add(activityItems);
                   //     Toast.makeText(getActivity(), "working....", Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        Toast.makeText(getActivity(), "location Error.", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }

                    recordValue = String.valueOf(total_records);
                    netRecord = recordValue + record ;
                    textViewTotalRecords.setText(netRecord);
                    textViewTotalDistance.setText(String.valueOf(total_distance));

                    hidepProgress();
                }
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

}