package com.boabeta.idregtes;

import android.os.Parcel;
import android.os.Parcelable;

import ai.tech5.finger.utils.Finger;

public class FingerData implements Parcelable {

    public int pos;
    public int nistQuality = 0;

    public int nist2Quality = 0;
    public int quality = 0;
    public int minutiaesNumber = 0;

    public String primaryImagePath;

    public String displayImagePath;


    public FingerData(Finger finger) {

        this.pos = finger.pos;
        this.nistQuality = finger.nistQuality;
        this.nist2Quality = finger.nist2Quality;
        this.quality = finger.quality;
        this.minutiaesNumber = finger.minutiaesNumber;


    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.pos);
        dest.writeInt(this.nistQuality);
        dest.writeInt(this.nist2Quality);
        dest.writeInt(this.quality);
        dest.writeInt(this.minutiaesNumber);
        dest.writeString(this.primaryImagePath);
        dest.writeString(this.displayImagePath);
    }

    public void readFromParcel(Parcel source) {
        this.pos = source.readInt();
        this.nistQuality = source.readInt();
        this.nist2Quality = source.readInt();
        this.quality = source.readInt();
        this.minutiaesNumber = source.readInt();
        this.primaryImagePath = source.readString();
        this.displayImagePath = source.readString();
    }

    protected FingerData(Parcel in) {
        this.pos = in.readInt();
        this.nistQuality = in.readInt();
        this.nist2Quality = in.readInt();
        this.quality = in.readInt();
        this.minutiaesNumber = in.readInt();
        this.primaryImagePath = in.readString();
        this.displayImagePath = in.readString();
    }

    public static final Parcelable.Creator<FingerData> CREATOR = new Parcelable.Creator<FingerData>() {
        @Override
        public FingerData createFromParcel(Parcel source) {
            return new FingerData(source);
        }

        @Override
        public FingerData[] newArray(int size) {
            return new FingerData[size];
        }
    };
}
