package com.fretron.fleet.dashboard;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.fretron.fleet.ActivityListAdapter;
import com.fretron.fleet.ActivityListItems;
import com.fretron.fleet.Orientation;
import com.fretron.fleet.R;
import com.fretron.fleet.VolleyMain;
import com.fretron.fleet.dashboard.DashBoard;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.JSONException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.google.android.gms.plus.internal.PlusCommonExtras.TAG;
import static com.fretron.fleet.R.id.map;

public class DashMainFragment extends DialogFragment implements SearchView.OnQueryTextListener {
    protected View mView;
    GoogleMap googleMap;
    private MapView mapView;
    private List<ActivityListItems> activityList = new ArrayList<>();
    private ActivityListAdapter mAdapter;
    ArrayAdapter<CharSequence> arrayAdapter;
    private SQLiteDatabase sqLiteDatabase;
    private Orientation mOrientation;
    Menu menu ;
    String token,customer_id="something";
    private ProgressDialog pDialog;
    ProgressBar progressBar;
    private List<ActivityListItems> mCountryModel;
    TextView textView;

    public DashMainFragment() {
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
       // boolean mapsSupported = true;
        if (googleMap == null) {
            mapView = (MapView) getActivity().findViewById(map);
            googleMap = mapView.getMap();
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(28.38,77.12), 4));

