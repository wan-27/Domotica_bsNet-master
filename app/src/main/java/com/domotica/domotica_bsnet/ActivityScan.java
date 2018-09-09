package com.domotica.domotica_bsnet;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.domotica.domotica_bsnet.Network.NetInfo;
import com.domotica.domotica_bsnet.Network.bsnetDevice;
import com.domotica.domotica_bsnet.Utils.HostsAdapter;
import com.domotica.domotica_bsnet.Utils.Openweb;
import com.domotica.domotica_bsnet.Utils.Prefs;
import com.domotica.domotica_bsnet.Utils.alertDialogs.dialog_EditText;
import com.domotica.domotica_bsnet.db.bsnetDBHelper;

import java.util.ArrayList;

import soulissclient.Constants;
import soulissclient.net.UDPHelper;

import static com.domotica.domotica_bsnet.Network.bsnetDevice.DiscoverFase.*;

public class ActivityScan extends ActivityNet implements AdapterView.OnItemClickListener {
    private final String            TAG = "ActivityScanNet";
    private int                     currentNetwork = 0;
    private long                    network_ip = 0;
    private long                    network_start = 0;
    private long                    network_end = 0;
    private HostsAdapter            adapter;
    private Button                  btn_discover;
    private AbstractDiscovery       mDiscoveryTask = null;
    private Handler                 timeoutHandler;
    private preferencesHandler      prefs;
    private bsnetDBHelper           bsnet;
    private Boolean                 backPressed = false;
    private int                     host_longclick;

    private ArrayList<bsnetDevice>                  dhosts = null;      //Souliss Hosts founded
    final private static ArrayList<bsnetDevice>     foundhosts = new ArrayList<bsnetDevice>();  //Founded Hosts

    private class hostrespose implements Runnable{
        private String iphost;
        private bsnetDevice.DiscoverFase disf;

        public Runnable timeExpired = new Runnable() {
            @Override
            public void run() {

                Log.e(Constants.TAG, "time expired --> "+iphost+ " Discover Fase : " + disf);

                for(int fd = 0; fd < foundhosts.size(); fd++) {
                    if (foundhosts.get(fd).ipAddress.equals(iphost)) {
                        if(foundhosts.get(fd).discoverFase == disf){
                            if(++foundhosts.get(fd).cntDis < bsnetDevice.MAX_CNTDIS) {

                                Log.d(Constants.TAG, "De nuevo, solicitamos : " + disf + " host: " + foundhosts.get(fd).ipAddress);

                                //Solicitamos de nuevo los datos
                                new hostrespose(foundhosts.get(fd).ipAddress, foundhosts.get(fd).discoverFase).run();
                                gethostInformation(foundhosts.get(fd), bsnetDevice.DiscoverFase.Souliss_UDP(disf), prefs);
                            } else{
                                if(disf == DIS_PING){
                                    foundhosts.remove(fd);
                                }
                            }
                        }
                    }
                }
            }
        };

        hostrespose(String iphost, bsnetDevice.DiscoverFase disf) {
            this.iphost = iphost;
            this.disf   = disf;

            Log.i(Constants.TAG, "SET TIMEOUT TO --> "+iphost);

            timeoutHandler.postDelayed(timeExpired, 3000);
        }

        @Override
        public void run() {

        }
    }

