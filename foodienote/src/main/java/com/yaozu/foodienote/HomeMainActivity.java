package com.yaozu.foodienote;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


public class HomeMainActivity extends Activity {
    private ImageView mImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_main);

        mImageView = (ImageView) findViewById(R.id.image_view);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeMainActivity.this,"µã»÷ÁËÍ¼Æ¬",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
