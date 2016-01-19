package com.yaozu.listener.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
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
        View view  = View.inflate(mContext,R.layout.activity_social_chatdetail_item,null);
        RelativeLayout senderrl = (RelativeLayout) view.findViewById(R.id.chat_detail_sender_rl);
        RelativeLayout myselfrl = (RelativeLayout) view.findViewById(R.id.chat_detail_myself_rl);
        TextView sendercontent = (TextView) view.findViewById(R.id.chat_detail_sendercontent);
        TextView myselfcontent = (TextView) view.findViewById(R.id.chat_detail_myselfcontent);

        ChatDetailInfo info = mChatDetaillists.get(i);
        if ("true".equals(info.getIssender())) {
            senderrl.setVisibility(View.VISIBLE);
            myselfrl.setVisibility(View.GONE);

            sendercontent.setText(info.getChatcontent());
        } else {
            senderrl.setVisibility(View.GONE);
            myselfrl.setVisibility(View.VISIBLE);

            myselfcontent.setText(info.getChatcontent());
        }
        return view;
    }
}
