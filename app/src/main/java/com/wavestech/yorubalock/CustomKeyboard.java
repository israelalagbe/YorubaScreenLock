package com.wavestech.yorubalock;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class CustomKeyboard extends LinearLayout implements View.OnClickListener {

    // constructors
    public CustomKeyboard(Context context) {
        this(context, null, 0);
    }

    public CustomKeyboard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomKeyboard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private EditText editText = null;

    // Our communication link to the EditText
    InputConnection inputConnection;

    private void init(Context context, AttributeSet attrs) {

        // initialize buttons
        LayoutInflater.from(context).inflate(R.layout.keyboard, this, true);

        findViewById(R.id.button_delete).setOnClickListener(this);
        findViewById(R.id.button_a).setOnClickListener(this);
        findViewById(R.id.button_b).setOnClickListener(this);
        findViewById(R.id.button_d).setOnClickListener(this);
        findViewById(R.id.button_e).setOnClickListener(this);
        findViewById(R.id.button_ę).setOnClickListener(this);
        findViewById(R.id.button_f).setOnClickListener(this);
        findViewById(R.id.button_g).setOnClickListener(this);
        findViewById(R.id.button_gb).setOnClickListener(this);
        findViewById(R.id.button_h).setOnClickListener(this);
        findViewById(R.id.button_i).setOnClickListener(this);
        findViewById(R.id.button_j).setOnClickListener(this);
        findViewById(R.id.button_k).setOnClickListener(this);
        findViewById(R.id.button_l).setOnClickListener(this);
        findViewById(R.id.button_m).setOnClickListener(this);
        findViewById(R.id.button_n).setOnClickListener(this);
        findViewById(R.id.button_o).setOnClickListener(this);
        findViewById(R.id.button_ọ).setOnClickListener(this);
        findViewById(R.id.button_p).setOnClickListener(this);
        findViewById(R.id.button_r).setOnClickListener(this);
        findViewById(R.id.button_s).setOnClickListener(this);
        findViewById(R.id.button_ṣ).setOnClickListener(this);
        findViewById(R.id.button_t).setOnClickListener(this);
        findViewById(R.id.button_u).setOnClickListener(this);
        findViewById(R.id.button_w).setOnClickListener(this);
        findViewById(R.id.button_y).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        // do nothing if the InputConnection has not been set yet
        if (inputConnection == null) return;

        // Delete text or input key value
        // All communication goes through the InputConnection
        if (v.getId() == R.id.button_delete) {
            CharSequence selectedText = inputConnection.getSelectedText(0);
            if (TextUtils.isEmpty(selectedText)) {
                // no selection, so delete previous character
                inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DEL));
                inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,KeyEvent.KEYCODE_DEL));
            } else {
                // delete the selection
                inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,KeyEvent.KEYCODE_DEL));
            }
        } else {
            Button button = (Button) v;
            String prevText = "";
            if(this.editText != null) {
                prevText = this.editText.getText().toString();
            }
            String value = prevText + button.getText().toString();
            inputConnection.commitText(value, 1);
        }
    }

    // The activity (or some parent or controller) must give us
    // a reference to the current EditText's InputConnection
    public void setInputConnection(InputConnection ic) {
        this.inputConnection = ic;
    }

    public void setEditText(EditText editText) {
        this.editText = editText;
    }

}