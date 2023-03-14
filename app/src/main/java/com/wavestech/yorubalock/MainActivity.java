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
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static String TAG = "YorLog";
    private Switch enableLockSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        enableLockSwitch=findViewById(R.id.enableLockSwitch);

        Toast toast = Toast.makeText(MainActivity.this, "This set password: " + Utils.getPassword(MainActivity.this, "password"), Toast.LENGTH_LONG);
        toast.show();

        //If password has already been set, check the switch
        if(Utils.getPassword(MainActivity.this, "password") != null){
            enableLockSwitch.setChecked(true);
        }

        enableLockSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if(checked){
                    launchPinCodeActivity();
                }
                else{
                    Utils.setPassword(MainActivity.this, "password", null);
                    stopScreenLock();
                }
            }
        });
        Log.v(MainActivity.TAG, "Main activity started");
    }

    private void launchPinCodeActivity(){
        Intent intent=new Intent(MainActivity.this,PinCode.class);
        startActivity(intent);
    }

    public void onClick(View v) {

    }

    private void stopScreenLock(){
        stopService(new Intent(MainActivity.this, LockScreenService.class));
    }

}