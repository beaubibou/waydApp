package com.wayd.bean;

import android.graphics.Bitmap;

/**
 * Created by bibou on 01/01/2018.
 */

public class PhotoActivite {

    private int id;
    private int idActivite;
    private String photo;
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getIdActivite() {
        return idActivite;
    }
    public void setIdActivite(int idActivite) {
        this.idActivite = idActivite;
    }
    public String getPhoto() {
        return photo;
    }


    public Bitmap getPhotoBitmap() {
        return Outils.getPhotoFormString(photo);
    }
    public void setPhoto(String photo) {
        this.photo = photo;
    }
    public PhotoActivite(int id, int idActivite, String photo) {
        super();
        this.id = id;
        this.idActivite = idActivite;
        this.photo = photo;
    }
    public PhotoActivite() {
        super();
    }

}
