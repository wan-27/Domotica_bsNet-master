package com.domotica.domotica_bsnet.DeviceConfigurations;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.domotica.domotica_bsnet.R;
import com.domotica.domotica_bsnet.Utils.Http_Access.MyVolley;
import com.domotica.domotica_bsnet.fragments.DevicesFragment;

public class ActivityNetInfo extends AppCompatActivity {
    TextView ssid, ip, netmask, gateway, dns, mac, ntpTime, ntpDate, lastSync;
    Button refresh;
    String ipServer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.net_info);

        Bundle bundle = getIntent().getExtras();
        ipServer = bundle.getString("ipServer");
        ssid = findViewById(R.id.text_ssid2);
        ip = findViewById(R.id.text_ip2);
        netmask = findViewById(R.id.text_netmaskNi);
        gateway = findViewById(R.id.text_gateway2);
        dns = findViewById(R.id.text_dns2);
        mac = findViewById(R.id.text_mac2);
        ntpTime = findViewById(R.id.text_ntptime2);
        ntpDate = findViewById(R.id.text_ntpdate2);
        lastSync = findViewById(R.id.text_lastsync2);
        refresh = findViewById(R.id.buttonRefresh);

        ((TextView) findViewById(R.id.textViewDevNI1)).setText(DevicesFragment.getNameLongclick());

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarDatos();
            }
        });

        cargarDatos();
    }

    private void cargarDatos(){
        StringRequest request = prepararPeticion();

        // Add StringRequest to the RequestQueue
        MyVolley.getInstance(this).addRequest(request);
    }



    private StringRequest prepararPeticion(){
        String mUrlString = "http://"+ipServer+"/admin/infovalues";
        StringRequest stringRequest = new StringRequest(
                mUrlString,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Do something with response string
                        String[] parts = response.split("\\|"); // Separar por "|"
                        ssid.setText(parts[3]);
                        ip.setText(parts[5]);
                        gateway.setText(parts[7]);
                        netmask.setText(parts[9]);
                        mac.setText(parts[11]);
                        dns.setText(parts[13]);
                        lastSync.setText(parts[15]);
                        ntpTime.setText(parts[17]);
                        ntpDate.setText(parts[19]);
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
}
