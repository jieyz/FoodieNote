package com.yaozu.listener.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.yaozu.listener.R;

/**
 * Created by 耀祖 on 2016/2/15.
 */
public class SelectMenuActivity extends BaseActivity {
    private GridView gridView;
    private int mWindowWidth;
    private String[] letters = new String[]{"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.username_select_menu);
        gridView = (GridView) findViewById(R.id.username_select_gridview);
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mWindowWidth = wm.getDefaultDisplay().getWidth();
        gridView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return letters.length;
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
            public View getView(int i, View view, ViewGroup viewGroup) {
                View v = View.inflate(SelectMenuActivity.this,R.layout.username_select_menu_item,null);
                v.setLayoutParams(new GridView.LayoutParams(mWindowWidth / 4, mWindowWidth / 4));
                TextView text = (TextView) v.findViewById(R.id.username_select_menu_item_text);
                text.setText(letters[i]);
                return v;
            }
        });
    }

    @Override
    public void notifyCurrentSongMsg(String name, String singer, long album_id, int currentPos) {

    }

    @Override
    public void notifySongPlaying() {

    }

    @Override
    public void notifySongPause() {

    }
}
