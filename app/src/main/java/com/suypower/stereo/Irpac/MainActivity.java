package com.suypower.stereo.Irpac;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.suypower.stereo.Irpac.Activity.CordovaWebViewActivity;
import com.suypower.stereo.Irpac.Activity.LoginActivity;


import com.suypower.stereo.Irpac.Activity.SignaturepadViewPlugin;
import com.suypower.stereo.Irpac.Fragment.CDVWebviewfragment;
import com.suypower.stereo.Irpac.Httpservice.BaseTask;
import com.suypower.stereo.Irpac.Httpservice.HttpUserInfo;
import com.suypower.stereo.Irpac.Httpservice.InterfaceTask;
import com.suypower.stereo.Irpac.System.APP;
import com.suypower.stereo.Irpac.System.AppConfig;
import com.suypower.stereo.Irpac.System.UserInfo;
import com.suypower.stereo.suypowerview.Base.LibConfig;
import com.suypower.stereo.suypowerview.Common.BaseUserInfo;
import com.suypower.stereo.suypowerview.CordovaPlugin.BaseViewPlugin;
import com.suypower.stereo.suypowerview.DB.MessageDB;
import com.suypower.stereo.suypowerview.DataClass.Contacts;
import com.suypower.stereo.suypowerview.FrameController.FragmentMangerX;
import com.suypower.stereo.suypowerview.FrameController.FragmentName;
import com.suypower.stereo.suypowerview.MQTT.ControlCenter;
import com.suypower.stereo.suypowerview.Notification.NotificationClass;
import com.suypower.stereo.suypowerview.PopWindowInfo.CustomPopWindowPlugin;
import com.suypower.stereo.suypowerview.Server.StereoService;
import com.suypower.stereo.suypowerview.ServerReturnData.ReturnData;


