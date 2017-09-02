package com.wayd.activity;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.wayd.bean.Outils;

/**
 * Created by bibou on 14/05/2017.
 */

@SuppressWarnings("DefaultFileTemplate")
public class MyLifecycleHandler implements Application.ActivityLifecycleCallbacks {
    // I use four separate variables here. You can, of course, just use two and
    // increment/decrement them instead of using four and incrementing them all.
    private int resumed;
    private int paused;
    private int started;
    private int stopped;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
        ++resumed;
        android.util.Log.w("test", " resumed application is in foreground: " + (resumed > paused));
        if (resumed > paused) {
            if (Outils.gps.locationManager == null)
                Outils.gps.getLocation();
        } //else {
          //  Outils.gps.stopUsingGPS();
     //   }
        // application en

    }

    @Override
    public void onActivityPaused(Activity activity) {
        ++paused;

   //     android.util.Log.w("test", "*******************************************************************paused application is in devant: " + (resumed > paused));

    // application en
        android.util.Log.w("test", "*******************************************************************paused devant: " + isApplicationInForeground());


}

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
        ++started;
    }

    @Override
    public void onActivityStopped(Activity activity) {
        ++stopped;
      //  android.util.Log.w("test", " stopped application is visible: " + (started > stopped));
        android.util.Log.w("test", "*******************************************************************pauseddddd devant: " + isApplicationInForeground());
        if (!isApplicationInForeground()){
            Outils.gps.stopUsingGPS();
        }



    }

    public  boolean isApplicationVisible() {
        return started > stopped;
    }

    public  boolean isApplicationInForeground() {
        return resumed > paused;
    }

    // If you want a static function you can use to check if your application is
    // foreground/background, you can use the following:
    /*
    // Replace the four variables above with these four
    private static int resumed;
    private static int paused;
    private static int started;
    private static int stopped;

    // And these two public static functions
    public static boolean isApplicationVisible() {
        return started > stopped;
    }

    public static boolean isApplicationInForeground() {
        return resumed > paused;
    }
    */
}