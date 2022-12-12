package com.wavestech.yorubalock;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static String TAG = "YorLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.v(MainActivity.TAG, "Main activity started");
    }

    public void onClick(View v) {
        Log.v(MainActivity.TAG, "starting lock screen");
        startScreenLock();
        Toast toast = Toast.makeText(this, "Lock Screen service started", Toast.LENGTH_LONG);
        toast.show();
        Log.v(MainActivity.TAG, "Lockscreen service");
    }

    private void stopScreenLock(){
        stopService(new Intent(this, LockScreenService.class));
    }
    private void startScreenLock(){
        startService(new Intent(this, LockScreenService.class));
        //Check if this app is my default launcher
        if(!isMyAppLauncherDefault()){
            showInfoDialogForDefaultLauncher();
        }
    }
    private void showInfoDialogForDefaultLauncher() {
        new AlertDialog.Builder(this)
                .setTitle("Default Launcher")
                .setMessage("You will need to change your default launcher to \"Yoruba Lock\" to complete the setup. Click OK to continue.")
                .setCancelable(false)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resetPreferredLauncherAndOpenChooser(getApplicationContext());
                    }
                }).show();
    }

    public static void resetPreferredLauncherAndOpenChooser(Context context) {
        PackageManager packageManager = context.getPackageManager();
        ComponentName componentName = new ComponentName(context, LockScreenActivity.class);
        packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

        Intent selector = new Intent(Intent.ACTION_MAIN);
        selector.addCategory(Intent.CATEGORY_HOME);
        selector.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(selector);

        packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT, PackageManager.DONT_KILL_APP);
    }

    /**
     * method checks to see if app is currently set as default launcher
     * @return boolean true means currently set as default, otherwise false
     */
    private boolean isMyAppLauncherDefault() {
        final IntentFilter filter = new IntentFilter(Intent.ACTION_MAIN);
        filter.addCategory(Intent.CATEGORY_HOME);

        List<IntentFilter> filters = new ArrayList<IntentFilter>();
        filters.add(filter);

        ResolveInfo resolveInfo = getPackageManager().resolveActivity(getIntent(), PackageManager.MATCH_DEFAULT_ONLY);
        String currentHomePackage = resolveInfo.activityInfo.packageName;

        final String myPackageName = getPackageName();
        Log.v(MainActivity.TAG, "Comparing current and home package: current - "+ currentHomePackage + " mine - " + myPackageName);
        return currentHomePackage == myPackageName;
    }
}