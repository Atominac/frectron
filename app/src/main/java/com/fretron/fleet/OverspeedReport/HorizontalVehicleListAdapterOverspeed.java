package com.fretron.fleet.OverspeedReport;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.fretron.fleet.R;
import java.util.List;

class HorizontalVehicleListAdapterOverspeed  extends RecyclerView.Adapter<HorizontalVehicleListAdapterOverspeed.ViewHolder>{
    private List<String> mDataSet;
    private Context mContext;
    private ReportOverspeedFragment fragment;


    HorizontalVehicleListAdapterOverspeed(Context context, List<String> list, ReportOverspeedFragment fragment){
        mDataSet = list;
        mContext = context;
        this.fragment = fragment ;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView mTextView;
        ImageButton mRemoveButton;
        RelativeLayout mRelativeLayout;
        public ViewHolder(View v){
            super(v);
            mTextView = (TextView) v.findViewById(R.id.vehicle_horizontal_list_vehicle_name);
            mRemoveButton = (ImageButton) v.findViewById(R.id.vehicle_horizontal_list_remove_button);
            mRelativeLayout = (RelativeLayout) v.findViewById(R.id.vehicle_horizontal_list_parent_layout);
        }
    }

    @Override
    public HorizontalVehicleListAdapterOverspeed.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        // Create a new View
        View v = LayoutInflater.from(mContext).inflate(R.layout.horizontal_list,parent,false);
        return new HorizontalVehicleListAdapterOverspeed.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final HorizontalVehicleListAdapterOverspeed.ViewHolder holder, int position){
        holder.mTextView.setText(mDataSet.get(holder.getAdapterPosition()));

        holder.mRemoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.parentItemList.clear();
                fragment.mAdapter.notifyDataSetChanged();
                fragment.total_records = 0 ;
                fragment.total_distance = 0.0;
                fragment.neTime = 0.0 ;
                String itemLabel = mDataSet.get(holder.getAdapterPosition());
                fragment.selected_vehicles.remove(holder.getAdapterPosition());
                mDataSet.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
                notifyItemRangeChanged(holder.getAdapterPosition(),mDataSet.size());
                Toast.makeText(mContext,"Removed : " + itemLabel,Toast.LENGTH_SHORT).show();
                String query2 = "SELECT * FROM vehicle_list";
                fragment.list.clear();
                fragment.c2 = fragment.sqLiteDatabase.rawQuery(query2, null);
                fragment.c2.moveToFirst();
                if (fragment.c2.moveToFirst()) {
                    do {

                        for ( int i = 0 ; i<fragment.current_vehicle_in_list.size();i++ ){
                            if (fragment.c2.getString(0).equals(fragment.current_vehicle_in_list.get(i))){
                                if (!fragment.list.contains(fragment.c2.getString(1))){
                                    fragment.list.add(fragment.c2.getString(1));
                                }
                            }
                        }
                    } while (fragment.c2.moveToNext());
                }
                fragment.c2.close();
                fragment.makeJsonObjectRequest(fragment.start_date_epo,
                        fragment.end_date_epo,fragment.list);
            }
        });
    }

    @Override
    public int getItemCount(){
        return mDataSet.size();
    }

}

