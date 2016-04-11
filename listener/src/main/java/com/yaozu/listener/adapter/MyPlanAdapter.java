package com.yaozu.listener.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yaozu.listener.R;

/**
 * Created by jieyaozu on 2016/4/9.
 */
public class MyPlanAdapter extends BaseAdapter {
    private Context context;

    public MyPlanAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return 20;
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
        View view = View.inflate(context, R.layout.list_myplan_item, null);

        TextView textView = (TextView) view.findViewById(R.id.item_myplan_name);
        textView.setText("第" + position + "个计划");
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return view;
    }
}
