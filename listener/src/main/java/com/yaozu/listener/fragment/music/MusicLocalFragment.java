package com.yaozu.listener.fragment.music;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.yaozu.listener.Infointerface;
import com.yaozu.listener.R;
import com.yaozu.listener.YaozuApplication;
import com.yaozu.listener.adapter.HomeListViewAdapter;
import com.yaozu.listener.db.dao.SongInfoDao;
import com.yaozu.listener.fragment.BaseFragment;
import com.yaozu.listener.fragment.OnFragmentInteractionListener;
import com.yaozu.listener.playlist.model.Song;
import com.yaozu.listener.playlist.provider.JavaMediaScanner;
import com.yaozu.listener.service.MusicService;
import com.yaozu.listener.widget.SoundWaveView;

import java.io.File;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link MusicLocalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MusicLocalFragment extends BaseFragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private Activity mActivity;
    private Infointerface mInfointerface;
    private ListView mListView;
    private HomeListViewAdapter mAdapter;
    private ImageView actionBack;
    private SongInfoDao mSongInfoDao;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MusicLocalFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MusicLocalFragment newInstance(String param1, String param2) {
        MusicLocalFragment fragment = new MusicLocalFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public MusicLocalFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_music_local, container, false);
        mListView = (ListView) view.findViewById(R.id.home_listview);
        actionBack = (ImageView) view.findViewById(R.id.fragment_music_local_back);
        mAdapter = new HomeListViewAdapter(mActivity);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        actionBack.setOnClickListener(this);
        mSongInfoDao = new SongInfoDao(mActivity);
        MusicService service = YaozuApplication.getIntance().getMusicService();
        if (service != null) {
            highLightPlayingItem(service.getmCurrentIndex());
        }
        getData();
    }

    @Override
    public void notifyCurrentSongMsg(String name, String singer, long album_id, int currentPos) {
        highLightPlayingItem(currentPos);
    }

    @Override
    public void notifySongPlaying() {
        start();
    }

    @Override
    public void notifySongPause() {
        pause();
    }

    private void getData() {
/*        String path = Environment.getExternalStorageDirectory().getPath();
        path = path + File.separator + "KuwoMusic" + File.separator + "music";*/
        mAdapter.setSongData((ArrayList<Song>) mSongInfoDao.findAllSongInfo());
        mListView.setAdapter(mAdapter);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        mInfointerface = (Infointerface) activity;
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void highLightPlayingItem(int pos) {
        mAdapter.setCurrentPlayingPos(pos);
    }

    public void pause() {
        SoundWaveView view = (SoundWaveView) mAdapter.getItem(0);
        if (view != null) {
            view.stop();
        }
    }

    public void start() {
        SoundWaveView view = (SoundWaveView) mAdapter.getItem(0);
        if (view != null) {
            view.start();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fragment_music_local_back:
                MusicLocalFragment.this.getFragmentManager().popBackStack();
                break;
        }
    }
}
