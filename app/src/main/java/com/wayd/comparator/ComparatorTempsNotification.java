package com.wayd.comparator;

import com.wayd.bean.Notification;

import java.util.Comparator;

/**
 * Created by bibou on 27/03/2017.
 */

@SuppressWarnings("DefaultFileTemplate")
public class ComparatorTempsNotification implements Comparator<Notification> {
    @Override
    public int compare(Notification notification1,Notification notification2) {

        if (notification1.getDatecreation().before(notification2.getDatecreation()) )
              return 1;
        else
            return -1;
    }
}
