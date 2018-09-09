package com.domotica.domotica_bsnet.Scenes;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.domotica.domotica_bsnet.R;

import java.util.List;

import soulissclient.Constants;

public class SceneAdapter extends ArrayAdapter {

    private List<Scene> scenes;
    private boolean ctrlPercentage;

    public SceneAdapter(Context context, List<Scene> scenes, boolean ctrlPer) {
        super(context, 0, scenes);

        this.scenes = scenes;
//        Log.i(Constants.TAG, "SceneAdapter: Control Porcentage: "+ctrlPer);
        this.ctrlPercentage = ctrlPer;
    }

    public Scene getScene(int pos){
        return scenes.get(pos);
    }

    public int Size(){
        return scenes.size();
    }

    public boolean addScene(){
        String nombre = "newScene_"+Size();
        Scene sEscena = new Scene(nombre);
        return scenes.add(sEscena);
    }

    public void delScene(int index){

        scenes.remove(index);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Obtener inflater.
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ViewHolder holder;

        // ¿Ya se infló este view?
        if (null == convertView) {
            //Si no existe, entonces inflarlo con image_list_view.xml
            convertView = inflater.inflate(R.layout.list_scene, parent, false);

            holder = new ViewHolder();
            holder.nombre = (TextView) convertView.findViewById(R.id.scenename1);
            holder.estado = (TextView) convertView.findViewById(R.id.scenestate1);
            holder.hora = (TextView) convertView.findViewById(R.id.scenetime1);
            //holder.accion0 = convertView.findViewById(R.id.sceneactivity0);
            holder.accion = (TextView) convertView.findViewById(R.id.sceneactivity1);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Lead actual.
        Scene escena = (Scene) getItem(position);

        // Setup.
        holder.nombre.setText(escena.getNombre());
        holder.hora.setText(escena.getTiempo());

        String[] act_array;

        if(ctrlPercentage){
            //holder.accion0.setText(getContext().getString(R.string.action_percentage));
            act_array = getContext().getResources().getStringArray(R.array.openPercentage);
        }
        else{
            //holder.accion0.setText(getContext().getString(R.string.action_scene));
            act_array = getContext().getResources().getStringArray(R.array.scene_options);
        }

        //String[] act_array = getContext().getResources().getStringArray(R.array.scene_options);
        int nact = Integer.parseInt(escena.getAccion());
        String act = act_array[nact];
        holder.accion.setText(act);
        if (escena.isActivo()) {
            holder.estado.setText("Activo");
        } else {
            holder.estado.setText("Inactivo");
        }

        return convertView;
    }

    static class ViewHolder {
        TextView nombre;
        TextView estado;
        TextView hora;
        TextView accion;
        //TextView accion0;
    }
}
