package com.suypower.stereo.Irpac.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.suypower.stereo.Irpac.Httpservice.BaseTask;
import com.suypower.stereo.Irpac.Httpservice.HttpChat;
import com.suypower.stereo.Irpac.Httpservice.HttpLogin;
import com.suypower.stereo.Irpac.Httpservice.HttpPhoneBook;
import com.suypower.stereo.Irpac.Httpservice.HttpUserInfo;
import com.suypower.stereo.Irpac.Httpservice.HttpWeApp;
import com.suypower.stereo.Irpac.Httpservice.InterfaceTask;
import com.suypower.stereo.Irpac.MainActivity;
import com.suypower.stereo.Irpac.R;
import com.suypower.stereo.Irpac.System.APP;
import com.suypower.stereo.Irpac.System.AppConfig;
import com.suypower.stereo.Irpac.System.UpdateApp;
import com.suypower.stereo.Irpac.System.UserInfo;
import com.suypower.stereo.suypowerview.AlertView.AlertDlg;
import com.suypower.stereo.suypowerview.Base.LibConfig;
import com.suypower.stereo.suypowerview.Common.BaseUserInfo;
import com.suypower.stereo.suypowerview.Common.Common;
import com.suypower.stereo.suypowerview.CustomView.STImageView;
import com.suypower.stereo.suypowerview.DB.MessageDB;
import com.suypower.stereo.suypowerview.PopWindowInfo.CustomPopWindowPlugin;
import com.suypower.stereo.suypowerview.ServerReturnData.ReturnData;
import com.suypower.stereo.suypowerview.UpdateApp.AppUpdate;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {


    private ImageView btnreturn;
    private TextView title, addrconfig, t2;
    private Button btnlogin;
    private EditText editusername, edituserpwd;
    //    private ImageView clear1, clear2;
    private STImageView stImageView;
    private UpdateApp updateApp;
    private RelativeLayout backview;
    private AlertDlg alertDlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnreturn = (ImageView) findViewById(R.id.btnreturn);
        btnreturn.setOnClickListener(onClickListenerreturn);
        stImageView = (STImageView) findViewById(R.id.nickimg);
        backview = (RelativeLayout) findViewById(R.id.backview);

        stImageView.setmRadius(16);
        stImageView.setmIsCircle(false);
        stImageView.reDraw();
        stImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        title = (TextView) findViewById(R.id.title);
        addrconfig = (TextView) findViewById(R.id.txtaddr);

        addrconfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("服务器设置");
                final EditText serverHost = new EditText(LoginActivity.this);
                serverHost.setText(LibConfig.getKeyShareVarForString("serverUrl"));
                builder.setView(serverHost);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (serverHost.getText().toString().equals("")) {

                            dialog.dismiss();
                            return;
                        }
                        LibConfig.setKeyShareVar("serverUrl", serverHost.getText().toString());
                        AppConfig.cHost = String.format("http://%1$s", serverHost.getText().toString());


                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("取消", null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });
//        t2 = (TextView) findViewById(R.id.t2);
        btnlogin = (Button) findViewById(R.id.btnlogin);
        btnlogin.setOnClickListener(onClickListenerlogin);
        editusername = (EditText) findViewById(R.id.editusernma);
//        editusername.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                String nickimgid = LibConfig.getKeyShareVarForString(editusername.getText().toString());
//                if (!nickimgid.equals("null")) {
//                    if (Common.checkCacheIsExits(nickimgid, ".jpg")) {
//                        Bitmap bitmap = BitmapFactory.decodeFile(getCacheDir()
//                                + File.separator + nickimgid + ".jpg");
//                        if (bitmap != null) {
//                            stImageView.setImageBitmap(bitmap);
//                            stImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
//                        } else
//                            stImageView.setImageDrawable(getResources().getDrawable(R.mipmap.default_user));
//                    }
//                } else
//                    stImageView.setImageDrawable(getResources().getDrawable(R.mipmap.default_user));
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
        edituserpwd = (EditText) findViewById(R.id.edituserpwd);


