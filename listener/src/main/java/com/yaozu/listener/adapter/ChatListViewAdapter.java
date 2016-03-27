package com.yaozu.listener.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yaozu.listener.R;
import com.yaozu.listener.YaozuApplication;
import com.yaozu.listener.activity.ChatDetailActivity;
import com.yaozu.listener.constant.IntentKey;
import com.yaozu.listener.db.model.ChatListInfo;
import com.yaozu.listener.db.model.Person;
import com.yaozu.listener.listener.PersonState;
import com.yaozu.listener.listener.PersonStateInterface;
import com.yaozu.listener.utils.NetUtil;
import com.yaozu.listener.widget.RoundCornerImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 耀祖 on 2016/1/17.
 */
public class ChatListViewAdapter extends BaseAdapter implements PersonStateInterface {
    private List<ChatListInfo> mChatlists = new ArrayList<ChatListInfo>();
    private Context mContext;

    public ChatListViewAdapter(Context context, List<ChatListInfo> chatlists) {
        YaozuApplication.personStateInstances.add(this);
        mContext = context;
        mChatlists = chatlists;
    }

    public void updateData(ChatListInfo info) {
        boolean isHave = false;
        for (ChatListInfo chatListInfo : mChatlists) {
            if (chatListInfo.getOtherUserid().equals(info.getOtherUserid())) {
                chatListInfo.setLastchatcontent(info.getLastchatcontent());
                chatListInfo.setUnreadcount(info.getUnreadcount());
                isHave = true;
            }
        }
        if (!isHave) {
            mChatlists.add(info);
        }
    }

    @Override
    public int getCount() {
        return mChatlists.size();
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
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            view = View.inflate(mContext, R.layout.list_chatlist_item, null);
            holder.username = (TextView) view.findViewById(R.id.chat_list_name);
            holder.lastcontent = (TextView) view.findViewById(R.id.chat_list_lastchatcontent);
            holder.unreads = (TextView) view.findViewById(R.id.chat_list_unreads);
            holder.icon = (RoundCornerImageView) view.findViewById(R.id.chat_list_usericon);
            holder.playing = (ImageView) view.findViewById(R.id.chat_list_playing);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        final ChatListInfo chatListInfo = mChatlists.get(i);
        holder.username.setText(chatListInfo.getUsername());
        holder.lastcontent.setText(chatListInfo.getLastchatcontent());
        NetUtil.setImageIcon(chatListInfo.getOtherUserid(), holder.icon, true, false);

        if (!TextUtils.isEmpty(chatListInfo.getUnreadcount()) && !"0".equals(chatListInfo.getUnreadcount())) {
            holder.unreads.setVisibility(View.VISIBLE);
            holder.unreads.setText(chatListInfo.getUnreadcount());
        } else {
            holder.unreads.setVisibility(View.GONE);
        }

        if (PersonState.PLAYING.toString().equals(chatListInfo.getState())) {
            holder.playing.setVisibility(View.VISIBLE);
        } else {
            holder.playing.setVisibility(View.GONE);
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ChatDetailActivity.class);
                intent.putExtra(IntentKey.CHAT_USERID, chatListInfo.getOtherUserid());
                intent.putExtra(IntentKey.CURRENT_SONG_INFO, chatListInfo.getSonginfo());
                intent.putExtra(IntentKey.CURRENT_SONG_STATE, chatListInfo.getState());
                mContext.startActivity(intent);
            }
        });
        return view;
    }

    public class ViewHolder {
        public TextView username;
        public TextView lastcontent;
        public TextView unreads;
        public RoundCornerImageView icon;
        public ImageView playing;
    }

    @Override
    public void updatePersonState(Person person) {
        for (int i = 0; i < mChatlists.size(); i++) {
            ChatListInfo chatInfo = mChatlists.get(i);
            if (person.getId().equals(chatInfo.getOtherUserid())) {
                chatInfo.setState(person.getState());
                chatInfo.setSonginfo(person.getCurrentSong());
                notifyDataSetChanged();
                break;
            }
        }
    }
}
