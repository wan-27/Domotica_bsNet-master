package com.domotica.domotica_bsnet.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.domotica.domotica_bsnet.R;
import com.domotica.domotica_bsnet.Scenes.ActivityScene;
import com.domotica.domotica_bsnet.Scenes.ActivityShowScene;
import com.domotica.domotica_bsnet.Scenes.Scene;
import com.domotica.domotica_bsnet.Utils.Http_Access.MyVolley;
import com.domotica.domotica_bsnet.Scenes.SceneAdapter;
import com.domotica.domotica_bsnet.Utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import soulissclient.Constants;

import static android.app.Activity.RESULT_OK;

public class SceneFragment extends Fragment {
    private ListView mSceneList;
    private SceneAdapter mSceneAdapter;
    private String ipServer;
    private TextView numEscenas;
    private List<Scene> escenasTotal;
    private Button anadirEscena, reload;
    private JSONArray arrayScenes;
    private HashMap<String, Scene> scenes;
    private boolean ctrlOpening = false;

    public SceneFragment() {
        // Required empty public constructor
    }

    public void onResume(){
        super.onResume();

        loadScenes();
    }
    public static SceneFragment newInstance(/*parámetros*/) {
        SceneFragment fragment = new SceneFragment();
        // Setup parámetros
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
        }

