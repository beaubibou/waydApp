package com.wayd.bean;

import android.graphics.Bitmap;

/**
 * Created by bibou on 02/04/2017.
 */

@SuppressWarnings("DefaultFileTemplate")
public class PhotoWaydeur {

    private int id;
    private Bitmap photo;


    public PhotoWaydeur(int id, String photostr) {
        super();
        this.id = id;
        this.photo=Outils.getPhotoFormString(photostr);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }
}