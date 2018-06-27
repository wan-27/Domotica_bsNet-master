package com.domotica.domotica_bsnet.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.domotica.domotica_bsnet.Network.bsnetDevice;

import java.util.ArrayList;

import soulissclient.Constants;

/**
 * Created by jctejero on 31/10/2017.
 */

public class bsnetDBHelper {
    private static bsnetDBOpenHelper bsnetDatabase;
    private final Context context;
    private static SQLiteDatabase database;
    private String ssid = null;

    public bsnetDBHelper(Context context) {
        bsnetDatabase = new bsnetDBOpenHelper(context);
        this.context = context;
    }

    public static synchronized SQLiteDatabase getDatabase() {
        return database;
    }

    public static synchronized void open() throws SQLException {
        if (database == null || !database.isOpen())
            database = bsnetDatabase.getWritableDatabase();
    }

    public void clean() {
        if (database != null && database.isOpen()) {
            database.execSQL("VACUUM");
        } else
            Log.w(Constants.TAG, "DB closed, clean() failed");
    }

    public void close() {
        bsnetDatabase.close();
        if (database != null && database.isOpen())
            database.close();
        else
            Log.w(Constants.TAG, "DB already closed");
    }

    public void clearDevices(){
        database.delete(bsnetDBOpenHelper.TABLE_NODES, null, null);
    }

    public void borrarDevice(String ip){
        database.delete(bsnetDBOpenHelper.TABLE_NODES, bsnetDBOpenHelper.COLUMN_IPADDRESS + " = '" + ip + "'",null);
    }

    public int createOrUpdateDevice(bsnetDevice host){
        int numReg = -1;

        ContentValues values = new ContentValues();

        //device isn't at the table

        String StrSelect = "select count(*) from " + bsnetDBOpenHelper.TABLE_NODES + " where " + bsnetDBOpenHelper.COLUMN_IPADDRESS + " = '" + host.ipAddress + "'";

        Cursor mCount = database.rawQuery(StrSelect, null);
        mCount.moveToFirst();
        int count = mCount.getInt(0);

        Log.i(Constants.TAG, StrSelect + " -- Count: " + count);

        //int count = mCount.getCount();

        if(count == 0){
            //I can insert new device
            Log.d(Constants.TAG, "database new device: "+host.ipAddress);
            values.put(bsnetDBOpenHelper.COLUMN_IPADDRESS, host.ipAddress);
            values.put(bsnetDBOpenHelper.COLUMN_MACADDRESS, host.hardwareAddress);
            values.put(bsnetDBOpenHelper.COLUMN_GATEWAY, host.deviceType);
            values.put(bsnetDBOpenHelper.COLUMN_MAINTYPICAL, host.typicals.get(0));
            values.put(bsnetDBOpenHelper.COLUMN_HOSTNAME, host.hostname);
            values.put(bsnetDBOpenHelper.COLUMN_ISALIVE, 1);
            values.put(bsnetDBOpenHelper.COLUMN_SLOTS, host.slots);
            values.put(bsnetDBOpenHelper.COLUMN_SSID, host.ssid);
            numReg = (int) database.insert(bsnetDBOpenHelper.TABLE_NODES, null, values);
        }

        return numReg;
    }

    public void sethostnameByip(String hostname, String ip){
        Log.i(Constants.TAG, "Set Host  " + hostname + " -- " + ip);

        ContentValues values = new ContentValues();
        values.put(bsnetDBOpenHelper.COLUMN_HOSTNAME, hostname);

        database.update( bsnetDBOpenHelper.TABLE_NODES, values, bsnetDBOpenHelper.COLUMN_IPADDRESS + " = '" + ip + "'", null);
    }

    public Cursor readHosts(){
        String strSelect = "SELECT * from " + bsnetDBOpenHelper.TABLE_NODES;

        return database.rawQuery(strSelect, null);
        /*
        mCount.moveToFirst();

        while(!mCount.isAfterLast()){
            Log.d(Constants.TAG, "ipaddress : "+ mCount.getString(mCount.getColumnIndex(bsnetDBOpenHelper.COLUMN_IPADDRESS)));
            mCount.moveToNext();
        }
        int count = mCount.getInt(0);
        mCount.close();
        */
    }

    public ArrayList<bsnetDevice> getHosts(){
        bsnetDevice host = null;
        ArrayList<bsnetDevice> hosts = null;
        String strSelect = "SELECT * from " + bsnetDBOpenHelper.TABLE_NODES;

        if(ssid != null){
            strSelect = strSelect + " WHERE ssid = '" + ssid +"'";
        }

        Cursor cursor = database.rawQuery(strSelect, null);

        cursor.moveToFirst();

        Log.i(Constants.TAG, "Record Count : " + cursor.getCount());

        while(!cursor.isAfterLast()){
            host = new bsnetDevice();
            host.ipAddress          = cursor.getString(cursor.getColumnIndex(bsnetDBOpenHelper.COLUMN_IPADDRESS));
            host.hostname           = cursor.getString(cursor.getColumnIndex(bsnetDBOpenHelper.COLUMN_HOSTNAME));
            host.deviceType         = cursor.getInt(cursor.getColumnIndex(bsnetDBOpenHelper.COLUMN_GATEWAY));
            host.typical            = cursor.getInt(cursor.getColumnIndex(bsnetDBOpenHelper.COLUMN_MAINTYPICAL));
            host.hardwareAddress    = cursor.getString(cursor.getColumnIndex(bsnetDBOpenHelper.COLUMN_MACADDRESS));
            host.ssid               = cursor.getString(cursor.getColumnIndex(bsnetDBOpenHelper.COLUMN_SSID));

            if(hosts == null)
                hosts = new ArrayList<bsnetDevice>();

            hosts.add(host);
            cursor.moveToNext();
        }

        //

        return hosts;
    }

    public void setssid(String netid){
        ssid = netid;
    }

    public String getssid(){
        return ssid;
    }
}
