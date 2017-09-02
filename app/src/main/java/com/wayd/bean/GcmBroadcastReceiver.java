package com.wayd.bean;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by bibou on 23/10/2016.
 */

@SuppressWarnings("DefaultFileTemplate")
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (!Outils.isConnect()) return;

        Bundle extras = intent.getExtras();
        Outils.LOOP_BACK_RECEIVER_GCM.sendMessageListener(extras);
      //  setResultCode(Activity.RESULT_OK);

    }





}