        ActivityScene activity = (ActivityScene) getActivity();
        ipServer = activity.getIpServer();
        ctrlOpening = activity.getctrlOpening();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_scene, container, false);

        String name = DevicesFragment.getNameLongclick();
        TextView devName = (TextView) root.findViewById(R.id.textViewDevFS1);
        devName.setText(name);

        // Instancia del ListView.
        mSceneList = (ListView) root.findViewById(R.id.scene_list);
        numEscenas = (TextView) root.findViewById(R.id.textViewScene3);

        //Get Scenes from Server
        loadScenes();

        reload = (Button) root.findViewById(R.id.buttonReloadScn);
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadScenes();
            }
        });

        anadirEscena = (Button) root.findViewById(R.id.addScene);
        anadirEscena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSceneAdapter.addScene();
                mSceneAdapter.notifyDataSetChanged();

                //Abrir escena añadida

                int pos;
                if (scenes.isEmpty()) {
                    pos = 0;
                } else {
                    pos = scenes.size();
                }

                mSceneList.performItemClick(mSceneList.getChildAt(pos),pos,mSceneList.getAdapter().getItemId(pos));




                /*
                Map<String,String> params = new HashMap<String, String>();
                try {
                    params.put("newScenes",arrayScenes.get(0).toString());
                    MyVolley.ServerRequest(getContext(),ipServer,"/setScenes", Request.Method.POST,
                            null, null, params);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                */
            }
        });

        setHasOptionsMenu(true);
        return root;
    }

    public void esperarYcargar() {
        int milisegundos = 200;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // acciones que se ejecutan tras los milisegundos
                loadScenes();
            }
        }, milisegundos);
    }

    private void IncicializarLista() {
        // Inicializar el adaptador con la fuente de datos.

        mSceneAdapter = new SceneAdapter(getActivity(), escenasTotal, ctrlOpening);
        //Relacionando la lista con el adaptador
        mSceneList.setAdapter(mSceneAdapter);

        mSceneList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int pos, long l) {
                //scene_longclick = pos;

                scene_delete(pos);

                return true;
            }
        });

        // Eventos
        mSceneList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Scene currentScene = (Scene) mSceneAdapter.getItem(position);

                Intent i = new Intent(getContext(), ActivityShowScene.class);
                i.putExtra("nombre", currentScene.getNombre());
                i.putExtra("tiempo", currentScene.getTiempo());
                i.putExtra("action", currentScene.getAccion());
                i.putExtra("activo", currentScene.isActivo());
                i.putExtra("lunes", currentScene.isLunes());
                i.putExtra("martes", currentScene.isMartes());
                i.putExtra("miercoles", currentScene.isMiercoles());
                i.putExtra("jueves", currentScene.isJueves());
                i.putExtra("viernes", currentScene.isViernes());
                i.putExtra("sabado", currentScene.isSabado());
                i.putExtra("domingo", currentScene.isDomingo());
                i.putExtra("ipServer", ipServer);
                i.putExtra("Position", position);
                i.putExtra("ctrlOpening", ctrlOpening);
                startActivityForResult(i, 0);
                //startActivity(i);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            mSceneAdapter.getScene(data.getIntExtra("Position", 0)).setNombre(data.getStringExtra("nombre"));
            mSceneAdapter.getScene(data.getIntExtra("Position", 0)).setTiempo(data.getStringExtra("tiempo"));
            mSceneAdapter.getScene(data.getIntExtra("Position", 0)).setLunes(data.getBooleanExtra("lunes", false));
            mSceneAdapter.getScene(data.getIntExtra("Position", 0)).setMartes(data.getBooleanExtra("martes", false));
            mSceneAdapter.getScene(data.getIntExtra("Position", 0)).setMiercoles(data.getBooleanExtra("miercoles", false));
            mSceneAdapter.getScene(data.getIntExtra("Position", 0)).setJueves(data.getBooleanExtra("jueves", false));
            mSceneAdapter.getScene(data.getIntExtra("Position", 0)).setViernes(data.getBooleanExtra("viernes", false));
            mSceneAdapter.getScene(data.getIntExtra("Position", 0)).setSabado(data.getBooleanExtra("sabado", false));
            mSceneAdapter.getScene(data.getIntExtra("Position", 0)).setDomingo(data.getBooleanExtra("domingo", false));
            mSceneAdapter.getScene(data.getIntExtra("Position", 0)).setActivo(data.getBooleanExtra("activo", false));
            mSceneAdapter.getScene(data.getIntExtra("Position", 0)).setTiempo(data.getStringExtra("tiempo"));
            mSceneAdapter.getScene(data.getIntExtra("Position", 0)).setAccion(String.valueOf(data.getIntExtra("spinnerAction", 0)));

            mSceneAdapter.notifyDataSetChanged();

            //numEscenas.setText(": " + (scenes.size() + 1));
            //Create and send Json from scenes
            createscenesjson();

            /*
            if(data.getBooleanExtra("restart", false)){
                openMotorControlActivity(data.getStringExtra("device"), data.getStringExtra("ipaddress"), false);
            }
            */
        }
    }

    private void createscenesjson() {

        JSONArray scnArr = new JSONArray();

        try {

            for (int n = 0; n < mSceneAdapter.Size(); n++) {
                scnArr.put(mSceneAdapter.getScene(n).toJsonObject());
            }

            JSONObject ScenesObj = new JSONObject();
            ScenesObj.put("Scenas", scnArr);

            //Enviar json al dispositivo.
            MyVolley.JsonObjectRequest(getContext(), Request.Method.POST, ipServer, "/setScenes", ScenesObj, null, null);

            //Get Scenes from Server
            esperarYcargar();
            //loadScenes();


            //Log.i(Constants.TAG, ScenesObj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadScenes() {

        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    arrayScenes = new JSONArray(response);

                    if (scenes == null)
                        scenes = new HashMap<>();
                    else scenes.clear();

                    JSONObject obj = (JSONObject) arrayScenes.get(0);
                    JSONArray arr = obj.getJSONArray("Scenas");

                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject scena = arr.getJSONObject(i);
                        String nombre;
                        String tiempo;
                        String accion;
                        boolean activo = false;
                        boolean lunes = false;
                        boolean martes = false;
                        boolean miercoles = false;
                        boolean jueves = false;
                        boolean viernes = false;
                        boolean sabado = false;
                        boolean domingo = false;

                        nombre = scena.getString("scenename");

                        //Utils.toAscii(nombre);

                        nombre = Utils.json_Encode(nombre);

                        accion = scena.getString("Action");
                        tiempo = scena.getString("time");
                        if (scena.has("Active-Scene"))
                            activo = scena.getString("Active-Scene").equals("on");
                        if (scena.has("weekday-mon"))
                            lunes = scena.getString("weekday-mon").equals("on");
                        if (scena.has("weekday-tue"))
                            martes = scena.getString("weekday-tue").equals("on");
                        if (scena.has("weekday-wed"))
                            miercoles = scena.getString("weekday-wed").equals("on");
                        if (scena.has("weekday-thu"))
                            jueves = scena.getString("weekday-thu").equals("on");
                        if (scena.has("weekday-fri"))
                            viernes = scena.getString("weekday-fri").equals("on");
                        if (scena.has("weekday-sat"))
                            sabado = scena.getString("weekday-sat").equals("on");
                        if (scena.has("weekday-sun"))
                            domingo = scena.getString("weekday-sun").equals("on");

                        Scene sEscena = new Scene(nombre, tiempo, accion, activo, lunes, martes, miercoles, jueves, viernes, sabado, domingo);
                        if (!scenes.containsKey(sEscena.getNombre())) {
                            scenes.put(sEscena.getNombre(), sEscena);
                        }
                    }

                    numEscenas.setText(": " + scenes.size());
                    escenasTotal = getScenes(scenes);
                    IncicializarLista();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };


        MyVolley.ServerRequest(this.getContext(), ipServer, "/getScenes", Request.Method.GET,
                listener, null, null);
    }


    /*
    private void loadScenes(){
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                /*
                arrayScenes = new JSONArray(response);
                if(scenes == null)
                    scenes = new HashMap<>();
                else scenes.clear();
                JSONObject obj = (JSONObject) arrayScenes.get(0);
                JSONArray arr = obj.getJSONArray("Scenas");
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject scena = arr.getJSONObject(i);
                    String nombre;
                    String tiempo;
                    String accion;
                    boolean activo = false;
                    boolean lunes = false;
                    boolean martes = false;
                    boolean miercoles = false;
                    boolean jueves = false;
                    boolean viernes = false;
                    boolean sabado = false;
                    boolean domingo = false;
                    nombre = scena.getString("scenename");
                    accion = scena.getString("Action");
                    tiempo = scena.getString("time");
                    if (scena.has("Active-Scene"))
                        activo = scena.getString("Active-Scene").equals("on");
                    if (scena.has("weekday-mon"))
                        lunes = scena.getString("weekday-mon").equals("on");
                    if (scena.has("weekday-tue"))
                        martes = scena.getString("weekday-tue").equals("on");
                    if (scena.has("weekday-wed"))
                        miercoles = scena.getString("weekday-wed").equals("on");
                    if (scena.has("weekday-thu"))
                        jueves = scena.getString("weekday-thu").equals("on");
                    if (scena.has("weekday-fri"))
                        viernes = scena.getString("weekday-fri").equals("on");
                    if (scena.has("weekday-sat"))
                        sabado = scena.getString("weekday-sat").equals("on");
                    if (scena.has("weekday-sun"))
                        domingo = scena.getString("weekday-sun").equals("on");
                    Scene sEscena = new Scene(nombre, tiempo, accion, activo, lunes, martes, miercoles, jueves, viernes, sabado, domingo);
                    if (!scenes.containsKey(sEscena.getNombre())) {
                        scenes.put(sEscena.getNombre(), sEscena);
                    }
                }
                numEscenas.setText(": " + scenes.size());
                escenasTotal = getScenes(scenes);
                IncicializarLista();
            }
        };
        MyVolley.JsonObjectRequest(getContext(), Request.Method.GET, ipServer, "/getScenes", null,listener, null);
    }
    */

    private void scene_delete(final int pos) {
        final AlertDialog.Builder AlertDialog = new AlertDialog.Builder(getContext(), R.style.AppAlertTheme);
        String sceneName = getString(R.string.titl_delscene, ((Scene) mSceneAdapter.getItem(pos)).getNombre());

        boolean[] checkedVal = new boolean[1];
        checkedVal[0] = true;

        AlertDialog.setTitle(R.string.opt_scene);
        AlertDialog.setMessage(sceneName);

        AlertDialog.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface arg0, int arg1) {
                // do something when the OK button is clicked
                mSceneAdapter.delScene(pos);
                mSceneAdapter.notifyDataSetChanged();


                //Create and send Json from scenes
                createscenesjson();
                //Log.i(Constants.TAG, "Eliminar escena : "+pos);
            }
        });

        AlertDialog.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface arg0, int arg1) {
                // do something when the Cancel button is clicked

            }
        });
        AlertDialog.show();
    }

    public List<Scene> getScenes(HashMap<String, Scene> scenes) {
        return new ArrayList<>(scenes.values());
    }
}