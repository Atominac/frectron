package com.sample.project_frectron;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Random;

public class HorizontalVehicleListAdapter  extends RecyclerView.Adapter<HorizontalVehicleListAdapter.ViewHolder>{
    private List<String> mDataSet;
    private Context mContext;
    private Random mRandom = new Random();

    public HorizontalVehicleListAdapter(Context context,List<String> list){
        mDataSet = list;
        mContext = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView mTextView;
        public ImageButton mRemoveButton;
        public RelativeLayout mRelativeLayout;
        public ViewHolder(View v){
            super(v);
            mTextView = (TextView) v.findViewById(R.id.vehicle_horizontal_list_vehicle_name);
            mRemoveButton = (ImageButton) v.findViewById(R.id.vehicle_horizontal_list_remove_button);
            mRelativeLayout = (RelativeLayout) v.findViewById(R.id.vehicle_horizontal_list_parent_layout);
        }
    }

    @Override
    public HorizontalVehicleListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        // Create a new View
        View v = LayoutInflater.from(mContext).inflate(R.layout.vehicle_horizontal_listview,parent,false);
        return new HorizontalVehicleListAdapter.ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(HorizontalVehicleListAdapter.ViewHolder holder, final int position){
        holder.mTextView.setText(mDataSet.get(position));
        // Set a click listener for TextView
//        holder.mTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String animal = mDataSet.get(position);
//                Toast.makeText(mContext,animal,Toast.LENGTH_SHORT).show();
//            }
//        });

        holder.mRemoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String itemLabel = mDataSet.get(position);

                mDataSet.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,mDataSet.size());
                Toast.makeText(mContext,"Removed : " + itemLabel,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount(){
        return mDataSet.size();
    }

}
