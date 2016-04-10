package com.yaozu.listener.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.yaozu.listener.constant.DataInterface;
import com.yaozu.listener.dao.MsmResponse;
import com.yaozu.listener.dao.NetDao;
import com.yaozu.listener.utils.PhoneInfoUtil;
import com.yaozu.listener.utils.VolleyHelper;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by jieyaozu on 2016/4/7.
 */
public class CheckSmsCodeActivity extends BaseActivity implements View.OnClickListener {
    private TextView phoneNumber;
    private EditText verifyCode;

    private String username, pnumber, password;
    private TextView obtainCode;
    private Button next;
    private String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_sms_code);
        phoneNumber = (TextView) findViewById(R.id.check_sms_phonenumber);
        verifyCode = (EditText) findViewById(R.id.check_sms_code);
        obtainCode = (TextView) findViewById(R.id.check_sms_obtain_again);
        next = (Button) findViewById(R.id.check_sms_next);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        pnumber = intent.getStringExtra("pnumber");
        password = intent.getStringExtra("password");
        obtainCode.setOnClickListener(this);
        next.setOnClickListener(this);
        obtainCode.setClickable(false);

        initView();
        startCountDownTimer();
    }

    private void startCountDownTimer() {
        new CountDownTimer(60000, 1000) {

            @Override
            public void onTick(long l) {
                obtainCode.setText((l / 1000) + "秒后重新获取验证码");
            }

            @Override
            public void onFinish() {
                obtainCode.setText("重新获取验证码");
                obtainCode.setClickable(true);
                obtainCode.setTextColor(getResources().getColor(R.color.black));
            }
        }.start();
    }

    private void initView() {
        phoneNumber.setText("+86 " + pnumber);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.check_sms_obtain_again:
                obtainVerifyCode();
                break;
            case R.id.check_sms_next:
                String smscode = verifyCode.getText().toString().trim();
                if (TextUtils.isEmpty(smscode)) {
                    Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (smscode.length() != 6 || !smscode.matches("[0-9]+")) {
                    Toast.makeText(this, "请输入6位数字的验证码", Toast.LENGTH_SHORT).show();
                    return;
                }
                verifySmsCode(smscode);
                break;
        }
    }

    /**
     * 输入验证码验证是否正确
     * 验证正确后发送注册请求并登录
     */
    private void verifySmsCode(String smscode) {
        NetDao.verifySmsPhoneCode(pnumber, smscode, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(response.toString());
                int code = jsonObject.getIntValue("code");
                if (code == 1) {
                    Toast.makeText(CheckSmsCodeActivity.this, "验证成功", Toast.LENGTH_SHORT).show();
                    PhoneInfoUtil phoneInfo = new PhoneInfoUtil(CheckSmsCodeActivity.this);
                    registerRequest(pnumber, username, password, phoneInfo.getDeviceId());
                } else {
                    Toast.makeText(CheckSmsCodeActivity.this, "验证失败", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CheckSmsCodeActivity.this, "验证失败", Toast.LENGTH_SHORT).show();
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
        String url = null;
        try {
            url = DataInterface.getRegisterUrl() + "?userid=" + userid + "&username=" + URLEncoder.encode(username, "UTF-8") + "&password=" + password + "&deviceid=" + deviceid;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        VolleyHelper.getRequestQueue().add(new JsonObjectRequest(Request.Method.GET,
                url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "response : " + response.toString());
                        Intent intent = new Intent(CheckSmsCodeActivity.this, HomeMainActivity.class);
                        com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(response.toString());
                        int code = jsonObject.getIntValue("code");
                        String msg = jsonObject.getString("message");
                        String token = jsonObject.getString("token");
                        String username = jsonObject.getString("username");
                        String iconurl = jsonObject.getString("iconurl");
                        Toast.makeText(CheckSmsCodeActivity.this, msg, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(CheckSmsCodeActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
            }
        }));
    }

    private void obtainVerifyCode() {
        NetDao.getSmsPhoneCode(pnumber, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "response : " + response.toString());
                MsmResponse msmResponse = JSON.parseObject(response.toString(), MsmResponse.class);
                String state = msmResponse.getAlibaba_aliqin_fc_sms_num_send_response().getResult().getSuccess();
                if (!"true".equals(state)) {
                    Toast.makeText(CheckSmsCodeActivity.this, "获取验证码失败，请重新获取", Toast.LENGTH_SHORT).show();
                } else {
                    startCountDownTimer();
                    obtainCode.setClickable(false);
                    obtainCode.setTextColor(getResources().getColor(R.color.gray_white));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CheckSmsCodeActivity.this, "获取验证码失败，请重新获取", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
