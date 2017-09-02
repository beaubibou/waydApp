package com.wayd.bean;

/**
 * Created by bibou on 22/02/2017.
 */

@SuppressWarnings("DefaultFileTemplate")
public class IndicateurWayd {

    private int nbrTotalActivite;
    private int nbrTotalParticipation;
    private int nbrTotalInscrit;
    private int nbrTotalMessage;
    private int nbrTotalMessageByact;


    public IndicateurWayd(int nbrTotalActivite, int nbrTotalParticipation,
                          int nbrTotalInscrit, int nbrTotalMessage, int nbrTotalMessageByact) {
        super();
        this.nbrTotalActivite = nbrTotalActivite;
        this.nbrTotalParticipation = nbrTotalParticipation;
        this.nbrTotalInscrit = nbrTotalInscrit;
        this.nbrTotalMessage = nbrTotalMessage;
        this.nbrTotalMessageByact = nbrTotalMessageByact;
    }

    public int getNbrTotalMessage() {
        return nbrTotalMessage;
    }

    public int getNbrTotalMessageByact() {
        return nbrTotalMessageByact;
    }

    public int getNbrTotalActivite() {
        return nbrTotalActivite;
    }

    public int getNbrTotalParticipation() {
        return nbrTotalParticipation;
    }

    public int getNbrTotalInscrit() {
        return nbrTotalInscrit;
    }


}
