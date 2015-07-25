package com.example.nozery.diveapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by nozery on 7/11/2015.
 */

public class ParseDbHelper {

    public static final String AUTHTOKEN_TYPE_READ_ONLY = "Read only";

    public static final String AUTHTOKEN_TYPE_FULL_ACCESS = "Full access";

    private ParseUser mCurrentUser;
    private Context mContext;

    // Logcat tag
    private static final String LOG = "ParseDbHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "appData";

    // Table Names
    protected static final String TABLE_USER_PROFILES = "user_profile";
    protected static final String TABLE_BOARD_PROFILES = "board_profiles";
    protected static final String TABLE_BOARD_MSG = "board_msg";

    // Common column names
    protected static final String KEY_ID = "id";
    protected static final String KEY_CREATED_AT = "created_at";

    // Table Create Statements
    // User profile table create statement
    private static final String CREATE_TABLE_USER_PROFILE = "CREATE TABLE "
            +ProfileEntry.TABLE_NAME  + "("
            +ProfileEntry.COLUMN_NAME_USERNAME + " TEXT PRIMARY KEY,"
            +ProfileEntry.COLUMN_NAME_EMAIL + " TEXT,"
            +ProfileEntry.COLUMN_NAME_NAME + " TEXT,"
            +ProfileEntry.COLUMN_NAME_GENDER + " TEXT,"
            +ProfileEntry.COLUMN_NAME_BIRTHDAY + " TEXT,"
            +ProfileEntry.COLUMN_NAME_LANGUAGE + " TEXT,"
            +ProfileEntry.COLUMN_NAME_COUNTRY + " TEXT,"
            +ProfileEntry.COLUMN_NAME_CERTIFICATION + " TEXT,"
            +ProfileEntry.COLUMN_NAME_ORGANIZATION + " TEXT,"
            +ProfileEntry.COLUMN_NAME_ADD_CERT + " TEXT,"
            +ProfileEntry.COLUMN_NAME_PROFILE_PIC + " TEXT,"
            + KEY_CREATED_AT + " DATETIME" + ")";

    // Tag table create statement
    //private static final String CREATE_TABLE_BOARD_PROFILES = "CREATE TABLE " + TABLE_TAG
    //        + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TAG_NAME + " TEXT,"
    //        + KEY_CREATED_AT + " DATETIME" + ")";

    // todo_tag table create statement
    //private static final String CREATE_TABLE_BOARD_MSG = "CREATE TABLE "
    //        + TABLE_TODO_TAG + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
    //        + KEY_TODO_ID + " INTEGER," + KEY_TAG_ID + " INTEGER,"
    //        + KEY_CREATED_AT + " DATETIME" + ")";

    /* Inner class that defines the user profile table contents */
    public static abstract class ProfileEntry implements BaseColumns {

        public static final String TABLE_NAME = TABLE_USER_PROFILES;
        public static final String COLUMN_NAME_USERNAME = "username";
        public static final String COLUMN_NAME_EMAIL = "email";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_GENDER = "gender";
        public static final String COLUMN_NAME_BIRTHDAY = "birthday";
        public static final String COLUMN_NAME_LANGUAGE = "language";
        public static final String COLUMN_NAME_COUNTRY = "country";
        public static final String COLUMN_NAME_CERTIFICATION = "certification";
        public static final String COLUMN_NAME_ORGANIZATION = "organization";
        public static final String COLUMN_NAME_ADD_CERT = "additionalCert";
        public static final String COLUMN_NAME_PROFILE_PIC = "profilePic";
        public static final String COLUMN_NAME_PASSWORD = "password";

    }

    public ParseDbHelper() {

    }

    public void onCreate() {

        //TODO: fetch required data from server (what arguments shoud this function receive?
        // creating required tables
        //db.execSQL(CREATE_TABLE_USER_PROFILE);
        //db.execSQL(CREATE_TABLE_BOARD_PROFILE);
        //db.execSQL(CREATE_TABLE_BOARD_MSG);

    }

    public void onUpgrade() {
        // TODO: how to upgrade with parse?
    }
    public void onDowngrade() {
        // TODO: how to upgrade with parse?
    }

    /**
     * getting all user profiles
     * */
    public boolean init() {
        mCurrentUser = ParseUser.getCurrentUser();
        return null != mCurrentUser;
    }

    public UserProfile getCurrentProfile() {
        mCurrentUser = ParseUser.getCurrentUser();
        UserProfile profile = new UserProfile();
        profile.setValue(ProfileEntry.COLUMN_NAME_USERNAME
                , mCurrentUser.getString(ProfileEntry.COLUMN_NAME_USERNAME));
        profile.setValue(ProfileEntry.COLUMN_NAME_EMAIL
                , mCurrentUser.getString(ProfileEntry.COLUMN_NAME_EMAIL));
        profile.setValue(ProfileEntry.COLUMN_NAME_NAME
                , mCurrentUser.getString(ProfileEntry.COLUMN_NAME_NAME));
        profile.setValue(ProfileEntry.COLUMN_NAME_GENDER
                , mCurrentUser.getString(ProfileEntry.COLUMN_NAME_GENDER));
        profile.setValue(ProfileEntry.COLUMN_NAME_BIRTHDAY
                , mCurrentUser.getString(ProfileEntry.COLUMN_NAME_BIRTHDAY));
        profile.setValue(ProfileEntry.COLUMN_NAME_LANGUAGE
                , mCurrentUser.getString(ProfileEntry.COLUMN_NAME_LANGUAGE));
        profile.setValue(ProfileEntry.COLUMN_NAME_COUNTRY
                , mCurrentUser.getString(ProfileEntry.COLUMN_NAME_COUNTRY));
        profile.setValue(ProfileEntry.COLUMN_NAME_CERTIFICATION
                , mCurrentUser.getString(ProfileEntry.COLUMN_NAME_CERTIFICATION));
        profile.setValue(ProfileEntry.COLUMN_NAME_ORGANIZATION
                , mCurrentUser.getString(ProfileEntry.COLUMN_NAME_ORGANIZATION));
        profile.setValue(ProfileEntry.COLUMN_NAME_ADD_CERT
                , mCurrentUser.getString(ProfileEntry.COLUMN_NAME_ADD_CERT));
        profile.setValue(ProfileEntry.COLUMN_NAME_PROFILE_PIC
                , mCurrentUser.getString(ProfileEntry.COLUMN_NAME_PROFILE_PIC));

        return profile;
    }

