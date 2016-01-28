package com.yaozu.listener.fragment.social;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yaozu.listener.R;
import com.yaozu.listener.activity.LoginActivity;
import com.yaozu.listener.constant.Constant;
import com.yaozu.listener.fragment.BaseFragment;
import com.yaozu.listener.utils.FileUtil;
import com.yaozu.listener.utils.User;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by 耀祖 on 2015/12/5.
 */
public class MyselfFragment extends BaseFragment implements View.OnClickListener {
    private RelativeLayout mQuit;
    //昵称
    private RelativeLayout nickName;
    private SharedPreferences sp;
    private ImageView userIcon;
    private User mUser;
    private TextView mAccount_view;
    private RelativeLayout userInfoRl;
    private Dialog dialog;
    private static final int ACTIVITY_RESULT_GALRY = 0;
    private static String ICON_PATH = FileUtil.getSDPath() + File.separator + "ListenerMusic" + File.separator+"icon.jpg";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mQuit = (RelativeLayout) view.findViewById(R.id.fragment_social_mine_quit);
        mAccount_view = (TextView) view.findViewById(R.id.fragment_social_mine_account);
        userInfoRl = (RelativeLayout) view.findViewById(R.id.fragment_social_mine_userinfo_rl);
        nickName = (RelativeLayout) view.findViewById(R.id.fragment_social_mine_nickname_rl);
        userIcon = (ImageView) view.findViewById(R.id.fragment_social_mine_usericon);
        initData();
    }

    private void initData() {
        mUser = new User(getActivity());
        mAccount_view.setText(mUser.getUserAccount());
        mQuit.setOnClickListener(this);
        userInfoRl.setOnClickListener(this);
        nickName.setOnClickListener(this);

        Bitmap bitmap = BitmapFactory.decodeFile(ICON_PATH);
        if(bitmap != null){
            userIcon.setImageBitmap(bitmap);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sp = getActivity().getSharedPreferences(Constant.LOGIN_MSG, Context.MODE_PRIVATE);
        return inflater.inflate(R.layout.fragment_social_my, container, false);
    }

    private void showDialog() {
        dialog = new Dialog(this.getActivity(), R.style.NobackDialog);
        View view = View.inflate(getActivity(), R.layout.quit_dialog, null);
        LinearLayout quitAccount = (LinearLayout) view.findViewById(R.id.quit_dialog_quitaccount);
        LinearLayout quitApp = (LinearLayout) view.findViewById(R.id.quit_dialog_quitapp);
        quitAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                mUser.quitLogin();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        });
        quitApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setContentView(view);
        dialog.show();
    }

    /**
     * 打开相册选取图片
     */
    public void getimgefromegalry() {
        Intent localIntent1 = new Intent(Intent.ACTION_GET_CONTENT);
        localIntent1.setType("image/*");
        localIntent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //EffectActivity localCaricatureCreatorActivity = CaricatureSurfaceView.this.m_activity;
        String str = this.getString(R.string.ChooseFile);//2130968579 = Choose File��
        Intent localIntent3 = Intent.createChooser(localIntent1, str);
        this.startActivityForResult(localIntent3, ACTIVITY_RESULT_GALRY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ACTIVITY_RESULT_GALRY:
                if(data == null || data.getData() == null){
                    return;
                }
                Uri localUri = data.getData();
                ContentResolver resolver = getActivity().getContentResolver();
                try {
                    Bitmap bm = MediaStore.Images.Media.getBitmap(resolver, localUri);
                    userIcon.setImageBitmap(bm);
                    //保存到本地
                    saveOutput(bm);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    /**
     * 保存到本地
     * @param croppedImage
     */
    private void saveOutput(Bitmap croppedImage) {
        File file = new File(ICON_PATH);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {

                e.printStackTrace();
            }
        }

        OutputStream outStream;
        try {
            outStream = new FileOutputStream(file);
            croppedImage.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
            Log.i("CropImage", "bitmap saved tosd,path:" + file.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fragment_social_mine_userinfo_rl:
                getimgefromegalry();
                break;
            case R.id.fragment_social_mine_quit:
                showDialog();
                break;
            case R.id.fragment_social_mine_nickname_rl:

                break;
        }
    }
}
