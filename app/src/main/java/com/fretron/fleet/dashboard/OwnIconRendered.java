package com.fretron.fleet.dashboard;

import android.content.Context;
import com.fretron.fleet.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

class OwnIconRendered extends DefaultClusterRenderer<ItemCluster> {
    OwnIconRendered(Context context, GoogleMap map, ClusterManager<ItemCluster> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(ItemCluster item,
                                               MarkerOptions markerOptions) {
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.truck_icon));
    }

}
