package com.domotica.domotica_bsnet.DeviceConfigurations;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.domotica.domotica_bsnet.R;
import com.domotica.domotica_bsnet.Utils.Http_Access.MyVolley;
import com.domotica.domotica_bsnet.fragments.DevicesFragment;


public class ActivitySystemConf extends AppCompatActivity {
    private CheckBox checkBoxAuth, checkVisible;
    private EditText user, pass;
    private Button save, reboot, cancel;
    private String ipServer, nombre;
    private TextView devName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.system_options);
        Bundle bundle = getIntent().getExtras();
        ipServer = bundle.getString("ipServer");

        checkBoxAuth = (CheckBox) findViewById(R.id.checkAuth);
        checkVisible = (CheckBox) findViewById(R.id.checkVisible);

        user = (EditText) findViewById(R.id.editTextUser);
        pass = (EditText) findViewById(R.id.editTextPass);
        reboot = (Button) findViewById(R.id.buttonReboot);
        save = (Button) findViewById(R.id.buttonSaveSystemConf);
        cancel = (Button) findViewById(R.id.buttonCancelSystemConf);

        devName = (TextView) findViewById(R.id.textViewDevSO1);
        nombre = DevicesFragment.getNameLongclick();
        devName.setText(nombre);

        setEnabledEditText(false);

        checkBoxAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBoxAuth.isChecked()) {
                    setEnabledEditText(true);
                } else {
                    setEnabledEditText(false);

                }
            }
        });

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


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        reboot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reiniciar();
            }
        });

    }

    private void reiniciar() {
        MyVolley.ServerRequest(this, ipServer, "/admin/restart",
                Request.Method.GET, null, null, null);
    }

    private void setEnabledEditText(boolean bool) {
        user.setEnabled(bool);
        pass.setEnabled(bool);
        checkVisible.setEnabled(bool);
    }

    /*
    private StringRequest prepararPeticion() {
        String mUrlString = "http://" + ipServer + "/admin/restart";
        StringRequest stringRequest = new StringRequest(
                mUrlString,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        );
        return stringRequest;
    }
    */
}