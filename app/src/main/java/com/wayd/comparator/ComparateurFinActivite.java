package com.wayd.comparator;

import com.wayd.bean.Activite;

import java.util.Comparator;

/**
 * Created by bibou on 27/03/2017.
 */

@SuppressWarnings("DefaultFileTemplate")
public class ComparateurFinActivite implements Comparator<Activite> {


    @Override
    public int compare(Activite o1, Activite o2) {
        if (o1.getFinidans() < o2.getFinidans())
            return -1;
        else
            return 1;
    }
}
