package com.yaozu.listener.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.yaozu.listener.R;
import com.yaozu.listener.constant.IntentKey;

/**
 * Created by 耀祖 on 2016/1/28.
 */
public class UserIconDetail extends Activity implements View.OnClickListener {
    private String iconPath;
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usericon_detail);
        imageView = (ImageView) findViewById(R.id.activity_usericon_detail);
        iconPath = getIntent().getStringExtra(IntentKey.USER_ICON_PATH);
        Bitmap bmp = BitmapFactory.decodeFile(iconPath);
        imageView.setImageBitmap(bmp);

        imageView.setOnClickListener(this);
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return true;
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.activity_usericon_detail:
                finish();
                break;
        }
    }
}
