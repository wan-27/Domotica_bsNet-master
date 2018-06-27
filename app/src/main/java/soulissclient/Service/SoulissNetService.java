package soulissclient.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.domotica.domotica_bsnet.MainActivity;
import com.domotica.domotica_bsnet.preferencesHandler;

import soulissclient.Constants;
import soulissclient.net.UDPRunnable;

/**
 * Created by jctejero on 05/10/2017.
 *
 * Based on SoulissDataService.java from soulissApp
 */

public class SoulissNetService extends Service {

    private final IBinder mBinder = new LocalBinder();

    private preferencesHandler prefsHandle;
    private Thread udpThread;

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();


        Log.w(Constants.TAG, "service onCreate()");
        prefsHandle = MainActivity.getPrefs();

        startUDPListener();
    }

    private void startUDPListener() {

        if (udpThread == null || !udpThread.isAlive() || udpThread.isInterrupted()) {
            // Create the object with the run() method
            Runnable runnable = new UDPRunnable(prefsHandle);
            // Create the udpThread supplying it with the runnable
            // object
            udpThread = new Thread(runnable);
            // Start the udpThread
            udpThread.start();
            Log.i(Constants.TAG, "UDP thread started");
        }
    }

    /**
     * Schedule a new execution of the srvice via mHandler.postDelayed
     */
    public void reschedule(boolean immediate) {
/*
        opts.initializePrefs();// reload interval

        // the others
        // reschedule self
        Calendar calendar = Calendar.getInstance();
        if (immediate) {
            mHandler.removeCallbacks(mUpdateSoulissRunnable);// the first removes
            Log.i(TAG, "Reschedule immediate");
            mHandler.post(mUpdateSoulissRunnable);
        } else {
            Log.i(TAG, "Regular mode, rescheduling self every " + opts.getDataServiceIntervalMsec() / 1000 + " seconds");

            if (getLastupd().getTime().getTime() + opts.getBackedOffServiceIntervalMsec() < Calendar.getInstance().getTime().getTime()) {
                Log.i(TAG, "DETECTED LATE SERVICE, LAST RUN: " + getLastupd().getTime());

                reschedule(true);
                return;
            }

            // One of the two should get it
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

            PendingIntent secureShot = PendingIntent.getService(this, 0, cIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            calendar.setTimeInMillis(getLastupd().getTime().getTime());
            calendar.add(Calendar.MILLISECOND, opts.getBackedOffServiceIntervalMsec().intValue());
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), secureShot);
            //will call onStart(), detect late and schedule immediate
            Log.i(TAG, "DATASERVICE SCHEDULED ON: " + calendar.getTime());

        }
        startUDPListener();
*/
    }

    /**
     * Class for clients to access. Because we know this service always runs in
     * the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public SoulissNetService getService() {
            return SoulissNetService.this;
        }
    }
}
