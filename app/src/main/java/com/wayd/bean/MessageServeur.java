package com.wayd.bean;

/**
 * Created by bibou on 30/09/2016.
 */


@SuppressWarnings("DefaultFileTemplate")
public class MessageServeur {
    private final boolean reponse;
    private final String message;


    public MessageServeur(boolean reponse, String message) {
        super();
        this.reponse = reponse;
        this.message = message;
    }

    public boolean isReponse() {
        return reponse;
    }

      public String getMessage() {
        return message;
    }



}