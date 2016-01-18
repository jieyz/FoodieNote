package com.yaozu.listener.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yaozu.listener.R;
import com.yaozu.listener.db.model.ChatDetailInfo;
import com.yaozu.listener.db.model.ChatListInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 耀祖 on 2016/1/18.
 */
public class ChatDetailListViewAdapter extends BaseAdapter {
    private List<ChatDetailInfo> mChatDetaillists = new ArrayList<ChatDetailInfo>();
    private Context mContext;

    public ChatDetailListViewAdapter(Context context, List<ChatDetailInfo> chatlists) {
        mContext = context;
        mChatDetaillists = chatlists;
    }

    public void updateAddData(ChatDetailInfo info) {
        mChatDetaillists.add(info);
    }

    @Override
    public int getCount() {
        return mChatDetaillists.size();
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
        TextView view = new TextView(mContext);
        ChatDetailInfo info = mChatDetaillists.get(i);
        view.setText(info.getChatcontent());
        if ("true".equals(info.getIssender())) {
            view.setTextColor(Color.BLACK);
        } else {
            view.setTextColor(Color.GREEN);
        }
        return view;
    }
}
