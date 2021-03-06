package com.wayd.bean;

import android.graphics.Bitmap;

import com.application.wayd.R;
import com.google.android.gms.internal.li;

import org.apache.commons.lang3.StringEscapeUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Activite
{
    private final int typeUser;
    private final int typeAcces;
    public static int GRATUIT = 1;
    public static int PAYANT = 2;
    public static int GRATUITE_INCONNU = 0;
    private int id;

    private String titre;

    private String libelle            ;

    private int idorganisateur;

    private double latitude;

    private double longitude;

    private  double note;

    private  Bitmap photo;

    private Date dateFin;

    private boolean archive;
    private int idTypeActite;

    private  String pseudoOrganisateur;

    private boolean dejainscrit;

    public int getSexe() {
        return sexe;
    }

    public Date getDateDebut() {
        return dateDebut;
    }


    private  int sexe;

    private String ageStr;
    private boolean interet;
    private  int nbrparticipant;

    private  long finidans;

    public long getFinidans() {
        return finidans;
    }

    private int nbmaxwaydeur;

    private  String tpsrestant;
    private  String fulldescription;
    private Date dateDebut;
    private String datedebutStr;
    private String adresse;
    private int gratuit;
private String lienfacebook;
    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public int getIdTypeActite() {
        return idTypeActite;
    }

    public void setIdTypeActite(int idTypeActite) {
        this.idTypeActite = idTypeActite;
    }

    public Activite(int id, String titre, String libelle, int idorganisateur, Date datedebut, Date datefin,
                    double latitude, double longitude, String adresse, String nomorganisateur, String pseudoorganisateur,
                    String photostr, double note, boolean dejainscrit,  boolean archive, int totalavis,
                    int sexe, int nbrparticipant, String tpsrestant, String agestr, int nbmaxwaydeur, long finidans,
                    int idTypeActite,int typeUser,int typeAcces,boolean interet,String    fulldescription,int gratuit,String lienfacebook)
    {
        super();

        this.id=id;
        this.titre = titre;
        this.libelle = libelle;
        this.idorganisateur = idorganisateur;
       // this.datecreation = datecreation;
        this.dateFin=datefin;
        this.dateDebut = datedebut;
        this.note=note;
        this.latitude = latitude;
        this.longitude=longitude;
       this.adresse=adresse;
        this.pseudoOrganisateur =pseudoorganisateur;
      //  String datecreationStr = Outils.getStringWsFromDate(datecreation);
        datedebutStr = Outils.getStringWsFromDate(datedebut);
   //     datefinStr = Outils.getStringWsFromDate(datefin);
    //    this.nomorganisateur=nomorganisateur;
        this.photo=Outils.getPhotoFormString(photostr);
        this.dejainscrit=dejainscrit;
        //this.organisateur=organisateur;
        this.archive=archive;
      //  this.totalavis=totalavis;
        this.sexe=sexe;
        this.nbrparticipant=nbrparticipant;
        this.tpsrestant=tpsrestant;
        this.ageStr=agestr;
        this. nbmaxwaydeur= nbmaxwaydeur;
        this.finidans=finidans;
        this.idTypeActite=idTypeActite;
        this.typeUser=typeUser;
        this.typeAcces=typeAcces;
        this.interet=interet;
        this.fulldescription=fulldescription;
        this.gratuit=gratuit;
        this.lienfacebook= lienfacebook;
    }

    public int getGratuit() {
        return gratuit;
    }

    public String getFulldescription() {
        return fulldescription;
    }

    public boolean isInteret() {
        return interet;
    }

    public void setInteret(boolean interet) {
        this.interet = interet;
    }

    public int getNbmaxwaydeur() {
        return nbmaxwaydeur;
    }

    public String getAgeStr() {
        return ageStr;
    }

    public String getTpsrestant() {
      if (archive)
       // if (tpsrestant.toLowerCase().equals("Activité passée".toLowerCase()))
            return "Terminée";
        else
        return "Se termine dans " +tpsrestant;
    }

    public String getDatedebutStr() {
        SimpleDateFormat formater = new SimpleDateFormat("'le' EE d MMM yyyy");
        String datestr = formater.format(dateDebut);
        return datestr;
    }

   // public boolean isOrganisateur() {
   //     return organisateur;
   // }

    public boolean isOrganisator(int idpersonne){

        return idpersonne == idorganisateur;


    }
    public String getNbrparticipantStr() {

      if (iscomplete())  return "Complete";

        return "Nombre de Waydeurs: "+Integer.toString(nbrparticipant);

    }

    public int getNbrparticipant(){
        return nbrparticipant;
    }

    public String getTitre()
    {
        return titre;
    }

    public String getTitreUnicode(){

        return StringEscapeUtils.unescapeJava(titre.replaceFirst(".", (titre.charAt(0) + "").toUpperCase()));
   //  return     StringEscapeUtils.unescapeJava(titre);

    }

    public String getLibelleUnicode(){
        return  StringEscapeUtils.unescapeJava(libelle);

    }



    public  String getSexeOrganisateur()
    {
        if (sexe==0)return "Femme";
        if (sexe==1)return "Homme";
        if (sexe==2)return "Autre";
        if (sexe==3)return "Masqué";
        return "Inconnu";
    }

    public String getLibelle()
    {
        return libelle;
    }

    public int getId()
    {
        return id;
    }

    public int getIdorganisateur()
    {
        return idorganisateur;
    }

    public boolean isDejainscrit() {
        return dejainscrit;
    }

    public double getLatitude()
    {
        return latitude;
    }

    public double getLongitude()
    {
        return longitude;
    }

    public boolean isArchive() {
       return archive;
    }

    public boolean isOrganisateur(int idpersonne){
        return idorganisateur == idpersonne;
    }

    public Bitmap getPhoto() {
        return photo;
    }


    public String getPseudoOrganisateur() {
        return  StringEscapeUtils.unescapeJava(pseudoOrganisateur.replaceFirst(".",(pseudoOrganisateur.charAt(0)+"").toUpperCase()));


    }

    public void updateActivite(Activite activite){

        this.id=activite.getId();
        this.titre = activite.getTitre();
        this.libelle = activite.getLibelle();
        this.idorganisateur = activite.getIdorganisateur();
        // this.datecreation = datecreation;
        //  this.datefinr=datefin;
        this.dateDebut =activite.getDateDebut();
        this.note=note;
        this.latitude = latitude;
        this.longitude=longitude;
        //    this.adresse=adresse;
        this.pseudoOrganisateur =activite.getPseudoOrganisateur();
        //  String datecreationStr = Outils.getStringWsFromDate(datecreation);
        datedebutStr = Outils.getStringWsFromDate(this.dateDebut);
        //     datefinStr = Outils.getStringWsFromDate(datefin);
        //    this.nomorganisateur=nomorganisateur;
        this.photo=activite.getPhoto();
        this.dejainscrit=activite.isDejainscrit();
       // this.organisateur=activite.isOrganisateur();
        this.archive=archive;
        //  this.totalavis=totalavis;
        this.sexe=activite.getSexe();
        this.nbrparticipant=activite.getNbrparticipant();
        this.ageStr=activite.getAgeStr();
        this. nbmaxwaydeur=activite.getNbmaxwaydeur();
        this.finidans=activite.getFinidans();
        this.idTypeActite=activite.getIdTypeActite();
        this.fulldescription=activite.fulldescription;
        this.gratuit=activite.getGratuit();
        this.lienfacebook=activite.lienfacebook;

    }

    public String getLienfacebook() {
        return lienfacebook;
    }

    public void setLienfacebook(String lienfacebook) {
        this.lienfacebook = lienfacebook;
    }

    public int getTypeUser() {
        return typeUser;
    }

    public int getTypeAcces() {
        return typeAcces;
    }

    public double getNote() {
        return note;
    }



    public boolean iscomplete() {
        return nbmaxwaydeur == nbrparticipant;
    }

    public boolean isFromWaydeur() {
        return typeUser == Profil.WAYDEUR;
    }
    public boolean isFromPRO() {
        return typeUser == Profil.PRO;
    }

    public String getHoraire() {

        SimpleDateFormat jour = new SimpleDateFormat("dd-MM-yyyy");
        String datestrdebut = jour.format(dateDebut);
        SimpleDateFormat formatHeure = new SimpleDateFormat("HH:mm");
        String heuredebutstr = formatHeure.format(dateDebut);
        String heurefinstr = formatHeure.format(dateFin);

        return "Le " + datestrdebut + " de " + heuredebutstr + " à "
                + heurefinstr;
    }



    public void setNbmaxwaydeur(int nbmaxwaydeur) {
        this.nbmaxwaydeur = nbmaxwaydeur;
    }
}