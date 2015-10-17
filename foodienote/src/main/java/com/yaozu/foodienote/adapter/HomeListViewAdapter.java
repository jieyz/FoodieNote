package com.yaozu.foodienote.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.yaozu.foodienote.R;

/**
 * Created by Ò«×æ on 2015/10/17.
 */
public class HomeListViewAdapter extends BaseAdapter {
    private Context mContext;
    public HomeListViewAdapter(Context context){
        mContext = context;
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
        View view = View.inflate(mContext, R.layout.list_msg_item,null);
        return view;
    }
}
