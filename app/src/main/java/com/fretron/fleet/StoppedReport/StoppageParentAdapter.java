package com.fretron.fleet.StoppedReport;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fretron.fleet.R;

import java.util.List;

class StoppageParentAdapter extends RecyclerView.Adapter<StoppageParentAdapter.MyViewHolder> {
    private List<StoppageParentListDetails> activityList;
    private Context context;

    void setActivityList(List<StoppageParentListDetails> activityList, Context context) {
        this.activityList = activityList;
        this.context = context ;
        notifyDataSetChanged();
    }

    StoppageParentAdapter(List<StoppageParentListDetails> activityList, Context context) {
        this.activityList = activityList;
        this.context = context ;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, time;
        RecyclerView recyclerView;

        MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.textView_vehicle_name);
            time = (TextView) view.findViewById(R.id.textView_vehicle_time);
            recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_stoppage_sub_list);
            StoppageChildAdapter nAdapter = new StoppageChildAdapter(context);
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
        holder.time.setText(activityListItems.getTime());

        ((StoppageChildAdapter)holder.recyclerView.getAdapter()).setActivityList(activityListItems.childListDetailses);

    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }

}
