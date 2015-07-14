package com.example.nozery.diveapp;

import java.util.HashMap;

public class UserProfile {

    protected HashMap<String, String> mData;

    protected UserProfile() {

        mData = new HashMap<>();
        mData.put("username","USERNAME");
        mData.put("email","EMAIL@PROVIDER.COM");
        mData.put("name","John Doe");
        mData.put("gender","Not Specified");
        mData.put("birthday","Not Specified");
        mData.put("language","English");
        mData.put("country","Not Specified");
        mData.put("certification","Not Specified");
        mData.put("organization","Not Specified");
        mData.put("additionalCert","Not Specified");

    }

    protected UserProfile(UserProfile profile) {

        mData = new HashMap<>(profile.getData());
    }

    protected UserProfile(HashMap<String, String> data) {

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

}