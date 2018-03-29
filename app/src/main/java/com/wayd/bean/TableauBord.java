package com.wayd.bean;


import android.os.AsyncTask;
import android.os.Bundle;

import com.wayd.activity.AsyncTaches;

import java.io.Serializable;
import java.util.ArrayList;

public class TableauBord implements Serializable, AsyncTaches.AsyncGetTdb.AsyncGetTdbListener, ReceiverGCM.GCMMessageListener {
    private int nbrmessagenonlu;
    private int nbractiviteencours;
    private int nbrsuggestion;
    private int nbrnotification;
    private int nbrami;
    private final ArrayList<TdbChangeListener> listenerChangeTdb = new ArrayList<>();

    public TableauBord(int nbractiviteencours, int nbrmessagenonlu, int nbrsuggestion, int nbrnotification, int nbrami) {
        this.nbractiviteencours = nbractiviteencours;
        this.nbrmessagenonlu = nbrmessagenonlu;
        this.nbrsuggestion = nbrsuggestion;
        this.nbrnotification = nbrnotification;
        this.nbrami = nbrami;

    }

    public synchronized void addTdbChangeListener(TdbChangeListener listener) {
        listenerChangeTdb.add(listener);
    }

    public synchronized void removeTdbChangeListener(TdbChangeListener listener) {
        listenerChangeTdb.remove(listener);
    }

    @Override
    public void loopBack_GetTdb(TableauBord tableauDeBord) {
        this.nbractiviteencours = tableauDeBord.getNbractiviteencours();
        this.nbrmessagenonlu = tableauDeBord.getNbrmessagenonlu();
        this.nbrsuggestion = tableauDeBord.getNbrsuggestion();
        this.nbrnotification = tableauDeBord.getNbrnotification();
        this.nbrami = tableauDeBord.getNbrami();
        sendUpdateTdb();
    }

    @Override
    public void loopBackReceiveGCM(Bundle extras) {


        if (extras == null) return;

        int idMessageGcm = 0;
        try {
            idMessageGcm = Integer.parseInt(extras.getString("id"));
        } catch (Exception e) {

            //   e.printStackTrace();
            return;
        }


        switch (idMessageGcm) {


            case PushAndroidMessage.NBR_MESSAGE_NONLU:
                // On envoie dans le mgc l'id de la personne en plus de son numero GSM.
                nbrmessagenonlu = Integer.parseInt(extras.getString("nbrmessagenonlu"));
                sendUpdateTdb();
                break;

            case PushAndroidMessage.NBR_ACTIVITE:

                this.nbractiviteencours = Integer.parseInt(extras.getString("nbractivite"));
                sendUpdateTdb();
                break;

            case PushAndroidMessage.NBR_NOTIFICATION:

                this.nbrnotification = Integer.parseInt(extras.getString("nbrnotification"));
                sendUpdateTdb();
                break;

            case PushAndroidMessage.NBR_SUGGESTION:

                this.nbrsuggestion = Integer.parseInt(extras.getString("nbrsuggestion"));
                sendUpdateTdb();
                break;

            case PushAndroidMessage.NBR_AMI:
                this.nbrami = Integer.parseInt(extras.getString("nbrami"));
                sendUpdateTdb();
                break;


            case PushAndroidMessage.REFRESH_TDB: // Envoi un GCM avec toutes les indicateurs du TDB remis à jour
                // On envoie dans le mgc l'id de la personne en plus de son numero GSM.
                // A enlzver dans la version finale. Pour eviter les prb du au mêm num GSM dans le cas de l'emulateur
                new AsyncTaches.AsyncGetTdb(this, Outils.personneConnectee.getId()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                break;

            case PushAndroidMessage.TDB_REFRESH: // Envoi un GCM avec toutes les indicateurs du TDB remis à jour
                // On envoie dans le mgc l'id de la personne en plus de son numero GSM.
                // A enlzver dans la version finale. Pour eviter les prb du au mêm num GSM dans le cas de l'emulateur


                int nbrami_ = Integer.parseInt(extras.getString("nbrami"));
                int nbrnotification_ = Integer.parseInt(extras.getString("nbrnotification"));
                int nbractivite_ = Integer.parseInt(extras.getString("nbractivite"));
                int nbrmessagenonlu_ = Integer.parseInt(extras.getString("nbrmessagenonlu"));
                int nbrsuggestion_ = Integer.parseInt(extras.getString("nbrsuggestion"));
                update(nbractivite_, nbrmessagenonlu_, nbrsuggestion_, nbrnotification_, nbrami_);

                break;

        }

    }


    public interface TdbChangeListener {
        void updateTableauBord(TableauBord tableauBord);
    }

    private void sendUpdateTdb() {
        for (int i = listenerChangeTdb.size() - 1; i >= 0; i--) {
            listenerChangeTdb.get(i).updateTableauBord(this);
        }
    }

    public void initialise(int nbractiviteencours, int nbrmessagenonlu, int nbrsuggestion, int nbrnotification, int nbrami) {

        this.nbractiviteencours = nbractiviteencours;
        this.nbrmessagenonlu = nbrmessagenonlu;
        this.nbrsuggestion = nbrsuggestion;
        this.nbrnotification = nbrnotification;
        this.nbrami = nbrami;

        sendUpdateTdb();
    }

    private void update(int nbractiviteencours, int nbrmessagenonlu, int nbrsuggestion, int nbrnotification, int nbrami) {

        this.nbractiviteencours = nbractiviteencours;
        this.nbrmessagenonlu = nbrmessagenonlu;
        this.nbrsuggestion = nbrsuggestion;
        this.nbrnotification = nbrnotification;
        this.nbrami = nbrami;

        sendUpdateTdb();
    }

    public int getNbrami() {
        return nbrami;
    }


    public int getNbrnotification() {
        return nbrnotification;
    }

    public int getNbrmessagenonlu() {
        return nbrmessagenonlu;
    }

    public int getNbractiviteencours() {
        return nbractiviteencours;
    }

    public int getNbrsuggestion() {

        return nbrsuggestion;
        //return 20;
    }

    public void Raz() {
        this.nbractiviteencours = 0;
        this.nbrmessagenonlu = 0;
        this.nbrsuggestion = 0;
        this.nbrnotification = 0;
        this.nbrami = 0;

    }
}
