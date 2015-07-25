package com.example.nozery.diveapp;

import android.os.Parcelable;

import java.util.HashMap;

abstract class DiveBasePoi implements Parcelable
{
    protected HashMap<String, String> mData;

    protected Double mLat;
    protected Double mLng;
    protected String mName;
    protected String mDescription;

    protected DiveBasePoi(){

        mData = new HashMap<>();

        mLat = 0.0 ;
        mLng = 0.0 ;
        mName = "";
        mDescription = "";
    }
    protected  DiveBasePoi(DiveBasePoi diveBasePoi){

        mData = new HashMap<>(diveBasePoi.getData());

        mLat = diveBasePoi.mLat ;
        mLng = diveBasePoi.mLng ;
        mName = diveBasePoi.mName;
        mDescription = diveBasePoi.mDescription;
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

    // abstract API
    abstract public void SerializeData();
    abstract public void DeserializeData();
}