import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements CordovaInterface {

    public static final String RECEIVERSTARTUPSERVER = "RESETSERVERBROADCAST";
    //tabbar 按钮
    private ImageView imgtab1, imgtab2, imgtab3;
    private CDVWebviewfragment infocdvWebviewfragment;//资讯
    private CDVWebviewfragment groupcdvWebviewfragment;//关怀
    private CDVWebviewfragment mecdvWebviewfragment;//关怀

    private TextView mark2_main_text_tabbar;
    private Fragment fragmentnow;


    public static FragmentMangerX fragmentMangerX; //fragment框架
    private final ExecutorService threadPool = Executors.newCachedThreadPool();

    private RelativeLayout mainview;


    private Handler handler=new Handler();

    @Override
    public ExecutorService getThreadPool() {
        return threadPool;
    }

    @Override
    public void startActivityForResult(CordovaPlugin command, Intent intent, int requestCode) {

    }

    @Override
    public void setActivityResultCallback(CordovaPlugin plugin) {

    }

    @Override
    public Activity getActivity() {
        return this;
    }


    @Override
    public Object onMessage(String id, Object data) {


        if (id.equals("onReceivedTitle")) {

            ((FragmentName) fragmentnow).setWebTitle();
            return null;
        }
        if (id.equals(BaseViewPlugin.EXITSYSTEM)) {


            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("提示").setMessage("确定退出当前用户？");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                            Logout();

                        }
                    });
                    builder.setNegativeButton("取消",null);
                    AlertDialog alertDialog=builder.create();
                    alertDialog.show();
                }
            },200);



            return null;
        }




        if (id.equals(BaseViewPlugin.BadgeItemCount)) {
            final JSONObject jsonObject = (JSONObject) data;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {

                        if (jsonObject.getString("badge").equals("0"))

                            mark2_main_text_tabbar.setVisibility(View.INVISIBLE);
                        else {
                            mark2_main_text_tabbar.setVisibility(View.VISIBLE);
                            mark2_main_text_tabbar.setText(jsonObject.getString("badge"));
                        }
                    }
                    catch (Exception e)
                    {e.printStackTrace();}
                }
            });

            return null;
        }

        if (data.getClass().getSimpleName().equals("Message")) {
            //异步不带返回调用
            Map<String, Object> map = new Hashtable<String, Object>();
            map.put("id", id);
            map.put("data", data);
            Message message = new Message();
            message.obj = map;
            message.what = 0;
            if (((Message) data).arg1 == 0)
                infocdvWebviewfragment.onMessage(message);
            if (((Message) data).arg1 == 2)
                groupcdvWebviewfragment.onMessage(message);
            if (((Message) data).arg1 == 3)
                mecdvWebviewfragment.onMessage(message);
        }
        if (data.getClass().getSuperclass().getSimpleName().equals("CordovaPlugin")) {
            //异步带返回调用
            Map<String, Object> map = new Hashtable<String, Object>();
            map.put("id", id);
            map.put("data", data);
            Message message = new Message();
            message.obj = map;
            message.what = 1;

            if (((CordovaPlugin) data).webView.cordovaWebViewId == 0)
                infocdvWebviewfragment.onMessage(message);
            if (((CordovaPlugin) data).webView.cordovaWebViewId == 2)
                groupcdvWebviewfragment.onMessage(message);
            if (((CordovaPlugin) data).webView.cordovaWebViewId == 3)
                mecdvWebviewfragment.onMessage(message);
        }


        return null;
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==11)
        {
            return;
        }

        ((FragmentName) fragmentnow).onResult(requestCode, resultCode, data);


    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
            return;
        setContentView(R.layout.activity_main);
        //连接服务
        mainview = (RelativeLayout) findViewById(R.id.mainview);
        mark2_main_text_tabbar = (TextView)findViewById(R.id.mark2_main_text_tabbar);
        imgtab1 = (ImageView) findViewById(R.id.menu1_main_tabbar);
        imgtab2 = (ImageView) findViewById(R.id.menu2_main_tabbar);
        imgtab3 = (ImageView) findViewById(R.id.menu3_main_tabbar);

        imgtab1.setOnClickListener(onClickListenertab);
        imgtab2.setOnClickListener(onClickListenertab);
        imgtab3.setOnClickListener(onClickListenertab);

        fragmentMangerX = new FragmentMangerX(getFragmentManager(), R.id.container);
        intFragment();

        ((FragmentName) groupcdvWebviewfragment).SetFragmentName("groupcdvWebviewfragment");
        fragmentMangerX.AddFragment(groupcdvWebviewfragment, "groupcdvWebviewfragment");
        fragmentMangerX.FragmentHide("groupcdvWebviewfragment");

        ((FragmentName) mecdvWebviewfragment).SetFragmentName("mecdvWebviewfragment");
        fragmentMangerX.AddFragment(mecdvWebviewfragment, "mecdvWebviewfragment");
        fragmentMangerX.FragmentHide("mecdvWebviewfragment");

        ((FragmentName) infocdvWebviewfragment).SetFragmentName("infocdvWebviewfragment");
        fragmentMangerX.AddFragment(infocdvWebviewfragment, "infocdvWebviewfragment");
        fragmentMangerX.ShowFragment("infocdvWebviewfragment");
        fragmentnow = infocdvWebviewfragment;





        //清除通知栏
        NotificationClass.Clear_Notify();
        resetTabBackInmg();
        imgtab1.setImageDrawable(getResources().getDrawable(R.mipmap.home_select));
        BaseUserInfo.setBaseUserInfo(UserInfo.getBaseUserInfo());
        // 通过通知栏点击打开响应窗口



