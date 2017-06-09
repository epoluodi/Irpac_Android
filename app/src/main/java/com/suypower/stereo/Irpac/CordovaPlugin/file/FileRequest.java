package com.suypower.stereo.Irpac.CordovaPlugin.file;

import android.os.Message;


import com.suypower.stereo.Irpac.Httpservice.BaseTask;
import com.suypower.stereo.Irpac.Httpservice.HttpFile;
import com.suypower.stereo.Irpac.Httpservice.InterfaceTask;
import com.suypower.stereo.suypowerview.CordovaPlugin.BaseViewPlugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * optionmenu Word 操作控制类
 * @author yxg
 *
 */
public class FileRequest extends CordovaPlugin {
	

	private int fileCount=0;
	private int fileindex=0;
	private JSONArray jsonArray;



    @Override
    public boolean execute(String action, JSONArray args,
    		CallbackContext callbackContext) throws JSONException {
    	this.callbackContext = callbackContext;
		//// TODO: 16/8/5 目前只支持jpg类型，如果需要增加类型，需要改造一下。已经有方案，如需要改造联系YXG 
		String[] fileids=new String[args.length()];
		fileCount = fileids.length;
		jsonArray = new JSONArray();
		for (int i=0;i<args.length();i++)
		{
			fileids[i] = args.getString(i);
		}
		String fileType = ".jpg";


		if (fileids.length==0)
		{
			callbackContext.error("没有需要上传的资源");
			return true;
		}

		if (action.equals("upload")) {

			HttpFile httpFile=new HttpFile(interfacetask, HttpFile.MULTIILEUPLOAD,fileType,"02","02",fileids);

			httpFile.startTask();
			return true;
		}
		if (action.equals("uploadSign")) {

			HttpFile httpFile=new HttpFile(interfacetask, HttpFile.MULTIILEUPLOAD,fileType,"02","04",fileids);

			httpFile.startTask();
			return true;
		}
		if (action.equals("download")) {
			this.jsondata = args.getJSONObject(0);
			this.cordova.onMessage(BaseViewPlugin.FILEDOWNLOAD_ACTION, this);
		}
		if (action.equals("cancelDownload")) {
//			this.jsondata = args.getJSONObject(0);
			this.cordova.onMessage(BaseViewPlugin.FILECANCELDOWNLOAD_ACTION, this);
		}
		if (action.equals("cancelUpload")) {
//			this.jsondata = args.getJSONObject(0);
			this.cordova.onMessage(BaseViewPlugin.FILECANCELUPLOAD_ACTION, this);
		}

    	return true;
    }



	InterfaceTask interfacetask=new InterfaceTask() {
		@Override
		public void TaskResultForMessage(Message message) throws JSONException {
			if (message.what== BaseTask.FileTask)
			{
				if (message.arg2 == HttpFile.MULTIILEUPLOAD)
				{
					if (message.arg1==BaseTask.SUCCESS)
					{
						jsonArray.put(message.obj.toString());
						fileindex++;
						if (fileindex == fileCount)
						{
							callbackContext.success(jsonArray);
							return;
						}

					}
					else
					{
						callbackContext.error("上传失败");
						return;
					}
				}
			}
		}
	};




}
