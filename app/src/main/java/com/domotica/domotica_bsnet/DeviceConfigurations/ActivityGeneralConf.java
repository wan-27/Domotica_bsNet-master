package com.domotica.domotica_bsnet.DeviceConfigurations;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.domotica.domotica_bsnet.R;
import com.domotica.domotica_bsnet.Utils.Http_Access.MyVolley;
import com.domotica.domotica_bsnet.fragments.DevicesFragment;

import java.util.HashMap;
import java.util.Map;

public class ActivityGeneralConf extends AppCompatActivity {
    EditText name;
    TextView devName;
    Button save, cancel;
    String nombre;
    String ipServer;
    CheckBox percent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_options);
        Bundle bundle = getIntent().getExtras();
        ipServer = bundle.getString("ipServer");
        name = (EditText) findViewById(R.id.editTextName);
        save = (Button) findViewById(R.id.buttonSave);
        cancel = (Button) findViewById(R.id.buttonCancel);
        percent = (CheckBox) findViewById(R.id.checkPercent);

        devName = (TextView) findViewById(R.id.textViewDevGO1);
        nombre = DevicesFragment.getNameLongclick();
        devName.setText(nombre);


        percent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (percent.isChecked()) {
                    //ACTIVAR MODO CON PORCENTAJES
                } else {
                    //DESACTIVAR MODO CON PORCENTAJES
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,String> params = new HashMap<String, String>();
                params.put("devicename",name.getText().toString());

                MyVolley.ServerRequest(getBaseContext(), ipServer, "/updateConfig",
                        Request.Method.POST, null, null, params);
            }
        });




        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Do something with response string
                String[] parts = response.split("\\|"); // Separar por "|"
                name.setText(parts[1]);
            }
        };

        MyVolley.ServerRequest(this, ipServer, "/admin/generalvalues",
                Request.Method.GET, listener, null, null);
    }
}
