package com.wayd.bean;

import org.apache.commons.lang3.StringEscapeUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by bibou on 30/11/2016.
 */

@SuppressWarnings("DefaultFileTemplate")
public class Notification {

    private final int idactivite;
    private final Date datecreation;
    private final int idpersonne;
    private final int iddestinataire;
    private final String message;
    private boolean lu;
    private final int idtypenotification;
    private final int idnotification;
    public static final int DONNE_AVIS =1;
    public static final int RECOIT_AVIS=2;


    public Notification(Date datecreation, String message, int idactivite, int iddestinataire, int idpersonne,boolean lu,int idtypenotification,int idnotification) {
        this.datecreation = datecreation;
        this.message = message;
        this.idactivite = idactivite;
        this.iddestinataire = iddestinataire;
        this.idpersonne = idpersonne;
        this.lu=lu;
        this. idtypenotification= idtypenotification;
        this.idnotification=idnotification;
    }

    public void setLu(boolean lu) {
        this.lu = lu;
    }

    public boolean isLu() {
        return lu;
    }

    public int getIdnotification() {
        return idnotification;
    }


    public String getMessage() {
        return message;
    }


    public int getIddestinataire() {
        return iddestinataire;
    }



    public int getIdpersonne() {
        return idpersonne;
    }



    public Date getDatecreation() {
        return datecreation;
    }


    public int getIdactivite() {
        return idactivite;
    }


    public String getDateCreationStr(){
        SimpleDateFormat formater = new SimpleDateFormat("EEE, d MMM 'à' HH:mm");
        return "Le "+formater.format(datecreation);

    }

    public int getIdtypenotification() {
        return idtypenotification;
    }


    public String getMessageUnicode() {
        return  StringEscapeUtils.unescapeJava(message);
    }
}
