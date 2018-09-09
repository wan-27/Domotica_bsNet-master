package com.domotica.domotica_bsnet.Scenes;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
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

public class ActivityShowScene extends AppCompatActivity {
    EditText nombre, tiempo;
    Spinner spinnerAction;
    ToggleButton activo; //
    ToggleButton lunes, martes, miercoles, jueves, viernes, sabado, domingo;
    String ipServer;
    Boolean SceneModified = false;
    TextView textAction;
    String sTiempo;
    Integer position;
    boolean ctrlOpening;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context context = ActivityShowScene.this;

        setContentView(R.layout.scene_info);
        //guardar = findViewById(R.id.buttonSaveScene);
        //cancelar = (Button) findViewById(R.id.buttonCancelScene);
        nombre = findViewById(R.id.editTextNameScene);
        tiempo = findViewById(R.id.editTextTimeScene);
        spinnerAction = findViewById(R.id.spinnerScene);
        activo = findViewById(R.id.toggleActiv);
        lunes = findViewById(R.id.toggleMon);
        martes = findViewById(R.id.toggleTue);
        miercoles = findViewById(R.id.toggleWed);
        jueves = findViewById(R.id.toggleThu);
        viernes = findViewById(R.id.toggleFri);
        sabado = findViewById(R.id.toggleSat);
        domingo = findViewById(R.id.toggleSun);
        textAction = findViewById(R.id.text_Accion);

        TextView devName = findViewById(R.id.textViewDevSI1);
        String name = DevicesFragment.getNameLongclick();
        devName.setText(name);

        CompoundButton.OnCheckedChangeListener ScnModified = new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SceneModified = true;
            }
        };

        activo.setOnCheckedChangeListener(ScnModified);

        final Button guardar = findViewById(R.id.buttonSaveScene);
        guardar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                //String nm = nombre.getText().toString();
                data.putExtra("nombre", nombre.getText().toString());
                data.putExtra("tiempo", tiempo.getText().toString());
                data.putExtra("spinnerAction", spinnerAction.getSelectedItemPosition());
                data.putExtra("activo", activo.isChecked());
                data.putExtra("lunes", lunes.isChecked());
                data.putExtra("martes", martes.isChecked());
                data.putExtra("miercoles", miercoles.isChecked());
                data.putExtra("jueves", jueves.isChecked());
                data.putExtra("viernes", viernes.isChecked());
                data.putExtra("sabado", sabado.isChecked());
                data.putExtra("domingo", domingo.isChecked());
                //data.putExtra("ipServer", ipServer);
                data.putExtra("Position", position);

                setResult(RESULT_OK, data);

                finish();
            }
        });

        final Button Cancel = findViewById(R.id.buttonCancelScene);
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getScnOriginalValues();
            }
        });

        getScnOriginalValues();

        tiempo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Calendar mcurrentTime = Calendar.getInstance();

                String[] parts = sTiempo.split(":");
                int hour = Integer.parseInt(parts[0]);
                int minute = Integer.parseInt(parts[1]);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        tiempo.setText(tiempo(selectedHour,selectedMinute));
                    }
                }, hour ,minute , true);
                mTimePicker.show();

            }
        });

        adaptarVista(ctrlOpening);

    }

    private void getScnOriginalValues(){
        Bundle bundle = getIntent().getExtras();

        Integer.parseInt(getIntent().getStringExtra("action"));

        nombre.setText(bundle.getString("nombre"));
        tiempo.setText(bundle.getString("tiempo"));
        sTiempo = bundle.getString("tiempo");
        activo.setChecked(bundle.getBoolean("activo"));
        lunes.setChecked(bundle.getBoolean("lunes"));
        martes.setChecked(bundle.getBoolean("martes"));
        miercoles.setChecked(bundle.getBoolean("miercoles"));
        jueves.setChecked(bundle.getBoolean("jueves"));
        viernes.setChecked(bundle.getBoolean("viernes"));
        sabado.setChecked(bundle.getBoolean("sabado"));
        domingo.setChecked(bundle.getBoolean("domingo"));
        ipServer = bundle.getString("ipServer");
        position = bundle.getInt("Position");
        ctrlOpening = bundle.getBoolean("ctrlOpening");
    }

    String tiempo(int hora, int minuto){
        String hString, mString;
        if (hora<10){
            hString= "0"+hora;
        } else hString = ""+hora;
        if (minuto<10){
            mString= "0"+minuto;
        } else mString = ""+minuto;

        return(hString+":"+mString);
    }

    private void adaptarVista(boolean percentage) {
        int v = View.VISIBLE;
        int w = View.GONE;

        spinnerAction.setVisibility(v);
        textAction.setVisibility(v);

        ArrayAdapter spinner_adapter;

        if(!percentage){
            textAction.setText(R.string.act_scene);
            spinner_adapter = ArrayAdapter.createFromResource(this, R.array.scene_options, android.R.layout.simple_spinner_item);
            spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }
        else{
            textAction.setText(R.string.action_percent);
            spinner_adapter = ArrayAdapter.createFromResource(this, R.array.openPercentage, android.R.layout.simple_spinner_item);
            spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }

        spinnerAction.setAdapter(spinner_adapter);
        spinnerAction.setSelection(Integer.parseInt(getIntent().getStringExtra("action")));
    }
}
