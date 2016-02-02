package com.yaozu.listener.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.yaozu.listener.HomeMainActivity;
import com.yaozu.listener.R;
import com.yaozu.listener.constant.Constant;
import com.yaozu.listener.constant.DataInterface;
import com.yaozu.listener.fragment.social.MyselfFragment;
import com.yaozu.listener.listener.DownLoadIconListener;
import com.yaozu.listener.utils.NetUtil;
import com.yaozu.listener.utils.PhoneInfoUtil;
import com.yaozu.listener.utils.User;
import com.yaozu.listener.utils.VolleyHelper;

import org.json.JSONObject;

import java.io.File;

/**
 * Created by 耀祖 on 2016/1/25.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private EditText mAccout, mPassword;
    private Button mLogin;

    private String token1 = "ZeOpNKgIS6NVsPnNIGS6NGxP7Qfd0jcFN0C5Ibqjpg328zglcxril0v4m4zETCFHBA68rgPUDVEw2+rmhAQNLnLfI1nmn0oY";
    private String token2 = "AIzXjXl8KRobJnxbd8fhVnmGXj2xfWz1oFuzCcWFVHZb5axaA1K5spIaquTmp5+CVWLWAFPNoO6C8oPLXaCzITuX9Xew5d0E";
    private String token3 = "v8XjNiu5BYSQ21+pn93xunmGXj2xfWz1oFuzCcWFVHZb5axaA1K5sgrVvM+PHxVHKxvRo5TOSReC8oPLXaCzITe1/77+nlZ3";
    private SharedPreferences sp;
    private User mUser;
    private TextView registerTextView;
    private String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAccout = (EditText) findViewById(R.id.login_account);
        mPassword = (EditText) findViewById(R.id.login_password);
        mLogin = (Button) findViewById(R.id.login_login);
        registerTextView = (TextView) findViewById(R.id.activity_login_register);

        sp = this.getSharedPreferences(Constant.LOGIN_MSG, Context.MODE_PRIVATE);
        mLogin.setOnClickListener(this);
        registerTextView.setOnClickListener(this);
        mUser = new User(this);

        boolean islogin = mUser.isLogining();
        if (islogin) {
            Intent intent = new Intent(this, HomeMainActivity.class);
            intent.putExtra("token", mUser.getUserToken());
            startActivity(intent);
            finish();
        }
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
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                PhoneInfoUtil phoneInfoUtil = new PhoneInfoUtil(this);
                loginRequest(account, password, phoneInfoUtil.getDeviceId());
                break;
            case R.id.activity_login_register:
                Intent registerIntent = new Intent(this, RegisterActivity.class);
                startActivity(registerIntent);
                break;
        }
    }

    /**
     * 向服务器发送登录请求
     *
     * @param userid
     * @param password
     * @param deviceid
     */
    private void loginRequest(final String userid, String password, String deviceid) {
        String url = DataInterface.getLoginUrl() + "?userid=" + userid + "&password=" + password + "&deviceid=" + deviceid;
        VolleyHelper.getRequestQueue().add(new JsonObjectRequest(Request.Method.GET,
                url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "response : " + response.toString());
                        Intent intent = new Intent(LoginActivity.this, HomeMainActivity.class);
                        com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(response.toString());
                        int code = jsonObject.getIntValue("code");
                        String msg = jsonObject.getString("message");
                        String token = jsonObject.getString("token");
                        String username = jsonObject.getString("username");
                        String iconurl = jsonObject.getString("iconurl");
                        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                        if (code == 1) {
                            NetUtil.downLoadUserIcon(iconurl, new MyDownLoadListener());
                            intent.putExtra("token", jsonObject.getString("token"));
                            startActivity(intent);
                            mUser.storeLoginUserInfo(true, userid, username, token);
                            finish();
                        } else {
                            return;
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
            }
        }));
    }

    public class MyDownLoadListener implements DownLoadIconListener {

        @Override
        public void downLoadSuccess(Bitmap bitmap) {
            Log.d(TAG, "=============downLoadSuccess==================>");
            if (bitmap != null) {
                Toast.makeText(LoginActivity.this, "下载头像成功！", Toast.LENGTH_SHORT).show();
                MyselfFragment.saveOutput(bitmap, MyselfFragment.ICON_PATH);
                File cpIconPath = new File(MyselfFragment.CP_ICON_PATH);
                if (cpIconPath.exists()) {
                    cpIconPath.delete();
                }
            } else {
                Toast.makeText(LoginActivity.this, "下载的头像为空的！", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void downLoadFailed() {
            Log.d(TAG, "=============downLoadFailed==================>");
            Toast.makeText(LoginActivity.this, "下载头像失败！", Toast.LENGTH_SHORT).show();
        }
    }
}