    private BroadcastReceiver macacoRawDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            if (extras != null && extras.get("MACACO") != null && foundhosts != null) {

                Log.d(Constants.TAG, "****  macacoRawDataReceiver ****");

                ArrayList<Short> vers = (ArrayList<Short>) extras.get("MACACO");

                String ip = String.valueOf(vers.get(0))+"."+String.valueOf(vers.get(1))+"."+String.valueOf(vers.get(2))+"."+String.valueOf(vers.get(3));
                //bsnetDevice host = null;
                int      idxhost = -1;

                for(int i = 0; i<4; i++)
                    vers.remove(0);
                // vers = vers.subList(4, vers.size()-1);

                for(int fd = 0; fd < foundhosts.size(); fd++) {
                    if (foundhosts.get(fd).ipAddress.equals(ip)) {
                        idxhost = fd;
                        foundhosts.get(idxhost).cntDis = 0;  //Init request counter
                        break;
                    }
                }

                if(idxhost == -1) {
                    Log.d(Constants.TAG, "... host not located ...");

                    for(int fd = 0; fd < foundhosts.size(); fd++) {
                        Log.d(Constants.TAG, "--> "+fd+" :  host  : "+foundhosts.get(fd).ipAddress);
                    }

                    return;
                }

                int responseCode = vers.get(0);

                switch(responseCode){
                    case Constants.Net.Souliss_UDP_function_ping_resp:
                        foundhosts.get(idxhost).discoverFase = DIS_GATEWAY;

                        new hostrespose(foundhosts.get(idxhost).ipAddress, foundhosts.get(idxhost).discoverFase).run();

                        gethostInformation(foundhosts.get(idxhost), Constants.Net.Souliss_UDP_function_ping_bcast, prefs); //When compete host information add adapter

                        break;

                    case Constants.Net.Souliss_UDP_function_ping_bcast_resp:
                        Log.d(Constants.TAG, "Gateway Response: "+ip);

                        foundhosts.get(idxhost).discoverFase = DIS_DB_STRUCT;
                        new hostrespose(foundhosts.get(idxhost).ipAddress, foundhosts.get(idxhost).discoverFase).run();

                        String Gateway = String.valueOf(vers.get(Constants.reponses.ADDRESS_GATEWAY))+"."+
                                        String.valueOf(vers.get(Constants.reponses.ADDRESS_GATEWAY+1))+"."+
                                        String.valueOf(vers.get(Constants.reponses.ADDRESS_GATEWAY+2))+"."+
                                        String.valueOf(vers.get(Constants.reponses.ADDRESS_GATEWAY+3));

                        foundhosts.get(idxhost).deviceType = Gateway.equals(ip) ? bsnetDevice.TYPE_GATEWAY : bsnetDevice.TYPE_NODE;

                        gethostInformation(foundhosts.get(idxhost), Constants.Net.Souliss_UDP_function_db_struct, prefs);
                        break;

                    case Constants.Net.Souliss_UDP_function_db_struct_resp:
                        Log.d(Constants.TAG, "db struct Response: "+ip);

                        foundhosts.get(idxhost).discoverFase = DIS_TIPYCAL;
                        new hostrespose(foundhosts.get(idxhost).ipAddress, foundhosts.get(idxhost).discoverFase).run();

                                //Get the Nº of nodes, the max. number of allowed nodes, the number of slots
                        //for each node and the number of max. subscription allowed by the gateway
                        foundhosts.get(idxhost).slots = vers.get(Constants.reponses.NODES);

                        //Get Typicals node. Device type
                        gethostInformation(foundhosts.get(idxhost), Constants.Net.Souliss_UDP_function_typreq, prefs);
                        break;

                    case Constants.Net.Souliss_UDP_function_typreq_resp:
                        Log.d(Constants.TAG, "Typicals Response: "+ip);

                        foundhosts.get(idxhost).discoverFase = DIS_ACTIVE;

                        if(foundhosts.get(idxhost).typicals == null) {
                            foundhosts.get(idxhost).typicals = new ArrayList<Short>();
                        }else{
                            foundhosts.get(idxhost).typicals.clear();
                        }

                        for(int sl = 0; sl<foundhosts.get(idxhost).slots;sl++){
                            if(sl == 0){
                                foundhosts.get(idxhost).typical = vers.get(Constants.reponses.TYPICALS);
                            }
                            foundhosts.get(idxhost).typicals.add(vers.get(Constants.reponses.TYPICALS + sl)) ;
                        }

                        foundhosts.get(idxhost).position = dhosts.size();
                        dhosts.add(new bsnetDevice(foundhosts.get(idxhost)));
                        //Mostrar Dispositivo.
                        Log.d(Constants.TAG, "Nº dhosts : " + dhosts.size() + "  Set Hosts : "+dhosts.get(dhosts.size()-1).ipAddress);
                        adapter.setHosts(dhosts);
                        adapter.add(null);
                        break;
                }
            }
        }
    };

    private static void gethostInformation(final bsnetDevice host, final byte hostRequest, final preferencesHandler prefs){

        Thread t = new Thread(){
            public void run(){

                switch (hostRequest){
                    case Constants.Net.Souliss_UDP_function_ping:
                        try {
                            UDPHelper.ping(host.ipAddress, prefs);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case Constants.Net.Souliss_UDP_function_ping_bcast:
                        UDPHelper.discoverGateway(host.ipAddress, prefs);
                        break;

                    case Constants.Net.Souliss_UDP_function_db_struct:
                        UDPHelper.dbStructRequest(host.ipAddress, prefs);
                        break;

                    case Constants.Net.Souliss_UDP_function_typreq:
                        UDPHelper.typicalRequest(host.ipAddress, prefs, host.slots, 0);
                }
            }
        };

        t.start();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.discovery);
        //mInflater = LayoutInflater.from(ctxt);

        prefs = MainActivity.getPrefs();

        // enabling action bar app icon and behaving it as toggle button
        //Habilita el icono de la barra de aplicacion y su comportamiento como un conmutador
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        // Discover
        btn_discover = (Button) findViewById(R.id.btn_discover);
        btn_discover.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startDiscovering();
            }
        });

        // Options
        Button btn_options = (Button) findViewById(R.id.btn_options);
        btn_options.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(ctxt, Prefs.class));
            }
        });

        //Host list
        adapter = new HostsAdapter(ctxt);
        ListView list = (ListView) findViewById(R.id.output);
        list.setAdapter(adapter);
        //list.setItemsCanFocus(false);
        list.setOnItemClickListener(this);
        list.setLongClickable(true);
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int pos, long l) {

                host_longclick = pos;

                //editText(ActivityScan.this);
                showDialog_EditText(R.string.titl_hostname);

                //Log.v(Constants.TAG," Long Click -- pos: " + pos + " -- " + ((TextView) view.findViewById(R.id.mac)).getText().toString());

                return true;
            }
        } );

        //initList();
        //foundhosts = new ArrayList<bsnetDevice>();
        dhosts = new ArrayList<bsnetDevice>();

        timeoutHandler = new Handler();

    }

    private void showDialog_EditText(int title){
        dialog_EditText newET_Dialog = dialog_EditText.newInstance(title);
        newET_Dialog.setMyCustomListener(new dialog_EditText.CustomListener(){
            @Override
            public void onMyCustomAction(dialog_EditText.dialogEditTextEvent evt) {
                if(evt.getButton() == dialog_EditText.dialogEditTextEvent.POSITIVE) {
                    //Almacenar Nombre del dispositivo.
                    adapter.getHost(host_longclick).hostname = evt.getMessage();
                    adapter.notifyDataSetChanged();
                }
            }
        });
        newET_Dialog.show(getSupportFragmentManager(), "prueba");
    }

    private ArrayList seletedItems = new ArrayList();

    @Override
    public void onBackPressed() {

        unregisterReceiver(macacoRawDataReceiver);

        if((dhosts != null) && (dhosts.size() > 0)){
            final AlertDialog.Builder AlertDialog = new AlertDialog.Builder(ActivityScan.this);
            final CharSequence  test[] = new CharSequence[]{"Mantener dispositivos anteriores"};
            boolean[] setTrue = new boolean[1];
            setTrue[0] = true;
            AlertDialog.setTitle("Almacenar resultados ?");
            AlertDialog.setMultiChoiceItems(test, setTrue, new DialogInterface.OnMultiChoiceClickListener() {
                // indexSelected contains the index of item (of which checkbox checked)
                @Override
                public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                    if (isChecked) {
                        // If the user checked the item, add it to the selected items
                        // write your code when user checked the checkbox
                        seletedItems.add(indexSelected);
                    } else if (seletedItems.contains(indexSelected)) {
                        // Else, if the item is already in the array, remove it
                        // write your code when user Uchecked the checkbox
                        seletedItems.remove(Integer.valueOf(indexSelected));
                    }
                }
            });

            AlertDialog.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface arg0, int arg1) {
                    // do something when the OK button is clicked
                    Log.d(Constants.TAG, "... Almacenando dispositivos ...");

                    bsnet = MainActivity.getbsnet();

                    if(!seletedItems.contains(0)){
                        bsnet.clearDevices();
                    }

                    //Añadir dispositivo localizado a la base de datos
                    for(int fd = 0; fd < dhosts.size(); fd++) {
                        Log.i(Constants.TAG, "Registro insertado : " + bsnet.createOrUpdateDevice(dhosts.get(fd)));
                    }

                    backPressed = true;
                    ActivityScan.super.onBackPressed();
                }});

            AlertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface arg0, int arg1) {
                    // do something when the Cancel button is clicked
                    //AlertDialog.
                    Log.d(Constants.TAG, "... Obvia dispositivos ...");
                    backPressed = true;
                    ActivityScan.super.onBackPressed();
                }});
            AlertDialog.show();

        }
        else {
            super.onBackPressed();
            return;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // IDEM, serve solo per reporting
        if(!backPressed) {
            IntentFilter filtere = new IntentFilter();
            filtere.addAction(Constants.CUSTOM_INTENT_SOULISS_RAWDATA);
            registerReceiver(macacoRawDataReceiver, filtere);
        }
    };

    protected void setInfo() {

        // Info
        ((TextView) findViewById(R.id.info_ip)).setText(info_ip_str);
        ((TextView) findViewById(R.id.info_in)).setText(info_in_str);
        ((TextView) findViewById(R.id.info_mo)).setText(info_mo_str);

        // Scan button state
        if (mDiscoveryTask != null) {
            setButton(btn_discover, R.drawable.cancel, false);
            btn_discover.setText(R.string.btn_discover_cancel);
            btn_discover.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    cancelTasks();
                }
            });
        }

        //Siempre modificamos la información
        // Cancel running tasks
        cancelTasks();

        // Get ip information

        network_ip = NetInfo.getUnsignedLongFromIp(net.ip);

        // Detected IP
        int shift = (32 - ActivityNet.DEFAULT_CIDR);
        if (net.cidr < 31) {
            network_start = (network_ip >> shift << shift) + 1;
            network_end = (network_start | ((1 << shift) - 1)) - 1;
        } else {
            network_start = (network_ip >> shift << shift);
            network_end = (network_start | ((1 << shift) - 1));
        }

        Log.i(TAG, "network_ip : "+network_ip+ " | network_start: "+network_start+" | network_end: "+network_end);
    }
