package com.domotica.domotica_bsnet.Scenes;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.domotica.domotica_bsnet.R;
import com.domotica.domotica_bsnet.fragments.DevicesFragment;

import org.w3c.dom.Text;

import java.util.Calendar;

public class ActivityShowScene extends AppCompatActivity {
    EditText nombre, tiempo;
    Spinner spinner, spinnerPercent;
    ToggleButton activo; //
    ToggleButton lunes, martes, miercoles, jueves, viernes, sabado, domingo;
    Button cancelar, guardar;
    Context context;
    String ipServer, name;
    Boolean SceneModified = false;
    boolean percentage;
    TextView textAction, textPercent, devName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.context;

        setContentView(R.layout.scene_info);
        guardar = (Button) findViewById(R.id.buttonSaveScene);
        cancelar = (Button) findViewById(R.id.buttonCancelScene);
        nombre = (EditText) findViewById(R.id.editTextNameScene);
        tiempo = (EditText) findViewById(R.id.editTextTimeScene);
        spinner = (Spinner) findViewById(R.id.spinnerScene);
        spinnerPercent = (Spinner) findViewById(R.id.spinnerPercent);
        activo = (ToggleButton) findViewById(R.id.toggleActiv);
        lunes = (ToggleButton) findViewById(R.id.toggleMon);
        martes = (ToggleButton) findViewById(R.id.toggleTue);
        miercoles = (ToggleButton) findViewById(R.id.toggleWed);
        jueves = (ToggleButton) findViewById(R.id.toggleThu);
        viernes = (ToggleButton) findViewById(R.id.toggleFri);
        sabado = (ToggleButton) findViewById(R.id.toggleSat);
        domingo = (ToggleButton) findViewById(R.id.toggleSun);
        textAction = (TextView) findViewById(R.id.text_Accion);
        textPercent = (TextView) findViewById(R.id.text_percent);

        devName = (TextView) findViewById(R.id.textViewDevSI1);
        name = DevicesFragment.getNameLongclick();
        devName.setText(name);

        // obtener valor del boolean percentage
        percentage = true;
        adaptarVista(percentage);

        CompoundButton.OnCheckedChangeListener ScnModified = new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SceneModified = true;
            }
        };

        activo.setOnCheckedChangeListener(ScnModified);




        Bundle bundle = getIntent().getExtras();
        nombre.setText(bundle.getString("nombre"));
        tiempo.setText(bundle.getString("tiempo"));
        final String sTiempo = bundle.getString("tiempo");
        int select = 0;
        spinner.setSelection(Integer.parseInt(bundle.getString("spinner")));
        activo.setChecked(bundle.getBoolean("activo"));
        lunes.setChecked(bundle.getBoolean("lunes"));
        martes.setChecked(bundle.getBoolean("martes"));
        miercoles.setChecked(bundle.getBoolean("miercoles"));
        jueves.setChecked(bundle.getBoolean("jueves"));
        viernes.setChecked(bundle.getBoolean("viernes"));
        sabado.setChecked(bundle.getBoolean("sabado"));
        domingo.setChecked(bundle.getBoolean("domingo"));
        ipServer = bundle.getString("ipServer");


        tiempo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String[] parts = sTiempo.split(":");
                int hour = Integer.parseInt(parts[0]);
                int minute = Integer.parseInt(parts[1]);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        tiempo.setText(tiempo(selectedHour, selectedMinute));
                    }
                }, hour, minute, true);
                mTimePicker.show();

            }
        });

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    String tiempo(int hora, int minuto) {
        String hString, mString;
        if (hora < 10) {
            hString = "0" + hora;
        } else hString = "" + hora;
        if (minuto < 10) {
            mString = "0" + minuto;
        } else mString = "" + minuto;

        return (hString + ":" + mString);
    }

    private void adaptarVista(boolean b) {
        int v = View.VISIBLE;
        int w = View.GONE;

        if (b) {
            spinnerPercent.setVisibility(v);
            textPercent.setVisibility(v);
            spinner.setVisibility(w);
            textAction.setVisibility(w);

            ArrayAdapter spinner_adapter_percent = ArrayAdapter.createFromResource(this, R.array.openPercentage, android.R.layout.simple_spinner_item);
            spinner_adapter_percent.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerPercent.setAdapter(spinner_adapter_percent);
        } else {
            spinner.setVisibility(v);
            textAction.setVisibility(v);
            spinnerPercent.setVisibility(w);
            textPercent.setVisibility(w);

            ArrayAdapter spinner_adapter = ArrayAdapter.createFromResource(this, R.array.scene_options, android.R.layout.simple_spinner_item);
            spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(spinner_adapter);

        }
    }

}