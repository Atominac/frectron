package com.sample.project_frectron;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

public class ActivityListAdapter extends RecyclerView.Adapter<ActivityListAdapter.MyViewHolder> {

    private List<ActivityListItems> activityList;

    public ActivityListAdapter(List<ActivityListItems> activityList) {
        this.activityList = activityList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
     public TextView title, speed, starting_date,location,status,starting_time;

            public MyViewHolder(View view) {
            super(view);
                title = (TextView) view.findViewById(R.id.activity_truck_name);
                speed = (TextView) view.findViewById(R.id.activity_current_speed);
                starting_date = (TextView) view.findViewById(R.id.activity_truck_start_date);
                location = (TextView) view.findViewById(R.id.activity_truck_location);
                status = (TextView) view.findViewById(R.id.activity_truck_status);
                starting_time = (TextView) view.findViewById(R.id.activity_vehicle_start_time);

            }
     }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_content_list, parent, false);

    return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
    ActivityListItems activityListItems = activityList.get(position);
        holder.title.setText(activityListItems.getTitle());
        holder.speed.setText(activityListItems.getSpeed());
        holder.starting_date.setText(activityListItems.getStarting_date());
        holder.location.setText(activityListItems.getLocation());
        holder.status.setText(activityListItems.getStatus());
        holder.starting_time.setText(activityListItems.getStarting_time());
    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }

}
