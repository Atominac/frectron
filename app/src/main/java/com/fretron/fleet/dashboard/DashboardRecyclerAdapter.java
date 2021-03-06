package com.fretron.fleet.dashboard;

import android.os.Bundle;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.fretron.fleet.R;
import com.fretron.fleet.Timeline.TimelineFragment;

import java.util.ArrayList;
import java.util.List;

class DashboardRecyclerAdapter extends RecyclerView.Adapter<DashboardRecyclerAdapter.ViewHolder> {

    private List<DashboardRecyclerItems> activityList;
    Context context;
    private Context mContext;

    DashboardRecyclerAdapter(List<DashboardRecyclerItems> activityList, Context context) {
        this.activityList = activityList;
        mContext = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, speed, starting_date,location,status,starting_time;
        public ImageButton button;

            public ViewHolder(View view) {
            super(view);
                title = (TextView) view.findViewById(R.id.activity_truck_name);
                speed = (TextView) view.findViewById(R.id.activity_current_speed);
                starting_date = (TextView) view.findViewById(R.id.activity_truck_start_date);
                location = (TextView) view.findViewById(R.id.activity_truck_location);
                status = (TextView) view.findViewById(R.id.activity_truck_status);
                starting_time = (TextView) view.findViewById(R.id.activity_vehicle_start_time);
                button = (ImageButton) view.findViewById(R.id.view_timeline_button);
            }
     }

    @Override
    public DashboardRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_content_list, parent, false);

    return new DashboardRecyclerAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final DashboardRecyclerAdapter.ViewHolder holder, int position) {
    final DashboardRecyclerItems activityListItems = activityList.get(position);
        holder.title.setText(activityListItems.getTitle());
        holder.speed.setText(activityListItems.getSpeed());
        holder.starting_date.setText(activityListItems.getStarting_date());
        holder.location.setText(activityListItems.getLocation());
        holder.status.setText(activityListItems.getStatus());

        switch (activityListItems.getStatus()) {
            case "offline":
                holder.status.setBackgroundColor(Color.parseColor("#ff0000"));
                holder.status.setTextColor(Color.parseColor("#ffffff"));
                break;
            case "stopped":
                holder.status.setBackgroundColor(Color.parseColor("#ffff00"));
                break;
            case "moving":
                holder.status.setBackgroundColor(Color.parseColor("#228B22"));
                holder.status.setTextColor(Color.parseColor("#ffffff"));
                break;
            case "overspeeding":
                holder.status.setBackgroundColor(Color.parseColor("#FF4500"));
                holder.status.setTextColor(Color.parseColor("#ffffff"));
                break;
        }

        holder.starting_time.setText(activityListItems.getStarting_time());

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimelineFragment fragment = new TimelineFragment();
                Bundle bundle=new Bundle();
                bundle.putString("NAME",activityListItems.getTitle());
                bundle.putString("vtsVehicleId",activityListItems.getVtsDeviceId());
                fragment.setArguments(bundle);
                ((DashBoard) mContext).getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up)
                        .replace(R.id.fragment_container,fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }

    void setFilter(List<DashboardRecyclerItems> vehicleDetailList) {
        activityList = new ArrayList<>();
        activityList.addAll(vehicleDetailList);
        notifyDataSetChanged();
    }

}
