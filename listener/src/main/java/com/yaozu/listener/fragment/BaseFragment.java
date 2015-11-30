package com.yaozu.listener.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.View;

import com.yaozu.listener.R;

/**
 * Created by 耀祖 on 2015/11/28.
 */
public class BaseFragment extends Fragment {
    private View progressBar;
    public BaseFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar = view.findViewById(R.id.base_layout);
    }

    protected void showProgressBar(){
        if(progressBar != null){
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    protected void hideProgressBar(){
        if(progressBar != null){
            progressBar.setVisibility(View.GONE);
        }
    }
}
