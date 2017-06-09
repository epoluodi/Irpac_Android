package com.suypower.stereo.Irpac.Fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.suypower.stereo.Irpac.Activity.CordovaWebViewActivity;
import com.suypower.stereo.Irpac.R;
import com.suypower.stereo.Irpac.System.AppConfig;
import com.suypower.stereo.suypowerview.AlertView.AlertSheet;
import com.suypower.stereo.suypowerview.Camera.CameraCore;
import com.suypower.stereo.suypowerview.Camera.CameraHelper;
import com.suypower.stereo.suypowerview.Camera.PreviewPhotoViewPlugin;
import com.suypower.stereo.suypowerview.Common.ImageCore;
import com.suypower.stereo.suypowerview.CordovaPlugin.BaseViewPlugin;
import com.suypower.stereo.suypowerview.FrameController.FragmentName;
import com.suypower.stereo.suypowerview.Menu.Menu_Custom;
import com.suypower.stereo.suypowerview.PopWindowInfo.CustomPopWindowPlugin;
import com.suypower.stereo.suypowerview.Scan.ScanActivity;
import com.suypower.stereo.suypowerview.Scan.ScanPlugin;
import com.suypower.stereo.suypowerview.Share.ShareView;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 14-11-30.
 */
@SuppressLint("ValidFragment")
public class CDVWebviewfragment extends Fragment implements FragmentName {


    private String Fragment_Name = "";
    private CordovaWebView cordovaWebView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout headview;
    private TextView webtitle;
    private ImageView btnright;
    private int viewid;
    private int downY = 0;
    public String HostUrl = "";
    private int imageSelectcount = 0;//web选择相册 调用的图片数量

    private AlertSheet alertSheet;
    private CordovaPlugin cordovaPlugin;
    private Boolean IsRefresh;
    private String barrightJSwithFunctionID = "";
    private Boolean IsRightBarMenu = false;
    private List<String> MenuJsFunctionList;
    private Menu_Custom menu_custom;
    private RelativeLayout containerview;


    @Override
    public void setWebTitle() {
        webtitle.setText(cordovaWebView.webTitle);
    }

    @Override
    public void onResult(int requestCode, int resultCode, Intent data) {
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
    public void startIMessageControl() {

    }


    @Override
    public void stopIMessageControl() {

    }

    @Override
    public void selectcustomer(String guestid, String guestname) {
        return;
    }

    @Override
    public void SelectMenu(int Menuid) {

    }

    public CDVWebviewfragment(int id) {

        viewid = id;
    }


    @Override
    public void SetFragmentName(String name) {
        Fragment_Name = name;
    }

    @Override
    public String GetFragmentName() {
        return Fragment_Name;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cdvwebview, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refreshlayout);
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);
        headview = (RelativeLayout) rootView.findViewById(R.id.headview);
        cordovaWebView = (CordovaWebView) rootView.findViewById(R.id.cordovawebview);
        swipeRefreshLayout.setOnTouchListener(onTouchListener);
        cordovaWebView.setOnTouchListener(onTouchListenerwebview);
        containerview = (RelativeLayout) rootView.findViewById(R.id.containerview);
        webtitle = (TextView) rootView.findViewById(R.id.webtitle);
        btnright = (ImageView) rootView.findViewById(R.id.tbnright);
        btnright.setVisibility(View.GONE);
        webtitle.setText("");
        switch (viewid)
        {
            case 2:
                webtitle.setText("通知");
                break;
            case 3:
                webtitle.setText("我");
                break;
        }

        IsRefresh = false;
        cordovaWebView.clearHistory();
        cordovaWebView.clearFormData();
        cordovaWebView.clearCache(true);
        cordovaWebView.loadUrl(HostUrl);
        menu_custom = new Menu_Custom(getActivity(), iMenu);

        cordovaWebView.cordovaWebViewId = viewid;
        // cordovaWebView.addJavascriptInterface(new StereoBridge(js_handle),"StereoBridge");
        swipeRefreshLayout.setEnabled(false);

        return rootView;
    }

    Menu_Custom.IMenu iMenu = new Menu_Custom.IMenu() {
        @Override
        public void ClickMenu(int itemid) {
            cordovaWebView.loadUrl(String.format("javascript:%1$s()", MenuJsFunctionList.get(itemid)));
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CordovaWebViewActivity.REQUESTCLOSECALLBACK) {
            if (resultCode == 1) {
                cordovaWebView.loadUrl(String.format("javascript:%1$s()", data.getStringExtra("function")));
                Log.i("回调", data.getStringExtra("function"));
            } else if (resultCode == 2) {
                cordovaWebView.loadUrl(String.format("javascript:%1$s(%2$s)", data.getStringExtra("function"),
                        data.getStringExtra("arg")));
                Log.i("回调", data.getStringExtra("function"));
            }

            return;
        }
    }

    /**
     * 右键功能调用
     */
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
            }


            return false;
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


            //扫描
            if (id.equals(BaseViewPlugin.SCAN_ACTION)) //
            {
                cordovaPlugin = cordovaPlugindata;
                ScanPlugin scanPlugin = new ScanPlugin();
                scanPlugin.showScanActivity(getActivity());

                return;
            }
            //处理回退
            if (id.equals(BaseViewPlugin.GOBACK_ACTION)) //
            {
                cordovaWebView.goBack();
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

            //打开新窗口
            if (id.equals(BaseViewPlugin.OPENWINDOW_ACTION)) //
            {
                try {
                    msg = (Message) data;
                    JSONObject jsonObject = (JSONObject) msg.obj;
                    Intent intent = new Intent(getActivity(), CordovaWebViewActivity.class);
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

            if (id.equals(BaseViewPlugin.SHAREINFO)) {
                msg = (Message) data;
                JSONObject jsonObject = (JSONObject) msg.obj;

                ShareView shareView = new ShareView(getActivity(), jsonObject);
                shareView.show();
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
            }
        }
    };


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


    @Override
    public void onMessage(Message message) {
        handler.sendMessage(message);
    }

    public void setUrl(String url) {
        cordovaWebView.loadUrl(url);
    }

    @Override
    public void returnWeb() {
        if (cordovaWebView.startOfHistory())
            return;


        cordovaWebView.goBack();
    }


    public void close() {
        cordovaWebView.hideCustomView();
        cordovaWebView.handleDestroy();
        cordovaWebView.destroy();
        cordovaWebView = null;

    }

    public void PauseWeb() {
        cordovaWebView.handlePause(true);
    }

    public void ResumeWeb() {
        cordovaWebView.handleResume(true, true);
        cordovaWebView.getSettings().setTextZoom(AppConfig.WebTextZoom);
    }

    Handler js_handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            cordovaWebView.loadUrl(msg.obj.toString());
        }
    };

}
