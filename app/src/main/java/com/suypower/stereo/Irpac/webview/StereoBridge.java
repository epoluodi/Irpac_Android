package com.suypower.stereo.Irpac.webview;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;

import com.suypower.stereo.Irpac.Httpservice.HttpFavorite;
import com.suypower.stereo.Irpac.Httpservice.InterfaceTask;
import com.suypower.stereo.Irpac.System.APP;
import com.suypower.stereo.suypowerview.AlertView.AlertDlg;
import com.suypower.stereo.suypowerview.ServerReturnData.ReturnData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bingdor on 2016/8/1.
 */
public class StereoBridge {
    private Handler  m_handler;
    public StereoBridge(Handler handler){
        this.m_handler = handler;
    }


    InterfaceTask interfaceTask =new InterfaceTask() {
        @Override
        public void TaskResultForMessage(Message message) throws JSONException {
            //获取回调方法名称
            ReturnData result= (ReturnData)message.obj;
            String callBackMethod=result.getJsCallBackMethod();
           final String callBackJs= String.format("javascript:%1$s('%2$s')", callBackMethod, result.getReturnMsg());
         //  final String callBackJs="javascript:deleteFavoritecallback('1qaaa')";
            Message msg = m_handler.obtainMessage();
            msg.obj= callBackJs;
            m_handler.sendMessage(msg);
        }
    };

    @JavascriptInterface
    public void log(String str){
        Log.i(this.getClass().toString(),str);
        final AlertDlg alertDlg = new AlertDlg(APP.getApp(), AlertDlg.AlertEnum.ALTERTYPE);
        alertDlg.setContentText(str);
        alertDlg.setOkClickLiseter(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDlg.dismiss();
            }
        });
        alertDlg.show();
    }


    /***
     * 添加收藏
     */
    @JavascriptInterface
    public void addFavorite(String args, String jsCallBackMethod) throws JSONException {
        JSONObject jsonArgs = new JSONObject(args);
        Map<String,Object> params = new HashMap<>();
        params.put("newsId",jsonArgs.getString("newsId"));
        params.put("coverImg",jsonArgs.getString("coverImg"));
        params.put("summary",jsonArgs.getString("summary"));
        params.put("url",jsonArgs.getString("url"));
        params.put("title",jsonArgs.getString("title"));
        params.put("jsCallBackMethod",jsCallBackMethod);
        HttpFavorite httpFavorite = new HttpFavorite(interfaceTask);
        try {
            httpFavorite.addFavorite(params);
        }
        catch (Exception e)
        {

        }
    }

    /***
     * 取消收藏
     * @param args
     */
    @JavascriptInterface
    public  void deleteFavorite(String args,String jsCallBackMethod) throws JSONException
    {
        JSONObject jsonArgs = new JSONObject(args);
        HttpFavorite httpFavorite = new HttpFavorite(interfaceTask);
        Map<String,Object> params = new HashMap<>();
        params.put("favId",jsonArgs.getString("favId"));
        params.put("jsCallBackMethod",jsCallBackMethod);

       //final String callBackJs= String.format("javascript:%1$s(%2$s)", "deleteFavoritecallback", "123");
       // cordovaWebView.loadUrl(callBackJs);
        try
        {
            httpFavorite.deleteFavorite(params);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
