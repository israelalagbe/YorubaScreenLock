package com.wavestech.yorubalock;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.v("YorLog", "Main activity started");
    }

    public void onClick(View v) {
        Log.v("YorLog", "starting lock screen");
        startScreenLock();
        Toast toast = Toast.makeText(this, "Lock Screen service started", Toast.LENGTH_LONG);
        toast.show();
        Log.v("YorLog", "Lockscreen service");
    }

    private void stopScreenLock(){
        stopService(new Intent(this, LockScreenService.class));
    }
    private void startScreenLock(){
        startService(new Intent(this, LockScreenService.class));
    }
}