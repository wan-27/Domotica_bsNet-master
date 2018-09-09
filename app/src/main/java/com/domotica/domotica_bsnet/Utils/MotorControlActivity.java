package com.domotica.domotica_bsnet.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.domotica.domotica_bsnet.MainActivity;
import com.domotica.domotica_bsnet.Network.NetInfo;
import com.domotica.domotica_bsnet.R;
import com.domotica.domotica_bsnet.Utils.Http_Access.MyVolley;
import com.domotica.domotica_bsnet.preferencesHandler;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import soulissclient.Constants;
import soulissclient.net.UDPHelper;

public class MotorControlActivity extends AppCompatActivity {

    //Configuration Parameters
    private     final static short  CFG_PERC = 0;
    private     final static short  VAL_PERC = 1;

    //MaCaco info subscription bytes.
    private     final static short  BYTE_FUNCTION               = 0;
    private     final static short  BYTE_DATA                   = 5;
    private     final static short  BYTE_OPENING_PERC           = 6;

    //State Config
    private enum configState {
        WAITING_DOWN,
        WAITING_STOP1,
        WAITING_UP,
        WAITING_STOP2,
        NO_CONFIG;

        public static configState fromInteger(int x) {
            switch(x) {
                case 0:
                    return WAITING_DOWN;
                case 1:
                    return WAITING_STOP1;
                case 2:
                    return WAITING_UP;
                case 3:
                    return WAITING_STOP2;
                case 4:
                    return NO_CONFIG;
            }
            return null;
        }

        public static short fromName(configState name){
            switch (name){
                case WAITING_DOWN:
                    return 0;
                case WAITING_STOP1:
                    return 1;
                case WAITING_UP:
                    return 2;
                case WAITING_STOP2:
                    return 3;
                case NO_CONFIG:
                    return 4;
            }

            return -1;
        }
    }

//    private     static TextView msgConfig;

    private     boolean             config;
    private     String              ipaddress;
    protected   NetInfo             net = null;
    private     String              info_in_str;
    private     String              blindstate;
    private     boolean             waitingAnswer = false;
    private     configState         actualConfState = configState.NO_CONFIG;
    private     SeekBar seekBar;
    private     TextView /*seekBarValue, textCust,*/ text0, text25, text50, text75, text100;
    private     static preferencesHandler  prefs;
    private     static ConnectivityManager connMgr;
    private     boolean             actPercentage = false;

