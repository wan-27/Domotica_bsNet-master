package com.domotica.domotica_bsnet.Scenes;

import org.json.JSONException;
import org.json.JSONObject;

public class Scene {
    private String nombre, tiempo = "00:00", accion = "0";
    private boolean activo = false, lunes = false, martes = false, miercoles = false, jueves = false, viernes = false, sabado = false, domingo = false;

    public Scene(String nombre, String tiempo, String accion, boolean activo, boolean lunes, boolean martes, boolean miercoles, boolean jueves, boolean viernes, boolean sabado, boolean domingo) {
        this.nombre = nombre;
        this.tiempo = tiempo;
        this.accion = accion;
        this.activo = activo;
        this.lunes = lunes;
        this.martes = martes;
        this.miercoles = miercoles;
        this.jueves = jueves;
        this.viernes = viernes;
        this.sabado = sabado;
        this.domingo = domingo;
    }

    public Scene(String nombre){
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTiempo() {
        return tiempo;
    }

    public void setTiempo(String tiempo) {
        this.tiempo = tiempo;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public boolean isLunes() {
        return lunes;
    }

    public void setLunes(boolean lunes) {
        this.lunes = lunes;
    }

    public boolean isMartes() {
        return martes;
    }

    public void setMartes(boolean martes) {
        this.martes = martes;
    }

    public boolean isMiercoles() {
        return miercoles;
    }

    public void setMiercoles(boolean miercoles) {
        this.miercoles = miercoles;
    }

    public boolean isJueves() {
        return jueves;
    }

    public void setJueves(boolean jueves) {
        this.jueves = jueves;
    }

    public boolean isViernes() {
        return viernes;
    }

    public void setViernes(boolean viernes) {
        this.viernes = viernes;
    }

    public boolean isSabado() {
        return sabado;
    }

    public void setSabado(boolean sabado) {
        this.sabado = sabado;
    }

    public boolean isDomingo() {
        return domingo;
    }

    public void setDomingo(boolean domingo) {
        this.domingo = domingo;
    }

    @Override
    public String toString() {
        return "Scene{" +
                "nombre='" + nombre + '\'' +
                ", tiempo='" + tiempo + '\'' +
                ", accion='" + accion + '\'' +
                ", activo=" + activo +
                ", lunes=" + lunes +
                ", martes=" + martes +
                ", miercoles=" + miercoles +
                ", jueves=" + jueves +
                ", viernes=" + viernes +
                ", sabado=" + sabado +
                ", domingo=" + domingo +
                '}';
    }

    public JSONObject toJsonObject(){
        JSONObject scene = new JSONObject();

        try {
            scene.put("scenename", getNombre());

            if(activo){
                scene.put("Active-Scene", "on");
            }

            if(lunes){
                scene.put("weekday-mon","on");
            }

            if(martes){
                scene.put("weekday-tue","on");
            }

            if(miercoles){
                scene.put("weekday-wed","on");
            }

            if(jueves){
                scene.put("weekday-thu","on");
            }

            if(viernes){
                scene.put("weekday-fri","on");
            }

            if(sabado){
                scene.put("weekday-sat","on");
            }

            if(domingo){
                scene.put("weekday-sun","on");
            }

            scene.put("time", tiempo);
            scene.put("Action", Integer.parseInt(accion));

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return scene;
    }
}
