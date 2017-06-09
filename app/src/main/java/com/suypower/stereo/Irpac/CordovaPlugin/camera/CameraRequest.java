package com.suypower.stereo.Irpac.CordovaPlugin.camera;

import android.os.Message;


import com.suypower.stereo.suypowerview.CordovaPlugin.BaseViewPlugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *拍照插件
 * @author yxg
 *
 */
public class CameraRequest extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args,
    		CallbackContext callbackContext) throws JSONException {

		this.callbackContext = callbackContext;


		Message message=new Message();
		message.obj =args;//json数据
		message.arg1= this.webView.cordovaWebViewId; //前端webview  id
		JSONObject jsonObject;

		try {
			if (action.equals("chooseImage")) {
				jsonObject = args.getJSONObject(0);

				jsondata = jsonObject;
				String choosemodle = jsonObject.getString("chooseModel");
				if (choosemodle.equals("camera")) {
					this.cordova.onMessage(BaseViewPlugin.TAKEPICTURE_ACTION, this);
				}
				if (choosemodle.equals("picture")) {
					this.cordova.onMessage(BaseViewPlugin.PREVIEWPICTURE_ACTION, this);
				}
				if (choosemodle.equals("all")) {
					this.cordova.onMessage(BaseViewPlugin.CHOOSEIMAGE_ACTION, this);
				}
//				if (choosemodle.equals("upload")) {
////					message.obj = jsonObject.getString("images");
//					this.cordova.onMessage(BaseViewPlugin.UPLOAD_ACTION, this);
//
//				}
//				if (choosemodle.equals("preview")) {
//					this.cordova.onMessage(BaseViewPlugin.PREVIEWURL_ACTION, this);
//				}
			}
			if (action.equals("imagePreview")) {
				jsonObject = args.getJSONObject(0);
				jsondata =jsonObject;
				this.cordova.onMessage(BaseViewPlugin.PREVIEWURL_ACTION, this);
			}

		}
		catch (Exception e)
		{e.printStackTrace();}


    	
    	return true;
    }
}
