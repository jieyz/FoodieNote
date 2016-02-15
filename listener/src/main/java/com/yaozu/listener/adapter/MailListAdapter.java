package com.yaozu.listener.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.yaozu.listener.R;

/**
 * Created by 耀祖 on 2016/2/10.
 */
public class MailListAdapter extends BaseAdapter {
    private Context mContext;
    public MailListAdapter(Context context){
         this.mContext = context;
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view = View.inflate(mContext, R.layout.maillist_list_item,null);
        return view;
    }
}
