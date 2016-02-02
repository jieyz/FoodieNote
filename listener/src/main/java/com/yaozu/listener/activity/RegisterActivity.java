package com.yaozu.listener.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.yaozu.listener.HomeMainActivity;
import com.yaozu.listener.R;
import com.yaozu.listener.constant.DataInterface;
import com.yaozu.listener.utils.NetUtil;
import com.yaozu.listener.utils.PhoneInfoUtil;
import com.yaozu.listener.utils.VolleyHelper;

import org.json.JSONObject;

/**
 * Created by jieyz on 2016/1/26.
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    private EditText nickName, phoneNumber, password;
    private Button register;
    private String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        nickName = (EditText) findViewById(R.id.register_nickname);
        phoneNumber = (EditText) findViewById(R.id.register_phonenumber);
        password = (EditText) findViewById(R.id.register_password);
        register = (Button) findViewById(R.id.register_register);
        register.setOnClickListener(this);
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
            case R.id.register_register:
                String nickname = nickName.getText().toString().trim();
                String userid = phoneNumber.getText().toString().trim();
                String pwd = password.getText().toString().trim();

                if (TextUtils.isEmpty(nickname)) {
                    Toast.makeText(this, "用户名称不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(userid)) {
                    Toast.makeText(this, "电话号码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(pwd)) {
                    Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                PhoneInfoUtil phoneInfo = new PhoneInfoUtil(this);
                loginRequest(userid, nickname, pwd, phoneInfo.getDeviceId());
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
    private void loginRequest(final String userid, final String username, String password, String deviceid) {
        String url = DataInterface.getRegisterUrl() + "?userid=" + userid + "&username=" + username + "&password=" + password + "&deviceid=" + deviceid;
        VolleyHelper.getRequestQueue().add(new JsonObjectRequest(Request.Method.GET,
                url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "response : " + response.toString());
                        Intent intent = new Intent(RegisterActivity.this, HomeMainActivity.class);
                        com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(response.toString());
                        int code = jsonObject.getIntValue("code");
                        String msg = jsonObject.getString("message");
                        String token = jsonObject.getString("token");
                        String username = jsonObject.getString("username");
                        String iconurl = jsonObject.getString("iconurl");
                        Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
                        if (code == 1) {
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
                Toast.makeText(RegisterActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
            }
        }));
    }
}
