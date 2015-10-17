package com.yaozu.foodienote;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.yaozu.foodienote.fragment.DiscoverFragment;
import com.yaozu.foodienote.fragment.HomeFragment;
import com.yaozu.foodienote.fragment.MineFragment;
import com.yaozu.foodienote.fragment.OnFragmentInteractionListener;
import com.yaozu.foodienote.fragment.RankFragment;


public class HomeMainActivity extends Activity implements View.OnClickListener ,OnFragmentInteractionListener{
    private RadioGroup mRadipGroup;
    private RelativeLayout mFirst,mSecond,mThird,mFour;
    private RadioButton  mRadioFirst,mRadioSecond,mRadioThird,mRadioFour;
    private FragmentManager mFragmentManager;
    private String mCurrentFragmentTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_main);
        mFragmentManager = getFragmentManager();
        findViewByIds();
        setOnclickLisener();
    }

    private void findViewByIds(){
/*        mRadipGroup = (RadioGroup) findViewById(R.id.mb_tablelayout);
        mFirst = (RelativeLayout) findViewById(R.id.rl_first);
        mSecond = (RelativeLayout) findViewById(R.id.rl_second);
        mThird = (RelativeLayout) findViewById(R.id.rl_third);
        mFour = (RelativeLayout) findViewById(R.id.rl_four);*/

        mRadioFirst = (RadioButton) findViewById(R.id.radio_first);
        mRadioFirst.setChecked(true);
        mRadioSecond = (RadioButton) findViewById(R.id.radio_second);
        mRadioThird = (RadioButton) findViewById(R.id.radio_third);
        mRadioFour = (RadioButton) findViewById(R.id.radio_four);
    }

    private void setOnclickLisener(){
        mRadioFirst.setOnClickListener(this);
        mRadioSecond.setOnClickListener(this);
        mRadioThird.setOnClickListener(this);
        mRadioFour.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        mRadioFirst.setChecked(false);
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
        }
        if(fragment.isAdded()){
           tr.show(fragment);
        }else{
            tr.add(R.id.main_fragment_container,fragment,tag);
        }
        tr.commit();
        mCurrentFragmentTag = tag;
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
