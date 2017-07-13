package com.fretron.fleet;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fretron.fleet.dashboard.DashBoard;

public class SignOutFragment extends Fragment {
    protected View mView;


    public SignOutFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_out, container, false);
        this.mView = view;

        ((DashBoard) getActivity())
                .setActionBarTitle("Sign out");

        return view ;
    }

}
