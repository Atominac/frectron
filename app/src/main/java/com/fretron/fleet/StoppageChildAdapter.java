package com.fretron.fleet;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class StoppageChildAdapter extends RecyclerView.Adapter<StoppageChildAdapter.MyViewHolder> {
    private List<StoppageChildListDetails> activityList2;

    public StoppageChildAdapter(List<StoppageChildListDetails> activityList2) {
        this.activityList2 = activityList2;
    }
    public StoppageChildAdapter() {
        this.activityList2 = activityList2;
    }


    public void setActivityList(List<StoppageChildListDetails> activityList2) {
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
    public StoppageChildAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_stoppage_sub_list, parent, false);

        return new StoppageChildAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(StoppageChildAdapter.MyViewHolder holder, int position) {
        StoppageChildListDetails activityListItems2 = activityList2.get(position);
        holder.title.setText(activityListItems2.getTitle());
        holder.speed.setText(activityListItems2.getSpeed());
        holder.distance.setText(activityListItems2.getDistance());
    }

    @Override
    public int getItemCount() {
        return activityList2.size();
    }

}
