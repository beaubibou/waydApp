package com.wayd.comparator;

import com.wayd.bean.Activite;
import com.wayd.bean.Outils;

import java.util.Comparator;

/**
 * Created by bibou on 27/03/2017.
 */

@SuppressWarnings("DefaultFileTemplate")
public class ComparatorDistanceActivite implements Comparator<Activite> {
    @Override
    public int compare(Activite activite1, Activite activite2) {


        if (Outils.personneConnectee.getDistanceActivite(activite1) <
                Outils.personneConnectee.getDistanceActivite(activite2)) return -1;
        else
            return 1;
    }
}
