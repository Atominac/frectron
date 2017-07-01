package com.sample.project_frectron;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.github.vipulasri.timelineview.TimelineView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TimeLineViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.text_timeline_date)
    TextView mDate;
    @BindView(R.id.text_timeline_title)
    TextView mMessage;
    @BindView(R.id.time_marker)
    TimelineView mTimelineView;

    public TimeLineViewHolder(View itemView, int viewType) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mTimelineView = (TimelineView)itemView.findViewById(R.id.time_marker);
        mDate = (TextView)itemView.findViewById(R.id.text_timeline_date);
        mMessage = (TextView)itemView.findViewById(R.id.text_timeline_title);
        mTimelineView.initLine(viewType);
    }

}
