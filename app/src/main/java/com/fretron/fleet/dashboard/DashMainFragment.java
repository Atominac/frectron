package com.fretron.fleet.dashboard;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
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
import android.widget.ViewFlipper;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.fretron.fleet.Timeline.Orientation;
import com.fretron.fleet.R;
import com.fretron.fleet.Essentials.VolleyMain;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;


import org.json.JSONException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import static com.google.android.gms.plus.internal.PlusCommonExtras.TAG;
import static com.fretron.fleet.R.id.map;

public class DashMainFragment extends DialogFragment implements SearchView.OnQueryTextListener {
    protected View mView;
    GoogleMap googleMap;
    private MapView mapView;
    private List<DashboardRecyclerItems> activityList = new ArrayList<>();
    private DashboardRecyclerAdapter mAdapter;
    ArrayAdapter<CharSequence> arrayAdapter;
    private SQLiteDatabase sqLiteDatabase;
    private Orientation mOrientation;
    Menu menu;
    String token, customer_id ;
    ProgressBar progressBar;
    TextView textView;
    Calendar calendar;
    Double speedDouble = 0.0;
    ClusterManager<ItemCluster> mClusterManager ;
    private VehicleListLoadingTask vehicleListLoadingTask = null;
    String startPosition = "";
    DashboardRecyclerItems activityItems;
    RecyclerView recyclerView ;
    LinearLayoutManager linearLayoutManager ;
    MenuItem menuItem ;
    int recyclerIndex ;
    List<Address> start_position_string = null;

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

    ///////////////////////////////////Initializing the Google Map/////////////////////////////////
    private void initializeMap() {
        // boolean mapsSupported = true;
        if (googleMap == null) {
            mapView = (MapView) getActivity().findViewById(map);
            googleMap = mapView.getMap();
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(28.38, 77.12), 4));

            //setup markers etc...
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_dash_main_fragment, container, false);
        this.mView = view;

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);

        ((DashBoard) getActivity())
                .setActionBarTitle("Dashboard");

        deletePreviousData();
        createDatabase();
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        textView = (TextView) mView.findViewById(R.id.textView_company_name);

        /////////////////////////////// Here token is assigned the value//////////////////////////////////

//        token = getActivity().getIntent().getExtras().getString("Token");
//        assert token != null;
//        String[] splits = token.split("\\.");
//
//        String split = splits[1];
//        try {
//             String customerJson = getJson(split);
//             JSONParser parser = new JSONParser();
//             org.json.simple.JSONObject json = (org.json.simple.JSONObject) parser.parse(customerJson);
//             customer_id = (String) json.get("id");
//
//        } catch (Exception e) {
//            Toast.makeText(getActivity()," Not working ",Toast.LENGTH_LONG).show();
//            customer_id = "nothing" ;
//            e.printStackTrace();
//        }

       makeJsonObjectRequest(customer_id);

