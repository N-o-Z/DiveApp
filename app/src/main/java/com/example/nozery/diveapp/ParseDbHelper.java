package com.example.nozery.diveapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

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
    private static final String TABLE_DIVE_SITES_POI = "dive_site_poi";

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

    // Dive POI table create statement
    private static final String CREATE_TABLE_DIVE_SITES_POI = "CREATE TABLE "
            +DiveSitesPoiEntry.TABLE_NAME  + "("
            +DiveSitesPoiEntry.COLUMN_NAME_LATITUDE + " TEXT,"
            +DiveSitesPoiEntry.COLUMN_NAME_LONGITUDE + " TEXT,"
            +DiveSitesPoiEntry.COLUMN_NAME_POI_NAME + " TEXT,"
            +DiveSitesPoiEntry.COLUMN_NAME_DESCRIPTION + " TEXT,"
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

    public static abstract class DiveSitesPoiEntry implements BaseColumns {
        public static final String TABLE_NAME = TABLE_DIVE_SITES_POI;
        public static final String COLUMN_NAME_LATITUDE = "DsLat";
        public static final String COLUMN_NAME_LONGITUDE = "DsLng";
        public static final String COLUMN_NAME_POI_NAME = "DsName";
        public static final String COLUMN_NAME_DESCRIPTION = "DsDesc";
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

    /**
     * getting all dive pois
     * */
    public ArrayList<DiveBasePoi> getDivePois() {
        final ArrayList<DiveBasePoi> poisList = new ArrayList<>();

        ParseQuery<ParseObject> query = ParseQuery.getQuery(TABLE_DIVE_SITES_POI);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, com.parse.ParseException e) {
                Iterator keys;
                String key;
                for(ParseObject object : list) {
                    DiveSite divePoi = new DiveSite();
                    keys = object.keySet().iterator();
                    while(keys.hasNext()) {
                        key = keys.next().toString();
                        divePoi.setValue(key, object.getString(key));
                    }
                    poisList.add(divePoi);
                }
            }
        });

        /*String selectQuery = "SELECT  * FROM " + TABLE_DIVE_SITES_POI;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                DiveSite divePoi = new DiveSite();
                for(int i=0; i<c.getColumnCount(); i++) {
                    String colName = c.getColumnName(i);
                    if(colName.equals(KEY_CREATED_AT)) {
                        continue;
                    }
                    divePoi.setValue(colName,c.getString(i));
                }

                // adding to poi list
                poisList.add(divePoi);
            } while (c.moveToNext());
        }
        return poisList;*/
        return poisList;
    }

    /*
    * This function will add POIs to local DB for testing.
    * TODO: remove this after parse DB is ready.
    */
    public long createTestPoi()
    {
        ArrayList<DiveSite> pois = new ArrayList<DiveSite>() ;
        DiveSite poi = new DiveSite();

        // Adding Dive sites
        //poi.setValue(DiveSitesPoiEntry.COLUMN_NAME_LATITUDE, "29.539050");
        //poi.setValue(DiveSitesPoiEntry.COLUMN_NAME_LONGITUDE, "34.945576");
        //poi.setValue(DiveSitesPoiEntry.COLUMN_NAME_POI_NAME, "Barracuda");
        //poi.setValue(DiveSitesPoiEntry.COLUMN_NAME_TYPE, "club");
        //poi.setValue(DiveSitesPoiEntry.COLUMN_NAME_DESCRIPTION, "Barracuda dive club");
        //pois.add(new DivePoi(poi));

        //poi.setValue(DiveSitesPoiEntry.COLUMN_NAME_LATITUDE, "29.514708");
        //poi.setValue(DiveSitesPoiEntry.COLUMN_NAME_LONGITUDE, "34.926157");
        //poi.setValue(DiveSitesPoiEntry.COLUMN_NAME_POI_NAME, "Marina Divers");
        //poi.setValue(DiveSitesPoiEntry.COLUMN_NAME_TYPE, "club");
        //poi.setValue(DiveSitesPoiEntry.COLUMN_NAME_DESCRIPTION, "Marina Divers dive club");
        //pois.add(new DivePoi(poi));

        //poi.setValue(DiveSitesPoiEntry.COLUMN_NAME_LATITUDE, "29.498452");
        //poi.setValue(DiveSitesPoiEntry.COLUMN_NAME_LONGITUDE, "34.911720");
        //poi.setValue(DiveSitesPoiEntry.COLUMN_NAME_POI_NAME, "Snuba");
        //poi.setValue(DiveSitesPoiEntry.COLUMN_NAME_TYPE, "club");
        //poi.setValue(DiveSitesPoiEntry.COLUMN_NAME_DESCRIPTION, "Snuba dive club");
        //pois.add(new DivePoi(poi));

        poi.setValue(DiveSitesPoiEntry.COLUMN_NAME_LATITUDE, "29.538644");
        poi.setValue(DiveSitesPoiEntry.COLUMN_NAME_LONGITUDE, "34.946070 ");
        poi.setValue(DiveSitesPoiEntry.COLUMN_NAME_POI_NAME, "The Big Canyon");
        poi.setValue(DiveSitesPoiEntry.COLUMN_NAME_DESCRIPTION, "Big Canyon dive site");
        pois.add(new DiveSite(poi));

        poi.setValue(DiveSitesPoiEntry.COLUMN_NAME_LATITUDE, "29.514631");
        poi.setValue(DiveSitesPoiEntry.COLUMN_NAME_LONGITUDE, "34.926656");
        poi.setValue(DiveSitesPoiEntry.COLUMN_NAME_POI_NAME, "Satil Wreck");
        poi.setValue(DiveSitesPoiEntry.COLUMN_NAME_DESCRIPTION, "Satil wreck dive site");
        pois.add(new DiveSite(poi));

        poi.setValue(DiveSitesPoiEntry.COLUMN_NAME_LATITUDE, "29.513088");
        poi.setValue(DiveSitesPoiEntry.COLUMN_NAME_LONGITUDE, "34.926887");
        poi.setValue(DiveSitesPoiEntry.COLUMN_NAME_POI_NAME, "Yatush Wreck");
        poi.setValue(DiveSitesPoiEntry.COLUMN_NAME_DESCRIPTION, "Yatush wreck dive site");
        pois.add(new DiveSite(poi));

        poi.setValue(DiveSitesPoiEntry.COLUMN_NAME_LATITUDE, "29.497689");
        poi.setValue(DiveSitesPoiEntry.COLUMN_NAME_LONGITUDE, "34.912096");
        poi.setValue(DiveSitesPoiEntry.COLUMN_NAME_POI_NAME, "The Caves");
        poi.setValue(DiveSitesPoiEntry.COLUMN_NAME_DESCRIPTION, " The caves dive site");
        pois.add(new DiveSite(poi));

        return (createDiveSitesPoiTable(pois));
    }

    /*
     * Creating Dive POI table
     */
    public long createDiveSitesPoiTable(ArrayList<DiveSite> divePois) {

       /* ParseObject diveSite = new ParseObject(TABLE_DIVE_SITES_POI);
        diveSite.put(DiveSitesPoiEntry.COLUMN_NAME_POI_NAME,"testName");
        diveSite.put(DiveSitesPoiEntry.COLUMN_NAME_DESCRIPTION,"testDesc");
        diveSite.put(DiveSitesPoiEntry.COLUMN_NAME_LATITUDE,"0.0");
        diveSite.put(DiveSitesPoiEntry.COLUMN_NAME_LONGITUDE,"0.0");
        diveSite.saveInBackground();
*/
    /*    SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        //TODO: add list to DB table

        Iterator<DiveSite> it = divePois.iterator() ;
        DiveSite poi;

        while (it.hasNext()) {

            poi = it.next() ;

            values.put(DiveSitesPoiEntry.COLUMN_NAME_LATITUDE
                    , poi.getValue(DiveSitesPoiEntry.COLUMN_NAME_LATITUDE));
            values.put(DiveSitesPoiEntry.COLUMN_NAME_LONGITUDE
                    , poi.getValue(DiveSitesPoiEntry.COLUMN_NAME_LONGITUDE));
            values.put(DiveSitesPoiEntry.COLUMN_NAME_POI_NAME
                    , poi.getValue(DiveSitesPoiEntry.COLUMN_NAME_POI_NAME));
            values.put(DiveSitesPoiEntry.COLUMN_NAME_DESCRIPTION
                    , poi.getValue(DiveSitesPoiEntry.COLUMN_NAME_DESCRIPTION));


            // insert row
            db.insert(TABLE_DIVE_SITES_POI, null, values);
            values.clear();
        }
*/
        return (0);
    }

    /*
  * Updating Dive Sites POI
  */
    public int updateDiveSitesPoiTable(List<DiveSite> divePois) {

        /*SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        Iterator<DiveSite> it =  divePois.iterator();

        int numOfRowsupdated = 0 ;

        while(it.hasNext())
        {
            values.put(DiveSitesPoiEntry.COLUMN_NAME_LATITUDE
                    , it.next().getValue(DiveSitesPoiEntry.COLUMN_NAME_LATITUDE));
            values.put(DiveSitesPoiEntry.COLUMN_NAME_LONGITUDE
                    , it.next().getValue(DiveSitesPoiEntry.COLUMN_NAME_LONGITUDE));
            values.put(DiveSitesPoiEntry.COLUMN_NAME_DESCRIPTION
                    , it.next().getValue(DiveSitesPoiEntry.COLUMN_NAME_DESCRIPTION));

            // updating row
            //db.update(TABLE_DIVE_POI, values, DiveSitesPoiEntry.COLUMN_NAME_USERNAME + " = ?",
            //        new String[]{it.next().getValue(DiveSitesPoiEntry.COLUMN_NAME_USERNAME)});
        }*/

        return (0);
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
