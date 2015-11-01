package com.yaozu.foodienote;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.yaozu.foodienote.activity.MusicHomeActivity;
import com.yaozu.foodienote.fragment.DiscoverFragment;
import com.yaozu.foodienote.fragment.HomeFragment;
import com.yaozu.foodienote.fragment.MineFragment;
import com.yaozu.foodienote.fragment.OnFragmentInteractionListener;
import com.yaozu.foodienote.fragment.RankFragment;
import com.yaozu.foodienote.service.MusicService;


public class HomeMainActivity extends Activity implements View.OnClickListener, OnFragmentInteractionListener {
    private RadioButton mRadioFirst, mRadioSecond, mRadioThird, mRadioFour;
    private FragmentManager mFragmentManager;
    private String mCurrentFragmentTag;
    private ImageButton music_home;
    //播放或者暂停按钮
    private ImageView mPlayPause;
    private YaozuApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = YaozuApplication.getIntance();
        setContentView(R.layout.activity_home_main);
        mFragmentManager = getFragmentManager();
        findViewByIds();
        setOnclickLisener();

        FragmentTransaction tr = mFragmentManager.beginTransaction();
        tr.add(R.id.main_fragment_container, new HomeFragment());
        tr.commit();
    }

    private void findViewByIds() {
/*        mRadioFirst = (RadioButton) findViewById(R.id.radio_first);
        mRadioFirst.setChecked(true);
        mRadioSecond = (RadioButton) findViewById(R.id.radio_second);
        mRadioThird = (RadioButton) findViewById(R.id.radio_third);
        mRadioFour = (RadioButton) findViewById(R.id.radio_four);
        music_home = (ImageButton) findViewById(R.id.music_home);*/
        mPlayPause = (ImageView) findViewById(R.id.mediaplay_play_pause);
    }

    private void setOnclickLisener() {
/*        mRadioFirst.setOnClickListener(this);
        mRadioSecond.setOnClickListener(this);
        mRadioThird.setOnClickListener(this);
        mRadioFour.setOnClickListener(this);
        music_home.setOnClickListener(this);*/
        mPlayPause.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        /*mRadioFirst.setChecked(false);
        mRadioSecond.setChecked(false);
        mRadioThird.setChecked(false);
        mRadioFour.setChecked(false);
        Fragment fragment = null;
        String tag = null;
        final FragmentTransaction tr = mFragmentManager.beginTransaction();
        if(mCurrentFragmentTag != null){
            Fragment currentFragment = mFragmentManager.findFragmentByTag(mCurrentFragmentTag);
            if(currentFragment != null){
                tr.hide(currentFragment);
            }
        }
        switch (v.getId()){
            case R.id.radio_first:
                mRadioFirst.setChecked(true);

                tag = HomeFragment.class.getSimpleName();
                fragment = mFragmentManager.findFragmentByTag(tag);
                if(fragment == null){
                    fragment = new HomeFragment();
                }
                break;
            case R.id.radio_second:
                mRadioSecond.setChecked(true);

                tag = RankFragment.class.getSimpleName();
                fragment = mFragmentManager.findFragmentByTag(tag);
                if(fragment == null){
                    fragment = new RankFragment();
                }
                break;
            case R.id.radio_third:
                mRadioThird.setChecked(true);

                tag = DiscoverFragment.class.getSimpleName();
                fragment = mFragmentManager.findFragmentByTag(tag);
                if(fragment == null){
                    fragment = new DiscoverFragment();
                }
                break;
            case R.id.radio_four:
                mRadioFour.setChecked(true);

                tag = MineFragment.class.getSimpleName();
                fragment = mFragmentManager.findFragmentByTag(tag);
                if(fragment == null){
                    fragment = new MineFragment();
                }
                break;
            case R.id.music_home:
                Intent intent = new Intent(HomeMainActivity.this, MusicHomeActivity.class);
                startActivity(intent);
                return;
        }
        if(fragment.isAdded()){
           tr.show(fragment);
        }else{
            tr.add(R.id.main_fragment_container,fragment,tag);
        }
        tr.commit();
        mCurrentFragmentTag = tag;*/
        switch (v.getId()) {
            case R.id.mediaplay_play_pause:
                MusicService service = app.getMusicService();
                if (service == null) {
                    Intent intent = new Intent(this, MusicService.class);
                    startService(intent);
                }else{
                    if(service.isPlaying()){
                        service.pause();
                    }else{
                        service.start();
                    }
                }
                break;
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {

        super.onResume();
    }
}
