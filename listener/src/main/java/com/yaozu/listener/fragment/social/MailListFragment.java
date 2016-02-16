package com.yaozu.listener.fragment.social;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
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
import android.widget.TextView;

import com.yaozu.listener.R;
import com.yaozu.listener.adapter.MailListAdapter;
import com.yaozu.listener.constant.Constant;
import com.yaozu.listener.fragment.BaseFragment;

/**
 * Created by 耀祖 on 2015/12/5.
 */
public class MailListFragment extends BaseFragment {
    private ListView mListView;
    private MailListAdapter adapter;
    /**
     * 父布局
     */
    private LinearLayout parentView;
    private PopupWindow popupwindow;
    private int mWindowWidth;
    private int mWindowHeight;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_expand);
            animation.setFillAfter(true);
            parentView.startAnimation(animation);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        parentView = (LinearLayout) view.findViewById(R.id.maillist_parentview);
        mListView = (ListView) view.findViewById(R.id.maillist_listview);
        adapter = new MailListAdapter(this.getActivity(), parentView, this);
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

    private String[] letters = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

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

        popupwindow = new PopupWindow(contentview, LinearLayout.LayoutParams.MATCH_PARENT, mListView.getHeight());
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
}
