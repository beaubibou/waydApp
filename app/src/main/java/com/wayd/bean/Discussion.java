package com.wayd.bean;

import android.graphics.Bitmap;

import com.wayd.busMessaging.MessageBus;

import org.apache.commons.lang3.StringEscapeUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by bibou on 11/09/2016.
 */
@SuppressWarnings("DefaultFileTemplate")
public class Discussion {


    private int idtypeactivite;

    private final int idemetteur;
    private final String titre;
    private String message;
    private Date datecreation;
    private int nbrnonlu;
    private final Bitmap photo;
    private Date dateDernierMessage;
    private final int idactivite;
    private final int type;
    public final static int STAND_ALONE = 1;
    public final static int GROUP_TALK = 2;

    private String id;


    public int getType() {
        return type;
    }

    public int getIdactivite() {
        return idactivite;
    }

    public Discussion(String message,
                      Date datecreation, String titre, int idemetteur,
                      String photostr, int nbrnonlu, int type, int idactivite, int idtypeactivite,long dateMessage) {

        switch (type) {
            case STAND_ALONE:
                this.id = "E" + idemetteur;
                break;
            case GROUP_TALK:
                this.id = "A" + idactivite;
                break;
        }

        this.message = message;
        this.titre = titre;
        this.idemetteur = idemetteur;
        this.datecreation = datecreation;
        this.photo = Outils.getPhotoFormString(photostr);
        this.nbrnonlu = nbrnonlu;
        this.type = type;
        this.idactivite = idactivite;
        this.idtypeactivite = idtypeactivite;
        dateDernierMessage=new Date(dateMessage);

    }




    public int getIdtypeactivite() {
        return idtypeactivite;
    }



    public Bitmap getPhoto() {
        return photo;
    }

    private String getMessage() {
        return message;
    }


    public String getTitre() {
        return StringEscapeUtils.unescapeJava(titre.replaceFirst(".", (titre.charAt(0) + "").toUpperCase()));
    //    return titre.replaceFirst(".", (titre.charAt(0) + "").toUpperCase());

    }

    public Date getDatecreation() {
        return datecreation;
    }

    public int getIdemetteur() {
        return idemetteur;
    }

    private int getNbrnonlu() {
        return nbrnonlu;
    }

    public String getNbrnonluStr() {
        if (nbrnonlu > 0)
            return Integer.toString(nbrnonlu);
        else return "";
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
    public String getCorpsCourtUnicode() {

        String shortcourt = StringEscapeUtils.unescapeJava(message);
        if (shortcourt.isEmpty()) return "";
        if (shortcourt.length() > 26)
            return shortcourt.substring(0, 26) + "...";
        else
            return shortcourt ;
    }

    public String getCorps() {

        return  StringEscapeUtils.unescapeJava(message);


    }

    public String getId() {
        return id;
    }

    public boolean isEgale(Discussion discussion) {

        return this.id.equals(discussion.getId());
    }

    public void updateDiscussion(Discussion vdiscussion) {
        this.message = vdiscussion.getMessage();
        this.nbrnonlu = vdiscussion.getNbrnonlu();
        this.datecreation = vdiscussion.getDatecreation();
    }


    public void update(MessageBus messagebus) {

        this.message= messagebus.getMessage();
        this.datecreation= messagebus.getDatecreation();

    }
}




