package com.wayd.bean;

/**
 * Created by bibou on 26/09/2016.
 */

@SuppressWarnings("DefaultFileTemplate")
public class TypeActivite {
    private boolean ischecked;
    private final String nom;
    private final int idcategorie;
    private final int id;
    public final static int ENTRAIDE=8;
    public final static int WAYDEURS_DISPO =6;
    public  final static int AUTRE=7;
    public final static int DISCUTER=5;
    public final static int JOUER=4;
    public final static int CULTURE=3;
    public  final static int SPORT=2;
    public final static int BAR_RESTO=1;

    public TypeActivite(int id, int idcategorie, String nom, boolean ischecked) {
        this.id = id;
        this.idcategorie = idcategorie;
        this.nom = nom;
        this.ischecked = ischecked;
    }


    public int getId() {
        return id;
    }



    public String getNom() {
        return nom;
    }


}

