package com.fretron.fleet;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ViewFlipper;

import com.fretron.fleet.dashboard.DashBoard;
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

import static com.fretron.fleet.R.id.map2;

public class LocationFragment extends Fragment{
    protected View mView;
    Menu menu ;
    private RecyclerView vehicleList_recyclerView ;
    ArrayList<String> vehicle_list = new ArrayList<>();
    RecyclerView.Adapter adapter;

    SQLiteDatabase sqLiteDatabase;
    Cursor c,c2;

    public LocationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_location_fragment, container, false);
        this.mView = view;

        ((DashBoard) getActivity())
                .setActionBarTitle("Location History");

        setHasOptionsMenu(true);

        openDatabase();

        String query2 = "SELECT * FROM vehicle_list";
        c = sqLiteDatabase.rawQuery(query2, null);
        c.moveToFirst();

        if (c.moveToFirst()) {
            do {
                String vehicleRegNo = c.getString(0);
                vehicle_list.add(vehicleRegNo);

            } while (c.moveToNext());
        }
        c.close();

        final ListView listView = (ListView) mView.findViewById(R.id.location_vehicle_name_list);
        ArrayAdapter<String> ad = new ArrayAdapter<String>(getActivity(), R.layout.location_vehiclelist_recycler,
                R.id.location_list_textView,vehicle_list);
        listView.setAdapter(ad);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3)
            {

                ViewTimelineLocation fragment = new ViewTimelineLocation();
                Bundle bundle=new Bundle();
                bundle.putString("NAME",listView.getItemAtPosition(position).toString());

                String query2 = "SELECT * FROM vehicle_list";
                String vehicleId = "default" ;
                c2 = sqLiteDatabase.rawQuery(query2, null);
                c2.moveToFirst();

                if (c2.moveToFirst()) {
                    do {

                        if (listView.getItemAtPosition(position).toString().equals(c2.getString(0))){
                            vehicleId = c2.getString(1);
                        }

                    } while (c2.moveToNext());
                }
                c2.close();

                bundle.putString("vtsVehicleId",vehicleId);
                fragment.setArguments(bundle);

                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container,fragment);
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });

        return view;
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

    protected void openDatabase() {
        sqLiteDatabase = getActivity().openOrCreateDatabase("vehicle_details", Context.MODE_PRIVATE, null);
    }

}
