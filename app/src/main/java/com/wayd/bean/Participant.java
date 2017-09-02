package com.wayd.bean;

import android.graphics.Bitmap;

/**
 * Created by bibou on 02/02/2017.
 */

@SuppressWarnings("DefaultFileTemplate")
public class Participant {
    private int id;
    private String pseudo;
    private double note;
    private Bitmap photo;

    public Participant(int id, String pseudo, int nbravis, int sexe, Double note, String photostr,String age) {

        this.photo = Outils.getPhotoFormString(photostr);
        this.note = note;
        this.pseudo = pseudo;
        this.id = id;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public double getNote() {
        return note;
    }

    public String getPseudo() {
        return pseudo;
    }

    public int getId() {
        return id;
    }

}
