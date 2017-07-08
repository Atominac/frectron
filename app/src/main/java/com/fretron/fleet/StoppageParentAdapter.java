package com.fretron.fleet;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class StoppageParentAdapter extends RecyclerView.Adapter<StoppageParentAdapter.MyViewHolder> {
    private List<StoppageParentListDetails> activityList;
    Context context;

    public void setActivityList(List<StoppageParentListDetails> activityList) {
        this.activityList = activityList;
        notifyDataSetChanged();
    }

    public StoppageParentAdapter(List<StoppageParentListDetails> activityList) {
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
            recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_stoppage_sub_list);
            OverspeedChildAdapter nAdapter = new OverspeedChildAdapter();
            recyclerView.setAdapter(nAdapter);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(layoutManager);
        }
    }

    @Override
    public StoppageParentAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.stoppage_list, parent, false);

        return new StoppageParentAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(StoppageParentAdapter.MyViewHolder holder, int position) {

        StoppageParentListDetails activityListItems = activityList.get(position);
        holder.title.setText(activityListItems.getTitle());
        holder.distance.setText(activityListItems.getDistance());
        holder.speed.setText(activityListItems.getSpeed());

        ((StoppageChildAdapter)holder.recyclerView.getAdapter()).setActivityList(activityListItems.childListDetailses);

    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }

}
