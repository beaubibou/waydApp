package com.wayd.webservice;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.ksoap2.transport.HttpsTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import com.wayd.bean.Activite;
import com.wayd.bean.Ami;
import com.wayd.bean.Avis;
import com.wayd.bean.Discussion;
import com.wayd.bean.IndicateurWayd;
import com.wayd.bean.InfoNotation;
import com.wayd.bean.Message;
import com.wayd.bean.MessageServeur;
import com.wayd.bean.Notification;
import com.wayd.bean.Outils;
import com.wayd.bean.Participant;
import com.wayd.bean.Personne;
import com.wayd.bean.Preference;
import com.wayd.bean.Profil;
import com.wayd.bean.PhotoWaydeur;
import com.wayd.bean.ProfilNotation;
import com.wayd.bean.ProfilPro;
import com.wayd.bean.RetourMessage;
import com.wayd.bean.TableauBord;
import com.wayd.bean.TypeActivite;
import com.wayd.bean.Version;

public class Wservice {
 private final static String URL = "http://192.168.1.79:8080//wayd/services/WBservices?wsdl";
   //private final static String URL = "http://wayd.fr:8080//wayd/services/WBservices?wsdl";
    private final static int timeoutws = 10000;
    private static final String NAMESPACE = "http://ws.wayd";
    private static final String SOAP_ACTION_PREFIX = "/";
    private static final String HOST = "wayd.fr";
    private static final int PORT = 8443;
    private static final String FILE = "/wayd/services/WBservices?wsdl";
    private static final boolean SECURE = false;

