package com.wavestech.yorubalock;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;


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

    public static void hideNativeKeyboard(Activity activity,View view) {
        Log.v(MainActivity.TAG, "Hide Keyboard called");
        Context context = view.getContext();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        int milSec = 5; // Delay in seconds
        Utils.delay(milSec, new Utils.DelayCallback() {
            @Override
            public void afterDelay() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.v(MainActivity.TAG, "Hide keyboard after delay");
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                });
            }
        });
    }

    public static String removeLastChar(String str) {
        return removeLastChars(str, 1);
    }

    public static String removeLastChars(String str, int chars) {
        if(str.length() == 0)
            return str;
        return str.substring(0, str.length() - chars);
    }

    public static String getDefaultActivity(Context context) {
        PackageManager packageManager = context.getPackageManager();

// Create an intent for the home screen
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);

// Get the default activity that handles the home screen intent
        ResolveInfo defaultLauncher = packageManager.resolveActivity(homeIntent, PackageManager.MATCH_DEFAULT_ONLY);

// Get the package name and class name of the default launcher activity
        String packageName = defaultLauncher.activityInfo.packageName;
        String className = defaultLauncher.activityInfo.name;
//        Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
//        startActivity(intent);
        return className;
    }

}