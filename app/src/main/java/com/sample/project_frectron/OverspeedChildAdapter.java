package com.sample.project_frectron;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class OverspeedChildAdapter extends RecyclerView.Adapter<OverspeedChildAdapter.MyViewHolder> {

    private List<OverspeedingChildListDetails> activityList2;

    public OverspeedChildAdapter(List<OverspeedingChildListDetails> activityList2) {
        this.activityList2 = activityList2;
    }
    public OverspeedChildAdapter() {
        this.activityList2 = activityList2;
    }


    public void setActivityList2(List<OverspeedingChildListDetails> activityList2) {
        this.activityList2 = activityList2;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, speed, distance;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.overspeeding_start_time);
            speed = (TextView) view.findViewById(R.id.overspeeding_duration);
            distance = (TextView) view.findViewById(R.id.overspeeding_speed);
        }
    }

    @Override
    public OverspeedChildAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_overspeed_sub_list, parent, false);

        return new OverspeedChildAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(OverspeedChildAdapter.MyViewHolder holder, int position) {
        OverspeedingChildListDetails activityListItems2 = activityList2.get(position);
        holder.title.setText(activityListItems2.getTitle());
        holder.speed.setText(activityListItems2.getSpeed());
        holder.distance.setText(activityListItems2.getDistance());
    }

    @Override
    public int getItemCount() {
        return activityList2.size();
    }

}
