package com.suypower.stereo.Irpac.System;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.suypower.stereo.Irpac.Activity.LoginActivity;

import com.suypower.stereo.Irpac.Httpservice.BaseTask;
import com.suypower.stereo.Irpac.Httpservice.HttpChat;
import com.suypower.stereo.Irpac.Httpservice.HttpLogin;
import com.suypower.stereo.Irpac.Httpservice.HttpPhoneBook;
import com.suypower.stereo.Irpac.Httpservice.HttpUserInfo;
import com.suypower.stereo.Irpac.Httpservice.HttpWeApp;
import com.suypower.stereo.Irpac.Httpservice.InterfaceTask;
import com.suypower.stereo.Irpac.MainActivity;
import com.suypower.stereo.Irpac.SplashActivity;
import com.suypower.stereo.suypowerview.AlertView.AlertDlg;
import com.suypower.stereo.suypowerview.Base.LibConfig;
import com.suypower.stereo.suypowerview.Common.BaseUserInfo;
import com.suypower.stereo.suypowerview.Common.Common;
import com.suypower.stereo.suypowerview.DB.MessageDB;
import com.suypower.stereo.suypowerview.PopWindowInfo.CustomPopWindowPlugin;
import com.suypower.stereo.suypowerview.ServerReturnData.ReturnData;
import com.suypower.stereo.suypowerview.UpdateApp.AppUpdate;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 初始化
 * Created by Stereo on 16/5/27.
 */
public class AppContextLauncher {

    private Context context;
    private String username, userpwd;
    private AppLaucherCallback appLaucherCallback;
    private UpdateApp updateApp;
    private HttpLogin httpLogin;
    private AlertDlg alertDlg;


    public AppContextLauncher(Activity activity, final AppLaucherCallback appLaucherCallback) {
        this.appLaucherCallback = appLaucherCallback;
        context = activity;

        username = LibConfig.getKeyShareVarForString("username");
        userpwd = LibConfig.getKeyShareVarForString("userpwd");

        Log.i("view user", username);
        Log.i("view pwd", userpwd);
        //判断是否有用户名和密码，才可以登录
        if (username.equals("null") || userpwd.equals("null")) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    appLaucherCallback.onInitOver(getLoginView());
                }
            }, 1000);
            return;
        }
        UserInfo.Init(username, userpwd);


        /**
         * 更新检查
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateApp = new UpdateApp();
                boolean r = updateApp.CheckVerison();
                if (r)
                    handler.sendEmptyMessage(0);
                else
                    handler.sendEmptyMessage(1);
            }
        }).start();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 0://更新
                    appLaucherCallback.StartUpdateApp();

//                    if (Common.IsWifi(APP.getApp())) {
//                        AppUpdate appUpdate = new AppUpdate(iUpdateResult);
//                        appUpdate.downloadAPK(AppConfig.AppUpgrade + "download");
//
//                    } else {
//
//                    }

                    alertDlg = new AlertDlg(context, AlertDlg.AlertEnum.INFO);
                    alertDlg.setContentText("发现新的版本，是否立即更新？");
                    alertDlg.setCancelClickLiseter(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDlg.dismiss();
//                                httpLogin = new HttpLogin(interfaceTask);
//                                httpLogin.startTask();
                            System.exit(0);
                            return;
                        }
                    });
                    alertDlg.setOkClickLiseter(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDlg.dismiss();
                            AppUpdate appUpdate = new AppUpdate(iUpdateResult);
                            appUpdate.downloadAPK(AppConfig.AppUpgrade + "download");
                            Toast.makeText(APP.getApp(), "正在更新，请稍等", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    });
                    alertDlg.show();


                    break;
                case 1://登录
                    httpLogin = new HttpLogin(interfaceTask);
                    httpLogin.isToken=true;
                    httpLogin.startTask();
                    break;
            }

        }
    };


    AppUpdate.IUpdateResult iUpdateResult = new AppUpdate.IUpdateResult() {


        @Override
        public void DownloadResult(int state, String msg) {

            Toast.makeText(APP.getApp(), "更新失败", Toast.LENGTH_SHORT).show();
            httpLogin = new HttpLogin(interfaceTask);
            httpLogin.startTask();

        }
    };


    InterfaceTask interfaceTask = new InterfaceTask() {
        @Override
        public void TaskResultForMessage(Message message) {


            if (message.what == BaseTask.LoginTask) {
                if (message.arg1 != BaseTask.SUCCESS) {

                    appLaucherCallback.onInitOver(getLoginView());
                    return;
                } else {


                    HttpUserInfo httpUserInfo = new HttpUserInfo(interfaceTask, HttpUserInfo.GETBASEUSERINFO);
                    httpUserInfo.startTask();

                }
                return;
            }

            Log.e("准备进行业务数据获取", "ok");
            if (message.what == BaseTask.UserInfoTask) {
                if (message.arg2 == HttpUserInfo.GETBASEUSERINFO) {
                    if (message.arg1 != BaseTask.SUCCESS) {
                        Toast.makeText(APP.getApp(), "获取个人信息失败", Toast.LENGTH_SHORT).show();
                        appLaucherCallback.onInitOver(getLoginView());
                        return;
                    } else {

                        appLaucherCallback.onInitOver(getMainView());
                        System.gc();
                    }
                }


                return;
            }
        }
    };


    /**
     * 返回登录view
     *
     * @return
     */
    private Intent getLoginView() {
        Intent intent = new Intent(context, LoginActivity.class);
        return intent;
    }

    /**
     * 返回Mainview
     *
     * @return
     */
    private Intent getMainView() {
        Intent intent = new Intent(context, MainActivity.class);
        return intent;
    }


    /**
     * 接口
     */
    public interface AppLaucherCallback {
        void onInitOver(Intent intent);

        void StartUpdateApp();
    }
}
