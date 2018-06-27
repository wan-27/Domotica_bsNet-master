package com.domotica.domotica_bsnet.Scenes;

public class Scene {
    private String nombre, tiempo, accion;
    private boolean activo, lunes, martes, miercoles, jueves, viernes, sabado, domingo;

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
}