    @Override
    public void onResume() {
        super.onResume();

        Log.d(Constants.TAG, "MotorControlActivity onResume");

        // Listening for network events
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        filter.addAction(Constants.CUSTOM_INTENT_SOULISS_SUBSCRIBE_RESP);
        registerReceiver(receiver, filter);

        if(config){
            forceRegisterValue(CFG_PERC, configState.fromName(configState.WAITING_DOWN));
            //sendConfigState(configState.WAITING_DOWN);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);

        Log.d(Constants.TAG, "MotorControlActivity onPause");
    }

    @Override
    public void onStop() {
        super.onStop();

        if(config){
            forceRegisterValue(CFG_PERC, configState.fromName(configState.NO_CONFIG));
            //sendConfigState(configState.NO_CONFIG);
        }

        Log.d(Constants.TAG, "MotorControlActivity onStop");
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {

            Bundle extras = intent.getExtras();

            if (extras != null && extras.get("MACACO") != null) {
                waitingAnswer = false;

                ArrayList<Short> vers = (ArrayList<Short>) extras.get("MACACO");
                //Suponemos que Solo está el slot de acción de la persiana (slot-0)
                Short function = vers.get(BYTE_FUNCTION);

                Log.d(Constants.TAG, "Function Received : 0x"+Integer.toHexString(function));

                switch (function ){
                    case Constants.Net.Souliss_UDP_function_subscribe_resp:
                        Short blindState = vers.get(BYTE_DATA);

                        //Blind Percentage
                        //int  perc = vers.get(BYTE_OPENING_PERC);


                        setBlindPercentage(vers.get(BYTE_OPENING_PERC));

                        //perc = perc + vers.get(6);
                        //float fperc = Half.toFloat((short) perc);
                        //Log.d(Constants.TAG, "Blind State : 0x"+Integer.toHexString(blindState));

                        switch(blindState){
                            case Constants.typicals.Souliss_T2n_LimSwitch_Open:
                                blindstate = getString(R.string.st_blindstate, getString(R.string.st_open));
                                break;

                            case Constants.typicals.Souliss_T2n_LimSwitch_Close:
                                blindstate = getString(R.string.st_blindstate, getString(R.string.st_close));
                                break;

                            case Constants.typicals.Souliss_T2n_CloseCmd_SW:
                                blindstate = getString(R.string.st_blindstate, getString(R.string.st_closing));
                                break;

                            case Constants.typicals.Souliss_T2n_OpenCmd_SW:
                                blindstate = getString(R.string.st_blindstate, getString(R.string.st_opening));
                                break;

                            case Constants.typicals.Souliss_T2n_Coil_Stop:
                            case Constants.typicals.Souliss_T2n_NoLimSwitch:
                                blindstate = getString(R.string.st_blindstate, getString(R.string.st_stop));
                                break;

                            default:
                                blindstate = getString(R.string.st_blindstate, "");
                                break;
                        }

                        setState();

                        break;

                    case Constants.Net.Souliss_UDP_function_force_back_register:
                    case Constants.Net.Souliss_UDP_subscribe_data_answer:
                        actualizeConfigState(configState.fromInteger(vers.get(BYTE_DATA)));
                        break;
                }
            }

                // 3G(connected) -> Wifi(connected)
            // Support Ethernet, with ConnectivityManager.TYPE_ETHER=3
            final NetworkInfo ni = connMgr.getActiveNetworkInfo();
            if (ni != null) {
                if (ni.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
                    int type = ni.getType();
                    if (type == ConnectivityManager.TYPE_WIFI) { // WIFI
                        net.getWifiInfo();
                        if (net.ssid != null) {
                            info_in_str = getString(R.string.net_ssid, net.ssid);
                        }
                    }
                }
                setSSID();
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if(data.getBooleanExtra("restart", false)){
                //            openMotorControlActivity(data.getStringExtra("device"), data.getStringExtra("ipaddress"), false);
                Log.e("MainActivity", "Error in creating fragment");
            }
        }
    }

    private void actualizeConfigState(configState newConfState){
        Log.d(Constants.TAG, "MotorControlActivity "+ newConfState.toString());
        switch (newConfState){
            case NO_CONFIG:
                if(actualConfState != newConfState) {
                    Intent data = new Intent();
                    data.putExtra("restart", true);
                    data.putExtra("device", getIntent().getExtras().getString("device"));
                    data.putExtra("ipaddress", getIntent().getExtras().getString("ipaddress"));
                    setResult(RESULT_OK, data);

                    finish();
                }
                else {
                    this.findViewById(R.id.motorState).setVisibility(TextView.VISIBLE);
                    //this.findViewById(R.id.Config_msg).setVisibility(TextView.INVISIBLE);
                    if(actPercentage) {
                        this.findViewById(R.id.msg_blperc).setVisibility(TextView.VISIBLE);
                        this.findViewById(R.id.layout_percentage).setVisibility(TextView.VISIBLE);
                    }
                }

                break;
            case WAITING_UP:
               // ((TextView) this.findViewById(R.id.motorState)).setVisibility(TextView.INVISIBLE);
               // ((TextView) this.findViewById(R.id.msg_blperc)).setVisibility(TextView.INVISIBLE);
               // ((TextView) this.findViewById(R.id.Config_msg)).setVisibility(TextView.VISIBLE);
                ((TextView) this.findViewById(R.id.Config_msg)).setText(getString(R.string.ConfPerc_fin));
                break;
            case WAITING_DOWN:
                this.findViewById(R.id.motorState).setVisibility(TextView.INVISIBLE);
                this.findViewById(R.id.msg_blperc).setVisibility(TextView.INVISIBLE);
                this.findViewById(R.id.Config_msg).setVisibility(TextView.VISIBLE);
                ((TextView) this.findViewById(R.id.Config_msg)).setText(getString(R.string.ConfPerc_ini));
                break;
            case WAITING_STOP1:
                break;
            case WAITING_STOP2:
                break;
        }

        actualConfState = newConfState;
    }

    private void forceRegisterValue(final short reg, final short val){
        //Send device register value
        Thread t = new Thread() {
            public void run() {
                String[] ips = ipaddress.split("\\.");

                //Set module config mode
                UDPHelper.issueSouliss(Constants.Net.Souliss_UDP_function_force_register, ipaddress,
                        String.valueOf(reg), prefs, String.valueOf(val));

            }
        };
        t.start();
    }

    private void sendConfigState(final configState cSte){
        //Send device we are on config state
        Thread t = new Thread() {
            public void run() {
                String[] ips = ipaddress.split("\\.");

                //Set module config mode
                UDPHelper.issueSouliss(Constants.Net.Souliss_UDP_function_force_register, ipaddress,
                        String.valueOf(CFG_PERC), prefs, String.valueOf(configState.fromName(cSte)));

            }
        };
        t.start();
    }

    private void setBlindPercentage(int percentage){
        Log.d(Constants.TAG, "Blind percentage : "+Integer.toString(percentage)+" %");
        ((TextView) this.findViewById(R.id.msg_blperc)).setText(String.valueOf(percentage)+" %");

        int Progress = Math.round((float)percentage / 25 );

        seekBar.setProgress(Progress);
    }

    private void setState(){
        ((TextView) this.findViewById(R.id.motorState)).setText(blindstate);
    }

    private void setSSID(){
        ((TextView) this.findViewById(R.id.textSSID)).setText(getString(R.string.net_ssid, net.ssid));
    }

    //@SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(Constants.TAG, "MotorControlActivity onCreate");

        ipaddress = getIntent().getExtras().getString("ipaddress");
        config = getIntent().getExtras().getBoolean("config");

        prefs = MainActivity.getPrefs();
        connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        setContentView(R.layout.motor_control);

        seekBar = (SeekBar) this.findViewById(R.id.seekBarPercent);
        //seekBarValue = (TextView) this.findViewById(R.id.seekBarValue);
        //textCust = (TextView) this.findViewById(R.id.textCust);
        text0 = (TextView) this.findViewById(R.id.text0percent);
        text25 = (TextView) this.findViewById(R.id.text25percent);
        text50 = (TextView) this.findViewById(R.id.text50percent);
        text75 = (TextView) this.findViewById(R.id.text75percent);
        text100 = (TextView) this.findViewById(R.id.text100percent);

        ((TextView) this.findViewById(R.id.motorcontrol_title)).setText(getIntent().getExtras().getString("device"));

        //==========================
        //Read percentage activation
        //==========================
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(Constants.TAG, "general values Received");
                // Do something with response string
                String[] parts = response.split("\\|"); // Separar por "|"
                //name.setText(parts[1]);
                actPercentage = parts[3].compareTo("true") == 0;

                Log.d(Constants.TAG, "actPercentage "+actPercentage);

                if(!actPercentage) {
                    findViewById(R.id.msg_blperc).setVisibility(TextView.INVISIBLE);
                    findViewById(R.id.layout_percentage).setVisibility(TextView.INVISIBLE);
                    findViewById(R.id.OpenSelection).setVisibility(TextView.INVISIBLE);
                }
                else{
                    if(!config) {
                        findViewById(R.id.msg_blperc).setVisibility(TextView.VISIBLE);
                        findViewById(R.id.layout_percentage).setVisibility(TextView.VISIBLE);
                        findViewById(R.id.OpenSelection).setVisibility(TextView.VISIBLE);
                    }
                }
            }
        };

