package com.wayd.bean;


import android.graphics.Bitmap;


public class ProfilNotation {

    private String pseudo;
    private Bitmap photo;
    private double note;
    private String ageStr;
    private boolean ami;
    private int idpersonne;


    public Bitmap getPhoto() {
        return photo;
    }


    public ProfilNotation(double note, int nbravis, String photostr, String ville, String pseudo, String nom, int sexe, int idpersonne,
                          int nbrami, int nbrparticipation, int nbractivite, String agestr, String commentaire, boolean ami, String titreActivite) {
        this.note = note;
        this.photo = Outils.getPhotoFormString(photostr);
        this.pseudo = pseudo;
        this.ageStr = agestr;
	    this.ami = ami;
        this.idpersonne=idpersonne;

    }

    public int getIdpersonne() {
        return idpersonne;
    }

    public double getNote() {
        return note;
    }

    public String getPseudo() {
        return pseudo.replaceFirst(".", (pseudo.charAt(0) + "").toUpperCase());
    }

    public String getAgeStr() {
        return ageStr;
    }

    public boolean isAmi() {
        return ami;
    }

}
