package com.suypower.stereo.suypowerview.CordovaPlugin;

import android.view.View;

import org.json.JSONArray;

/**
 * viewpager base基础类
 * @author YXG
 */
public abstract class BaseViewPlugin {
    public static final String TAG = "BaseViewPlugin";
    public static final String MENU_ACTION = "OPTIONMENU";
    public static final String TAKEPICTURE_ACTION = "TAKEPICTURE";
    public static final String PREVIEWPICTURE_ACTION = "PREPHTOTO";
    public static final String CHOOSEIMAGE_ACTION = "CHOOSEIMAGE";
    public static final String UPLOAD_ACTION = "UPLOAD";
    public static final String PREVIEWURL_ACTION = "PREVIEWURL";
    public static final String SCAN_ACTION = "SCAN";
    public static final String WORD_ACTION = "WORDACTION";
    public static final String SIGNATURE_ACTION = "SIGNATUREACTION";
    public static final String WORDPREVIEW_ACTION = "WORDPREVIEWACTION";
    public static final String UPLOADFILE_ACTION = "UPLOADFILEACTION";
    public static final String FILEDOWNLOAD_ACTION = "FILEDOWNLOADACTION";
    public static final String FILECANCELDOWNLOAD_ACTION = "FILECANCELDOWNLOADACTION";
    public static final String FILECANCELUPLOAD_ACTION = "FILECANCELUPLOADACTION";
    public static final String INITWEB_ACTION = "INITWEBACTION";
    public static final String UPDATE_ACTION = "UPDATEACTION";
    public static final String DIALOG_ACTION = "DIALOGACTION";
    public static final String GOBACK_ACTION = "GOBACKACTION";
    public static final String GOBACK2_ACTION = "GOBACK2ACTION";
    public static final String INFO_ACTION = "INFOACTION";
    public static final String NAVBAR_ACTION = "NAVBARACTION";
    public static final String WEBREFRESH_ACTION = "WEBREFRESHACTION";
    public static final String TITLE_ACTION = "TITLEACTION";
    public static final String ACCCOUNT_INFO = "ACCCOUNTINFO";
    public static final String OPENWINDOW_ACTION="OPENWINDOWACTION";
    public static final String CLOSEWINDOW_ACTION="CLOSEWINDOWACTION";
    public static final String CLEARWEBCACHE_ACTION="CLEARWEBCACHEACTION";
    public static final String SETBARRIGHT_ACTION="SETBARRIGHTACTION";
    public static final String CLOSEEVENT_ACTION="CLOSEEVENTACTION";
    public static final String COMMENTS_INIT="COMMENTSINIT";
    public static final String COMMENTS_UNINIT="COMMENTSUNINIT";
    public static final String COMMENTS_UPDATESTATE="COMMENTSUPDATESTATE";
    public static final String PREVIEW_IMAGE = "PREVIEW_IMAGE";
    public static final String PHONEBOOK_SELECT = "PHONEBOOK_SELECT";
    public static final String SHAREINFO = "SHARE_INFO";
    public static final String WEBPREVIEW = "WEBPREVIEW";
    public static final String EXITSYSTEM = "EXITSYSTEM";
    public static final String BadgeItemCount = "BadgeItemCount";
    public static final String openSignView = "openSignView";
    Boolean isshowmenu = false;//是否显示菜单


    public Boolean isShowmenu() {
        return isshowmenu;
    }

    public void setIsshowmenu(Boolean isshowmenu) {
        this.isshowmenu = isshowmenu;
    }

    /**
     * 得到plugin 自定义菜单
     * @param menujson
     * @return
     */
    public abstract int getMenuList(JSONArray menujson);

    /**
     * 传递cordova js 交互数据
     * @param id
     * @param data
     */
    public abstract void onCordovaMessage(String id ,Object data);

    /**
     * 显示optionmenu
     * @param v 被加载的view
     */
    public abstract void showOptionMenu(View v,int menutype);


    public abstract void loadWebUrl(String Url);


    //web返回
    public void goback(){}
    /**
     * 回调H5JS
     * @param method js 方法名称
     * @param jsonObject 参数
     */
    public void CallBackCordovaJS(String method,Object jsonObject)
    {

    }
}
