package com.domotica.domotica_bsnet.Scenes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.domotica.domotica_bsnet.R;

import java.util.List;

public class SceneAdapter extends ArrayAdapter {

    public SceneAdapter(Context context, List<Scene> objects) {
        super(context, 0, objects);
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
        String[] act_array = getContext().getResources().getStringArray(R.array.scene_options);
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

    }
}
