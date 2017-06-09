package com.suypower.stereo.Irpac.CordovaPlugin.PhoneBook;

import android.os.Message;

import com.suypower.stereo.suypowerview.CordovaPlugin.BaseViewPlugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * 发布插件
 *
 * @author yxg
 */
public class PhoneBookPlugin extends CordovaPlugin {





    @Override
    public boolean execute(String action, JSONArray args,
                           CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;
        //web返回
        if (action.equals("open")) {
            this.jsondata = args.getString(0);
            this.cordova.onMessage(BaseViewPlugin.PHONEBOOK_SELECT, this);
            return true;
        }





        return true;
    }
}
