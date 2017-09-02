package com.wayd.bean;

import android.graphics.Bitmap;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by bibou on 25/11/2016.
 */

@SuppressWarnings("DefaultFileTemplate")
public class Message {
    private int  idactivite;
    private int idmessage;
    private final int idemetteur;
    private String corps;
    private boolean lu;
    private Date datecreation;

    private Bitmap photo;

    public int getIdactivite() {
        return idactivite;
    }

    public Message(String corps, Date datecreation, String pseudoemetteur, String nomemetteur,
                   int idemetteur, int idmessage,  boolean lu, int idactivite) {
        this.corps = corps;
        this.idemetteur = idemetteur;
        this.idmessage = idmessage;
        this.datecreation=datecreation;
        this.lu=lu;
        this.idactivite=idactivite;
    }
    public Message(String corps, Date datecreation, String pseudoemetteur, String nomemetteur, int idemetteur, int idmessage) {
        this.corps = corps;
        this.idemetteur = idemetteur;
        this.datecreation=datecreation;
        this.idmessage=idmessage;
        this.lu=false;
    }


    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }


    public String getCorps() {
        return corps;
    }

    public boolean isLu() {
        return lu;
    }

    public void setLu(boolean lu) {
        this.lu = lu;
    }

    public int getIdmessage() {
        return idmessage;
    }


    public Date getDatecreation() {
        return datecreation;
    }

    public void setDatecreation(Date datecreation) {
        this.datecreation = datecreation;
    }

    public int getIdemetteur() {
        return idemetteur;
    }



    public  String getDateCreationStr(){// Affiche l'heure si la date correspond au jour
        SimpleDateFormat formatLong = new SimpleDateFormat("dd-MM HH:mm");
        SimpleDateFormat formatCourt = new SimpleDateFormat("HH:mm:ss");

        GregorianCalendar calendrier=new GregorianCalendar();
        calendrier.setTime(datecreation);

        GregorianCalendar	now=new GregorianCalendar();

        if (calendrier.get(GregorianCalendar.DAY_OF_MONTH)==now.get(GregorianCalendar.DAY_OF_MONTH) &&
                calendrier.get(GregorianCalendar.MONTH)==now.get(GregorianCalendar.MONTH) &&
                calendrier.get(GregorianCalendar.YEAR)==now.get(GregorianCalendar.YEAR))
            return formatCourt.format(datecreation);
        else
            return formatLong.format(datecreation);

    }



}
