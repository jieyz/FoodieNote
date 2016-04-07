package com.yaozu.listener.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.yaozu.listener.R;

/**
 * Created by jieyaozu on 2016/4/7.
 */
public class CheckSmsCodeActivity extends BaseActivity {
    private TextView phoneNumber;
    private EditText verifyCode;

    private String username, pnumber, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_sms_code);
        phoneNumber = (TextView) findViewById(R.id.check_sms_phonenumber);
        verifyCode = (EditText) findViewById(R.id.check_sms_code);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        pnumber = intent.getStringExtra("pnumber");
        password = intent.getStringExtra("password");

        initView();
    }

    private void initView() {
        phoneNumber.setText("+86 "+pnumber);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void notifyCurrentSongMsg(String name, String singer, long album_id, int currentPos) {

    }

    @Override
    public void notifySongPlaying() {

    }

    @Override
    public void notifySongPause() {

    }
}
