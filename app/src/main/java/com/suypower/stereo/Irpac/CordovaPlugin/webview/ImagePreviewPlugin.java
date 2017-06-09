package com.suypower.stereo.Irpac.CordovaPlugin.webview;

import com.suypower.stereo.suypowerview.CordovaPlugin.BaseViewPlugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Bingdor on 2017/2/21.
 */

public class ImagePreviewPlugin extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        try {
            JSONObject argJSON = args.getJSONObject(0);
            this.callbackContext = callbackContext;
            this.jsondata = argJSON;
            this.cordova.onMessage(BaseViewPlugin.PREVIEW_IMAGE, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*Intent intent1 = new Intent(getActivity(), WeAppActivity.class);
        intent1.putExtra("from", APP.getApp().tranuserinnfo.toString());
        startActivity(intent1);
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.alpha_exit);*/
        return true;
    }
}
