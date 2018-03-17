package com.wayd.bean;


import android.graphics.Bitmap;

import org.apache.commons.lang3.StringEscapeUtils;


public class Profil {
    public static final int WAYDEUR =3;
	public static final int PRO =1;
    public static final int CARPEDIEM =4;
    private  String commentaire;
	private  int nbrami;
	private  int nbrparticipation;
	private int nbractivite;
	private int idpersonne;
	private String pseudo;
	private String ville;
	private Bitmap photo;
	private int nbravis;
	private double note;
	private int sexe;
	private String ageStr;

	public Bitmap getPhoto() {
		return photo;
	}

	public String getCommentaire() {
		return commentaire;
	}

	public Profil(double note, int nbravis, String photostr, String ville, String pseudo, String nom, int sexe, int idpersonne,
				  int nbrami, int nbrparticipation, int nbractivite, String agestr,String commentaire) {
		this.note = note;
		this.nbravis = nbravis;
		this.photo=Outils.getPhotoFormString(photostr);
		this.ville = ville;
		this.pseudo = pseudo;
		this.sexe=sexe;
		this.idpersonne=idpersonne;
		this.nbractivite = nbractivite;
		this.nbrparticipation = nbrparticipation;
		this.nbrami = nbrami;
		this.ageStr=agestr;
		this.commentaire=commentaire;
	}

	public int getSexe() {
		return sexe;
	}

	public void setSexe(int sexe) {
		this.sexe = sexe;
	}

	public int getNbractivite() {
		return nbractivite;
	}

	public int getNbrparticipation() {
		return nbrparticipation;
	}

	public int getNbrami() {
		return nbrami;
	}

	public int getIdpersonne() {
		return idpersonne;
	}

	public int getNbravis() {
		return nbravis;
	}

	public double getNote() {
		return note;
	}

	public String getPseudo() {
		return StringEscapeUtils.unescapeJava(pseudo.replaceFirst(".",(pseudo.charAt(0)+"").toUpperCase()));
	}

	public  String getSexeStr()
	{

		if (sexe==0)return "Femme";
		if (sexe==1)return "Homme";
		if (sexe==2)return "Autre";
		if (sexe==3)return "Masqué";
		return "Inconnu";
	}
	public  String getAgeStr()
	{
		return ageStr;
	}


}
