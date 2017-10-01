package com.wayd.bean;


import android.graphics.Bitmap;
import android.net.Uri;

import com.google.android.gms.internal.ty;
import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Personne implements GPSTracker.positionGpsListener {

    private  String siret;
    private  String siteWeb;
    private  String telephone;
    private int rayon;
    private boolean premiereconnexion;
    /**
     * serialVersionUID - long, DOCUMENTEZ_MOI
     */
    private int id;
    private String email;
    private String login;
    private String nom;
    private String pseudo;
    private String ville;
    private boolean actif;
    private final Date datecreation;
    private String message;
    private Bitmap photo;
    private Date datenaissance;
    private int sexe;
    private Uri imageuri;
    private double latitudeGps, longitudeGps;
    private double latitude, longitude;
    private String commentaire;
    private boolean affichesexe;
    private boolean afficheage;
    private boolean admin = false;
    private boolean isGps,notification;
    private final ArrayList<PersonneChangeListener> listenerChangePersonne = new ArrayList<>();
    private int typeUser;

    public boolean isNotification() {
        return notification;
    }

    public void setNotification(boolean notification) {
        this.notification = notification;
    }

    public boolean isPremiereconnexion() {
        return premiereconnexion;
    }

    public void setPremiereconnexion(boolean premiereconnexion) {
        this.premiereconnexion = premiereconnexion;
    }

    public boolean isAdmin() {
        return admin;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    private Uri getImageuri() {
        return imageuri;
    }

    public int getRayon() {
        return rayon;
    }

    public void setRayon(int rayon) {
        this.rayon = rayon;
    }

    public Personne(int id, String login, String mdp, String nom,
                    String pseudo, String ville, boolean actif, boolean verrouille,
                    int nbrecheccnx, Date datecreation, String message, String photostr, Date datenaissance, int sexe, String commentaire,
                    boolean afficheage, boolean affichesexe, boolean premiereconnexion, int rayon, boolean admin,boolean notification,int typeUser,
                    String siteWeb,String telephone,String siret) {
        super();
        this.id = id;
        this.login = login;
        this.nom = nom;
        this.pseudo = pseudo;
        this.ville = ville;
        this.actif = actif;
        this.datecreation = datecreation;
        this.datenaissance = datenaissance;
        this.message = message;
        this.photo = Outils.getPhotoFormString(photostr);
        this.sexe = sexe;
        this.commentaire = commentaire;
        this.afficheage = afficheage;
        this.affichesexe = affichesexe;
        this.premiereconnexion = premiereconnexion;
        this.rayon = rayon;
        this.admin = admin;
        this.notification=notification;
        this.typeUser= typeUser;
        this.siteWeb=siteWeb;
        this.telephone=telephone;
        this.siret=siret;
    }

    public int getTypeUser() {
        return typeUser;
    }

    public void setTypeUser(int typeUser) {
        this.typeUser = typeUser;
    }

    public String getSiret() {
        return siret;
    }

    public void setSiret(String siret) {
        this.siret = siret;
    }

    public String getSiteWeb() {
        return siteWeb;
    }

    public void setSiteWeb(String siteWeb) {
        this.siteWeb = siteWeb;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Personne(Personne p) {
        super();
        this.id = p.id;
        this.login = p.login;
        this.nom = p.nom;
        this.pseudo = p.pseudo;
        this.ville = p.ville;
        this.actif = p.actif;
        this.datecreation = p.datecreation;
        this.datenaissance = p.datenaissance;
        this.message = p.message;
        this.photo = p.getPhoto();
        this.sexe = p.sexe;
        this.email = p.getEmail();
        this.imageuri = p.getImageuri();
        this.commentaire = p.getCommentaire();
        this.afficheage = p.isAfficheage();
        this.affichesexe = p.isAffichesexe();
        this.premiereconnexion = p.isPremiereconnexion();
        this.admin = p.admin;
        this.rayon = p.getRayon();
        this.notification=p.isNotification();
        this.typeUser=p.getTypeUser();
        this.siret=p.getSiret();
        this.telephone=p.getTelephone();
        this.siteWeb=p.getSiteWeb();


    }

    public double getLongitude() {
        return longitudeGps;
    }

    public void setPositionGps(double latitude, double longitude) {
        this.longitudeGps = longitude;
        this.latitudeGps = latitude;

    }

    public double getLatitude() {
        return latitudeGps;
    }

    public double getDistanceActivite(Activite activite) {

        LatLng encours = getPosition();
        return Outils.getDistance(activite.getLatitude(), encours.latitude, activite.getLongitude(), encours.longitude);

    }

    public LatLng getPositionLatLngGps() {
        return new LatLng(latitudeGps, longitudeGps);

    }

    private String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void Raz() {

        this.id = 0;
        this.login = "";
        this.nom = "";
        this.pseudo = "";
        this.ville = "";
        this.actif = false;
        this.message = "";
        this.photo = null;
        this.rayon = 0;


    }

    public int getSexe() {
        return sexe;
    }

    public void setSexe(int sexe) {
        this.sexe = sexe;
    }

    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPseudo() {
        return pseudo.replaceFirst(".", (pseudo.charAt(0) + "").toUpperCase());
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
        //	sendUpdatePersonne();
    }

    public void notifyPersonneChange() {
        sendUpdatePersonne();
    }

    public boolean isAfficheage() {
        return afficheage;
    }

    public void setAfficheage(boolean afficheage) {
        this.afficheage = afficheage;
    }

    public boolean isAffichesexe() {
        return affichesexe;
    }

    public void setAffichesexe(boolean affichesexe) {
        this.affichesexe = affichesexe;
    }


    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
        //	sendUpdatePersonne();
    }


    public Date getDatenaissance() {
        return datenaissance;
    }

    public void setDatenaissance(Date datenaissance) {
        this.datenaissance = datenaissance;
    }

    public String getAge() {

        if (datenaissance != null) {
            Calendar curr = Calendar.getInstance();
            Calendar birth = Calendar.getInstance();
            birth.setTime(datenaissance);
            int yeardiff = curr.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
            curr.add(Calendar.YEAR, -yeardiff);

            if (birth.after(curr)) {
                yeardiff = yeardiff - 1;
            }

            if (yeardiff < 0) return "Erreur";
            return Integer.toString(yeardiff)+" ans";
        }

        return "0";
    }




    public String getDistanceActiviteStr(Activite activite) {

        if (activite.isArchive()) return " ";

        double distance = (int) getDistanceActivite(activite);
        NumberFormat nf;
        String distanceStr;
        if (distance < 1000) {
            nf = new DecimalFormat("0.#");
            distanceStr = nf.format(distance);
            return " à " + distanceStr + "M";
        }
        if (distance >= 1000) {
            nf = new DecimalFormat("0.##");
            distanceStr = nf.format(distance / 1000);
            return " à " + distanceStr + "KM";
        }

        return "Pas de distance";

    }

    public String getSexeStr() {
        if (sexe == 0) return "Femme";
        if (sexe == 1) return "Homme";
        return "Inconnu";


    }


    public synchronized void addPersonneChangeListener(PersonneChangeListener listener) {
        listenerChangePersonne.add(listener);


    }

    public synchronized void removePersonneChangeListener(PersonneChangeListener listener) {
        listenerChangePersonne.remove(listener);

    }

    @Override
    public void loopBackChangePositionGps(LatLng latLng) {
        this.longitudeGps = latLng.longitude;
        this.latitudeGps = latLng.latitude;
    }


    public void SetPositionSaisie(LatLng latLng) {
        this.longitude = latLng.longitude;
        this.latitude = latLng.latitude;
    }

    private LatLng getPositionSaisie() {

        return new LatLng(latitude, longitude);
    }

    public void setGps(boolean gps) {
        isGps = gps;
    }

    public LatLng getPosition() {
        if (isGps)
            return getPositionLatLngGps();
        else
            return getPositionSaisie();
    }

    public interface PersonneChangeListener {
        void updatePersonne(Personne personne);
    }

    private void sendUpdatePersonne() {
        for (int i = listenerChangePersonne.size() - 1; i >= 0; i--) {
            listenerChangePersonne.get(i).updatePersonne(this);
        }
    }


}
