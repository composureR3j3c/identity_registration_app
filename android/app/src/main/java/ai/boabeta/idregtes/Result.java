package com.boabeta.idregtes;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import ai.tech5.finger.utils.LivenessScore;

public class Result implements Parcelable {

    public ArrayList<LivenessScore> livenessScores;
    public ArrayList<FingerData> fingers;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.livenessScores);
        dest.writeList(this.fingers);
    }

    public void readFromParcel(Parcel source) {
        this.livenessScores = source.createTypedArrayList(LivenessScore.CREATOR);
        this.fingers = new ArrayList<FingerData>();
        source.readList(this.fingers, FingerData.class.getClassLoader());
    }

    public Result() {
    }

    protected Result(Parcel in) {
        this.livenessScores = in.createTypedArrayList(LivenessScore.CREATOR);
        this.fingers = new ArrayList<FingerData>();
        in.readList(this.fingers, FingerData.class.getClassLoader());
    }

    public static final Creator<Result> CREATOR = new Creator<Result>() {
        @Override
        public Result createFromParcel(Parcel source) {
            return new Result(source);
        }

        @Override
        public Result[] newArray(int size) {
            return new Result[size];
        }
    };
}
