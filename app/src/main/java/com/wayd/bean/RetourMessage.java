package com.wayd.bean;

/**
 * Created by bibou on 30/03/2017.
 */

@SuppressWarnings("DefaultFileTemplate")
public class RetourMessage {
    private long dateCreation;
    private int id;
    public final static int PLUS_SON_AMI=-1;
    public final static int NON_AUTORISE=-2;
    public static final int PLUS_INSCRIT = -3;

    public RetourMessage(long dateCreation, int id,int idemetteur) {
        super();
        this.dateCreation = dateCreation;
        this.id = id;

    }

    public long getDateCreation() {
        return dateCreation;
    }

    public int getId() {
        return id;
    }

}

