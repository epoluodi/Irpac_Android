package com.suypower.stereo.Irpac.Httpservice;

import android.os.Message;

import org.json.JSONException;

/**
 * 任务接口
 * Created by Stereo on 16/4/14.
 */
public interface InterfaceTask {

    /**
     * 任务执行结果

     */

    void TaskResultForMessage(Message message) throws JSONException;

}