/*
    public void makeToast(int msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
*/
    protected void setButtons(boolean disable) {
        if (disable) {
            setButtonOff(btn_discover, R.drawable.disabled);
        } else {
            setButtonOn(btn_discover, R.drawable.discover);
        }
    }

    private void setButton(Button btn, int res, boolean disable) {
        if (disable) {
            setButtonOff(btn, res);
        } else {
            setButtonOn(btn, res);
        }
    }

    private void setButtonOff(Button b, int drawable) {
        b.setClickable(false);
        b.setEnabled(false);
        b.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0);
    }

    private void setButtonOn(Button b, int drawable) {
        b.setClickable(true);
        b.setEnabled(true);
        b.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0);
    }

    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        final bsnetDevice host = dhosts.get(position);

        //Open device web page
        /*
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://" + host.ipAddress));

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            makeToast(R.string.msg_NO_webDevice);
            Log.e(TAG, e.getMessage());
        }
*/
    }

    /**
     * Discover hosts
     */
    private void startDiscovering() {
        //usamos el método de búsqueda por defecto
        initList();
        mDiscoveryTask = new DefaultDiscovery(ActivityScan.this);
        mDiscoveryTask.setNetwork(network_ip, network_start, network_end);
        mDiscoveryTask.execute();
        btn_discover.setText(R.string.btn_discover_cancel);
        setButton(btn_discover, R.drawable.cancel, false);
        btn_discover.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cancelTasks();
            }
        });
        Openweb.makeToast(getApplicationContext(), R.string.discover_start);
        //makeToast(R.string.discover_start);
        setProgressBarVisibility(true);
        setProgressBarIndeterminateVisibility(true);
    }

    public void stopDiscovering() {
        Log.e(TAG, "stopDiscovering()");
        mDiscoveryTask = null;
        setButtonOn(btn_discover, R.drawable.discover);
        btn_discover.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startDiscovering();
            }
        });
        setProgressBarVisibility(false);
        setProgressBarIndeterminateVisibility(false);
        btn_discover.setText(R.string.btn_discover);
    }

    protected void cancelTasks() {
        if (mDiscoveryTask != null) {
            Log.e(TAG, "To Cancel");
            mDiscoveryTask.onCancelled();
            //mDiscoveryTask.cancel(true);
            mDiscoveryTask = null;
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initList() {
        foundhosts.clear();
        dhosts.clear();
        adapter.clear();
    }


    public int addHost(bsnetDevice host) {

        if(!host.ipAddress.equals(net.ip)) {
            host.position = foundhosts.size();
            foundhosts.add(new bsnetDevice(host.ipAddress, host.hardwareAddress, net.ssid));


            new hostrespose(host.ipAddress, host.discoverFase).run();
        }
        else {
            return -1;
        }
        return foundhosts.size();
    }
}
