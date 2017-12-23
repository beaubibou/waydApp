package com.wayd.bean;

import android.graphics.Bitmap;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.Date;



public class Ami {
    private int id;
    private String pseudo;
    private Bitmap photo;
    private final Date depuisle;
    private double note;

    public Ami(String photostr, String pseudo, String nom, String login, int id, Date depuisle,double note) {
        this.photo=Outils.getPhotoFormString(photostr);
        this.pseudo = pseudo;
        this.id = id;
        this.depuisle=depuisle;
        this.note=note;


    }

    public double getNote() {
        return note;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public String getPseudo() {

        return StringEscapeUtils.unescapeJava(pseudo.replaceFirst(".",(pseudo.charAt(0)+"").toUpperCase()));
    }

    public int getId() {
        return id;
    }

    public String getDepuisle(){
        return Outils.getStringFromDateCourte(depuisle);
    }

}