        MyVolley.ServerRequest(this, ipaddress, "/admin/generalvalues",
                Request.Method.GET, listener, null, null);


        if(config) {
            //Send device we are on config state
            Thread t = new Thread() {
                public void run() {
                    String[] ips = ipaddress.split("\\.");

                    //Set module config mode
                    UDPHelper.issueSouliss(Constants.Net.Souliss_UDP_function_force_register, ipaddress,
                            String.valueOf(CFG_PERC), prefs, String.valueOf(configState.fromName(configState.WAITING_DOWN)));

                }
            };
            t.start();
        }
        else {

            TimerTask task = new TimerTask() {
                @Override
                public void run() {

                    String[] ips = ipaddress.split("\\.");

                    waitingAnswer = true;

                    UDPHelper.issueSouliss(Constants.Net.Souliss_UDP_function_force_back_register, ipaddress,
                            String.valueOf(CFG_PERC), prefs, "0");
                    /*
                    // Start the next activity
                    Intent mainIntent = new Intent().setClass(
                            SplashActivity.this, MainActivity.class);
                    startActivity(mainIntent);

                    // Close the activity so the user won't able to go back this
                    // activity pressing Back button
                    finish();*/
                }
            };

            // Simulate a long loading process on application startup.
            Timer timer = new Timer();
            timer.schedule(task, 500);
            /*
            Thread t = new Thread() {
                public void run() {
                    //Read module config mode
                    String[] ips = ipaddress.split("\\.");

                    waitingAnswer = true;

                    UDPHelper.issueSouliss(Constants.Net.Souliss_UDP_function_force_back_register, ipaddress,
                            String.valueOf(CFG_PERC), prefs, "0");
                }
            };
            t.start();
            */
        }
/*
        this.findViewById(R.id.motorState).setVisibility(TextView.INVISIBLE);
        this.findViewById(R.id.Config_msg).setVisibility(TextView.INVISIBLE);
        this.findViewById(R.id.layout_percentage).setVisibility(TextView.INVISIBLE);
*/
        //}

