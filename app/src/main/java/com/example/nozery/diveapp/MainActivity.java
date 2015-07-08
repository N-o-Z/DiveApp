package com.example.nozery.diveapp;

import android.app.FragmentManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.app.ActionBar;

public class MainActivity extends ActionBarActivity implements
        MapFragment.OnMapInteractionListener, BoardFragment.OnBoardInteractionListener,
        SearchFragment.OnSearchInteractionListener, ProfileFragment.OnProfileInteractionListener {

    private Button mMapButton;
    private Button mBoardButton;
    private Button mSearchButton;
    private Button mProfileButton;

    private MapFragment mMapFragment;
    private static BoardFragment mBoardFragment;
    private static SearchFragment mSearchFragment;
    private static ProfileFragment mProfileFragment;

    private FragmentManager mManager;

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    private void switchToFragment(android.app.Fragment frag) {
        mManager.beginTransaction()
                .replace(R.id.fragment_container, frag)
                .addToBackStack(null).commit();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mManager = getFragmentManager();

        //TODO: Decide on fragment input
        //final MapFragment
        mMapFragment = MapFragment.newInstance("a","b");
        mBoardFragment = BoardFragment.newInstance("a","b");
        mSearchFragment = SearchFragment.newInstance("a","b");
        mProfileFragment = ProfileFragment.newInstance("a","b");

        switchToFragment(mMapFragment);

        //Initializing buttons and listeners
        mMapButton = (Button) findViewById(R.id.btn_main_map);
        mMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToFragment(mMapFragment);
            }
        });
        mBoardButton = (Button) findViewById(R.id.btn_main_board);
        mBoardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToFragment(mBoardFragment);
            }

        });
        mSearchButton = (Button) findViewById(R.id.btn_main_search);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToFragment(mSearchFragment);
            }

        });
        mProfileButton = (Button) findViewById(R.id.btn_main_profile);
        mProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToFragment(mProfileFragment);
            }
        });
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
    public void onMapInteraction(Uri uri) {

    }

    @Override
    public void onBoardInteraction(Uri uri) {

    }

    @Override
    public void onSearchInteraction(Uri uri) {

    }

    @Override
    public void onProfileInteraction(Uri uri) {

    }
}
