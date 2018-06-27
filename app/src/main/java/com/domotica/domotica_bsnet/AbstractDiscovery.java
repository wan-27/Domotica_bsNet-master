package com.domotica.domotica_bsnet;

import android.os.AsyncTask;

import com.domotica.domotica_bsnet.Network.bsnetDevice;
import com.domotica.domotica_bsnet.Utils.Openweb;

import java.lang.ref.WeakReference;

public abstract class AbstractDiscovery extends AsyncTask<Void, bsnetDevice, Void> {

    private final String TAG = "AbstractDiscovery";

    private boolean cancelTask = false;

    protected int hosts_done = 0;
    final protected WeakReference<ActivityScan> mDiscover;

    protected long ip;
    protected long start = 0;
    protected long end = 0;
    protected long size = 0;

    public AbstractDiscovery(ActivityScan discover) {
        mDiscover = new WeakReference<ActivityScan>(discover);
    }

    public void setNetwork(long ip, long start, long end) {
        this.ip = ip;
        this.start = start;
        this.end = end;
    }

    abstract protected Void doInBackground(Void... params);

    @Override
    protected void onPreExecute() {
        size = (int) (end - start + 1);
        if (mDiscover != null) {
            final ActivityScan discover = mDiscover.get();
            if (discover != null) {
                discover.setProgress(0);
            }
        }
    }

    @Override
    protected void onProgressUpdate(bsnetDevice... host) {
        if(cancelTask) return;

        if (mDiscover != null) {
            final ActivityScan discover = mDiscover.get();
            if (discover != null) {
                if (!isCancelled()) {
                    /*
                    if (host != null) {
                        //Add Host
                        discover.addHost(host[0]);
                    }
                    */

                    if (size > 0) {
                        discover.setProgress((int) (hosts_done * 10000 / size));
                    }
                }

            }
        }
    }

    @Override
    protected void onPostExecute(Void unused) {
        if(cancelTask) return;

        if (mDiscover != null) {
            final ActivityScan discover = mDiscover.get();
            if (discover != null) {
                /* Si usamos el vibrador para indicar que se ha terminado
                if (discover.prefs.getBoolean(Prefs.KEY_VIBRATE_FINISH,
                        Prefs.DEFAULT_VIBRATE_FINISH) == true) {
                    Vibrator v = (Vibrator) discover.getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(ActivityScan.VIBRATE);
                }
                */
                Openweb.makeToast(discover.getApplicationContext(), R.string.discover_finished);
                discover.stopDiscovering();
            }
        }
    }


    @Override
    protected void onCancelled() {
        cancelTask = true;

        if (mDiscover != null) {
            final ActivityScan discover = mDiscover.get();
            if (discover != null) {
                Openweb.makeToast(discover.getApplicationContext(), R.string.discover_canceled);
                discover.stopDiscovering();
            }
        }
        super.onCancelled();
    }
}
