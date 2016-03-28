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
import com.yaozu.listener.widget.SoundWaveView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 耀祖 on 2016/1/18.
 */
public class ChatDetailListViewAdapter extends BaseAdapter {
    private List<ChatDetailInfo> mChatDetaillists = new ArrayList<ChatDetailInfo>();
    private Context mContext;

    public ChatDetailListViewAdapter(Context context, String otherUserid, List<ChatDetailInfo> chatlists) {
        mContext = context;
        mChatDetaillists = chatlists;
        NetUtil.setImageIcon(otherUserid, null, false, false);
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
        ViewHolder holder = null;
        View view = null;
        if (convertView == null) {
            view = View.inflate(mContext, R.layout.activity_social_chatdetail_item, null);
            holder = new ViewHolder();
            holder.senderrl = (RelativeLayout) view.findViewById(R.id.chat_detail_sender_rl);
            holder.myselfrl = (RelativeLayout) view.findViewById(R.id.chat_detail_myself_rl);
            holder.sendercontent = (TextView) view.findViewById(R.id.chat_detail_sendercontent);
            holder.myselfcontent = (TextView) view.findViewById(R.id.chat_detail_myselfcontent);
            holder.senderIcon = (RoundCornerImageView) view.findViewById(R.id.activity_chatdetail_item_sender_icon);
            holder.myselfIcon = (RoundCornerImageView) view.findViewById(R.id.activity_chatdetail_item_myself_icon);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        ChatDetailInfo info = mChatDetaillists.get(i);
        if ("true".equals(info.getIssender())) {
            holder.senderrl.setVisibility(View.VISIBLE);
            holder.myselfrl.setVisibility(View.GONE);
            NetUtil.setImageIcon(info.getOtherUserid(), holder.senderIcon, true, false);

            holder.sendercontent.setText(info.getChatcontent());
        } else {
            holder.senderrl.setVisibility(View.GONE);
            holder.myselfrl.setVisibility(View.VISIBLE);
            NetUtil.setImageIcon(User.getUserAccount(), holder.myselfIcon, true, false);
            holder.myselfcontent.setText(info.getChatcontent());
        }
        return view;
    }

    public class ViewHolder {
        public RelativeLayout senderrl;
        public RelativeLayout myselfrl;
        public TextView sendercontent;
        public TextView myselfcontent;
        public RoundCornerImageView senderIcon;
        public RoundCornerImageView myselfIcon;
    }
}
