package com.yaozu.listener.activity;

import android.os.Bundle;
import android.widget.ListView;

import com.yaozu.listener.R;
import com.yaozu.listener.adapter.MyPlanAdapter;

/**
 * Created by jieyaozu on 2016/4/9.
 */
public class MyPlanActivity extends SwipeBackBaseActivity {
    private ListView listView;
    private MyPlanAdapter myPlanAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myplan);
        findviews();
        initview();
    }

    private void findviews() {
        listView = (ListView) findViewById(R.id.myplan_listview);
    }

    private void initview() {
        myPlanAdapter = new MyPlanAdapter(this);
        listView.setAdapter(myPlanAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
