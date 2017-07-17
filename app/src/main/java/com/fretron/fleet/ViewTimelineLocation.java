package com.fretron.fleet;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate;
import com.fretron.fleet.dashboard.DashBoard;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import static com.google.android.gms.plus.internal.PlusCommonExtras.TAG;
import static com.fretron.fleet.R.id.map2;

public class ViewTimelineLocation extends Fragment {
    protected View mView;
    GoogleMap googleMap;
    private MapView mapView;
    private RecyclerView mRecyclerView;
    List<TimeLineModel> mDataList = new ArrayList<>();
    private Orientation mOrientation;
    private boolean mWithLinePadding;
    Context context;
    Button button;
    ImageButton previousDate , nextDate ;
    int mYear,mMonth,mDay;
    String vehicle_Id,vehicle_name;
    ProgressBar progressBar;
    CoordinatorLayout coordinatorLayout;
    String start_date_epo , end_date_epo ;
    private UserLoginTask mAuthTask = null;
    String startPosition = "";
    Calendar calendar ;
    TimeLineAdapter mTimeLineAdapter ;
    TimeLineModel timeLineModel ;
    TextView noText ;
    private ProgressDialog pDialog;

    public ViewTimelineLocation() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapsInitializer.initialize(getActivity());

        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
        }
        initializeMap();
    }

    private void initializeMap() {
        boolean mapsSupported = true;
        if (googleMap == null && mapsSupported) {
            mapView = (MapView) getActivity().findViewById(map2);
            googleMap = mapView.getMap();
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            //setup markers etc...
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vehicle_Id=getArguments().getString("vtsVehicleId");
        vehicle_name = getArguments().getString("NAME");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_view_timeline_location, container, false);
        this.mView = view;

        ((DashBoard) getActivity())
                .setActionBarTitle(vehicle_name);

        coordinatorLayout = (CoordinatorLayout) mView.findViewById(R.id.coordinate_layout_timeline);
        progressBar = (ProgressBar)mView.findViewById(R.id.progressBar_location_history);
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recyclerView);
        button = (Button)mView.findViewById(R.id.location_date_selector_button);
        noText = (TextView) mView.findViewById(R.id.no_data_image);

        pDialog = new ProgressDialog(mView.getContext());
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        button.setText(
                new StringBuilder()
                        // Month is 0 based so add 1
                        .append(mDay).append("/")
                        .append(mMonth + 1).append("/")
                        .append(mYear).append(" "));

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        calendar.set(mYear, mMonth , mDay, 0,0,0);
        long startTime = calendar.getTimeInMillis();
       // start_date_epo = Long.toString(startTime);

        Calendar calendar2 = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        calendar2.set(mYear, mMonth , mDay, 23,59, 59);
        long endTime = calendar2.getTimeInMillis();
       // end_date_epo = Long.toString(endTime);

        start_date_epo = "1499140800911" ;
        end_date_epo = "1499227199911" ;
        makeJsonObjectRequest(start_date_epo, end_date_epo ,vehicle_Id);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateDialog pickerFrag = new DateDialog(ViewTimelineLocation.this);
                pickerFrag.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
                pickerFrag.show(getFragmentManager(), "DATE_PICKER");


            }
        });

        previousDate = (ImageButton)mView.findViewById(R.id.date_previous_button);
        previousDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDataList.clear();
                mTimeLineAdapter.notifyDataSetChanged();
                String string = button.getText().toString();
                String[] parts = string.split("/");
                int day = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);
                String year_String = parts[2];
                String repalce_space = year_String.replaceAll("\\s+","");
                int year = Integer.parseInt(repalce_space);
                // Toast.makeText(getActivity(),date + month + year , Toast.LENGTH_LONG ).show();

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
                calendar.set(year, month-1, day-1, 0,0,0);
                long startTime = calendar.getTimeInMillis();
                String selected_startTime = Long.toString(startTime);


                calendar.set(year, month-1, day-1, 23,59, 59);
                long endTime = calendar.getTimeInMillis();
                String selected_endTime = Long.toString(endTime);
                button.setText(day-1+"/"+month+"/"+year);
                makeJsonObjectRequest(selected_startTime,selected_endTime,vehicle_Id);

            }
        });

        nextDate = (ImageButton)mView.findViewById(R.id.date_next_button);
        nextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDataList.clear();
                mTimeLineAdapter.notifyDataSetChanged();
                String string = button.getText().toString();
                String[] parts = string.split("/");
                int day = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);
                String year_String = parts[2];
                String repalce_space = year_String.replaceAll("\\s+","");
                int year = Integer.parseInt(repalce_space);
                // Toast.makeText(getActivity(),date + month + year , Toast.LENGTH_LONG ).show();

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
                calendar.set(year, month, day+1, 0,0,0);
                long startTime = calendar.getTimeInMillis();
                String selected_startTime = Long.toString(startTime);

                calendar.set(year, month, day+1, 23,59, 59);
                long endTime = calendar.getTimeInMillis();
                String selected_endTime = Long.toString(endTime);
                button.setText(day+1+"/"+month+"/"+year);
                makeJsonObjectRequest(selected_startTime,selected_endTime,vehicle_Id);

            }
        });

        mapView = (MapView) mView.findViewById(map2);
        setHasOptionsMenu(true);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        initializeMap();
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

                        LocationFragment fragment = new LocationFragment();
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

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private LinearLayoutManager getLinearLayoutManager() {
        if (mOrientation == Orientation.HORIZONTAL) {
            return new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        } else {
            return new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        }
    }

    public class UserLoginTask extends AsyncTask<String, Void, String[]> {
        private  Double lat , lng ;
        private  String date ;

        UserLoginTask(Double latitude , Double longitude , String dateTime) {
            lat = latitude ;
            lng = longitude ;
            date = dateTime ;
        }

        @Override
        protected String[] doInBackground(String... params) {
            String[] arr = new String[2];
            // TODO: attempt authentication against a network service.
            try {
                Geocoder gcd = new Geocoder(getActivity(), Locale.getDefault());
                List<Address> start_position_string = null;
                start_position_string = gcd.getFromLocation(lat, lng, 1);
                startPosition = "Unknown Location" ;
                if (start_position_string.size()!=0){
                    startPosition = start_position_string.get(0).getAddressLine(0);
                }

                calendar = Calendar.getInstance();
                calendar.setTimeInMillis(Long.parseLong(date));

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

                arr[0] = startPosition ;
                arr[1]= netDateTime ;

            } catch (IOException e) {
                e.printStackTrace();
            }

            return arr;
        }

        @Override
        protected void onPreExecute() {
            showProgress();
        }

        @Override
        protected void onPostExecute(String[] arr) {
            mAuthTask = null;
            timeLineModel = new TimeLineModel(arr[0], arr[1], OrderStatus.COMPLETED);
            mDataList.add(timeLineModel);
            mTimeLineAdapter = new TimeLineAdapter(mDataList, mOrientation, mWithLinePadding);
            mRecyclerView.scrollToPosition(mDataList.size());
            mRecyclerView.setLayoutManager(getLinearLayoutManager());
            mRecyclerView.setAdapter(mTimeLineAdapter);
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }

    public void makeJsonObjectRequest(String startTime,String endTime , String vts_id) {

        String urlJsonArray = "http://35.189.189.215:8094/timeLine";
//        BigInteger bi1 =  new BigInteger("1499140800911");
//        BigInteger bi2 =  new BigInteger("1499227199911");

        BigInteger bi1 =  new BigInteger(startTime);
        BigInteger bi2 =  new BigInteger(endTime);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put( "startTime", bi1 );
        data.put( "endTime", bi2 );
        data.put( "vehicle", vts_id );

        Requester request = new Requester(Request.Method.POST,urlJsonArray,new JSONObject(data),new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (int i = 0; i <=jsonArray.length(); i++) {
                    try {
                        JSONObject vehicleDetails = (JSONObject) jsonArray.get(i);

                        JSONObject  start_position_object = (JSONObject) vehicleDetails.get("startPosition");
                        Double lat = start_position_object.getDouble("latitude");
                        Double lng = start_position_object.getDouble("longitude");

                        JSONObject  end_position_object = (JSONObject) vehicleDetails.get("endPosition");
                        Double endlat = end_position_object.getDouble("latitude");
                        Double endlng = end_position_object.getDouble("longitude");


                        Polyline line = googleMap.addPolyline(new PolylineOptions()
                                .add(new LatLng(lat, lng), new LatLng(endlat, endlng))
                                .width(5)
                                .color(Color.BLUE));

                        Marker marker = googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(lat, lng))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));


                        builder.include(marker.getPosition());

                        String dateTime = vehicleDetails.getString("startTime");
                        mAuthTask = new UserLoginTask(lat,lng,dateTime);
                        mAuthTask.execute((String) null);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                LatLngBounds bounds = builder.build();
                int padding = 0;
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                googleMap.moveCamera(cu);
                googleMap.animateCamera(cu);
                hideProgress();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                timeLineModel = new TimeLineModel(("no data"), ("no data") , OrderStatus.COMPLETED);
                mDataList.add(timeLineModel);
                mTimeLineAdapter = new TimeLineAdapter(mDataList, mOrientation, mWithLinePadding);
                mRecyclerView.setAdapter(mTimeLineAdapter);
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

    public void showProgress(){
        coordinatorLayout.setVisibility(View.GONE);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgress(){
        coordinatorLayout.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(false);
        progressBar.setVisibility(View.GONE);
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


}
