package com.sample.project_frectron;


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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate;
import com.appeaser.sublimepickerlibrary.recurrencepicker.SublimeRecurrencePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.sql.Array;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
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
    private SQLiteDatabase sqLiteDatabase;
    private RecyclerView.Adapter horizontalListAdapter;
    Cursor c;
    private RecyclerView parentRecycler , childRecycler ,horizontalRecyclerView ;
    ArrayList<String> selected_vehicles = new ArrayList<>();
    int position = 0, positionn = 0;
    private List<OverspeedParentListDetails> parentItemList = new ArrayList<>();
    OverspeedChildAdapter nAdapter;
    OverspeedParentAdapter mAdapter;
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

        int size_count = 0;
        if (c.moveToFirst()) {
            do {
                String vehicleRegNo = c.getString(0);
                //String vtsDeviceId = c.getString(1);
                vehicleRegistrationList.add(vehicleRegNo);
                size_count++;

            } while (c.moveToNext());
        }
        c.close();

        final ArrayList<String> current_vehicle_in_list = new ArrayList<>(size_count);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        horizontalRecyclerView = (RecyclerView)mView.findViewById(R.id.vehicle_horizontal_list);
        horizontalRecyclerView.setLayoutManager(layoutManager);

        horizontalListAdapter = new HorizontalVehicleListAdapter(mContext, current_vehicle_in_list);
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

                int size = vehicleRegistrationList.size();

                if (position <= size) {
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
                }

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (selected_vehicles.isEmpty()){
                            Toast.makeText(getActivity(),"Please select a vehicle",Toast.LENGTH_LONG).show();
                        }

                        else {
                            dialog.dismiss();
                            while (positionn < selected_vehicles.size()) {
                                current_vehicle_in_list.add(positionn, selected_vehicles.get(position));
                                horizontalListAdapter.notifyItemInserted(position);
                                position++;
                                positionn++;
                            }
                            horizontalRecyclerView.scrollToPosition(position);
                            //buttom recycler list starts here
                            final ArrayList<String> list = new ArrayList<>();
                            list.add("1234");
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
            Toast.makeText(getActivity(),end_date_epo,Toast.LENGTH_LONG).show();
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
            Toast.makeText(getActivity(),start_date_epo,Toast.LENGTH_LONG).show();
        }

    }

    protected void openDatabase() {
        sqLiteDatabase = getActivity().openOrCreateDatabase("vehicle_details", Context.MODE_PRIVATE, null);
    }


    private void makeJsonObjectRequest(String startTime,String endTime , final ArrayList vts_id) {
        showpProgress();

        String urlJsonArray = "http://35.189.162.187:7074/reports/overspeed";
        BigInteger bi1 =  new BigInteger("1496485983096");
        BigInteger bi2 =  new BigInteger("1497695588750");
        Map<String, Object> data = new HashMap<String, Object>();
        data.put( "startTime", bi1 );
        data.put( "endTime", bi2 );
        data.put( "vehicleList", vts_id );

        Requester request = new Requester(Request.Method.POST,urlJsonArray,new JSONObject(data),new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {


                for (int i = 0; i <= jsonArray.length(); i++) {
                    try {
                        JSONObject vehicleDetails = (JSONObject) jsonArray.get(i);
                        String imei_no =  vehicleDetails.get("imei").toString();

                        OverspeedParentListDetails p = new OverspeedParentListDetails();
                        p.setDistance("200km");
                        p.setSpeed("20Kmph");
                        p.setTitle("TR101");

                        JSONArray  start_position_object = (JSONArray) vehicleDetails.get("value");
                        ArrayList<OverspeedingChildListDetails> childs = new ArrayList<>();
                        for (int j=0 ; j<=4;j++){

                            JSONObject inner_json = (JSONObject) start_position_object.get(j);
                            String over_speed_start_time = inner_json.getString("startTime");
                            String over_speed_duration = inner_json.getString("duration");
                            String over_speed_speed = inner_json.getString("averageSpeed");
                            OverspeedingChildListDetails activityItems = new OverspeedingChildListDetails(over_speed_start_time,over_speed_duration,over_speed_speed);
                            childs.add(activityItems);
                        }

                        p.childListDetailses = childs;
                        parentItemList.add(p);
                        Toast.makeText(getActivity(), imei_no , Toast.LENGTH_SHORT).show();
                    }

                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                    hidepProgress();
                }

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
