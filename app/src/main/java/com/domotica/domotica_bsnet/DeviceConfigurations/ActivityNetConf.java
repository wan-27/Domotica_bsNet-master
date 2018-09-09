package com.domotica.domotica_bsnet.DeviceConfigurations;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.domotica.domotica_bsnet.R;
import com.domotica.domotica_bsnet.Utils.Http_Access.MyVolley;
import com.domotica.domotica_bsnet.fragments.DevicesFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import soulissclient.Constants;

import static com.domotica.domotica_bsnet.Utils.Utils.SortJSONARRAY;

public class ActivityNetConf extends AppCompatActivity {
    Spinner spinnerT;
//    WifiManager wifiManager;
    CheckBox cbDHCP, cbVisible;
    Button botonSave, botonCancel;
    EditText ip0, ip1, ip2, ip3, nm0, nm1, nm2, nm3, gw0, gw1, gw2, gw3, dns0, dns1, dns2, dns3, pass;
    private TextView ip, nm, gw, dns;
    private TextView tv1, tv2, tv3, tv4, tv5, tv6, tv7, tv8, tv9, tv10, tv11, tv12;
    //TextView estado;
    private TextView devName;
    private String ipServer, nombre;
    private String ssid;

    static private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.net_options);
        Bundle bundle = getIntent().getExtras();
        ipServer = bundle.getString("ipServer");
        spinnerT = findViewById(R.id.spinnerRed);
        cbDHCP = findViewById(R.id.checkDHCP);
        cbVisible =(CheckBox) findViewById(R.id.checkVisibleNet);
        botonSave = findViewById(R.id.buttonSaveNet);
        botonCancel = findViewById(R.id.buttonCancelNet);
        //estado = findViewById(R.id.textEstado);
        devName = findViewById(R.id.textViewDevNO1);
        nombre = DevicesFragment.getNameLongclick();
        devName.setText(nombre);
        context = this;

        ip0 = findViewById(R.id.textip0);
        ip1 = findViewById(R.id.textip1);
        ip2 = findViewById(R.id.textip2);
        ip3 = findViewById(R.id.textip3);
        nm0 = findViewById(R.id.textnetmask0);
        nm1 = findViewById(R.id.textnetmask1);
        nm2 = findViewById(R.id.textnetmask2);
        nm3 = findViewById(R.id.textnetmask3);
        gw0 = findViewById(R.id.textgateway0);
        gw1 = findViewById(R.id.textgateway1);
        gw2 = findViewById(R.id.textgateway2);
        gw3 = findViewById(R.id.textgateway3);
        dns0 = findViewById(R.id.textdns0);
        dns1 = findViewById(R.id.textdns1);
        dns2 = findViewById(R.id.textdns2);
        dns3 = findViewById(R.id.textdns3);
        pass = findViewById(R.id.editTextPass);
        ip = findViewById(R.id.text_ipOpt);
        nm = findViewById(R.id.text_netmaskOpt);
        gw = findViewById(R.id.text_gatewayOpt);
        dns = findViewById(R.id.text_dnsOpt);

        tv1 = findViewById(R.id.textView2);
        tv2 = findViewById(R.id.textViewip3);
        tv3 = findViewById(R.id.textView3);
        tv4 = findViewById(R.id.textView4);
        tv5 = findViewById(R.id.textView5);
        tv6 = findViewById(R.id.textView6);
        tv7 = findViewById(R.id.textView7);
        tv8 = findViewById(R.id.textView8);
        tv9 = findViewById(R.id.textView9);
        tv10 = findViewById(R.id.textView10);
        tv11 = findViewById(R.id.textView11);
        tv12 = findViewById(R.id.textView12);

        cbDHCP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibleElements(!cbDHCP.isChecked());
            }
        });


        cbVisible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbVisible.isChecked()) {
                    pass.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

        botonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String,String> params = new HashMap<String, String>();

                params.put("ssid", spinnerT.getSelectedItem().toString());
                params.put("password", pass.getText().toString());

                if(cbDHCP.isChecked()) {
                    params.put("dhcp", "checked");
                }
                else{
                    params.put("ip_0", ip0.getText().toString());
                    params.put("ip_1", ip1.getText().toString());
                    params.put("ip_2", ip2.getText().toString());
                    params.put("ip_3", ip3.getText().toString());
                    params.put("nm_0", nm0.getText().toString());
                    params.put("nm_1", nm1.getText().toString());
                    params.put("nm_2", nm2.getText().toString());
                    params.put("nm_3", nm3.getText().toString());
                    params.put("gw_0", gw0.getText().toString());
                    params.put("gw_1", gw1.getText().toString());
                    params.put("gw_2", gw2.getText().toString());
                    params.put("gw_3", gw3.getText().toString());
                    params.put("dns_0", dns0.getText().toString());
                    params.put("dns_1", dns1.getText().toString());
                    params.put("dns_2", dns2.getText().toString());
                    params.put("dns_3", dns3.getText().toString());
                }
                //params.put("devicename",name.getText().toString());
                //params.put("habPercentage", Boolean.toString(percent.isChecked()));

                MyVolley.ServerRequest(getBaseContext(), ipServer, "/updateNet",
                        Request.Method.POST, null, null, params);
            }
        });

        final Response.Listener<String> listenerValues = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Do something with response string
                String[] parts = response.split("\\|"); // Separar por "|"
                ssid = parts[1];
                ip0.setText(parts[3]);
                ip1.setText(parts[5]);
                ip2.setText(parts[7]);
                ip3.setText(parts[9]);
                nm0.setText(parts[11]);
                nm1.setText(parts[13]);
                nm2.setText(parts[15]);
                nm3.setText(parts[17]);
                gw0.setText(parts[19]);
                gw1.setText(parts[21]);
                gw2.setText(parts[23]);
                gw3.setText(parts[25]);
                dns0.setText(parts[27]);
                dns1.setText(parts[29]);
                dns2.setText(parts[31]);
                dns3.setText(parts[33]);
                boolean chk = parts[35].equals("checked");
                setVisibleElements(!chk);
                cbDHCP.setChecked(chk);
            }
        };

        botonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyVolley.ServerRequest(context, ipServer, "/admin/values",
                        Request.Method.GET, listenerValues, null, null);
            }
        });

        //detectWifi();

        MyVolley.ServerRequest(this, ipServer, "/admin/values",
                Request.Method.GET, listenerValues, null, null);


        TimerTask task = new TimerTask() {
            @Override
            public void run() {

                MyVolley.JsonArrayRequest(context, ipServer, "/scan", readWifi, null);

                // Close the activity so the user won't able to go back this
                // activity pressing Back button
                //finish();
            }
        };

        // Simulate a long loading process on application startup.
        Timer timer = new Timer();
        timer.schedule(task, 3000);
    }

    private void showSSIDList(List<String> ssidList){
        ArrayAdapter spinner_adapter;
        spinner_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ssidList);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerT.setAdapter(spinner_adapter);

        for(int i=0; i<ssidList.size(); i++){
            if(ssidList.get(i).equals(ssid)){
                spinnerT.setSelection(i);
                break;
            }
        }
    }

    final Response.Listener<JSONArray> readWifi = new Response.Listener<JSONArray>(){
        @Override
        public void onResponse(JSONArray response) {
            // temporary string to show the parsed response
            //String jsonResponse;

            Log.d(Constants.TAG, response.toString());

            /* TODO: Mostrar las redes encontradas indicando un símbolo de red con el nivel de señal y
                     y un candado con el niver de seguridad
            */
            try {

                response = SortJSONARRAY(response, "rssi");

                List<String> idList = new ArrayList<String>();

                for (int i = 0; i < response.length(); i++) {
                    JSONObject net = (JSONObject) response.get(i);

                    Integer rssi = net.getInt("rssi");
                    String ssid = net.getString("ssid");
                    String bssid = net.getString("bssid");
                    Integer channel = net.getInt("channel");
                    Integer secure = net.getInt("secure");
                    boolean hidden = net.getBoolean("hidden");

                    idList.add(ssid);
                }

                showSSIDList(idList);

            }catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),
                        "Error: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        }
    };

    private void setVisibleElements(boolean bool) {
        int v;
        if(bool) v= View.VISIBLE;
        else v = View.GONE;

        ip0.setVisibility(v);
        ip1.setVisibility(v);
        ip2.setVisibility(v);
        ip3.setVisibility(v);
        nm0.setVisibility(v);
        nm1.setVisibility(v);
        nm2.setVisibility(v);
        nm3.setVisibility(v);
        gw0.setVisibility(v);
        gw1.setVisibility(v);
        gw2.setVisibility(v);
        gw3.setVisibility(v);
        dns0.setVisibility(v);
        dns1.setVisibility(v);
        dns2.setVisibility(v);
        dns3.setVisibility(v);
        ip.setVisibility(v);
        nm.setVisibility(v);
        gw.setVisibility(v);
        dns.setVisibility(v);
        tv1.setVisibility(v);
        tv2.setVisibility(v);
        tv3.setVisibility(v);
        tv4.setVisibility(v);
        tv5.setVisibility(v);
        tv6.setVisibility(v);
        tv7.setVisibility(v);
        tv8.setVisibility(v);
        tv9.setVisibility(v);
        tv10.setVisibility(v);
        tv11.setVisibility(v);
        tv12.setVisibility(v);
    }
}
