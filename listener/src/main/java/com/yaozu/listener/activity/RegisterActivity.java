package com.yaozu.listener.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.yaozu.listener.HomeMainActivity;
import com.yaozu.listener.R;
import com.yaozu.listener.constant.DataInterface;
import com.yaozu.listener.dao.MsmResponse;
import com.yaozu.listener.dao.NetDao;
import com.yaozu.listener.utils.IntentUtil;
import com.yaozu.listener.utils.PhoneInfoUtil;
import com.yaozu.listener.utils.VolleyHelper;

import org.json.JSONObject;


/**
 * Created by jieyz on 2016/1/26.
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    private String TAG = this.getClass().getSimpleName();
    private EditText nickName, phoneNumber, password;
    private Button register;
    private Dialog dialog;

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
                final String nickname = nickName.getText().toString().trim();
                final String userid = phoneNumber.getText().toString().trim();
                final String pwd = password.getText().toString().trim();

                if (TextUtils.isEmpty(nickname)) {
                    Toast.makeText(this, "用户名称不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(userid)) {
                    Toast.makeText(this, "电话号码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!verifyPhoneNumber(userid)) {
                    return;
                }

                if (TextUtils.isEmpty(pwd)) {
                    Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                PhoneInfoUtil phoneInfo = new PhoneInfoUtil(this);
/*                try {
                    registerRequest(userid, URLEncoder.encode(nickname, "UTF-8"), pwd, phoneInfo.getDeviceId());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }*/
                showDialog(userid, nickname, pwd);
                break;
        }
    }

    private boolean verifyPhoneNumber(String number) {
        if (number.length() != 11 || !number.matches("[0-9]+") || number.charAt(0) != 49) {
            Toast.makeText(this, "电话号码的格式不正确", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void showDialog(final String phoneNumber, final String nickname, final String password) {
        dialog = new Dialog(this, R.style.NobackDialog);
        View view = View.inflate(this, R.layout.dialog_verify_phonenumber, null);
        TextView phone = (TextView) view.findViewById(R.id.dialog_verify_phonenumber);
        phone.setText("+86 " + phoneNumber);
        TextView cancel = (TextView) view.findViewById(R.id.dialog_verify_cancel);
        TextView ok = (TextView) view.findViewById(R.id.dialog_verify_confirm);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                sendVerifyCode(phoneNumber, nickname, password);
            }
        });
        dialog.setContentView(view);
        dialog.show();
    }

    /**
     * 发送短信验证码
     */
    private void sendVerifyCode(final String phoneNumber, final String nickname, final String password) {
        NetDao.getSmsPhoneCode(phoneNumber, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "response : " + response.toString());
                MsmResponse msmResponse = JSON.parseObject(response.toString(), MsmResponse.class);
                String state = msmResponse.getAlibaba_aliqin_fc_sms_num_send_response().getResult().getSuccess();
                if (!"true".equals(state)) {
                    Toast.makeText(RegisterActivity.this, "获取验证码失败，请重新获取", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(RegisterActivity.this, CheckSmsCodeActivity.class);
                    intent.putExtra("username", nickname);
                    intent.putExtra("pnumber", phoneNumber);
                    intent.putExtra("password", password);
                    startActivity(intent);
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    /**
     * 向服务器发送注册请求
     *
     * @param userid
     * @param password
     * @param deviceid
     */
    private void registerRequest(final String userid, final String username, String password, String deviceid) {
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
