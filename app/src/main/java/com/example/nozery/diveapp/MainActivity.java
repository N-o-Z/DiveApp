package com.example.nozery.diveapp;

import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import com.parse.Parse;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends ActionBarActivity implements
        DiveMapFragment.OnMapInteractionListener, BoardFragment.OnBoardInteractionListener,
        SearchFragment.OnSearchInteractionListener, ProfileFragment.OnProfileInteractionListener,
        DatePickerDialog.OnDateSetListener {

    //Device SDK Version
    final static int SDK_VERSION = Build.VERSION.SDK_INT;

    //Debug clear app cache flag
    public final boolean CLEAR_APP_CACHE = false;

    //UI members
    private Button mMapButton;
    private Button mBoardButton;
    private Button mSearchButton;
    private Button mProfileButton;

    //Fragments members
    private DiveMapFragment mDiveMapFragment;
    private BoardFragment mBoardFragment;
    private SearchFragment mSearchFragment;
    private ProfileFragment mProfileFragment;
    private FragmentManager mManager;
    static protected FragmentsEnum mWorkingFrag;

    //DB members
    MyDbHelper mAppDbHelper;

    //Data members
    List<UserProfile> mUserProfiles;
    ArrayList<DiveBasePoi> mDivepois;

    private UserProfile mWorkingProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean debug = (0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
        if(debug) {
            if(CLEAR_APP_CACHE) {
              clearApplicationData();
            }
        }

        mAppDbHelper = new MyDbHelper(getApplicationContext());

        mManager = getFragmentManager();
        initializeData();

        //TODO: Decide on fragment input
        //final DiveMapFragment
        mDiveMapFragment = DiveMapFragment.newInstance(mDivepois);
        mBoardFragment = BoardFragment.newInstance("a","b");
        mSearchFragment = SearchFragment.newInstance("a","b");
        mProfileFragment = ProfileFragment.newInstance(mWorkingProfile.getData());

        //TODO: consider implementation
        switchToFragment(mDiveMapFragment, FragmentsEnum.MAP);

        //Initializing buttons and listeners
        mMapButton = (Button) findViewById(R.id.btn_main_map);
        mMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToFragment(mDiveMapFragment, FragmentsEnum.MAP);
            }
        });
        mBoardButton = (Button) findViewById(R.id.btn_main_board);
        mBoardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToFragment(mBoardFragment, FragmentsEnum.BOARD);
            }

        });
        mSearchButton = (Button) findViewById(R.id.btn_main_search);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToFragment(mSearchFragment, FragmentsEnum.SEARCH);
            }

        });
        mProfileButton = (Button) findViewById(R.id.btn_main_profile);
        mProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToFragment(mProfileFragment, FragmentsEnum.PROFILE);
            }
        });

        // Enable Local Datastore.
        //TODO: Need to move this to the app initial activity maybe it's this one?
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "gsvpCsYn9FbHDvjiBhOZoZyA5toqS3JM81WhHuIK"
                , "OHHJXQH4eMTQvik4uvrW80nZxnecayjrKqMMBH4k");

        // Test block for Parse
        /*
        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();
        */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    private void switchToFragment(android.app.Fragment frag, FragmentsEnum newFrag) {

        if(mWorkingFrag == newFrag) {
            return;
        }

        if(null != mWorkingFrag) {
            setSeparatorVisibility(View.INVISIBLE);
        }
        mWorkingFrag = newFrag;
        setSeparatorVisibility(View.VISIBLE);

        mManager.beginTransaction()
                .replace(R.id.fragment_container, frag)
                .addToBackStack(null).commit();
    }

    private void setSeparatorVisibility(int visibility ) {
        switch(mWorkingFrag) {
            case MAP:
                findViewById(R.id.map_separator).setVisibility(visibility);
                break;
            case BOARD:
                findViewById(R.id.board_separator).setVisibility(visibility);
                break;
            case SEARCH:
                findViewById(R.id.search_separator).setVisibility(visibility);
                break;
            case PROFILE:
                findViewById(R.id.profile_separator).setVisibility(visibility);
                break;
        }
    }

    private void getWorkingProfile() {

        //WA Currently providing first element
        //TODO: this is for future multi profile support (consider implementation)
        mWorkingProfile = mUserProfiles.get(0);

    }

    private void getUserProfiles() {
        mUserProfiles = mAppDbHelper.getUserProfiles();
        if(1 > mUserProfiles.size()) {
            //WA create default profile
            //TODO: Handle - Not supposed to happen (it should be populated on registration)
            //TODO: Maybe send to registration page??
            UserProfile profile = new UserProfile();
            Drawable pPicture;
            pPicture = getDrawable(getResources(), R.drawable.profile);

            profile.setValue("username", "TestingUsername");
            profile.setValue("profilePic", encodeImage(pPicture));
            mAppDbHelper.createProfile(profile);
            //      mUserProfiles.add(profile);
        }
        //  else {
        mUserProfiles = mAppDbHelper.getUserProfiles();
        //  }
        getWorkingProfile();
    }

    private void getPois() {

        mDivepois = mAppDbHelper.getDivePois();
    }
    
    //Initialize data from app DB
    private void initializeData() {

        // TODO remove this test after parse is ready
        mAppDbHelper.createTestPoi();

        getUserProfiles();
        //Get more data

        getPois();

    }

    @Override
    public void onMapInteraction(Uri uri) {

    }

    @Override
    public void onBoardInteraction(Uri uri) {

    }

    @Override
    public void onSearchInteraction(Uri uri) {

    }

    @Override
    public void onProfileInteraction(UserProfile profile) {

        mWorkingProfile = profile;
        mAppDbHelper.updateProfile(profile);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        final Calendar c = Calendar.getInstance();
        c.set(year, monthOfYear, dayOfMonth);
        //TODO: Complete or not
    }

    protected enum FragmentsEnum {
        MAP, BOARD, SEARCH, PROFILE
    }

    protected static Drawable getDrawable(Resources r, int id) {
        if (SDK_VERSION >= 21) {
            return r.getDrawable(id, null);
        } else {
            return r.getDrawable(id);
        }
    }

    protected static String encodeImage(Drawable image) {

        Bitmap bitmap;
        bitmap = ((BitmapDrawable) image).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);
    }

    protected static Drawable decodeImage(Resources rs, String image) {
        byte[] byteArray = Base64.decode(image, Base64.DEFAULT);
        return new BitmapDrawable(rs
                , BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length));
    }

    private void clearApplicationData() {
        File cache = getCacheDir();
        File appDir = new File(cache.getParent());
        if(appDir.exists()){
            String[] children = appDir.list();
            for(String s : children){
                if(!s.equals("lib")){
                    deleteDir(new File(appDir, s));
                    Log.i("TAG", "File /data/data/APP_PACKAGE/" + s + " DELETED");
                }
            }
        }
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        return dir.delete();
    }
}
