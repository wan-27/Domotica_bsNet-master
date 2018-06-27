package com.domotica.domotica_bsnet.Network;

import java.util.ArrayList;

import soulissclient.Constants;

/**
 * Created by jctejero on 09/11/2017.
 */

public class bsnetDevice {
    public static final int TYPE_GATEWAY    = 0;
    public static final int TYPE_NODE       = 1;
    public static final int MAX_CNTDIS      = 3;

    public int          position        = 0;
    public int          responseTime    = 0; // ms
    public String       ipAddress       = null;
    public String       hardwareAddress = NetInfo.NOMAC;
    public String       hostname        = null;
    public String       ssid;
    public DiscoverFase discoverFase    = DiscoverFase.DIS_PING;

    //public int      isAlive         = 0;

    //Souliss
    public int              deviceType  = TYPE_GATEWAY;
    public int              slots       = 0;
    public ArrayList<Short> typicals    = null;
    public int              typical;

    public int              cntDis      = 0;

    public bsnetDevice(){

    }

    public bsnetDevice(String ipAddress, String hardwareAddress, String ssid){
        this.ipAddress = ipAddress;
        this.hardwareAddress = hardwareAddress;
        this.ssid = ssid;
    }

    public bsnetDevice(bsnetDevice host){
        this.position           = host.position;
        this.ipAddress          = host.ipAddress;
        this.hardwareAddress    = host.hardwareAddress;
        this.hostname           = host.hostname;
        this.ssid               = host.ssid;
        this.discoverFase       = host.discoverFase;

        this.deviceType         = host.deviceType;
        this.slots              = host.slots;
        this.typicals           = host.typicals;
        this.typical            = host.typical;
    }

    public enum DiscoverFase {
        DIS_PING,
        DIS_GATEWAY,
        DIS_DB_STRUCT,
        DIS_TIPYCAL,
        DIS_ACTIVE;

        public static byte Souliss_UDP(DiscoverFase df){
            switch (df){
                case DIS_PING:
                    return Constants.Net.Souliss_UDP_function_ping;
                case DIS_GATEWAY:
                    return Constants.Net.Souliss_UDP_function_ping_bcast;
                case DIS_DB_STRUCT:
                    return Constants.Net.Souliss_UDP_function_db_struct;
                case DIS_TIPYCAL:
                    return Constants.Net.Souliss_UDP_function_typreq;
            }
            return -1;
        }
    }
}
