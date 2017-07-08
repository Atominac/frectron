package com.fretron.fleet;


import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate;
import com.appeaser.sublimepickerlibrary.recurrencepicker.SublimeRecurrencePicker;
import com.fretron.fleet.dashboard.DashBoard;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private RecyclerView.Adapter horizontalListAdapter;
    Cursor c,c2;
    ArrayList<String> list = new ArrayList<String>();
    private RecyclerView parentRecycler , childRecycler ,horizontalRecyclerView ;
    ArrayList<String> selected_vehicles = new ArrayList<>();
    int position = 0, positionn = 0;
    List<OverspeedParentListDetails> parentItemList = new ArrayList<>();
    OverspeedChildAdapter nAdapter;
    OverspeedParentAdapter mAdapter;
    Calendar calendar;
    String check;
    CheckBox[] cb = new CheckBox[1];
    RecyclerView recyclerView;
    ArrayList<String> current_vehicle_in_list = new ArrayList<>();
    Double total_distance = 0.0 ,totalSubDistance , total_time , averageSpeed = 0.0;
    TextView textViewTotalRecords,textViewTotalDistance,textViewTotalTime;
    String record = " Record(s)" , recordValue , netRecord;
    int total_records = 0 ;

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
        View view = inflater.inflate(R.layout.fragment_report_overspeed, container, false);
        this.mView = view;

        ((DashBoard) getActivity())
                .setActionBarTitle("Report -> OverSpeeding");

        Context mContext = getActivity().getApplicationContext();

        openDatabase();
        String query2 = "SELECT * FROM vehicle_list";
        c = sqLiteDatabase.rawQuery(query2, null);
        c.moveToFirst();

        textViewTotalRecords = (TextView)mView.findViewById(R.id.running_report_total_records);
        textViewTotalDistance = (TextView)mView.findViewById(R.id.running_report_total_distance);
        textViewTotalTime = (TextView)mView.findViewById(R.id.running_report_total_time);
        mAdapter = new OverspeedParentAdapter(parentItemList);
        RecyclerView recyclerView = (RecyclerView) mView.findViewById(R.id.recyclerView_overspeed_list);
        recyclerView.setLayoutManager(getLinearLayoutManager());
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        Button button = (Button) mView.findViewById(R.id.overspeeding_start_date_button);
        button.setOnClickListener(this);

        Button button2 = (Button) mView.findViewById(R.id.overspeeding_end_date_button);
        button2.setOnClickListener(this);

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

        horizontalListAdapter = new HorizontalVehicleListAdapterOverspeed(mContext, current_vehicle_in_list , ReportOverspeedFragment.this);
        horizontalRecyclerView.setAdapter(horizontalListAdapter);

        Button mButtonAdd = (Button) mView.findViewById(R.id.button_add_vehicle);

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


                            // JSON CALL STARTS HERE
                            makeJsonObjectRequest(start_date_epo,end_date_epo,list);
                        }
                    }
                });
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
            calendar.set(mSelectedDate.getStartDate().get(Calendar.YEAR), mSelectedDate.getStartDate().get(Calendar.MONTH)+1, mSelectedDate.getStartDate().get(Calendar.DAY_OF_MONTH),
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
            calendar.set(mSelectedDate2.getStartDate().get(Calendar.YEAR), mSelectedDate2.getStartDate().get(Calendar.MONTH)+1, mSelectedDate2.getStartDate().get(Calendar.DAY_OF_MONTH),
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
        Map<String, Object> data = new HashMap<String, Object>();
        data.put( "startTime", bi1 );
        data.put( "endTime", bi2 );
        data.put( "vehicleList", vts_id );

        Requester request = new Requester(Request.Method.POST,urlJsonArray,new JSONObject(data),new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {

                for (int i = 0; i <= jsonArray.length(); i++) {
                    try {
                        totalSubDistance = 0.0;
                        JSONObject vehicleDetails = (JSONObject) jsonArray.get(i);
                        String imei_no =  vehicleDetails.get("imei").toString();

                        OverspeedParentListDetails p = new OverspeedParentListDetails();

                        JSONArray  start_position_object = (JSONArray) vehicleDetails.get("value");
                        ArrayList<OverspeedingChildListDetails> childs = new ArrayList<>();
                        for (int j=0 ; j<=4;j++){

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


                            String over_speed_duration = inner_json.getString("duration");
                            String over_speed_speed = inner_json.getString("averageSpeed");


                            averageSpeed= Math.round((Double.parseDouble(over_speed_speed)/1000) * 100.0) / 100.0;

                            Double calculatedDistance = Double.parseDouble(over_speed_duration)*Double.parseDouble(over_speed_speed);
                            total_distance = (total_distance + calculatedDistance)/1000;
                            totalSubDistance = (totalSubDistance + calculatedDistance)/1000;

                            OverspeedingChildListDetails activityItems = new OverspeedingChildListDetails(netDateTime,over_speed_duration,String.valueOf(averageSpeed)+ "\nKmph");
                            childs.add(activityItems);
                        }

                        p.setSpeed("N-A-");
                        p.setTitle(imei_no);
                        p.setDistance(String.valueOf(Math.round(totalSubDistance * 100.0) / 100.0));
                        p.childListDetailses = childs;
                        parentItemList.add(p);
                        total_records ++;
                    }

                    catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                recordValue = String.valueOf(total_records);
                netRecord = recordValue + record ;
                textViewTotalRecords.setText(netRecord);
                textViewTotalDistance.setText(String.valueOf(Math.round(total_distance * 100.0) / 100.0));
                hidepProgress();
                mAdapter.setActivityList(parentItemList);
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

}
