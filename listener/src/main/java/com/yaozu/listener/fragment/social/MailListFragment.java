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
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
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
    private LinearLayout parentView;
    private PopupWindow popupwindow;
    private int mWindowWidth;
    private int mWindowHeight;
    //通讯录数据DAO
    private static FriendDao friendDao;
    public static List<Person> persons;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_expand);
            animation.setFillAfter(true);
            parentView.startAnimation(animation);
        }
    };

    public static Handler updateAdapterHandler = new Handler(){
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
        parentView = (LinearLayout) view.findViewById(R.id.maillist_parentview);
        mListView = (ListView) view.findViewById(R.id.maillist_listview);

        adapter = new MailListAdapter(this.getActivity(), persons, parentView, this);
        mListView.setAdapter(adapter);
        WindowManager wm = (WindowManager) this.getActivity().getSystemService(Context.WINDOW_SERVICE);
        mWindowWidth = wm.getDefaultDisplay().getWidth();
        mWindowHeight = wm.getDefaultDisplay().getHeight();
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
    public void showPopupView() {
        View contentview = View.inflate(this.getActivity(), R.layout.username_select_menu, null);
        GridView gridView = (GridView) contentview.findViewById(R.id.username_select_gridview);
        gridView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return letters.length;
            }

            @Override
            public Object getItem(int i) {
                return null;
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                View v = View.inflate(getActivity(), R.layout.username_select_menu_item, null);
                v.setLayoutParams(new GridView.LayoutParams(mWindowWidth / 4, mWindowWidth / 4));
                TextView text = (TextView) v.findViewById(R.id.username_select_menu_item_text);
                text.setText(letters[i]);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupwindow.dismiss();
                    }
                });
                return v;
            }
        });

        popupwindow = new PopupWindow(contentview, LinearLayout.LayoutParams.MATCH_PARENT, parentView.getHeight());
        popupwindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(150);
                            Message msg = mHandler.obtainMessage();
                            mHandler.sendMessage(msg);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        //设置消失动画
        popupwindow.setAnimationStyle(R.style.mypopwindow_anim_style);
        popupwindow.setFocusable(true);
        popupwindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        int[] location = new int[2];
        parentView.getLocationInWindow(location);
        popupwindow.showAtLocation(parentView, Gravity.TOP | Gravity.LEFT, 0, Constant.HOME_ACTIONBAR + Constant.SOCIAL_ACTIONBAR + getStatusBarHeight());

        Animation scaleAt = AnimationUtils.loadAnimation(getActivity(), R.anim.popup_show);
        //TranslateAnimation translate = new TranslateAnimation(0, 0, popupwindow.getHeight(), 0);
        scaleAt.setFillEnabled(true);
        scaleAt.setInterpolator(new DecelerateInterpolator());
        contentview.startAnimation(scaleAt);
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
            String userid = intent.getStringExtra(IntentKey.USER_ID);
            requestCheckUserInfo(userid);
        }
    }

    private void requestCheckUserInfo(final String userid) {
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
                    person.setIsNew("true");
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
