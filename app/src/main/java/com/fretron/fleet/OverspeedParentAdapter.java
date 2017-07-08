package com.fretron.fleet;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class OverspeedParentAdapter extends RecyclerView.Adapter<OverspeedParentAdapter.MyViewHolder>{

    private List<OverspeedParentListDetails> activityList;
    Context context;


    public void setActivityList(List<OverspeedParentListDetails> activityList) {
        this.activityList = activityList;
        notifyDataSetChanged();
    }

    public OverspeedParentAdapter(List<OverspeedParentListDetails> activityList) {
        this.activityList = activityList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, speed, distance;
        public RecyclerView recyclerView;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.textView_vehicle_name);
            speed = (TextView) view.findViewById(R.id.textView_vehicle_time);
            distance = (TextView) view.findViewById(R.id.textView_vehicle_distance);
            recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_overspeed_sub_list);
            OverspeedChildAdapter nAdapter = new OverspeedChildAdapter();
            recyclerView.setAdapter(nAdapter);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(layoutManager);
        }
    }

    @Override
    public OverspeedParentAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.overspeed_list, parent, false);

        return new OverspeedParentAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(OverspeedParentAdapter.MyViewHolder holder, int position) {


        OverspeedParentListDetails activityListItems = activityList.get(position);
        holder.title.setText(activityListItems.getTitle());
        holder.distance.setText(activityListItems.getDistance());
        holder.speed.setText(activityListItems.getSpeed());

        ((OverspeedChildAdapter)holder.recyclerView.getAdapter()).setActivityList2(activityListItems.childListDetailses);

    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }

}
