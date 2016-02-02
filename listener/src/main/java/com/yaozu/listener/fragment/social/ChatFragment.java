package com.yaozu.listener.fragment.social;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.yaozu.listener.R;
import com.yaozu.listener.adapter.ChatListViewAdapter;
import com.yaozu.listener.db.dao.ChatListInfoDao;
import com.yaozu.listener.db.model.ChatListInfo;
import com.yaozu.listener.fragment.BaseFragment;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;

/**
 * Created by 耀祖 on 2015/12/5.
 */
public class ChatFragment extends BaseFragment {
    private ListView mListView;
    private ChatListViewAdapter mChatListViewAdapter;
    private ChatListInfoDao chatListInfoDao;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView = (ListView) view.findViewById(R.id.fragment_social_chat_listview);
        chatListInfoDao = new ChatListInfoDao(getActivity());
        mChatListViewAdapter = new ChatListViewAdapter(getActivity(),chatListInfoDao.findAllChatListInfo());
        mListView.setAdapter(mChatListViewAdapter);
    }

    @Override
    public void updateChatListInfo(ChatListInfo info) {
        super.updateChatListInfo(info);
        mChatListViewAdapter.updateData(info);
        mChatListViewAdapter.notifyDataSetChanged();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_social_chat, container, false);
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
