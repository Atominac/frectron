package com.fretron.fleet.Timeline;

import android.os.Parcel;
import android.os.Parcelable;

class TimeLineModel implements Parcelable {
    private String mMessage;
    private String mDate;
    private OrderStatus mStatus;

    public TimeLineModel() {

    }

    TimeLineModel(String mMessage, String mDate, OrderStatus mStatus) {
        this.mMessage = mMessage;
        this.mDate = mDate;
        this.mStatus = mStatus;
    }

    String getMessage() {
        return mMessage;
    }

    void setMessage(String message) {
        this.mMessage = message;

    }

    String getDate() {
        return mDate;

    }

    void setDate(String date) {
        this.mDate = date;

    }

    OrderStatus getStatus() {
        return mStatus;

    }

    void setStatus(OrderStatus mStatus) {
        this.mStatus = mStatus;

    }

    @Override
    public int describeContents() {
        return 0;

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mMessage);
        dest.writeString(this.mDate);
        dest.writeInt(this.mStatus == null ? -1 : this.mStatus.ordinal());

    }

    private TimeLineModel(Parcel in) {

        this.mMessage = in.readString();
        this.mDate = in.readString();
        int tmpMStatus = in.readInt();
        this.mStatus = tmpMStatus == -1 ? null : OrderStatus.values()[tmpMStatus];

    }

    public static final Parcelable.Creator<TimeLineModel> CREATOR = new Parcelable.Creator<TimeLineModel>() {

        @Override

        public TimeLineModel createFromParcel(Parcel source) {
            return new TimeLineModel(source);

        }



        @Override

        public TimeLineModel[] newArray(int size) {

            return new TimeLineModel[size];

        }

    };

}
