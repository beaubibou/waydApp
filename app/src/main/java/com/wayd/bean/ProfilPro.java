package com.wayd.bean;

import android.graphics.Bitmap;

/**
 * Created by bibou on 21/10/2017.
 */

public class ProfilPro {
    int id;

    private String pseudo;
    private String adresse;
    private String siret;
    private String telephone;
    private Long datecreation;
    private int nbractivite;
    private Bitmap photo;
    private String commentaire;
    private String siteweb;


    public void setSiteweb(String siteweb) {
        this.siteweb = siteweb;
    }

    public ProfilPro(int id, String pseudo, String adresse, String siret,
                     String telephone, Long datecreation, int nbractivite,
                     String photostr, String commentaire, String siteweb) {
        super();
        this.id = id;
        this.pseudo = pseudo;
        this.adresse = adresse;
        this.siret = siret;
        this.telephone = telephone;
        this.datecreation = datecreation;
        this.nbractivite = nbractivite;
        this.photo = this.photo=Outils.getPhotoFormString(photostr);
        this.commentaire = commentaire;
        this.siteweb = siteweb;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAdresse() {

        if (adresse.isEmpty())return "Adresse: Non communiquée";
        return "Adresse: "+adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getSiret() {

        if (siret.isEmpty())return "N° SIRET: Non communiqué";
        return "N° SIRET: "+siret;
    }

    public void setSiret(String siret) {
        this.siret = siret;
    }

    public String getTelephone() {
        if (telephone.isEmpty())return "Tel: Non communiqué";
        return "Tel: "+telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Long getDatecreation() {
        return datecreation;
    }

    public void setDatecreation(Long datecreation) {
        this.datecreation = datecreation;
    }

    public int getNbractivite() {
        return nbractivite;
    }

    public void setNbractivite(int nbractivite) {
        this.nbractivite = nbractivite;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getSiteweb() {
        return siteweb;
    }
}
