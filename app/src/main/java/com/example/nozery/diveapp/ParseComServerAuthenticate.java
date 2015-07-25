package com.example.nozery.diveapp;

import android.util.Log;
import com.google.gson.Gson;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Handles the comminication with Parse.com
 *
 * User: udinic
 * Date: 3/27/13
 * Time: 3:30 AM
 */
public class ParseComServerAuthenticate implements ServerAuthenticate {

    @Override
    public String userSignUp(String username, String email, String pass, String authType)
            throws Exception {
        Log.d(MainActivity.APP_NAME, "userSignUp");
        ParseUser user = new ParseUser();


        UserProfile profile = new UserProfile();
        profile.setValue(MyDbHelper.ProfileEntry.COLUMN_NAME_USERNAME, username);
        profile.setValue(MyDbHelper.ProfileEntry.COLUMN_NAME_EMAIL, email);

        user.setUsername(profile.getValue(MyDbHelper.ProfileEntry.COLUMN_NAME_USERNAME));
        user.setEmail(profile.getValue(MyDbHelper.ProfileEntry.COLUMN_NAME_EMAIL));
        user.setPassword(pass);
        user.put(MyDbHelper.ProfileEntry.COLUMN_NAME_NAME
                , profile.getValue(MyDbHelper.ProfileEntry.COLUMN_NAME_NAME));
        user.put(MyDbHelper.ProfileEntry.COLUMN_NAME_GENDER
                , profile.getValue(MyDbHelper.ProfileEntry.COLUMN_NAME_GENDER));
        user.put(MyDbHelper.ProfileEntry.COLUMN_NAME_BIRTHDAY
                , profile.getValue(MyDbHelper.ProfileEntry.COLUMN_NAME_BIRTHDAY));
        user.put(MyDbHelper.ProfileEntry.COLUMN_NAME_LANGUAGE
                , profile.getValue(MyDbHelper.ProfileEntry.COLUMN_NAME_LANGUAGE));
        user.put(MyDbHelper.ProfileEntry.COLUMN_NAME_COUNTRY
                , profile.getValue(MyDbHelper.ProfileEntry.COLUMN_NAME_COUNTRY));
        user.put(MyDbHelper.ProfileEntry.COLUMN_NAME_CERTIFICATION
                , profile.getValue(MyDbHelper.ProfileEntry.COLUMN_NAME_CERTIFICATION));
        user.put(MyDbHelper.ProfileEntry.COLUMN_NAME_ORGANIZATION
                , profile.getValue(MyDbHelper.ProfileEntry.COLUMN_NAME_ORGANIZATION));
        user.put(MyDbHelper.ProfileEntry.COLUMN_NAME_ADD_CERT
                , profile.getValue(MyDbHelper.ProfileEntry.COLUMN_NAME_ADD_CERT));
        user.put(MyDbHelper.ProfileEntry.COLUMN_NAME_PROFILE_PIC
                , "");
        user.put(MyDbHelper.ProfileEntry.COLUMN_NAME_PASSWORD, pass);


        user.signUp();

        return user.getSessionToken();
    }

    @Override
    public String userSignIn(String username, String pass, String authType) throws Exception {

        Log.d(MainActivity.APP_NAME, "userSignIn");

        ParseUser user = new ParseUser();
        ParseUser.logIn(username, pass);

        return user.getSessionToken();
    }
}
