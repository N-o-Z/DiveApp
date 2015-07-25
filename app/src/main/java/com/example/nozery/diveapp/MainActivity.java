package com.example.nozery.diveapp;

import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.content.Intent;
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
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Calendar;

public class MainActivity extends ActionBarActivity implements
        DiveMapFragment.OnMapInteractionListener, BoardFragment.OnBoardInteractionListener,
        SearchFragment.OnSearchInteractionListener, ProfileFragment.OnProfileInteractionListener,
        DatePickerDialog.OnDateSetListener {

    protected static final String APP_NAME = "DiveApp";
    private final String TAG = getClass().getSimpleName();
    private static final int LOGIN_SIGN_UP = 1;

    //Device SDK Version
    final static int SDK_VERSION = Build.VERSION.SDK_INT;

    //Debug clear app cache flag
    public final boolean CLEAR_APP_CACHE = false;

    /* Test account auth

     */
    private AccountManager mAccountManager;
    private AlertDialog mAlertDialog;
    private boolean mInvalidate;
    private static final String STATE_DIALOG = "state_dialog";
    private static final String STATE_INVALIDATE = "state_invalidate";

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
    protected static ParseDbHelper mAppDbHelper = new ParseDbHelper();

    //Data members
    private UserProfile mWorkingProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean debug = (0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
        if(debug) {
            if(CLEAR_APP_CACHE) {
                clearApplicationData();
            }
        }

        setContentView(R.layout.activity_main);


        if (!mAppDbHelper.init()) {
            Intent intent = new Intent(this, SignUpActivity.class);

            //Start Log in Activity
            startActivityForResult(intent,LOGIN_SIGN_UP);
        }
        else {
            initApp();
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case LOGIN_SIGN_UP: {
                if(RESULT_OK == resultCode) {
                    initApp();
                }
                else {
                    //TODO: block all app logic
                }
                break;
            }
        }
    }

    private void initApp() {
        initializeData();
        mManager = getFragmentManager();

        //TODO: Decide on fragment input
        //final DiveMapFragment
        mDiveMapFragment = DiveMapFragment.newInstance("a", "b");
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

    private void getUserProfile() {

        mWorkingProfile = mAppDbHelper.getCurrentProfile();
        if("" == mWorkingProfile.getValue(ParseDbHelper.ProfileEntry.COLUMN_NAME_PROFILE_PIC)) {
            Drawable pPicture;
            pPicture = getDrawable(getResources(), R.drawable.profile);
            mWorkingProfile.setValue(
                    ParseDbHelper.ProfileEntry.COLUMN_NAME_PROFILE_PIC, encodeImage(pPicture));
            mAppDbHelper.updateProfile(mWorkingProfile);
         }
        /*mUserProfiles = mAppDbHelper.getUserProfiles();
        if(1 > mUserProfiles.size()) {
            //WA create default profile
            //TODO: Handle - Not supposed to happen (it should be populated on registration)
            //TODO: Maybe send to registration page??

            UserProfile profile = new UserProfile();
            Drawable pPicture;
            pPicture = getDrawable(getResources(), R.drawable.profile);
            profile.setValue("profilePic", encodeImage(pPicture));
            profile.setValue("username", "TestingUsername");

            mAppDbHelper.createProfile(profile);
            //      mUserProfiles.add(profile);
        }
        //  else {
        mUserProfiles = mAppDbHelper.getUserProfiles();
        //  }
        getWorkingProfile();*/

    }

    //Initialize data from app DB
    private void initializeData() {

        getUserProfile();
        //Get more data

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
        mAppDbHelper.updateProfile(mWorkingProfile);
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

    protected void showMessage(final String msg) {
        if (TextUtils.isEmpty(msg))
            return;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
