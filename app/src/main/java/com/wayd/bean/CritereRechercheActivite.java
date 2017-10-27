package com.wayd.bean;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by bibou on 03/02/2017.
 */
@SuppressWarnings("DefaultFileTemplate")
public class CritereRechercheActivite {
   private double latitude;
    private double longitude;
    private int rayon;
    private int idtypeactivite;
    private int typeUser;
    private String motcle;

    public void setIdtypeactivite(int idtypeactivite) {
        this.idtypeactivite = idtypeactivite;
    }

    private final int TOUTES_ACTIVITE=-1;
    private int commenceDans;

    public int getCommenceDans() {
        return commenceDans;
    }


    public String getCommencantDans(){

        if (commenceDans==0)
            return "Activités en cours";
        else
            return "Actitivités dans "+commenceDans/60+ " heures";



    }
    public void setCommenceDans(int commenceDans) {
        this.commenceDans = commenceDans;
    }

    public CritereRechercheActivite(String motcle, int idtypeactivite, int rayon, int commenceDans) {

        this.motcle = motcle;
        this.idtypeactivite = idtypeactivite;
        this.rayon = rayon;
        this.commenceDans=commenceDans;

    }


    public int getTypeUser() {
        return typeUser;
    }

    public void setTypeUser(int typeUser) {
        this.typeUser = typeUser;
    }

    public CritereRechercheActivite( String motcle, int idtypeactivite, int rayon, double longitude, double latitude,int commenceDans) {
        this.motcle = motcle;
        this.idtypeactivite = idtypeactivite;
        this.rayon = rayon;
        this.longitude = longitude;
        this.latitude = latitude;
        this.commenceDans=commenceDans;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getRayon() {
        return rayon;
    }

    public int getIdtypeactivite() {
        return idtypeactivite;
    }

    public String getMotcle() {
        return motcle;
    }


    public void setPosition(double latitude, double longitude) {
        this.latitude=latitude;
        this.longitude=longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatLng(LatLng position){
        this.latitude=position.latitude;
        this.longitude=position.longitude;

    }

    public void setRayon(int rayon) {
        this.rayon = rayon;
    }



    public void setMotcle(String motcle) {
        this.motcle = motcle;
    }


}
