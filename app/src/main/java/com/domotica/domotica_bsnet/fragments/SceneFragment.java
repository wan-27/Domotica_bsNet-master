package com.domotica.domotica_bsnet.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.domotica.domotica_bsnet.R;
import com.domotica.domotica_bsnet.Scenes.ActivityScene;
import com.domotica.domotica_bsnet.Scenes.ActivityShowScene;
import com.domotica.domotica_bsnet.Scenes.Scene;
import com.domotica.domotica_bsnet.Utils.Http_Access.MyVolley;
import com.domotica.domotica_bsnet.Scenes.SceneAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SceneFragment extends Fragment {
    private ListView mSceneList;
    private SceneAdapter mSceneAdapter;
    private String ipServer;
    private TextView numEscenas;
    private List<Scene> escenasTotal;
    private Button anadirEscena, reload;
    private JSONArray arrayScenes;
    private int scene_longclick;

    public SceneFragment() {
        // Required empty public constructor
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_scene, container, false);
        ActivityScene activity = (ActivityScene) getActivity();
        ipServer = activity.getIpServer();

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
                Map<String,String> params = new HashMap<String, String>();
                try {
                    params.put("newScenes",arrayScenes.get(0).toString());
                    MyVolley.ServerRequest(getContext(),ipServer,"/setScenes", Request.Method.POST,
                            null, null, params);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        setHasOptionsMenu(true);
        return root;
    }

    private void IncicializarLista() {
        // Inicializar el adaptador con la fuente de datos.
        mSceneAdapter = new SceneAdapter(getActivity(), escenasTotal);
        //Relacionando la lista con el adaptador
        mSceneList.setAdapter(mSceneAdapter);

        mSceneList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int pos, long l) {
                scene_longclick = pos;

                scene_delete();

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
                i.putExtra("spinner", currentScene.getAccion());
                i.putExtra("activo", currentScene.isActivo());
                i.putExtra("lunes", currentScene.isLunes());
                i.putExtra("martes", currentScene.isMartes());
                i.putExtra("miercoles", currentScene.isMiercoles());
                i.putExtra("jueves", currentScene.isJueves());
                i.putExtra("viernes", currentScene.isViernes());
                i.putExtra("sabado", currentScene.isSabado());
                i.putExtra("domingo", currentScene.isDomingo());
                i.putExtra("ipServer", ipServer);


                startActivity(i);
            }
        });
    }

    private void loadScenes(){
        MyVolley.ServerRequest(this.getContext(), ipServer,"/getScenes", Request.Method.GET,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        try {
                            arrayScenes = new JSONArray(response);
                            HashMap<String, Scene> scenes = new HashMap<>();
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
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },null,null);

    }

    private void scene_delete() {
        final AlertDialog.Builder AlertDialog = new AlertDialog.Builder(getContext(), R.style.AppAlertTheme);
        String sceneName = getString(R.string.titl_delscene, ((Scene) mSceneAdapter.getItem(scene_longclick)).getNombre());

        boolean[] checkedVal = new boolean[1];
        checkedVal[0]=true;

        AlertDialog.setTitle(R.string.opt_scene);
        AlertDialog.setMessage(sceneName);

        AlertDialog.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface arg0, int arg1) {
                // do something when the OK button is clicked

            }});

        AlertDialog.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface arg0, int arg1) {
                // do something when the Cancel button is clicked

            }});
        AlertDialog.show();
    }
/*
    private StringRequest prepararPeticion() {
        String mUrlString = "http://" + ipServer + "/getScenes";
        StringRequest request = new StringRequest(
                Request.Method.GET, mUrlString, new Response.Listener<String>() {
            public void onResponse(String response) {
                try {
                    JSONArray array = new JSONArray(response);
                    HashMap<String, Scene> scenes = new HashMap<>();
                    JSONObject obj = (JSONObject) array.get(0);
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


                } catch (
                        JSONException e)

                {
                    e.printStackTrace();


                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                // Anything you want
            }
        });


        // Add StringRequest to the RequestQueue
        return request;
    }
*/
    public List<Scene> getScenes(HashMap<String, Scene> scenes) {
        return new ArrayList<>(scenes.values());
    }


}
