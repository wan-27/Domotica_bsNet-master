package com.domotica.domotica_bsnet;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.domotica.domotica_bsnet.Utils.Prefs;
import com.domotica.domotica_bsnet.db.bsnetDBHelper;
import com.domotica.domotica_bsnet.fragments.DevicesFragment;
//import com.domotica.domotica_bsnet.fragments.DevicesFragment;

import java.util.ArrayList;

import soulissclient.Constants;
import soulissclient.Service.SoulissNetService;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private LinearLayout mLinearLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;

    private boolean doubleBackToExitPressedOnce = false;

    private static preferencesHandler   prefsHandle;
    private static bsnetDBHelper        bsnet;
    private static Context              ctxt;

    private SoulissNetService           mBoundService;

    /* SOULISS DATA SERVICE BINDINGS */
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {

            mBoundService = ((SoulissNetService.LocalBinder) service).getService();
            Log.i(Constants.TAG, "Dataservice connected");

            preferencesHandler pref = prefsHandle; //SoulissApp.getOpzioni();
            if (pref.isDataServiceEnabled()) {
                //will detect if late
                mBoundService.reschedule(false);
            } else {
                Log.w(Constants.TAG, "Dataservice DISABLED");
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            // Because it is running in our same process, we should never
            // see this happen.
            mBoundService = null;
            Toast.makeText(MainActivity.this, "Dataservice disconnected", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ctxt = getApplicationContext();
        prefsHandle = new preferencesHandler(ctxt);
        prefsHandle.reload();

        mTitle = mDrawerTitle = getTitle();

        build_sliderMenu();

        if (savedInstanceState == null) {

            displayView(0);
        }

        //Open Database
        bsnet = new bsnetDBHelper(ctxt);
        bsnet.open();

        //**** Init Service to communicate with Souliss Network ****
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                doBindService();    //Initialize service
            }
        });
    }

    private void doBindService() {
        Log.i(Constants.TAG, "doBindService(), BIND_AUTO_CREATE.");
        bindService(new Intent(MainActivity.this, SoulissNetService.class), mConnection, BIND_AUTO_CREATE);
    }

    @SuppressLint("ResourceType")
    private void build_sliderMenu(){
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mLinearLayout = (LinearLayout) findViewById(R.id.drawer_view);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        navDrawerItems = new ArrayList<NavDrawerItem>();

        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));

        navMenuIcons.recycle();

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);

        //Include Toolbar
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        //themeUtils.onActivityCreateSetTheme(this,getSupportActionBar(),this);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                mToolbar,
                R.string.app_name,
                R.string.app_name
        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);

                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);

                invalidateOptionsMenu();
            }
        };
        //mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
    }

    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position,
                                long id) {
            mDrawerLayout.closeDrawer(mLinearLayout);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    displayView(position);
                }
            }, 300);
        }
    }

    private void displayView(int position) {

        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new DevicesFragment();
                break;

            case 1:
                startActivity(new Intent(ctxt, Prefs.class));
                break;

            case 2:
                finish();
                break;

            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();

                    fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();

            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position]);
        } else {

            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }



    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed(){
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        String noDevMsg = getString(R.string.ExitMsg);
        Toast.makeText(this, noDevMsg, Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2500);
    }

    public void finish(){
        super.finish();
    }

    public static preferencesHandler getPrefs() {
        return prefsHandle;
    }

    public static bsnetDBHelper getbsnet(){
        return bsnet;
    }

    public static Context getCtxt(){
        return ctxt;
    }
}
