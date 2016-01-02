package com.yaozu.listener.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.yaozu.listener.R;
import com.yaozu.listener.adapter.HomeViewPagerAdapter;
import com.yaozu.listener.fragment.music.MusicHomeFragment;
import com.yaozu.listener.fragment.music.MusicLocalFragment;
import com.yaozu.listener.fragment.social.SocialFragment;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ViewPager viewpager;
    private RadioButton homeMusicRadioButton;
    private RadioButton homeMineRadioButton;
    private RadioGroup homeRadioGroup;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public HomeFragment() {
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
        return inflater.inflate(R.layout.fragment_main_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewpager = (ViewPager) view.findViewById(R.id.main_home_viewpager);
        homeRadioGroup = (RadioGroup) view.findViewById(R.id.home_actionbar_radiogroup);
        homeMusicRadioButton = (RadioButton) view.findViewById(R.id.home_actionbar_music_fragment);
        homeMineRadioButton = (RadioButton) view.findViewById(R.id.home_actionbar_mine_fragment);
        homeMusicRadioButton.setChecked(true);
        homeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.home_actionbar_music_fragment:
                        viewpager.setCurrentItem(0);
                        break;
                    case R.id.home_actionbar_mine_fragment:
                        viewpager.setCurrentItem(1);
                        break;
                }
            }
        });
        ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(new MusicHomeFragment());
        fragments.add(new SocialFragment());
        viewpager.setAdapter(new HomeViewPagerAdapter(((FragmentActivity) getActivity()).getSupportFragmentManager(), fragments));

        viewpager.addOnPageChangeListener(new HomeOnPageChangeListener());
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

    public class HomeOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (position == 0) {
                homeMusicRadioButton.setChecked(true);
            } else {
                homeMineRadioButton.setChecked(true);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
