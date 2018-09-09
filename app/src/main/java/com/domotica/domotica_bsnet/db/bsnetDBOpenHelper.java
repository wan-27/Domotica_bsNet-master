package com.domotica.domotica_bsnet.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import soulissclient.Constants;

/**
 * Created by jctejero on 31/10/2017.
 */

public class bsnetDBOpenHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME    = "bsnet.db";
    public static final String TABLE_NODES      = "nodes";

    /*
     * NODES TABLE
     */
    public static final String COLUMN_ID            = "_id";
    public static final String COLUMN_IPADDRESS     = "ipaddress";
    public static final String COLUMN_MACADDRESS    = "macaddress";
    public static final String COLUMN_GATEWAY       = "gateway";
    public static final String COLUMN_MAINTYPICAL   = "maintypical";
    public static final String COLUMN_HOSTNAME      = "hostname";
    public static final String COLUMN_ISALIVE       = "isalive";
    public static final String COLUMN_SLOTS         = "slots";
    public static final String COLUMN_SSID          = "ssid";
    public static final String[] ALLCOLUMNS_NODES = {COLUMN_ID, COLUMN_IPADDRESS, COLUMN_MACADDRESS, COLUMN_GATEWAY,
            COLUMN_MAINTYPICAL, COLUMN_HOSTNAME, COLUMN_ISALIVE, COLUMN_SLOTS, COLUMN_SSID};

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE_NODES = "create table " + TABLE_NODES
            + "( "
            // COLUMN DEF
            + COLUMN_ID + " integer primary key autoincrement, " + COLUMN_HOSTNAME + " TEXT, "
            + COLUMN_IPADDRESS + " TEXT, " + COLUMN_MACADDRESS + " TEXT, " + COLUMN_GATEWAY + " integer not null, "
            + COLUMN_MAINTYPICAL + " integer not null, " + COLUMN_ISALIVE + " integer not null, "
            + COLUMN_SLOTS + " integer not null, " + COLUMN_SSID + " TEXT " + ");";

    private Context context;

    /**
     * super wrapper createDB
     *
     * @param context
     */
    public bsnetDBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        Log.w(Constants.TAG, "DB on create " + database.getPath());
        database.execSQL(DATABASE_CREATE_NODES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.w(bsnetDBOpenHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion);

    }
}
