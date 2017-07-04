package com.sample.project_frectron;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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
import static com.sample.project_frectron.R.id.map2;
import static com.sample.project_frectron.R.id.visible;

public class ViewTimelineLocation extends Fragment {
    protected View mView;
    GoogleMap googleMap;
    private MapView mapView;
    Menu menu ;
    SelectedDate mSelectedDate;
    private RecyclerView vehicleList_recyclerView , mRecyclerView;
    private TimeLineAdapter mTimeLineAdapter;
    private List<TimeLineModel> mDataList = new ArrayList<>();
    ArrayList<String> vehicle_list = new ArrayList<>();
    private Orientation mOrientation;
    private boolean mWithLinePadding;
    Context context;
    Button button;
    ImageButton previousDate , nextDate ;
    int mYear,mMonth,mDay;
    String vehicle_Id,vehicle_name;
    ProgressBar progressBar;
    CoordinatorLayout coordinatorLayout;
    NestedScrollView nestedScrollView;
    String start_date_epo , end_date_epo ;

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
            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(28.452839, 77.069670))
                    .title("Vehicle Locations")
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(28.452839,77.069670), 15));

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
        nestedScrollView = (NestedScrollView)mView.findViewById(R.id.nestedScroll_timeline);
        button = (Button)mView.findViewById(R.id.location_date_selector_button);

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
        calendar.set(mYear, mMonth, mDay, 0,0,0);
        long startTime = calendar.getTimeInMillis();
        start_date_epo = Long.toString(startTime);

        Calendar calendar2 = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        calendar2.set(mYear, mMonth, mDay, 23,59, 59);
        long endTime = calendar2.getTimeInMillis();
        end_date_epo = Long.toString(endTime);


       Date d =  new Date();
        d.setHours(0);
        d.setMinutes(0);
        d.setSeconds(0);
        Long start = d.getTime();

        d.setHours(23);
        d.setMinutes(59);
        d.setSeconds(59);
        Long end = d.getTime();

        Log.d("sldf" , "" +  start + ".............." + end);

        //initView();

        makeJsonObjectRequest("" + start, "" + end,vehicle_Id);

//        LinearLayoutManager layoutManager
//                = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        //  mRecyclerView.setLayoutManager(layoutManager);
//        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recyclerView);
//        mRecyclerView.setLayoutManager(layoutManager);
//        mRecyclerView.setHasFixedSize(true);
//       // mDataList.add(new TimeLineModel("Huda city center", "2017-02-12 08:00", OrderStatus.COMPLETED));
//        mTimeLineAdapter = new TimeLineAdapter(mDataList, mOrientation, mWithLinePadding);
//        mRecyclerView.setAdapter(mTimeLineAdapter);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateDialog pickerFrag = new DateDialog();
               // pickerFrag.setCallback(mFragmentCallback2);
                pickerFrag.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
                pickerFrag.show(getFragmentManager(), "DATE_PICKER");
            }
        });

        previousDate = (ImageButton)mView.findViewById(R.id.date_previous_button);
        previousDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        nextDate = (ImageButton)mView.findViewById(R.id.date_next_button);
        nextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

//    private void initView() {
//        setDataListItems();
//        mTimeLineAdapter = new TimeLineAdapter(mDataList, mOrientation, mWithLinePadding);
//        mRecyclerView.setAdapter(mTimeLineAdapter);
//    }
//
//    private void setDataListItems(){
//        mDataList.add(new TimeLineModel("Huda city center", "2017-02-12 08:00", OrderStatus.COMPLETED));
//        mDataList.add(new TimeLineModel("MG Road metro", "2017-02-13 09:00", OrderStatus.COMPLETED));
//        mDataList.add(new TimeLineModel("Sultanpur", "2017-02-15 10:00", OrderStatus.COMPLETED));
//        mDataList.add(new TimeLineModel("Hauz khaz", "2017-02-17 18:00", OrderStatus.COMPLETED));
//        mDataList.add(new TimeLineModel("India Gate", "2017-02-18 09:30", OrderStatus.COMPLETED));
//        mDataList.add(new TimeLineModel("Anad vihar ISBT", "2017-02-20 08:00", OrderStatus.COMPLETED));
//        mDataList.add(new TimeLineModel("Vaishali Ghaziabad", "2017-02-22 15:00", OrderStatus.COMPLETED));
//        mDataList.add(new TimeLineModel("Lucknow", "2017-02-24 14:30", OrderStatus.COMPLETED));
//        mDataList.add(new TimeLineModel("Gorakhpur", "2017-02-27 14:00", OrderStatus.ACTIVE));
//    }

    private void makeJsonObjectRequest(String startTime,String endTime , String vts_id) {
        showpProgress();

        String urlJsonArray = "http://35.189.189.215:8094/timeLine";
        BigInteger bi1 =  new BigInteger("1499140800911");
        BigInteger bi2 =  new BigInteger("1499227199911");

        vts_id = "1234";

        Map<String, Object> data = new HashMap<String, Object>();
        data.put( "startTime", bi1 );
        data.put( "endTime", bi2 );
        data.put( "vehicle", vts_id );

        Requester request = new Requester(Request.Method.POST,urlJsonArray,new JSONObject(data),new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {

                for (int i = 0; i <=8; i++) {
                    try {
                        TimeLineModel timeLineModel ;
                        JSONObject vehicleDetails = (JSONObject) jsonArray.get(i);

                        JSONObject  start_position_object = (JSONObject) vehicleDetails.get("startPosition");
                        Double lat = start_position_object.getDouble("latitude");
                        Double lng = start_position_object.getDouble("longitude");
                        Geocoder gcd = new Geocoder(getActivity(), Locale.getDefault());
                        List<Address> start_position_string = gcd.getFromLocation(lat, lng, 1);;
                        String startPosition = start_position_string.get(0).getAddressLine(0);

//                        JSONObject  end_position_object = (JSONObject) vehicleDetails.get("endPosition");
//                        Double lat2 = end_position_object.getDouble("lat");
//                        Double lng2 = end_position_object.getDouble("long");
//                        Geocoder gcd2 = new Geocoder(getActivity(), Locale.getDefault());
//                        List<Address> end_position_string = gcd2.getFromLocation(lat2, lng2, 1);
//
//                        String endPo = end_position_string.get(0).getAddressLine(0);

                        timeLineModel = new TimeLineModel(startPosition, "2017-02-12 08:00", OrderStatus.COMPLETED);
                        mDataList.add(timeLineModel);

                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }

                    LinearLayoutManager layoutManager
                            = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                    mRecyclerView = (RecyclerView) mView.findViewById(R.id.recyclerView);
                    mRecyclerView.setLayoutManager(layoutManager);
                    mRecyclerView.setHasFixedSize(true);
                    // mDataList.add(new TimeLineModel("Huda city center", "2017-02-12 08:00", OrderStatus.COMPLETED));
                    mTimeLineAdapter = new TimeLineAdapter(mDataList, mOrientation, mWithLinePadding);
                    mRecyclerView.setAdapter(mTimeLineAdapter);

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
        //coordinatorLayout.setVisibility(View.GONE);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
    }
    private void hidepProgress() {
        coordinatorLayout.setVisibility(View.VISIBLE);
        nestedScrollView.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(false);
        progressBar.setVisibility(View.GONE);
    }

}
