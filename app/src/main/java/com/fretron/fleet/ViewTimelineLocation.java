package com.fretron.fleet;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
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

import static com.fretron.fleet.R.id.start;
import static com.google.android.gms.plus.internal.PlusCommonExtras.TAG;
import static com.fretron.fleet.R.id.map2;

public class ViewTimelineLocation extends Fragment  implements View.OnClickListener {
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
    public AsyncTask<String , Void , String> myGeocoderTask ;
    String startPosition = "";
    Calendar calendar ;
    TimeLineAdapter mTimeLineAdapter ;
    TimeLineModel timeLineModel ;
    TextView noText ;
    private ProgressDialog pDialog;
    Marker marker ;
    Polyline line ;
    LinearLayoutManager linearLayoutManager;
    AppBarLayout appBarLayout ;
    int recyclerIndex ;
    List<Address> start_position_string = null;

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
        appBarLayout = (AppBarLayout)mView.findViewById(R.id.appBar);
        if (appBarLayout.getLayoutParams() != null) {
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
            AppBarLayout.Behavior appBarLayoutBehaviour = new AppBarLayout.Behavior();
            appBarLayoutBehaviour.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
                @Override
                public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                    return false;
                }
            });
            layoutParams.setBehavior(appBarLayoutBehaviour);
        }

        progressBar = (ProgressBar)mView.findViewById(R.id.progressBar_location_history);
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recyclerView);
        button = (Button)mView.findViewById(R.id.location_date_selector_button);
        button.setOnClickListener(this);
        noText = (TextView) mView.findViewById(R.id.no_data_image);

        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

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
        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
        calendar.set(mYear, mMonth , mDay, 0,0,0);
        long startTime = calendar.getTimeInMillis();
        start_date_epo = Long.toString(startTime);

        Calendar calendar2 = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
        calendar2.set(mYear, mMonth , mDay, 23,59, 59);
        long endTime = calendar2.getTimeInMillis();
        end_date_epo = Long.toString(endTime);

//        start_date_epo = "1499140800911" ;
//        end_date_epo = "1499227199911" ;
        makeJsonObjectRequest(start_date_epo, end_date_epo ,vehicle_Id);
//
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                DateDialog pickerFrag = new DateDialog(ViewTimelineLocation.this);
//                pickerFrag.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
//                pickerFrag.show(getFragmentManager(), "DATE_PICKER");
//
//
//            }
//        });

        previousDate = (ImageButton)mView.findViewById(R.id.date_previous_button);
        previousDate.setOnClickListener(this);
//        previousDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                mAuthTask.cancel(true);
//                mDataList.clear();
//                mTimeLineAdapter.notifyDataSetChanged();
//                String string = button.getText().toString();
//                String[] parts = string.split("/");
//                int day = Integer.parseInt(parts[0]);
//                int month = Integer.parseInt(parts[1]);
//                String year_String = parts[2];
//                String repalce_space = year_String.replaceAll("\\s+","");
//                int year = Integer.parseInt(repalce_space);
//                // Toast.makeText(getActivity(),date + month + year , Toast.LENGTH_LONG ).show();
//
//                Calendar calendar = Calendar.getInstance();
//                calendar.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
//                calendar.set(year, month-1, day-1, 0,0,0);
//                long startTime = calendar.getTimeInMillis();
//                String selected_startTime = Long.toString(startTime);
//
//
//                calendar.set(year, month-1, day-1, 23,59, 59);
//                long endTime = calendar.getTimeInMillis();
//                String selected_endTime = Long.toString(endTime);
//                button.setText(day-1+"/"+month+"/"+year);
//                makeJsonObjectRequest(selected_startTime,selected_endTime,vehicle_Id);
//
//            }
//        });

        nextDate = (ImageButton)mView.findViewById(R.id.date_next_button);
        nextDate.setOnClickListener(this);