//        if (Integer.valueOf(android.os.Build.VERSION.SDK) > 17) {
//
//            List<String> list = new ArrayList<>();
//
//            if (!PermissionManger.getNew().checkPermission(PermissionManger.PermissionCallPhone))
//            {
//                list.add(PermissionManger.PermissionCallPhone);
////                this.requestPermissions(new String[]{PermissionManger.PermissionCallPhone}, 1);
//            }
//            if (!PermissionManger.getNew().checkPermission(PermissionManger.PermissionCamera))
//            {
//                list.add(PermissionManger.PermissionCamera);
////                this.requestPermissions(new String[]{PermissionManger.PermissionCamera}, 1);
//            }
//
//            if (!PermissionManger.getNew().checkPermission(PermissionManger.PermissionReadContacts))
//            {
//                list.add(PermissionManger.PermissionReadContacts);
////                this.requestPermissions(new String[]{PermissionManger.PermissionReadContacts}, 1);
//            }
//
//            if (!PermissionManger.getNew().checkPermission(PermissionManger.PermissionWriteContacts))
//            {
//                list.add(PermissionManger.PermissionWriteContacts);
////                this.requestPermissions(new String[]{PermissionManger.PermissionWriteContacts}, 1);
//            }
//
//            if (!PermissionManger.getNew().checkPermission(PermissionManger.PermissionRecordAudio))
//            {
//                list.add(PermissionManger.PermissionRecordAudio);
////                this.requestPermissions(new String[]{PermissionManger.PermissionRecordAudio}, 1);
//            }
//
//            String []p = new String[list.size()];
//            for (int i=0;i<list.size();i++)
//            {
//                p[i] = list.get(i);
//            }
//
//            this.requestPermissions(p, 1);
//
//        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("重新启动服务", "------");
            Intent intent1 = new Intent(MainActivity.this, StereoService.class);
            intent1.putExtra("mode", "1");
            startService(intent1);
            serviceConnection = null;
            bindService(intent1, serviceConnection, BIND_AUTO_CREATE);
        }
    };

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ControlCenter.controlCenter = (ControlCenter) iBinder;
//            ControlCenter.controlCenter.disturb = Config.getKeyShareVarForBoolean(MainTabView.this, "disturb");
//            ControlCenter.controlCenter.msgdisturb = Config.getKeyShareVarForBoolean(MainTabView.this, "msgdisturb");
            ControlCenter.controlCenter.init(UserInfo.getUserInfo().getToken(), UserInfo.getUserInfo().getUserId());
            ControlCenter.controlCenter.setIsRunAPP(true);
            ControlCenter.controlCenter.setIslogin(true);
            ControlCenter.controlCenter.LoopMsgStart();
            ControlCenter.controlCenter.setIsNotification(true);

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i("APP unbind", "退出");
            ControlCenter.controlCenter.setIsRunAPP(false);
            ControlCenter.controlCenter = null;
        }
    };


    /**
     * 初始化各个tab view
     */
    void intFragment() {
        //AppConfig.Tab1Url1;
        infocdvWebviewfragment = new CDVWebviewfragment(0);
        infocdvWebviewfragment.HostUrl = AppConfig.Tab1Url1;//"file:///android_asset/www/apps/pms/html/index.html";// AppConfig.Tab1Url1;
        groupcdvWebviewfragment = new CDVWebviewfragment(2);
        groupcdvWebviewfragment.HostUrl = AppConfig.Tab1Url2;
        mecdvWebviewfragment=new CDVWebviewfragment(3);
        mecdvWebviewfragment.HostUrl = AppConfig.Tab1Url3;
    }


    /**
     * tabbar 按钮点击
     */
    View.OnClickListener onClickListenertab = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            System.gc();
            resetTabBackInmg();


            switch (view.getId()) {
                case R.id.menu1_main_tabbar:
                    imgtab1.setImageDrawable(getResources().getDrawable(R.mipmap.home_select));
                    fragmentMangerX.FragmentHide(fragmentnow);
                    fragmentMangerX.ShowFragment(infocdvWebviewfragment);
                    fragmentnow = infocdvWebviewfragment;
                    break;
                case R.id.menu2_main_tabbar:
                    imgtab2.setImageDrawable(getResources().getDrawable(R.mipmap.message_select));
                    fragmentMangerX.FragmentHide(fragmentnow);
                    fragmentMangerX.ShowFragment(groupcdvWebviewfragment);
                    fragmentnow = groupcdvWebviewfragment;
                    break;
                case R.id.menu3_main_tabbar:
                    imgtab3.setImageDrawable(getResources().getDrawable(R.mipmap.me_select));
                    fragmentMangerX.FragmentHide(fragmentnow);
                    fragmentMangerX.ShowFragment(mecdvWebviewfragment);
                    fragmentnow = mecdvWebviewfragment;

                    break;

            }
        }
    };


    /**
     * 重置tabbar按钮
     */
    void resetTabBackInmg() {
        imgtab1.setImageDrawable(getResources().getDrawable(R.mipmap.home_normal));
        imgtab2.setImageDrawable(getResources().getDrawable(R.mipmap.message_normal));
        imgtab3.setImageDrawable(getResources().getDrawable(R.mipmap.me_normal));
    }

    @Override
    protected void onResume() {


        infocdvWebviewfragment.ResumeWeb();
        groupcdvWebviewfragment.ResumeWeb();
        mecdvWebviewfragment.ResumeWeb();

        super.onResume();
    }

    @Override
    protected void onPause() {

        infocdvWebviewfragment.PauseWeb();
        groupcdvWebviewfragment.PauseWeb();
        mecdvWebviewfragment.PauseWeb();
        super.onPause();
    }

    /**
     * 关闭注销
     */
    public void Logout() {


        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);

        LibConfig.setKeyShareVar("userpwd", "null");

        overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.alpha_exit);
        finish();
    }

    @Override
    protected void onDestroy() {

        infocdvWebviewfragment.close();
        groupcdvWebviewfragment.close();
        mecdvWebviewfragment.close();

        super.onDestroy();
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if (keyCode == 4) {
            moveTaskToBack(false);
            return false;
        } else if (keyCode == 66) {

        }
        return super.onKeyUp(keyCode, event);
    }

}
