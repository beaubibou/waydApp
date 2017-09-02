package com.wayd.bean;

import android.os.Bundle;

import java.util.ArrayList;

/**
 * Created by bibou on 11/03/2017.
 */

@SuppressWarnings("DefaultFileTemplate")
public class ReceiverGCM {// il n'est pas possilbe d'ecoute le GCMBroccatRecevier. Cette classe en static dans Outils est l'interface d'écoute pour disctribuer
    // les message de typepush (gcm);

    private final ArrayList<GCMMessageListener> listenerGCMMessage = new ArrayList<>();


    public ReceiverGCM() {

    }


    public void sendMessageListener(Bundle bundle) {
       // System.out.println("messagejkkkkk listern" + listenerGCMMessage.size());

        for (int i = listenerGCMMessage.size() - 1; i >= 0; i--) {
            listenerGCMMessage.get(i).loopBackReceiveGCM(bundle);
        }
    }

    public synchronized void addGCMMessageListener(GCMMessageListener listener) {
        listenerGCMMessage.add(listener);

    }

    public synchronized void removeGCMMessageListener(GCMMessageListener listener) {
        listenerGCMMessage.remove(listener);
    }

    public interface GCMMessageListener {
        void loopBackReceiveGCM(Bundle bundle);
    }

}
