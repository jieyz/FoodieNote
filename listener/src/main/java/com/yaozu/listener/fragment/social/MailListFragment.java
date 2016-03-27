package com.yaozu.listener.fragment.social;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.yaozu.listener.R;
import com.yaozu.listener.activity.AddNewFriendActivity;
import com.yaozu.listener.adapter.MailListAdapter;
import com.yaozu.listener.constant.Constant;
import com.yaozu.listener.constant.DataInterface;
import com.yaozu.listener.constant.IntentKey;
import com.yaozu.listener.db.dao.FriendDao;
import com.yaozu.listener.db.model.Person;
import com.yaozu.listener.fragment.BaseFragment;
import com.yaozu.listener.utils.SortChineseName;
import com.yaozu.listener.utils.VolleyHelper;

import org.json.JSONObject;

import java.util.Collections;
import java.util.List;

/**
 * Created by 耀祖 on 2015/12/5.
 */
public class MailListFragment extends BaseFragment implements View.OnClickListener {
    private ListView mListView;
    private static MailListAdapter adapter;
    /**
     * 父布局
     */
    private RelativeLayout parentView;
    private PopupWindow popupwindow;
    private int mWindowWidth;
    private int mWindowHeight;
    //通讯录数据DAO
    private static FriendDao friendDao;
    public static List<Person> persons;
    //索引
    private LinearLayout indexesLinearLayout;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            hideIndexesLinearLayout();
        }
    };

    public static Handler updateAdapterHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            persons = friendDao.findAllFriends();
            //插入索引字母并排序
            addLetters();
            adapter.setData(persons);
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerVerifyFriendReceiver();
        friendDao = new FriendDao(this.getActivity());
        persons = friendDao.findAllFriends();
        //插入索引字母并排序
        addLetters();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        parentView = (RelativeLayout) view.findViewById(R.id.maillist_parentview);
        mListView = (ListView) view.findViewById(R.id.maillist_listview);
        indexesLinearLayout = (LinearLayout) view.findViewById(R.id.mailist_listview_indexes);
        initAlphabetLayout();

        adapter = new MailListAdapter(this.getActivity(), persons, parentView);
        mListView.setAdapter(adapter);
        WindowManager wm = (WindowManager) this.getActivity().getSystemService(Context.WINDOW_SERVICE);
        mWindowWidth = wm.getDefaultDisplay().getWidth();
        mWindowHeight = wm.getDefaultDisplay().getHeight();

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (AbsListView.SCREEN_STATE_ON == scrollState) {
                    if (indexesLinearLayout.getVisibility() == View.VISIBLE) {
                        removeHideMessage();
                    } else {
                        showIndexesLinearLayout();
                    }
                } else if (AbsListView.SCREEN_STATE_OFF == scrollState) {
                    sendHideMessage();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    private void initAlphabetLayout() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        params.gravity = Gravity.CENTER_HORIZONTAL;
        for (int i = 0; i < letters.length; i++) {
            TextView textView = new TextView(getActivity());
            textView.setTextColor(getResources().getColor(R.color.white));
            textView.setTextSize(10);
            textView.setText(letters[i]);
            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(params);
            textView.setTag(i + 1);
            indexesLinearLayout.addView(textView);
        }

        indexesLinearLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        removeHideMessage();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        sendHideMessage();
                        break;
                }
                return true;
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_social_maillist, container, false);
    }

    private static String[] letters = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    private static void addLetters() {
        for (int i = 0; i < letters.length; i++) {
            Person person = new Person();
            person.setName(letters[i]);
            person.setId(null);
            persons.add(person);
        }
        //插入索引字母并排序
        Collections.sort(persons, new SortChineseName());
        //删除不必要的索引字母
        for (int i = persons.size() - 1; i >= 0; i--) {
            Person curent_person = persons.get(i);
            Person pre_person = (i + 1) >= persons.size() ? null : persons.get(i + 1);
            if ((pre_person == null && curent_person.getId() == null) || (curent_person.getId() == null && pre_person.getId() == null)) {
                persons.remove(i);
            }
        }
    }

    /**
     * 展示索引字母
     */
    private void showIndexesLinearLayout() {
        Animation scaleAt = AnimationUtils.loadAnimation(getActivity(), R.anim.popup_show);
        //TranslateAnimation translate = new TranslateAnimation(0, 0, popupwindow.getHeight(), 0);
        scaleAt.setFillEnabled(true);
        scaleAt.setInterpolator(new DecelerateInterpolator());
        scaleAt.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                indexesLinearLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        indexesLinearLayout.startAnimation(scaleAt);
    }

    /**
     * 隐藏索引字母
     */
    private void hideIndexesLinearLayout() {
        Animation scaleAt = AnimationUtils.loadAnimation(getActivity(), R.anim.popup_dismiss);
        //TranslateAnimation translate = new TranslateAnimation(0, 0, popupwindow.getHeight(), 0);
        scaleAt.setFillEnabled(true);
        scaleAt.setInterpolator(new DecelerateInterpolator());
        scaleAt.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                indexesLinearLayout.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        indexesLinearLayout.startAnimation(scaleAt);
    }

    private void sendHideMessage() {
        Message msg = mHandler.obtainMessage();
        msg.what = 0;
        mHandler.sendMessageDelayed(msg, 2000);
    }

    private void removeHideMessage() {
        mHandler.removeMessages(0);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
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

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegisterVerifyFriendRecevier();
    }

    /**
     * 注册验证好友的广播
     */
    protected void registerVerifyFriendReceiver() {
        if (verifyFrienBroadcastReceiver == null) {
            verifyFrienBroadcastReceiver = new VerifyFrienBroadcastReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(IntentKey.NOTIFY_VERIFY_FRIEND);
            filter.addAction(IntentKey.NOTIFY_VERIFY_AGREE_FRIEND);
            localBroadcastManager = LocalBroadcastManager.getInstance(this.getActivity());
            localBroadcastManager.registerReceiver(verifyFrienBroadcastReceiver, filter);
        }
    }

    protected void unRegisterVerifyFriendRecevier() {
        if (verifyFrienBroadcastReceiver != null) {
            localBroadcastManager = LocalBroadcastManager.getInstance(this.getActivity());
            localBroadcastManager.unregisterReceiver(verifyFrienBroadcastReceiver);
            verifyFrienBroadcastReceiver = null;
        }
    }

    private VerifyFrienBroadcastReceiver verifyFrienBroadcastReceiver;
    private LocalBroadcastManager localBroadcastManager;

    private class VerifyFrienBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (IntentKey.NOTIFY_VERIFY_FRIEND.equals(intent.getAction())) {
                String userid = intent.getStringExtra(IntentKey.USER_ID);
                requestCheckUserInfo(userid, false);
            } else if (IntentKey.NOTIFY_VERIFY_AGREE_FRIEND.equals(intent.getAction())) {
                String userid = intent.getStringExtra(IntentKey.USER_ID);
                requestCheckUserInfo(userid, true);
            }
        }
    }

    private void requestCheckUserInfo(final String userid, final boolean isAgree) {
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
                    Person person = new Person();
                    person.setId(userid);
                    person.setName(username);
                    person.setBeizhuname(username);
                    if (isAgree) {
                        person.setIsNew("false");
                    } else {
                        person.setIsNew("true");

                    }
                    //更新数据库
                    if (!friendDao.isHavePerson(userid)) {
                        friendDao.add(person);
                        //刷新界面
                        persons.add(person);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }));
    }
}
