package com.example.nozery.diveapp;

import android.os.Parcel;

public class DiveSite extends DiveBasePoi
{
    private final static String DS_LAT =  "DsLat" ;
    private final static String DS_LNG =  "DsLng" ;
    private final static String DS_NAME =  "DsName" ;
    private final static String DS_DESC =  "DsDesc" ;

    protected DiveSite(){
        super();

        mData.put(DS_LAT, "N/A");
        mData.put(DS_LNG, "N/A");
        mData.put(DS_NAME, "N/A");
        mData.put(DS_DESC, "N/A");
    }

    protected DiveSite(DiveSite diveSite){
        super(diveSite);

    }

    @Override
    public void SerializeData() {

        mData.put(DS_LAT, String.valueOf(mLat));
        mData.put(DS_LNG, String.valueOf(mLng));
        mData.put(DS_NAME, mName);
        mData.put(DS_DESC, mDescription);
    }

    @Override
    public void DeserializeData() {
        mLat = Double.parseDouble(mData.get(DS_LAT));
        mLng = Double.parseDouble(mData.get(DS_LNG));
        mName = mData.get(DS_NAME);
        mName = mData.get(DS_DESC);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
