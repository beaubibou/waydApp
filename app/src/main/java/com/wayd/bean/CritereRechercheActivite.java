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
    private String motcle;
    private boolean isfiltre;
    private final int TOUTES_ACTIVITE=-1;

    public CritereRechercheActivite(boolean isfiltre, String motcle, int idtypeactivite, int rayon, double longitude, double latitude) {
        this.isfiltre = isfiltre;
        this.motcle = motcle;
        this.idtypeactivite = idtypeactivite;
        this.rayon = rayon;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public CritereRechercheActivite(boolean isfiltre, String motcle, int idtypeactivite, int rayon) {
        this.isfiltre = isfiltre;
        this.motcle = motcle;
        this.idtypeactivite = idtypeactivite;
        this.rayon = rayon;

    }

    public CritereRechercheActivite(CritereRechercheActivite filtre) {
        this.isfiltre = filtre.isfiltre;
       this.motcle = filtre.getMotcle();
        this.idtypeactivite = filtre.getIdtypeactivite();
        this.rayon = filtre.getRayon();
        this.longitude = filtre.getLongitude();
        this.latitude = filtre.getLatitude();
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


    public boolean isfiltre() {
        return isfiltre;
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

    public void setIdtypeactivite(int idtypeactivite) {
        this.idtypeactivite = idtypeactivite;
        if (idtypeactivite==TOUTES_ACTIVITE)
            this.isfiltre=false;

    }

    public void setMotcle(String motcle) {
        this.motcle = motcle;
    }

    public void setIsfiltre(boolean isfiltre) {
        this.isfiltre = isfiltre;
    }
}
