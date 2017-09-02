package com.wayd.bean;

/**
 * Created by bibou on 13/01/2017.
 */

@SuppressWarnings("DefaultFileTemplate")
public class Preference {
    private boolean active;
    private int idTypeactivite;
    private String nom;

    public String getNom() {
        return nom;
    }

    public Preference(int idTypeactivite, String nom,boolean active) {
        this.idTypeactivite = idTypeactivite;
        this.nom=nom;
        this.active=active;
    }


    public int getIdTypeactivite() {
        return idTypeactivite;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
