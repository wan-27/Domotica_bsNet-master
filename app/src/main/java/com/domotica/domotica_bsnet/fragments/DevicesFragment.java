package com.domotica.domotica_bsnet.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


import com.android.volley.Request;
import com.android.volley.Response;
import com.domotica.domotica_bsnet.Scenes.ActivityScene;
import com.domotica.domotica_bsnet.MainActivity;
import com.domotica.domotica_bsnet.Network.NetInfo;
import com.domotica.domotica_bsnet.R;
import com.domotica.domotica_bsnet.Utils.HostsAdapter;
import com.domotica.domotica_bsnet.Utils.Http_Access.MyVolley;
import com.domotica.domotica_bsnet.Utils.MotorControlActivity;
import com.domotica.domotica_bsnet.Utils.Openweb;
import com.domotica.domotica_bsnet.Utils.Prefs;
import com.domotica.domotica_bsnet.Utils.alertDialogs.Items_AlertDialog;
import com.domotica.domotica_bsnet.Utils.alertDialogs.OkCancelDialog;
import com.domotica.domotica_bsnet.Utils.alertDialogs.dialog_EditText;
import com.domotica.domotica_bsnet.ActivityScan;
import com.domotica.domotica_bsnet.db.bsnetDBHelper;
import com.domotica.domotica_bsnet.preferencesHandler;

import soulissclient.Constants;

import static android.app.Activity.RESULT_OK;


/**
 * Created by jctejero on 19/12/2017.
 */

public class DevicesFragment extends Fragment implements AdapterView.OnItemClickListener {

    private HostsAdapter hostsAdapter;
    private int host_longclick;
    private static String ipServer_longclick;
    private static String name_longclick;
    private static boolean ctrlOpening;

    private static preferencesHandler prefsHandle;
    private static bsnetDBHelper bsnet;
    private static Context ctxt;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefsHandle = MainActivity.getPrefs();
        bsnet = MainActivity.getbsnet();
        ctxt = MainActivity.getCtxt();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        final View v = inflater.inflate(R.layout.fragment_devices, container, false);

