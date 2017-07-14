package com.fretron.fleet;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
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

import static com.google.android.gms.plus.internal.PlusCommonExtras.TAG;

public class ReportStoppedFragment extends Fragment implements View.OnClickListener {
    protected View mView;
    SelectedDate mSelectedDate,mSelectedDate2;
    private Orientation mOrientation;
    int mHour,mHour2, mMinute, mMinute2;
    public SQLiteDatabase sqLiteDatabase;
    Calendar calendar;
    String mRecurrenceOption, mRecurrenceRule,mRecurrenceOption2, mRecurrenceRule2;
    ProgressBar progressBar;
    String start_date_epo , end_date_epo ;
    private RecyclerView.Adapter horizontalListAdapter;
    private RecyclerView horizontalRecyclerView ;
    int position = 0, positionn = 0;
    ArrayList<String> selected_vehicles = new ArrayList<>();
    Cursor c , c2 ;
    ArrayList<String> list = new ArrayList<String>();
    List<StoppageParentListDetails> stoppageParentItemList = new ArrayList<>();
    String check;
    CheckBox[] cb = new CheckBox[1];
    ArrayList<String> current_vehicle_in_list = new ArrayList<>();
    StoppageParentAdapter mAdapter;
    Double parentTime = 0.0 , netTime = 0.0;
    TextView textViewTotalRecords,textViewTotalTime;
    String record = " Record(s)" , recordValue , netRecord;
    int total_records = 0 ;
    RecyclerView recyclerView;

    public ReportStoppedFragment() {

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
        View view = inflater.inflate(R.layout.fragment_report_stopped, container, false);
        this.mView = view;

        ((DashBoard) getActivity())
                .setActionBarTitle("Report -> Stoppage");

        Button button = (Button) mView.findViewById(R.id.stoppage_start_date_button);
        button.setOnClickListener(this);

        Button button2 = (Button) mView.findViewById(R.id.stoppage_end_date_button);
        button2.setOnClickListener(this);

        Context mContext = getActivity().getApplicationContext();

        openDatabase();
        String query2 = "SELECT * FROM vehicle_list";
        c = sqLiteDatabase.rawQuery(query2, null);
        c.moveToFirst();


        textViewTotalRecords = (TextView)mView.findViewById(R.id.running_report_total_records);
        textViewTotalTime = (TextView)mView.findViewById(R.id.running_report_total_time);

        mAdapter = new StoppageParentAdapter(stoppageParentItemList);
        recyclerView = (RecyclerView) mView.findViewById(R.id.recyclerView_stoppage_list);
        recyclerView.setLayoutManager(getLinearLayoutManager());
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        final ArrayList<String> vehicleRegistrationList = new ArrayList<>();

        if (c.moveToFirst()) {
            do {
                String vehicleRegNo = c.getString(0);
                vehicleRegistrationList.add(vehicleRegNo);

            } while (c.moveToNext());
        }
        c.close();


        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        horizontalRecyclerView = (RecyclerView)mView.findViewById(R.id.vehicle_horizontal_list);
        horizontalRecyclerView.setLayoutManager(layoutManager);

        horizontalListAdapter = new HorizontalVehicleListAdapterStopped(mContext, current_vehicle_in_list , ReportStoppedFragment.this);
        horizontalRecyclerView.setAdapter(horizontalListAdapter);

        Button mButtonAdd = (Button) mView.findViewById(R.id.button_add_vehicle);

        mButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.add_vehicle_dialog);
               // dialog.setTitle("Select your vehicle");
                final ListView listView = (ListView) dialog.findViewById(R.id.list);
                Button btn = (Button) dialog.findViewById(R.id.button_ok);

                ArrayAdapter<String> ad = new ArrayAdapter<>(getActivity(), R.layout.add_vehicle_dialog_list,
                        R.id.add_vehicle_list_item_text,vehicleRegistrationList);
                listView.setAdapter(ad);
                dialog.show();

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Displaying horizontal list aside of add button

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

                            if (stoppageParentItemList != null){
                                stoppageParentItemList.clear();
                                mAdapter = new StoppageParentAdapter(stoppageParentItemList);
                                mAdapter.notifyDataSetChanged();
                                total_records = 0 ;
                            }

