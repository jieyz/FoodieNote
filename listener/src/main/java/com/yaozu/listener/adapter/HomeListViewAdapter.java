package com.yaozu.listener.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yaozu.listener.R;
import com.yaozu.listener.YaozuApplication;
import com.yaozu.listener.constant.IntentKey;
import com.yaozu.listener.playlist.model.Song;
import com.yaozu.listener.service.MusicService;

import java.util.List;

/**
 * Created by Ò«×æ on 2015/10/17.
 */
public class HomeListViewAdapter extends BaseAdapter {
    private Context mContext;
    private List<Song> songs;
    public HomeListViewAdapter(Context context){
        mContext = context;
    }

    public void setSongData(List<Song> songs){
        this.songs = songs;
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(mContext, R.layout.list_song_item,null);
        TextView songName = (TextView) view.findViewById(R.id.song_name);
        TextView singer = (TextView) view.findViewById(R.id.song_singer);
        final Song song = songs.get(position);
        songName.setText(song.getTitle());
        singer.setText(song.getSinger());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicService service = YaozuApplication.getIntance().getMusicService();
                if (service == null) {
                    Intent intent = new Intent(mContext, MusicService.class);
                    intent.putExtra(IntentKey.MEDIA_FILE_URL, song.getFileUrl());
                    mContext.startService(intent);
                } else {
                    service.stopPlayBack();
                    service.setMediaSongAndPrepare(song);
                }
            }
        });
        return view;
    }
}
