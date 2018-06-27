/*
 * Copyright (C) 2009-2010 Aubort Jean-Baptiste (Rorist)
 * Licensed under GNU's GPL 2, see README
 */

package com.domotica.domotica_bsnet;

import android.os.Handler;
import android.util.Log;

import com.domotica.domotica_bsnet.Network.HardwareAddress;
import com.domotica.domotica_bsnet.Network.NetInfo;
import com.domotica.domotica_bsnet.Network.RateControl;
import com.domotica.domotica_bsnet.Network.bsnetDevice;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import soulissclient.Constants;
import soulissclient.net.UDPHelper;

public class DefaultDiscovery extends AbstractDiscovery {

    private final String TAG = "DefaultDiscovery";
    private final static int[] DPORTS = { 139, 445, 22, 80 };
    private final static int TIMEOUT_SCAN = 3600; // seconds
    private final static int TIMEOUT_SHUTDOWN = 10; // seconds
    private final static int THREADS = 10; // Test, plz set in options again ?
    private final int mRateMult = 5; // Number of alive hosts between Rate
    private int pt_move = 2; // 1=backward 2=forward
    private ExecutorService mPool;
    private final boolean doRateControl = true;
    private RateControl mRateControl;
//    private Save mSave;
    private Handler timeoutHandler;
    private static preferencesHandler prefs;