                            // JSON CALL STARTS HERE
                            makeJsonObjectRequest(start_date_epo,end_date_epo,list);

                        }
                    }
                });
            }
        });


        return view;
    }

    public void makeJsonObjectRequest(final String startTime, String endTime , ArrayList vts_id) {
        showpProgress();

        String urlJsonArray = "http://35.189.189.215:8094/stoppageReport";
        BigInteger bi1 =  new BigInteger(startTime);
        BigInteger bi2 =  new BigInteger(endTime);

//        BigInteger bi1 =  new BigInteger("1496485983096");
//        BigInteger bi2 =  new BigInteger("1497695588750");

        Map<String, Object> data = new HashMap<String, Object>();
        data.put( "startTime", bi1 );
        data.put( "endTime", bi2 );
        data.put( "vehicleList", vts_id );

        Requester request = new Requester(Request.Method.POST,urlJsonArray,new JSONObject(data),new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {

                String location = "NA";
                for (int i = 0; i <= jsonArray.length(); i++) {
                    try {
                        parentTime = 0.0 ;
                        JSONObject vehicleDetails = (JSONObject) jsonArray.get(i);
                        String imei_no =  vehicleDetails.get("imei").toString();

                        StoppageParentListDetails p2 = new StoppageParentListDetails();

                        JSONArray  start_position_object = (JSONArray) vehicleDetails.get("value");
                        ArrayList<StoppageChildListDetails> childs = new ArrayList<>();
                        for (int j=0 ; j<=2;j++){

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

                            Geocoder gcd = new Geocoder(getActivity(), Locale.getDefault());
                            List<Address> start_position_string = gcd.getFromLocation(lat, lng, 1);

                            if (start_position_string.size()!=0){
                                location = start_position_string.get(0).getAddressLine(0);
                            }

                            String stoppageDuration = inner_json.getString("duration");
                            parentTime = parentTime + Double.parseDouble(stoppageDuration);

                          //  String over_speed_speed = inner_json.getString("averageSpeed");
                            StoppageChildListDetails activityItems = new StoppageChildListDetails(netDateTime,stoppageDuration + "\nmsec",location);
                            childs.add(activityItems);
                        }

                        p2.setTitle(imei_no);
                        p2.setTime(String.valueOf(parentTime)+ " msec");
                        p2.childListDetailses = childs;
                        stoppageParentItemList.add(p2);
                        total_records ++;
                        netTime = netTime + parentTime ;

                    }

                    catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }

                recordValue = String.valueOf(total_records);
                netRecord = recordValue + record ;
                textViewTotalRecords.setText(netRecord);

                textViewTotalTime.setText(String.valueOf(netTime)+" millisec");
                mAdapter = new StoppageParentAdapter(stoppageParentItemList);
                mAdapter.setActivityList(stoppageParentItemList);
                recyclerView.setLayoutManager(getLinearLayoutManager());
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(mAdapter);
                hidepProgress();
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


    private void updateInfoView(){
        if (mSelectedDate != null) {
            String y = String.valueOf(mSelectedDate.getStartDate().get(Calendar.YEAR));
            String m = String.valueOf(mSelectedDate.getStartDate().get(Calendar.MONTH)+1);
            String d = String.valueOf(mSelectedDate.getStartDate().get(Calendar.DAY_OF_MONTH));
            Button button2 = (Button) mView.findViewById(R.id.stoppage_end_date_button);
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
            Button button2 = (Button) mView.findViewById(R.id.stoppage_start_date_button);
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

            case R.id.stoppage_start_date_button:

                SublimePickerFragment pickerFrag = new SublimePickerFragment();
                pickerFrag.setCallback(mFragmentCallback2);
                pickerFrag.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
                pickerFrag.show(getFragmentManager(), "SUBLIME_PICKER");
                break;

            case R.id.stoppage_end_date_button:

                SublimePickerFragment pickerFrag2 = new SublimePickerFragment();
                pickerFrag2.setCallback(mFragmentCallback);
                pickerFrag2.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
                pickerFrag2.show(getFragmentManager(), "SUBLIME_PICKER");
                break;
        }
    }

    protected void openDatabase() {
        sqLiteDatabase = getActivity().openOrCreateDatabase("vehicle_details", Context.MODE_PRIVATE, null);
    }
    private void showpProgress() {
        LinearLayout linearLayout = (LinearLayout)mView.findViewById(R.id.magic_linear_layout3);
        progressBar = (ProgressBar)mView.findViewById(R.id.progressBar_stoppage_activity);
        linearLayout.setVisibility(View.GONE);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
    }
    private void hidepProgress() {
        LinearLayout linearLayout = (LinearLayout)mView.findViewById(R.id.magic_linear_layout3);
        progressBar = (ProgressBar)mView.findViewById(R.id.progressBar_stoppage_activity);
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
