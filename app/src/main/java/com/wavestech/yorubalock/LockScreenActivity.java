package com.wavestech.yorubalock;

import androidx.appcompat.app.AppCompatActivity;

import android.app.KeyguardManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class LockScreenActivity extends AppCompatActivity  implements
        LockscreenUtils.OnLockStatusChangedListener {

    private LockscreenUtils mLockscreenUtils;

    // Handle events of calls and unlock screen if necessary
    private class StateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    unlockHomeButton();
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mLockscreenUtils = new LockscreenUtils();
        this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
        this.getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
        );
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);
        displayCurrentTime();
        initializeListeners();
        //Hide the action bar
        getSupportActionBar().hide();

        // unlock screen in case of app get killed by system
        if (getIntent() != null && getIntent().hasExtra("kill")
                && getIntent().getExtras().getInt("kill") == 1) {
            enableKeyguard();
            unlockHomeButton();
        } else {

            try {
                // disable keyguard
                disableKeyguard();

                // lock home button
                lockHomeButton();

                // start service for observing intents
                startService(new Intent(this, LockScreenService.class));

                // listen the events get fired during the call
                StateListener phoneStateListener = new StateListener();
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                telephonyManager.listen(phoneStateListener,
                        PhoneStateListener.LISTEN_CALL_STATE);

//

            } catch (Exception e) {
                Log.e(MainActivity.TAG, e.getMessage());
            }

        }
    }

    // Don't finish Activity on Back press
    @Override
    public void onBackPressed() {
        return;
    }

    // Handle button clicks
    @Override
    public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
                || (keyCode == KeyEvent.KEYCODE_POWER)
                || (keyCode == KeyEvent.KEYCODE_VOLUME_UP)
                || (keyCode == KeyEvent.KEYCODE_CAMERA)) {
            return true;
        }
        if ((keyCode == KeyEvent.KEYCODE_HOME)) {
            Toast.makeText(getApplicationContext(),"Home  button pressed",Toast.LENGTH_LONG).show();
            return true;
        }

        return false;

    }

    // handle the key press events here itself
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP
                || (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN)
                || (event.getKeyCode() == KeyEvent.KEYCODE_POWER)) {
            return false;
        }
        if ((event.getKeyCode() == KeyEvent.KEYCODE_HOME)) {
            Toast.makeText(getApplicationContext(),"Home  button pressed",Toast.LENGTH_LONG).show();
            return false;
        }

        //Let the number keys work for the password text input
        if(event.getKeyCode() >= KeyEvent.KEYCODE_1 && event.getKeyCode() <= KeyEvent.KEYCODE_9)
        {
            return super.dispatchKeyEvent(event);
        }
        return false;
    }



    public void unlock(View v) {
        unlockDevice();
    }

    // Simply unlock device when home button is successfully unlocked
    @Override
    public void onLockStatusChanged(boolean isLocked) {
        if (!isLocked) {
            unlockDevice();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unlockHomeButton();
    }

    @SuppressWarnings("deprecation")
    private void disableKeyguard() {
        KeyguardManager mKM = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock mKL = mKM.newKeyguardLock("IN");
        mKL.disableKeyguard();
    }

    @SuppressWarnings("deprecation")
    private void enableKeyguard() {
        KeyguardManager mKM = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock mKL = mKM.newKeyguardLock("IN");
        mKL.reenableKeyguard();
    }

    //Simply unlock device by finishing the activity
    private void unlockDevice()
    {
        finish();
    }

    // Lock home button
    public void lockHomeButton() {
        mLockscreenUtils.lock(this);
    }

    // Unlock home button and wait for its callback
    public void unlockHomeButton() {
        mLockscreenUtils.unlock();
    }

    public void displayCurrentTime() {
        TextView timeTextView = (TextView) findViewById(R.id.timeTextView);

        LocalTime currentTime = LocalTime.now();
//
        // create a formatter for Yoruba time
        DateTimeFormatter yorubaTimeFormatter = DateTimeFormatter.ofPattern("h:mm a", new Locale("yo", "NG"));

        // format the current time in Yoruba
        String yorubaTime = currentTime.format(yorubaTimeFormatter);
        String timeOfTheDay = "Unknown";
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if (hour >= 5 && hour < 12) {
            timeOfTheDay = "Àárọ̀";
        } else if (hour >= 12 && hour < 17) {
            timeOfTheDay = "Ọ̀ọ̀sán";
        } else if (hour >= 17 && hour < 21) {
            timeOfTheDay = "Ìrọ̀lẹ́";
        } else {
            timeOfTheDay = "Aalẹ́";
        }

        timeTextView.setText(yorubaTime.replace("AM",timeOfTheDay).replace("PM",timeOfTheDay));

        //Date formater

        YorubaDateConverter converter = new YorubaDateConverter();

        TextView dateTextView = (TextView) findViewById(R.id.dateView);

        dateTextView.setText(converter.getDateString());
    }


    public void initializeListeners() {
        Button button = findViewById(R.id.openUnlockBtn);

        Runnable longPressRunnable = new Runnable() {
            @Override
            public void run() {
                // Handle long press after 10 seconds
                stopService(new Intent(LockScreenActivity.this, LockScreenService.class));
                unlockDevice();
            }
        };

        Handler handler = new Handler();
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    handler.postDelayed(longPressRunnable, 6000); // Set a delay of 6 seconds
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    handler.removeCallbacks(longPressRunnable); // Cancel the long press event if the button is released or cancelled
                }
                return false;
            }
        });
    }
}