package com.suypower.stereo.Irpac.Activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.suypower.stereo.Irpac.Httpservice.BaseTask;
import com.suypower.stereo.Irpac.Httpservice.HttpUserInfo;
import com.suypower.stereo.Irpac.Httpservice.InterfaceTask;
import com.suypower.stereo.Irpac.MainActivity;
import com.suypower.stereo.Irpac.R;
import com.suypower.stereo.Irpac.System.APP;
import com.suypower.stereo.Irpac.System.AppConfig;
import com.suypower.stereo.suypowerview.AlertView.AlertSheet;
import com.suypower.stereo.suypowerview.Camera.CameraCore;
import com.suypower.stereo.suypowerview.Camera.CameraHelper;
import com.suypower.stereo.suypowerview.Camera.PreviewPhotoViewPlugin;
import com.suypower.stereo.suypowerview.Common.ImageCore;
import com.suypower.stereo.suypowerview.CordovaPlugin.BaseViewPlugin;
import com.suypower.stereo.suypowerview.CustomLayout.LinearLayoutYXG;
import com.suypower.stereo.suypowerview.DB.MessageDB;
import com.suypower.stereo.suypowerview.DataClass.Contacts;
import com.suypower.stereo.suypowerview.Menu.Menu_Custom;
import com.suypower.stereo.suypowerview.Notification.NotificationClass;
import com.suypower.stereo.suypowerview.PopWindowInfo.CustomPopWindowPlugin;
import com.suypower.stereo.suypowerview.PublishComments.PublishCommentsView;
import com.suypower.stereo.suypowerview.Scan.ScanActivity;
import com.suypower.stereo.suypowerview.Scan.ScanPlugin;
import com.suypower.stereo.suypowerview.ServerReturnData.ReturnData;
import com.suypower.stereo.suypowerview.Share.ShareView;
import com.suypower.stereo.suypowerview.image.ImagePreviewActivity;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CordovaWebViewActivity extends AppCompatActivity implements CordovaInterface {


    public static final int REQUESTCLOSECALLBACK = 8;
    private ImageView btnreturn;
    private CordovaWebView cordovaWebView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout headview;
    private TextView webtitle;
    private ImageView btnright;
    private int downY = 0;
    public String HostUrl = "";
    private Boolean IsRefresh;
    private CordovaPlugin cordovaPlugin;
    private int imageSelectcount = 0;//web选择相册 调用的图片数量
    private AlertSheet alertSheet;
    private String barrightJSwithFunctionID = "";
    private Menu_Custom menu_custom;
    private Boolean IsRightBarMenu = false;
    private List<String> MenuJsFunctionList;
    private RelativeLayout containerview;
    private Boolean IsCloseNotices = false;
    private String CloseNoticesFunction = "";
    private String CloseNoticesArg = "";
    private String strwebtitle = "";
    private LinearLayoutYXG linearLayoutYXG;
    private PublishCommentsView publishCommentsView = null;
    private final ExecutorService threadPool = Executors.newCachedThreadPool();
    private String commentsJS = "";
    private String commentsnumJS = "";
    private View statusbarview;

    private String signCallBackJS;
    private View backView=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cordova_web_view);

        statusbarview = (View) findViewById(R.id.statusbarview);
        linearLayoutYXG = (LinearLayoutYXG) findViewById(R.id.container);
        btnreturn = (ImageView) findViewById(R.id.btnreturn);
        btnreturn.setOnClickListener(onClickListenerreturn);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshlayout);
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);
        headview = (RelativeLayout) findViewById(R.id.headview);
        cordovaWebView = (CordovaWebView) findViewById(R.id.cordovawebview);
        containerview = (RelativeLayout) findViewById(R.id.containerview);
        swipeRefreshLayout.setOnTouchListener(onTouchListener);
        cordovaWebView.setOnTouchListener(onTouchListenerwebview);
        webtitle = (TextView) findViewById(R.id.webtitle);
        btnright = (ImageView) findViewById(R.id.tbnright);
        btnright.setVisibility(View.GONE);

        webtitle.setText("");
        HostUrl = getIntent().getStringExtra("url");
        strwebtitle = getIntent().getStringExtra("title");
        if (!getIntent().getBooleanExtra("ShowTitle", true)) {
            headview.setVisibility(View.GONE);
            statusbarview.setVisibility(View.VISIBLE);
        } else {
            statusbarview.setVisibility(View.GONE);
        }
        IsRefresh = true;