    public DefaultDiscovery(ActivityScan discover) {
        super(discover);

        timeoutHandler = new Handler();

        prefs = MainActivity.getPrefs();

        mRateControl = new RateControl();
//        mSave = new Save();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (mDiscover != null) {
            final ActivityScan discover = mDiscover.get();
            if (discover != null) {
                Log.v(TAG, "start=" + NetInfo.getIpFromLongUnsigned(start) + " (" + start
                        + "), end=" + NetInfo.getIpFromLongUnsigned(end) + " (" + end
                        + "), length=" + size);
                mPool = Executors.newFixedThreadPool(THREADS);
                if (ip <= end && ip >= start) {
                    Log.i(TAG, "Back and forth scanning");
                    // gateway
                    launch(start);

                    // hosts
                    long pt_backward = ip;
                    long pt_forward = ip + 1;
                    long size_hosts = size - 1;

                    for (int i = 0; i < size_hosts; i++) {
                        // Set pointer if of limits
                        if (pt_backward <= start) {
                            pt_move = 2;
                        } else if (pt_forward > end) {
                            pt_move = 1;
                        }
                        // Move back and forth
                        if (pt_move == 1) {
                            launch(pt_backward);
                            pt_backward--;
                            pt_move = 2;
                        } else if (pt_move == 2) {
                            launch(pt_forward);
                            pt_forward++;
                            pt_move = 1;
                        }
                    }
                } else {
                    Log.i(TAG, "Sequencial scanning");
                    for (long i = start; i <= end; i++) {
                        launch(i);
                    }
                }

                mPool.shutdown();
                try {
                    if(!mPool.awaitTermination(TIMEOUT_SCAN, TimeUnit.SECONDS)){
                        mPool.shutdownNow();
                        Log.e(TAG, "Shutting down pool");
                        if(!mPool.awaitTermination(TIMEOUT_SHUTDOWN, TimeUnit.SECONDS)){
                            Log.e(TAG, "Pool did not terminate");
                        }
                    }
                } catch (InterruptedException e){
                    Log.e(TAG, e.getMessage());
                    mPool.shutdownNow();
                    Thread.currentThread().interrupt();
                } finally {
//                    mSave.closeDb();
                }
            }
        }
        return null;
    }

    @Override
    protected void onCancelled() {
        if (mPool != null) {
            synchronized (mPool) {
                mPool.shutdownNow();
                // Prevents some task to end (and close the Save DB)
            }
        }
        super.onCancelled();
    }

    private void launch(long i) {
        if(!mPool.isShutdown()) {
            mPool.execute(new CheckRunnable(NetInfo.getIpFromLongUnsigned(i)));
        }
    }

    private void CheckSouliss(bsnetDevice host) {
        //Ping to host to detect device type
        try {
            //host.discoverFase = bsnetDevice.DiscoverFase.DIS_PING;
            if(mDiscover.get().addHost(host) > 0) {
                Log.i(Constants.TAG, "Send ping to --> " + host.ipAddress);
                UDPHelper.ping(host.ipAddress, prefs);
                //UDPHelper.ping(host.ipAddress, prefs).getHostAddress();
            }
        } catch (Exception e) {
            Log.e(TAG, "ping to --> " + host.ipAddress + " error :" + e.getMessage(), e);
        }
    }

    private int getRate() {
        if (doRateControl) {
            return mRateControl.rate;
        }
/*
        if (mDiscover != null) {
            final ActivityDiscovery discover = mDiscover.get();
            if (discover != null) {
                return Integer.parseInt(discover.prefs.getString(Prefs.KEY_TIMEOUT_DISCOVER,
                        Prefs.DEFAULT_TIMEOUT_DISCOVER));
            }
        }
*/
        return 1;
    }

    private class CheckRunnable implements Runnable {
        private String  addr;
        private final static int    mRate = 1000;

        CheckRunnable(String addr) {
            this.addr = addr;
        }

        public void run() {
            if(isCancelled()) {
                publish(null);
            }
            Log.i(TAG, "run="+addr);
            // Create host object
            final bsnetDevice host = new bsnetDevice();
//            host.responseTime = getRate();
            host.responseTime = mRate;

            host.ipAddress = addr;
            try {
                InetAddress h = InetAddress.getByName(addr);

                //String nm = InetAddress.getByName(addr).getHostName();


                // Rate control check
                if (doRateControl && mRateControl.indicator != null && hosts_done % mRateMult == 0) {
                    mRateControl.adaptRate();
                }

                // Arp Check #1 --> Get devices connected to Access Point
                host.hardwareAddress = HardwareAddress.getHardwareAddress(addr);
                if(!NetInfo.NOMAC.equals(host.hardwareAddress)){
                    Log.e(Constants.TAG, "found using arp #1 "+addr);

                    CheckSouliss(host);

                    //publish(host);
                    return;
                }

                // Native InetAddress check
                if (h.isReachable(getRate())) {
                    Log.e(Constants.TAG, "found using InetAddress ping "+addr);
                    CheckSouliss(host);

                    // Set indicator and get a rate
                    if (doRateControl && mRateControl.indicator == null) {
                        mRateControl.indicator = addr;
                        mRateControl.adaptRate();
                    }

                    return;
                }

                // Arp Check #2
                host.hardwareAddress = HardwareAddress.getHardwareAddress(addr);
                if(!NetInfo.NOMAC.equals(host.hardwareAddress)){
                    Log.e(TAG, "found using arp #2 "+addr);
                    CheckSouliss(host);
                    //publish(host);
                    return;
                }

                // Arp Check #3
                host.hardwareAddress = HardwareAddress.getHardwareAddress(addr);
                if(!NetInfo.NOMAC.equals(host.hardwareAddress)){
                    Log.e(TAG, "found using arp #3 "+addr);
                    CheckSouliss(host);
                    //publish(host);
                    return;
                }
                publish(null);

            } catch (IOException e) {
                publish(null);
                Log.e(TAG, e.getMessage());
            } 
        }
    }

    private void publish(final bsnetDevice host) {
        hosts_done++;
        if(host == null){
            Log.i(TAG, "Publish - host - null");
            publishProgress( null);
            return; 
        }

        if (mDiscover != null) {

            Log.i(TAG, "Publish - mDiscover");

            final ActivityScan discover = mDiscover.get();
            if (discover != null) {
                // Mac Addr not already detected
                if(NetInfo.NOMAC.equals(host.hardwareAddress)){
                    host.hardwareAddress = HardwareAddress.getHardwareAddress(host.ipAddress);
                }

                // NIC vendor
//                host.nicVendor = HardwareAddress.getNicVendor(host.hardwareAddress);

                // Is gateway ?
                if (discover.net.gatewayIp.equals(host.ipAddress)) {
                    host.deviceType = bsnetDevice.TYPE_GATEWAY;
                }
            }
        }
        publishProgress(host);
    }
}
