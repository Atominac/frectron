package com.sample.project_frectron;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ViewFlipper;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.JSONException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.plus.internal.PlusCommonExtras.TAG;
import static com.sample.project_frectron.R.id.map;

public class DashMainFragment extends DialogFragment {
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
        boolean mapsSupported = true;
        if (googleMap == null && mapsSupported) {
            mapView = (MapView) getActivity().findViewById(map);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_dash_main_fragment, container, false);
        this.mView = view;

        ((DashBoard) getActivity())
                .setActionBarTitle("All Vehicles");

        deletePreviousData();
        createDatabase();

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
        mAdapter = new ActivityListAdapter(activityList);
        recyclerView.setLayoutManager(getLinearLayoutManager());
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        Spinner spinner = (Spinner) mView.findViewById(R.id.select_status);
        arrayAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.vehicle_status, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

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
                } else {
                    ViewFlipper viewFlipper = (ViewFlipper) mView.findViewById(R.id.viewFlipper2);
                    viewFlipper.setInAnimation(getActivity(), R.anim.view_transition_in_right);
                    viewFlipper.setOutAnimation(getActivity(), R.anim.view_transition_out_right);
                    viewFlipper.showPrevious();
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
                    for (int i = 0; i <= response.length(); i++){
                    org.json.JSONObject vehicleDetails = (org.json.JSONObject) response.get(i);
                    String vehicle_Id = vehicleDetails.get("vehicleRegistrationNumber").toString();
                    String vtsDeviceId = vehicleDetails.get("vtsDeviceId").toString();
                    //String current_location_latitude = vehicleDetails.get("vehicleLocationLatitude").toString();
                    //String current_location_longitude = vehicleDetails.get("vehicleLocationLongitude").toString();
                    //String starting_date = vehicleDetails.get("vehicleStartDate").toString();
                    //String current_status = vehicleDetails.get("vehicleStatus").toString();

                        activityItems = new ActivityListItems(vehicle_Id,"NA","NA","","","");
                        activityList.add(activityItems);
                        insertIntoDB2(vehicle_Id,vtsDeviceId);

                }

                }
                 catch (JSONException e) {
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
            //Toast.makeText(getActivity(), "got it", Toast.LENGTH_LONG).show();
            SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
            //SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
            //searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
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
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS vehicle_list(vehicleRegistrationNo VARCHAR, vtsId VARCHAR);");
    }

    protected void deletePreviousData(){
        getActivity().deleteDatabase("vehicle_details");
    }

    protected void insertIntoDB2(String vehicleRegistrationNo , String vtsDeviceId){
        String query = "INSERT INTO vehicle_list (vehicleRegistrationNo,vtsId) VALUES('"+vehicleRegistrationNo+"', '"+vtsDeviceId+"');";
        sqLiteDatabase.execSQL(query);

    }


}
