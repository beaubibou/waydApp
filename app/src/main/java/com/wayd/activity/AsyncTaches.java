package com.wayd.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.wayd.bean.Activite;
import com.wayd.bean.Ami;
import com.wayd.bean.Avis;
import com.wayd.bean.CritereRechercheActivite;
import com.wayd.bean.Discussion;

import com.wayd.bean.IndicateurWayd;
import com.wayd.bean.Message;
import com.wayd.bean.MessageServeur;
import com.wayd.bean.Notification;
import com.wayd.bean.Outils;
import com.wayd.bean.Participant;
import com.wayd.bean.Personne;
import com.wayd.bean.PhotoActivite;
import com.wayd.bean.Preference;
import com.wayd.bean.Profil;
import com.wayd.bean.PhotoWaydeur;
import com.wayd.bean.ProfilNotation;
import com.wayd.bean.ProfilPro;
import com.wayd.bean.RetourMessage;
import com.wayd.bean.TableauBord;
import com.wayd.webservice.Wservice;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by bibou on 31/01/2017.
 */

@SuppressWarnings("DefaultFileTemplate")
public class AsyncTaches {

    final static int nbMaxTentative = 10;
    final static String MESSAGE_ECHEC_IO = "Tentative ";


    public static class AsyncGetListAmis extends AsyncTask<String, Integer, ArrayList<Ami>> {
        Integer tentative = 0;
        ProgressDialog mProgressDialog;
        String messageretour;
        boolean exeption = false;
        final Async_GetListAmis ecouteur;
        final int idpersonne;
        final Context mcontext;

        public AsyncGetListAmis(Async_GetListAmis ecouteur, int idpersonne, Context mcontext) {
            super();
            this.ecouteur = ecouteur;
            this.idpersonne = idpersonne;
            this.mcontext = mcontext;
        }

        @Override

        protected ArrayList<Ami> doInBackground(String... params) {
            do {
                try {
                    exeption = false;
                    tentative++;
                    Log.d("AsyncGetListAmis", "Tentative" + tentative.toString());
                    return (new Wservice().getListAmi(idpersonne));
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    messageretour = "Echec connexion serveur ";
                    exeption = true;
                } catch (XmlPullParserException e) {
                    // TODO Auto-generated catch block
                    exeption = true;
                    e.printStackTrace();

                }

            }
            while (tentative < nbMaxTentative && exeption == true);
            return null;

        }

