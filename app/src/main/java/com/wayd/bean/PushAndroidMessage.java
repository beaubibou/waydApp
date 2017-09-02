package com.wayd.bean;

import java.util.Date;

public class PushAndroidMessage {
private long id;
private final Date datecreation;
    private String titre;
final static int REFRESH_TDB=1;
	final static int notification=2;
	public static final int NBR_MESSAGE_NONLU=3;
	public static final int NBR_ACTIVITE=4;
	public static final int NBR_NOTIFICATION=5;
	public static final int NBR_SUGGESTION=6;
	public static final int Annule_Activite=7;
	public static final int Annule_PARTICIPATION=8;
	public static final int NBR_AMI = 9;
	public static final int TDB_REFRESH=10;
	public static final int EFFACE_SUGGESTION=11;
	public static final int UPDATE_ACTIVITE=12;
	public static final int UPDATE_NOTIFICATION=13;


public PushAndroidMessage(long id, Date datecreation, String text, String titre) {
	super();
	this.id = id;
	this.datecreation = datecreation;
	this.titre = titre;
}


}
