package com.domotica.domotica_bsnet.DeviceConfigurations;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.domotica.domotica_bsnet.R;
import com.domotica.domotica_bsnet.Utils.Http_Access.MyVolley;
import com.domotica.domotica_bsnet.fragments.DevicesFragment;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ActivityNtpConf extends AppCompatActivity {
    static int zh[] = {-120, -110, -100, -90, -80, -70, -60, -50, -40, -35, -30, -20, -10, 0, 10, 20, 30, 35, 40, 45, 50, 55, 57, 60, 65, 70, 80, 90, 95, 100, 110, 120, 130};
    Spinner spinnerT;
    EditText ntpServer, update, editHour;
    CheckBox summerTime, activarNtp;
    Button save, cancel;
    String ipServer, nombre;
    private TextView actNtp, time, upd, tZone, sTime, info, devName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ntp_options);
        Bundle bundle = getIntent().getExtras();
        ipServer = bundle.getString("ipServer");
        final Context context = ActivityNtpConf.this;

        ntpServer = (EditText) findViewById(R.id.editServer);
        update = (EditText) findViewById(R.id.editUpdate);
        summerTime = (CheckBox) findViewById(R.id.checkSummer);
        save = (Button) findViewById(R.id.buttonSaveNtp);
        cancel = (Button) findViewById(R.id.buttonCancelNtp);
        spinnerT = (Spinner) findViewById(R.id.spinnerNtp);
        ArrayAdapter spinner_adapter = ArrayAdapter.createFromResource( this, R.array.TZone , android.R.layout.simple_spinner_item);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerT.setAdapter(spinner_adapter);
        editHour = (EditText) findViewById(R.id.editHour);
        activarNtp = (CheckBox) findViewById(R.id.checkNtp);
        info = (TextView) findViewById(R.id.textView14);
        actNtp = (TextView) findViewById(R.id.text_ntpserver);
        time = (TextView) findViewById(R.id.text_inputDate);
        upd = (TextView) findViewById(R.id.text_ntpUpdate);
        tZone = (TextView) findViewById(R.id.text_zone);
        sTime = (TextView) findViewById(R.id.text_summer);

        devName = (TextView) findViewById(R.id.textViewDevNTPO1);
        nombre = DevicesFragment.getNameLongclick();
        devName.setText(nombre);

        setVisibleElements(false);
        activarNtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activarNtp.isChecked()) {
                    setVisibleElements(true);
                } else {
                    setVisibleElements(false);
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dstval = "";
                Map<String,String> params = new HashMap<String, String>();
                params.put("ntpserver",ntpServer.getText().toString());
                params.put("update",update.getText().toString());
                params.put("tz",String.valueOf(zh[spinnerT.getSelectedItemPosition()]));
                if(summerTime.isChecked())
                    dstval = "checked";
                params.put("dst",dstval);

                MyVolley.ServerRequest(getBaseContext(), ipServer, "/updateNTP",
                        Request.Method.POST, null, null, params);
            }
        });

        Calendar ca = Calendar.getInstance();
        int ho, mi;
        ho = ca.getTime().getHours();
        mi = ca.getTime().getMinutes();

        editHour.setText(tiempo(ho, mi));

        editHour.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int hour, minute;
                hour = c.getTime().getHours();
                minute = c.getTime().getMinutes();
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        editHour.setText(tiempo(selectedHour, selectedMinute));
                    }
                }, hour, minute, true);
                mTimePicker.show();

            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

/*
        StringRequest request = prepararPeticion();
        // Add StringRequest to the RequestQueue
        MyVolley.getInstance(this).addRequest(request);
*/
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Do something with response string
                String[] parts = response.split("\\|"); // Separar por "|"
                ntpServer.setText(parts[1]);
                update.setText(parts[3]);
                for(int i = 0; i<zh.length; i++) {
                    if(zh[i] == Integer.parseInt(parts[5])) {
                        spinnerT.setSelection(i);
                        break;
                    }
                }
                boolean chk = parts[7].equals("checked");

                summerTime.setChecked(chk);
            }
        };

        MyVolley.ServerRequest(this, ipServer, "/admin/ntpvalues",
                Request.Method.GET, listener, null, null);
    }


    private void setVisibleElements(boolean bool) {
        int v, w;
        if (bool) {
            v = View.VISIBLE;
            w = View.GONE;
        } else {
            v = View.GONE;
            w = View.VISIBLE;
        }


        spinnerT.setVisibility(v);
        ntpServer.setVisibility(v);
        update.setVisibility(v);
        upd.setVisibility(v);
        tZone.setVisibility(v);
        actNtp.setVisibility(v);
        info.setVisibility(v);
        summerTime.setVisibility(v);
        sTime.setVisibility(v);
        editHour.setVisibility(w);
        time.setVisibility(w);

    }

    private String tiempo(int hora, int minuto) {
        String hString, mString;
        if (hora < 10) {
            hString = "0" + hora;
        } else hString = "" + hora;
        if (minuto < 10) {
            mString = "0" + minuto;
        } else mString = "" + minuto;

        return (hString + ":" + mString);
    }

/*
    private StringRequest prepararPeticion(){
        String mUrlString = "http://"+ipServer+"/admin/ntpvalues";
        StringRequest stringRequest = new StringRequest(
                mUrlString,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Do something with response string
                        String[] parts = response.split("\\|"); // Separar por "|"
                        ntpServer.setText(parts[1]);
                        update.setText(parts[3]);
                        boolean chk = parts[7].equals("checked");

                        summerTime.setChecked(chk);
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
