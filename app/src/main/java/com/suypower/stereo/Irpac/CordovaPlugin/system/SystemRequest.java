package com.suypower.stereo.Irpac.CordovaPlugin.system;

import android.app.Application;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;


import com.suypower.stereo.Irpac.System.APP;
import com.suypower.stereo.Irpac.System.UserInfo;
import com.suypower.stereo.suypowerview.CordovaPlugin.BaseViewPlugin;


import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * app 系统插件
 * @author yxg
 *
 */
public class SystemRequest extends CordovaPlugin {
	
	private static final String TAG = "SystemRequest";

    private CallbackContext callbackContext = null;





    @Override
    public boolean execute(String action, JSONArray args,
    		CallbackContext callbackContext) throws JSONException {
    	this.callbackContext = callbackContext;
		Message message=new Message();
		message.arg1= this.webView.cordovaWebViewId; //前端webview  id
		//指示器
		if (action.equals("dialog"))
		{
			message.obj =args.getJSONObject(0);//json数据
			this.cordova.onMessage(BaseViewPlugin.DIALOG_ACTION, message);
			return true;
		}
		//打开新窗口
		if (action.equals("newWindow")) {
			message.obj=null;
			this.cordova.onMessage(BaseViewPlugin.OPENWINDOW_ACTION, message);
			return true;
		}

		//指示器
		if (action.equals("info")) {
			message.obj=args.getJSONObject(0);
			this.cordova.onMessage(BaseViewPlugin.INFO_ACTION, message);
			return true;
		}
		//web标题
		if (action.equals("title")) {
			message.obj=args.getJSONObject(0);
			this.cordova.onMessage(BaseViewPlugin.TITLE_ACTION, message);
			return true;
		}
		//设置状态栏
		if (action.equals("navbar"))
		{
			message.obj=args.getJSONObject(0);
			this.cordova.onMessage(BaseViewPlugin.NAVBAR_ACTION, message);
			return true;
		}
		//下拉刷新
		if (action.equals("webrefresh"))
		{
			message.obj=args.getJSONObject(0);
			this.cordova.onMessage(BaseViewPlugin.WEBREFRESH_ACTION, message);
			return true;
		}


		//获得APP版本信息
		if (action.equals("getAppInfo")) {
//			message.obj=args.getJSONObject(0);
			try
			{
				JSONObject jsonObject = new JSONObject();

				jsonObject.put("appVer","APP版本：" + APP.getApp().AppVerName());
//				jsonObject.put("updateDT", "更新日期：" + GlobalConfig.globalConfig.getUpdateDT());
				callbackContext.success(jsonObject);
			}
			catch (Exception e)
			{e.printStackTrace();}
			return true;
		}

		//账号信息
		if (action.equals("getLoginUser")) {
			try
			{
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("userName", UserInfo.getUserInfo().getUsername());
				jsonObject.put("avatar", UserInfo.getUserInfo().getPhoto());
				jsonObject.put("userId", UserInfo.getUserInfo().getUserId());
				jsonObject.put("nickName", UserInfo.getUserInfo().getNickname());
				jsonObject.put("officeName", UserInfo.getUserInfo().getOfficeName());
				jsonObject.put("gh", UserInfo.getUserInfo().getGh());
				jsonObject.put("token", UserInfo.getUserInfo().getToken());
				jsonObject.put("deviceType", "2");
				callbackContext.success(jsonObject);
			}
			catch (Exception e)
			{e.printStackTrace();}
			return true;
		}

		if (action.equals("setLoginUser")) {
			try
			{
				JSONObject jsonObject=args.getJSONObject(0);
				if (!jsonObject.isNull("nickName")) {
					UserInfo.getUserInfo().setNickname(jsonObject.getString("nickName"));
					UserInfo.getUserInfo().setUsername(jsonObject.getString("nickName"));
				}
				if (!jsonObject.isNull("avatar")) {
					UserInfo.getUserInfo().setPhoto(jsonObject.getString("avatar"));

				}
				if (!jsonObject.isNull("officeName")) {
					UserInfo.getUserInfo().setOfficeName(jsonObject.getString("officeName"));

				}


			}
			catch (Exception e)
			{e.printStackTrace();}
			return true;
		}


		//账号信息
		if (action.equals("toast")) {
			try
			{
				String desc = args.getString(0);
				Toast.makeText(cordova.getActivity(),desc,Toast.LENGTH_SHORT).show();
			}
			catch (Exception e)
			{e.printStackTrace();}
			return true;
		}

		//退出
		if (action.equals("exitSystem")) {
			this.cordova.onMessage(BaseViewPlugin.EXITSYSTEM, message);
			return true;
		}

		//退出
		if (action.equals("setBadgeItemCount")) {

			this.cordova.onMessage(BaseViewPlugin.BadgeItemCount, args.getJSONObject(0));
			return true;
		}


    	return true;
    }
}
