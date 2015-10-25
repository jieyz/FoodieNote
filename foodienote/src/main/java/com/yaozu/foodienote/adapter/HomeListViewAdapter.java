package com.yaozu.foodienote.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.yaozu.foodienote.R;

/**
 * Created by 耀祖 on 2015/10/17.
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
        ImageView userface = (ImageView) view.findViewById(R.id.list_item_userface);
        userface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"这是用户头像",Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}
