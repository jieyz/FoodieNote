package com.yaozu.listener.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.yaozu.listener.R;
import com.yaozu.listener.constant.DataInterface;
import com.yaozu.listener.constant.IntentKey;
import com.yaozu.listener.utils.VolleyHelper;

import org.json.JSONObject;

/**
 * Created by jieyaozu on 2016/2/20.
 */
public class SearchActivity extends SwipeBackBaseActivity implements View.OnClickListener {
    private ImageView actionBack;
    private EditText searchUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);
        actionBack = (ImageView) findViewById(R.id.search_friend_back);
        searchUser = (EditText) findViewById(R.id.search_user_edit);
        actionBack.setOnClickListener(this);
        searchUser.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    requestSearchUser(searchUser.getText().toString().trim());
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.left_enter_page, R.anim.right_quit_page);
    }

    private void requestSearchUser(final String userid) {
        String url = DataInterface.getCheckUserInfoUrl() + "?userid=" + userid;
        VolleyHelper.getRequestQueue().add(new JsonObjectRequest(Request.Method.GET,
                url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(response.toString());
                int code = jsonObject.getIntValue("code");
                String msg = jsonObject.getString("message");
                String token = jsonObject.getString("token");
                String username = jsonObject.getString("username");
                String iconurl = jsonObject.getString("iconurl");
                if (code == 1) {
                    Intent intent = new Intent(SearchActivity.this, UserDetailActivity.class);
                    intent.putExtra(IntentKey.USER_TOKEN, token);
                    intent.putExtra(IntentKey.USER_NAME, username);
                    intent.putExtra(IntentKey.USER_ID, userid);
                    intent.putExtra(IntentKey.USER_ICON_URL, iconurl);
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_enter_page, R.anim.left_quit_page);
                } else if (code == 0) {
                    Toast.makeText(SearchActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }));
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
            case R.id.search_friend_back:
                finish();
                break;
        }
    }
}
