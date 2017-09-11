package com.suypower.stereo.Irpac.CordovaPlugin.ajax;

import android.util.Log;


import com.suypower.stereo.Irpac.System.AppConfig;
import com.suypower.stereo.suypowerview.Http.AjaxHttp;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.logging.Handler;

/**
 * 封装消息异步请求插件
 *
 * @author liuzeren
 */
public class AjaxRequest extends CordovaPlugin {


    private CallbackContext mcallbackContext = null;

    private JSONObject jsonObject, paramjson;
    private String url, dataType;

    @Override
    public boolean execute(final String action, JSONArray args,
                           CallbackContext callbackContext) throws JSONException {
        mcallbackContext = callbackContext;


        jsonObject = args.getJSONObject(0);
        Log.i("开始转发URL", jsonObject.toString());
        url = AppConfig.cHost+"/riskControl/app" + jsonObject.getString("url");
        dataType = jsonObject.getString("dataType");
        paramjson = null;


//        this.cordova.getThreadPool().execute(new Runnable() {
//            @Override
//            public void run() {
                try {
                    AjaxHttp ajaxHttp = new AjaxHttp();
                    if (action.equals("get")) {
                        if (!jsonObject.isNull("param"))

                            url = url + jsonObject.getString("param");
                        ajaxHttp.openRequest(url, AjaxHttp.REQ_METHOD_GET);
                        if (!ajaxHttp.sendRequest()) {
                            ajaxHttp.closeRequest();
                            mcallbackContext.error("请求失败");
                            return true;
                        }

                    }

                    if (action.equals("post")) {

                        ajaxHttp.openRequest(url, AjaxHttp.REQ_METHOD_POST);
                        if (!jsonObject.isNull("param"))
                            paramjson = jsonObject.getJSONObject("param");
                        if (paramjson != null) {
                            Iterator<String> iterator = paramjson.keys();
                            while (iterator.hasNext()) {
                                String key = iterator.next();
                                ajaxHttp.setPostValuesForKey(key, paramjson.getString(key));
                            }
                            ajaxHttp.setEntity(ajaxHttp.getPostData());
                        }

                        if (!ajaxHttp.sendRequest()) {
                            ajaxHttp.closeRequest();
                            mcallbackContext.error("请求失败");
                            return true;
                        }
                    }


                    //获得返回数据
                    byte[] buffer = ajaxHttp.getRespBodyData();
                    if (buffer == null) {
                        ajaxHttp.closeRequest();
                        mcallbackContext.error("请求失败");
                        return true;
                    }
//
                    try {
                        String result = new String(buffer, "utf-8");
                        Log.e("结果", result);
                        if (dataType.equals("json")) {
                            ajaxHttp.closeRequest();
                            JSONObject resultjson = new JSONObject(result);
                            mcallbackContext.success(resultjson);
                            return true;
                        } else {
                            ajaxHttp.closeRequest();
                            mcallbackContext.success(result);
                            return true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        mcallbackContext.error("请求失败");
                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
//
//            }
//        });


        return true;

    }



}
