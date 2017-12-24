package com.wayd.bean;

import android.graphics.Bitmap;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Avis implements Serializable{

	@SuppressWarnings("WeakerAccess")
	private final
	int idnoter;

	@SuppressWarnings("WeakerAccess")
	private
	String titre;

	@SuppressWarnings("WeakerAccess")
	private final
	String libelle;

	@SuppressWarnings("WeakerAccess")
	private final
	Date datenotation;

	@SuppressWarnings("WeakerAccess")
	private final
	double note;

		@SuppressWarnings("WeakerAccess")
	private final
	String pseudonotateur;
	@SuppressWarnings("WeakerAccess")
	private final
	Bitmap photonotateur;
	private String   titreactivite;
	private int idpersonnenotateur;


	public Avis(double note, boolean fait, Date datenotation, String libelle,
				String titre, int idpersonnenotateur, int idpersonnenotee, int idactivite, int idnoter,
				String nomnotateur, String pseudonotateur, String photonotaeurstr, String   titreactivite) {
		this.note = note;
		this.datenotation = datenotation;
		this.libelle = libelle;
		this.titre = titre;
		this.idnoter = idnoter;
		this.pseudonotateur = pseudonotateur;
		this.photonotateur=Outils.getPhotoFormString(photonotaeurstr);
		this.titreactivite=titreactivite;
		this.idpersonnenotateur=idpersonnenotateur;

	}

    public int getIdpersonnenotateur() {
		return idpersonnenotateur;
	}

    public String getPseudonotateur() {
		return StringEscapeUtils.unescapeJava(pseudonotateur.replaceFirst(".",(pseudonotateur.charAt(0)+"").toUpperCase()));

	}
	public String getTitreactivite() {
		return StringEscapeUtils.unescapeJava(titreactivite.replaceFirst(".",(titreactivite.charAt(0)+"").toUpperCase()));
		}

    public double getNote() {
		return note;
	}

	public String getAvis() {
		return libelle;
	}

    public String getAvisCourtUnicode() {

		String shortcourt= StringEscapeUtils.unescapeJava(libelle);
		if (shortcourt.isEmpty()) return "";
		if (shortcourt.length() > 120)
			return shortcourt.substring(0,120) + "...";
		else
			return shortcourt ;
	}

    public Bitmap getPhotonotateur() {
		return photonotateur;
	}


    public String getDateNotationStr() {
		
		if (datenotation==null )return "Pas de date";
		SimpleDateFormat formater = new SimpleDateFormat("dd-MM-yyyy");
		return formater.format(datenotation);
	}

	




}
