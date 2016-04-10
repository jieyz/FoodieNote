package com.yaozu.listener.fragment.social;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.yaozu.listener.R;
import com.yaozu.listener.activity.CropImageActivity;
import com.yaozu.listener.activity.LoginActivity;
import com.yaozu.listener.activity.MyPlanActivity;
import com.yaozu.listener.activity.UserIconDetail;
import com.yaozu.listener.constant.Constant;
import com.yaozu.listener.constant.DataInterface;
import com.yaozu.listener.constant.IntentKey;
import com.yaozu.listener.fragment.BaseFragment;
import com.yaozu.listener.listener.UploadListener;
import com.yaozu.listener.utils.FileUtil;
import com.yaozu.listener.utils.IntentUtil;
import com.yaozu.listener.utils.NetUtil;
import com.yaozu.listener.utils.User;
import com.yaozu.listener.utils.VolleyHelper;
import com.yaozu.listener.widget.RoundCornerImageView;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by 耀祖 on 2015/12/5.
 */
public class MyselfFragment extends BaseFragment implements View.OnClickListener {
    private RelativeLayout mQuit;
    //昵称
    private RelativeLayout nickName;
    //昵称
    private TextView nickName1, nickName2;
    //我的计划
    private RelativeLayout myPlan;
    private SharedPreferences sp;
    private RoundCornerImageView userIcon;
    private ImageView fragment_social_mine_usericon_onclick;
    //用户信息
    private User mUser;
    private TextView mAccount_view;
    private RelativeLayout userInfoRl;
    private Dialog dialog;
    private static final int ACTIVITY_RESULT_GALRY = 0;
    private static final int ACTIVITY_RESULT_CROPIMAGE = 1;
    public static String ICON_PATH = FileUtil.getSDPath() + File.separator + "ListenerMusic" + File.separator + "icon.jpg";

    public Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1000:
                    Toast.makeText(getActivity(), "上传头像成功!", Toast.LENGTH_SHORT).show();
                    Bitmap localBitmap = (Bitmap) msg.obj;
                    userIcon.setImageBitmap(localBitmap);
                    userIcon.invalidate();
                    break;
                case 2000:
                    Toast.makeText(getActivity(), "上传头像失败，请重试！", Toast.LENGTH_SHORT).show();
                    FileUtil.deleteFile(ICON_PATH);
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mQuit = (RelativeLayout) view.findViewById(R.id.fragment_social_mine_quit);
        myPlan = (RelativeLayout) view.findViewById(R.id.fragment_social_mine_myplan);
        mAccount_view = (TextView) view.findViewById(R.id.fragment_social_mine_account);
        userInfoRl = (RelativeLayout) view.findViewById(R.id.fragment_social_mine_userinfo_rl);
        nickName = (RelativeLayout) view.findViewById(R.id.fragment_social_mine_nickname_rl);
        userIcon = (RoundCornerImageView) view.findViewById(R.id.fragment_social_mine_usericon);
        fragment_social_mine_usericon_onclick = (ImageView) view.findViewById(R.id.fragment_social_mine_usericon_onclick);
        nickName1 = (TextView) view.findViewById(R.id.fragment_social_mine_nickname);
        nickName2 = (TextView) view.findViewById(R.id.fragment_social_mine_nickname2);
        initData();
    }

    private void initData() {
        mUser = new User(getActivity());
        mAccount_view.setText(mUser.getUserAccount());
        mQuit.setOnClickListener(this);
        myPlan.setOnClickListener(this);
        userInfoRl.setOnClickListener(this);
        nickName.setOnClickListener(this);
        fragment_social_mine_usericon_onclick.setOnClickListener(this);
        nickName1.setText(mUser.getUserName());
        nickName2.setText(mUser.getUserName());

        NetUtil.setImageIcon(User.getUserAccount(), userIcon, false, false);
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
        String str = this.getString(R.string.ChooseFile);//2130968579 = Choose File
        Intent localIntent3 = Intent.createChooser(localIntent1, str);
        this.startActivityForResult(localIntent3, ACTIVITY_RESULT_GALRY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ACTIVITY_RESULT_GALRY:
                if (data == null || data.getData() == null) {
                    return;
                }
                Uri localUri = data.getData();
                ContentResolver resolver = getActivity().getContentResolver();
                try {
                    final Bitmap bm = MediaStore.Images.Media.getBitmap(resolver, localUri);
                    Intent cropimage = new Intent(getActivity(), CropImageActivity.class);
                    IntentKey.cropBitmap = bm;
                    startActivityForResult(cropimage, ACTIVITY_RESULT_CROPIMAGE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case ACTIVITY_RESULT_CROPIMAGE:
                if (IntentKey.cropBitmap == null) {
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //保存到本地
                        FileUtil.saveOutput(IntentKey.cropBitmap, ICON_PATH);
                        //上传头像到服务器上
                        NetUtil.uploadIconFile(getActivity(), new File(ICON_PATH), new MyUploadListener());
                    }
                }).start();
                break;
        }
    }

    /**
     * 文件上传监听
     */
    public class MyUploadListener implements UploadListener {

        @Override
        public void uploadSuccess() {
            //压缩小的图片
            Bitmap smallBitmap = FileUtil.compressUserIcon(200, ICON_PATH);
            Message msg = handler.obtainMessage();
            msg.obj = smallBitmap;
            msg.what = 1000;
            handler.sendMessage(msg);
        }

        @Override
        public void uploadFailed() {
            Message msg = handler.obtainMessage();
            msg.what = 1000;
            handler.sendMessage(msg);
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
                showEditNameDialog();
                break;
            case R.id.fragment_social_mine_usericon_onclick:
                IntentUtil.toUserIconActivity(this.getActivity(), User.getUserAccount());
                break;
            case R.id.fragment_social_mine_myplan:
                Intent myplan = new Intent(this.getActivity(), MyPlanActivity.class);
                startActivity(myplan);
                break;
        }
    }

    private void showEditNameDialog() {
        dialog = new Dialog(this.getActivity(), R.style.NobackDialog);
        View view = View.inflate(getActivity(), R.layout.dialog_editname, null);
        final EditText name = (EditText) view.findViewById(R.id.dialog_edit_name);
        name.setText(mUser.getUserName());
        name.setSelection(mUser.getUserName().length());
        TextView dialog_edit_confirm = (TextView) view.findViewById(R.id.dialog_edit_confirm);
        dialog_edit_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = name.getText().toString().trim();
                if (TextUtils.isEmpty(userName)) {
                    Toast.makeText(MyselfFragment.this.getActivity(), "用户名不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                dialog.dismiss();
                requestUpdateUserInfo(mUser.getUserAccount(), userName);
            }
        });
        dialog.setContentView(view);
        dialog.show();
    }

    /**
     * 更新用户信息
     *
     * @param userid
     */
    private void requestUpdateUserInfo(String userid, final String username) {
        String url = null;
        try {
            url = DataInterface.getUpdateUserInfoUrl() + "?userid=" + userid + "&username=" + URLEncoder.encode(username, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        VolleyHelper.getRequestQueue().add(new JsonObjectRequest(Request.Method.GET,
                url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(response.toString());
                int code = jsonObject.getIntValue("code");
                String msg = jsonObject.getString("message");
                Toast.makeText(MyselfFragment.this.getActivity(), msg, Toast.LENGTH_SHORT).show();
                if (code == 1) {
                    //更新本地存储
                    mUser.updateUserName(username);

                    nickName1.setText(mUser.getUserName());
                    nickName2.setText(mUser.getUserName());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }));
    }
}
