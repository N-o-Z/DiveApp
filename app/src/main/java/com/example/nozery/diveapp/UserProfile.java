package com.example.nozery.diveapp;

import java.util.HashMap;

public class UserProfile {

    public static final int MIN_USERNAME_LENGTH = 4;
    public static final int MAX_USERNAME_LENGTH = 16;
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int MAX_PASSWORD_LENGTH = 30;

    protected HashMap<String, String> mData;

    protected UserProfile() {

        mData = new HashMap<>();
        mData.put("username","USERNAME");
        mData.put("email","EMAIL@PROVIDER.COM");
        mData.put("name","Not Specified");
        mData.put("gender","Not Specified");
        mData.put("birthday","Not Specified");
        mData.put("language","English");
        mData.put("country","Not Specified");
        mData.put("certification","Not Specified");
        mData.put("organization","Not Specified");
        mData.put("additionalCert", "Not Specified");
        mData.put("sessionToken","Not Specified");

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