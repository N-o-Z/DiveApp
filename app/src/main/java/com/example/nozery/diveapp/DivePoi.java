package com.example.nozery.diveapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class DivePoi implements Parcelable {
    protected HashMap<String, String> mData;

    protected DivePoi() {

        mData = new HashMap<>();
        mData.put("Lat","0");
        mData.put("Lng","0");
        mData.put("Name","N/A");
        mData.put("Type","N/A");
        mData.put("Description","N/A");
    }

    protected DivePoi(DivePoi divePoi) {

        mData = new HashMap<>(divePoi.getData());
    }

    protected DivePoi(HashMap<String, String> data) {

        mData = new HashMap<>(data);

    }

    // setters
    public void setValue(String key, String value) {

        mData.put(key,value);
    }

    // getters
    public HashMap<String, String> getData() {

        return mData;
    }

    public String getValue(String key) {

        return mData.get(key);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        
    }
}
