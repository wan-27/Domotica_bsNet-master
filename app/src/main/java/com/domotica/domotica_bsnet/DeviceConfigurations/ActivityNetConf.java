package com.domotica.domotica_bsnet.DeviceConfigurations;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
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
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.domotica.domotica_bsnet.R;
import com.domotica.domotica_bsnet.Utils.Http_Access.MyVolley;
import com.domotica.domotica_bsnet.fragments.DevicesFragment;

import java.util.ArrayList;
import java.util.List;

public class ActivityNetConf extends AppCompatActivity {
    Spinner spinnerT;
    WifiManager wifiManager;
    CheckBox checkBox, checkVisible;
    Button botonSave, botonCancel;
    EditText ip1, ip2, ip3, ip4, nm1, nm2, nm3, nm4, gw1, gw2, gw3, gw4, dns1, dns2, dns3, dns4, pass;
    private TextView ip, nm, gw, dns;
    private TextView tv1, tv2, tv3, tv4, tv5, tv6, tv7, tv8, tv9, tv10, tv11, tv12;
    TextView estado, devName;
    String ipServer, nombre;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.net_options);
        Bundle bundle = getIntent().getExtras();
        ipServer = bundle.getString("ipServer");
        spinnerT = (Spinner) findViewById(R.id.spinnerRed);
        checkBox = (CheckBox) findViewById(R.id.checkDHCP);
        checkVisible = (CheckBox) findViewById(R.id.checkVisibleNet);
        botonSave = (Button) findViewById(R.id.buttonSaveNet);
        botonCancel = (Button) findViewById(R.id.buttonCancelNet);
        estado = (TextView) findViewById(R.id.textEstado);
        devName = (TextView) findViewById(R.id.textViewDevNO1);
        nombre = DevicesFragment.getNameLongclick();
        devName.setText(nombre);

        ip1 = (EditText) findViewById(R.id.textip1);
        ip2 = (EditText) findViewById(R.id.textip2);
        ip3 = (EditText) findViewById(R.id.textip3);
        ip4 = (EditText) findViewById(R.id.textip4);
        nm1 = (EditText) findViewById(R.id.textnetmask1);
        nm2 = (EditText) findViewById(R.id.textnetmask2);
        nm3 = (EditText) findViewById(R.id.textnetmask3);
        nm4 = (EditText) findViewById(R.id.textnetmask4);
        gw1 = (EditText) findViewById(R.id.textgateway1);
        gw2 = (EditText) findViewById(R.id.textgateway2);
        gw3 = (EditText) findViewById(R.id.textgateway3);
        gw4 = (EditText) findViewById(R.id.textgateway4);
        dns1 = (EditText) findViewById(R.id.textdns1);
        dns2 = (EditText) findViewById(R.id.textdns2);
        dns3 = (EditText) findViewById(R.id.textdns3);
        dns4 = (EditText) findViewById(R.id.textdns4);
        pass = (EditText) findViewById(R.id.editTextPass);

        pass = (EditText) findViewById(R.id.editTextPass);
        ip = (TextView) findViewById(R.id.text_ipOpt);
        nm = (TextView) findViewById(R.id.text_netmaskOpt);
        gw = (TextView) findViewById(R.id.text_gatewayOpt);
        dns = (TextView) findViewById(R.id.text_dnsOpt);

        tv1 = (TextView) findViewById(R.id.textView2);
        tv2 = (TextView) findViewById(R.id.textViewip3);
        tv3 = (TextView) findViewById(R.id.textView3);
        tv4 = (TextView) findViewById(R.id.textView4);
        tv5 = (TextView) findViewById(R.id.textView5);
        tv6 = (TextView) findViewById(R.id.textView6);
        tv7 = (TextView) findViewById(R.id.textView7);
        tv8 = (TextView) findViewById(R.id.textView8);
        tv9 = (TextView) findViewById(R.id.textView9);
        tv10 = (TextView) findViewById(R.id.textView10);
        tv11 = (TextView) findViewById(R.id.textView11);
        tv12 = (TextView) findViewById(R.id.textView12);

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    setVisibleElements(false);
                } else {
                    setVisibleElements(true);

                }
            }
        });

        checkBox.setChecked(true);
        setVisibleElements(false);

        checkVisible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkVisible.isChecked()) {
                    pass.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

        botonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        botonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        detectWifi();

        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Do something with response string
                String[] parts = response.split("\\|"); // Separar por "|"
                ip1.setText(parts[5]);
                ip2.setText(parts[7]);
                ip3.setText(parts[9]);
                ip4.setText(parts[11]);
                nm1.setText(parts[13]);
                nm2.setText(parts[15]);
                nm3.setText(parts[17]);
                nm4.setText(parts[19]);
                gw1.setText(parts[21]);
                gw2.setText(parts[23]);
                gw3.setText(parts[25]);
                gw4.setText(parts[27]);
                dns1.setText(parts[29]);
                dns2.setText(parts[31]);
                dns3.setText(parts[33]);
                dns4.setText(parts[35]);
                boolean chk = parts[37].equals("checked");
                setVisibleElements(!chk);
                checkBox.setChecked(chk);
            }
        };

        MyVolley.ServerRequest(this, ipServer, "/admin/values",
                Request.Method.GET, listener, null, null);

        /*
        StringRequest request = prepararPeticion();
        // Add StringRequest to the RequestQueue
        MyVolley.getInstance(this).addRequest(request);
        */
    }


    public void detectWifi() {

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        List<ScanResult> wifilist;

        wifilist = wifiManager.getScanResults();
        List<String> idList = new ArrayList<String>();
        String ssid = "";

        for (int i = 0; i < wifilist.size(); i++) {
            ssid = wifilist.get(i).SSID;
            idList.add(ssid);
        }

        ArrayAdapter spinner_adapter;
        spinner_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, idList);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerT.setAdapter(spinner_adapter);
    }

    private void setVisibleElements(boolean bool) {
        int v;
        if(bool) v= View.VISIBLE;
        else v = View.GONE;

        ip1.setVisibility(v);
        ip2.setVisibility(v);
        ip3.setVisibility(v);
        ip4.setVisibility(v);
        nm1.setVisibility(v);
        nm2.setVisibility(v);
        nm3.setVisibility(v);
        nm4.setVisibility(v);
        gw1.setVisibility(v);
        gw2.setVisibility(v);
        gw3.setVisibility(v);
        gw4.setVisibility(v);
        dns1.setVisibility(v);
        dns2.setVisibility(v);
        dns3.setVisibility(v);
        dns4.setVisibility(v);
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

    /*
    private StringRequest prepararPeticion(){
        String mUrlString = "http://"+ipServer+"/admin/values";
        StringRequest stringRequest = new StringRequest(
                mUrlString,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Do something with response string
                        String[] parts = response.split("\\|"); // Separar por "|"
                        ip1.setText(parts[5]);
                        ip2.setText(parts[7]);
                        ip3.setText(parts[9]);
                        ip4.setText(parts[11]);
                        nm1.setText(parts[13]);
                        nm2.setText(parts[15]);
                        nm3.setText(parts[17]);
                        nm4.setText(parts[19]);
                        gw1.setText(parts[21]);
                        gw2.setText(parts[23]);
                        gw3.setText(parts[25]);
                        gw4.setText(parts[27]);
                        dns1.setText(parts[29]);
                        dns2.setText(parts[31]);
                        dns3.setText(parts[33]);
                        dns4.setText(parts[35]);
                        boolean chk = parts[37].equals("checked");
                        setEnabledEditText(!chk);
                        checkBox.setChecked(chk);



                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getBaseContext(), "ERROR",Toast.LENGTH_LONG).show();
                    }
                }
        );
        return stringRequest;
    }
    */
}
