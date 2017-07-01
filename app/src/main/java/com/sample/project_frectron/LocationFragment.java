package com.sample.project_frectron;

import android.app.SearchManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ViewFlipper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.SEARCH_SERVICE;
import static com.sample.project_frectron.R.id.map2;

public class LocationFragment extends Fragment implements View.OnClickListener {
    protected View mView;
    GoogleMap googleMap;
    private MapView mapView;
    private boolean mapsSupported = true;
    Menu menu ;
    private RecyclerView vehicleList_recyclerView , mRecyclerView;
    private TimeLineAdapter mTimeLineAdapter;
    private List<TimeLineModel> mDataList = new ArrayList<>();
    ArrayList<String> vehicle_list = new ArrayList<>();
    private Orientation mOrientation;
    private boolean mWithLinePadding;

    public LocationFragment() {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_location_fragment, container, false);
        this.mView = view;

        ((DashBoard) getActivity())
                .setActionBarTitle("Location History");

//        CardView cardView = (CardView)mView.findViewById(R.id.card_view_1);
//        cardView.setOnClickListener(this);

        mapView = (MapView) mView.findViewById(map2);
        setHasOptionsMenu(true);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        vehicleList_recyclerView = (RecyclerView) mView.findViewById(R.id.location_vehicle_name_recycler);

        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(getLinearLayoutManager());
        mRecyclerView.setHasFixedSize(true);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        initView();

        return view;
    }

    @Override
    public void onClick(View v) {
        ViewFlipper viewFlipper = (ViewFlipper) mView.findViewById(R.id.viewFlipper3);
        viewFlipper.setInAnimation(getActivity(), R.anim.view_transition_in_left);
        viewFlipper.setOutAnimation(getActivity(), R.anim.view_transition_out_left);
        viewFlipper.showNext();
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

    private LinearLayoutManager getLinearLayoutManager() {
        if (mOrientation == Orientation.HORIZONTAL) {
            return new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        } else {
            return new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        }
    }

    private void initView() {
        setDataListItems();
        mTimeLineAdapter = new TimeLineAdapter(mDataList, mOrientation, mWithLinePadding);
        mRecyclerView.setAdapter(mTimeLineAdapter);
    }

    private void setDataListItems(){
        mDataList.add(new TimeLineModel("Huda city center", "2017-02-12 08:00", OrderStatus.COMPLETED));
        mDataList.add(new TimeLineModel("MG Road metro", "2017-02-13 09:00", OrderStatus.COMPLETED));
        mDataList.add(new TimeLineModel("Sultanpur", "2017-02-15 10:00", OrderStatus.COMPLETED));
        mDataList.add(new TimeLineModel("Hauz khaz", "2017-02-17 18:00", OrderStatus.COMPLETED));
        mDataList.add(new TimeLineModel("India Gate", "2017-02-18 09:30", OrderStatus.COMPLETED));
        mDataList.add(new TimeLineModel("Anad vihar ISBT", "2017-02-20 08:00", OrderStatus.COMPLETED));
        mDataList.add(new TimeLineModel("Vaishali Ghaziabad", "2017-02-22 15:00", OrderStatus.COMPLETED));
        mDataList.add(new TimeLineModel("Lucknow", "2017-02-24 14:30", OrderStatus.COMPLETED));
        mDataList.add(new TimeLineModel("Gorakhpur", "2017-02-27 14:00", OrderStatus.ACTIVE));
    }

}
