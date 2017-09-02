package com.wayd.busMessaging;

import java.util.Date;

/**
 * Created by bibou on 29/03/2017.
 */

@SuppressWarnings("DefaultFileTemplate")
public class MessageBus {// Permet de transmettre via le bus un message d'information concernant le message emis par le chat.
   // Utiliser dans mes discussiosn pour intercepter les changements de messages dans l'entête des discussions.

    private String message;
    private Date datecreation;
    private String idDiscussion;
    public MessageBus(String message, Date datecreation, String idDiscussion) {
        this.message = message;
        this.datecreation = datecreation;
        this.idDiscussion = idDiscussion;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDatecreation() {
        return datecreation;
    }

    public void setDatecreation(Date datecreation) {
        this.datecreation = datecreation;
    }

    public String getIdDiscussion() {
        return idDiscussion;
    }

    public void setIdDiscussion(String idDiscussion) {
        this.idDiscussion = idDiscussion;
    }
}
