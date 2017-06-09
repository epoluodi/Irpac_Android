package com.suypower.stereo.Irpac.CordovaPlugin.Http;

import android.os.Message;

import com.suypower.stereo.Irpac.Httpservice.HttpFavorite;
import com.suypower.stereo.Irpac.Httpservice.InterfaceTask;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * app 系统插件
 * @author yxg
 *
 */
public class FavoriteRequest extends CordovaPlugin {
    private CallbackContext callbackContext = null;
	InterfaceTask m_interfaceTask = new InterfaceTask() {
		@Override
		public void TaskResultForMessage(Message message) throws JSONException {
			String  result = message.obj.toString();
			callbackContext.success(new JSONObject(result));
		}
	};
	private HttpFavorite httpFavorite = new HttpFavorite(m_interfaceTask);

    @Override
    public boolean execute(String action, JSONArray args,
    		CallbackContext callbackContext)  {
    	this.callbackContext = callbackContext;
		Message message=new Message();

		message.arg1= this.webView.cordovaWebViewId; //前端webview  id

		/***
		 * 添加收藏
		 */
		try
		{
			switch (action)
			{
				case "addFavorite":
					addFav(args);
					break;
				case "deleteFavorite":
					deleteFav(args);
					break;
				default:
					break;
			}

		}
		catch (Exception e)
		{
			callbackContext.error("调用服务失败！");
		}

    	return true;
    }

	private void addFav(JSONArray args) throws Exception
	{
		JSONObject jsonArgs =(JSONObject) args.get(0);
		Map<String,Object> params = new HashMap<>();
		params.put("newsId",jsonArgs.getString("newsId"));
		params.put("coverImg",jsonArgs.getString("coverImg"));
		if (jsonArgs.isNull("summary"))
			params.put("summary","");
		else
			params.put("summary",jsonArgs.isNull("summary"));
		params.put("url",jsonArgs.getString("url"));
		params.put("title",jsonArgs.getString("title"));
		if(httpFavorite == null)
		{
			httpFavorite = new HttpFavorite(m_interfaceTask);
		}
		httpFavorite.addFavorite(params);

	}

	private void deleteFav(JSONArray args) throws Exception
	{
		JSONObject jsonArgs =(JSONObject) args.get(0);
		Map<String,Object> params = new HashMap<>();
		params.put("favId",jsonArgs.getString("favId"));
		if(httpFavorite == null)
		{
			httpFavorite = new HttpFavorite(m_interfaceTask);
		}
		httpFavorite.deleteFavorite(params);
	}
}
