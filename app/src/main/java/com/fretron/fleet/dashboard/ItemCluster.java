package com.fretron.fleet.dashboard;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;

import java.util.Collection;

class ItemCluster implements ClusterItem {
    private final LatLng mPosition;

    ItemCluster(double lat, double lng) {
        mPosition = new LatLng(lat, lng);
    }


    @Override
    public LatLng getPosition() {
        return mPosition;
    }

}
