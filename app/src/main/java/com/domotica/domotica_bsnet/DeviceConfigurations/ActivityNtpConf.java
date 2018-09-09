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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ActivityNtpConf extends AppCompatActivity {
    static int zh[] = {-120, -110, -100, -90, -80, -70, -60, -50, -40, -35, -30, -20, -10, 0, 10, 20, 30, 35, 40, 45, 50, 55, 57, 60, 65, 70, 80, 90, 95, 100, 110, 120, 130};
    Spinner spinnerT;
    EditText ntpServer, update;
    CheckBox summerTime, activarNtp, checkScn;
    Button save, cancel;
    String ipServer;
    private TextView actNtp, time, upd, tZone, sTime, info, tvDate, actScn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ntp_options);
        Bundle bundle = getIntent().getExtras();
        ipServer = bundle.getString("ipServer");
        final Context context = ActivityNtpConf.this;

        ntpServer = findViewById(R.id.editServer);
        update = findViewById(R.id.editUpdate);
        summerTime = findViewById(R.id.checkSummer);
        save =  findViewById(R.id.buttonSaveNtp);
        cancel = findViewById(R.id.buttonCancelNtp);
        spinnerT = findViewById(R.id.spinnerNtp);
        ArrayAdapter spinner_adapter = ArrayAdapter.createFromResource( this, R.array.TZone , android.R.layout.simple_spinner_item);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerT.setAdapter(spinner_adapter);
        //editHour = findViewById(R.id.tvDate);
        activarNtp = (CheckBox) findViewById(R.id.checkNtp);
        info = (TextView) findViewById(R.id.textView14);
        actNtp = (TextView) findViewById(R.id.text_ntpserver);
        time = (TextView) findViewById(R.id.text_inputDate);
        upd = (TextView) findViewById(R.id.text_ntpUpdate);
        tZone = (TextView) findViewById(R.id.text_zone);
        sTime = (TextView) findViewById(R.id.text_summer);
        tvDate = (TextView)findViewById(R.id.tvDate);
        ((TextView) findViewById(R.id.textViewDevNTPO1)).setText(DevicesFragment.getNameLongclick());

        actScn = (TextView) findViewById(R.id.text_actScn);
        checkScn = findViewById(R.id.checkScn);

        setVisibleElements(false);

        activarNtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibleElements(activarNtp.isChecked());
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dstval = "";
                Map<String,String> params = new HashMap<String, String>();

                if(!activarNtp.isChecked()){
                    Calendar ca = Calendar.getInstance();

                    params.put("ntpactive", "unchecked");
                    params.put("hour", String.valueOf(ca.get(Calendar.HOUR_OF_DAY)));
                    params.put("minute", String.valueOf(ca.get(Calendar.MINUTE)));
                    params.put("year", String.valueOf(ca.get(Calendar.YEAR)));
                    params.put("month", String.valueOf(ca.get(Calendar.MONTH)));
                    params.put("day", String.valueOf(ca.get(Calendar.DAY_OF_MONTH)));
                }
                else {
                    params.put("ntpactive", "checked");
                    params.put("ntpserver",ntpServer.getText().toString());
                    params.put("update",update.getText().toString());
                    params.put("tz",String.valueOf(zh[spinnerT.getSelectedItemPosition()]));
                    if(summerTime.isChecked())
                        params.put("dst","checked");
                }

                MyVolley.ServerRequest(getBaseContext(), ipServer, "/updateNTP",
                        Request.Method.POST, null, null, params);
            }
        });

        //DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar ca = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat(getString(R.string.lanDateFormat));
        String date = df.format(ca.getTime());
        tvDate.setText(date);
        //editHour.setText(date);

        //editHour.setText(tiempo(ho, mi));
/*
        editHour.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int hour, minute;
                hour =  c.get(Calendar.HOUR_OF_DAY);
                minute = c.get(Calendar.MINUTE);
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
*/
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ntpValues();
            }
        });

/*
        StringRequest request = prepararPeticion();
        // Add StringRequest to the RequestQueue
        MyVolley.getInstance(this).addRequest(request);
*/

        ntpValues();
    }

    private void ntpValues(){

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
                activarNtp.setChecked(parts[9].compareTo("true") == 0);
                setVisibleElements(activarNtp.isChecked());
//                editHour.setText(parts[11]);
            }
        };


        MyVolley.ServerRequest(this, ipServer, "/admin/ntpvalues",
                Request.Method.GET, listener, null, null);
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
        tvDate.setVisibility(w);
        time.setVisibility(w);
        actScn.setVisibility(w);
        checkScn.setVisibility(w);
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