//        cordovaWebView.clearHistory();
//        cordovaWebView.clearFormData();
//        cordovaWebView.clearCache(true);


        cordovaWebView.loadUrl(HostUrl);
        cordovaWebView.cordovaWebViewId = 0;
        menu_custom = new Menu_Custom(this, iMenu);
        NotificationClass.Clear_Notify(NotificationClass.EBINFO);

        swipeRefreshLayout.setEnabled(false);
        webtitle.setText(strwebtitle);
    }

    @Override
    protected void onDestroy() {
        cordovaWebView.destroy();
        super.onDestroy();
    }


    Menu_Custom.IMenu iMenu = new Menu_Custom.IMenu() {
        @Override
        public void ClickMenu(int itemid) {
            cordovaWebView.loadUrl(String.format("javascript:%1$s()", MenuJsFunctionList.get(itemid)));
        }
    };

    View.OnClickListener onClickListenerbtnright = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (!IsRightBarMenu)
                cordovaWebView.loadUrl(String.format("javascript:%1$s()", barrightJSwithFunctionID));
            else {
                menu_custom.ShowMenu(btnright);
            }
        }
    };


    /**
     * 判断webview 滑动
     */
    View.OnTouchListener onTouchListenerwebview = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (IsRefresh) {
//                    Log.e("动作",String.valueOf(cordovaWebView.getScrollY()));
//                    if (cordovaWebView.getScrollY() == 0)
//                        swipeRefreshLayout.setEnabled(true);
//                    else
//                        swipeRefreshLayout.setEnabled(false);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(200);
                                handlermove.sendEmptyMessage(0);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (publishCommentsView != null) {
                    publishCommentsView.CloseView();
                    containerview.removeView(backView);
                    backView=null;
                }

            }
            return false;
        }
    };


    /**
     * 滑动处理
     */
    View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (IsRefresh) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    if (downY == 0)
                        downY = (int) event.getY();
                    int moveY = (int) event.getY();
                    int y = moveY - downY;
                    setLayoutY((int) (y * 0.7));
                    return false;
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int upY = (int) event.getY();
                    Log.i("upY", String.valueOf(upY));
                    downY = 0;
                    if (!swipeRefreshLayout.isRefreshing())
                        setLayoutY(0);
                    return false;
                }
            }
            return false;
        }
    };

    /**
     * 更新webview 布局
     *
     * @param y
     */
    public void setLayoutY(int y) {
        ViewGroup.MarginLayoutParams margin = new ViewGroup.MarginLayoutParams(cordovaWebView.getLayoutParams());
        margin.setMargins(0, y, 0, 0);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(margin);
        cordovaWebView.setLayoutParams(layoutParams);
    }


    /**
     * 下拉刷新
     */
    SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            cordovaWebView.clearHistory();
            cordovaWebView.clearFormData();
            cordovaWebView.clearCache(true);
            cordovaWebView.loadUrl(HostUrl);
            swipeRefreshLayout.setRefreshing(false);
        }
    };


    /**
     * 返回
     */
    View.OnClickListener onClickListenerreturn = new View.OnClickListener() {
        @Override
        public void onClick(View view) {


            if (cordovaWebView.canGoBack()) {
                cordovaWebView.goBack();
                return;
            }

            if (IsCloseNotices) {
//                cordovaWebView.loadUrl(String.format("javascript:%1$s()", CloseNoticesFunction));
                Intent intent = new Intent();
                intent.putExtra("function", CloseNoticesFunction);
                intent.putExtra("arg", CloseNoticesArg);
                setResult(2, intent);
            }


            finish();
            overridePendingTransition(R.anim.alpha, R.anim.slide_out_right);
        }
    };


    @Override
    protected void onResume() {
        super.onResume();

        cordovaWebView.onResume();
        cordovaWebView.handleResume(true, true);

    }

    @Override
    protected void onPause() {
        super.onPause();
        cordovaWebView.onPause();
        cordovaWebView.handlePause(true);
    }

    @Override
    protected void onStop() {
        cordovaWebView.handleDestroy();
        super.onStop();
    }


    /**
     * 拍照
     */
    View.OnClickListener onClickListenertakepicture = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            alertSheet.dismiss();
            CameraCore cameraPlugin = new CameraCore(getActivity());
            cameraPlugin.takePictureForNaative();
        }
    };

    /**
     * 从相机中选择
     */
    View.OnClickListener onClickListenerchoosepicture = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            alertSheet.dismiss();
            CameraCore cameraPlugin = new CameraCore(getActivity());
            cameraPlugin.openPreviewPhotoForNavtive(imageSelectcount);
        }
    };


    Handler handlermove = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.e("动作", String.valueOf(cordovaWebView.getScrollY()));
            if (cordovaWebView.getScrollY() == 0)
                swipeRefreshLayout.setEnabled(true);
            else
                swipeRefreshLayout.setEnabled(false);
        }
    };


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == 4) {
            if (cordovaWebView.canGoBack()) {
                cordovaWebView.goBack();
                return false;
            }
            if (IsCloseNotices) {
//                cordovaWebView.loadUrl(String.format("javascript:%1$s()", CloseNoticesFunction));

                Intent intent = new Intent();
                intent.putExtra("function", CloseNoticesFunction);
                intent.putExtra("arg", CloseNoticesArg);
                setResult(2, intent);
            }
            finish();
            overridePendingTransition(R.anim.alpha, R.anim.slide_out_right);
            return false;
        }
        return super.onKeyUp(keyCode, event);
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




        if (id.equals("onShowCustomView")) {
            headview.setVisibility(View.GONE);
            if (publishCommentsView !=null)
                publishCommentsView.getLinearLayout().setVisibility(View.GONE);
            return null;
        }
        if (id.equals("onHideCustomView")) {
            headview.setVisibility(View.VISIBLE);
            if (publishCommentsView !=null)
                publishCommentsView.getLinearLayout().setVisibility(View.VISIBLE);
            return null;
        }
        if (id.equals("onReceivedTitle")) {

            if (strwebtitle == null || strwebtitle.equals(""))
                webtitle.setText(cordovaWebView.webTitle);
            return null;
        }
        if (id.equals("onPageStarted"))
        {

//            CustomPopWindowPlugin.ShowPopWindow(cordovaWebView, getLayoutInflater(), "正在加载");
            return null;
        }
        if (id.equals("onPageFinished"))
        {
//            CustomPopWindowPlugin.CLosePopwindow();
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
            handler.sendMessage(message);
        }
        if (data.getClass().getSuperclass().getSimpleName().equals("CordovaPlugin")) {
            //异步带返回调用
            Map<String, Object> map = new Hashtable<String, Object>();
            map.put("id", id);
            map.put("data", data);
            Message message = new Message();
            message.obj = map;
            message.what = 1;
            handler.sendMessage(message);
        }


        return null;
    }





    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);

            String id = "";
            Object data = null;
            Message msg = null;
            CordovaPlugin cordovaPlugindata = null;
            Map<String, Object> mapmsg = null;
            switch (message.what) {
                case 0:
                    mapmsg = (Map<String, Object>) message.obj;
                    id = mapmsg.get("id").toString();
                    data = mapmsg.get("data");
                    break;
                case 1:
                    mapmsg = (Map<String, Object>) message.obj;
                    id = mapmsg.get("id").toString();
                    cordovaPlugindata = (CordovaPlugin) mapmsg.get("data");
                    break;
            }
            if (id.equals(BaseViewPlugin.openSignView)) {
                signCallBackJS = ((Message) data).obj.toString();;
                Intent intent=new Intent(CordovaWebViewActivity.this, SignaturepadViewPlugin.class);
                startActivityForResult(intent,11);


                return ;
            }

            //扫描
            if (id.equals(BaseViewPlugin.SCAN_ACTION)) //
            {
                cordovaPlugin = cordovaPlugindata;
                ScanPlugin scanPlugin = new ScanPlugin();
                scanPlugin.showScanActivity(CordovaWebViewActivity.this);
                return;
            }
            //处理回退
            if (id.equals(BaseViewPlugin.GOBACK_ACTION)) //
            {
                cordovaWebView.goBack();
                return;
            }
            //处理回退
            if (id.equals(BaseViewPlugin.CLOSEWINDOW_ACTION)) //
            {
                msg = (Message) data;
                String function = msg.obj.toString();
                if (!function.equals("null")) {

                    Intent intent = new Intent();
                    intent.putExtra("function", function);
                    setResult(1, intent);
                }
                if (IsCloseNotices) {
//                cordovaWebView.loadUrl(String.format("javascript:%1$s()", CloseNoticesFunction));
                    Intent intent = new Intent();
                    intent.putExtra("function", CloseNoticesFunction);
                    intent.putExtra("arg", CloseNoticesArg);
                    setResult(2, intent);
                }
                finish();
                overridePendingTransition(R.anim.alpha, R.anim.slide_out_right);
                return;
            }
            //清除缓存
            if (id.equals(BaseViewPlugin.CLEARWEBCACHE_ACTION)) //
            {
                cordovaWebView.clearHistory();
                cordovaWebView.clearFormData();
                cordovaWebView.clearCache(true);
                return;
            }
            if (id.equals(BaseViewPlugin.SETBARRIGHT_ACTION)) //
            {
                try {
                    msg = (Message) data;
                    JSONObject jsonObject = (JSONObject) msg.obj;
                    if (jsonObject.getString("hide").equals("NO")) {
                        btnright.setVisibility(View.VISIBLE);
                        btnright.setOnClickListener(onClickListenerbtnright);
                    } else {
                        btnright.setVisibility(View.GONE);
                        btnright.setOnClickListener(null);
                        return;
                    }
                    if (jsonObject.getString("mode").equals("button")) {
                        barrightJSwithFunctionID = jsonObject.getString("function");
                        if (jsonObject.getString("type").equals("add")) {
                            IsRightBarMenu = false;
                            btnright.setBackground(getResources().getDrawable(R.drawable.btn_add_selector));
                        } else if (jsonObject.getString("type").equals("edit")) {
                            IsRightBarMenu = false;
                            btnright.setBackground(getResources().getDrawable(R.drawable.btn_edit_selector));
                        } else if (jsonObject.getString("type").equals("list")) {
                            IsRightBarMenu = false;
                            btnright.setBackground(getResources().getDrawable(R.drawable.btn_list_selector));
                        }else if (jsonObject.getString("type").equals("share")) {
                            IsRightBarMenu = false;
                            btnright.setBackground(getResources().getDrawable(R.drawable.btn_share_selector));
                        }
                    } else if (jsonObject.getString("mode").equals("list")) {
                        IsRightBarMenu = true;
                        if (jsonObject.getString("type").equals("more")) {
                            btnright.setBackground(getResources().getDrawable(R.drawable.bar_webview_right));
                        }
                        MenuJsFunctionList = new ArrayList<>();

                        JSONArray jsonArray = jsonObject.getJSONArray("menu");
                        JSONArray jsonArray1 = jsonObject.getJSONArray("function");
                        menu_custom.clearMenu();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            MenuJsFunctionList.add(jsonArray1.getString(i));
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            if (jsonObject1.getString("icon").equals("add"))
                                menu_custom.addMenuItem(R.mipmap.menu_add, jsonObject1.getString("name"), i);
                            else if (jsonObject1.getString("icon").equals("edit"))
                                menu_custom.addMenuItem(R.mipmap.menu_edit, jsonObject1.getString("name"), i);
                            else if (jsonObject1.getString("icon").equals("addfriend"))
                                menu_custom.addMenuItem(R.mipmap.addfriend_menu, jsonObject1.getString("name"), i);
                            else if (jsonObject1.getString("icon").equals("scan"))
                                menu_custom.addMenuItem(R.mipmap.scan_menu, jsonObject1.getString("name"), i);
                        }
                        menu_custom.setBackViewSuffer(containerview);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }

            //打开新窗口
            if (id.equals(BaseViewPlugin.OPENWINDOW_ACTION)) //
            {


                try {
                    msg = (Message) data;
                    JSONObject jsonObject = (JSONObject) msg.obj;
                    Intent intent = new Intent(CordovaWebViewActivity.this, CordovaWebViewActivity.class);
                    intent.putExtra("url", jsonObject.getString("url"));

                    //显示模式
                    if (!jsonObject.isNull("mode")) {
                        if (jsonObject.getString("mode").equals("NOTITLE")) {
                            intent.putExtra("ShowTitle", false);//不显示标题
                        }
                    } else
                        intent.putExtra("ShowTitle", true);


                    //标题
                    if (!jsonObject.isNull("title")) {
                        String strtitle = jsonObject.getString("title");//标题
                        intent.putExtra("title", strtitle);//不显示标题
                    } else
                        intent.putExtra("title", "");//不显示标题

                    startActivityForResult(intent, CordovaWebViewActivity.REQUESTCLOSECALLBACK);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.alpha_exit);
                    Log.i("打开新窗口", jsonObject.getString("url"));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return;
            }

            //显示提示框
            if (id.equals(BaseViewPlugin.DIALOG_ACTION)) //
            {

                try {
                    msg = (Message) data;
                    JSONObject jsonObject = (JSONObject) msg.obj;
                    if (jsonObject.getString("model").equals("show")) {
                        int timeout = 15000;
                        try {
                            timeout = jsonObject.getInt("timeout");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        CustomPopWindowPlugin.ShowPopWindowForTimeout(cordovaWebView,
                                getActivity().getLayoutInflater(), jsonObject.getString("info"), timeout);

                    }
                    if (jsonObject.getString("model").equals("change")) {
                        CustomPopWindowPlugin.Setpoptext(jsonObject.getString("info"));

                    }
                    if (jsonObject.getString("model").equals("close")) {
                        CustomPopWindowPlugin.CLosePopwindow();

                    }
//                callbackJs = jsonObject.getString("callback");


                    return;

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


            //显示指示器
            if (id.equals(BaseViewPlugin.INFO_ACTION)) //
            {

                try {
                    msg = (Message) data;
                    JSONObject jsonObject = (JSONObject) msg.obj;
                    String title = jsonObject.getString("title");
                    if (title.equals(""))
                        title = "信息";
                    String info = jsonObject.getString("info");
                    int timeout = jsonObject.getInt("timeout");


                    CustomPopWindowPlugin.ShowPopWindow(cordovaWebView, getActivity().getLayoutInflater(),
                            "", info, timeout);


                    return;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }





            //修改web标题
            if (id.equals(BaseViewPlugin.TITLE_ACTION)) {
                try {
                    msg = (Message) data;
                    JSONObject jsonObject = (JSONObject) msg.obj;
                    webtitle.setText(jsonObject.getString("title"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }

            //注册关闭事件
            if (id.equals(BaseViewPlugin.CLOSEEVENT_ACTION)) {
                try {
                    msg = (Message) data;
                    JSONObject jsonObject = new JSONObject(msg.obj.toString());
                    if (jsonObject.getString("eventType").equals("add")) {
                        IsCloseNotices = true;
                        CloseNoticesFunction = jsonObject.getString("function");
                        CloseNoticesArg = jsonObject.getString("arg");
                    } else if (jsonObject.getString("eventType").equals("remove")) {
                        IsCloseNotices = false;
                        CloseNoticesFunction = "";
                        CloseNoticesArg = "";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }

            //设置状态栏
            if (id.equals(BaseViewPlugin.NAVBAR_ACTION)) {
                try {
                    msg = (Message) data;
                    JSONObject jsonObject = (JSONObject) msg.obj;
                    Animation animation;

                    if (jsonObject.getString("hide").equals("true")) {


                        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_to_top);
                        animation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                headview.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                        headview.startAnimation(animation);

                    } else {


                        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_from_top);
                        animation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                headview.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                headview.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                        headview.startAnimation(animation);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
            if (id.equals(BaseViewPlugin.CHOOSEIMAGE_ACTION))// 打开预览
            {
                cordovaPlugin = cordovaPlugindata;
                try {
                    imageSelectcount = ((JSONObject) cordovaPlugindata.jsondata).getInt("imageCount");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                alertSheet = new AlertSheet(getActivity());
                alertSheet.addSheetButton("拍照", onClickListenertakepicture);
                alertSheet.addSheetButton("从相机中选择", onClickListenerchoosepicture);
                alertSheet.show();
                return;
            }




            if (id.equals(BaseViewPlugin.SHAREINFO))
            {
                msg = (Message) data;
                JSONObject jsonObject = (JSONObject) msg.obj;

                ShareView shareView=new ShareView(getActivity(),jsonObject);
                shareView.show();
                return;
            }

            //下拉刷新
            if (id.equals(BaseViewPlugin.WEBREFRESH_ACTION)) {
                try {
                    msg = (Message) data;
                    JSONObject jsonObject = (JSONObject) msg.obj;
                    if (jsonObject.getString("enable").equals("true")) {
                        IsRefresh = true;
                        swipeRefreshLayout.setEnabled(true);
                    } else {
                        IsRefresh = false;
                        swipeRefreshLayout.setEnabled(false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }

            if (id.equals(BaseViewPlugin.COMMENTS_INIT)) //
            {
                msg = (Message) data;
                JSONObject jsonObject = (JSONObject) msg.obj;

                if (publishCommentsView == null) {
                    publishCommentsView = new PublishCommentsView(new PublishCommentsView.IComments() {

                        @Override
                        public void ClickCommentsView(Boolean open) {
                            if (open)
                            {
                                if(backView == null)
                                {
                                    backView = new View(CordovaWebViewActivity.this);
                                    RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.MATCH_PARENT);
                                    backView.setLayoutParams(layoutParams);
                                    backView.setBackground(getResources().getDrawable(R.color.blackTransparent6));
                                    backView.setClickable(true);
                                    backView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            containerview.removeView(backView);
                                            if (publishCommentsView != null) {
                                                publishCommentsView.CloseView();
                                            }
                                            backView=null;
                                        }
                                    });
                                }
                                containerview.addView(backView);
                            }
                            else
                            {
                                containerview.removeView(backView);
                                backView=null;
                            }
                        }

                        @Override
                        public void ClickNum() {
                            cordovaWebView.loadUrl(String.format("javascript:%1$s()",
                                    commentsnumJS));
                        }

                        @Override
                        public void ClickReturn() {
                            onClickListenerreturn.onClick(null);
                        }

                        @Override
                        public void ClickLike(int state) {

                            JSONObject jsonObject1 = new JSONObject();
                            try {
                                jsonObject1.put("type", 2);
                                jsonObject1.put("content", state);
                            } catch (JSONException jse) {
                                jse.printStackTrace();
                            }

                            cordovaWebView.loadUrl(String.format("javascript:%1$s(%2$s)",
                                    commentsJS, jsonObject1.toString()));
                        }

                        @Override
                        public void ClickFav(int state) {
                            JSONObject jsonObject1 = new JSONObject();
                            try {
                                jsonObject1.put("type", 1);
                                jsonObject1.put("content", state);
                            } catch (JSONException jse) {
                                jse.printStackTrace();
                            }

                            cordovaWebView.loadUrl(String.format("javascript:%1$s(%2$s)",
                                    commentsJS, jsonObject1.toString()));
                        }

                        @Override
                        public void submitComments(String content) {
                            JSONObject jsonObject1 = new JSONObject();
                            try {
                                jsonObject1.put("type", 3);
                                jsonObject1.put("content", content);
                            } catch (JSONException jse) {
                                jse.printStackTrace();
                            }
                            cordovaWebView.loadUrl(String.format("javascript:%1$s(%2$s)",
                                    commentsJS, jsonObject1.toString()));
                        }
                    });
                    publishCommentsView.setLinearLayoutYXG(linearLayoutYXG);
                    linearLayoutYXG.addView(publishCommentsView.getLinearLayout());


                }
                try {
                    commentsJS = jsonObject.getString("callback");

                    JSONObject jsondata = jsonObject.getJSONObject("data");
                    if (jsondata.getInt("showComment") == 1)
                        publishCommentsView.setLinearComments(true);
                    else
                        publishCommentsView.setLinearComments(false);

                    if (jsondata.getInt("showLike") == 1)
                        publishCommentsView.setLikeState(jsondata.getInt("isLike"));
                    else
                        publishCommentsView.setLikeState(0);

                    if (jsondata.getInt("showCommentsNum") == 1) {
                        publishCommentsView.setCommentsNum(1, jsondata.getString("num"));
                        commentsnumJS = jsondata.getString("numFunction");
                    } else
                        publishCommentsView.setCommentsNum(0, "");

                    publishCommentsView.setFavState(jsondata.getInt("isFav"));


                } catch (Exception e) {
                    e.printStackTrace();
                }

                return;
            }

            if (id.equals(BaseViewPlugin.COMMENTS_UPDATESTATE)) //
            {
                msg = (Message) data;
                JSONObject jsonObject = (JSONObject) msg.obj;
                if (publishCommentsView != null) {

                    try {
                        if (jsonObject.getInt("type") == 1)
                            publishCommentsView.setFavState(jsonObject.getInt("ope"));
                        else if (jsonObject.getInt("type") == 2)
                            publishCommentsView.setLikeState(jsonObject.getInt("ope"));
                        else if (jsonObject.getInt("type") == 3)
                            publishCommentsView.setCommentsNum(jsonObject.getInt("ope"),
                                    jsonObject.getString("num"));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return;
            }


            if (id.equals(BaseViewPlugin.COMMENTS_UNINIT)) //
            {
                if (publishCommentsView != null) {
                    linearLayoutYXG.removeView(publishCommentsView.getLinearLayout());
                    publishCommentsView = null;
                }
                return;
            }
            if(id.equals(BaseViewPlugin.PREVIEW_IMAGE)){
                Intent intent = new Intent(getActivity(), ImagePreviewActivity.class);
                JSONObject jsonObject = (JSONObject) cordovaPlugindata.jsondata;
                intent.putExtra("data", jsonObject.toString());
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.alpha_enter, R.anim.alpha_exit);
            }
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==11)
        {
            if (resultCode == 1) {
                String uuid = data.getExtras().getString("uuid");
                cordovaWebView.loadUrl(String.format("javascript:%1$s('%2$s')", signCallBackJS,
                        uuid));


            }

            return;
        }
        if (CameraHelper.JSCallCamera == requestCode)//拍照回调
        {
            //拍照返回信息
            if (resultCode == -1) {

                String mediaid = ImageCore.copyCacheFile(CameraHelper.photopath);
                JSONArray jsonArray = new JSONArray();
                jsonArray.put(mediaid);
                cordovaPlugin.callbackContext.success(jsonArray);
                return;
            }

        }

        if (requestCode == PreviewPhotoViewPlugin.JSCallPreviewPhtoto) {
            if (resultCode == 1) {
                Bundle bundle = data.getExtras();
                String[] files = bundle.getStringArray("files");
                JSONArray jsonArray = new JSONArray();
                for (int i = 0; i < files.length; i++) {
                    Log.i("选择照片", files[i]);

                    jsonArray.put(files[i]);
                }
                cordovaPlugin.callbackContext.success(jsonArray);
                return;
            }
        }
        if (requestCode == CordovaWebViewActivity.REQUESTCLOSECALLBACK) {
            if (resultCode == 1) {
                cordovaWebView.loadUrl(String.format("javascript:%1$s()", data.getStringExtra("function")));
                Log.i("回调", data.getStringExtra("function"));
            } else if (resultCode == 2) {

                try {
                    cordovaWebView.loadUrl(String.format("javascript:%1$s(%2$s)", data.getStringExtra("function"),
                            data.getStringExtra("arg")));
                    Log.i("回调", data.getStringExtra("function"));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            return;
        }
        if (requestCode == ScanActivity.SCANRESULTREQUEST) {
            if (resultCode == 1) {
                String code = data.getStringExtra("code");
                Log.i("扫描到的信息", code);
                if (cordovaPlugin != null) {
                    cordovaPlugin.callbackContext.success(code);
                    cordovaPlugin = null;
                }
            }
            return;
        }

    }

    @Override
    public ExecutorService getThreadPool() {
        return threadPool;
    }
}
