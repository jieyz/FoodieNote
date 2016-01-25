package com.yaozu.listener.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.yaozu.listener.HomeMainActivity;
import com.yaozu.listener.R;

/**
 * Created by 耀祖 on 2016/1/25.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private EditText mAccout, mPassword;
    private Button mLogin;

    private String token1 = "ZeOpNKgIS6NVsPnNIGS6NGxP7Qfd0jcFN0C5Ibqjpg328zglcxril0v4m4zETCFHBA68rgPUDVEw2+rmhAQNLnLfI1nmn0oY";
    private String token2 = "AIzXjXl8KRobJnxbd8fhVnmGXj2xfWz1oFuzCcWFVHZb5axaA1K5spIaquTmp5+CVWLWAFPNoO6C8oPLXaCzITuX9Xew5d0E";
    private String token3 = "v8XjNiu5BYSQ21+pn93xunmGXj2xfWz1oFuzCcWFVHZb5axaA1K5sgrVvM+PHxVHKxvRo5TOSReC8oPLXaCzITe1/77+nlZ3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAccout = (EditText) findViewById(R.id.login_account);
        mPassword = (EditText) findViewById(R.id.login_password);
        mLogin = (Button) findViewById(R.id.login_login);

        mLogin.setOnClickListener(this);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_login:
                String account = mAccout.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                if (TextUtils.isEmpty(account)) {
                    Toast.makeText(this, "账号不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!"jieyaozu".equals(account) && !"jieyaozu2".equals(account) && !"jieyaozu3".equals(account)) {
                    Toast.makeText(this, "账号不存在", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!"yuanwang44".equals(password)) {
                    Toast.makeText(this, "密码不正确", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(this, HomeMainActivity.class);
                String token = null;
                if ("jieyaozu".equals(account)) {
                    token = token1;
                } else if ("jieyaozu2".equals(account)) {
                    token = token2;
                } else if ("jieyaozu3".equals(account)) {
                    token = token3;
                }
                intent.putExtra("token",token);
                startActivity(intent);
                finish();
                break;
        }
    }
}
