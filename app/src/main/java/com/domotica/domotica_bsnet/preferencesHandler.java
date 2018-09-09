package com.domotica.domotica_bsnet;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;

import java.util.Calendar;
import java.util.Random;

import soulissclient.Constants;

import static soulissclient.Constants.TAG;
import static soulissclient.Constants.prefs.*;

/**
 * Created by jctejero on 17/09/2017.
 */

public class preferencesHandler {
    private Context cntx;

    private int         userIndex;
    private int         nodeIndex;
    private Integer     UDPPort;
    private int         ServiceInterval;
    private boolean     dataService;
    private boolean     eyeActionbar;


    private SharedPreferences customCachedPrefs;

    preferencesHandler(Context cntx){
        this.cntx = cntx;
        //customCachedPrefs = contx.getSharedPreferences("SoulissPrefs", Activity.MODE_PRIVATE);
        customCachedPrefs = PreferenceManager.getDefaultSharedPreferences(cntx);
        initializePrefs();
        // Log.d(TAG, "Constructing prefs");
    };

    public void initializePrefs() {
        // Get the xml/preferences.xml preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(cntx);

        userIndex       = prefs.getInt(USER_INDEX, -1);
        nodeIndex       = prefs.getInt(NODE_INDEX, -1);
        UDPPort         = prefs.getInt(UDP_PORT, Constants.Net.DEFAULT_SOULISS_PORT);
        ServiceInterval = prefs.getInt(SERVICE_INTERVAL_KEY, 15);
        dataService     = prefs.getBoolean(DATA_SERVICE_KEY, false);
        eyeActionbar    = prefs.getBoolean(EYE_ACTBAR_STATE, true);
    }

    public int getServiceInterval() {
        return ServiceInterval;
    }

    public int getServiceIntervalmsec() {
        return getServiceInterval()*1000;
    }


    static Random r = new Random(Calendar.getInstance().getTimeInMillis());
    public void reload() {
        Log.i(TAG, "Going thru preference reload()");
        // SharedPreferences prefs =
        // PreferenceManager.getDefaultSharedPreferences(contx);
        initializePrefs();

        Log.i(TAG, "Souliss Data Service Enabled? "+dataService);

        if (userIndex == -1) {// MAI inizializzato, lo calcolo
            /* USER INDEX, statico */

            int casual = r.nextInt(Constants.MAX_USER_IDX - 1);// 100
            setUserIndex(casual);
            Log.i(TAG, "automated userIndex-index Using: " + casual);
        }

        if (nodeIndex == -1) {// MAI inizializzato, lo calcolo
            /* PHONE ID diventa node index */
            try {
                nodeIndex = ((Settings.Secure.getString(cntx.getContentResolver(), Settings.Secure.ANDROID_ID)).hashCode() % (Constants.MAX_NODE_IDX - 1));
                nodeIndex = Math.abs(nodeIndex);
                if (nodeIndex == 0)
                    nodeIndex++;
                Log.w(TAG, "Pref init END. Node index = " + nodeIndex);
                setNodeIndex(nodeIndex);
            } catch (Exception e) {// fallito il computo, uso random e lo salvo
                //Random r = new Random(Calendar.getInstance().getTimeInMillis());
                int casual = r.nextInt(Constants.MAX_NODE_IDX);
                setNodeIndex(casual);
                Log.e(Constants.TAG, "automated Node-index fail " + e.getMessage() + ". Using " + casual);
            }
        }
    }

    public Context getCntx() {
        return cntx;
    }

    public Integer getUDPPort() {
        return UDPPort;
    }

    public void setUDPPort(Integer UDPPort) {
        this.UDPPort = UDPPort;
        SharedPreferences.Editor editor = customCachedPrefs.edit();
        editor.putInt(UDP_PORT, this.UDPPort);
        editor.apply();
    }

    public int getUserIndex() {
        return userIndex;
    }

    public void setUserIndex(int userIndex) {
        this.userIndex = userIndex;
        SharedPreferences.Editor editor = customCachedPrefs.edit();
        editor.putInt(USER_INDEX, userIndex);
        editor.apply();
    }

    public int getNodeIndex() {
        return nodeIndex;
    }

    public void setNodeIndex(int nodeIndex) {
        this.nodeIndex = nodeIndex;
        SharedPreferences.Editor editor = customCachedPrefs.edit();
        editor.putInt(NODE_INDEX, nodeIndex);
        editor.apply();
    }

    public boolean  getEyeState() {return eyeActionbar;}
    public void     setEyeState(boolean eyestate) {
        this.eyeActionbar = eyestate;
        SharedPreferences.Editor editor = customCachedPrefs.edit();
        editor.putBoolean(EYE_ACTBAR_STATE, eyestate);
        editor.apply();
    }

    public boolean isDataServiceEnabled() {
        return dataService;
    }
}
