package com.yaozu.listener.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.yaozu.listener.R;
import com.yaozu.listener.YaozuApplication;
import com.yaozu.listener.constant.DataInterface;
import com.yaozu.listener.constant.IntentKey;
import com.yaozu.listener.playlist.model.Song;
import com.yaozu.listener.service.MusicService;
import com.yaozu.listener.utils.NetUtil;
import com.yaozu.listener.utils.VolleyHelper;
import com.yaozu.listener.widget.SoundWaveView;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by jieyaozu on 2015/10/17.
 */
public class HomeListViewAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Song> songs;
    private int mCurrentPlayingPos = -1;
    private View mView;

    public HomeListViewAdapter(Context context) {
        mContext = context;
    }

    public void setSongData(ArrayList<Song> songs) {
        this.songs = songs;
    }

    public void setCurrentPlayingPos(int pos) {
        mCurrentPlayingPos = pos;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int position) {
        return mView;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = null;
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            view = View.inflate(mContext, R.layout.list_song_item, null);
            holder.songName = (TextView) view.findViewById(R.id.song_name);
            holder.singer = (TextView) view.findViewById(R.id.song_singer);
            holder.indicate = (ImageView) view.findViewById(R.id.playing_item);
            holder.soundWaveView = (SoundWaveView) view.findViewById(R.id.sound_wave);
            holder.uploadFile = (TextView) view.findViewById(R.id.upload_file);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        final Song song = songs.get(position);
        //判断文件是否存在
        File file = new File(song.getFileUrl());
        final boolean exist = file.exists();

        holder.songName.setText(song.getTitle());
        holder.singer.setText(song.getSinger() + " -- " + song.getAlbum());
        //如果不存在把颜色置灰
        if(!exist){
            holder.songName.setTextColor(Color.parseColor("#EAEAEA"));
            holder.singer.setTextColor(Color.parseColor("#EAEAEA"));
        }else{
            holder.songName.setTextColor(Color.parseColor("#363636"));
            holder.singer.setTextColor(Color.parseColor("#7F7F7F"));
        }
         //音乐播放时的波浪示意
        if (mCurrentPlayingPos == position) {
            holder.indicate.setVisibility(View.VISIBLE);
            holder.soundWaveView.setVisibility(View.VISIBLE);
            MusicService service = YaozuApplication.getIntance().getMusicService();
            if (service != null && service.isPlaying()) {
                holder.soundWaveView.start();
            }
            mView = holder.soundWaveView;
        } else {
            holder.indicate.setVisibility(View.INVISIBLE);
            holder.soundWaveView.setVisibility(View.GONE);
            holder.soundWaveView.stop();
        }

        final int currentPosition = position;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!exist){
                    Toast.makeText(mContext,"文件不存在！",Toast.LENGTH_SHORT).show();
                    return;
                }
                setCurrentPlayingPos(position);
                MusicService service = YaozuApplication.getIntance().getMusicService();
                if (service == null) {
                    Intent intent = new Intent(mContext, MusicService.class);
                    //intent.putExtra(IntentKey.MEDIA_FILE_URL, song.getFileUrl());
                    intent.putExtra(IntentKey.MEDIA_CURRENT_INDEX, currentPosition);
                    intent.putExtra(IntentKey.MEDIA_FILE_LIST, songs);
                    mContext.startService(intent);
                } else {
                    if (service.getmCurrentIndex() == currentPosition) {
                        return;
                    }
                    service.setmSongs(songs);
                    service.switchNextSong(currentPosition);
                }
            }
        });

        holder.uploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NetUtil.uploadSongIfNotExist(song, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(response.toString());
                        int code = jsonObject.getIntValue("code");
                        //表示服务器上没有此歌曲
                        if(code == 1){
                            NetUtil.uploadFile(mContext, song, new File(song.getFileUrl()), null).start();
                        }else{
                            Log.e("HomeListViewAdapter","=====歌曲已经在服务器上存在=====>");
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
            }
        });
        return view;
    }

    public class ViewHolder {
        public TextView songName;
        public TextView singer;
        public ImageView indicate;
        public SoundWaveView soundWaveView;
        public TextView uploadFile;
    }
}