    /*
    * Updating profile
    */
    public void updateProfile(UserProfile profile) {

        mCurrentUser.put(ProfileEntry.COLUMN_NAME_NAME
                ,profile.getValue(ProfileEntry.COLUMN_NAME_NAME));
        mCurrentUser.put(ProfileEntry.COLUMN_NAME_GENDER
                ,profile.getValue(ProfileEntry.COLUMN_NAME_GENDER));
        mCurrentUser.put(ProfileEntry.COLUMN_NAME_BIRTHDAY
                ,profile.getValue(ProfileEntry.COLUMN_NAME_BIRTHDAY));
        mCurrentUser.put(ProfileEntry.COLUMN_NAME_LANGUAGE
                ,profile.getValue(ProfileEntry.COLUMN_NAME_LANGUAGE));
        mCurrentUser.put(ProfileEntry.COLUMN_NAME_COUNTRY
                ,profile.getValue(ProfileEntry.COLUMN_NAME_COUNTRY));
        mCurrentUser.put(ProfileEntry.COLUMN_NAME_CERTIFICATION
                , profile.getValue(ProfileEntry.COLUMN_NAME_CERTIFICATION));
        mCurrentUser.put(ProfileEntry.COLUMN_NAME_ORGANIZATION
                , profile.getValue(ProfileEntry.COLUMN_NAME_ORGANIZATION));
        mCurrentUser.put(ProfileEntry.COLUMN_NAME_ADD_CERT
                , profile.getValue(ProfileEntry.COLUMN_NAME_ADD_CERT));
        mCurrentUser.put(ProfileEntry.COLUMN_NAME_PROFILE_PIC
                , profile.getValue(ProfileEntry.COLUMN_NAME_PROFILE_PIC));
        mCurrentUser.saveInBackground();

    }

    public String userSignUp(String username, String email, String pass, String authType)
            throws Exception {
        Log.d(MainActivity.APP_NAME, "userSignUp");
        ParseUser user = new ParseUser();


        UserProfile profile = new UserProfile();
        profile.setValue(ParseDbHelper.ProfileEntry.COLUMN_NAME_USERNAME, username);
        profile.setValue(ParseDbHelper.ProfileEntry.COLUMN_NAME_EMAIL, email);

        user.setUsername(profile.getValue(ParseDbHelper.ProfileEntry.COLUMN_NAME_USERNAME));
        user.setEmail(profile.getValue(ParseDbHelper.ProfileEntry.COLUMN_NAME_EMAIL));
        user.setPassword(pass);
        user.put(ParseDbHelper.ProfileEntry.COLUMN_NAME_NAME
                , profile.getValue(ParseDbHelper.ProfileEntry.COLUMN_NAME_NAME));
        user.put(ParseDbHelper.ProfileEntry.COLUMN_NAME_GENDER
                , profile.getValue(ParseDbHelper.ProfileEntry.COLUMN_NAME_GENDER));
        user.put(ParseDbHelper.ProfileEntry.COLUMN_NAME_BIRTHDAY
                , profile.getValue(ParseDbHelper.ProfileEntry.COLUMN_NAME_BIRTHDAY));
        user.put(ParseDbHelper.ProfileEntry.COLUMN_NAME_LANGUAGE
                , profile.getValue(ParseDbHelper.ProfileEntry.COLUMN_NAME_LANGUAGE));
        user.put(ParseDbHelper.ProfileEntry.COLUMN_NAME_COUNTRY
                , profile.getValue(ParseDbHelper.ProfileEntry.COLUMN_NAME_COUNTRY));
        user.put(ParseDbHelper.ProfileEntry.COLUMN_NAME_CERTIFICATION
                , profile.getValue(ParseDbHelper.ProfileEntry.COLUMN_NAME_CERTIFICATION));
        user.put(ParseDbHelper.ProfileEntry.COLUMN_NAME_ORGANIZATION
                , profile.getValue(ParseDbHelper.ProfileEntry.COLUMN_NAME_ORGANIZATION));
        user.put(ParseDbHelper.ProfileEntry.COLUMN_NAME_ADD_CERT
                , profile.getValue(ParseDbHelper.ProfileEntry.COLUMN_NAME_ADD_CERT));
        user.put(ParseDbHelper.ProfileEntry.COLUMN_NAME_PROFILE_PIC
                , "");
        user.put(ParseDbHelper.ProfileEntry.COLUMN_NAME_PASSWORD, pass);


        user.signUp();

        return user.getSessionToken();
    }

    public String userSignIn(String username, String pass, String authType) throws Exception {

        Log.d(MainActivity.APP_NAME, "userSignIn");

        ParseUser user = new ParseUser();
        ParseUser.logIn(username, pass);

        return user.getSessionToken();
    }

    /**
     * get datetime
     * */
    protected static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}
