package com.yaozu.listener.fragment.social;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.yaozu.listener.R;
import com.yaozu.listener.fragment.BaseFragment;
import com.yaozu.listener.fragment.OnFragmentInteractionListener;

import java.util.ArrayList;

/**
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link SocialFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SocialFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ViewPager mViewPager;
    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
    private RadioGroup mRdioGroup;
    private RadioButton chat, maillist, my;
    private PagerTabStrip pagertabstrip;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SocialFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SocialFragment newInstance(String param1, String param2) {
        SocialFragment fragment = new SocialFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public SocialFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        fragments.add(new ChatFragment());
        fragments.add(new MailListFragment());
        fragments.add(new MyselfFragment());
    }

    @Override
    public void notifyCurrentSongMsg(String name, String singer, int currentPos) {

    }

    @Override
    public void notifySongPlaying() {

    }

    @Override
    public void notifySongPause() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_social, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewPager = (ViewPager) view.findViewById(R.id.social_viewpager);
        mViewPager.setAdapter(new SocialViewPagerAdapter(((FragmentActivity) getActivity()).getSupportFragmentManager()));
        mRdioGroup = (RadioGroup) view.findViewById(R.id.social_actionbar);
        pagertabstrip = (PagerTabStrip) view.findViewById(R.id.social_pagertabstrip);
        pagertabstrip.setDrawFullUnderline(false);
        pagertabstrip.setBackgroundColor(getResources().getColor(R.color.white));
        pagertabstrip.setTabIndicatorColor(getResources().getColor(R.color.appthemecolor));

        chat = (RadioButton) view.findViewById(R.id.social_chat_actionbar);
        maillist = (RadioButton) view.findViewById(R.id.social_maillist_actionbar);
        my = (RadioButton) view.findViewById(R.id.social_my_actionbar);
        chat.setTextColor(getResources().getColor(R.color.appthemecolor));
        mRdioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.social_chat_actionbar:
                        chat.setTextColor(getResources().getColor(R.color.appthemecolor));
                        maillist.setTextColor(getResources().getColor(R.color.black));
                        my.setTextColor(getResources().getColor(R.color.black));
                        mViewPager.setCurrentItem(0);
                        break;
                    case R.id.social_maillist_actionbar:
                        chat.setTextColor(getResources().getColor(R.color.black));
                        maillist.setTextColor(getResources().getColor(R.color.appthemecolor));
                        my.setTextColor(getResources().getColor(R.color.black));
                        mViewPager.setCurrentItem(1);
                        break;
                    case R.id.social_my_actionbar:
                        chat.setTextColor(getResources().getColor(R.color.black));
                        maillist.setTextColor(getResources().getColor(R.color.black));
                        my.setTextColor(getResources().getColor(R.color.appthemecolor));
                        mViewPager.setCurrentItem(2);
                        break;
                }
            }
        });
        
        mViewPager.addOnPageChangeListener(new SocialOnPageChangeListener());
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

    private class SocialViewPagerAdapter extends FragmentPagerAdapter {

        public SocialViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    public class SocialOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (position == 0) {
                chat.setTextColor(getResources().getColor(R.color.appthemecolor));
                maillist.setTextColor(getResources().getColor(R.color.black));
                my.setTextColor(getResources().getColor(R.color.black));
            } else if (position == 1) {
                chat.setTextColor(getResources().getColor(R.color.black));
                maillist.setTextColor(getResources().getColor(R.color.appthemecolor));
                my.setTextColor(getResources().getColor(R.color.black));
            } else if (position == 2) {
                chat.setTextColor(getResources().getColor(R.color.black));
                maillist.setTextColor(getResources().getColor(R.color.black));
                my.setTextColor(getResources().getColor(R.color.appthemecolor));
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
