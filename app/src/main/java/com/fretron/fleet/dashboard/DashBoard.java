package com.fretron.fleet.dashboard;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.fretron.fleet.LocationHistory.LocationFragment;
import com.fretron.fleet.R;
import com.fretron.fleet.OverspeedReport.ReportOverspeedFragment;
import com.fretron.fleet.RunningReport.ReportRunningFragment;
import com.fretron.fleet.StoppedReport.ReportStoppedFragment;
import com.fretron.fleet.SignOut.SignOutFragment;
import com.fretron.fleet.login.UserSessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashBoard extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    NavigationView navigationView = null ;
    Toolbar toolbar = null ;
    UserSessionManager session ;
    ExpandableListView expListView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        fab.setVisibility(View.INVISIBLE);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        drawer.setScrimColor(Color.TRANSPARENT);

        DashMainFragment fragment = new DashMainFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.commit();

        enableExpandableList();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            int count = getFragmentManager().getBackStackEntryCount();

            if (count == 0) {
                super.onBackPressed();
                //additional code
            } else {
                getFragmentManager().popBackStack();
            }
//            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dash_board, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void enableExpandableList() {
        final ArrayList<String> listDataHeader = new ArrayList<>();

        final HashMap<String, List<String>> listDataChild = new HashMap<>();
        expListView = (ExpandableListView) findViewById(R.id.left_drawer);

        prepareListData(listDataHeader, listDataChild);
        ExpandableListAdapter listAdapter ;
        listAdapter = new CustomNavDrawer(this, listDataHeader, listDataChild);
        // setting list adapter
        expListView.setAdapter(listAdapter);

        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                DrawerLayout drawer ;
                switch (groupPosition) {
                    case 0:
                        DashMainFragment fragment = new DashMainFragment();
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container,fragment);
                        fragmentTransaction.commit();
                        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.START);
                        return true;

                    case 1:
                        LocationFragment fragmenty = new LocationFragment();
                        FragmentTransaction fragmentTransactiony = getSupportFragmentManager().beginTransaction();
                        fragmentTransactiony.replace(R.id.fragment_container,fragmenty);
                        fragmentTransactiony.commit();
                        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.START);
                        return true;

                    case 5 :
                        session = new UserSessionManager(getApplicationContext());
                        session.logout();
                        Toast.makeText(DashBoard.this,"You are Logged out",Toast.LENGTH_LONG).show();
//                        SignOutFragment signOutFragment = new SignOutFragment();
//                        FragmentTransaction signoutTransaction = getSupportFragmentManager().beginTransaction();
//                        signoutTransaction.replace(R.id.fragment_container,signOutFragment);
//                        signoutTransaction.commit();
//                        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//                        drawer.closeDrawer(GravityCompat.START);
                        return true;

                }

                return false;


            }
        });


        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {

            }
        });
        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {

            }
        });
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                switch(groupPosition) {

                    case 2:
                        switch (childPosition) {
                            case 0:
                                ReportRunningFragment fragmentz = new ReportRunningFragment();
                                FragmentTransaction fragmentTransactionz = getSupportFragmentManager().beginTransaction();
                                fragmentTransactionz.replace(R.id.fragment_container,fragmentz);
                                fragmentTransactionz.commit();

                                break;

                            case 1:
                                ReportOverspeedFragment fragment2 = new ReportOverspeedFragment();
                                FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                                fragmentTransaction2.replace(R.id.fragment_container,fragment2);
                                fragmentTransaction2.commit();

                                break;

                            case 2:
                                ReportStoppedFragment fragment3 = new ReportStoppedFragment();
                                FragmentTransaction fragmentTransaction3 = getSupportFragmentManager().beginTransaction();
                                fragmentTransaction3.replace(R.id.fragment_container,fragment3);
                                fragmentTransaction3.commit();

                                break;
                        }
                        break;
                }
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    private void prepareListData(List<String> listDataHeader, Map<String,
                List<String>> listDataChild) {

        // Adding Parent data
        String header_1 = getResources().getString(R.string.nav_header_1);
        String header_3 = getResources().getString(R.string.nav_header_3);
        String header_4 = getResources().getString(R.string.nav_header_4);
        String header_5 = getResources().getString(R.string.nav_header_5);
        String header_6 = getResources().getString(R.string.nav_header_6);
        String header_7 = getResources().getString(R.string.nav_header_7);
        listDataHeader.add(header_1);
        listDataHeader.add(header_3);
        listDataHeader.add(header_4);
        listDataHeader.add(header_5);
        listDataHeader.add(header_6);
        listDataHeader.add(header_7);

        // Adding child data
        List<String> first = new ArrayList<>();
        first.add("All vehicles");

        List<String> third = new ArrayList<>();
        third.add("Tap to view the history timeline of the vehicles");

        List<String> fourth = new ArrayList<>();
        fourth.add("Running");
        fourth.add("Overspeed");
        fourth.add("Stopped");

        List<String> fifth = new ArrayList<>();
        fifth.add("Alert settings");

        List<String> sixth = new ArrayList<>();
        sixth.add("Manage all settings");


        listDataChild.put(listDataHeader.get(0), first); // Header, Child data
        listDataChild.put(listDataHeader.get(1), third);
        listDataChild.put(listDataHeader.get(2), fourth);
        listDataChild.put(listDataHeader.get(3), fifth);
        listDataChild.put(listDataHeader.get(4), sixth);
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }


}
