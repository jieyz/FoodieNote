package com.yaozu.listener.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yaozu.listener.R;
import com.yaozu.listener.db.model.ChatDetailInfo;
import com.yaozu.listener.db.model.ChatListInfo;
import com.yaozu.listener.utils.NetUtil;
import com.yaozu.listener.utils.User;
import com.yaozu.listener.widget.RoundCornerImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 耀祖 on 2016/1/18.
 */
public class ChatDetailListViewAdapter extends BaseAdapter {
    private List<ChatDetailInfo> mChatDetaillists = new ArrayList<ChatDetailInfo>();
    private Context mContext;
    //当前登录用户的头像
    private Bitmap thisUserBitmap;
    //其它用户的头像
    private Bitmap otherUserBitmap;

    public ChatDetailListViewAdapter(Context context, String otherUserid, List<ChatDetailInfo> chatlists) {
        mContext = context;
        mChatDetaillists = chatlists;
        thisUserBitmap = User.getUserIcon();
        NetUtil.setImageIcon(otherUserid, null, false, false);
        otherUserBitmap = NetUtil.getLocalOtherUserIcon(otherUserid);
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
        return mChatDetaillists.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view = View.inflate(mContext, R.layout.activity_social_chatdetail_item, null);
        RelativeLayout senderrl = (RelativeLayout) view.findViewById(R.id.chat_detail_sender_rl);
        RelativeLayout myselfrl = (RelativeLayout) view.findViewById(R.id.chat_detail_myself_rl);
        TextView sendercontent = (TextView) view.findViewById(R.id.chat_detail_sendercontent);
        TextView myselfcontent = (TextView) view.findViewById(R.id.chat_detail_myselfcontent);
        RoundCornerImageView senderIcon = (RoundCornerImageView) view.findViewById(R.id.activity_chatdetail_item_sender_icon);
        RoundCornerImageView myselfIcon = (RoundCornerImageView) view.findViewById(R.id.activity_chatdetail_item_myself_icon);

        ChatDetailInfo info = mChatDetaillists.get(i);
        if ("true".equals(info.getIssender())) {
            senderrl.setVisibility(View.VISIBLE);
            myselfrl.setVisibility(View.GONE);
            if (otherUserBitmap != null) {
                senderIcon.setImageBitmap(otherUserBitmap);
            }
            sendercontent.setText(info.getChatcontent());
        } else {
            senderrl.setVisibility(View.GONE);
            myselfrl.setVisibility(View.VISIBLE);
            myselfIcon.setImageBitmap(thisUserBitmap);
            myselfcontent.setText(info.getChatcontent());
        }
        return view;
    }
}
