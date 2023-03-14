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
import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.text.TextWatcher;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class PinCode extends AppCompatActivity {
    private EditText pinCodeInput;
    private Button btnContinue;
    private String pinCodeText="";

    private boolean firstTimeLaunch = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_code);

        pinCodeInput = findViewById(R.id.pinCodeInput);
        btnContinue=findViewById(R.id.btnContinue);

        initializeListeners();
    }

    private void initializeListeners(){
        this.initKeyboard();

        pinCodeInput.setOnFocusChangeListener((view, hasFocus) -> {
            findViewById(R.id.keyboard).setVisibility(hasFocus ? View.VISIBLE : View.INVISIBLE);
            hideNativeKeyboard(view);

            Log.v(MainActivity.TAG, "Focus gained");
        });
        pinCodeInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                pinCodeText=charSequence.toString();
                Log.v(MainActivity.TAG, "text input: " + pinCodeText);

                if(pinCodeText.length()>=4){
                    btnContinue.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(MainActivity.TAG, "starting lock screen");
                startScreenLock();
                Utils.setPassword(PinCode.this, "password", pinCodeText);
                String password = Utils.getPassword(PinCode.this, "password");
                Toast toast = Toast.makeText(PinCode.this, "Lock Screen service started, password has been set: " + password, Toast.LENGTH_LONG);
                toast.show();
                Log.v(MainActivity.TAG, "Lockscreen service");
            }
        });
    }

    private void initKeyboard() {
        CustomKeyboard keyboard = (CustomKeyboard) findViewById(R.id.keyboard);

        // prevent system keyboard from appearing when EditText is tapped
        pinCodeInput.setRawInputType(InputType.TYPE_CLASS_TEXT);
        pinCodeInput.setTextIsSelectable(true);

        // pass the InputConnection from the EditText to the keyboard
        InputConnection ic = pinCodeInput.onCreateInputConnection(new EditorInfo());
        keyboard.setInputConnection(ic);
        keyboard.setEditText(this.pinCodeInput);
    }

    public void hideNativeKeyboard(View view) {
        Log.v(MainActivity.TAG, "Hide Keyboard called");
        Context context = view.getContext();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        int milSec = 5; // Delay in seconds
        Utils.delay(milSec, new Utils.DelayCallback() {
            @Override
            public void afterDelay() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.v(MainActivity.TAG, "Hide keyboard after delay");
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                });
            }
        });
    }

    private void startScreenLock(){
        startService(new Intent(this, LockScreenService.class));
        //Check if this app is my default launcher
        if(!isMyAppLauncherDefault()){
            showInfoDialogForDefaultLauncher();
        }
    }
    private void showInfoDialogForDefaultLauncher() {
        Context context = this;
        new AlertDialog.Builder(this)
                .setTitle("Default Launcher")
                .setMessage("Iwọ yoo nilo lati yi Default Launcher rẹ pada si \"Yorùbá Lock\" lati pari iṣeto naa. Se ki n Tèsíwájú?")
                .setCancelable(false)
                .setPositiveButton("Tèsíwájú", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resetPreferredLauncherAndOpenChooser(context);
//                        finish();
                    }
                }).show();
    }

    public static void resetPreferredLauncherAndOpenChooser(Context context) {
        PackageManager packageManager = context.getPackageManager();
        ComponentName componentName = new ComponentName(context, LockScreenActivity.class);
        packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

        packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT, PackageManager.DONT_KILL_APP);

        Intent selector = new Intent(Intent.ACTION_MAIN);
        selector.addCategory(Intent.CATEGORY_HOME);
        selector.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(selector);


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

    /**
     * GO BACK TO HOME SCREEN AFTER A MINIMIZE
     */
    @Override
    protected void onResume() {
        super.onResume();
        if(!firstTimeLaunch) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        firstTimeLaunch = false;
    }
}