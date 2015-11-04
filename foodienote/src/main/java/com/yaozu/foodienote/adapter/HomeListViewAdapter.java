package com.yaozu.foodienote.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yaozu.foodienote.R;
import com.yaozu.foodienote.YaozuApplication;
import com.yaozu.foodienote.constant.IntentKey;
import com.yaozu.foodienote.playlist.model.Song;
import com.yaozu.foodienote.service.MusicService;

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
                    service.setMediaPathAndPrepare(song.getFileUrl());
                }
            }
        });
        return view;
    }
}