//        if (check.equals("true")) {
//            new Thread(mRunnable = new Runnable() {
//                @Override
//                public void run() {
//                    while (true) {
//                        try {
//                            Thread.sleep(INTERVAL);
//                            mHandler.post(mRunnable = new Runnable() {
//
//                                @Override
//                                public void run() {
//                                    makeJsonObjectRequest(customer_id);
//                                }
//
//
//                            });
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }).start();
//        }


        // prepareData();


        recyclerView = (RecyclerView) mView.findViewById(R.id.recyclerView2);
        mAdapter = new DashboardRecyclerAdapter(activityList, getActivity());
        recyclerView.setLayoutManager(getLinearLayoutManager());
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);


        ////////////////////////////// Filter using Spinner //////////////////////////////////////////////////
        final Spinner spinner = (Spinner) mView.findViewById(R.id.select_status);
        arrayAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.vehicle_status, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String query = "SELECT * FROM vehicle_list";
                String query2 = "SELECT * FROM vehicle_list WHERE status = 'moving'";
                String query3 = "SELECT * FROM vehicle_list WHERE status = 'stopped'";
                String query4 = "SELECT * FROM vehicle_list WHERE status = 'offline'";

                String selectedItem = parent.getItemAtPosition(position).toString();
                DashboardRecyclerItems activityItems;
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
                                activityItems = new DashboardRecyclerItems(vehicleRegNo, speed, startTime, location, status, "", vtsDeviceId);
                                activityList.add(activityItems);

                            }
                            while (c.moveToNext());
                            RecyclerView recyclerView = (RecyclerView) mView.findViewById(R.id.recyclerView2);
                            mAdapter = new DashboardRecyclerAdapter(activityList, getActivity());
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
                                activityItems = new DashboardRecyclerItems(vehicleRegNo, speed, startTime, location, status, "", vtsDeviceId);
                                activityList.add(activityItems);

                            } while (c.moveToNext());
                            RecyclerView recyclerView = (RecyclerView) mView.findViewById(R.id.recyclerView2);
                            mAdapter = new DashboardRecyclerAdapter(activityList, getActivity());
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
                                activityItems = new DashboardRecyclerItems(vehicleRegNo, speed, startTime, location, status, "", vtsDeviceId);
                                activityList.add(activityItems);

                            } while (c.moveToNext());
                            RecyclerView recyclerView = (RecyclerView) mView.findViewById(R.id.recyclerView2);
                            mAdapter = new DashboardRecyclerAdapter(activityList, getActivity());
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
                                activityItems = new DashboardRecyclerItems(vehicleRegNo, speed, startTime, location, status, "", vtsDeviceId);
                                activityList.add(activityItems);

                            } while (c.moveToNext());
                            RecyclerView recyclerView = (RecyclerView) mView.findViewById(R.id.recyclerView2);
                            mAdapter = new DashboardRecyclerAdapter(activityList, getActivity());
                            recyclerView.setLayoutManager(getLinearLayoutManager());
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(mAdapter);
                        }
                        c.close();


                        break;
                    }
                }

            } // to close the onItemSelected

            public void onNothingSelected(AdapterView<?> parent) {
                //nothing
            }
        });

        //////////////////////////////// Switch from list to map or map to list////////////////////////////
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

    // Function to decode the token
    private static String getJson(String strEncoded) throws UnsupportedEncodingException {
        byte[] decodedBytes = Base64.decode(strEncoded, Base64.URL_SAFE);
        return new String(decodedBytes, "UTF-8");
    }

    //Decoding the lat long into address
    private class VehicleListLoadingTask extends AsyncTask<String, Void, String> {
        private  double[] latArray , longArray ;

        VehicleListLoadingTask(double[] laArray , double[] lnArray) {
            latArray = laArray ;
            longArray = lnArray ;
            //mAuthTask = UserLoginTask.this;
        }

        @Override
        protected String doInBackground(String... params) {
           // ContentValues values = new ContentValues();

            if (!isCancelled()) {
                try {

                    Geocoder gcd = new Geocoder(getActivity(), Locale.getDefault());

                    for (recyclerIndex = 0; recyclerIndex < latArray.length - 1; recyclerIndex++) {
                        start_position_string = gcd.getFromLocation(latArray[recyclerIndex], longArray[recyclerIndex], 1);
                        startPosition = "Unknown Location";
                        if (start_position_string.size() != 0) {
                            String city = start_position_string.get(0).getLocality();
                            String state = start_position_string.get(0).getAdminArea();
                            startPosition  = start_position_string.get(0).getAddressLine(0) + "\n" +
                                    city + "\n" + state;
                        }

//                        values.put("location", startPosition);
//                        sqLiteDatabase.update("vehicle_list", values, "location =" + recyclerIndex , null);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    activityList.get(recyclerIndex).setLocation(startPosition);
                                    mAdapter.notifyDataSetChanged();
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
            showpProgress();
        }

        @Override
        protected void onCancelled() {
            vehicleListLoadingTask = null;
        }
    }



    private void makeJsonObjectRequest(String customer_id) {
        showpProgress();
        final String[] speed = {"0"};
        final String[] netDateTime = {"NA"};
        final String[] status = {"NA"};
        final ItemCluster[] offsetItem = {null};
        final int[] index = {0};

        //String urlJsonArray = "http://35.189.189.215:8094/dashboard?customerId="+customer_id;
        String urlJsonArray = "http://35.189.189.215:8094/dashboard";
        final JsonArrayRequest jsonObjReq = new JsonArrayRequest(urlJsonArray, new Response.Listener<org.json.JSONArray>() {

            @Override
            public void onResponse(org.json.JSONArray response) {
                double[] latArray = new double[response.length()];
                double[] longArray = new double[response.length()];

                int arrayIndex = 0 ;

                try {

                    mClusterManager = new ClusterManager<>(getActivity(), googleMap);
                    for (int i = 0; i <= response.length(); i++) {
                        org.json.JSONObject vehicleDetails = (org.json.JSONObject) response.get(i);
                        final String vehicle_Id = vehicleDetails.get("vehicleRegistrationNumber").toString();
                        String vtsDeviceId = vehicleDetails.get("vtsDeviceId").toString();

                        if (vehicleDetails.has("startTime")) {

                            String startingDate = vehicleDetails.get("startTime").toString();
                            calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(Long.parseLong(startingDate));

                            int yy = calendar.get(Calendar.YEAR);
                            int mm = calendar.get(Calendar.MONTH);
                            int dd = calendar.get(Calendar.DAY_OF_MONTH);
                            int hh = calendar.get(Calendar.HOUR_OF_DAY);
                            int mi = calendar.get(Calendar.MINUTE);

                            netDateTime[0] = String.valueOf(dd) + "/" +
                                    String.valueOf(mm) + "/" +
                                    String.valueOf(yy) + " " +
                                    String.valueOf(hh) + ":" +
                                    String.valueOf(mi);
                        }

                        if (vehicleDetails.has("latitude") &&
                                vehicleDetails.has("longitude") &&
                                vehicleDetails.has("state") &&
                                vehicleDetails.has("speed")
                                ) {
                            final Double lat = vehicleDetails.getDouble("latitude");
                            final Double lng = vehicleDetails.getDouble("longitude");

                            offsetItem[0] = new ItemCluster(lat, lng);
                            mClusterManager.addItem(offsetItem[0]);

                            int current_status = (int) vehicleDetails.get("state");

                            switch (current_status) {
                                case -1:
                                    status[0] = "offline";
//                                    Marker marker = googleMap.addMarker(new MarkerOptions()
//                                            .position(new LatLng(lat, lng))
//                                            .title(vehicle_Id)
//                                            .icon(BitmapDescriptorFactory
//                                                    .fromResource(R.drawable.truck_icon)));

                                    break;
                                case 0:
                                    status[0] = "stopped";
//                                    marker = googleMap.addMarker(new MarkerOptions()
//                                            .position(new LatLng(lat, lng))
//                                            .title(vehicle_Id)
//                                            .icon(BitmapDescriptorFactory
//                                                    .fromResource(R.drawable.truck_icon)));
                                    break;
                                case 1:
                                    status[0] = "moving";
//                                    marker = googleMap.addMarker(new MarkerOptions()
//                                            .position(new LatLng(lat, lng))
//                                            .title(vehicle_Id)
//                                            .icon(BitmapDescriptorFactory
//                                                    .fromResource(R.drawable.truck_icon)));
                                    break;
                                case 2:
                                    status[0] = "overspeeding";
//                                    marker = googleMap.addMarker(new MarkerOptions()
//                                            .position(new LatLng(lat, lng))
//                                            .title(vehicle_Id)
//                                            .icon(BitmapDescriptorFactory
//                                                    .fromResource(R.drawable.truck_icon)));
                                    break;
                                default:
                                    status[0] = "offline";
                            }

                            speed[0] = vehicleDetails.get("speed").toString();
                            speedDouble = Math.round(Double.parseDouble(speed[0]) * 100.0) / 100.0;

                            activityItems = new DashboardRecyclerItems(vehicle_Id, String.valueOf(speedDouble) + " m/s", netDateTime[0], "Loading location" , status[0], "", vtsDeviceId);
                            activityList.add(activityItems);

                            insertIntoDB2(vehicle_Id, vtsDeviceId, "Loading", netDateTime[0] , status[0] ,
                                            String.valueOf(speedDouble) + " m/s" , String.valueOf(lat) ,
                                            String.valueOf(lng) );

                            latArray[arrayIndex] = lat ;
                            longArray[arrayIndex]= lng ;

                            arrayIndex++;

                        } else {
                            speedDouble = 0.0 ;
                            insertIntoDB2(vehicle_Id, vtsDeviceId, "N-A-", "N-A-", "offline", "N-A-", "", "");
                            activityItems = new DashboardRecyclerItems(vehicle_Id, "N-A-", "N-A-", "N-A-", "offline", "", vtsDeviceId);
                            activityList.add(activityItems);

                        }

                        index[0]++;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                vehicleListLoadingTask = new VehicleListLoadingTask(latArray,longArray);
                vehicleListLoadingTask.execute((String) null);

                mAdapter = new DashboardRecyclerAdapter(activityList, getActivity());
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(mAdapter);
                googleMap.setOnCameraChangeListener(mClusterManager);
                googleMap.setOnMarkerClickListener(mClusterManager);
                googleMap.setInfoWindowAdapter(new MyCustomAdapterForItems());
                new OwnIconRendered(getActivity(),googleMap,mClusterManager);
//                mClusterManager.getMarkerManager().
//                mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<ItemCluster>() {
//                    @Override
//                    public boolean onClusterItemClick(ItemCluster myItem) {
//
//                        Cursor markerCursor = sqLiteDatabase.rawQuery(query, null);
//                        markerCursor.moveToFirst();
//                        if (markerCursor.moveToFirst()) {
//                            do {
//                                String vehicleRegNo = markerCursor.getString(0);
//                                String vtsDeviceId = markerCursor.getString(1);
//                                String location = markerCursor.getString(2);
//                                String startTime = markerCursor.getString(3);
//                                String status = markerCursor.getString(4);
//                                String speed = markerCursor.getString(5);
//                                String lat = markerCursor.getString(6);
//                                String lng = markerCursor.getString(7);
//
//                                if (lat.equals(String.valueOf(myItem.getPosition().latitude))&&
//                                        lng.equals(String.valueOf(myItem.getPosition().longitude))){
//
//
//                                    LayoutInflater lil = LayoutInflater.from(getActivity());
//                                    View promptsView = lil.inflate(R.layout.onmarker_layout, null);
//
//                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
//                                    alertDialogBuilder.setView(promptsView);
//                                    alertDialogBuilder.setTitle(vehicleRegNo);
//
//                                    TextView vehicleName = (TextView) promptsView
//                                            .findViewById(R.id.onmarker_truck_name);
//                                    vehicleName.setText(vtsDeviceId);
//
//                                    TextView vehicleLocation = (TextView) promptsView
//                                            .findViewById(R.id.onmarker_truck_location);
//                                    vehicleLocation.setText(location);
//
//                                    TextView vehicleDate = (TextView) promptsView
//                                            .findViewById(R.id.onmarker_truck_start_date);
//                                    vehicleDate.setText(startTime);
//
//                                    TextView vehicleStatus = (TextView) promptsView
//                                            .findViewById(R.id.onmarker_truck_status);
//                                    vehicleStatus.setText(status);
//
//                                    TextView vehicleSpeed = (TextView) promptsView
//                                            .findViewById(R.id.onmarker_current_speed);
//                                    vehicleSpeed.setText(speed);
//
//
//                                    alertDialogBuilder
//                                            .setCancelable(false)
//                                            .setPositiveButton("close",
//                                                    new DialogInterface.OnClickListener() {
//                                                        public void onClick(DialogInterface dialog, int id) {
//                                                            dialog.cancel();
//                                                        }
//                                                    }
//                                            );
//
//                                    AlertDialog alertDialog = alertDialogBuilder.create();
//                                    alertDialog.show();
//
//                                }
//
//                            }
//                            while (markerCursor.moveToNext());
//                        }
//                        markerCursor.close();
//
//                        return true ;
//                    }
//                });
//                mClusterManager.cluster();
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

                        Intent setIntent = new Intent(Intent.ACTION_MAIN);
                        setIntent.addCategory(Intent.CATEGORY_HOME);
                        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(setIntent);

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
//        check = "false";
//        mHandler.removeCallbacks(mRunnable);
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
        menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(this);

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        newText = newText.toLowerCase();
        ArrayList<DashboardRecyclerItems> newList = new ArrayList<>();
        for (DashboardRecyclerItems activityListItems : activityList) {
            String vehicleName = activityListItems.getTitle().toLowerCase();
            if (vehicleName.contains(newText)) {
                newList.add(activityListItems);
            }
        }

        mAdapter.setFilter(newList);
        return true;
    }

    private void showpProgress() {
        ViewFlipper viewFlipper = (ViewFlipper) mView.findViewById(R.id.viewFlipper2);
        progressBar = (ProgressBar) mView.findViewById(R.id.progressBar_Activity);
        viewFlipper.setVisibility(View.GONE);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hidepProgress() {
        ViewFlipper viewFlipper = (ViewFlipper) mView.findViewById(R.id.viewFlipper2);
        progressBar = (ProgressBar) mView.findViewById(R.id.progressBar_Activity);
        viewFlipper.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(false);
        progressBar.setVisibility(View.GONE);
    }

    protected void createDatabase() {
        sqLiteDatabase = getActivity().openOrCreateDatabase("vehicle_details", Context.MODE_PRIVATE, null);
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS vehicle_list(vehicleRegistrationNo VARCHAR," +
                " vtsId VARCHAR , location VARCHAR , startTime VARCHAR ," +
                " status VARCHAR , speed VARCHAR , latitude VARCHAR , longitude VARCHAR);");
    }

    protected void deletePreviousData() {
        getActivity().deleteDatabase("vehicle_details");
    }

    protected void insertIntoDB2(String vehicleRegistrationNo, String vtsDeviceId
            , String location, String startTime, String status, String speed , String latitude , String longitude) {
        String query = "INSERT INTO vehicle_list (vehicleRegistrationNo,vtsId,location" +
                ",startTime,status,speed,latitude,longitude) VALUES('" + vehicleRegistrationNo + "', '" + vtsDeviceId + "'," +
                "'" + location + "','" + startTime + "','" + status + "','" + speed + "' ,'" + latitude + "' ,'" + longitude + "');";
        sqLiteDatabase.execSQL(query);

    }

    private class MyCustomAdapterForItems implements GoogleMap.InfoWindowAdapter {

        private final View myContentsView;

        MyCustomAdapterForItems() {
            myContentsView = getActivity().getLayoutInflater().inflate(
                    R.layout.onmarker_layout, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {

            final String query = "SELECT * FROM vehicle_list";
            Cursor markerCursor = sqLiteDatabase.rawQuery(query, null);
            markerCursor.moveToFirst();
            if (markerCursor.moveToFirst()) {
                do {
                    String vehicleRegNo = markerCursor.getString(0);
                    String vtsDeviceId = markerCursor.getString(1);
                    String location = markerCursor.getString(2);
                    String startTime = markerCursor.getString(3);
                    String status = markerCursor.getString(4);
                    String speed = markerCursor.getString(5);
                    String lat = markerCursor.getString(6);
                    String lng = markerCursor.getString(7);

                    if (lat.equals(String.valueOf(marker.getPosition().latitude))&&
                            lng.equals(String.valueOf(marker.getPosition().longitude))){

                        TextView vehicleName = (TextView) myContentsView
                                .findViewById(R.id.onmarker_truck_name);
                        vehicleName.setText(vehicleRegNo);

                        TextView vehicleLocation = (TextView) myContentsView
                                .findViewById(R.id.onmarker_truck_location);
                        vehicleLocation.setText(location);

                        TextView vehicleDate = (TextView) myContentsView
                                .findViewById(R.id.onmarker_truck_start_date);
                        vehicleDate.setText(startTime);

                        TextView vehicleStatus = (TextView) myContentsView
                                .findViewById(R.id.onmarker_truck_status);
                        vehicleStatus.setText(status);

                        TextView vehicleSpeed = (TextView) myContentsView
                                .findViewById(R.id.onmarker_current_speed);
                        vehicleSpeed.setText(speed);


                    }

                }
                while (markerCursor.moveToNext());
            }
            markerCursor.close();

            return myContentsView;
        }
    }
}