    public Avis getAvis(int idnoter_, int idactivite, int idnotateur, int idpersonnenotee,int idDemandeur) throws IOException,
            XmlPullParserException {
        Avis retour = null;
        String METHOD = "getAvis";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        envelope.bodyOut = request;
        request.addProperty("idnoter", idnoter_);
        request.addProperty("idactivite", idactivite);
        request.addProperty("idnotateur", idnotateur);
        request.addProperty("idpersonnenotee", idpersonnenotee);
        request.addProperty("idDemandeur", idDemandeur);
        request.addProperty("jeton", Outils.jeton);

        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);

            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);

            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }

        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.getResponse();
            if (resultSOAP == null) return null;
            int idnoter = Integer.parseInt((resultSOAP
                    .getProperty("idnoter").toString()));
            int idpersonnenotateur = Integer.parseInt((resultSOAP
                    .getProperty("idpersonnenotateur").toString()));

            String titre = (resultSOAP.getProperty("titre").toString());
            String titreactivite = (resultSOAP.getProperty("titreactivite").toString());
            String libelle = (resultSOAP.getProperty("libelle").toString());

            double note = Double.parseDouble((resultSOAP.getProperty("note")
                    .toString()));
            boolean fait = Boolean.parseBoolean((resultSOAP
                    .getProperty("fait").toString()));
            Date datenotation = Outils.getDateFromSoapObject((resultSOAP
                    .getProperty("datenotationstr")));
            String nomnotateur = (resultSOAP.getProperty("nomnotateur").toString());
            String pseudonotateur = (resultSOAP.getProperty("prenomnotateur") == null) ? "" : (resultSOAP.getProperty("prenomnotateur").toString());
            String photonotateur = (resultSOAP.getProperty("photonotateur").toString());
            retour = new Avis(note, fait, datenotation, libelle, titre,
                    idpersonnenotateur, idpersonnenotee, idactivite, idnoter, nomnotateur, pseudonotateur, photonotateur, titreactivite);
            return retour;
            //

        }
        return null;

    }

    public  MessageServeur addSuggestion(String suggestion, int idpersonne)
            throws IOException, XmlPullParserException {
        String METHOD = "addSuggestion";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        request.addProperty("suggestion", suggestion);
        request.addProperty("idpersonne", idpersonne);
        request.addProperty("jeton", Outils.jeton);

        envelope.bodyOut = request;

        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
         //   SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }

        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.getResponse();
            return getMessageServeurFromSoap(resultSOAP);
        }
        return null;
    }


    public  MessageServeur addPrbConnexion(String probleme, String email)
            throws IOException, XmlPullParserException {
        String METHOD = "addPrbConnexion";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        request.addProperty("probleme", probleme);
        request.addProperty("email", email);

        envelope.bodyOut = request;

        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
            //   SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }

        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.getResponse();
            return getMessageServeurFromSoap(resultSOAP);
        }
        return null;
    }
    public  MessageServeur addAvis(int idpersonne, int idpersonnenotee, int idactivite, String titre, String libelle,
                                         float note, boolean ami)
            throws IOException, XmlPullParserException {
        String METHOD = "addAvis";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        request.addProperty("idpersonne", idpersonne);
        request.addProperty("idpersonnenotee", idpersonnenotee);
        request.addProperty("idactivite", idactivite);
        request.addProperty("titre", titre);
        request.addProperty("libelle", libelle);
        request.addProperty("note", Float.toString(note));
        request.addProperty("demandeami", ami);
        request.addProperty("jeton", Outils.jeton);

        envelope.bodyOut = request;
        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
        //    SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }
        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.getResponse();
            return getMessageServeurFromSoap(resultSOAP);
        }
        return null;
    }

    public  MessageServeur addActivite(String titre, String libelle,
                                             int idorganisateur, int dureebalise,
                                             int idtypeactivite, double latitude, double longitude,
                                             String adresse, int nbmaxwaydeur, int dureeactivite, String jeton)
            throws IOException, XmlPullParserException {
        String METHOD = "addActivite";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        request.addProperty("titre", titre);
        request.addProperty("libelle", libelle);
        request.addProperty("idorganisateur", idorganisateur);
        request.addProperty("dureebalise", dureebalise);
        request.addProperty("idtypeactivite", idtypeactivite);
        String latitudeStr = Double.toString(latitude);
        request.addProperty("latitudestr", latitudeStr);
        String longitudeStr = Double.toString(longitude);
        request.addProperty("longitudestr", longitudeStr);
        request.addProperty("adresse", adresse);
        request.addProperty("nbmaxwaydeur", nbmaxwaydeur);
        request.addProperty("dureeactivite", dureeactivite);
        request.addProperty("jeton", jeton);

        envelope.bodyOut = request;
        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
       //     SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }
        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.getResponse();
            return getMessageServeurFromSoap(resultSOAP);
        }
        return null;
    }



    public  MessageServeur effaceActivite(int organisteur,
                                                int idactivite) throws IOException,
            XmlPullParserException {
        String METHOD = "effaceActivite";

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        request.addProperty("organisteur", organisteur);
        request.addProperty("idactivite", idactivite);
        request.addProperty("jeton", Outils.jeton);
        envelope.bodyOut = request;
        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
        //    SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }
        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.getResponse();
            return getMessageServeurFromSoap(resultSOAP);
        }
        return null;
    }

    public  ArrayList<Preference> getListPreferences(int idpersonne)
            throws IOException, XmlPullParserException {
        ArrayList<Preference> retour = new ArrayList<>();
        String METHOD = "getListPreferences";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        envelope.bodyOut = request;
        request.addProperty("iddemandeur", Outils.personneConnectee.getId());
        request.addProperty("idpersonne", idpersonne);
        request.addProperty("jeton", Outils.jeton);

        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
         //   SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }
        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.bodyIn;
            for (int f = 0; f < resultSOAP.getPropertyCount(); f++) {

                int idtypeactivite = Integer.parseInt(((SoapObject) resultSOAP
                        .getProperty(f)).getProperty("idtypeactivite").toString());

                String nom = ((SoapObject) resultSOAP.getProperty(f))
                        .getProperty("nom").toString();

                Boolean active = Boolean.parseBoolean(((SoapObject) resultSOAP
                        .getProperty(f)).getProperty("active").toString());
                Preference preference = new Preference(idtypeactivite, nom, active);
                retour.add(preference);
            }

        }

        return retour;

    }

    public  MessageServeur effaceParticipation(int idDemandeur,int idAeffacer,
                                                     int idactivite) throws IOException,
            XmlPullParserException {
        String METHOD = "effaceParticipation";

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        request.addProperty("idDemandeur", idDemandeur);
        request.addProperty("idAeffacer", idAeffacer);
        request.addProperty("idactivite", idactivite);
        request.addProperty("jeton", Outils.jeton);

        envelope.bodyOut = request;
        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
           // SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }
        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.getResponse();
            return getMessageServeurFromSoap(resultSOAP);
        }
        return null;
    }


    public  MessageServeur addParticipation(int iddemandeur, int idorganisateur,
                                                  int idactivite) throws IOException,
            XmlPullParserException {
        String METHOD = "addParticipation";

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        request.addProperty("iddemandeur", iddemandeur);
        request.addProperty("idorganisateur", idorganisateur);
        request.addProperty("idactivite", idactivite);
        request.addProperty("jeton", Outils.jeton);

        envelope.bodyOut = request;
        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
       //     SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }

        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.getResponse();
            return getMessageServeurFromSoap(resultSOAP);

        }
        return null;
    }


    public synchronized static Date getDateFromString(String dateStr) throws ParseException {
        // String lFormatTemplate = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        String lFormatTemplate = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat lDateFormat = new SimpleDateFormat(lFormatTemplate);
        return lDateFormat.parse(dateStr);

    }

    public  MessageServeur getMessageServeurFromSoap(SoapObject resultSOAP) {
        MessageServeur messageserveur;
        String message = (resultSOAP
                .getProperty("message").toString());
        boolean reponse = Boolean.parseBoolean((resultSOAP
                .getProperty("reponse").toString()));
        messageserveur = new MessageServeur(reponse, message);
        return messageserveur;
    }

    public  RetourMessage getRetourMessageFromSoap(SoapObject resultSOAP) {
        RetourMessage retourMessage;

        int idemetteur = Integer.parseInt(resultSOAP
                .getProperty("idemetteur").toString());
        int idmessage = Integer.parseInt(resultSOAP
                .getProperty("id").toString());
        long datecreation = Long.parseLong(resultSOAP
                .getProperty("datecreation").toString());

        retourMessage = new RetourMessage(datecreation, idmessage, idemetteur);
        return retourMessage;
    }


    public Version getVersion()
            throws IOException, XmlPullParserException {

        String METHOD = "getVersion";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        envelope.bodyOut = request;

        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
            //   SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }


        if (envelope.bodyIn != null) {

            SoapObject resultSOAP = (SoapObject) envelope.getResponse();

            int version = Integer.parseInt((resultSOAP
                    .getProperty("version").toString()));

            int mineur = Integer.parseInt((resultSOAP
                    .getProperty("mineur").toString()));

            int majeur = Integer.parseInt((resultSOAP
                    .getProperty("majeur").toString()));


            return new Version(version,majeur,mineur);


        }
        return null;



    }



    public Activite getActivite(int idactivite)
            throws IOException, XmlPullParserException {

        String METHOD = "getActivite";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        envelope.bodyOut = request;
        request.addProperty("idpersonne", Outils.personneConnectee.getId());
        request.addProperty("idactivite", idactivite);
        request.addProperty("jeton",Outils.jeton);
        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
         //   SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }
        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.bodyIn;
            return getActiviteFromSOAP(resultSOAP).get(0);
        }

        return null;

    }

    public Profil getFullProfil(int idpersonne)
            throws IOException, XmlPullParserException {
        String METHOD = "getFullProfil";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        envelope.bodyOut = request;
        request.addProperty("iddemandeur", Outils.personneConnectee.getId());
        request.addProperty("idpersonne", idpersonne);
        request.addProperty("jeton", Outils.jeton);
        // HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
         //   SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }
        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.bodyIn;
            return getProfilFromSOAP(resultSOAP).get(0);
        }
        return null;

    }
    public ProfilPro getFullProfilPro(int idpersonne)
            throws IOException, XmlPullParserException {
        String METHOD = "getFullProfilPro";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        envelope.bodyOut = request;
        request.addProperty("iddemandeur", Outils.personneConnectee.getId());
        request.addProperty("idpersonne", idpersonne);
        request.addProperty("jeton", Outils.jeton);
        // HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
            //   SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }
        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.bodyIn;
            return getProfilProFromSOAP(resultSOAP).get(0);
        }
        return null;

    }

    public  ProfilNotation getProfilNotation(int notateur, int idpersonne, int idactivite)
            throws IOException, XmlPullParserException {

        String METHOD = "getProfilNotation";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        envelope.bodyOut = request;
        request.addProperty("notateur", notateur);
        request.addProperty("idpersonne", idpersonne);
        request.addProperty("idactivite", idactivite);

        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
        //    SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }
        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.bodyIn;
            return getProfilNotationFromSOAP(resultSOAP).get(0);
        }
        return null;
    }



    public  ArrayList<Discussion> getListDiscussion(int idpersonne)
            throws IOException, XmlPullParserException {
        ArrayList<Discussion> retour = new ArrayList<>();
        String METHOD = "getListDiscussion";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        envelope.bodyOut = request;
        request.addProperty("iddemandeur", Outils.personneConnectee.getId());
        request.addProperty("idpersonne", idpersonne);
        request.addProperty("jeton", Outils.jeton);
        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
         //   SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }
        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.bodyIn;
            return getDiscussionFromSOAP(resultSOAP);
        }
        return retour;
    }

    public  ArrayList<Message> getDiscussion(int iddestinataire, int idemetteur)
            throws IOException, XmlPullParserException {
        ArrayList<Message> retour = new ArrayList<>();
        String METHOD = "getDiscussion";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        envelope.bodyOut = request;
        request.addProperty("iddestinataire", iddestinataire);
        request.addProperty("idemetteur", idemetteur);
        request.addProperty("jeton", Outils.jeton);
        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
           // SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }
        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.bodyIn;
            return getMessageFromSOAP(resultSOAP);
        }
        return retour;
    }

    public  ArrayList<Message> getDiscussionByAct(int iddestinataire, int idactivite)
            throws IOException, XmlPullParserException {
        ArrayList<Message> retour = new ArrayList<>();
        String METHOD = "getDiscussionByAct";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        envelope.bodyOut = request;
        request.addProperty("iddestinataire", iddestinataire);
        request.addProperty("idactivite", idactivite);
        request.addProperty("jeton", Outils.jeton);
        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
          //  SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }
        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.bodyIn;
            return getMessageFromSOAP(resultSOAP);
        }
        return retour;
    }

    public  ArrayList<Notification> getListNotification(int idpersonne)
            throws IOException, XmlPullParserException {
        ArrayList<Notification> retour = new ArrayList<>();
        String METHOD = "getListNotification";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        envelope.bodyOut = request;
        request.addProperty("iddemandeur", Outils.personneConnectee.getId());
        request.addProperty("idpersonne", idpersonne);
        request.addProperty("jeton", Outils.jeton);
        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
         //   SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }
        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.bodyIn;
            return getNotificationFromSOAP(resultSOAP);
        }
        return retour;
    }


    public  ArrayList<Message> getListMessageAfter(int idpersonne, int idxmessage)
            throws IOException, XmlPullParserException {
        ArrayList<Message> retour = new ArrayList<>();
        String METHOD = "getListMessageAfter";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        envelope.bodyOut = request;
        request.addProperty("idpersonne", idpersonne);
        request.addProperty("idxmessage", idxmessage);
        request.addProperty("jeton", Outils.jeton);

        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
      //      SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }
        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.bodyIn;
            return getMessageFromSOAP(resultSOAP);
        }
        return retour;
    }



    public  ArrayList<Notification> getListNotificationAfter(int idpersonne, int idxmessage)
            throws IOException, XmlPullParserException {
        ArrayList<Notification> retour = new ArrayList<>();
        String METHOD = "getListNotificationAfter";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        envelope.bodyOut = request;
        request.addProperty("idpersonne", idpersonne);
        request.addProperty("idxmessage", idxmessage);
        request.addProperty("jeton", Outils.jeton);
        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
          //  SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }
        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.bodyIn;
            return getNotificationFromSOAP(resultSOAP);
        }
        return retour;
    }


    public  ArrayList<Message> getListMessageAfterByAct(int idpersonne, int idxmessage, int idactivite)
            throws IOException, XmlPullParserException {
        ArrayList<Message> retour = new ArrayList<>();
        String METHOD = "getListMessageAfterByAct";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        envelope.bodyOut = request;
        request.addProperty("idpersonne", idpersonne);
        request.addProperty("idxmessage", idxmessage);
        request.addProperty("idactivite", idactivite);
        request.addProperty("jeton", Outils.jeton);

        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
         //   SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }
        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.bodyIn;
            return getMessageFromSOAP(resultSOAP);
        }
        return retour;
    }


    private  ArrayList<InfoNotation> getInfoNotationFromSOAP(SoapObject resultSOAP) {
        ArrayList<InfoNotation> retour = new ArrayList<>();
        for (int f = 0; f < resultSOAP.getPropertyCount(); f++) {
            int totalavis = Integer.parseInt(((SoapObject) resultSOAP
                    .getProperty(f)).getProperty("totalavis").toString());

            double moyenne = Double.parseDouble(((SoapObject) resultSOAP
                    .getProperty(f)).getProperty("moyenne").toString());

            InfoNotation infonnotation = new InfoNotation(totalavis, moyenne);
            retour.add(infonnotation);
        }
        return retour;
    }

    private  ArrayList<Profil> getProfilFromSOAP(SoapObject resultSOAP) {
        ArrayList<Profil> retour = new ArrayList<>();
        for (int f = 0; f < resultSOAP.getPropertyCount(); f++) {


           // String nom = ((SoapObject) resultSOAP.getProperty(f))
              //      .getProperty("nom").toString();

            String nom = ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("nom") == null ? "" : ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("nom").toString();

            String pseudo = ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("prenom") == null ? "" : ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("prenom").toString();

            String photostr = ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("photostr") == null ? "" : ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("photostr").toString();


            String ville = ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("ville") == null ? "" : ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("ville").toString();

            double note = Double.parseDouble(((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("note").toString());

            int nbravis = Integer.parseInt(((SoapObject) resultSOAP
                    .getProperty(f)).getProperty("nbravis").toString());

            int sexe = Integer.parseInt(((SoapObject) resultSOAP
                    .getProperty(f)).getProperty("sexe").toString());

            String commentaire = ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("commentaire") == null ? "" : ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("commentaire").toString();

            int idpersonne = Integer.parseInt(((SoapObject) resultSOAP
                    .getProperty(f)).getProperty("id").toString());

            int nbrami = Integer.parseInt(((SoapObject) resultSOAP
                    .getProperty(f)).getProperty("nbrami").toString());
            int nbrparticipation = Integer.parseInt(((SoapObject) resultSOAP
                    .getProperty(f)).getProperty("nbrparticipation").toString());
            int nbractivite = Integer.parseInt(((SoapObject) resultSOAP
                    .getProperty(f)).getProperty("nbractivite").toString());
            String agestr = ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("age") == null ? "" : ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("age").toString();


            Log.d("Asyntache","l"+commentaire+"l");
            Profil profil = new Profil(note, nbravis, photostr, ville, pseudo, nom, sexe, idpersonne, nbrami, nbrparticipation, nbractivite, agestr, commentaire);
            retour.add(profil);
        }
        return retour;

    }

    private  ArrayList<ProfilPro> getProfilProFromSOAP(SoapObject resultSOAP) {
        ArrayList<ProfilPro> retour = new ArrayList<>();
        for (int f = 0; f < resultSOAP.getPropertyCount(); f++) {


            // String nom = ((SoapObject) resultSOAP.getProperty(f))
            //      .getProperty("nom").toString();
            int idpersonne = Integer.parseInt(((SoapObject) resultSOAP
                    .getProperty(f)).getProperty("id").toString());

            String pseudo = ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("prenom") == null ? "" : ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("prenom").toString();

            String adresse = ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("adresse") == null ? "" : ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("adresse").toString();

            String siret = ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("siret") == null ? "" : ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("siret").toString();

            String telephone = ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("telephone") == null ? "" : ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("telephone").toString();

            long datecreation =Long.parseLong(((SoapObject) resultSOAP
                    .getProperty(f)).getProperty("datecreation").toString());

            int nbractivite = Integer.parseInt(((SoapObject) resultSOAP
                    .getProperty(f)).getProperty("nbractivite").toString());

            String photostr = ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("photostr") == null ? "" : ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("photostr").toString();


            String commentaire = ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("commentaire") == null ? "" : ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("commentaire").toString();

            String siteWeb = ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("siteweb") == null ? "" : ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("siteweb").toString();

            ProfilPro profil = new ProfilPro( idpersonne,  pseudo,  adresse,  siret,
                     telephone,  datecreation,  nbractivite,
             photostr,  commentaire, siteWeb);
            retour.add(profil);
        }
        return retour;

    }


    private  ArrayList<ProfilNotation> getProfilNotationFromSOAP(SoapObject resultSOAP) {
        ArrayList<ProfilNotation> retour = new ArrayList<>();
        for (int f = 0; f < resultSOAP.getPropertyCount(); f++) {


            String nom = ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("nom").toString();

            String pseudo = ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("prenom") == null ? "" : ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("prenom").toString();

            String photostr = ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("photostr") == null ? "" : ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("photostr").toString();


            String ville = ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("ville") == null ? "" : ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("ville").toString();

            double note = Double.parseDouble(((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("note").toString());

            int nbravis = Integer.parseInt(((SoapObject) resultSOAP
                    .getProperty(f)).getProperty("nbravis").toString());

            int sexe = Integer.parseInt(((SoapObject) resultSOAP
                    .getProperty(f)).getProperty("sexe").toString());

            String commentaire = ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("commentaire") == null ? "" : ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("commentaire").toString();

            int idpersonne = Integer.parseInt(((SoapObject) resultSOAP
                    .getProperty(f)).getProperty("id").toString());

            int nbrami = Integer.parseInt(((SoapObject) resultSOAP
                    .getProperty(f)).getProperty("nbrami").toString());
            int nbrparticipation = Integer.parseInt(((SoapObject) resultSOAP
                    .getProperty(f)).getProperty("nbrparticipation").toString());
            int nbractivite = Integer.parseInt(((SoapObject) resultSOAP
                    .getProperty(f)).getProperty("nbractivite").toString());
            String agestr = ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("age") == null ? "" : ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("age").toString();

            String titreActivite = ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("titreActivite") == null ? "" : ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("titreActivite").toString();

            boolean isAmi = Boolean.parseBoolean(((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("ami").toString());
            ProfilNotation profil = new ProfilNotation(note, nbravis, photostr, ville, pseudo, nom, sexe, idpersonne, nbrami, nbrparticipation, nbractivite, agestr, commentaire, isAmi, titreActivite);
            retour.add(profil);
        }
        return retour;

    }

    private  ArrayList<Activite> getActiviteFromSOAP(SoapObject resultSOAP) {
        ArrayList<Activite> retour = new ArrayList<>();

        for (int f = 0; f < resultSOAP.getPropertyCount(); f++) {

            // Gere le cas ou l'activite n'a pas été trouvé
            if (resultSOAP.getProperty(f) == null) {
                retour.add(null);
                return retour;
            }

            int id = Integer.parseInt(((SoapObject) resultSOAP
                    .getProperty(f)).getProperty("id").toString());

            String titre = ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("titre").toString();

            String libelle = ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("libelle").toString();

            int idorganisateur = Integer.parseInt(((SoapObject) resultSOAP
                    .getProperty(f)).getProperty("idorganisateur").toString());

            int sexe = Integer.parseInt(((SoapObject) resultSOAP
                    .getProperty(f)).getProperty("sexe").toString());

            int nbrparticipant = Integer.parseInt(((SoapObject) resultSOAP
                    .getProperty(f)).getProperty("nbrparticipant").toString());


            Date datedebut = Outils
                    .getDateFromSoapObject(((SoapObject) resultSOAP
                            .getProperty(f)).getProperty("datedebutStr"));

            Date datefin = Outils
                    .getDateFromSoapObject(((SoapObject) resultSOAP
                            .getProperty(f))
                            .getProperty("datefinStr"));


            double latitude = Double.parseDouble(((SoapObject) resultSOAP
                    .getProperty(f)).getProperty("latitude").toString());

            double longitude = Double.parseDouble(((SoapObject) resultSOAP
                    .getProperty(f)).getProperty("longitude").toString());

            String adresse = ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("adresse").toString();

            String photostr = ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("photo") == null ? "" : ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("photo").toString();

            String nomorganisateur = ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("nomorganisateur") == null ? "" : ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("nomorganisateur").toString();
            String tpsrestant = ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("tpsrestant") == null ? "" : ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("tpsrestant").toString();


            String pseudoorganisateur = ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("prenomorganisateur") == null ? "" : ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("prenomorganisateur").toString();

            double note = Double.parseDouble(((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("note").toString());
            boolean dejainscrit = Boolean.parseBoolean(((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("dejainscrit").toString());
            boolean organisateur = Boolean.parseBoolean(((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("organisateur").toString());

            boolean archive = Boolean.parseBoolean(((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("archive").toString());
            int totalavis = Integer.parseInt(((SoapObject) resultSOAP
                    .getProperty(f)).getProperty("totalavis").toString());

            long finidans = Long.parseLong(((SoapObject) resultSOAP
                    .getProperty(f)).getProperty("finidans").toString());

            int nbmaxwaydeur = Integer.parseInt(((SoapObject) resultSOAP
                    .getProperty(f)).getProperty("nbmaxwaydeur").toString());

            int typeactivite = Integer.parseInt(((SoapObject) resultSOAP
                    .getProperty(f)).getProperty("typeactivite").toString());

            String agestr = ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("age") == null ? "" : ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("age").toString();

            int typeUser=Integer.parseInt(((SoapObject) resultSOAP
                    .getProperty(f)).getProperty("typeUser").toString());

            int typeAcces=Integer.parseInt(((SoapObject) resultSOAP
                    .getProperty(f)).getProperty("typeAcces").toString());


            Activite activite = new Activite(id, titre, libelle, idorganisateur, datedebut, datefin,
                    latitude, longitude, adresse, nomorganisateur, pseudoorganisateur,
                    photostr, note, dejainscrit, organisateur, archive, totalavis,
                    sexe, nbrparticipant, tpsrestant, agestr, nbmaxwaydeur, finidans,typeactivite,typeUser,typeAcces);

            Log.d("idtype ativite","********************"+typeactivite);
            retour.add(activite);
        }


        return retour;

    }



    private  ArrayList<Discussion> getDiscussionFromSOAP(SoapObject resultSOAP) {
        ArrayList<Discussion> retour = new ArrayList<>();
        for (int f = 0; f < resultSOAP.getPropertyCount(); f++) {


            //      int idmessage = Integer.parseInt(((SoapObject) resultSOAP
            //     .getProperty(f)).getProperty("idmessage").toString());

            int idemetteur = Integer.parseInt(((SoapObject) resultSOAP
                    .getProperty(f)).getProperty("idemetteur").toString());

            int nbrnonlu = Integer.parseInt(((SoapObject) resultSOAP
                    .getProperty(f)).getProperty("nbnonlu").toString());

            // String nomemetteur = ((SoapObject) resultSOAP.getProperty(f))
            //     .getProperty("nomemetteur").toString();


            String titre = ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("titre") == null ? "" : ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("titre").toString();

            Date datecreation = Outils
                    .getDateFromSoapObject(((SoapObject) resultSOAP
                            .getProperty(f))
                            .getProperty("datecreationstr"));

            String message = ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("message").toString();


            String photo = ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("photo").toString();


            int type = Integer.parseInt(((SoapObject) resultSOAP
                    .getProperty(f)).getProperty("type").toString());
            int idactivite = Integer.parseInt(((SoapObject) resultSOAP
                    .getProperty(f)).getProperty("idactivite").toString());

            int idtypeactivite = Integer.parseInt(((SoapObject) resultSOAP
                    .getProperty(f)).getProperty("idtypeactivite").toString());

            long dateMessage = Long.parseLong(((SoapObject) resultSOAP
                    .getProperty(f)).getProperty("dateMessage").toString());

            Discussion discussion = new Discussion(message,
                    datecreation, titre, idemetteur, photo, nbrnonlu, type, idactivite, idtypeactivite, dateMessage);
            retour.add(discussion);
        }
        return retour;

    }

    private  ArrayList<Message> getMessageFromSOAP(SoapObject resultSOAP) {
        ArrayList<Message> retour = new ArrayList<>();
        for (int f = 0; f < resultSOAP.getPropertyCount(); f++) {

            int idmessage = Integer.parseInt(((SoapObject) resultSOAP
                    .getProperty(f)).getProperty("idmessage").toString());

            int idactivite = Integer.parseInt(((SoapObject) resultSOAP
                    .getProperty(f)).getProperty("idactivite").toString());

            int idemetteur = Integer.parseInt(((SoapObject) resultSOAP
                    .getProperty(f)).getProperty("idemetteur").toString());

            String nomemetteur = ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("nomemetteur") == null ? "" : ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("nomemetteur").toString();

            String pseudoemetteur = ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("prenomemetteur") == null ? "" : ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("prenomemetteur").toString();

            Date datecreation = Outils
                    .getDateFromSoapObject(((SoapObject) resultSOAP
                            .getProperty(f))
                            .getProperty("datecreationstr"));

            String corps = ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("corps").toString();

            boolean lu = Boolean.parseBoolean(((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("lu").toString());

            Message message = new Message(corps,
                    datecreation, pseudoemetteur, nomemetteur, idemetteur, idmessage, lu, idactivite);
            retour.add(message);
        }
        return retour;

    }

    private  ArrayList<Notification> getNotificationFromSOAP(SoapObject resultSOAP) {
        ArrayList<Notification> retour = new ArrayList<>();
        for (int f = 0; f < resultSOAP.getPropertyCount(); f++) {

            int idactivite = Integer.parseInt(((SoapObject) resultSOAP
                    .getProperty(f)).getProperty("idactivite").toString());

            int iddestinataire = Integer.parseInt(((SoapObject) resultSOAP
                    .getProperty(f)).getProperty("iddestinataire").toString());
            int idpersonneconcernee = Integer.parseInt(((SoapObject) resultSOAP
                    .getProperty(f)).getProperty("idpersonne").toString());

            int idtypenotification = Integer.parseInt(((SoapObject) resultSOAP
                    .getProperty(f)).getProperty("idtype").toString());

            int idnotification = Integer.parseInt(((SoapObject) resultSOAP
                    .getProperty(f)).getProperty("idnotification").toString());

            String message = ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("message").toString();

            Date datecreation = Outils
                    .getDateFromSoapObject(((SoapObject) resultSOAP
                            .getProperty(f))
                            .getProperty("d_creationstr"));

            boolean lu = Boolean.parseBoolean(((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("lu").toString());

            Notification notification = new Notification(datecreation, message, idactivite, iddestinataire, idpersonneconcernee, lu, idtypenotification, idnotification);
            retour.add(notification);
        }
        return retour;
    }

    public  ArrayList<Activite> getMesActiviteEncours(int idpersonne)
            throws IOException, XmlPullParserException {
        ArrayList<Activite> retour = new ArrayList<>();
        String METHOD = "getMesActiviteEncours";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        envelope.bodyOut = request;
        request.addProperty("iddemandeur", Outils.personneConnectee.getId());
        request.addProperty("idpersonne", idpersonne);
        request.addProperty("jeton", Outils.jeton);
        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
         //   SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }
        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.bodyIn;
            return getActiviteFromSOAP(resultSOAP);
        }
        return retour;
    }

    public ArrayList<Activite> getMesActiviteArchive(int idpersonne)
            throws IOException, XmlPullParserException {
        ArrayList<Activite> retour = new ArrayList<>();
        String METHOD = "getMesActiviteArchive";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        envelope.bodyOut = request;
        request.addProperty("iddemandeur", Outils.personneConnectee.getId());
        request.addProperty("idpersonne", idpersonne);
        request.addProperty("jeton", Outils.jeton);
        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
        //    SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }
        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.bodyIn;
            return getActiviteFromSOAP(resultSOAP);
        }
        return retour;

    }

    public  ArrayList<Activite> getListActivitePref(int idpersonne)
            throws IOException, XmlPullParserException {
        ArrayList<Activite> retour = new ArrayList<>();
        String METHOD = "getListActivitePref";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        envelope.bodyOut = request;
        request.addProperty("iddemandeur", Outils.personneConnectee.getId());
        request.addProperty("idpersonne", idpersonne);
        request.addProperty("jeton", Outils.jeton);

        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
         //   SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }

        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.bodyIn;
            return getActiviteFromSOAP(resultSOAP);
        }
        return retour;
    }

    public ArrayList<Activite> getListActiviteAvenir(double latitude,
                                                            double longitude, int rayon, int idtypeactivite, String motcle, int commencedans)
            throws IOException, XmlPullParserException {
        ArrayList<Activite> retour = new ArrayList<>();
        String METHOD = "getListActiviteAvenir";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        envelope.bodyOut = request;
        request.addProperty("ipdersonne", Outils.personneConnectee.getId());
        request.addProperty("latitudestr", String.valueOf(latitude));
        request.addProperty("longitudestr", String.valueOf(longitude));
        request.addProperty("rayon", rayon);
        request.addProperty("idtypeactivite", idtypeactivite);
        request.addProperty("motcle", motcle);
        request.addProperty("commencedans", commencedans);
        request.addProperty("jeton", Outils.jeton);

        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
       //     SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }
        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.bodyIn;
            return getActiviteFromSOAP(resultSOAP);
        }
        return retour;
    }

    public  ArrayList<Activite> getListActiviteAvenirNocritere(double latitude,
                                                                     double longitude, int rayon, String motcle, int commencedans)
            throws IOException, XmlPullParserException {
        ArrayList<Activite> retour = new ArrayList<>();
        String METHOD = "getListActiviteAvenirNocritere";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        envelope.bodyOut = request;
        request.addProperty("idpersonne", Outils.personneConnectee.getId());
        request.addProperty("latitudestr", String.valueOf(latitude));
        request.addProperty("longitudestr", String.valueOf(longitude));
        request.addProperty("rayon", rayon);
        request.addProperty("motcle", motcle);
        request.addProperty("commencedans", commencedans);
        request.addProperty("jeton", Outils.jeton);


        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
         //   SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }
        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.bodyIn;
            return getActiviteFromSOAP(resultSOAP);
        }
        return retour;
    }

    public  ArrayList<Activite> getActivites(Double malatitude,
                                            Double malongitude, int rayonmetre, int idtypeactivite,
                                            String motcle, int typeUser, int commenceDans)

            throws IOException, XmlPullParserException {
        ArrayList<Activite> retour = new ArrayList<>();
        String METHOD = "getActivites";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        envelope.bodyOut = request;
        request.addProperty("idPersonne", Outils.personneConnectee.getId());
        request.addProperty("latitudestr", String.valueOf(malatitude));
        request.addProperty("longitudestr", String.valueOf(malongitude));
        request.addProperty("rayonmetre", rayonmetre);
        request.addProperty("typeactivite", idtypeactivite);
        request.addProperty("motcle", motcle);
        request.addProperty("typeUser", typeUser);
        request.addProperty("commenceDans", commenceDans);
        request.addProperty("jeton", Outils.jeton);


        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
            //   SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }
        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.bodyIn;
            return getActiviteFromSOAP(resultSOAP);
        }
        return retour;
    }


    public  ArrayList<Ami> getListAmi(int idpersonne_)
            throws IOException, XmlPullParserException {
        ArrayList<Ami> retour = new ArrayList<>();
        String METHOD = "getListAmi";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        envelope.bodyOut = request;
        request.addProperty("iddemandeur", Outils.personneConnectee.getId());
        request.addProperty("idpersonne", idpersonne_);
        request.addProperty("jeton", Outils.jeton);

        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
         //   SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }
        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.bodyIn;
            for (int f = 0; f < resultSOAP.getPropertyCount(); f++) {

                int idpersonne = Integer.parseInt(((SoapObject) resultSOAP
                        .getProperty(f)).getProperty("id").toString());
                String photostr = ((SoapObject) resultSOAP.getProperty(f))
                        .getProperty("photostr") == null ? "" : ((SoapObject) resultSOAP.getProperty(f))
                        .getProperty("photostr").toString();

                String nom = ((SoapObject) resultSOAP.getProperty(f))
                        .getProperty("nom").toString();


                String pseudo = ((SoapObject) resultSOAP.getProperty(f))
                        .getProperty("prenom") == null ? "" : ((SoapObject) resultSOAP.getProperty(f))
                        .getProperty("prenom").toString();

                Date depuisle = Outils
                        .getDateFromSoapObject(((SoapObject) resultSOAP
                                .getProperty(f))
                                .getProperty("depuisle"));


                double note = Double.parseDouble(((SoapObject) resultSOAP.getProperty(f))
                        .getProperty("note").toString());

                Ami ami = new Ami(photostr, pseudo, nom, "", idpersonne, depuisle, note);
                retour.add(ami);

            }

        }

        return retour;

    }

    public  ArrayList<Avis> getListAvis(int idpersonnenotee_)
            throws IOException, XmlPullParserException {
        ArrayList<Avis> retour = new ArrayList<>();
        String METHOD = "getListAvis";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        envelope.bodyOut = request;
        request.addProperty("iddemandeur", Outils.personneConnectee.getId());
        request.addProperty("idpersonnenotee", idpersonnenotee_);
        request.addProperty("jeton", Outils.jeton);

        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
          //  SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }
        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.bodyIn;
            for (int f = 0; f < resultSOAP.getPropertyCount(); f++) {

                int idnoter = Integer.parseInt((((SoapObject) resultSOAP
                        .getProperty(f)).getProperty("idnoter").toString()));


                int idactivite = Integer.parseInt((((SoapObject) resultSOAP
                        .getProperty(f)).getProperty("idactivite").toString()));

                int idpersonnenotee = Integer.parseInt((((SoapObject) resultSOAP
                        .getProperty(f)).getProperty("idpersonnenotee").toString()));

                int idpersonnenotateur = Integer.parseInt((((SoapObject) resultSOAP
                        .getProperty(f)).getProperty("idpersonnenotateur").toString()));
                //String titre = ((((SoapObject) resultSOAP
               //         .getProperty(f)).getProperty("titre").toString()));

            //    String libelle = ((((SoapObject) resultSOAP
                   //     .getProperty(f)).getProperty("libelle").toString()));

                String libelle = ((SoapObject) resultSOAP.getProperty(f))
                        .getProperty("libelle") == null ? "" : ((SoapObject) resultSOAP.getProperty(f))
                        .getProperty("libelle").toString();

                String titre = ((SoapObject) resultSOAP.getProperty(f))
                        .getProperty("titre") == null ? "" : ((SoapObject) resultSOAP.getProperty(f))
                        .getProperty("titre").toString();




                Date datenotation = Outils.getDateFromSoapObject((((SoapObject) resultSOAP
                        .getProperty(f)).getProperty("datenotationstr")));
                Double note = Double.parseDouble((((SoapObject) resultSOAP.getProperty(f)).getProperty("note")
                        .toString()));
                boolean fait = true;

                String nomnotateur = ((((SoapObject) resultSOAP
                        .getProperty(f)).getProperty("nomnotateur").toString()));

                String pseudonotateur = ((SoapObject) resultSOAP.getProperty(f))
                        .getProperty("prenomnotateur") == null ? "" : ((SoapObject) resultSOAP.getProperty(f))
                        .getProperty("prenomnotateur").toString();

                String titreactivite = ((((SoapObject) resultSOAP
                        .getProperty(f)).getProperty("titreactivite").toString()));


                String photonotateur = ((SoapObject) resultSOAP.getProperty(f))
                        .getProperty("photonotateur") == null ? "" : ((SoapObject) resultSOAP.getProperty(f))
                        .getProperty("photonotateur").toString();


                Avis tmp = new Avis(note, fait, datenotation, libelle, titre,
                        idpersonnenotateur, idpersonnenotee, idactivite, idnoter, nomnotateur, pseudonotateur, photonotateur, titreactivite);
                retour.add(tmp);

            }

        }

        return retour;

    }


    public  ArrayList<Participant> getListParticipants(int idactivite)
            throws IOException, XmlPullParserException {
        ArrayList<Participant> retour = new ArrayList<>();
        String METHOD = "getListParticipant";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        envelope.bodyOut = request;
        request.addProperty("iddemandeur", Outils.personneConnectee.getId());
        request.addProperty("idactivite", idactivite);
        request.addProperty("jeton", Outils.jeton);

        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
        //    SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }
        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.bodyIn;
            for (int f = 0; f < resultSOAP.getPropertyCount(); f++) {

                int id = Integer.parseInt((((SoapObject) resultSOAP
                        .getProperty(f)).getProperty("id").toString()));

                String pseudo = ((((SoapObject) resultSOAP
                        .getProperty(f)).getProperty("pseudo").toString()));

                int nbravis = Integer.parseInt((((SoapObject) resultSOAP
                        .getProperty(f)).getProperty("nbravis").toString()));


                int sexe = Integer.parseInt((((SoapObject) resultSOAP
                        .getProperty(f)).getProperty("sexe").toString()));

                Double note = Double.parseDouble((((SoapObject) resultSOAP.getProperty(f)).getProperty("note")
                        .toString()));

                String age = ((((SoapObject) resultSOAP
                        .getProperty(f)).getProperty("age").toString()));


                String photostr = ((SoapObject) resultSOAP.getProperty(f))
                        .getProperty("photostr") == null ? "" : ((SoapObject) resultSOAP.getProperty(f))
                        .getProperty("photostr").toString();


                Participant participant = new Participant(id, pseudo, nbravis, sexe, note, photostr, age);

                retour.add(participant);

            }

        }

        return retour;

    }

    public  ArrayList<TypeActivite> getListTypeActivite()
            throws IOException, XmlPullParserException {
        ArrayList<TypeActivite> retour = new ArrayList<>();
        String METHOD = "getListTypeActivite";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.bodyOut = new SoapObject(NAMESPACE, METHOD);

        //  HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
        //  String HOST = "wayd.fr";
        //  int PORT = 8443;
        //  String FILE = "/waydes/services/WsTest?wsdl";
        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
         //   SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }
        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.bodyIn;
            for (int f = 0; f < resultSOAP.getPropertyCount(); f++) {

                int id = Integer.parseInt(((SoapObject) resultSOAP
                        .getProperty(f)).getProperty("id").toString());
                int idcategorie = Integer.parseInt(((SoapObject) resultSOAP
                        .getProperty(f)).getProperty("idcategorie").toString());

                String nom = ((SoapObject) resultSOAP.getProperty(f))
                        .getProperty("nom").toString();

                int typeUser = Integer.parseInt(((SoapObject) resultSOAP
                        .getProperty(f)).getProperty("typeUser").toString());
                TypeActivite tmp = new TypeActivite(id, idcategorie, nom, false, typeUser);

                retour.add(tmp);

            }

        }

        return retour;

    }


    public   IndicateurWayd getIndicateurs() throws IOException,
            XmlPullParserException {

        String METHOD = "getIndicateurs";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.bodyOut = new SoapObject(NAMESPACE, METHOD);

        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
          //  SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }
        if (envelope.bodyIn != null) {

            SoapObject resultSOAP = (SoapObject) envelope.getResponse();

            int nbrTotalActivite = Integer.parseInt((resultSOAP
                    .getProperty("nbrTotalActivite").toString()));

            int nbrTotalParticipation = Integer.parseInt((resultSOAP
                    .getProperty("nbrTotalParticipation").toString()));

            int nbrTotalInscrit = Integer.parseInt((resultSOAP
                    .getProperty("nbrTotalInscrit").toString()));

            int nbrTotalMessage = Integer.parseInt((resultSOAP
                    .getProperty("nbrTotalMessage").toString()));

            int nbrTotalMessageByact = Integer.parseInt((resultSOAP
                    .getProperty("nbrTotalMessageByact").toString()));

            return new IndicateurWayd(nbrTotalActivite, nbrTotalParticipation,
                    nbrTotalInscrit, nbrTotalMessage, nbrTotalMessageByact);

        }
        return null;
    }

    public   TableauBord getTableauBord(int idpersonne) throws IOException,
            XmlPullParserException {

        String METHOD = "getTableauBord";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        envelope.bodyOut = request;
        request.addProperty("idpersonne", idpersonne);
        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
         //   SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }
        if (envelope.bodyIn != null) {

            SoapObject resultSOAP = (SoapObject) envelope.getResponse();
            if (resultSOAP == null) return null;
            int nbrmessagenonlu = Integer.parseInt((resultSOAP
                    .getProperty("nbrmessagenonlu").toString()));

            int nbractiviteencours = Integer.parseInt((resultSOAP
                    .getProperty("nbractiviteencours").toString()));
            int nbrsuggestion = Integer.parseInt((resultSOAP
                    .getProperty("nbrsuggestion").toString()));

            int nbrnotification = Integer.parseInt((resultSOAP
                    .getProperty("nbrnotification").toString()));
            int nbrami = Integer.parseInt((resultSOAP
                    .getProperty("nbrami").toString()));

            return new TableauBord(nbractiviteencours, nbrmessagenonlu, nbrsuggestion, nbrnotification, nbrami);

        }
        return null;
    }

    public  Personne getPersonnebyToken(String idtoken)
            throws IOException, XmlPullParserException {
        String METHOD = "getPersonnebyToken";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        envelope.bodyOut = request;
        request.addProperty("idtoken", idtoken);
        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
        //    SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }
        if (envelope.bodyIn != null) {

            SoapObject resultSOAP = (SoapObject) envelope.getResponse();
            if (resultSOAP == null) return null;
            int id = Integer.parseInt((resultSOAP.getProperty("id").toString()));
            String login = (resultSOAP.getProperty("login").toString());
            String mdp = (resultSOAP.getProperty("mdp") == null ? "" : (resultSOAP.getProperty("mdp").toString()));

            String nom = (resultSOAP.getProperty("nom") == null ? "" : (resultSOAP.getProperty("nom").toString()));
            String pseudo = (resultSOAP.getProperty("prenom") == null ? "" : (resultSOAP.getProperty("prenom").toString()));
            String message = (resultSOAP.getProperty("message") == null ? "" : (resultSOAP.getProperty("message").toString()));


            String photo = (resultSOAP.getProperty("photo") == null ? "" : (resultSOAP.getProperty("photo").toString()));
            String ville = (resultSOAP.getProperty("ville") == null ? "" : (resultSOAP.getProperty("ville").toString()));

            boolean actif = Boolean.parseBoolean((resultSOAP
                    .getProperty("actif").toString()));
            boolean verrouille = Boolean.parseBoolean((resultSOAP
                    .getProperty("verrouille").toString()));
            int nbrecheccnx = Integer.parseInt((resultSOAP
                    .getProperty("nbrecheccnx").toString()));
            Date datecreation = Outils.getDateFromSoapObject((resultSOAP
                    .getProperty("datecreationstr")));
            Date datenaissance = Outils.getDateFromSoapObject((resultSOAP
                    .getProperty("datenaissancestr")));
            int sexe = Integer.parseInt((resultSOAP.getProperty("sexe").toString()));
            int rayon = Integer.parseInt((resultSOAP.getProperty("rayon").toString()));

            boolean affichesexe = Boolean.parseBoolean((resultSOAP
                    .getProperty("affichesexe").toString()));
            boolean afficheage = Boolean.parseBoolean((resultSOAP
                    .getProperty("afficheage").toString()));
            String commentaire = (resultSOAP.getProperty("commentaire") == null ? "" : (resultSOAP.getProperty("commentaire").toString()));
            boolean premiereconnexion = Boolean.parseBoolean((resultSOAP
                    .getProperty("premiereconnexion").toString()));

            boolean admin = Boolean.parseBoolean((resultSOAP
                    .getProperty("admin").toString()));
            boolean notification=Boolean.parseBoolean((resultSOAP
                    .getProperty("notification").toString()));

            int typeUser = Integer.parseInt((resultSOAP.getProperty("typeUser").toString()));
            String siteWeb = (resultSOAP.getProperty("siteWeb") == null ? "" : (resultSOAP.getProperty("siteWeb").toString()));
            String telephone= (resultSOAP.getProperty("telephone") == null ? "" : (resultSOAP.getProperty("telephone").toString()));
            String siret = (resultSOAP.getProperty("siret") == null ? "" : (resultSOAP.getProperty("siret").toString()));

            return new Personne(id, login, mdp, nom, pseudo, ville, actif,
                    verrouille, nbrecheccnx, datecreation, message, photo, datenaissance, sexe, commentaire,
                    afficheage, affichesexe, premiereconnexion, rayon, admin,notification,typeUser,siteWeb,telephone,siret);

        }
        return null;

    }


    public  MessageServeur updatePseudo(String pseudo, Long datenaissance, int sexe, int idpersonne)

            throws IOException, XmlPullParserException {
        String METHOD = "updatePseudo";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        request.addProperty("pseudo", pseudo);
        request.addProperty("datenaissance", datenaissance);
        request.addProperty("sexe", sexe);
        request.addProperty("idpersonne", idpersonne);
        request.addProperty("jeton", Outils.jeton);

        envelope.bodyOut = request;
        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
          //  SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }
        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.getResponse();
            return getMessageServeurFromSoap(resultSOAP);
        }
        return null;
    }

    public   MessageServeur updateActivite(int idpersonne, int idactivite,String titre,String libelle,int nbmaxWaydeurs)

            throws IOException, XmlPullParserException {
        String METHOD = "updateActivite";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        request.addProperty("idpersonne", idpersonne);
        request.addProperty("idactivite", idactivite);
        request.addProperty("titre", titre);
        request.addProperty("libelle", libelle);
        request.addProperty("nbrmax", nbmaxWaydeurs);
        request.addProperty("jeton", Outils.jeton);

        envelope.bodyOut = request;
        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
            //  SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);


        }
        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.getResponse();
            return getMessageServeurFromSoap(resultSOAP);
        }
        return null;
    }



    public   MessageServeur updateNotification(int idpersonne,String jeton)

            throws IOException, XmlPullParserException {
        String METHOD = "updateNotification";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);

        request.addProperty("idpersonne", idpersonne);
        request.addProperty("jeton", Outils.jeton);

        envelope.bodyOut = request;
        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
            //  SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }
        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.getResponse();
            return getMessageServeurFromSoap(resultSOAP);
        }
        return null;
    }



    public  MessageServeur updateProfilWayd(Bitmap photo,
                                                  String nom, String pseudo, Date datenaissance,
                                                  int sexe, String commentaire, int idpersonne, boolean afficheage, boolean affichesexe)
            throws IOException, XmlPullParserException {
        String METHOD = "updateProfilWayd";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        String photostr = Outils.encodeTobase64(photo);
        request.addProperty("photostr", photostr);
        request.addProperty("nom", nom);
        request.addProperty("prenom", pseudo);
        String datenaissancestr = Outils.getStringWsFromDate(datenaissance);
        request.addProperty("datenaissancestr", datenaissancestr);
        request.addProperty("sexe", sexe);
        request.addProperty("commentaire", commentaire);
        request.addProperty("idpersonne", idpersonne);
        request.addProperty("afficheage", afficheage);
        request.addProperty("affichesexe", affichesexe);
        request.addProperty("jeton", Outils.jeton);
        envelope.bodyOut = request;

        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
        //    SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }
        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.getResponse();
            return getMessageServeurFromSoap(resultSOAP);
        }
        return null;
    }

    public   MessageServeur updatePosition(int idpersonne, Double latitude,
                                                Double longitude)
            throws IOException, XmlPullParserException {
        String METHOD = "updatePosition";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        request.addProperty("idpersonne", idpersonne);
        String latitudeStr = Double.toString(latitude);
        request.addProperty("latitudestr", latitudeStr);
        String longitudestr = Double.toString(longitude);
        request.addProperty("longitudestr", longitudestr);
        request.addProperty("jeton", Outils.jeton);

        envelope.bodyOut = request;
        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
        //    SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }
        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.getResponse();
            return getMessageServeurFromSoap(resultSOAP);
        }
        return null;
    }


    public   RetourMessage addMessage(int idemetteur,
                                           String corps, int iddestinataire)
            throws IOException, XmlPullParserException {

        String METHOD = "addMessage";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        request.addProperty("idemetteur", idemetteur);
        request.addProperty("corps", corps);
        request.addProperty("iddestinataire", iddestinataire);
        request.addProperty("jeton", Outils.jeton);

        envelope.bodyOut = request;
        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
          //  SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }
        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.getResponse();
            return getRetourMessageFromSoap(resultSOAP);
        }
        return null;
    }

    public   RetourMessage addMessageByAct(int idemetteur,
                                                String corps, int idactivite
    )
            throws IOException, XmlPullParserException {
        String METHOD = "addMessageByAct";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        request.addProperty("idemetteur", idemetteur);
        request.addProperty("corps", corps);
        request.addProperty("idactivite", idactivite);
        request.addProperty("jeton", Outils.jeton);

        envelope.bodyOut = request;
        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
        //    SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }
        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.getResponse();
            return getRetourMessageFromSoap(resultSOAP);
        }
        return null;
    }

    public   void testJeton(String idtoken, String photostr, String nom,String gcmToken)
            throws IOException, XmlPullParserException {
        String METHOD = "testToken";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        request.addProperty("idtoken", idtoken);
        request.addProperty("photostr", photostr);
        request.addProperty("nom", nom);
        request.addProperty("gcmToken", gcmToken);

        envelope.bodyOut = request;
        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
          //  SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }       //  if (envelope.bodyIn != null) {
        //     SoapObject resultSOAP = (SoapObject) envelope.getResponse();
        //     return getMessageServeur(resultSOAP);
        //   }
        //  return null;
    }


    public  MessageServeur updateGCM(int idpersonne, String gcm)
            throws IOException, XmlPullParserException {
        String METHOD = "updateGCM";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        request.addProperty("idpersonne", idpersonne);
        request.addProperty("gcm", gcm);
        request.addProperty("jeton", Outils.jeton);


        envelope.bodyOut = request;
        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
       //     SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }
        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.getResponse();
            return getMessageServeurFromSoap(resultSOAP);
        }
        return null;
    }


    public   MessageServeur effaceMessageRecu(int idpersonne, int idmessage)
            throws IOException, XmlPullParserException {
        String METHOD = "effaceMessageRecu";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        request.addProperty("idpersonne", idpersonne);
        request.addProperty("idmessage", idmessage);
        request.addProperty("jeton", Outils.jeton);
        envelope.bodyOut = request;
        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
          //  SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }
        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.getResponse();
            return getMessageServeurFromSoap(resultSOAP);
        }
        return null;
    }

    public  MessageServeur effaceMessageRecuByAct(int idpersonne, int idmessage)
            throws IOException, XmlPullParserException {
        String METHOD = "effaceMessageRecuByAct";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        request.addProperty("idpersonne", idpersonne);
        request.addProperty("idmessage", idmessage);
        request.addProperty("jeton", Outils.jeton);
        envelope.bodyOut = request;
        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
         //   SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }
        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.getResponse();
            return getMessageServeurFromSoap(resultSOAP);
        }
        return null;
    }

    public  MessageServeur effaceDiscussion(int iddestinataire, int idemetteur)
            throws IOException, XmlPullParserException {
        String METHOD = "effaceDiscussion";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        request.addProperty("iddestinataire", iddestinataire);
        request.addProperty("idemetteur", idemetteur);
        request.addProperty("jeton", Outils.jeton);
        envelope.bodyOut = request;
        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
        //    SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }
        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.getResponse();
            return getMessageServeurFromSoap(resultSOAP);
        }
        return null;
    }

    public  MessageServeur effaceAmi(int idpersonne, int idami)
            throws IOException, XmlPullParserException {
        String METHOD = "effaceAmi";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        request.addProperty("idpersonne", idpersonne);
        request.addProperty("idami", idami);
        request.addProperty("jeton", Outils.jeton);
        envelope.bodyOut = request;
        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
          //  SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }
        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.getResponse();
            return getMessageServeurFromSoap(resultSOAP);
        }
        return null;
    }

    public MessageServeur effaceNotificationRecu(int iddestinataire, int idnotification)
            throws IOException, XmlPullParserException {
        String METHOD = "effaceNotificationRecu";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        request.addProperty("iddestinataire", iddestinataire);
        request.addProperty("idnotification", idnotification);
        request.addProperty("jeton", Outils.jeton);
        envelope.bodyOut = request;
        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
       //     SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }
        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.getResponse();
            return getMessageServeurFromSoap(resultSOAP);
        }
        return null;
    }

    public   MessageServeur effaceMessageEmis(int idpersonne, int idmessage)
            throws IOException, XmlPullParserException {
        String METHOD = "effaceMessageEmis";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);


        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        request.addProperty("idpersonne", idpersonne);
        request.addProperty("idmessage", idmessage);
        request.addProperty("jeton", Outils.jeton);
        envelope.bodyOut = request;

        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
        //    SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }
        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.getResponse();
            return getMessageServeurFromSoap(resultSOAP);
        }
        return null;
    }

    public  MessageServeur effaceMessageEmisByAct(int idpersonne, int idmessage)
            throws IOException, XmlPullParserException {
        String METHOD = "effaceMessageEmisByAct";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        request.addProperty("idpersonne", idpersonne);
        request.addProperty("idmessage", idmessage);
        request.addProperty("jeton", Outils.jeton);
        envelope.bodyOut = request;
        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
        //    SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }
        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.getResponse();
            return getMessageServeurFromSoap(resultSOAP);
        }
        return null;
    }


    public   MessageServeur acquitMessage(int idpersonne, int idmessage)
            throws IOException, XmlPullParserException {
        String METHOD = "acquitMessage";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        request.addProperty("idpersonne", idpersonne);
        request.addProperty("idmessage", idmessage);
        request.addProperty("jeton", Outils.jeton);
        envelope.bodyOut = request;
        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
         //   SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }
        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.getResponse();
            return getMessageServeurFromSoap(resultSOAP);
        }
        return null;
    }

    public   MessageServeur acquitNotification(int idpersonne, int idmessage)
            throws IOException, XmlPullParserException {
        String METHOD = "acquitNotification";

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        request.addProperty("idpersonne", idpersonne);
        request.addProperty("idmessage", idmessage);
        request.addProperty("jeton", Outils.jeton);
        envelope.bodyOut = request;
        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
    //        SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }
        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.getResponse();
            return getMessageServeurFromSoap(resultSOAP);
        }
        return null;
    }

    public MessageServeur acquitMessageByAct(int idpersonne, int idmessage)
            throws IOException, XmlPullParserException {
        String METHOD = "acquitMessageByAct";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        request.addProperty("idpersonne", idpersonne);
        request.addProperty("idmessage", idmessage);
        request.addProperty("jeton", Outils.jeton);
        envelope.bodyOut = request;
        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
         //   SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }
        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.getResponse();
            return getMessageServeurFromSoap(resultSOAP);
        }
        return null;
    }

    public   MessageServeur acquitAllNotification(int idpersonne, String jeton)
            throws IOException, XmlPullParserException {
        String METHOD = "acquitAllNotification";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        request.addProperty("idpersonne", idpersonne);
        request.addProperty("jeton", jeton);

        envelope.bodyOut = request;

        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
      //      SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }
        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.getResponse();
            return getMessageServeurFromSoap(resultSOAP);
        }
        return null;
    }

    public   MessageServeur acquitMessageDiscussion(int idpersonne, int idemetteur, String jeton)
            throws IOException, XmlPullParserException {
        String METHOD = "acquitMessageDiscussion";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        request.addProperty("idpersonne", idpersonne);
        request.addProperty("idemetteur", idemetteur);
        request.addProperty("jeton", jeton);
        envelope.bodyOut = request;
        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
        //    SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }
        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.getResponse();
            return getMessageServeurFromSoap(resultSOAP);
        }
        return null;
    }

    public   MessageServeur acquitMessageDiscussionByAct(int iddestinataire, int idactivite)
            throws IOException, XmlPullParserException {
        String METHOD = "acquitMessageDiscussionByAct";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        request.addProperty("iddestinataire", iddestinataire);
        request.addProperty("idactivite", idactivite);
        request.addProperty("jeton", Outils.jeton);
        envelope.bodyOut = request;
        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
         //   SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }
        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.getResponse();
            return getMessageServeurFromSoap(resultSOAP);
        }
        return null;
    }

    public  MessageServeur updatePreference(int idpersonne, ArrayList<Preference> listdefinition, int rayon)
            throws IOException, XmlPullParserException {
        String METHOD = "updatePreference";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);

        request.addProperty("idpersonne", idpersonne);
        request.addProperty("rayon", rayon);


        for (Preference definition : listdefinition) {
            request.addProperty("idtypeactivite", definition.getIdTypeactivite());
        }

        for (Preference definition : listdefinition) {
            request.addProperty("active", definition.isActive());
        }

        request.addProperty("jeton", Outils.jeton);
        envelope.bodyOut = request;
        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
        //    SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }
        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.getResponse();
            return getMessageServeurFromSoap(resultSOAP);
        }
        return null;
    }

    public   ArrayList<PhotoWaydeur> getListPhotoWaydeur(List<Integer> listId)
            throws IOException, XmlPullParserException {
        ArrayList<PhotoWaydeur> retour = new ArrayList<>();
        String METHOD = "getListPhotoWaydeur";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        envelope.bodyOut = request;

        for (Integer entier: listId) {
            request.addProperty("idpersonne", entier);
        }

        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
           // SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }
        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.bodyIn;
            return getProfilDiscussionFromSOAP(resultSOAP);
        }
        return retour;
    }

    public   ArrayList<PhotoWaydeur> getListPhotoWaydeurByAct(int idactivite)
            throws IOException, XmlPullParserException {
        ArrayList<PhotoWaydeur> retour = new ArrayList<>();
        String METHOD = "getListPhotoWaydeurByAct";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        envelope.bodyOut = request;
        request.addProperty("iddemandeur", Outils.personneConnectee.getId());
        request.addProperty("idactivite", idactivite);
        request.addProperty("jeton", Outils.jeton);


        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
            // SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }
        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.bodyIn;
            return getProfilDiscussionFromSOAP(resultSOAP);
        }
        return retour;
    }



    private  ArrayList<PhotoWaydeur> getProfilDiscussionFromSOAP(SoapObject resultSOAP) {
        ArrayList<PhotoWaydeur> retour = new ArrayList<>();

        for (int f = 0; f < resultSOAP.getPropertyCount(); f++) {

            // Gere le cas ou l'activite n'a pas été trouvé
            if (resultSOAP.getProperty(f) == null) {
                retour.add(null);
                return retour;
            }

            int id = Integer.parseInt(((SoapObject) resultSOAP
                    .getProperty(f)).getProperty("id").toString());



            String photostr = ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("photo") == null ? "" : ((SoapObject) resultSOAP.getProperty(f))
                    .getProperty("photo").toString();




            PhotoWaydeur activite = new PhotoWaydeur(id,photostr);
            retour.add(activite);
        }
        return retour;

    }

    public  PhotoWaydeur getPhotoWaydeur(int idpersonne) throws IOException,
            XmlPullParserException {
        PhotoWaydeur retour = null;
        String METHOD = "getPhotoWaydeur";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        envelope.bodyOut = request;
        request.addProperty("idpersonne", idpersonne);

        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
        //    SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }


        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.getResponse();
            if (resultSOAP == null) return null;
            int id = Integer.parseInt((resultSOAP
                    .getProperty("id").toString()));

            String photostr = (resultSOAP.getProperty("photo") == null) ? "" : (resultSOAP.getProperty("photo").toString());

            retour = new PhotoWaydeur(id,photostr);
            return retour;
            //

        }
        return null;

    }



    public  MessageServeur signalActivite(int  idpersonne, int idactivite,int  idmotif,String  motif,String titre,String libelle ) throws IOException,
            XmlPullParserException {

        String METHOD = "signalerActivite";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        request.addProperty("idpersonne", idpersonne);
        request.addProperty("idactivite", idactivite);
        request.addProperty("idmotif", idmotif);
        request.addProperty("motif", motif);
        request.addProperty("titre", titre);
        request.addProperty("libelle", libelle);
        request.addProperty("jeton", Outils.jeton);
        envelope.bodyOut = request;

        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
            //      SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }

        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.getResponse();
            return getMessageServeurFromSoap(resultSOAP);
        }
        return null;


    }

    public   MessageServeur signalProfil(int  idpersonne, int idsignalement,int  idmotif,String  motif ) throws IOException,
            XmlPullParserException {

        String METHOD = "signalerProfil";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        request.addProperty("idpersonne", idpersonne);
        request.addProperty("idactivite", idsignalement);
        request.addProperty("idmotif", idmotif);
        request.addProperty("motif", motif);
        request.addProperty("jeton", Outils.jeton);
        envelope.bodyOut = request;

        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
            //      SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }

        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.getResponse();
            return getMessageServeurFromSoap(resultSOAP);
        }
        return null;


    }


    public  MessageServeur updateNotificationPref(int idpersonne,boolean notification)

            throws IOException, XmlPullParserException {
        String METHOD = "updateNotificationPref";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);

        request.addProperty("idpersonne", idpersonne);
        request.addProperty("jeton", Outils.jeton);
        request.addProperty("notification", notification);

        envelope.bodyOut = request;
        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
            //  SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }
        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.getResponse();
            return getMessageServeurFromSoap(resultSOAP);
        }
        return null;
    }

    public MessageServeur addActivitePro(String titre, String libelle, int idorganisateur, int idtypeactivite,
                                         double latitude, double longitude, String adresse, Long datedebut, Long datefin, String jeton)
            throws IOException, XmlPullParserException {

        String METHOD = "addActivitePro";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        request.addProperty("titre", titre);
        request.addProperty("libelle", libelle);
        request.addProperty("idorganisateur", idorganisateur);
        request.addProperty("idtypeactivite", idtypeactivite);
        String latitudeStr = Double.toString(latitude);
        request.addProperty("latitudestr", latitudeStr);
        String longitudeStr = Double.toString(longitude);
        request.addProperty("longitudestr", longitudeStr);
        request.addProperty("adresse", adresse);
        request.addProperty("dateDebut", datedebut);
         request.addProperty("dateFin", datefin);
         request.addProperty("jeton", jeton);

        envelope.bodyOut = request;
        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
            //     SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }
        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.getResponse();
            return getMessageServeurFromSoap(resultSOAP);
        }

        return null;


    }

    public  MessageServeur updateProfilPro(Bitmap photo,
                                           String pseudo, String telephone, String siret, String siteweb,
                                           String commentaire, int idpersonne)
            throws IOException, XmlPullParserException {
        String METHOD = "updateProfilPro";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        String photostr = Outils.encodeTobase64(photo);
        request.addProperty("photostr", photostr);
        request.addProperty("pseudo", pseudo);
        request.addProperty("commentaire", commentaire);
        request.addProperty("idpersonne", idpersonne);
        request.addProperty("tel", telephone);
        request.addProperty("siret", siret);
        request.addProperty("siteweb", siteweb);
        request.addProperty("jeton", Outils.jeton);
        envelope.bodyOut = request;

        if (SECURE) {
            HttpsTransportSE transport = new HttpsTransportSE(HOST, PORT, FILE, timeoutws);
            //    SslRequest.allowAllSSL();
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        } else {
            HttpTransportSE transport = new HttpTransportSE(URL, timeoutws);
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);

        }
        if (envelope.bodyIn != null) {
            SoapObject resultSOAP = (SoapObject) envelope.getResponse();
            return getMessageServeurFromSoap(resultSOAP);
        }
        return null;
    }

}
