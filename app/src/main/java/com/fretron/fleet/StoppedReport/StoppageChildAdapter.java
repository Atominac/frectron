package com.fretron.fleet.StoppedReport;

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

class StoppageChildAdapter extends RecyclerView.Adapter<StoppageChildAdapter.MyViewHolder> {
    private List<StoppageChildListDetails> activityList2;
    private LocationThread mAuthTask = null;
    private String position = "" ;
    private Context context;

    public StoppageChildAdapter(List<StoppageChildListDetails> activityList2, Context context) {
        this.activityList2 = activityList2;
        this.context =context;
    }

    StoppageChildAdapter(Context context) {
        this.activityList2 = activityList2;
        this.context =context;
    }

    void setActivityList(List<StoppageChildListDetails> activityList2) {
        this.activityList2 = activityList2;
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, speed, location ;

        MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.stoppage_start_time);
            speed = (TextView) view.findViewById(R.id.stoppage_duration);
            location = (TextView) view.findViewById(R.id.stoppage_location);
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
        holder.location.setText(activityListItems2.getLocation());

        mAuthTask = new LocationThread();
        mAuthTask.execute((String) null);

      //  holder.distance.setText(activityListItems2.getDistance());
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
