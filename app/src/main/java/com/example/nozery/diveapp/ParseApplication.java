package com.example.nozery.diveapp;

import android.app.Application;

import com.parse.Parse;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "gsvpCsYn9FbHDvjiBhOZoZyA5toqS3JM81WhHuIK"
                , "OHHJXQH4eMTQvik4uvrW80nZxnecayjrKqMMBH4k");
    }
}