        net = new NetInfo(getApplicationContext());

        final TextView textSSID = (TextView) this.findViewById(R.id.textSSID);
        textSSID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                //startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                //return true;
            }
        });

        //Subcribe device on Gateway to receive information from it
        Thread t = new Thread() {
            public void run() {

                while(waitingAnswer);

                waitingAnswer = false;

                if(config){
                    //Subscribe to data change
                    UDPHelper.stateRequest(Constants.Net.Souliss_UDP_subscribe_data_request, ipaddress,prefs,1,0);
                }
                else {
                    //Subscribe to MaCaco_OUT_s
                    UDPHelper.stateRequest(Constants.Net.Souliss_UDP_function_subscribe, ipaddress, prefs, 1, 0);
                }
            }
        };
        t.start();

        final ImageView imgStop = (ImageView) this.findViewById(R.id.iv_stop);
        imgStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(Constants.TAG, "Click Stop");

                if(config) {
                    if ((actualConfState != configState.WAITING_STOP1) && (actualConfState != configState.WAITING_STOP2))
                        return;
                }

                Thread t = new Thread() {
                    public void run() {
                        String[] ips = ipaddress.split("\\.");

                        UDPHelper.issueSoulissCommand(ipaddress, ips[3], "0", prefs,
                                String.valueOf(Constants.typicals.Souliss_T2n_StopCmd));
                    }
                };
                t.start();
            }
        });

        imgStop.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        imgStop.setImageResource(R.drawable.stop_push);
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        imgStop.setImageResource(R.drawable.stop_rel);
                        break;
                    }
                }
                return false;
            }
        });

        //net.getWifiInfo();
        //((TextView) this.findViewById(R.id.textSSID)).setText(getString(R.string.net_ssid, net.ssid));
        final ImageView bajar = (ImageView) this.findViewById(R.id.iv_bajar);
        bajar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(Constants.TAG, "Click Bajar");

                if(config){
                    if(actualConfState != configState.WAITING_DOWN) return;
                }

                Thread t = new Thread() {
                    public void run() {
                        String[] ips = ipaddress.split("\\.");

                        UDPHelper.issueSoulissCommand(ipaddress, ips[3], "0", prefs,
                                String.valueOf(Constants.typicals.Souliss_T2n_CloseCmd_SW));

                    }
                };
                t.start();
            }
        });

        bajar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                //Log.i(Constants.TAG, "motion event: " + event.toString());
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        bajar.setImageResource(R.drawable.arrow_down_push);
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        bajar.setImageResource(R.drawable.arrow_down_rel);
                        break;
                    }
                }
                return false;
            }
        });

        final ImageView subir = (ImageView) this.findViewById(R.id.iv_subir);
        subir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(Constants.TAG, "Click Subir");

                if(config){
                    if(actualConfState != configState.WAITING_UP) return;
                }

                Thread t = new Thread() {
                    public void run() {
                        String[] ips = ipaddress.split("\\.");
                        UDPHelper.issueSoulissCommand(ipaddress, ips[3], "0", prefs,
                                String.valueOf(Constants.typicals.Souliss_T2n_OpenCmd_SW));
                    }
                };
                t.start();
            }
        });

        subir.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                //Log.i(Constants.TAG, "motion event: " + event.toString());
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        subir.setImageResource(R.drawable.arrow_up_push);
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        subir.setImageResource(R.drawable.arrow_up_rel);
                        break;
                    }
                }
                return false;
            }
        });

        // Add listener to SeekBar

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int seekBarProgress = 0;
//            boolean stopTouch = false;
//            boolean startTouch = false;
//            boolean state = false;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarProgress = progress;
                //seekBarValue.setText(seekBarProgress * 25 + "%");
/*
                if(state) {
                    forceRegisterValue(VAL_PERC, (short) progress);
                    //stopTouch = false;
                }
*/
                Log.i(Constants.TAG, "seekBar Progress: "+progress+" %");
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
 //               state = true;
                Log.i(Constants.TAG, "seekBar onStartTrackingTouch: ");
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
//                state = false;
                forceRegisterValue(VAL_PERC, (short) seekBarProgress);
                Log.i(Constants.TAG, "seekBar onStopTrackingTouch: ");

            }

        });
    }
}