//        clear1 = (ImageView) findViewById(R.id.btnclear1);
//        clear2 = (ImageView) findViewById(R.id.btnclear2);
//        clear1.setOnClickListener(onClickListenerclear);
//        clear2.setOnClickListener(onClickListenerclear);

//        if (!LibConfig.getKeyShareVarForString("username").equals("null")) {
//            editusername.setText(LibConfig.getKeyShareVarForString("username"));
//            String nickimgid = LibConfig.getKeyShareVarForString(LibConfig.getKeyShareVarForString("username"));
//            if (!nickimgid.equals("null")) {
//                if (Common.checkCacheIsExits(nickimgid, ".jpg")) {
//                    Bitmap bitmap = BitmapFactory.decodeFile(getCacheDir()
//                            + File.separator + nickimgid + ".jpg");
//                    if (bitmap != null) {
//                        stImageView.setImageBitmap(bitmap);
//                        stImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
//                    } else
//                        stImageView.setImageDrawable(getResources().getDrawable(R.mipmap.default_user));
//                }
//            } else
//                stImageView.setImageDrawable(getResources().getDrawable(R.mipmap.default_user));
//            edituserpwd.requestFocus();
//        }


        backview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(editusername.getWindowToken(), 0);
                im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(edituserpwd.getWindowToken(), 0);
            }
        });


        /**
         * 更新检查
         */
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                updateApp = new UpdateApp();
//                boolean r = updateApp.CheckVerison();
//                if (r)
//                    handler.sendEmptyMessage(0);
//            }
//        }).start();


    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            final AppConfig appConfig = new AppConfig();
            switch (msg.what) {
                case 0://更新

                    alertDlg = new AlertDlg(LoginActivity.this, AlertDlg.AlertEnum.INFO);
                    alertDlg.setContentText("发现新的版本，是否立即更新？");
                    alertDlg.setCancelClickLiseter(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDlg.dismiss();
//                            HttpLogin httpLogin = new HttpLogin(interfaceTask);
//                            httpLogin.startTask();
                            System.exit(0);
                            return;
                        }
                    });
                    alertDlg.setOkClickLiseter(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDlg.dismiss();
                            btnlogin.setEnabled(false);
                            AppUpdate appUpdate = new AppUpdate(iUpdateResult);
                            appUpdate.downloadAPK(appConfig.AppUpgrade + "download");
                            Toast.makeText(APP.getApp(), "正在更新，请稍等", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    });
                    alertDlg.show();


