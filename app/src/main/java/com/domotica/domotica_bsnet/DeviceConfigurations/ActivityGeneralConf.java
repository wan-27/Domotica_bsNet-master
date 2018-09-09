package com.domotica.domotica_bsnet.DeviceConfigurations;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.domotica.domotica_bsnet.R;
import com.domotica.domotica_bsnet.Utils.Http_Access.MyVolley;

import java.util.HashMap;
import java.util.Map;

public class ActivityGeneralConf extends AppCompatActivity {
    EditText name;
    Button save;
    CheckBox percent;
    Button reboot;
    String ipServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_options);
        Bundle bundle = getIntent().getExtras();
        ipServer = bundle.getString("ipServer");
        name = (EditText) findViewById(R.id.editTextName);
        save = (Button) findViewById(R.id.buttonSave);
        percent = (CheckBox) findViewById(R.id.checkPercent);
        reboot = (Button) findViewById(R.id.buttonReboot);
        //String nameDevice = bundle.getString("nameDevice");
        //TextView nmDevice = (TextView) findViewById(R.id.titleGenConf);
        //nmDevice.setText(nameDevice);
        ((TextView) this.findViewById(R.id.textViewDevGO1)).setText(bundle.getString("nameDevice"));

        name.setEnabled(false);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,String> params = new HashMap<String, String>();
                params.put("devicename",name.getText().toString());
                params.put("habPercentage", Boolean.toString(percent.isChecked()));

                MyVolley.ServerRequest(getBaseContext(), ipServer, "/setGeneralVal",
                        Request.Method.POST, null, null, params);
            }
        });


        reboot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reiniciar();
            }
        });

        final Button Cancel = (Button) findViewById(R.id.buttonCancel);
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceName();
            }
        });

        getDeviceName();
    }

    Response.Listener<String> listener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            // Do something with response string
            String[] parts = response.split("\\|"); // Separar por "|"
            name.setText(parts[1]);
            percent.setChecked(parts[3].compareTo("true") == 0);
        }
    };

    private void getDeviceName(){
        MyVolley.ServerRequest(this, ipServer, "/admin/generalvalues",
                Request.Method.GET, listener, null, null);
    }

    private void reiniciar(){
        MyVolley.ServerRequest(this, ipServer, "/admin/restart",
                Request.Method.GET, listener, null, null);
    }
}