//        nextDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                mDataList.clear();
//                mTimeLineAdapter.notifyDataSetChanged();
//                String string = button.getText().toString();
//                String[] parts = string.split("/");
//                int day = Integer.parseInt(parts[0]);
//                int month = Integer.parseInt(parts[1]);
//                String year_String = parts[2];
//                String repalce_space = year_String.replaceAll("\\s+","");
//                int year = Integer.parseInt(repalce_space);
//                // Toast.makeText(getActivity(),date + month + year , Toast.LENGTH_LONG ).show();
//
//                Calendar calendar = Calendar.getInstance();
//                calendar.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
//                calendar.set(year, month, day+1, 0,0,0);
//                long startTime = calendar.getTimeInMillis();
//                String selected_startTime = Long.toString(startTime);
//
//                calendar.set(year, month, day+1, 23,59, 59);
//                long endTime = calendar.getTimeInMillis();
//                String selected_endTime = Long.toString(endTime);
//                button.setText(day+1+"/"+month+"/"+year);
//                makeJsonObjectRequest(selected_startTime,selected_endTime,vehicle_Id);
//
//            }
//        });

        mapView = (MapView) mView.findViewById(map2);
        setHasOptionsMenu(true);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.location_date_selector_button:
                DateDialog pickerFrag = new DateDialog(ViewTimelineLocation.this);
                pickerFrag.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
                pickerFrag.show(getFragmentManager(), "DATE_PICKER");
                break;

            case R.id.date_previous_button:
                if(myGeocoderTask != null && !myGeocoderTask.isCancelled())
                    myGeocoderTask.cancel(true);
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
                break;

            case R.id.date_next_button:
                if(myGeocoderTask != null && !myGeocoderTask.isCancelled())
                    myGeocoderTask.cancel(true);
                mDataList.clear();
                mTimeLineAdapter.notifyDataSetChanged();
                String string2 = button.getText().toString();
                String[] parts2 = string2.split("/");
                int day2 = Integer.parseInt(parts2[0]);
                int month2 = Integer.parseInt(parts2[1]);
                String year_String2 = parts2[2];
                String repalce_space2 = year_String2.replaceAll("\\s+","");
                int year2 = Integer.parseInt(repalce_space2);
                // Toast.makeText(getActivity(),date + month + year , Toast.LENGTH_LONG ).show();

                Calendar calendar2 = Calendar.getInstance();
                calendar2.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
                calendar2.set(year2, month2, day2+1, 0,0,0);
                long startTime2 = calendar2.getTimeInMillis();
                String selected_startTime2 = Long.toString(startTime2);

                calendar2.set(year2, month2, day2+1, 23,59, 59);
                long endTime2 = calendar2.getTimeInMillis();
                String selected_endTime2 = Long.toString(endTime2);
                button.setText(day2+1+"/"+month2+"/"+year2);
                makeJsonObjectRequest(selected_startTime2,selected_endTime2,vehicle_Id);


        }
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

    private class UserLoginTask extends AsyncTask<String, Void, String> {
        private  double[] latArray , longArray ;

        UserLoginTask(double[] laArray , double[] lnArray) {
            latArray = laArray ;
            longArray = lnArray ;
            //mAuthTask = UserLoginTask.this;
        }

        @Override
        protected String doInBackground(String... params) {

            if (!isCancelled()) {
                try {

                    Geocoder gcd = new Geocoder(getActivity(), Locale.getDefault());

                    for (recyclerIndex = 0; recyclerIndex < latArray.length - 1; recyclerIndex++) {
                        start_position_string = gcd.getFromLocation(latArray[recyclerIndex], longArray[recyclerIndex], 1);
                        if (start_position_string.size() != 0) {
                            String city = start_position_string.get(0).getLocality();
                            String state = start_position_string.get(0).getAdminArea();
                            startPosition = start_position_string.get(0).getAddressLine(0) + "\n" +
                                    city + "\n" + state;
                        }
                        else
                            startPosition = "Unknown Location";

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                               try {
                                   mDataList.get(recyclerIndex).setMessage(startPosition);
                                   mTimeLineAdapter.notifyDataSetChanged();
                               }catch (Exception e){
                                   Log.w("AsynkTask : " , e.getMessage());
                               }
                            }
                        });

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            showProgress();


        }

        @Override
        protected void onCancelled() {
            mAuthTask.cancel(true);
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

                double[] latArray = new double[jsonArray.length()];
                double[] longArray = new double[jsonArray.length()] ;

                int arrayIndex = 0 ;

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


                        line = googleMap.addPolyline(new PolylineOptions()
                                .add(new LatLng(lat, lng), new LatLng(endlat, endlng))
                                .width(5)
                                .color(Color.BLUE));

                        marker = googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(lat, lng))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                        builder.include(marker.getPosition());

                        String dateTime = vehicleDetails.getString("startTime");

                        calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(Long.parseLong(dateTime));

                        int yy = calendar.get(Calendar.YEAR);
                        int mm = calendar.get(Calendar.MONTH)+ 1 ;
                        int dd = calendar.get(Calendar.DAY_OF_MONTH);
                        int hh = calendar.get(Calendar.HOUR_OF_DAY);
                        int mi = calendar.get(Calendar.MINUTE);

                        String netDateTime = String.valueOf(dd)+ "/" +
                                String.valueOf(mm)+  "/" +
                                String.valueOf(yy)+  " " +
                                String.valueOf(hh)+  ":" +
                                String.valueOf(mi);

                        timeLineModel = new TimeLineModel("Location not available", netDateTime , OrderStatus.COMPLETED);
                        mDataList.add(timeLineModel);


                        latArray[arrayIndex] = lat ;
                        longArray[arrayIndex]= lng ;

                        arrayIndex++;

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


                mAuthTask = new UserLoginTask(latArray,longArray);
                myGeocoderTask  =  mAuthTask.execute((String) null);
                LatLngBounds bounds = builder.build();
                int padding = 0;
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                googleMap.moveCamera(cu);
                googleMap.animateCamera(cu);
                mTimeLineAdapter = new TimeLineAdapter(mDataList, mOrientation, mWithLinePadding);
                mRecyclerView.scrollToPosition(mDataList.size());
                mRecyclerView.setLayoutManager(linearLayoutManager);
                mRecyclerView.setAdapter(mTimeLineAdapter);

                hideProgress();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                timeLineModel = new TimeLineModel(("no data"), ("no data") , OrderStatus.COMPLETED);
                mDataList.add(timeLineModel);
                mTimeLineAdapter = new TimeLineAdapter(mDataList, mOrientation, mWithLinePadding);
                mRecyclerView.setAdapter(mTimeLineAdapter);
                googleMap.clear();
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(28.38, 77.12), 4));
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hideProgress();
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
