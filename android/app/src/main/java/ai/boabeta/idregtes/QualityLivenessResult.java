package com.boabeta.idregtes;

import android.os.Parcel;
import android.os.Parcelable;

public class QualityLivenessResult implements Parcelable {
    public boolean isFailed = false;
    public String failureDetails = null;
    public String successDetails = null;

    // Default constructor
    public QualityLivenessResult(boolean failed, String failureDetails, String successDetails) {
        this.isFailed = failed;
        this.failureDetails = failureDetails;
        this.successDetails = successDetails;
    }

    // Parcelable constructor
    protected QualityLivenessResult(Parcel in) {
        isFailed = in.readByte() != 0;
        failureDetails = in.readString();
        successDetails = in.readString();
    }

    // FIXED CREATOR - Copy this exactly
    public static final Parcelable.Creator<QualityLivenessResult> CREATOR = new Parcelable.Creator<QualityLivenessResult>() {
        @Override
        public QualityLivenessResult createFromParcel(Parcel in) {
            return new QualityLivenessResult(in);
        }

        @Override
        public QualityLivenessResult[] newArray(int size) {
            return new QualityLivenessResult[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isFailed ? 1 : 0));
        dest.writeString(failureDetails);
        dest.writeString(successDetails);
    }
}
