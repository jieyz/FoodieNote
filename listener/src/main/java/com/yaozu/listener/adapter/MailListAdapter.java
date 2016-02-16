package com.yaozu.listener.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.yaozu.listener.R;
import com.yaozu.listener.fragment.social.MailListFragment;

/**
 * Created by 耀祖 on 2016/2/10.
 */
public class MailListAdapter extends BaseAdapter {
    private Context mContext;
    private LinearLayout parentView;
    private MailListFragment mf;

    public MailListAdapter(Context context, LinearLayout pv,MailListFragment mf) {
        this.mContext = context;
        this.parentView = pv;
        this.mf = mf;
    }

    @Override
    public int getCount() {
        return 5;
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
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view = View.inflate(mContext, R.layout.maillist_list_item, null);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.scale_shrink);
                animation.setFillAfter(true);
                parentView.startAnimation(animation);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mf.showPopupView();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        });
        return view;
    }
}
