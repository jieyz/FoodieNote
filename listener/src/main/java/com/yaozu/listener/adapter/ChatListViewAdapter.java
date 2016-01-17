package com.yaozu.listener.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yaozu.listener.R;
import com.yaozu.listener.db.model.ChatListInfo;
import com.yaozu.listener.widget.SoundWaveView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 耀祖 on 2016/1/17.
 */
public class ChatListViewAdapter extends BaseAdapter {
    private List<ChatListInfo> mChatlists = new ArrayList<ChatListInfo>();
    private Context mContext;
    public ChatListViewAdapter(Context context,List<ChatListInfo> chatlists){
        mContext = context;
        mChatlists = chatlists;
    }

    public void updateData(ChatListInfo info){
        boolean isHave = false;
        for(ChatListInfo chatListInfo:mChatlists){
            if(chatListInfo.getUserid().equals(info.getUserid())){
                chatListInfo.setLastchatcontent(info.getLastchatcontent());
                isHave = true;
            }
        }
        if(!isHave){
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
        if(convertView == null){
            holder = new ViewHolder();
            view = View.inflate(mContext, R.layout.list_chatlist_item, null);
            holder.username = (TextView) view.findViewById(R.id.chat_list_name);
            holder.lastcontent = (TextView) view.findViewById(R.id.chat_list_lastchatcontent);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        ChatListInfo chatListInfo = mChatlists.get(i);
        holder.username.setText(chatListInfo.getUserid());
        holder.lastcontent.setText(chatListInfo.getLastchatcontent());
        return view;
    }

    public class ViewHolder {
        public TextView username;
        public TextView lastcontent;
        public ImageView icon;
    }
}