        /**
         * @see AsyncTask#onPostExecute(Object)
         */
        @Override
        protected void onPostExecute(ArrayList<Ami> result) {

            if (exeption) {
                mProgressDialog.dismiss();
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }


           ecouteur.loopBack_getListAmis(result);
           mProgressDialog.dismiss();


        }

        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mcontext, "Patientez ...", "Chargement des amis...", true);
            mProgressDialog.setCancelable(false);

        }


        interface Async_GetListAmis {
            void loopBack_getListAmis(ArrayList<Ami> listami);
        }


    }


    public static class AsyncGetListActivite extends AsyncTask<String, String, ArrayList<Activite>> {
        private final int typeUser;
        Integer tentative = 0;
        ProgressDialog mProgressDialog;
        String messageretour;
        boolean exeption = false;
        final Async_GetListActiviteListener ecouteur;
        final double latitude;
        final double longitude;
        final int rayon;
        final int idtypeactivite;
        final int commencedans;
        final String motcle;
        final Context mcontext;

        public AsyncGetListActivite(Async_GetListActiviteListener ecouteur, CritereRechercheActivite critereRechercheActivite, Context mcontext) {
            super();
            this.ecouteur = ecouteur;
            this.mcontext = mcontext;
            this.motcle = critereRechercheActivite.getMotcle();
            this.idtypeactivite = critereRechercheActivite.getIdtypeactivite();
            this.rayon = critereRechercheActivite.getRayon();
            this.longitude = critereRechercheActivite.getLongitude();
            this.latitude = critereRechercheActivite.getLatitude();
            this.typeUser=critereRechercheActivite.getTypeUser();
            this.commencedans=critereRechercheActivite.getCommenceDans();

        }

        @Override

        protected ArrayList<Activite> doInBackground(String... params) {

            do {
                try {
                    exeption = false;
                    tentative++;

                    Log.d("AsyncGetListActivite", "Tentative" + tentative.toString());

                    return new Wservice().getActivites(latitude,
                           longitude,  rayon,  idtypeactivite,
                     motcle,  typeUser, commencedans);

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    messageretour = "Echec connexion serveur ";
                    exeption = true;
                } catch (XmlPullParserException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    exeption = true;

                }

            } while (tentative < nbMaxTentative && exeption == true);

            return null;


        }

        /**
         * @see AsyncTask#onPostExecute(Object)
         */
        @Override
        protected void onPostExecute(ArrayList<Activite> result) {

            if (exeption) {
                mProgressDialog.dismiss();
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }

            if (result != null) {
                ecouteur.loopBack_getListActivite(result);
            }

            if (result == null) {
                ecouteur.loopBack_getListActivite(null);
            }

            mProgressDialog.dismiss();

        }

        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mcontext, "Patientez ...", "Recherche des activités...", true);
            mProgressDialog.setCancelable(false);

        }


        interface Async_GetListActiviteListener {
            void loopBack_getListActivite(ArrayList<Activite> listactivite);
        }


    }


    public static class AsyncGetListDiscussion extends AsyncTask<String, Integer, ArrayList<Discussion>> {
        Integer tentative = 0;
        ProgressDialog mProgressDialog;
        String messageretour;
        boolean exeption = false;
        final Async_GetListDiscussionListener ecouteur;
        final int idpersonne;
        final Context mcontext;
        final boolean afficeProgress;

        public AsyncGetListDiscussion(Async_GetListDiscussionListener ecouteur, int idpersonne, Context mcontext, boolean afficheProgress) {
            super();
            this.ecouteur = ecouteur;
            this.idpersonne = idpersonne;
            this.mcontext = mcontext;
            this.afficeProgress = afficheProgress;

        }

        @Override

        protected ArrayList<Discussion> doInBackground(String... params) {

            do {
                try {
                    exeption = false;
                    tentative++;
                    Log.d("AsyncGetListDiscussion", "Tentative" + tentative.toString());
                    return (new Wservice().getListDiscussion(idpersonne));
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    messageretour = "Echec connexion serveur ";
                    exeption = true;
                    publishProgress(tentative);
                } catch (XmlPullParserException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    exeption = true;

                }
            } while (tentative < nbMaxTentative && exeption == true);

            return null;


        }

        // Permet d'afficher dans la  progress bar pour voir le nbr de tentativte de connexion
        // en mode debug seulement. n'est implémenté que sur cette tache pour le modéle
        protected void onProgressUpdate(Integer... values) {
            if (mProgressDialog != null) {

                //  mProgressDialog.setTitle(values[0].toString());

            }

        }

        /**
         * @see AsyncTask#onPostExecute(Object)
         */
        @Override
        protected void onPostExecute(ArrayList<Discussion> result) {

            if (exeption) {
                if (afficeProgress) mProgressDialog.dismiss();
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            ecouteur.loopBack_GetListDiscussion(result);
            if (afficeProgress) mProgressDialog.dismiss();
        }

        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            //Affiche progress permet de mettre pas pas la barre d'attente
            if (afficeProgress) {
                mProgressDialog = ProgressDialog.
                        show(mcontext, "Patientez ...", "Chargement des discussions...", true);
                mProgressDialog.setCancelable(false);
            }

        }


        interface Async_GetListDiscussionListener {
            void loopBack_GetListDiscussion(ArrayList<Discussion> listDiscussion);
        }


    }


    public static class AsyncEffaceAmi extends AsyncTask<String, String, MessageServeur> {
        Integer tentative = 0;
        ProgressDialog mProgressDialog;
        boolean exeption = false;
        final Async_EffaceAmiListener ecouteur;
        final int idpersonne;
        final int idami;
        final Context mcontext;

        public AsyncEffaceAmi(Async_EffaceAmiListener ecouteur, int idpersonne, int idami, Context mcontext) {
            super();
            this.ecouteur = ecouteur;
            this.idpersonne = idpersonne;
            this.mcontext = mcontext;
            this.idami = idami;
        }

        @Override
        protected MessageServeur doInBackground(String... params) {

            do {
                try {
                    exeption = false;
                    tentative++;
                    Log.d("AsyncEffaceAmi", "Tentative" + tentative.toString());

                    return new Wservice().effaceAmi(idpersonne, idami);
                } catch (IOException | XmlPullParserException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    exeption = true;
                }
            } while (tentative < nbMaxTentative && exeption == true);

            return null;

        }

        /**
         * @see AsyncTask#onPostExecute(Object)
         */
        @Override
        protected void onPostExecute(MessageServeur result) {

            if (exeption) {
                mProgressDialog.dismiss();
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

                return;
            }

            if (result != null)
                ecouteur.loopBack_EffaceAmi(result);

            mProgressDialog.dismiss();

        }

        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mcontext, "Patientez ...", "Mise à jour ...", true);
            mProgressDialog.setCancelable(true);
        }

        interface Async_EffaceAmiListener {
            void loopBack_EffaceAmi(MessageServeur messageserveur);
        }
    }

    public static class AsyncUpdatePreference extends AsyncTask<String, String, MessageServeur> {
        Integer tentative = 0;
        ProgressDialog mProgressDialog;
        boolean exeption = false;
        final Async_UpdatePreferenceListener ecouteur;
        final int idpersonne;
        final int rayon;
        final ArrayList<Preference> listpreference;
        final Context mcontext;

        public AsyncUpdatePreference(Async_UpdatePreferenceListener ecouteur, int idpersonne, ArrayList<Preference> listpreference, int rayon, Context mcontext) {
            super();
            this.ecouteur = ecouteur;
            this.idpersonne = idpersonne;
            this.rayon = rayon;
            this.listpreference = listpreference;
            this.mcontext = mcontext;

        }

        @Override
        protected MessageServeur doInBackground(String... params) {

            do {
                try {
                    exeption = false;
                    tentative++;
                    Log.d("AsyncUpdatePreference", "Tentative" + tentative.toString());
                    return new Wservice().updatePreference(idpersonne, listpreference, rayon);
                } catch (IOException | XmlPullParserException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    exeption = true;
                }
            } while (tentative < nbMaxTentative && exeption == true);

            return null;

        }


        /**
         * @see AsyncTask#onPostExecute(Object)
         */
        @Override
        protected void onPostExecute(MessageServeur result) {

            if (exeption) {
                mProgressDialog.dismiss();
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

                return;
            }

            ecouteur.loopBack_UpdatePreference(result);

            mProgressDialog.dismiss();

        }

        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mcontext, "Patientez ...", "Mise à jour des préférences ...", true);
            mProgressDialog.setCancelable(true);
        }

        interface Async_UpdatePreferenceListener {
            void loopBack_UpdatePreference(MessageServeur messageserveur);
        }
    }

    public static class AsyncGetActivite extends AsyncTask<String, String, Activite> {
        // ProgressDialog mProgressDialog;
        Integer tentative = 0;
        String messageretour;
        boolean exeption = false;
        final int idactivite;
        final Async_GetActiviteListener ecouteur;
        final Context mcontext;

        public AsyncGetActivite(Async_GetActiviteListener ecouteur, int idactivite, Context mcontext) {
            super();
            this.ecouteur = ecouteur;
            this.idactivite = idactivite;
            this.mcontext = mcontext;
        }

        interface Async_GetActiviteListener {
            void loopBack_GetActivite(Activite activite);
        }

        @Override
        protected Activite doInBackground(String... params) {
            do {
                try {
                    exeption = false;
                    tentative++;
                    Log.d("AsyncGetActivite", "Tentative" + tentative.toString());
                    return new Wservice().getActivite(idactivite);

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    messageretour = "Problème connexion serveur";
                    exeption = true;

                } catch (XmlPullParserException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    exeption = true;
                }
            } while (tentative < nbMaxTentative && exeption == true);

            return null;
        }

        /**
         * @see AsyncTask#onPostExecute(Object)
         */
        @Override
        protected void onPostExecute(Activite activite) {
            //  activite=result;
            if (exeption) {
                //     mProgressDialog.dismiss();
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }


            ecouteur.loopBack_GetActivite(activite);


            //mProgressDialog.dismiss();
        }

        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            //   mProgressDialog = ProgressDialog.show(mcontext, "Patientez ...", "Recuperation de l'activite ...", true);
            //    mProgressDialog.setCancelable(true);
        }
    }

    public static class AsyncAddParticipation extends AsyncTask<String, String, MessageServeur> {
        Integer tentative = 0;
        ProgressDialog mProgressDialog;
        boolean exeption = false;
        final Async_AddParticipationListener ecouteur;
        final int idactivite;
        final int idorganisateur;
        final Context mcontext;

        public AsyncAddParticipation(Async_AddParticipationListener ecouteur, int idorganisateur, int idactivite, Context mcontext) {
            super();
            this.ecouteur = ecouteur;
            this.idorganisateur = idorganisateur;
            this.idactivite = idactivite;
            this.mcontext = mcontext;
        }

        interface Async_AddParticipationListener {
            void loopBack_AddParticipation(MessageServeur messageserveur);
        }


        @Override
        protected MessageServeur doInBackground(String... params) {

            do {
                try {
                    exeption = false;
                    tentative++;
                    Log.d("AsyncAddParticipation", "Tentative" + tentative.toString());
                    return (new Wservice().addParticipation(Outils.personneConnectee.getId(), idorganisateur, idactivite));

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();

                    exeption = true;

                } catch (XmlPullParserException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    exeption = true;

                }
            } while (tentative < nbMaxTentative && exeption == true);

            return null;

        }

        /**
         * @see AsyncTask#onPostExecute(Object)
         */
        @Override
        protected void onPostExecute(MessageServeur result) {

            if (exeption) {
                mProgressDialog.dismiss();
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }

            if (result != null)
                ecouteur.loopBack_AddParticipation(result);

            else {

                Toast toast = Toast.makeText(mcontext, "Pas de retour serveur", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
            mProgressDialog.dismiss();

        }

        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mcontext, "Patientez ...", "Ajoute votre participation...", true);
            mProgressDialog.setCancelable(true);

        }
    }

    public static class AsyncEffaceActivite extends AsyncTask<String, String, MessageServeur> {
        Integer tentative = 0;
        ProgressDialog mProgressDialog;
        boolean exeption = false;
        final int idactivite;
        final int idpersonne;
        final Async_EffaceActiviteListener ecouteur;
        final Context mcontext;

        public AsyncEffaceActivite(Async_EffaceActiviteListener ecouteur, int idpersonne, int idactivite, Context mcontext) {
            super();
            this.ecouteur = ecouteur;
            this.idpersonne = idpersonne;
            this.idactivite = idactivite;
            this.mcontext = mcontext;
        }

        interface Async_EffaceActiviteListener {
            void loopBack_EffaceActivite(MessageServeur messageserveur);
        }

        @Override
        protected MessageServeur doInBackground(String... params) {

            do {
                try {
                    exeption = false;
                    tentative++;
                    Log.d("AsyncEffaceActivite", "Tentative" + tentative.toString());
                    return (new Wservice().effaceActivite(idpersonne, idactivite));

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    exeption = true;

                } catch (XmlPullParserException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    exeption = true;

                }
            }
            while (tentative < nbMaxTentative && exeption == true);

            return null;
        }

        /**
         * @see AsyncTask#onPostExecute(Object)
         */
        @Override
        protected void onPostExecute(MessageServeur result) {

            if (exeption) {
                mProgressDialog.dismiss();
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }

            if (result != null) {

                ecouteur.loopBack_EffaceActivite(result);

            } else {
                Toast toast = Toast.makeText(mcontext, "Pas de retour serveur", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }

            mProgressDialog.dismiss();
        }

        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mcontext, "Patientez ...", "Annulation de l'activité...", true);
            mProgressDialog.setCancelable(false);
        }
    }

    public static class AsyncEffaceParticipation extends AsyncTask<String, String, MessageServeur> {

        Integer tentative = 0;
        ProgressDialog mProgressDialog;
        boolean exeption = false;
        final int idactivite;

        final int idAeffacer;
        final int idDemandeur;
        final Async_EffaceParticipationListener ecouteur;
        final Context mcontext;

        public AsyncEffaceParticipation(Async_EffaceParticipationListener ecouteur, int idDemandeur, int idAeffacer, int idactivite, Context mcontext) {
            super();
            this.ecouteur = ecouteur;
            this.idactivite = idactivite;
            this.idAeffacer = idAeffacer;
            this.idDemandeur = idDemandeur;
            this.mcontext = mcontext;
        }

        interface Async_EffaceParticipationListener {
            void loopBack_EffaceParticipation(MessageServeur messageserveur);
        }

        @Override
        protected MessageServeur doInBackground(String... params) {

            do {
                try {
                    exeption = false;
                    tentative++;
                    Log.d("AsyncEffaceParticipati", "Tentative" + tentative.toString());
                    return (new Wservice().effaceParticipation(idDemandeur, idAeffacer, idactivite));

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    exeption = true;


                } catch (XmlPullParserException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    exeption = true;

                }
            } while (tentative < nbMaxTentative && exeption == true);

            return null;

        }

        /**
         * @see AsyncTask#onPostExecute(Object)
         */
        @Override
        protected void onPostExecute(MessageServeur result) {

            if (exeption) {
                mProgressDialog.dismiss();
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }


            if (result != null) {

                ecouteur.loopBack_EffaceParticipation(result);

            } else {
                Toast toast = Toast.makeText(mcontext, "Pas de retour serveur", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }


            mProgressDialog.dismiss();
        }

        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mcontext, "Patientez ...", "Annulation de la particiaption...", true);
            mProgressDialog.setCancelable(false);
        }
    }

    public static class AsyncGetProfil extends AsyncTask<String, String, Profil> {
        //    ProgressDialog mProgressDialog;
        Integer tentative = 0;
        String messageretour;
        boolean exeption = false;
        final int idpersonne;
        final Async_GetProfilListener ecouteur;
        final Context mcontext;

        public AsyncGetProfil(Async_GetProfilListener ecouteur, int idpersonne, Context mcontext) {
            super();
            this.ecouteur = ecouteur;
            this.idpersonne = idpersonne;
            this.mcontext = mcontext;
        }

        interface Async_GetProfilListener {
            void loopBack_GetProfil(Profil profil);
        }

        @Override
        protected Profil doInBackground(String... params) {

            do {
                try {
                    exeption = false;
                    tentative++;
                    Log.d("AsyncGetProfil", "Tentative" + tentative.toString());
                    return new Wservice().getFullProfil(idpersonne);
                } catch (IOException e) {
                    e.printStackTrace();
                    exeption = true;
                    messageretour = "Probleme connexion";

                } catch (XmlPullParserException e) {
                    exeption = true;
                    e.printStackTrace();
                }
            } while (tentative < nbMaxTentative && exeption == true);


            return null;

        }

        @Override
        protected void onPostExecute(Profil result) {
            if (exeption) {
                //          mProgressDialog.dismiss();
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }

            if (result != null) {
                ecouteur.loopBack_GetProfil(result);
            } else {
                Toast toast = Toast.makeText(mcontext, "Erreur inconnue", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
            //    mProgressDialog.dismiss();
        }


        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            // mProgressDialog = ProgressDialog.show(mcontext, "Patientez ...", "Chargement du profil ...", true);
            //     mProgressDialog.setCancelable(true);


        }

    }


    public static class AsyncAddNotation extends AsyncTask<String, String, MessageServeur> {

        Integer tentative = 0;
        ProgressDialog mProgressDialog;
        String messageretour;
        boolean exeption = false;
        final int idpersonne;
        final int idactivite;
        final Async_AddNotationListener ecouteur;
        final String commentaire;
        final boolean demandeami;
        final Context mcontext;
        final float note;

        public AsyncAddNotation(Async_AddNotationListener ecouteur, int idpersonne, int idactivite, String commentaire, float note, boolean demandeami, Context mcontext) {
            super();
            this.ecouteur = ecouteur;
            this.idpersonne = idpersonne;
            this.idactivite = idactivite;
            this.mcontext = mcontext;
            this.demandeami = demandeami;
            this.commentaire = commentaire;
            this.note = note;
        }

        interface Async_AddNotationListener {
            void loopBack_AddNotation(MessageServeur messageserveur);
        }

        @Override
        protected MessageServeur doInBackground(String... params) {
            do {
                try {
                    exeption = false;
                    tentative++;
                    Log.d("AsyncAddNotation", "Tentative" + tentative.toString());
                    return new Wservice().addAvis(Outils.personneConnectee.getId(), idpersonne, idactivite, "", commentaire, note, demandeami);
                } catch (IOException e) {
                    e.printStackTrace();
                    exeption = true;
                    messageretour = "Probleme connexion";

                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                    exeption = true;
                }

            } while (tentative < nbMaxTentative && exeption == true);
            return null;

        }

        @Override
        protected void onPostExecute(MessageServeur messageserveur) {
            if (exeption) {
                mProgressDialog.dismiss();
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }

            if (messageserveur != null) {

                ecouteur.loopBack_AddNotation(messageserveur);

            } else {
                Toast toast = Toast.makeText(mcontext, "Erreur inconnue", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();


            }
            mProgressDialog.dismiss();
        }

        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {

            mProgressDialog = ProgressDialog.show(mcontext, "Patientez ...", "Sauvegarde de la notation ...", true);
            mProgressDialog.setCancelable(true);
        }

    }

    public static class AsyncAddActivite extends AsyncTask<String, String, MessageServeur> {
        Integer tentative = 0;
        ProgressDialog mProgressDialog;
        String messageretour;
        boolean exeption = false;
        final int dureeBalise;
        final int dureeActivite;
        final Async_AddActiviteListener ecouteur;
        final String titre;
        final String libelle;
        final String adresse;
        final String jeton;
        final int nbmaxwaydeur;
        final int idorganisateur;
        final int idtypeactivite;
        final double latitude;
        final double longitude;
        final Context mcontext;


        public AsyncAddActivite(Async_AddActiviteListener ecouteur, String titre, String libelle,
                                int idorganisateur, int dureeBalise,
                                int idtypeactivite, double latitude, double longitude, String adresse,
                                int nbmaxwaydeur, int dureeActivite, String jeton, Context mcontext) {
            super();
            this.ecouteur = ecouteur;
            this.titre = titre;
            this.libelle = libelle;
            this.idorganisateur = idorganisateur;
            this.idtypeactivite = idtypeactivite;
            this.latitude = latitude;
            this.longitude = longitude;
            this.adresse = adresse;
            this.nbmaxwaydeur = nbmaxwaydeur;
            this.mcontext = mcontext;
            this.jeton = jeton;
            this.dureeActivite = dureeActivite;
            this.dureeBalise = dureeBalise;
        }

        interface Async_AddActiviteListener {
            void loopBack_AddActivite(MessageServeur messageserveur);
        }

        @Override
        protected MessageServeur doInBackground(String... params) {


            do {
                try {
                    exeption = false;
                    tentative++;
                    Log.d("AsyncAddActivite", "Tentative" + tentative.toString());

                    return new Wservice().addActivite(titre, libelle,
                            idorganisateur, dureeBalise,
                            idtypeactivite, latitude, longitude, adresse,
                            nbmaxwaydeur, dureeActivite, jeton);
                } catch (IOException e) {
                    e.printStackTrace();
                    exeption = true;
                    messageretour = "Probleme connexion";

                } catch (XmlPullParserException e) {
                    exeption = true;
                    e.printStackTrace();
                }

            } while (tentative < nbMaxTentative && exeption == true);
            return null;

        }

        @Override
        protected void onPostExecute(MessageServeur messageserveur) {
            if (exeption) {
                mProgressDialog.dismiss();
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }


            ecouteur.loopBack_AddActivite(messageserveur);

            mProgressDialog.dismiss();
        }

        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {

            mProgressDialog = ProgressDialog.show(mcontext, "Patientez ...", "Propose l'activité ...", true);
            mProgressDialog.setCancelable(true);
        }

    }


    public static class AsyncGetMesActiviteEnCours extends AsyncTask<String, String, ArrayList<Activite>> {
        Integer tentative = 0;
        ProgressDialog mProgressDialog;
        String messageretour;
        boolean exeption = false;
        final AsyncGetMesActiveEnCoursListener ecouteur;
        final int idpersonne;
        final Context mcontext;

        public AsyncGetMesActiviteEnCours(AsyncGetMesActiveEnCoursListener ecouteur, int idpersonne, Context mcontext) {
            super();
            this.ecouteur = ecouteur;
            this.idpersonne = idpersonne;
            this.mcontext = mcontext;
        }

        interface AsyncGetMesActiveEnCoursListener {
            void loopBack_GetMesActiviteEnCours(ArrayList<Activite> listeactivite);
        }

        @Override
        protected ArrayList<Activite> doInBackground(String... params) {

            do {
                try {
                    exeption = false;
                    tentative++;
                    Log.d("AsyncGetMesActiviteEnCo", "Tentative" + tentative.toString());

                    return new Wservice().getMesActiviteEncours(idpersonne);

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    messageretour = "Echec connexion serveur ";
                    exeption = true;
                } catch (XmlPullParserException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    exeption = true;

                }
            } while (tentative < nbMaxTentative && exeption == true);

            return null;

        }

        @Override
        protected void onPostExecute(ArrayList<Activite> result) {
            if (exeption) {
                mProgressDialog.dismiss();
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }

           // if (result != null)
                ecouteur.loopBack_GetMesActiviteEnCours(result);

            mProgressDialog.dismiss();
        }


        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {

            mProgressDialog = ProgressDialog.show(mcontext, "Patientez ...", "Recherche de mes activites ...", true);
            mProgressDialog.setCancelable(true);
        }

    }

    public static class AsyncGetMesActiviteArchive extends AsyncTask<String, String, ArrayList<Activite>> {

        ProgressDialog mProgressDialog;
        String messageretour;
        boolean exeption = false;
        Integer tentative = 0;
        final AsyncGetMesActiveArchiveListener ecouteur;
        final int idpersonne;
        final Context mcontext;

        public AsyncGetMesActiviteArchive(AsyncGetMesActiveArchiveListener ecouteur, int idpersonne, Context mcontext) {
            super();
            this.ecouteur = ecouteur;
            this.idpersonne = idpersonne;
            this.mcontext = mcontext;
        }

        interface AsyncGetMesActiveArchiveListener {
            void loopBack_GetMesActiviteArchive(ArrayList<Activite> listeactivite);
        }

        @Override
        protected ArrayList<Activite> doInBackground(String... params) {

            do {
                try {
                    exeption = false;
                    tentative++;
                    Log.d("AsyncGetMesActiviteArch", "Tentative" + tentative.toString());

                    return new Wservice().getMesActiviteArchive(idpersonne);

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    messageretour = "Echec connexion serveur ";
                    exeption = true;
                } catch (XmlPullParserException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    exeption = true;
                }

            } while (tentative < nbMaxTentative && exeption == true);
            return null;

        }

        @Override
        protected void onPostExecute(ArrayList<Activite> result) {
            if (exeption) {
                mProgressDialog.dismiss();
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }


                ecouteur.loopBack_GetMesActiviteArchive(result);

            mProgressDialog.dismiss();
        }


        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {

            mProgressDialog = ProgressDialog.show(mcontext, "Patientez ...", "Recherche de mes archives ...", true);
            mProgressDialog.setCancelable(true);
        }

    }

    public static class AsyncGetAdresse extends AsyncTask<String, String, Address> {
        final double latitude;
        final double longitude;
        final Context mcontext;
        final AsyncGetAdresseListener ecouteur;
        Integer tentative = 0;
        String messageretour;
        boolean exeption = false;

        public AsyncGetAdresse(AsyncGetAdresseListener ecouteur, double latitude, double longitude, Context mcontext) {
            super();
            this.ecouteur = ecouteur;
            this.latitude = latitude;
            this.longitude = longitude;
            this.mcontext = mcontext;
        }

        interface AsyncGetAdresseListener {
            void loopBack_GetAdresse(Address adresse);
        }

        @Override
        protected Address doInBackground(String... params) {

            List<Address> addresses;

            do {
                try {
                    exeption = false;
                    tentative++;
                    Log.d("AsyncGetAdresse", "Tentative" + tentative.toString());

                    // Geocoder geocoder = new Geocoder(mcontext);
                    // addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    addresses = Outils.getFromLocation(latitude, longitude, 1);
                    if (addresses.size() == 0) return null;
                    return addresses.get(0);
                } catch (Exception e) {
                    e.printStackTrace();
                    messageretour = "Echec connexion serveur ";
                    exeption = true;
                }

            } while (tentative < nbMaxTentative && exeption == true);

            return null;

        }

        @Override
        protected void onPostExecute(Address adresse) {
            //   mProgressDialog.dismiss();
            ecouteur.loopBack_GetAdresse(adresse);
        }

        protected void onPreExecute() {

        }
    }

    public static class AsyncGetListActivitePref extends AsyncTask<String, String, ArrayList<Activite>> {
        Integer tentative = 0;
        ProgressDialog mProgressDialog;
        String messageretour;
        boolean exeption = false;
        final AsyncGetListActivitePrefListener ecouteur;
        final int idpersonne;
        final Context mcontext;

        public AsyncGetListActivitePref(AsyncGetListActivitePrefListener ecouteur, int idpersonne, Context mcontext) {
            super();
            this.ecouteur = ecouteur;
            this.mcontext = mcontext;
            this.idpersonne = idpersonne;
        }

        @Override

        protected ArrayList<Activite> doInBackground(String... params) {

            do {
                try {
                    exeption = false;
                    tentative++;
                    Log.d("AsyncGetListActivitePr", "Tentative" + tentative.toString());
                    return new Wservice().getListActivitePref(idpersonne);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    messageretour = "Echec connexion serveur ";
                    exeption = true;

                } catch (XmlPullParserException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    exeption = true;


                }
            } while (tentative < nbMaxTentative && exeption == true);

            return null;


        }

        /**
         * @see AsyncTask#onPostExecute(Object)
         */
        @Override
        protected void onPostExecute(ArrayList<Activite> result) {

            if (exeption) {
                mProgressDialog.dismiss();
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            ecouteur.loopBack_GetListActivitePref(result);
            mProgressDialog.dismiss();

        }

        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mcontext, "Patientez ...", "Recherche des préfèrences...", true);
            mProgressDialog.setCancelable(false);

        }


        interface AsyncGetListActivitePrefListener {
            void loopBack_GetListActivitePref(ArrayList<Activite> listactivite);
        }
    }

    public static class AsyncAddMessage extends AsyncTask<String, String, RetourMessage> {
        Integer tentative = 0;
        ProgressDialog mProgressDialog;
        boolean exeption = false;
        final AsyncAddMessageListener ecouteur;
        final int idemetteur;
        final int iddestinataire;
        final String message;
        final Context mcontext;

        public AsyncAddMessage(AsyncAddMessageListener ecouteur, int idemetteur, String message, int iddestinataire, Context mcontext) {
            super();
            this.ecouteur = ecouteur;
            this.idemetteur = idemetteur;
            this.iddestinataire = iddestinataire;
            this.message = message;
            this.mcontext = mcontext;

        }

        @Override
        protected RetourMessage doInBackground(String... params) {

            do {
                try {
                    exeption = false;
                    tentative++;
                    Log.d("AsyncAddMessage", "Tentative" + tentative.toString());
                    return new Wservice().addMessage(idemetteur, message, iddestinataire);
                } catch (IOException | XmlPullParserException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    exeption = true;
                }
            } while (tentative < nbMaxTentative && exeption == true);


            return null;

        }

        /**
         * @see AsyncTask#onPostExecute(Object)
         */
        @Override
        protected void onPostExecute(RetourMessage result) {

            if (exeption) {
                mProgressDialog.dismiss();
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }

            if (result != null)
                ecouteur.loopBack_AddMessage(result);
            mProgressDialog.dismiss();

        }

        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mcontext, "Patientez ...", "Mise à jour ...", true);
            mProgressDialog.setCancelable(true);
        }

        interface AsyncAddMessageListener {
            void loopBack_AddMessage(RetourMessage messageserveur);
        }
    }

    public static class AsyncEffaceMessageRecu extends AsyncTask<String, String, MessageServeur> {
        ProgressDialog mProgressDialog;
        Integer tentative = 0;
        boolean exeption = false;
        final AsyncEffaceMessageRecuListener ecouteur;
        final int idemetteur;
        final int idmessageselection;

        final Context mcontext;

        public AsyncEffaceMessageRecu(AsyncEffaceMessageRecuListener ecouteur, int idemetteur, int idmessageselection, Context mcontext) {
            super();
            this.ecouteur = ecouteur;
            this.idemetteur = idemetteur;
            this.idmessageselection = idmessageselection;
            this.mcontext = mcontext;

        }

        @Override
        protected MessageServeur doInBackground(String... params) {

            do {
                try {
                    exeption = false;
                    tentative++;
                    Log.d("AsyncEffaceMessageRecu", "Tentative" + tentative.toString());
                    return new Wservice().effaceMessageRecu(idemetteur, idmessageselection);
                } catch (IOException | XmlPullParserException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    exeption = true;
                }
            } while (tentative < nbMaxTentative && exeption == true);
            return null;

        }

        /**
         * @see AsyncTask#onPostExecute(Object)
         */
        @Override
        protected void onPostExecute(MessageServeur result) {

            if (exeption) {
                mProgressDialog.dismiss();
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }


            ecouteur.loopBack_EffaceMessageRecu(result);
            mProgressDialog.dismiss();

        }

        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mcontext, "Patientez ...", "Mise à jour ...", true);
            mProgressDialog.setCancelable(true);
        }

        interface AsyncEffaceMessageRecuListener {
            void loopBack_EffaceMessageRecu(MessageServeur messageserveur);
        }
    }

    public static class AsyncEffaceMessageEmis extends AsyncTask<String, String, MessageServeur> {
        Integer tentative = 0;
        ProgressDialog mProgressDialog;
        boolean exeption = false;
        final AsyncEffaceMessageEmisListener ecouteur;
        final int idemetteur;
        final int idmessageselection;

        final Context mcontext;

        public AsyncEffaceMessageEmis(AsyncEffaceMessageEmisListener ecouteur, int idemetteur, int idmessageselection, Context mcontext) {
            super();
            this.ecouteur = ecouteur;
            this.idemetteur = idemetteur;
            this.idmessageselection = idmessageselection;
            this.mcontext = mcontext;

        }

        @Override
        protected MessageServeur doInBackground(String... params) {

            do {
                try {
                    exeption = false;
                    tentative++;
                    Log.d("AsyncEffaceMessageEmis", "Tentative" + tentative.toString());
                    return new Wservice().effaceMessageEmis(idemetteur, idmessageselection);
                } catch (IOException | XmlPullParserException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    exeption = true;
                }
            } while (tentative < nbMaxTentative && exeption == true);


            return null;

        }

        /**
         * @see AsyncTask#onPostExecute(Object)
         */
        @Override
        protected void onPostExecute(MessageServeur result) {

            if (exeption) {
                mProgressDialog.dismiss();
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }

            ecouteur.loopBack_EffaceMessageEmis(result);
            mProgressDialog.dismiss();

        }

        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mcontext, "Patientez ...", "Mise à jour ...", true);
            mProgressDialog.setCancelable(true);
        }

        interface AsyncEffaceMessageEmisListener {
            void loopBack_EffaceMessageEmis(MessageServeur messageserveur);
        }
    }


    public static class AsyncGetListMessageNext extends AsyncTask<String, String, ArrayList<Message>> {
        Integer tentative = 0;
        private final boolean visibleProgress;
        ProgressDialog mProgressDialog;
        String messageretour;
        boolean exeption = false;
        final AsyncGetListMessageNextListener ecouteur;
        final int emetteur;
        final int idxmessage;
        final Context mcontext;

        public AsyncGetListMessageNext(AsyncGetListMessageNextListener ecouteur, int emetteur, int idxmessage, boolean visibleProgress, Context mcontext) {
            super();
            this.ecouteur = ecouteur;
            this.emetteur = emetteur;
            this.idxmessage = idxmessage;
            this.mcontext = mcontext;
            this.visibleProgress = visibleProgress;
        }

        @Override

        protected ArrayList<Message> doInBackground(String... params) {

            do {
                try {
                    exeption = false;
                    tentative++;
                    Log.d("AsyncGetListMessageNext", "Tentative" + tentative.toString());

                    return (new Wservice().getListMessageAfter(emetteur, idxmessage));
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    messageretour = "Echec connexion serveur ";
                    exeption = true;

                } catch (XmlPullParserException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    exeption = true;
                }
            } while (tentative < nbMaxTentative && exeption == true);


            return null;
        }

        /**
         * @see AsyncTask#onPostExecute(Object)
         */
        @Override
        protected void onPostExecute(ArrayList<Message> listeMessage) {

            if (exeption) {
                if (mProgressDialog != null) mProgressDialog.dismiss();
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            ecouteur.loopBack_GetListMessageNext(listeMessage);
            if (mProgressDialog != null) mProgressDialog.dismiss();

        }

        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            if (visibleProgress) {
                mProgressDialog = ProgressDialog.show(mcontext, "Patientez ...", "Chargement des messages...", true);
                mProgressDialog.setCancelable(false);
            }
        }


        interface AsyncGetListMessageNextListener {
            void loopBack_GetListMessageNext(ArrayList<Message> listeMessage);
        }
    }

    public static class AsyncAcquitMessage extends AsyncTask<String, String, MessageServeur> {
        Integer tentative = 0;
        ProgressDialog mProgressDialog;
        boolean exeption = false;
        final AsyncAcquitMessageListener ecouteur;
        final int idemetteur;
        final int idmessage;
        final Context mcontext;

        public AsyncAcquitMessage(AsyncAcquitMessageListener ecouteur, int idemetteur, int idmessage, Context mcontext) {
            super();
            this.ecouteur = ecouteur;
            this.idemetteur = idemetteur;
            this.idmessage = idmessage;
            this.mcontext = mcontext;
        }

        @Override
        protected MessageServeur doInBackground(String... params) {

            do {
                try {
                    exeption = false;
                    tentative++;
                    Log.d("AsyncAcquitMessage", "Tentative" + tentative.toString());

                    return new Wservice().acquitMessage(idemetteur, idmessage);
                } catch (IOException | XmlPullParserException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    exeption = true;
                }
            } while (tentative < nbMaxTentative && exeption == true);


            return null;

        }

        /**
         * @see AsyncTask#onPostExecute(Object)
         */
        @Override
        protected void onPostExecute(MessageServeur result) {

            if (exeption) {
                mProgressDialog.dismiss();
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }

            ecouteur.loopBack_AcquitMessage(result);
            mProgressDialog.dismiss();

        }

        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mcontext, "Patientez ...", "Mise à jour ...", true);
            mProgressDialog.setCancelable(true);
        }

        interface AsyncAcquitMessageListener {
            void loopBack_AcquitMessage(MessageServeur messageserveur);
        }
    }

    public static class AsyncAcquitDiscussion extends AsyncTask<String, String, MessageServeur> {
        //  ProgressDialog mProgressDialog;
        Integer tentative = 0;
        boolean exeption = false;
        final AsyncAcquitDiscussionListener ecouteur;
        final int idpersonne;
        final int iddestinataire;
        final String jeton;
        final Context mcontext;

        public AsyncAcquitDiscussion(AsyncAcquitDiscussionListener ecouteur, int idpersonne, int iddestinataire, String jeton, Context mcontext) {
            super();
            this.ecouteur = ecouteur;
            this.idpersonne = idpersonne;
            this.iddestinataire = iddestinataire;
            this.jeton = jeton;
            this.mcontext = mcontext;
        }

        @Override
        protected MessageServeur doInBackground(String... params) {

            do {
                try {
                    exeption = false;
                    tentative++;
                    Log.d("AsyncAcquitDiscu", "Tentative" + tentative.toString());

                    return new Wservice().acquitMessageDiscussion(idpersonne, iddestinataire, jeton);
                } catch (IOException | XmlPullParserException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    exeption = true;
                }
            } while (tentative < nbMaxTentative && exeption == true);

            return null;

        }

        /**
         * @see AsyncTask#onPostExecute(Object)
         */
        @Override
        protected void onPostExecute(MessageServeur result) {

            if (exeption) {
                //     mProgressDialog.dismiss();
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }

            ecouteur.loopBack_AcquitDiscussion(result);


        }

        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {

        }

        interface AsyncAcquitDiscussionListener {
            void loopBack_AcquitDiscussion(MessageServeur messageserveur);
        }
    }


    public static class AsyncAddMessageByAct extends AsyncTask<String, String, RetourMessage> {
        Integer tentative = 0;
        ProgressDialog mProgressDialog;
        boolean exeption = false;
        final AsyncAddMessageByActListener ecouteur;
        final int idemetteur;
        final int idactivite;
        final String message;
        final Context mcontext;

        public AsyncAddMessageByAct(AsyncAddMessageByActListener ecouteur, int idemetteur, String message, int idactivite, Context mcontext) {
            super();
            this.ecouteur = ecouteur;
            this.idemetteur = idemetteur;
            this.idactivite = idactivite;
            this.message = message;
            this.mcontext = mcontext;

        }

        @Override
        protected RetourMessage doInBackground(String... params) {

            do {
                try {
                    exeption = false;
                    tentative++;
                    Log.d("AsyncAddMessageB", "Tentative" + tentative.toString());

                    return new Wservice().addMessageByAct(idemetteur, message, idactivite);
                } catch (IOException | XmlPullParserException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    exeption = true;
                }
            } while (tentative < nbMaxTentative && exeption == true);


            return null;

        }

        /**
         * @see AsyncTask#onPostExecute(Object)
         */
        @Override
        protected void onPostExecute(RetourMessage result) {

            if (exeption) {
                mProgressDialog.dismiss();
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }

            if (result != null)
                ecouteur.loopBack_AddMessageByAct(result);
            mProgressDialog.dismiss();

        }

        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mcontext, "Patientez ...", "Mise à jour ...", true);
            mProgressDialog.setCancelable(true);
        }

        interface AsyncAddMessageByActListener {
            void loopBack_AddMessageByAct(RetourMessage messageserveur);
        }
    }

    public static class AsyncEffaceMessageRecuByAct extends AsyncTask<String, String, MessageServeur> {
        Integer tentative = 0;
        ProgressDialog mProgressDialog;
        boolean exeption = false;
        final AsyncEffaceMessageRecuListenerByAct ecouteur;
        final int idemetteur;
        final int idmessageselection;

        final Context mcontext;

        public AsyncEffaceMessageRecuByAct(AsyncEffaceMessageRecuListenerByAct ecouteur, int idemetteur, int idmessageselection, Context mcontext) {
            super();
            this.ecouteur = ecouteur;
            this.idemetteur = idemetteur;
            this.idmessageselection = idmessageselection;
            this.mcontext = mcontext;
        }

        @Override
        protected MessageServeur doInBackground(String... params) {
            do {
                try {
                    exeption = false;
                    tentative++;
                    Log.d("AsyncEffaceMessageRecuB", "Tentative" + tentative.toString());
                    return new Wservice().effaceMessageRecuByAct(idemetteur, idmessageselection);
                } catch (IOException | XmlPullParserException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    exeption = true;
                }
            } while (tentative < nbMaxTentative && exeption == true);


            return null;

        }

        /**
         * @see AsyncTask#onPostExecute(Object)
         */
        @Override
        protected void onPostExecute(MessageServeur result) {

            if (exeption) {
                mProgressDialog.dismiss();
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }


            ecouteur.loopBack_EffaceMessageRecuByAct(result);
            mProgressDialog.dismiss();

        }

        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mcontext, "Patientez ...", "Mise à jour ...", true);
            mProgressDialog.setCancelable(true);
        }

        interface AsyncEffaceMessageRecuListenerByAct {
            void loopBack_EffaceMessageRecuByAct(MessageServeur messageserveur);
        }
    }

    public static class AsyncEffaceMessageEmisByAct extends AsyncTask<String, String, MessageServeur> {
        Integer tentative = 0;
        ProgressDialog mProgressDialog;
        boolean exeption = false;
        final AsyncEffaceMessageEmisListenerByAct ecouteur;
        final int idemetteur;
        final int idmessageselection;

        final Context mcontext;

        public AsyncEffaceMessageEmisByAct(AsyncEffaceMessageEmisListenerByAct ecouteur, int idemetteur, int idmessageselection, Context mcontext) {
            super();
            this.ecouteur = ecouteur;
            this.idemetteur = idemetteur;
            this.idmessageselection = idmessageselection;
            this.mcontext = mcontext;

        }

        @Override
        protected MessageServeur doInBackground(String... params) {

            do {
                try {
                    exeption = false;
                    tentative++;
                    Log.d("AsyncEffaceMessageEmisB", "Tentative" + tentative.toString());

                    return new Wservice().effaceMessageEmisByAct(idemetteur, idmessageselection);
                } catch (IOException | XmlPullParserException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    exeption = true;
                }
            } while (tentative < nbMaxTentative && exeption == true);


            return null;

        }

        /**
         * @see AsyncTask#onPostExecute(Object)
         */
        @Override
        protected void onPostExecute(MessageServeur result) {

            if (exeption) {
                mProgressDialog.dismiss();
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }

            ecouteur.loopBack_EffaceMessageEmisByAct(result);

            mProgressDialog.dismiss();

        }

        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mcontext, "Patientez ...", "Mise à jour ...", true);
            mProgressDialog.setCancelable(true);
        }

        interface AsyncEffaceMessageEmisListenerByAct {
            void loopBack_EffaceMessageEmisByAct(MessageServeur messageserveur);
        }
    }

    public static class AsyncGetListMessageByAct extends AsyncTask<String, String, ArrayList<Message>> {
        Integer tentative = 0;
        ProgressDialog mProgressDialog;
        String messageretour;
        boolean exeption = false;
        final AsyncGetListMessageListenerByAct ecouteur;
        final int emetteur;
        final int idactivite;
        final Context mcontext;

        public AsyncGetListMessageByAct(AsyncGetListMessageListenerByAct ecouteur, int emetteur, int idactivite, Context mcontext) {
            super();
            this.ecouteur = ecouteur;
            this.emetteur = emetteur;
            this.idactivite = idactivite;
            this.mcontext = mcontext;
        }

        @Override

        protected ArrayList<Message> doInBackground(String... params) {

            do {
                try {
                    exeption = false;
                    tentative++;
                    Log.d("AsyncGetListMessageB", "Tentative" + tentative.toString());

                    return (new Wservice().getDiscussionByAct(emetteur, idactivite));
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    messageretour = "Echec connexion serveur ";
                    exeption = true;
                } catch (XmlPullParserException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    exeption = true;

                }
            } while (tentative < nbMaxTentative && exeption == true);
            return null;
        }

        /**
         * @see AsyncTask#onPostExecute(Object)
         */
        @Override
        protected void onPostExecute(ArrayList<Message> listeMessage) {

            if (exeption) {
                mProgressDialog.dismiss();
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            ecouteur.loopBack_GetListeMessageByAct(listeMessage);
            mProgressDialog.dismiss();

        }

        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mcontext, "Patientez ...", "Chargement des messages...", true);
            mProgressDialog.setCancelable(false);

        }

        interface AsyncGetListMessageListenerByAct {
            void loopBack_GetListeMessageByAct(ArrayList<Message> listeMessage);
        }
    }

    public static class AsyncGetListMessageFullByAct extends AsyncTask<String, String, ArrayList<Message>> {
        Integer tentative = 0;
        ProgressDialog mProgressDialog;
        String messageretour;
        boolean exeption = false;
        final AsyncGetListMessageFullListenerByAct ecouteur;
        final int emetteur;
        final int idactivite;
        final Context mcontext;
        ArrayList<PhotoWaydeur> listPhotoWaydeur;


        public AsyncGetListMessageFullByAct(AsyncGetListMessageFullListenerByAct ecouteur, int emetteur, int idactivite, Context mcontext) {
            super();
            this.ecouteur = ecouteur;
            this.emetteur = emetteur;
            this.idactivite = idactivite;
            this.mcontext = mcontext;
        }

        @Override

        protected ArrayList<Message> doInBackground(String... params) {
            do {
                try {
                    exeption = false;
                    tentative++;
                    Log.d("AsyncGetListMessaFullBy", "Tentative" + tentative.toString());

                    // *****************  Recuepre les photos
                    ArrayList<Integer> listId = new ArrayList<>();
                    listPhotoWaydeur = new ArrayList<>(new Wservice().getListPhotoWaydeurByAct(idactivite));
                    // renvoi les messages

                    return (new Wservice().getDiscussionByAct(emetteur, idactivite));
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    messageretour = "Echec connexion serveur ";
                    exeption = true;
                } catch (XmlPullParserException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    exeption = true;

                }

            } while (tentative < nbMaxTentative && exeption == true);
            return null;
        }

        /**
         * @see AsyncTask#onPostExecute(Object)
         */
        @Override
        protected void onPostExecute(ArrayList<Message> listeMessage) {

            if (exeption) {
                mProgressDialog.dismiss();
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            ecouteur.loopBack_GetListeMessageFullByAct(listeMessage, listPhotoWaydeur);
            mProgressDialog.dismiss();

        }

        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mcontext, "Patientez ...", "Chargement des messages...", true);
            mProgressDialog.setCancelable(false);

        }

        interface AsyncGetListMessageFullListenerByAct {
            void loopBack_GetListeMessageFullByAct(ArrayList<Message> listeMessage, ArrayList<PhotoWaydeur> listPhotoWaydeur);
        }
    }


    public static class AsyncGetListMessageNextByAct extends AsyncTask<String, String, ArrayList<Message>> {
        Integer tentative = 0;
        ProgressDialog mProgressDialog;
        String messageretour;
        boolean exeption = false;
        final AsyncGetListMessageNextListenerByAct ecouteur;
        final int emetteur;
        final int idxmessage;
        final int idactivite;
        final Context mcontext;
        private boolean visibleProgress;

        public AsyncGetListMessageNextByAct(AsyncGetListMessageNextListenerByAct ecouteur, int emetteur, int idxmessage, int idactivite, boolean visibleProgress, Context mcontext) {
            super();
            this.ecouteur = ecouteur;
            this.emetteur = emetteur;
            this.idxmessage = idxmessage;
            this.idactivite = idactivite;
            this.mcontext = mcontext;
            this.visibleProgress = visibleProgress;
        }

        @Override

        protected ArrayList<Message> doInBackground(String... params) {
            do {
                try {
                    exeption = false;
                    tentative++;
                    Log.d("AsyncGetListMessageNex", "Tentative" + tentative.toString());
                    return (new Wservice().getListMessageAfterByAct(emetteur, idxmessage, idactivite));
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    messageretour = "Echec connexion serveur ";
                    exeption = true;
                } catch (XmlPullParserException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    exeption = true;

                }
            } while (tentative < nbMaxTentative && exeption == true);

            return null;
        }

        /**
         * @see AsyncTask#onPostExecute(Object)
         */
        @Override
        protected void onPostExecute(ArrayList<Message> listeMessage) {

            if (exeption) {

                if (mProgressDialog != null)
                    mProgressDialog.dismiss();
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }

            ecouteur.loopBack_GetListMessageNextByAct(listeMessage);
            if (mProgressDialog != null)
                mProgressDialog.dismiss();
        }

        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            // Affiche le barre de progression si demandé
            if (visibleProgress) {
                mProgressDialog = ProgressDialog.show(mcontext, "Patientez ...", "Chargement des messages...", true);
                mProgressDialog.setCancelable(false);
            }

        }


        interface AsyncGetListMessageNextListenerByAct {
            void loopBack_GetListMessageNextByAct(ArrayList<Message> listeMessage);
        }
    }

    public static class AsyncAcquitMessageByAct extends AsyncTask<String, String, MessageServeur> {
        Integer tentative = 0;
        ProgressDialog mProgressDialog;
        boolean exeption = false;
        final AsyncAcquitMessageListenerByAct ecouteur;
        final int idemetteur;
        final int idmessage;
        final Context mcontext;

        public AsyncAcquitMessageByAct(AsyncAcquitMessageListenerByAct ecouteur, int idemetteur, int idmessage, Context mcontext) {
            super();
            this.ecouteur = ecouteur;
            this.idemetteur = idemetteur;
            this.idmessage = idmessage;
            this.mcontext = mcontext;
        }

        @Override
        protected MessageServeur doInBackground(String... params) {

            do {
                try {
                    exeption = false;
                    tentative++;
                    Log.d("AsyncAcquitMessageByAct", "Tentative" + tentative.toString());
                    return new Wservice().acquitMessageByAct(idemetteur, idmessage);
                } catch (IOException | XmlPullParserException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    exeption = true;
                }
            } while (tentative < nbMaxTentative && exeption == true);

            return null;

        }

        /**
         * @see AsyncTask#onPostExecute(Object)
         */
        @Override
        protected void onPostExecute(MessageServeur result) {

            if (exeption) {
                mProgressDialog.dismiss();
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }

            ecouteur.loopBack_AcquitMessageByAct(result);
            mProgressDialog.dismiss();

        }

        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mcontext, "Patientez ...", "Mise à jour ...", true);
            mProgressDialog.setCancelable(true);
        }

        interface AsyncAcquitMessageListenerByAct {
            void loopBack_AcquitMessageByAct(MessageServeur messageserveur);
        }
    }

    public static class AsyncAcquitDiscussionByAct extends AsyncTask<String, String, MessageServeur> {
        Integer tentative = 0;
        boolean exeption = false;
        final AsyncAcquitDiscussionListenerByAct ecouteur;
        final int idemetteur;
        final int idactivite;
        final Context mcontext;

        public AsyncAcquitDiscussionByAct(AsyncAcquitDiscussionListenerByAct ecouteur, int idemetteur, int idactivite, Context mcontext) {
            super();
            this.ecouteur = ecouteur;
            this.idemetteur = idemetteur;
            this.idactivite = idactivite;
            this.mcontext = mcontext;
        }

        @Override
        protected MessageServeur doInBackground(String... params) {
            do {
                try {
                    exeption = false;
                    tentative++;
                    Log.d("AsyncAcquitDiscussionB", "Tentative" + tentative.toString());
                    return new Wservice().acquitMessageDiscussionByAct(idemetteur, idactivite);
                } catch (IOException | XmlPullParserException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    exeption = true;
                }
            } while (tentative < nbMaxTentative && exeption == true);


            return null;

        }

        /**
         * @see AsyncTask#onPostExecute(Object)
         */
        @Override
        protected void onPostExecute(MessageServeur result) {

            if (exeption) {
//                mProgressDialog.dismiss();
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }

            ecouteur.loopBack_AcquitDiscussionByAct(result);


        }

        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {

        }

        interface AsyncAcquitDiscussionListenerByAct {
            void loopBack_AcquitDiscussionByAct(MessageServeur messageserveur);
        }
    }


    public static class AsyncUpdateProfil extends AsyncTask<String, String, MessageServeur> {
        Integer tentative = 0;
        ProgressDialog mProgressDialog;
        boolean exeption = false;
        final AsyncUpdateProfilListener ecouteur;
        final int idpersonne;
        final Bitmap photo;
        final String nom;
        final String pseudo;
        final String commentaire;
        final Date datenaissance;
        final int sexe;
        final boolean afficheage;
        final boolean affichesexe;

        final Context mcontext;

        public AsyncUpdateProfil(AsyncUpdateProfilListener ecouteur, Bitmap photo,
                                 String nom, String pseudo, Date datenaissance,
                                 int sexe, String commentaire, int idpersonne, boolean afficheage, boolean affichesexe, Context mcontext) {
            super();
            this.ecouteur = ecouteur;
            this.idpersonne = idpersonne;
            this.photo = photo;
            this.nom = nom;
            this.pseudo = pseudo;
            this.datenaissance = datenaissance;
            this.sexe = sexe;
            this.commentaire = commentaire;
            this.afficheage = afficheage;
            this.affichesexe = affichesexe;
            this.mcontext = mcontext;

        }

        @Override
        protected MessageServeur doInBackground(String... params) {

            do {
                try {
                    exeption = false;
                    tentative++;
                    Log.d("AsyncUpdateProfil", "Tentative" + tentative.toString());
                    return new Wservice().updateProfilWayd(photo, nom, pseudo, datenaissance,
                            sexe, commentaire, idpersonne, afficheage, affichesexe);
                } catch (IOException | XmlPullParserException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    exeption = true;
                }
            } while (tentative < nbMaxTentative && exeption == true);

            return null;

        }

        /**
         * @see AsyncTask#onPostExecute(Object)
         */
        @Override
        protected void onPostExecute(MessageServeur result) {

            if (exeption) {
                mProgressDialog.dismiss();
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }

            ecouteur.loopBack_UpdateProfil(result);

            mProgressDialog.dismiss();

        }

        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mcontext, "Patientez ...", "Mise à jour du profil ...", true);
            mProgressDialog.setCancelable(true);
        }

        interface AsyncUpdateProfilListener {
            void loopBack_UpdateProfil(MessageServeur messageserveur);
        }
    }


    public static class AsyncGetListPreference extends AsyncTask<String, String, ArrayList<Preference>> {
        Integer tentative = 0;
        ProgressDialog mProgressDialog;
        String messageretour;
        boolean exeption = false;
        final int idpersonne;
        final AsyncGetListPreferenceListener ecouteur;
        final Context mcontext;

        public AsyncGetListPreference(AsyncGetListPreferenceListener ecouteur, int idpersonne, Context mcontext) {
            super();
            this.ecouteur = ecouteur;
            this.idpersonne = idpersonne;
            this.mcontext = mcontext;
        }

        interface AsyncGetListPreferenceListener {
            void loopBack_GetListPreference(ArrayList<Preference> listpreference);
        }

        @Override
        protected ArrayList<Preference> doInBackground(String... params) {
            do {
                try {
                    exeption = false;
                    tentative++;
                    Log.d("AsyncGetListPreference", "Tentative" + tentative.toString());
                    return new Wservice().getListPreferences(idpersonne);
                } catch (IOException e) {
                    e.printStackTrace();
                    exeption = true;
                    messageretour = "Probleme connexion";

                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                    exeption = true;
                }
            } while (tentative < nbMaxTentative && exeption == true);


            return null;

        }

        @Override
        protected void onPostExecute(ArrayList<Preference> result) {
            if (exeption) {
                mProgressDialog.dismiss();
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }

            ecouteur.loopBack_GetListPreference(result);

            mProgressDialog.dismiss();
        }


        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mcontext, "Patientez ...", "Chargement du profil ...", true);
            mProgressDialog.setCancelable(true);


        }

    }

    public static class AsyncAcquitAllNotification extends AsyncTask<String, String, MessageServeur> {
        Integer tentative = 0;
        boolean exeption = false;
        final AsyncAcquitTouteNotificationListener ecouteur;
        final int idpersonne;


        final Context mcontext;

        public AsyncAcquitAllNotification(AsyncAcquitTouteNotificationListener ecouteur, int idpersonne, Context mcontext) {
            super();
            this.ecouteur = ecouteur;
            this.idpersonne = idpersonne;
            this.mcontext = mcontext;


        }

        @Override
        protected MessageServeur doInBackground(String... params) {
            do {
                try {
                    exeption = false;
                    tentative++;
                    Log.d("AsyncAcquitAllNotificat", "Tentative" + tentative.toString());
                    return new Wservice().acquitAllNotification(idpersonne, Outils.jeton);
                } catch (IOException | XmlPullParserException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    exeption = true;
                }
            } while (tentative < nbMaxTentative && exeption == true);
            return null;

        }

        /**
         * @see AsyncTask#onPostExecute(Object)
         */
        @Override
        protected void onPostExecute(MessageServeur result) {

            if (exeption) {

                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }

            ecouteur.loopBack_AcquitTouteNotification(result);

        }

        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
        }

        interface AsyncAcquitTouteNotificationListener {
            void loopBack_AcquitTouteNotification(MessageServeur messageserveur);
        }
    }

    public static class AsyncAcquitNotification extends AsyncTask<String, String, MessageServeur> {
        Integer tentative = 0;
        boolean exeption = false;
        final AsyncAcquitNotificationListener ecouteur;
        final int idpersonne;
        final int idnotification;
        final Context mcontext;

        public AsyncAcquitNotification(AsyncAcquitNotificationListener ecouteur, int idpersonne, int idnotification, Context mcontext) {
            super();

            this.ecouteur = ecouteur;
            this.idpersonne = idpersonne;
            this.mcontext = mcontext;
            this.idnotification = idnotification;
        }

        @Override
        protected MessageServeur doInBackground(String... params) {
            do {
                try {
                    exeption = false;
                    tentative++;
                    Log.d("AsyncAcquitNotification", "Tentative" + tentative.toString());
                    return new Wservice().acquitNotification(idpersonne, idnotification);
                } catch (IOException | XmlPullParserException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    exeption = true;
                }
            } while (tentative < nbMaxTentative && exeption == true);


            return null;

        }

        /**
         * @see AsyncTask#onPostExecute(Object)
         */
        @Override
        protected void onPostExecute(MessageServeur result) {

            if (exeption) {
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }

            ecouteur.loopBack_AcquitNotification(result);

        }

        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
        }

        interface AsyncAcquitNotificationListener {
            void loopBack_AcquitNotification(MessageServeur messageserveur);
        }
    }

    public static class AsyncEffaceNotification extends AsyncTask<String, String, MessageServeur> {
        Integer tentative = 0;
        boolean exeption = false;
        final AsyncEffaceNotificationListener ecouteur;
        final int idpersonne;
        final int idnotificationselection;
        final Context mcontext;

        public AsyncEffaceNotification(AsyncEffaceNotificationListener ecouteur, int idpersonne, int idnotificationselection, Context mcontext) {
            super();

            this.ecouteur = ecouteur;
            this.idpersonne = idpersonne;
            this.mcontext = mcontext;
            this.idnotificationselection = idnotificationselection;

        }

        @Override
        protected MessageServeur doInBackground(String... params) {
            do {
                try {
                    exeption = false;
                    tentative++;
                    Log.d("AsyncEffaceNotification", "Tentative" + tentative.toString());
                    return new Wservice().effaceNotificationRecu(idpersonne, idnotificationselection);
                } catch (IOException | XmlPullParserException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    exeption = true;
                }
            } while (tentative < nbMaxTentative && exeption == true);

            return null;

        }

        /**
         * @see AsyncTask#onPostExecute(Object)
         */
        @Override
        protected void onPostExecute(MessageServeur result) {

            if (exeption) {
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }

            ecouteur.loopBack_EffaceNotification(result);

        }

        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
        }

        interface AsyncEffaceNotificationListener {
            void loopBack_EffaceNotification(MessageServeur messageserveur);
        }
    }

    public static class AsyncGetListNotification extends AsyncTask<String, String, ArrayList<Notification>> {
        Integer tentative = 0;
        ProgressDialog mProgressDialog;
        String messageretour;
        boolean exeption = false;
        final int idpersonne;
        final AsyncGetListNotificationListener ecouteur;
        final Context mcontext;

        public AsyncGetListNotification(AsyncGetListNotificationListener ecouteur, int idpersonne, Context mcontext) {
            super();
            this.ecouteur = ecouteur;
            this.idpersonne = idpersonne;
            this.mcontext = mcontext;
        }

        interface AsyncGetListNotificationListener {
            void loopBack_GetListNotification(ArrayList<Notification> listpreference);
        }

        @Override
        protected ArrayList<Notification> doInBackground(String... params) {

            do {
                try {
                    exeption = false;
                    tentative++;
                    Log.d("AsyncGetListNotificat", "Tentative" + tentative.toString());
                    return new Wservice().getListNotification(idpersonne);
                } catch (IOException e) {
                    e.printStackTrace();
                    exeption = true;
                    messageretour = "Problème connexion";

                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                    exeption = true;
                }
            } while (tentative < nbMaxTentative && exeption == true);

            return null;

        }

        @Override
        protected void onPostExecute(ArrayList<Notification> result) {
            if (exeption) {
                mProgressDialog.dismiss();
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }

            ecouteur.loopBack_GetListNotification(result);

            mProgressDialog.dismiss();
        }


        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mcontext, "Patientez ...", "Chargement du profil ...", true);
            mProgressDialog.setCancelable(true);


        }

    }

    public static class AsyncGetNextListNotification extends AsyncTask<String, String, ArrayList<Notification>> {
        Integer tentative = 0;
        private final boolean visibleProgressBar;
        ProgressDialog mProgressDialog;
        String messageretour;
        boolean exeption = false;
        final int idpersonne;
        final int idxnotification;
        final AsyncGetNextListNotificationListener ecouteur;
        final Context mcontext;

        public AsyncGetNextListNotification(AsyncGetNextListNotificationListener ecouteur, int idpersonne, int idxnotification, boolean visibleProgressBar, Context mcontext) {
            super();
            this.ecouteur = ecouteur;
            this.idpersonne = idpersonne;
            this.idxnotification = idxnotification;
            this.mcontext = mcontext;
            this.visibleProgressBar = visibleProgressBar;
        }

        interface AsyncGetNextListNotificationListener {
            void loopBack_GetNextListNotification(ArrayList<Notification> listpreference);
        }

        @Override
        protected ArrayList<Notification> doInBackground(String... params) {

            do {
                try {
                    exeption = false;
                    tentative++;
                    Log.d("AsyncGetNextListNo", "Tentative" + tentative.toString());

                    return new Wservice().getListNotificationAfter(idpersonne, idxnotification);
                } catch (IOException e) {
                    e.printStackTrace();
                    exeption = true;
                    messageretour = "Problème connexion";

                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                    exeption = true;
                }

            } while (tentative < nbMaxTentative && exeption == true);

            return null;

        }

        @Override
        protected void onPostExecute(ArrayList<Notification> result) {
            if (exeption) {
                if (mProgressDialog != null) mProgressDialog.dismiss();
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }

            ecouteur.loopBack_GetNextListNotification(result);

            if (mProgressDialog != null) mProgressDialog.dismiss();
        }


        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            if (visibleProgressBar) {
                mProgressDialog = ProgressDialog.show(mcontext, "Patientez ...", "Chargement du profil ...", true);
                mProgressDialog.setCancelable(true);
            }
        }

    }

    public static class AsyncUpdatePseudo extends AsyncTask<String, String, MessageServeur> {
        Integer tentative = 0;
        boolean exeption = false;
        final AsyncUpdatePseudoListener ecouteur;
        final int idpersonne;
        final String pseudo;
        final Long datenaissance;
        final int sexe;
        final Context mcontext;

        public AsyncUpdatePseudo(AsyncUpdatePseudoListener ecouteur, String pseudo, Long datenaissance, int sexe, int idpersonne, Context mcontext) {
            super();

            this.ecouteur = ecouteur;
            this.idpersonne = idpersonne;
            this.mcontext = mcontext;
            this.pseudo = pseudo;
            this.datenaissance = datenaissance;
            this.sexe = sexe;

        }

        @Override
        protected MessageServeur doInBackground(String... params) {

            do {
                try {
                    exeption = false;
                    tentative++;
                    Log.d("AsyncUpdatePseudo", "Tentative" + tentative.toString());

                    return new Wservice().updatePseudo(pseudo, datenaissance, sexe, idpersonne);
                } catch (IOException | XmlPullParserException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    exeption = true;
                }
            } while (tentative < nbMaxTentative && exeption == true);

            return null;

        }

        /**
         * @see AsyncTask#onPostExecute(Object)
         */
        @Override
        protected void onPostExecute(MessageServeur result) {

            if (exeption) {
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }

            ecouteur.loopBack_UpdatePseudo(result);

        }

        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
        }

        interface AsyncUpdatePseudoListener {
            void loopBack_UpdatePseudo(MessageServeur messageserveur);
        }
    }

    public static class AsyncGetAvis extends AsyncTask<String, String, Avis> {
        Integer tentative = 0;
        private int idDemandeur;
        ProgressDialog mProgressDialog;
        String messageretour;
        boolean exeption = false;
        final int idnoter;
        int idactivite;
        final int idnotateur;
        final int idpersonnenotee;
        final AsyncGetAvisListener ecouteur;
        final Context mcontext;

        public AsyncGetAvis(AsyncGetAvisListener ecouteur, int idnoter, int idactivite, int idnotateur, int idpersonnenotee, int idDemandeur, Context mcontext) {
            super();
            this.ecouteur = ecouteur;
            this.idactivite = idactivite;
            this.mcontext = mcontext;
            this.idnoter = idnoter;
            this.idactivite = idactivite;
            this.idnotateur = idnotateur;
            this.idpersonnenotee = idpersonnenotee;
            this.idDemandeur = idDemandeur;
        }

        interface AsyncGetAvisListener {
            void loopBack_GetAvis(Avis avis);
        }

        @Override
        protected Avis doInBackground(String... params) {

            do {
                try {
                    exeption = false;
                    tentative++;
                    Log.d("AsyncGetAvis", "Tentative" + tentative.toString());
                    return new Wservice().getAvis(idnoter, idactivite, idnotateur, idpersonnenotee, idDemandeur);

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    messageretour = "Problème connexion serveur";
                    exeption = true;

                } catch (XmlPullParserException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    exeption = true;

                }

            } while (tentative < nbMaxTentative && exeption == true);

            return null;

        }

        /**
         * @see AsyncTask#onPostExecute(Object)
         */
        @Override
        protected void onPostExecute(Avis avis) {
            //  activite=result;
            if (exeption) {
                mProgressDialog.dismiss();
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }

            ecouteur.loopBack_GetAvis(avis);
            mProgressDialog.dismiss();
        }

        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mcontext, "Patientez ...", "Recuperation de l'activite ...", true);
            mProgressDialog.setCancelable(true);
        }
    }


    public static class AsyncConnexionWayd extends AsyncTask<String, String, Personne> {
        Integer tentative = 0;
        String messageretour;
        boolean exeption = false;
        final AsyncConnexionWaydListener ecouteur;
        final Context mcontext;

        public AsyncConnexionWayd(AsyncConnexionWaydListener ecouteur, String token, Context mcontext) {
            super();
            this.ecouteur = ecouteur;
            this.mcontext = mcontext;

        }

        interface AsyncConnexionWaydListener {
            void loopBack_ConnexionWayd(Personne result);
        }

        @Override
        protected Personne doInBackground(String... params) {
            do {
                try {
                    exeption = false;
                    tentative++;
                    Log.d("AsyncConnexionWayd", "Tentative" + tentative.toString());

                    //    Outils.listeTutoriels.clear();
                    Outils.listtypeactivitecomplete.clear();
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    String photostr = "";
                    int nbtentative = 0;
                    Personne personneWs = null;

                    if ((mAuth.getCurrentUser().getPhotoUrl() != null) && !Outils.isConnectFromPwd()) {
                        Bitmap tmpphoto = Outils.getBitmapFromURL(mAuth.getCurrentUser().getPhotoUrl().toString());
                        Bitmap photo = Outils.redimendiensionnePhoto(tmpphoto);
                        //   recupere la photo pour profil FB ou GOOGLE
                        photostr = Outils.encodeTobase64(photo);
                    }

                //    if (Outils.isConnectFromPwd())

                 //   {
                        String gcmToken = FirebaseInstanceId.getInstance().getToken();
                    //    new Wservice().testJeton(Outils.jeton, photostr, mAuth.getCurrentUser().getDisplayName(), gcmToken);

                        // Si je ne suis pas connecte FB ou GOOGLE je ne mets pas à jour la photo issu de Firebase
                        // on met la variable noupdatephoto pour n
                        //
                //    } else {

                //        String gcmToken = FirebaseInstanceId.getInstance().getToken();
                //        new Wservice().testJeton(Outils.jeton, photostr, mAuth.getCurrentUser().getDisplayName(), gcmToken);

               //     }


                    while (nbtentative < 5 && personneWs == null) {
                        //    Thread.sleep(1000);// Pause 1 seconde pour laisser le temps de creer le compte la premiere fois à optimiser avec gcm
                    //   personneWs = new Wservice().getPersonnebyToken(Outils.jeton);//
                        personneWs = new Wservice().getPersonne(Outils.jeton, photostr, mAuth.getCurrentUser().getDisplayName(), gcmToken);//
                       Log.d("couc","coucou*******************  "+personneWs.getPseudo());
                        if (personneWs != null) {
                            Outils.listtypeactivitecomplete.addAll(new Wservice().getListTypeActivite(personneWs.getId()));
                            Outils.DERNIERE_VERSION_WAYD = new Wservice().getVersion(personneWs.getId());
                            Log.d("Version version", "***************************************************" + Outils.DERNIERE_VERSION_WAYD.getVersion());
                            Log.d("Version version", "" + Outils.DERNIERE_VERSION_WAYD.getMajeur());
                            Log.d("Version version", "" + Outils.DERNIERE_VERSION_WAYD.getMineur());

                            return new Personne(personneWs);
                        }
                        nbtentative++;
                        Thread.sleep(30);

                    }


                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    messageretour = "Problème connexion serveur";
                    exeption = true;

                } catch (XmlPullParserException | InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    exeption = true;

                }
            } while (tentative < nbMaxTentative && exeption == true);
            return null;

        }

        /**
         * @see AsyncTask#onPostExecute(Object)
         */
        @Override
        protected void onPostExecute(Personne personne) {
            //  activite=result;
            if (exeption) {
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                ecouteur.loopBack_ConnexionWayd(null);
                return;
            }

            ecouteur.loopBack_ConnexionWayd(personne);


            //    mProgressDialog.dismiss();
        }

        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            //    mProgressDialog = ProgressDialog.show(mcontext, "Patientez ...", "Connexion...", true);
            //   mProgressDialog.setCancelable(true);
        }
    }


    public static class AsyncGetIndicateursWayd extends AsyncTask<String, String, IndicateurWayd> {
        Integer tentative = 0;
        ProgressDialog mProgressDialog;
        String messageretour;
        boolean exeption = false;
        final AsyncGetIndicateurListener ecouteur;
        final Context mcontext;

        public AsyncGetIndicateursWayd(AsyncGetIndicateurListener ecouteur, Context mcontext) {
            super();
            this.ecouteur = ecouteur;
            this.mcontext = mcontext;

        }

        interface AsyncGetIndicateurListener {
            void loopBack_GetIndicateur(IndicateurWayd indicateurWayd);
        }

        @Override
        protected IndicateurWayd doInBackground(String... params) {

            do {
                try {
                    exeption = false;
                    tentative++;
                    Log.d("AsyncGetIndicateursWayd", "Tentative" + tentative.toString());
                    return new Wservice().getIndicateurs();

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    messageretour = "Problème connexion serveur";
                    exeption = true;

                } catch (XmlPullParserException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    exeption = true;

                }
            }
            while (tentative < nbMaxTentative && exeption == true);
            return null;

        }

        /**
         * @see AsyncTask#onPostExecute(Object)
         */
        @Override
        protected void onPostExecute(IndicateurWayd indicateurWayd) {
            //  activite=result;
            if (exeption) {
                mProgressDialog.dismiss();
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }

            ecouteur.loopBack_GetIndicateur(indicateurWayd);
            mProgressDialog.dismiss();
        }

        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mcontext, "Patientez ...", "Recuperation des statistiques ...", true);
            mProgressDialog.setCancelable(true);
        }
    }

    public static class AsyncAddSuggestion extends AsyncTask<String, String, MessageServeur> {
        Integer tentative = 0;
        ProgressDialog mProgressDialog;
        String messageretour;
        boolean exeption = false;
        final Async_AddSuggestionListener ecouteur;
        final String suggestion;
        final int idpersonne;
        final Context mcontext;

        public AsyncAddSuggestion(Async_AddSuggestionListener ecouteur, String suggestion, int idpersonne, Context mcontext) {
            super();
            this.ecouteur = ecouteur;
            this.mcontext = mcontext;
            this.idpersonne = idpersonne;
            this.suggestion = suggestion;

        }

        interface Async_AddSuggestionListener {
            void loopBack_AddSuggestion(MessageServeur messageserveur);
        }

        @Override
        protected MessageServeur doInBackground(String... params) {


            do {
                try {
                    exeption = false;
                    tentative++;
                    Log.d("AsyncAddSuggestion", "Tentative" + tentative.toString());

                    return new Wservice().addSuggestion(suggestion, idpersonne);
                } catch (IOException e) {
                    e.printStackTrace();
                    exeption = true;
                    messageretour = "Probleme connexion";

                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                    exeption = true;
                }
            } while (tentative < nbMaxTentative && exeption == true);

            return null;

        }

        @Override
        protected void onPostExecute(MessageServeur messageserveur) {
            if (exeption) {
                mProgressDialog.dismiss();
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }

            ecouteur.loopBack_AddSuggestion(messageserveur);

            mProgressDialog.dismiss();
        }

        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {

            mProgressDialog = ProgressDialog.show(mcontext, "Patientez ...", "Propose l'activité ...", true);
            mProgressDialog.setCancelable(true);
        }

    }

    public static class AsyncGetTdb extends AsyncTask<String, String, TableauBord> {

        ProgressDialog mProgressDialog;
        String messageretour;
        boolean exeption = false;
        final int idpersonne;
        final AsyncGetTdbListener ecouteur;


        public AsyncGetTdb(AsyncGetTdbListener ecouteur, int idpersonne) {
            super();
            this.ecouteur = ecouteur;

            this.idpersonne = idpersonne;
        }

        public interface AsyncGetTdbListener {
            void loopBack_GetTdb(TableauBord tableauDeBord);
        }

        @Override
        protected TableauBord doInBackground(String... params) {

            try {
                return new Wservice().getTableauBord(idpersonne);

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                messageretour = "Problème connexion serveur";
                exeption = true;

            } catch (XmlPullParserException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                exeption = true;

            }

            return null;

        }

        /**
         * @see AsyncTask#onPostExecute(Object)
         */
        @Override
        protected void onPostExecute(TableauBord tableauDeBord) {
            //  activite=result;
            if (exeption) {

                return;
            }

            ecouteur.loopBack_GetTdb(tableauDeBord);


        }

        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {

        }
    }


    public static class AsyncEffaceDiscussion extends AsyncTask<String, String, MessageServeur> {
        Integer tentative = 0;
        ProgressDialog mProgressDialog;
        boolean exeption = false;
        final Async_EffaceDiscussionListener ecouteur;
        final int idpersonne;
        final int idEmetteurDiscution;
        final Context mcontext;

        public AsyncEffaceDiscussion(Async_EffaceDiscussionListener ecouteur, int idpersonne, int idEmetteurDiscution, Context mcontext) {
            super();
            this.ecouteur = ecouteur;
            this.idpersonne = idpersonne;
            this.mcontext = mcontext;
            this.idEmetteurDiscution = idEmetteurDiscution;
        }

        @Override
        protected MessageServeur doInBackground(String... params) {

            do {
                try {
                    exeption = false;
                    tentative++;
                    Log.d("AsyncEffaceDiscussion", "Tentative" + tentative.toString());
                    return new Wservice().effaceDiscussion(idpersonne, idEmetteurDiscution);
                } catch (IOException | XmlPullParserException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    exeption = true;
                }
            } while (tentative < nbMaxTentative && exeption == true);
            return null;

        }

        /**
         * @see AsyncTask#onPostExecute(Object)
         */
        @Override
        protected void onPostExecute(MessageServeur result) {

            if (exeption) {
                mProgressDialog.dismiss();
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }

            if (result != null)
                ecouteur.loopBack_EffaceDiscussion(result);

            mProgressDialog.dismiss();

        }

        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mcontext, "Patientez ...", "Mise à jour ...", true);
            mProgressDialog.setCancelable(true);
        }

        interface Async_EffaceDiscussionListener {
            void loopBack_EffaceDiscussion(MessageServeur messageserveur);
        }
    }

    public static class AsyncGetListPhotoWaydeur extends AsyncTask<String, String, ArrayList<PhotoWaydeur>> {
        Integer tentative = 0;
        boolean exeption = false;
        final Async_GetListPhotoWaydeurListener ecouteur;
        final List<Integer> listpersonne;
        final Context mcontext;

        public AsyncGetListPhotoWaydeur(Async_GetListPhotoWaydeurListener ecouteur, List<Integer> listpersonne, Context mcontext) {
            super();
            this.ecouteur = ecouteur;
            this.listpersonne = listpersonne;
            this.mcontext = mcontext;
        }

        @Override
        protected ArrayList<PhotoWaydeur> doInBackground(String... params) {
            do {
                try {
                    exeption = false;
                    tentative++;
                    Log.d("AsyncGetListPhotoWa", "Tentative" + tentative.toString());
                    return new Wservice().getListPhotoWaydeur(listpersonne);
                } catch (IOException | XmlPullParserException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    exeption = true;
                }
            } while (tentative < nbMaxTentative && exeption == true);

            return null;

        }

        /**
         * @see AsyncTask#onPostExecute(Object)
         */
        @Override
        protected void onPostExecute(ArrayList<PhotoWaydeur> result) {

            if (exeption) {
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            ecouteur.loopBack_GetListProfilDiscussion(result);
        }

        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
        }

        interface Async_GetListPhotoWaydeurListener {
            void loopBack_GetListProfilDiscussion(ArrayList<PhotoWaydeur> listPhotoWaydeur);
        }
    }


    public static class AsyncGetPhotoWaydeur extends AsyncTask<String, String, PhotoWaydeur> {
        Integer tentative = 0;
        String messageretour;
        boolean exeption = false;
        final int idpersonne;
        final AsyncGetPhotoWaydeurListener ecouteur;
        final Context mcontext;

        public AsyncGetPhotoWaydeur(AsyncGetPhotoWaydeurListener ecouteur, int idpersonne, Context mcontext) {
            super();
            this.ecouteur = ecouteur;
            this.mcontext = mcontext;
            this.idpersonne = idpersonne;
        }

        interface AsyncGetPhotoWaydeurListener {
            void loopBack_GetProfilDiscussion(PhotoWaydeur photoWaydeur);
        }

        @Override
        protected PhotoWaydeur doInBackground(String... params) {

            do {
                try {
                    exeption = false;
                    tentative++;
                    Log.d("AsyncGetPhotoWaydeur", "Tentative" + tentative.toString());
                    return new Wservice().getPhotoWaydeur(idpersonne);

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    messageretour = "Problème connexion serveur";
                    exeption = true;

                } catch (XmlPullParserException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    exeption = true;

                }
            } while (tentative < nbMaxTentative && exeption == true);

            return null;

        }

        /**
         * @see AsyncTask#onPostExecute(Object)
         */
        @Override
        protected void onPostExecute(PhotoWaydeur photoWaydeur) {
            //  activite=result;
            if (exeption) {
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }

            ecouteur.loopBack_GetProfilDiscussion(photoWaydeur);


        }

        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {

        }
    }

    public static class AsyncGetProfilFull extends AsyncTask<String, String, Profil> {
        Integer tentative = 0;
        ProgressDialog mProgressDialog;
        String messageretour;
        boolean exeption = false;
        final int idpersonne;
        final Async_GetProfilListenerFull ecouteur;
        final Context mcontext;
        Profil retourProfil = null;
        ArrayList<Avis> retourListAvis;


        public AsyncGetProfilFull(Async_GetProfilListenerFull ecouteur, int idpersonne, Context mcontext) {
            super();
            this.ecouteur = ecouteur;
            this.idpersonne = idpersonne;
            this.mcontext = mcontext;
        }

        interface Async_GetProfilListenerFull {
            void loopBack_GetProfilFull(Profil profil, ArrayList<Avis> listAvis);
        }

        @Override
        protected Profil doInBackground(String... params) {

            do {
                try {
                    exeption = false;
                    tentative++;
                    Log.d("AsyncGetProfilFull", "Tentative" + tentative.toString());
                    retourProfil = new Wservice().getFullProfil(idpersonne);
                    retourListAvis = new ArrayList<>(new Wservice().getListAvis(idpersonne));
                } catch (IOException e) {
                    e.printStackTrace();
                    exeption = true;
                    messageretour = "Probleme connexion";

                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                    exeption = true;
                }
            } while (tentative < nbMaxTentative && exeption == true);

            return retourProfil;

        }

        @Override
        protected void onPostExecute(Profil result) {
            if (exeption) {
                mProgressDialog.dismiss();
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }

            if (result != null) {

                ecouteur.loopBack_GetProfilFull(retourProfil, retourListAvis);

            } else {

                Toast toast = Toast.makeText(mcontext, "Une erreur inconnue est survenue", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
            mProgressDialog.dismiss();
        }


        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mcontext, "Patientez ...", "Chargement du profil ...", true);
            mProgressDialog.setCancelable(true);

        }

    }

    public static class AsyncGetDonneAvisFull extends AsyncTask<String, String, ProfilNotation> {
        Integer tentative = 0;
        ProgressDialog mProgressDialog;
        String messageretour;
        boolean exeption = false;
        final int idpersonne;
        final int idPersonneNotee;
        final int idActivite;
        final Async_GetDonneAvisListenerFull ecouteur;
        final Context mcontext;
        ProfilNotation profilNotation;
        Activite activite;


        public AsyncGetDonneAvisFull(Async_GetDonneAvisListenerFull ecouteur, int idpersonne,
                                     int idPersonneNotee, int idActivite, Context mcontext) {
            super();
            this.ecouteur = ecouteur;
            this.idpersonne = idpersonne;
            this.idPersonneNotee = idPersonneNotee;
            this.idActivite = idActivite;
            this.mcontext = mcontext;
        }

        interface Async_GetDonneAvisListenerFull {
            void loopBack_GetDonneAvisFull(ProfilNotation profil, Activite activite);
        }

        @Override
        protected ProfilNotation doInBackground(String... params) {
            do {
                try {
                    exeption = false;
                    tentative++;
                    Log.d("AsyncGetDonneAvisFull", "Tentative" + tentative.toString());
                    profilNotation = new Wservice().getProfilNotation(idpersonne, idPersonneNotee, idActivite);
                    activite = new Wservice().getActivite(idActivite);
                } catch (IOException e) {
                    e.printStackTrace();
                    exeption = true;
                    messageretour = "Probleme connexion";

                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                    exeption = true;
                }
            } while (tentative < nbMaxTentative && exeption == true);

            return profilNotation;

        }

        @Override
        protected void onPostExecute(ProfilNotation result) {
            if (exeption) {
                mProgressDialog.dismiss();
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }

            if (result != null && activite != null) {

                ecouteur.loopBack_GetDonneAvisFull(profilNotation, activite);

            } else {
                Toast toast = Toast.makeText(mcontext, "Une erreur inconnue est survenue", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
            mProgressDialog.dismiss();
        }

        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mcontext, "Patientez ...", "Chargement ...", true);
            mProgressDialog.setCancelable(true);
        }

    }


    public static class AsyncGetListMessageFull extends AsyncTask<String, String, ArrayList<Message>> {
        //Renvoi les message et les photos.
        Integer tentative = 0;
        ProgressDialog mProgressDialog;
        String messageretour;
        boolean exeption = false;
        final AsyncGetListMessageListenerFull ecouteur;
        final int emetteur;
        final int destinataire;
        final Context mcontext;
        ArrayList<PhotoWaydeur> listPhotoWaydeur;

        public AsyncGetListMessageFull(AsyncGetListMessageListenerFull ecouteur, int emetteur, int destinataire, Context mcontext) {
            super();
            this.ecouteur = ecouteur;
            this.emetteur = emetteur;
            this.destinataire = destinataire;
            this.mcontext = mcontext;

        }

        @Override

        protected ArrayList<Message> doInBackground(String... params) {

            do {
                try {
                    exeption = false;
                    tentative++;
                    Log.d("AsyncGetListMessageFull", "Tentative" + tentative.toString());
                    // *****************  Recuepre les photos
                    ArrayList<Integer> listId = new ArrayList<>();
                    listId.add(emetteur);
                    listId.add(destinataire);
                    listPhotoWaydeur = new ArrayList<>(new Wservice().getListPhotoWaydeur(listId));

                    //**********************

                    return (new Wservice().getDiscussion(emetteur, destinataire));

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    messageretour = "Echec connexion serveur ";
                    exeption = true;
                } catch (XmlPullParserException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    exeption = true;

                }
            } while (tentative < nbMaxTentative && exeption == true);

            return null;


        }

        /**
         * @see AsyncTask#onPostExecute(Object)
         */
        @Override
        protected void onPostExecute(ArrayList<Message> listeMessage) {

            if (exeption) {
                mProgressDialog.dismiss();
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            ecouteur.loopBack_GetListeMessageFull(listeMessage, listPhotoWaydeur);
            mProgressDialog.dismiss();

        }

        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mcontext, "Patientez ...", "Chargement des messages...", true);
            mProgressDialog.setCancelable(false);

        }


        interface AsyncGetListMessageListenerFull {
            void loopBack_GetListeMessageFull(ArrayList<Message> listeMessage, ArrayList<PhotoWaydeur> listPhotoWaydeur);
        }
    }

    public static class AsyncGetActiviteFull extends AsyncTask<String, String, Activite> {
        // Renvoi le detail de l'activite ainsi que tous les participants
        Integer tentative = 0;
        ProgressDialog mProgressDialog;
        String messageretour;
        boolean exeption = false;
        final int idactivite;
        final Async_GetActiviteFullListener ecouteur;
        final Context mcontext;
        Activite activite;
        boolean afficheProgress;
        ArrayList<Participant> listParticipant;


        public AsyncGetActiviteFull(Async_GetActiviteFullListener ecouteur, int idactivite, boolean afficheProgress, Context mcontext) {
            super();
            this.ecouteur = ecouteur;
            this.idactivite = idactivite;
            this.mcontext = mcontext;
            this.afficheProgress = afficheProgress;
        }

        interface Async_GetActiviteFullListener {
            void loopBack_GetActiviteFull(Activite activite, ArrayList<Participant> listParticipant);
        }

        @Override
        protected Activite doInBackground(String... params) {
            do {
                try {
                    exeption = false;
                    tentative++;
                    activite = new Wservice().getActivite(idactivite);
                    listParticipant = new ArrayList<>(new Wservice().getListParticipants(idactivite));
                    Log.d("AsyncGetActiviteFull", "Tentative" + tentative.toString());

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    messageretour = "Problème connexion serveur";
                    exeption = true;

                } catch (XmlPullParserException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    exeption = true;
                }


            } while (tentative < nbMaxTentative && exeption == true);

            return activite;// Retour
        }

        /**
         * @see AsyncTask#onPostExecute(Object)
         */
        @Override
        protected void onPostExecute(Activite act) {

            if (exeption) {

                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

                if (afficheProgress)
                    if (mProgressDialog != null)
                        mProgressDialog.dismiss();
                return;
            }

            ecouteur.loopBack_GetActiviteFull(activite, listParticipant);// Envoi la list des participant et l'activite
            if (afficheProgress)
                mProgressDialog.dismiss();

        }

        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            if (afficheProgress) {
                mProgressDialog = ProgressDialog.show(mcontext, "Patientez ...", "Récuperation de l'activite ...", true);
                mProgressDialog.setCancelable(true);
            }
        }
    }


    public static class AsyncSignalActivite extends AsyncTask<String, String, MessageServeur> {


        Integer tentative = 0;
        ProgressDialog mProgressDialog;
        String messageretour;
        final String titre;
        final String libelle;
        boolean exeption = false;

        int idpersonne = 0;
        int idactivite = 0;
        final Async_SignalActiviteListener ecouteur;
        int idmotif = 0;
        String motif = "";


        final Context mcontext;


        public AsyncSignalActivite(Async_SignalActiviteListener ecouteur, int idpersonne, int idactivite, int idmotif,
                                   String motif, String titre, String libelle, Context mcontext) {
            super();
            this.ecouteur = ecouteur;
            this.idpersonne = idpersonne;
            this.idactivite = idactivite;
            this.idmotif = idmotif;
            this.motif = motif;
            this.titre = titre;
            this.libelle = libelle;
            this.mcontext = mcontext;


        }

        interface Async_SignalActiviteListener {
            void loopBack_SignalActivite(MessageServeur messageserveur);
        }

        @Override
        protected MessageServeur doInBackground(String... params) {
            do {
                try {
                    exeption = false;
                    tentative++;
                    Log.d("AsyncSignalActivite", "Tentative" + tentative.toString());
                    return new Wservice().signalActivite(idpersonne, idactivite, idmotif,
                            motif, titre, libelle);
                } catch (IOException e) {
                    e.printStackTrace();
                    exeption = true;
                    messageretour = "Problème connexion";

                } catch (XmlPullParserException e) {
                    exeption = true;
                    e.printStackTrace();
                }
            } while (tentative < nbMaxTentative && exeption == true);

            return null;

        }

        @Override
        protected void onPostExecute(MessageServeur messageserveur) {
            if (exeption) {
                mProgressDialog.dismiss();
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }

            ecouteur.loopBack_SignalActivite(messageserveur);
            mProgressDialog.dismiss();
        }

        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {

            mProgressDialog = ProgressDialog.show(mcontext, "Patientez ...", "Propose l'activité ...", true);
            mProgressDialog.setCancelable(true);
        }

    }

    public static class AsyncSignalProfil extends AsyncTask<String, String, MessageServeur> {

        ProgressDialog mProgressDialog;
        String messageretour;
        boolean exeption = false;
        Integer tentative = 0;
        int idpersonne = 0;
        int idsignalement = 0;
        final Async_SignalProfilListener ecouteur;
        int idmotif = 0;
        String motif = "";


        final Context mcontext;


        public AsyncSignalProfil(Async_SignalProfilListener ecouteur, int idpersonne, int idsignalement, int idmotif,
                                 String motif, Context mcontext) {
            super();
            this.ecouteur = ecouteur;
            this.idpersonne = idpersonne;
            this.idsignalement = idsignalement;
            this.idmotif = idmotif;
            this.motif = motif;
            this.mcontext = mcontext;


        }

        interface Async_SignalProfilListener {
            void loopBack_SignalProfil(MessageServeur messageserveur);
        }

        @Override
        protected MessageServeur doInBackground(String... params) {

            do {
                try {
                    exeption = false;
                    tentative++;
                    Log.d("AsyncSignalProfil", "Tentative" + tentative.toString());
                    return new Wservice().signalProfil(idpersonne, idsignalement, idmotif,
                            motif);
                } catch (IOException e) {
                    e.printStackTrace();
                    exeption = true;
                    messageretour = "Probleme connexion";

                } catch (XmlPullParserException e) {
                    exeption = true;
                    e.printStackTrace();
                }
            } while (tentative < nbMaxTentative && exeption == true);

            return null;

        }

        @Override
        protected void onPostExecute(MessageServeur messageserveur) {
            if (exeption) {
                mProgressDialog.dismiss();
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }

            ecouteur.loopBack_SignalProfil(messageserveur);
            mProgressDialog.dismiss();
        }

        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {

            mProgressDialog = ProgressDialog.show(mcontext, "Patientez ...", "Propose l'activité ...", true);
            mProgressDialog.setCancelable(true);
        }

    }

    public static class AsyncAddPrbConnexion extends AsyncTask<String, String, MessageServeur> {

        Integer tentative = 0;
        ProgressDialog mProgressDialog;
        String messageretour;
        boolean exeption = false;
        final AsyncAddPrbConnexionListener ecouteur;
        final String probleme, email;

        final Context mcontext;

        public AsyncAddPrbConnexion(AsyncAddPrbConnexionListener ecouteur, String probleme, String email, Context mcontext) {
            super();
            this.ecouteur = ecouteur;
            this.mcontext = mcontext;
            this.probleme = probleme;
            this.email = email;

        }

        interface AsyncAddPrbConnexionListener {
            void loopBack_AddPrbConnexion(MessageServeur messageserveur);
        }

        @Override
        protected MessageServeur doInBackground(String... params) {

            do {
                try {
                    exeption = false;
                    tentative++;
                    Log.d("AsyncAddPrbConnexion", "Tentative" + tentative.toString());
                    return new Wservice().addPrbConnexion(probleme, email);
                } catch (IOException e) {
                    e.printStackTrace();
                    exeption = true;
                    messageretour = "Probleme connexion";

                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                    exeption = true;
                }
            } while (tentative < nbMaxTentative && exeption == true);

            return null;
        }

        @Override
        protected void onPostExecute(MessageServeur messageserveur) {
            if (exeption) {
                mProgressDialog.dismiss();
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }

            ecouteur.loopBack_AddPrbConnexion(messageserveur);

            mProgressDialog.dismiss();
        }

        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {

            mProgressDialog = ProgressDialog.show(mcontext, "Patientez ...", "Envoi en cours ...", true);
            mProgressDialog.setCancelable(true);
        }

    }

    public static class AsyncUpdateActivite extends AsyncTask<String, String, MessageServeur> {

        Integer tentative = 0;
        boolean exeption = false;
        final AsyncUpdateActiviteListener ecouteur;
        int idpersonne, idactivite, nbmaxWaydeurs;
        String titre, libelle;
        final Context mcontext;

        public AsyncUpdateActivite(AsyncUpdateActiviteListener ecouteur, int idpersonne, int idactivite, String titre, String libelle, int nbmaxWaydeurs, Context mcontext) {
            super();

            this.ecouteur = ecouteur;
            this.idpersonne = idpersonne;
            this.mcontext = mcontext;
            this.idpersonne = idpersonne;
            this.idactivite = idactivite;
            this.titre = titre;
            this.libelle = libelle;
            this.nbmaxWaydeurs = nbmaxWaydeurs;

        }

        @Override
        protected MessageServeur doInBackground(String... params) {

            do {
                try {
                    exeption = false;
                    tentative++;
                    Log.d("AsyncUpdateActivite", "Tentative" + tentative.toString());

                    return new Wservice().updateActivite(idpersonne, idactivite, titre, libelle, nbmaxWaydeurs);
                } catch (IOException | XmlPullParserException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    exeption = true;
                }
            } while (tentative < nbMaxTentative && exeption == true);

            return null;

        }

        /**
         * @see AsyncTask#onPostExecute(Object)
         */
        @Override
        protected void onPostExecute(MessageServeur result) {

            if (exeption) {
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                ecouteur.loopBack_UpdateActivite(null, null, null, 0);//
                return;
            }

            ecouteur.loopBack_UpdateActivite(result, titre, libelle, nbmaxWaydeurs);

        }

        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
        }

        interface AsyncUpdateActiviteListener {
            void loopBack_UpdateActivite(MessageServeur messageserveur, String titre, String libelle, int maxWaydeurs);
        }
    }


    public static class AsyncUpdateGCM extends AsyncTask<String, String, MessageServeur> {

        Integer tentative = 0;
        boolean exeption = false;
        final AsyncUpdateGCMListener ecouteur;
        final int idPersonne;
        final String gcmToken;
        final Context mcontext;

        public AsyncUpdateGCM(AsyncUpdateGCMListener ecouteur, int idPersonne, String gcmToken, Context mcontext) {
            super();

            this.ecouteur = ecouteur;
            this.idPersonne = idPersonne;
            this.mcontext = mcontext;
            this.gcmToken = gcmToken;

        }

        @Override
        protected MessageServeur doInBackground(String... params) {

            do {
                try {
                    exeption = false;
                    tentative++;
                    Log.d("AsyncUpdateGCM", "Tentative" + tentative.toString());

                    return new Wservice().updateGCM(idPersonne, gcmToken);
                } catch (IOException | XmlPullParserException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    exeption = true;
                }
            } while (tentative < nbMaxTentative && exeption == true);

            return null;

        }

        /**
         * @see AsyncTask#onPostExecute(Object)
         */
        @Override
        protected void onPostExecute(MessageServeur result) {

            if (exeption) {
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            ecouteur.loopBack_UpdateGCM(result);
        }

        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
        }

        interface AsyncUpdateGCMListener {
            void loopBack_UpdateGCM(MessageServeur messageserveur);
        }
    }


    public static class AsyncUpdateNotificationPref extends AsyncTask<String, String, MessageServeur> {
        Integer tentative = 0;
        boolean exeption = false;
        final AsyncUpdateNotificationPrefListener ecouteur;
        final int idPersonne;
        final boolean notification;
        final Context mcontext;

        public AsyncUpdateNotificationPref(AsyncUpdateNotificationPrefListener ecouteur, int idPersonne, boolean notification, Context mcontext) {
            super();

            this.ecouteur = ecouteur;
            this.idPersonne = idPersonne;
            this.mcontext = mcontext;
            this.notification = notification;

        }

        @Override
        protected MessageServeur doInBackground(String... params) {

            do {
                try {
                    exeption = false;
                    tentative++;
                    Log.d("AsyncUpdateNotifica", "Tentative" + tentative.toString());
                    return new Wservice().updateNotificationPref(idPersonne, notification);
                } catch (IOException | XmlPullParserException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    exeption = true;
                }

            } while (tentative < nbMaxTentative && exeption == true);
            return null;

        }

        /**
         * @see AsyncTask#onPostExecute(Object)
         */
        @Override
        protected void onPostExecute(MessageServeur result) {

            if (exeption) {
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                ecouteur.loopBack_UpdateNotificationPref(null, notification);
                return;
            }
            ecouteur.loopBack_UpdateNotificationPref(result, notification);
        }

        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
        }

        interface AsyncUpdateNotificationPrefListener {
            void loopBack_UpdateNotificationPref(MessageServeur messageserveur, boolean notification);
        }
    }


    public static class AsyncAddActivitePro extends AsyncTask<String, String, MessageServeur> {
        Integer tentative = 0;
        ProgressDialog mProgressDialog;
        String messageretour;
        boolean exeption = false;
        final Async_AddActiviteProListener ecouteur;
        final String titre;
        final String libelle;
        final String adresse;
        final String jeton;
        final int idorganisateur;
        final int idtypeactivite;
        final double latitude;
        final double longitude;
        final Long datedebut, datefin;
        final Context mcontext;


        public AsyncAddActivitePro(Async_AddActiviteProListener ecouteur, String titre, String libelle,
                                   int idorganisateur, int idtypeactivite, double latitude, double longitude, String adresse
                , Long datedebut, Long datefin, String jeton, Context mcontext) {
            super();
            this.ecouteur = ecouteur;
            this.titre = titre;
            this.libelle = libelle;
            this.idorganisateur = idorganisateur;
            this.idtypeactivite = idtypeactivite;
            this.latitude = latitude;
            this.longitude = longitude;
            this.adresse = adresse;
            this.datedebut = datedebut;
            this.datefin = datefin;
            this.mcontext = mcontext;
            this.jeton = jeton;

        }

        interface Async_AddActiviteProListener {
            void loopBack_AddActivitePro(MessageServeur messageserveur);
        }

        @Override
        protected MessageServeur doInBackground(String... params) {


            do {
                try {
                    exeption = false;
                    tentative++;
                    Log.d("AsyncAddActivite", "Tentative" + tentative.toString());

                    return new Wservice().addActivitePro(titre, libelle,
                            idorganisateur, idtypeactivite, latitude, longitude, adresse, datedebut, datefin
                            , jeton);
                } catch (IOException e) {
                    e.printStackTrace();
                    exeption = true;
                    messageretour = "Probleme connexion";

                } catch (XmlPullParserException e) {
                    exeption = true;
                    e.printStackTrace();
                }

            } while (tentative < nbMaxTentative && exeption == true);
            return null;

        }

        @Override
        protected void onPostExecute(MessageServeur messageserveur) {
            if (exeption) {
                mProgressDialog.dismiss();
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }


            ecouteur.loopBack_AddActivitePro(messageserveur);

            mProgressDialog.dismiss();
        }

        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {

            mProgressDialog = ProgressDialog.show(mcontext, "Patientez ...", "Propose l'activité ...", true);
            mProgressDialog.setCancelable(true);
        }

    }


    public static class AsyncUpdateProfilPro extends AsyncTask<String, String, MessageServeur> {
        Integer tentative = 0;
        ProgressDialog mProgressDialog;
        boolean exeption = false;
        final AsyncUpdateProfilProListener ecouteur;
        final int idpersonne;
        final Bitmap photo;
        String siteweb;
        String siret;
        String telephone;
        final String pseudo;
        final String commentaire;


        final Context mcontext;

        public AsyncUpdateProfilPro(AsyncUpdateProfilProListener ecouteur, Bitmap photo,
                                    String pseudo, String telephone, String siret, String siteweb,
                                    String commentaire, int idpersonne, Context mcontext) {
            super();
            this.ecouteur = ecouteur;
            this.idpersonne = idpersonne;
            this.photo = photo;
            this.pseudo = pseudo;
            this.commentaire = commentaire;
            this.siret = siret;
            this.telephone = telephone;
            this.siteweb = siteweb;
            this.mcontext = mcontext;

        }

        @Override
        protected MessageServeur doInBackground(String... params) {

            do {
                try {
                    exeption = false;
                    tentative++;
                    Log.d("AsyncUpdateProfilPro", "Tentative" + tentative.toString());
                    return new Wservice().updateProfilPro(photo,
                            pseudo, telephone, siret, siteweb,
                            commentaire, idpersonne);
                } catch (IOException | XmlPullParserException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    exeption = true;
                }
            } while (tentative < nbMaxTentative && exeption == true);

            return null;

        }

        /**
         * @see AsyncTask#onPostExecute(Object)
         */
        @Override
        protected void onPostExecute(MessageServeur result) {

            if (exeption) {
                mProgressDialog.dismiss();
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }

            ecouteur.loopBack_UpdateProfilPro(result);

            mProgressDialog.dismiss();

        }

        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mcontext, "Patientez ...", "Mise à jour du profil ...", true);
            mProgressDialog.setCancelable(true);
        }

        interface AsyncUpdateProfilProListener {
            void loopBack_UpdateProfilPro(MessageServeur messageserveur);
        }
    }

    public static class AsyncGetProfilPro extends AsyncTask<String, String, ProfilPro> {
        //    ProgressDialog mProgressDialog;
        Integer tentative = 0;
        String messageretour;
        boolean exeption = false;
        final int idpersonne;
        final Async_GetProfilProListener ecouteur;
        final Context mcontext;

        public AsyncGetProfilPro(Async_GetProfilProListener ecouteur, int idpersonne, Context mcontext) {
            super();
            this.ecouteur = ecouteur;
            this.idpersonne = idpersonne;
            this.mcontext = mcontext;
        }

        interface Async_GetProfilProListener {
            void loopBack_GetProfilPro(ProfilPro profil);
        }

        @Override
        protected ProfilPro doInBackground(String... params) {

            do {
                try {
                    exeption = false;
                    tentative++;
                    Log.d("AsyncGetProfilPro", "Tentative" + tentative.toString());
                    return new Wservice().getFullProfilPro(idpersonne);
                } catch (IOException e) {
                    e.printStackTrace();
                    exeption = true;
                    messageretour = "Problème connexion";

                } catch (XmlPullParserException e) {
                    exeption = true;
                    e.printStackTrace();
                }
            } while (tentative < nbMaxTentative && exeption == true);


            return null;

        }

        @Override
        protected void onPostExecute(ProfilPro result) {
            if (exeption) {
                //          mProgressDialog.dismiss();
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }

            if (result != null) {
                ecouteur.loopBack_GetProfilPro(result);
            } else {
                Toast toast = Toast.makeText(mcontext, "Erreur inconnue", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
            //    mProgressDialog.dismiss();
        }


        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            // mProgressDialog = ProgressDialog.show(mcontext, "Patientez ...", "Chargement du profil ...", true);
            //     mProgressDialog.setCancelable(true);


        }

    }


    public static class AsyncAddInteret extends AsyncTask<String, String, MessageServeur> {
        private final int idpersonne;
        private final int typeInteret;
        Integer tentative = 0;
        ProgressDialog mProgressDialog;
        boolean exeption = false;
        final Async_AddInteretListener ecouteur;
        final int idactivite;

        final Context mcontext;

        public AsyncAddInteret(Async_AddInteretListener ecouteur, int idpersonne,int idactivite,int typeInteret, Context mcontext) {
            super();
            this.ecouteur = ecouteur;
            this.idactivite=idactivite;
            this.idpersonne=idpersonne;
            this.typeInteret=typeInteret;
            this.mcontext = mcontext;
        }

        interface Async_AddInteretListener {
            void loopBack_AddInteret(MessageServeur messageserveur);
        }


        @Override
        protected MessageServeur doInBackground(String... params) {

            do {
                try {
                    exeption = false;
                    tentative++;
                     return (new Wservice().addInteret(idpersonne,idactivite,typeInteret));

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();

                    exeption = true;

                } catch (XmlPullParserException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    exeption = true;

                }
            } while (tentative < nbMaxTentative && exeption == true);

            return null;

        }

        /**
         * @see AsyncTask#onPostExecute(Object)
         */
        @Override
        protected void onPostExecute(MessageServeur result) {

            if (exeption) {
                mProgressDialog.dismiss();
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }

            if (result != null)
                ecouteur.loopBack_AddInteret(result);

            else {

                Toast toast = Toast.makeText(mcontext, "Pas de retour serveur", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
            mProgressDialog.dismiss();

        }

        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mcontext, "Patientez ...", "Ajoute votre participation...", true);
            mProgressDialog.setCancelable(true);

        }
    }

    public static class AsyncGetListPhotoActivite extends AsyncTask<String, Integer, ArrayList<PhotoActivite>> {
        Integer tentative = 0;
        ProgressDialog mProgressDialog;
        String messageretour;
        boolean exeption = false;
        final Async_GetListPhotoActiviteListener ecouteur;
        final int idactivite;
        final Context mcontext;
        final boolean afficeProgress;

        public AsyncGetListPhotoActivite(Async_GetListPhotoActiviteListener ecouteur, int idactivite, Context mcontext, boolean afficheProgress) {
            super();
            this.ecouteur = ecouteur;
            this.idactivite = idactivite;
            this.mcontext = mcontext;
            this.afficeProgress = afficheProgress;

        }

        @Override

        protected ArrayList<PhotoActivite> doInBackground(String... params) {

            do {
                try {
                    exeption = false;
                    tentative++;
                    return (new Wservice().getListPhotoActivite(idactivite));
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    messageretour = "Echec connexion serveur ";
                    exeption = true;
                    publishProgress(tentative);
                } catch (XmlPullParserException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    exeption = true;

                }
            } while (tentative < nbMaxTentative && exeption == true);

            return null;


        }

        // Permet d'afficher dans la  progress bar pour voir le nbr de tentativte de connexion
        // en mode debug seulement. n'est implémenté que sur cette tache pour le modéle
        protected void onProgressUpdate(Integer... values) {
            if (mProgressDialog != null) {

                //  mProgressDialog.setTitle(values[0].toString());

            }

        }

        /**
         * @see AsyncTask#onPostExecute(Object)
         */
        @Override
        protected void onPostExecute(ArrayList<PhotoActivite> result) {

            if (exeption) {
                if (afficeProgress) mProgressDialog.dismiss();
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            ecouteur.loopBack_GetListPhotoActivite(result);
            if (afficeProgress) mProgressDialog.dismiss();
        }

        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            //Affiche progress permet de mettre pas pas la barre d'attente
            if (afficeProgress) {
                mProgressDialog = ProgressDialog.
                        show(mcontext, "Patientez ...", "Chargement des discussions...", true);
                mProgressDialog.setCancelable(false);
            }

        }


        interface Async_GetListPhotoActiviteListener {
            void loopBack_GetListPhotoActivite(ArrayList<PhotoActivite> listPhoto);
        }


    }

    public static class AsyncSupprimeCompte extends AsyncTask<String, String, MessageServeur> {
        Integer tentative = 0;
        ProgressDialog mProgressDialog;
        boolean exeption = false;
        final Async_SupprimeCompteListener ecouteur;


        final Context mcontext;

        public AsyncSupprimeCompte(Async_SupprimeCompteListener ecouteur,  Context mcontext) {
            super();
            this.ecouteur = ecouteur;
            this.mcontext = mcontext;
        }

        interface Async_SupprimeCompteListener {
            void loopBack_SupprimeCompte(MessageServeur messageserveur);
        }


        @Override
        protected MessageServeur doInBackground(String... params) {

            do {
                try {
                    exeption = false;
                    tentative++;
                    return (new Wservice().SupprimeCompte());

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();

                    exeption = true;

                } catch (XmlPullParserException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    exeption = true;

                }
            } while (tentative < nbMaxTentative && exeption == true);

            return null;

        }

        /**
         * @see AsyncTask#onPostExecute(Object)
         */
        @Override
        protected void onPostExecute(MessageServeur result) {

            if (exeption) {
                mProgressDialog.dismiss();
                Toast toast = Toast.makeText(mcontext, MESSAGE_ECHEC_IO + tentative, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }

            if (result != null)
                ecouteur.loopBack_SupprimeCompte(result);

            else {

                Toast toast = Toast.makeText(mcontext, "Pas de retour serveur", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
            mProgressDialog.dismiss();

        }

        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mcontext, "Patientez ...", "Suppression du compte...", true);
            mProgressDialog.setCancelable(true);

        }
    }

}

