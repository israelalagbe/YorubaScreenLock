package com.wavestech.yorubalock;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;


public class Utils {

    // Delay mechanism

    public interface DelayCallback{
        void afterDelay();
    }

    public static void delay(int milSec, final DelayCallback delayCallback){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                delayCallback.afterDelay();
            }
        }, milSec); // afterDelay will be executed after (secs*1000) milliseconds.
    }

    public static void setPassword(Activity activity, String key, String value) {
        SharedPreferences sharedPref = activity.getSharedPreferences("Yorubalock", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getPassword(Activity activity, String key) {
        SharedPreferences sharedPref = activity.getSharedPreferences("Yorubalock", Context.MODE_PRIVATE);
        return sharedPref.getString(key, null);
    }

}