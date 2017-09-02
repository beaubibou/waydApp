package com.wayd.comparator;

import com.wayd.bean.Discussion;

import java.util.Comparator;

/**
 * Created by bibou on 27/03/2017.
 */

@SuppressWarnings("DefaultFileTemplate")
public class ComparateurDiscussionDate implements Comparator<Discussion> {


    @Override
    public int compare(Discussion o1, Discussion o2) {

        if (o1.getDatecreation().before(o2.getDatecreation()))
            return 1;
        else
            return -1;
    }
}