//                    if (Common.IsWifi(APP.getApp())) {
//                        AppUpdate appUpdate = new AppUpdate(null);
//                        appUpdate.downloadAPK(AppConfig.AppUpgrade + "download");
//
//                    } else {
//                        alertDlg = new AlertDlg(LoginActivity.this, AlertDlg.AlertEnum.INFO);
//                        alertDlg.setContentText("发现新的版本，是否立即更新？");
//                        alertDlg.setOkClickLiseter(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                alertDlg.dismiss();
//                                CustomPopWindowPlugin.ShowPopWindow(editusername,getLayoutInflater(),"正在更新");
//                                AppUpdate appUpdate = new AppUpdate(iUpdateResult);
//                                appUpdate.downloadAPK(AppConfig.AppUpgrade + "download");
//                                return;
//                            }
//                        });
//                        alertDlg.show();
//                    }
                    break;
            }

        }
    };

    AppUpdate.IUpdateResult iUpdateResult = new AppUpdate.IUpdateResult() {


        @Override
        public void DownloadResult(int state, String msg) {

            Toast.makeText(APP.getApp(), "更新失败", Toast.LENGTH_SHORT).show();
            btnlogin.setEnabled(true);

        }
    };


    /**
     * md5加密
     *
     * @param str
     * @return
     */
    public String md5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte[] byteDigest = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < byteDigest.length; offset++) {
                i = byteDigest[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            // 32位加密
            return buf.toString();
            // 16位的加密
//			 return buf.toString().substring(8, 24);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 登录
     */
    View.OnClickListener onClickListenerlogin = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (LibConfig.getKeyShareVarForString("serverUrl").equals(""))
            {
                Toast.makeText(LoginActivity.this, "请设置服务器地址", Toast.LENGTH_SHORT).show();
                return;
            }
            AppConfig.cHost = String.format("http://%1$s", LibConfig.getKeyShareVarForString("serverUrl"));
            if (editusername.getText().toString().trim().equals("") ||
                    edituserpwd.getText().toString().trim().equals("")) {
                Toast.makeText(LoginActivity.this, "请输入用户名或密码", Toast.LENGTH_SHORT).show();
                btnlogin.setEnabled(true);
                return;
            }
            CustomPopWindowPlugin.ShowPopWindow(btnlogin, getLayoutInflater(), "登录...");
            UserInfo.Init(editusername.getText().toString().trim(),
                    md5(edituserpwd.getText().toString().trim()));


            btnlogin.setEnabled(false);


            /**
             * 更新检查
             */
            new Thread(new Runnable() {
                @Override
                public void run() {
                    updateApp = new UpdateApp();
                    boolean r = updateApp.CheckVerison();
                    if (r)
                        handlerchkVer.sendEmptyMessage(0);
                    else
                        handlerchkVer.sendEmptyMessage(1);
                }
            }).start();


        }
    };


    Handler handlerchkVer = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final AppConfig appConfig = new AppConfig();
            switch (msg.what) {
                case 0://更新

                    alertDlg = new AlertDlg(LoginActivity.this, AlertDlg.AlertEnum.INFO);
                    alertDlg.setContentText("发现新的版本，是否立即更新？");
                    alertDlg.setCancelClickLiseter(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDlg.dismiss();
//                            HttpLogin httpLogin = new HttpLogin(interfaceTask);
//                            httpLogin.startTask();
                            System.exit(0);
                            return;
                        }
                    });
                    alertDlg.setOkClickLiseter(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDlg.dismiss();
                            AppUpdate appUpdate = new AppUpdate(iUpdateResult);
                            appUpdate.downloadAPK(appConfig.AppUpgrade + "download");
                            Toast.makeText(APP.getApp(), "正在更新，请稍等", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    });
                    alertDlg.show();


//                    if (Common.IsWifi(APP.getApp())) {
//                        AppUpdate appUpdate = new AppUpdate(iUpdateResult);
//                        appUpdate.downloadAPK(AppConfig.AppUpgrade + "download");
//
//                    } else {
//
//                    }
                    break;
                case 1://登录
                    HttpLogin httpLogin = new HttpLogin(interfaceTask);
                    httpLogin.isToken = false;
                    httpLogin.startTask();
                    break;
            }

        }
    };


    InterfaceTask interfaceTask = new InterfaceTask() {
        @Override
        public void TaskResultForMessage(Message message) {
            if (message.what == BaseTask.LoginTask) {
                if (message.arg1 == BaseTask.SUCCESS) {
                    HttpUserInfo httpUserInfo = new HttpUserInfo(interfaceTask, HttpUserInfo.GETBASEUSERINFO);
                    httpUserInfo.startTask();

                    return;
                } else {
                    btnlogin.setEnabled(true);
                    CustomPopWindowPlugin.CLosePopwindow();
                    Toast.makeText(LoginActivity.this, message.obj.toString(), Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            if (message.what == BaseTask.UserInfoTask) {

                if (message.arg2 == HttpUserInfo.GETBASEUSERINFO) {
                    if (message.arg1 != BaseTask.SUCCESS) {
                        CustomPopWindowPlugin.CLosePopwindow();
                        Toast.makeText(APP.getApp(), "获取个人信息失败", Toast.LENGTH_SHORT).show();
                        btnlogin.setEnabled(true);
                        return;
                    } else {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.alpha, R.anim.alpha_exit);
                    }
                }
            }


        }
    };
    /**
     * 返回
     */
    View.OnClickListener onClickListenerreturn = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
            overridePendingTransition(R.anim.alpha, R.anim.slide_out_to_bottom);
        }
    };


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == 4) {
            finish();
            overridePendingTransition(R.anim.alpha, R.anim.slide_out_to_bottom);
        }
        return super.onKeyUp(keyCode, event);
    }


}
