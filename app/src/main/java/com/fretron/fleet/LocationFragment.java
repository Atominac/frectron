package com.fretron.fleet;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.fretron.fleet.dashboard.DashBoard;
import com.fretron.fleet.dashboard.DashMainFragment;

import java.util.ArrayList;


public class LocationFragment extends Fragment implements SearchView.OnQueryTextListener{
    protected View mView;
    Menu menu ;
    private RecyclerView vehicleList_recyclerView ;
    ArrayList<String> vehicle_list = new ArrayList<>();
    RecyclerView.Adapter adapter;
    ListView listView ;

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

        listView = (ListView) mView.findViewById(R.id.location_vehicle_name_list);
        ArrayAdapter<String> ad = new ArrayAdapter<String>(getActivity(), R.layout.location_vehiclelist_recycler,
                R.id.location_list_textView,vehicle_list);
        listView.setAdapter(ad);
        listView.setTextFilterEnabled(true);

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

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        this.menu = menu;
        inflater.inflate(R.menu.report_frag_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem menuItem = menu.findItem(R.id.action_search);
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
        if (TextUtils.isEmpty(newText)) {
            listView.clearTextFilter();
        } else {
            listView.setFilterText(newText);
        }
        return true;
    }

    protected void openDatabase() {
        sqLiteDatabase = getActivity().openOrCreateDatabase("vehicle_details", Context.MODE_PRIVATE, null);
    }
}