        return v;
    }

    //La vista de layout ha sido creada y ya está disponible
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button scan_btn = (Button) view.findViewById(R.id.scanbutton);

        Drawable drawable = getResources().getDrawable(R.drawable.discover);
        drawable.setBounds(20, 0, 68, 48);
        scan_btn.setCompoundDrawables(drawable, null, null, null);
        scan_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startDiscoverActivity(ctxt);
            }
        });

        // Listening for network events
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        getActivity().registerReceiver(newWifissid, filter);

        //Show database nodes
        displayHosts();
    }

    private BroadcastReceiver newWifissid = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            if (!prefsHandle.getEyeState()) {
                bsnet.setssid(NetInfo.getWifissid(ctxt));
                hostsAdapter.setHosts(bsnet.getHosts());
//                    displayHosts();
            } else bsnet.setssid(null);
        }
    };

    private void startDiscoverActivity(final Context ctxt) {
        //Log.i("Discovery", TAG+" startDiscoverActivity");

        startActivity(new Intent(ctxt, ActivityScan.class));
    }

    private void displayHosts() {
        if (bsnet.getHosts() != null) {
            hostsAdapter = new HostsAdapter(ctxt, bsnet.getHosts());
            //hostsAdapter = new HostsAdapter(ctxt);
            ListView list = (ListView) getView().findViewById(R.id.listView_main);
            list.setLongClickable(true);
            list.setAdapter(hostsAdapter);
            list.setOnItemClickListener(this);
            list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int pos, long l) {
                    host_longclick = pos;

                    host_options(view, pos);

                    return true;
                }
            });
        }
    }

    private void host_options(final View view, final int position) {
        Items_AlertDialog hostOptions = Items_AlertDialog.newInstance(R.string.opt_hostname, R.array.host_options, new Items_AlertDialog.OnItemsAlertdialogClickListener() {
            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel parcel, int i) {

            }

            @Override
            public void onClickListener(int which) {

                ipServer_longclick = hostsAdapter.getHost(host_longclick).ipAddress;
                name_longclick = hostsAdapter.getHost(host_longclick).hostname;
                if (name_longclick == null) name_longclick = ipServer_longclick;

                switch (which) {
                    case 0:     //Rename device
                        showDialog_EditText();
                        break;

                    case 1:     //Open web
                        Openweb.OpenWeb(ctxt, ipServer_longclick);
                        break;

                    case 2:     //Open device configuration
                        startActivity(new Intent(ctxt, com.domotica.domotica_bsnet.ActivityOptions.class));
                        //startActivity(new Intent(ctxt, ActivityScan.class));
                        break;

                    case 3:     //Open device Scenes configuration
                        Response.Listener<String> listener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Do something with response string
                                String[] parts = response.split("\\|"); // Separar por "|"

                                Intent i = new Intent(getContext(), ActivityScene.class);
                                ctrlOpening = (parts[3].compareTo("true") == 0);
                                startActivity(i);
                            }
                        };

                        //private void getDeviceName(){
                        MyVolley.ServerRequest(getContext(), ipServer_longclick, "/admin/generalvalues",
                                Request.Method.GET, listener, null, null);

                        break;

                    case 4:     //Open/Close Time
                        openMotorControlActivity(view, position, true);
                        break;

                    case 5:     //Delete device
                        String device = getString(R.string.titl_deldevice, hostsAdapter.getHost(host_longclick).hostname);

                        OkCancelDialog toDelDevice = OkCancelDialog.newInstance(device);
                        toDelDevice.setMyCustomListener(new OkCancelDialog.OKListener() {
                            @Override
                            public void onOKClick() {
                                //Borrar dispositivo
                                Log.d(Constants.TAG, "Borrar el dispositivo");
                                bsnet.borrarDevice(ipServer_longclick);
                                hostsAdapter.setHosts(bsnet.getHosts());
                            }
                        });
                        toDelDevice.show(getFragmentManager(), " ");
                        break;
                }
            }
        });


        hostOptions.show(getFragmentManager(), " ");
    }

    private void showDialog_EditText() {
        dialog_EditText newET_Dialog = dialog_EditText.newInstance(R.string.titl_hostname);

        newET_Dialog.setMyCustomListener(new dialog_EditText.CustomListener() {
            @Override
            public void onMyCustomAction(dialog_EditText.dialogEditTextEvent evt) {
                if (evt.getButton() == dialog_EditText.dialogEditTextEvent.POSITIVE) {
                    //Almacenar Nombre del dispositivo.
                    bsnet.sethostnameByip(evt.getMessage(), hostsAdapter.getHost(host_longclick).ipAddress);

                    hostsAdapter.setHosts(bsnet.getHosts());
                }
            }
        });
        newET_Dialog.show(getFragmentManager(), " ");
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.main_menu, menu);

        MenuItem itemOjo = null;
        for (int n = 0; n < menu.size(); n++)
            if (menu.getItem(n).getTitle().equals(getString(R.string.menu_ojo))) {
                itemOjo = menu.getItem(n);
            }

        if ((itemOjo != null) && !prefsHandle.getEyeState()) {
            itemOjo.setIcon(R.drawable.ojoc_s);
        } else itemOjo.setIcon(R.drawable.ojo_s);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_eye:

                if (prefsHandle.getEyeState()) {
                    item.setIcon(R.drawable.ojoc_s);
                    prefsHandle.setEyeState(false);
                    bsnet.setssid(NetInfo.getWifissid(ctxt));
                } else {
                    item.setIcon(R.drawable.ojo_s);
                    prefsHandle.setEyeState(true);
                    bsnet.setssid(null);
                }
                hostsAdapter.setHosts(bsnet.getHosts());

                return true;

            case R.id.options:
                startActivity(new Intent(ctxt, Prefs.class));

                return true;

            case R.id.exit:
                getActivity().finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (data.getBooleanExtra("restart", false)) {
                openMotorControlActivity(data.getStringExtra("device"), data.getStringExtra("ipaddress"), false);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.i(Constants.TAG, "On Resume");

        if (hostsAdapter != null) {
            hostsAdapter.setHosts(bsnet.getHosts());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        openMotorControlActivity(view, position, false);
    }

    private void openMotorControlActivity(String device, String ipAddress, boolean conf) {
        //open activity for the device selected

        Intent intent = new Intent(ctxt, MotorControlActivity.class);
        intent.putExtra("device", device);
        intent.putExtra("ipaddress", ipAddress);
        intent.putExtra("config", conf);

        startActivityForResult(intent, 0);

        //startActivity(intent);
    }

    private void openMotorControlActivity(View view, int position, boolean conf) {
        //open activity for the device selected

        openMotorControlActivity(((TextView) view.findViewById(R.id.hostname)).getText().toString(), hostsAdapter.getHost(position).ipAddress, conf);
        /*
        Intent intent = new Intent(ctxt, MotorControlActivity.class);
        intent.putExtra("device", ((TextView) view.findViewById(R.id.hostname)).getText().toString());
        intent.putExtra("ipaddress", hostsAdapter.getHost(position).ipAddress);
        intent.putExtra("config", conf);

        startActivity(intent);
        */
    }

    public static String getIpServerLongclick() {
        return ipServer_longclick;
    }

    public static String getNameLongclick() {
        return name_longclick;
    }

    public static boolean getCtrlOpening() {
        return ctrlOpening;
    }
}
