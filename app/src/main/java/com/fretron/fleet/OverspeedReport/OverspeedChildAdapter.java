package com.fretron.fleet.OverspeedReport;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fretron.fleet.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

class OverspeedChildAdapter extends RecyclerView.Adapter<OverspeedChildAdapter.MyViewHolder> {

    private List<OverspeedingChildListDetails> activityList2;
    private LocationThread mAuthTask = null;
    private String position = "" ;
    private Context context;

    public OverspeedChildAdapter(List<OverspeedingChildListDetails> activityList2 , Context context) {
        this.activityList2 = activityList2;
        this.context =context;
    }
    OverspeedChildAdapter(Context context) {
        this.activityList2 = activityList2;
        this.context =context;
    }


    void setActivityList2(List<OverspeedingChildListDetails> activityList2) {
        this.activityList2 = activityList2;
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, speed, distance, location;

        MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.overspeeding_start_time);
            speed = (TextView) view.findViewById(R.id.overspeeding_duration);
            distance = (TextView) view.findViewById(R.id.overspeeding_speed);
            location = (TextView) view.findViewById(R.id.overspeeding_place);

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
        holder.location.setText(activityListItems2.getLocation());


        mAuthTask = new LocationThread();
        mAuthTask.execute((String) null);


    }

    @Override
    public int getItemCount() {
        return activityList2.size();
    }

    private class LocationThread extends AsyncTask<String, Void, String> {

        LocationThread() {
        }

        @Override
        protected String doInBackground(String... params) {

            try {

                for (int j = 0 ; j < activityList2.size() ; j++){
                    Geocoder gcd = new Geocoder(context, Locale.getDefault());
                    List<Address> start_position_string ;
                    start_position_string = gcd.getFromLocation(activityList2.get(j).getLatitude(),
                            activityList2.get(j).getLongitude(), 1);
                    if (start_position_string.size()!=0){
                        String city = start_position_string.get(0).getLocality();
                        String state = start_position_string.get(0).getAdminArea();
                        position = start_position_string.get(0).getAddressLine(0) + "\n" +
                                    city + "\n" + state;
                    }
                    else
                        position = "Unknown Location";
                    activityList2.get(j).setLocation(position);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return position;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onCancelled() {
            mAuthTask.cancel(true);
        }

        @Override
        protected void onPostExecute(String position){
            notifyDataSetChanged();
        }
    }


}
