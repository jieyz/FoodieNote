package com.yaozu.listener.fragment.social;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.yaozu.listener.R;
import com.yaozu.listener.adapter.MailListAdapter;
import com.yaozu.listener.fragment.BaseFragment;

/**
 * Created by 耀祖 on 2015/12/5.
 */
public class MailListFragment extends BaseFragment {
    private ListView mListView;
    private MailListAdapter adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView = (ListView) view.findViewById(R.id.maillist_listview);
        adapter = new MailListAdapter(this.getActivity());
        mListView.setAdapter(adapter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_social_maillist, container, false);
    }

    @Override
    public void notifyCurrentSongMsg(String name, String singer,long album_id, int currentPos) {

    }

    @Override
    public void notifySongPlaying() {

    }

    @Override
    public void notifySongPause() {

    }
}
