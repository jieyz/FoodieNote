package com.yaozu.listener.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yaozu.listener.R;
import com.yaozu.listener.activity.AddNewFriendActivity;
import com.yaozu.listener.activity.UserDetailActivity;
import com.yaozu.listener.constant.IntentKey;
import com.yaozu.listener.db.dao.FriendDao;
import com.yaozu.listener.db.model.Person;
import com.yaozu.listener.fragment.social.MailListFragment;
import com.yaozu.listener.utils.NetUtil;
import com.yaozu.listener.widget.RoundCornerImageView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 耀祖 on 2016/2/10.
 */
public class MailListAdapter extends BaseAdapter {
    private List<Person> persons;
    private Context mContext;
    private LinearLayout parentView;
    private MailListFragment mf;
    private FriendDao friendDao;

    public MailListAdapter(Context context, List<Person> data, LinearLayout pv, MailListFragment mf) {
        this.mContext = context;
        this.parentView = pv;
        this.mf = mf;
        this.persons = data;
        friendDao = new FriendDao(mContext);
    }

    @Override
    public int getCount() {
        return persons.size() + 1;
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
        View view = null;
        if (i == 0) {
            view = View.inflate(mContext, R.layout.maillist_add_new_friend_item, null);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, AddNewFriendActivity.class);
                    mContext.startActivity(intent);
                }
            });
            return view;
        }
        final Person person = persons.get(i - 1);
        if (person.getId() == null) {
            view = View.inflate(mContext, R.layout.maillist_list_sort_item, null);
            TextView letter = (TextView) view.findViewById(R.id.maillist_list_item_letter);
            letter.setText(person.getName());
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
        view = View.inflate(mContext, R.layout.maillist_list_item, null);
        RoundCornerImageView icon = (RoundCornerImageView) view.findViewById(R.id.maillist_user_icon);
        NetUtil.setImageIcon(person.getId(), icon, true, false);
        TextView name = (TextView) view.findViewById(R.id.maillist_user_name);
        name.setText(person.getName());
        final TextView agree = (TextView) view.findViewById(R.id.new_friend_agree);
        if ("true".equals(person.getIsNew())) {
            agree.setVisibility(View.VISIBLE);
            agree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    agree.setVisibility(View.GONE);
                    person.setIsNew("false");
                    friendDao.update(person);
                }
            });
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, UserDetailActivity.class);
                intent.putExtra(IntentKey.USER_NAME, person.getName());
                intent.putExtra(IntentKey.USER_ID, person.getId());
                //intent.putExtra(IntentKey.USER_ICON_URL, iconurl);
                mContext.startActivity(intent);
            }
        });
        return view;
    }
}