            //setup markers etc...
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_dash_main_fragment, container, false);
        this.mView = view;

        ((DashBoard) getActivity())
                .setActionBarTitle("All Vehicles");

        deletePreviousData();
        createDatabase();

        LayoutInflater inflater2 = getActivity().getLayoutInflater();
        View myView = inflater2.inflate(R.layout.activity_content_list,container,false);

        textView = (TextView)mView.findViewById(R.id.textView_company_name);
        /*
        token = getActivity().getIntent().getExtras().getString("Token");
        assert token != null;
        String[] splits = token.split("\\.");

        String split = splits[1];
        try {
             String customerJson = getJson(split);
             JSONParser parser = new JSONParser();
             JSONObject json = (JSONObject) parser.parse(customerJson);
             customer_id = (String) json.get("customerId");

        } catch (Exception e) {
            Toast.makeText(getActivity()," Not working ",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        */

        makeJsonObjectRequest(customer_id);
       // prepareData();
        RecyclerView recyclerView = (RecyclerView) mView.findViewById(R.id.recyclerView2);
        final Context mContext = getActivity().getApplicationContext();
        mAdapter = new ActivityListAdapter(activityList,getActivity());
        recyclerView.setLayoutManager(getLinearLayoutManager());
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        final Spinner spinner = (Spinner) mView.findViewById(R.id.select_status);
        arrayAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.vehicle_status, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String query = "SELECT * FROM vehicle_list";
                String query2 = "SELECT * FROM vehicle_list WHERE status = 'moving'";
                String query3 = "SELECT * FROM vehicle_list WHERE status = 'stopped'";
                String query4 = "SELECT * FROM vehicle_list WHERE status = 'offline'";

                String selectedItem = parent.getItemAtPosition(position).toString();
                ActivityListItems activityItems;
                switch (selectedItem) {
                    case "All Vehicles": {
                        activityList.clear();
                        mAdapter.notifyDataSetChanged();
                        Cursor c = sqLiteDatabase.rawQuery(query, null);
                        c.moveToFirst();
                        if (c.moveToFirst()) {
                            do {
                                String vehicleRegNo = c.getString(0);
                                String vtsDeviceId = c.getString(1);
                                String location = c.getString(2);
                                String startTime = c.getString(3);
                                String status = c.getString(4);
                                String speed = c.getString(5);
                                activityItems = new ActivityListItems(vehicleRegNo, speed, "NA", location, status, "",vtsDeviceId);
                                activityList.add(activityItems);

                            }
                            while (c.moveToNext());
                            RecyclerView recyclerView = (RecyclerView) mView.findViewById(R.id.recyclerView2);
                            mAdapter = new ActivityListAdapter(activityList,getActivity());
                            recyclerView.setLayoutManager(getLinearLayoutManager());
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(mAdapter);
                        }
                        c.close();

                        break;
                    }
                    case "Active Vehicles": {

                        activityList.clear();
                        mAdapter.notifyDataSetChanged();
                        Cursor c = sqLiteDatabase.rawQuery(query2, null);
                        c.moveToFirst();
                        if (c.moveToFirst()) {
                            do {
                                String vehicleRegNo = c.getString(0);
                                String vtsDeviceId = c.getString(1);
                                String location = c.getString(2);
                                String startTime = c.getString(3);
                                String status = c.getString(4);
                                String speed = c.getString(5);
                                activityItems = new ActivityListItems(vehicleRegNo, speed, "NA", location, status, "",vtsDeviceId);
                                activityList.add(activityItems);

                            } while (c.moveToNext());
                            RecyclerView recyclerView = (RecyclerView) mView.findViewById(R.id.recyclerView2);
                            mAdapter = new ActivityListAdapter(activityList,getActivity());
                            recyclerView.setLayoutManager(getLinearLayoutManager());
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(mAdapter);
                        }
                        c.close();


                        break;
                    }
                    case "Offline Vehicles": {

                        activityList.clear();
                        mAdapter.notifyDataSetChanged();
                        Cursor c = sqLiteDatabase.rawQuery(query4, null);
                        c.moveToFirst();
                        if (c.moveToFirst()) {
                            do {
                                String vehicleRegNo = c.getString(0);
                                String vtsDeviceId = c.getString(1);
                                String location = c.getString(2);
                                String startTime = c.getString(3);
                                String status = c.getString(4);
                                String speed = c.getString(5);
                                activityItems = new ActivityListItems(vehicleRegNo, speed, "NA", location, status, "",vtsDeviceId);
                                activityList.add(activityItems);

                            } while (c.moveToNext());
                            RecyclerView recyclerView = (RecyclerView) mView.findViewById(R.id.recyclerView2);
                            mAdapter = new ActivityListAdapter(activityList,getActivity());
                            recyclerView.setLayoutManager(getLinearLayoutManager());
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(mAdapter);
                        }
                        c.close();


                        break;
                    }
                    case "Stopped Vehicles": {

                        activityList.clear();
                        mAdapter.notifyDataSetChanged();
                        Cursor c = sqLiteDatabase.rawQuery(query3, null);
                        c.moveToFirst();
                        if (c.moveToFirst()) {
                            do {
                                String vehicleRegNo = c.getString(0);
                                String vtsDeviceId = c.getString(1);
                                String location = c.getString(2);
                                String startTime = c.getString(3);
                                String status = c.getString(4);
                                String speed = c.getString(5);
                                activityItems = new ActivityListItems(vehicleRegNo, speed, "NA", location, status, "",vtsDeviceId);
                                activityList.add(activityItems);

                            } while (c.moveToNext());
                            RecyclerView recyclerView = (RecyclerView) mView.findViewById(R.id.recyclerView2);
                            mAdapter = new ActivityListAdapter(activityList,getActivity());
                            recyclerView.setLayoutManager(getLinearLayoutManager());
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(mAdapter);
                        }
                        c.close();


                        break;
                    }
                }

            } // to close the onItemSelected

            public void onNothingSelected(AdapterView<?> parent)
            {
                    //nothing
            }
        });

        Switch mySwitch = (Switch) mView.findViewById(R.id.switch1);
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    ViewFlipper viewFlipper = (ViewFlipper) mView.findViewById(R.id.viewFlipper2);
                    viewFlipper.setInAnimation(getActivity(), R.anim.view_transition_in_left);
                    viewFlipper.setOutAnimation(getActivity(), R.anim.view_transition_out_left);
                    viewFlipper.showNext();
                    spinner.setVisibility(View.GONE);
                    textView.setVisibility(View.VISIBLE);
                } else {
                    ViewFlipper viewFlipper = (ViewFlipper) mView.findViewById(R.id.viewFlipper2);
                    viewFlipper.setInAnimation(getActivity(), R.anim.view_transition_in_right);
                    viewFlipper.setOutAnimation(getActivity(), R.anim.view_transition_out_right);
                    viewFlipper.showPrevious();
                    spinner.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.GONE);
                }

            }
        });

        mapView = (MapView) mView.findViewById(map);
        setHasOptionsMenu(true);
        return view;
    }

    private static String getJson(String strEncoded) throws UnsupportedEncodingException {
        byte[] decodedBytes = Base64.decode(strEncoded, Base64.URL_SAFE);
        return new String(decodedBytes, "UTF-8");
    }

    private void makeJsonObjectRequest(String customer_id) {
        showpProgress();
//        String urlJsonArray = "http://35.189.162.187:7098/query?customerId="+customer_id; //Sahi karna hai yeh
        String urlJsonArray = "http://35.189.189.215:8094/dashboard";
        JsonArrayRequest jsonObjReq = new JsonArrayRequest(urlJsonArray, new Response.Listener<org.json.JSONArray>() {

            @Override
            public void onResponse(org.json.JSONArray response) {
                try {
                    ActivityListItems activityItems;
                    String location = "NA";
                    for (int i = 0; i <= response.length(); i++){
                        org.json.JSONObject vehicleDetails = (org.json.JSONObject) response.get(i);
                        String vehicle_Id = vehicleDetails.get("vehicleRegistrationNumber").toString();
                        String vtsDeviceId = vehicleDetails.get("vtsDeviceId").toString();

                        Double lat = vehicleDetails.getDouble("latitude");
                        Double lng = vehicleDetails.getDouble("longitude");
                        Geocoder gcd = new Geocoder(getActivity(), Locale.getDefault());
                        List<Address> start_position_string = gcd.getFromLocation(lat, lng, 1);;

                        if (start_position_string.size()!=0){
                            location = start_position_string.get(0).getAddressLine(0);
                        }

                   // String current_location_latitude = vehicleDetails.get("vehicleLocationLatitude").toString();
                   // String current_location_longitude = vehicleDetails.get("vehicleLocationLongitude").toString();
                   // String starting_date = vehicleDetails.get("vehicleStartDate").toString();
                        String speed = vehicleDetails.get("speed").toString();
                        int current_status = (int) vehicleDetails.get("state");
                        String status = null;

                        switch (current_status){
                            case -1 : status = "offline";
                                Marker marker = googleMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(lat,lng))
                                        .title(vehicle_Id)
                                        .icon(BitmapDescriptorFactory
                                                .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));

                                break;
                            case 0 :  status = "stopped";
                                marker = googleMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(lat,lng))
                                        .title(vehicle_Id)
                                        .icon(BitmapDescriptorFactory
                                                .defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                                break;
                            case 1 :  status = "moving";
                                break;
                            case 2 :  status = "overspeeding";
                                break;
                        }

                        activityItems = new ActivityListItems(vehicle_Id,speed,"NA",location,status,"",vtsDeviceId);
                        activityList.add(activityItems);
                        insertIntoDB2(vehicle_Id,vtsDeviceId,location,"",status,speed);

                }

                }
                 catch (JSONException | IOException e) {
                    e.printStackTrace();
                }

                hidepProgress();
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                //Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });

        VolleyMain.getInstance().addToRequestQueue(jsonObjReq);
    }

//    private void prepareData() {
//        ActivityListItems activityItems = new ActivityListItems("tr101", "22 Kmph", "201 km");
//        activityList.add(activityItems);
//
//        activityItems = new ActivityListItems("tr202", "34 Kmph", "781 km");
//        activityList.add(activityItems);
//
//        activityItems = new ActivityListItems("tr303", "54 Kmph", "281 km");
//        activityList.add(activityItems);
//
//        //mAdapter.notifyDataSetChanged();
//    }

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

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        this.menu = menu;
        inflater.inflate(R.menu.report_frag_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_search) {
            SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
            searchView.setOnQueryTextListener(this);
            //SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
            //searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            Toast.makeText(getActivity(),"Search has been disabled",Toast.LENGTH_SHORT).show();

            MenuItemCompat.setOnActionExpandListener(item,new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
            // Do something when collapsed
//                mAdapter.setFilter(mCountryModel);
                return true; // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
            // Do something when expanded
            return true; // Return true to expand action view
                }
            });

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showpProgress() {
        ViewFlipper viewFlipper = (ViewFlipper) mView.findViewById(R.id.viewFlipper2);
        progressBar = (ProgressBar)mView.findViewById(R.id.progressBar_Activity);
        viewFlipper.setVisibility(View.GONE);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hidepProgress() {
        ViewFlipper viewFlipper = (ViewFlipper) mView.findViewById(R.id.viewFlipper2);
        progressBar = (ProgressBar)mView.findViewById(R.id.progressBar_Activity);
        viewFlipper.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(false);
        progressBar.setVisibility(View.GONE);
    }

    protected void createDatabase(){
        sqLiteDatabase = getActivity().openOrCreateDatabase("vehicle_details", Context.MODE_PRIVATE, null);
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS vehicle_list(vehicleRegistrationNo VARCHAR," +
                " vtsId VARCHAR , location VARCHAR , startTime VARCHAR , status VARCHAR , speed VARCHAR);");
    }

    protected void deletePreviousData(){
        getActivity().deleteDatabase("vehicle_details");
    }

    protected void insertIntoDB2(String vehicleRegistrationNo , String vtsDeviceId
                                    , String location , String startTime , String status ,String speed){
        String query = "INSERT INTO vehicle_list (vehicleRegistrationNo,vtsId,location" +
                ",startTime,status,speed) VALUES('"+vehicleRegistrationNo+"', '"+vtsDeviceId+"'," +
                "'"+location+"','"+startTime+"','"+status+"','"+speed+"');";
        sqLiteDatabase.execSQL(query);

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
//        final List<ActivityListItems> filteredModelList = filter(mCountryModel, newText);
//        mAdapter.setFilter(filteredModelList);
        return false;
    }

//    private List<ActivityListItems> filter(List<ActivityListItems> models, String query) {
//        query = query.toLowerCase();
//        final List<ActivityListItems> filteredModelList = new ArrayList<>();
//        for (ActivityListItems model : models) {
//            final String text = model.getTitle().toLowerCase();
//            if (text.contains(query)) {
//                filteredModelList.add(model);
//            }
//        }
//        return filteredModelList;
//    }

}