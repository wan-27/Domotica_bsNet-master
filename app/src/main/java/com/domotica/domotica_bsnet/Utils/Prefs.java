package com.domotica.domotica_bsnet.Utils;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.domotica.domotica_bsnet.MainActivity;
import com.domotica.domotica_bsnet.R;
import com.domotica.domotica_bsnet.preferencesHandler;

import static android.content.ContentValues.TAG;
import static soulissclient.Constants.MAX_NODE_IDX;
import static soulissclient.Constants.MAX_USER_IDX;
import static soulissclient.Constants.prefs.NODE_INDEX_KEY;
import static soulissclient.Constants.prefs.UDP_PORT_KEY;
import static soulissclient.Constants.prefs.USER_INDEX_KEY;

/**
 * Created by jctejero on 12/09/2017.
 */

public class Prefs extends PreferenceActivity {
    //private final String TAG = "Prefs";

    public static final String KEY_EMAIL = "email";
    public static final String KEY_WIFI = "wifi";

    private static final String URL_EMAIL = "jctejero@uma.es";

    private PreferenceScreen ps = null;

    private preferencesHandler prefHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefHandler = MainActivity.getPrefs();

        // Load the preferences from an XML resource
        // Display the fragment as the main content.
        FragmentManager mFragmentManager = getFragmentManager();
        FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
        PrefsFragment mPrefsFragment = new PrefsFragment();
        mPrefsFragment.setprefHandler(prefHandler);
        mPrefsFragment.setappContext(getApplicationContext());
        mFragmentTransaction.replace(android.R.id.content, mPrefsFragment).commit();

    }

    public static class PrefsFragment extends PreferenceFragment
    {
        private preferencesHandler prefHandler;
        private Preference udpport;
        private Preference userindex;
        private Preference nodeindex;
        private Context ctxt;

        public void setprefHandler(preferencesHandler prefHandler){
            this.prefHandler = prefHandler;
        }

        public void setappContext(Context ctxt){
            this.ctxt = ctxt;
        }

        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            udpport = findPreference(UDP_PORT_KEY);
            String strMeatFormat = getString(R.string.udpport_summary);
            udpport.setSummary(String.format(strMeatFormat,prefHandler.getUDPPort()));
            udpport.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    //Log.w(TAG, "CHANGING UDP PORT:" + newValue);
                    try {
                        String ics = (String) newValue;
                        Integer rete = Integer.parseInt(ics);
                        // enforce 0 < x < 0xfe
                        if (rete >= 65535 || rete < 1)
                            throw new IllegalArgumentException();
                        prefHandler.setUDPPort(rete);
                        String strMeatFormat = getString(R.string.udpport_summary);
                        udpport.setSummary(String.format(strMeatFormat,prefHandler.getUDPPort()));
                    } catch (Exception e) {
                        Toast.makeText(ctxt, getString(R.string.udphint), Toast.LENGTH_SHORT)
                                .show();
                    }
                    return true;
                }
            });

            userindex = findPreference(USER_INDEX_KEY);
            String stdrMeatFormat = getString(R.string.userindex_summary);
            userindex.setSummary(String.format(stdrMeatFormat, prefHandler.getUserIndex()));
            userindex.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Log.w(TAG, "CHANGING USER INDEX:" + newValue);
                    try {
                        String ics = (String) newValue;
                        Integer rete = Integer.parseInt(ics);

                        if (rete >= MAX_USER_IDX || rete < 1)// enforce 0 < x < 64
                            throw new IllegalArgumentException();

                        prefHandler.setUserIndex(rete);
                        String stdrMeatFormat = getString(R.string.userindex_summary);
                        userindex.setSummary(String.format(stdrMeatFormat, prefHandler.getUserIndex()));
                    } catch (Exception e) {
                        Toast.makeText(ctxt, getString(R.string.useridxhint), Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });

            nodeindex = findPreference(NODE_INDEX_KEY);
            strMeatFormat = getString(R.string.nodeindex_summary);
            nodeindex.setSummary(String.format(strMeatFormat, prefHandler.getNodeIndex()));
            nodeindex.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Log.w(TAG, "CHANGING NODE INDEX:" + newValue);
                    try {
                        String ics = (String) newValue;
                        if (Integer.parseInt(ics) >= MAX_NODE_IDX || Integer.parseInt(ics) < 1)
                            throw new IllegalArgumentException();

                        prefHandler.setNodeIndex(Integer.parseInt(ics));
                        String strMeatFormat = getString(R.string.nodeindex_summary);
                        nodeindex.setSummary(String.format(strMeatFormat, prefHandler.getNodeIndex()));
                    } catch (Exception e) {
                        Toast.makeText(ctxt, getString(R.string.nodeidxhint), Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });


            // Wifi settings listener
            ((Preference) findPreference(KEY_WIFI))
                    .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        public boolean onPreferenceClick(Preference preference) {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                            //startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                            return true;
                        }
                    });

            // Contact
            Preference contact = (Preference) findPreference(KEY_EMAIL);
            contact.setSummary(URL_EMAIL);
            contact.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    final Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.setType("plain/text");
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { URL_EMAIL });
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Network Discovery");
                    try {
                        startActivity(emailIntent);
                    } catch (ActivityNotFoundException e) {
                    }
                    return true;
                }
            });
        }
    }
}
