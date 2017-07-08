package com.fretron.fleet;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class RunningReportListAdapter extends RecyclerView.Adapter<RunningReportListAdapter.MyViewHolder> {

    private List<RunningReportListItems> running_list;

    public RunningReportListAdapter(List<RunningReportListItems> running_list) {
        this.running_list = running_list;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView vehicle_name , initial_destination,
                total_time,
                total_distance,
                final_destination,
                average_speed,
                stoppage_time,
                drive_time,
                overspeed_duration,
                disconnect_duration,
                show_more;

        public RelativeLayout mRelativeLayout;

        public MyViewHolder(View view) {
            super(view);
            vehicle_name = (TextView) view.findViewById(R.id.vehicle_name);
            initial_destination = (TextView) view.findViewById(R.id.vehicle_initial_destination);
            total_time = (TextView) view.findViewById(R.id.vehicle_total_journey_duration);
            total_distance = (TextView) view.findViewById(R.id.vehicle_total_journey_distance);
            final_destination = (TextView) view.findViewById(R.id.vehicle_final_destination);
            average_speed = (TextView) view.findViewById(R.id.vehicle_averagespeed);
            stoppage_time = (TextView) view.findViewById(R.id.vehicle_stoppage_time);
            drive_time = (TextView) view.findViewById(R.id.vehicle_drive_time);
            overspeed_duration = (TextView) view.findViewById(R.id.vehicle_overspeeding_duration);
            disconnect_duration = (TextView) view.findViewById(R.id.vehicle_disconnect_duration);

            show_more = (TextView) view.findViewById(R.id.magic_textView);
            mRelativeLayout = (RelativeLayout) view.findViewById(R.id.magic_relative);
        }
    }

    @Override
    public RunningReportListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.running_report_recyclerview, parent, false);

        return new RunningReportListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RunningReportListAdapter.MyViewHolder holder, int position) {
        RunningReportListItems runningReportListItems = running_list.get(position);
        holder.vehicle_name.setText(runningReportListItems.getVehicle_name());
        holder.initial_destination.setText(runningReportListItems.getInitial_destination());
        holder.total_time.setText(runningReportListItems.getTotal_time());
        holder.total_distance.setText(runningReportListItems.getTotal_distance());
        holder.final_destination.setText(runningReportListItems.getFinal_destination());
        holder.average_speed.setText(runningReportListItems.getAverage_speed());
        holder.stoppage_time.setText(runningReportListItems.getStoppage_time());
        holder.drive_time.setText(runningReportListItems.getDrive_time());
        holder.overspeed_duration.setText(runningReportListItems.getOverspeed_duration());
        holder.disconnect_duration.setText(runningReportListItems.getDisconnect_duration());

        holder.show_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.show_more.setVisibility(View.GONE);
                holder.mRelativeLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return running_list.size();
    }

}
