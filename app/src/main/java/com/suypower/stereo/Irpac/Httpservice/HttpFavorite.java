package com.suypower.stereo.Irpac.Httpservice;


import android.os.Looper;
import java.util.Map;


/**
 * Created by Stereo on 16/4/15.
 */
public class HttpFavorite{
    public static final int QUERY_FAVORITE = 0;//查询
    public static final int DELETE_FAVORITE = 1;//删除
    public static final int ADD_FAVORITE = 2;//删除

    InterfaceTask m_interfaceTasks;

    public HttpFavorite(InterfaceTask interfaceTask) {
        m_interfaceTasks =interfaceTask;
    }

    /***
     * 查询我的收藏列表
     * @param param
     */
    public void queryFavorite(Map<String,Object> param) throws Exception{
        Map<String, Object> params = param;//调用的参数
        params.put("what", String.valueOf(HttpUtils.FavoriteTask));
        params.put("arg2",  String.valueOf(QUERY_FAVORITE));
        HttpRequestTask task = null;
        try {
            task = new HttpRequestTask(m_interfaceTasks, HttpMethodUtils.getFavoriteMethod);
            task.startTask(params);
        }
        catch (Exception e)
        {
            if(task != null)
            {
                task.stopTask();
            }
        }
    }

    /***
     * 添加收藏
     */
    public  void  addFavorite(Map<String,Object> param) throws Exception
    {
        Map<String, Object> params = param;//调用的参数
        params.put("what", String.valueOf(HttpUtils.DeleteFavoriteTask));
        params.put("arg2",  String.valueOf(ADD_FAVORITE));
        HttpRequestTask task = null;
        try {
            task = new HttpRequestTask(m_interfaceTasks, HttpMethodUtils.addFavoriteMethod);
            task.startTask(params);
        }
        catch (Exception e)
        {
            if(task != null)
            {
                task.stopTask();
            }
        }
    }

    /***
     * 取消收藏
     */
    public  void  deleteFavorite(Map<String,Object> param) throws Exception
    {
        Map<String, Object> params = param;//调用的参数
        params.put("what", String.valueOf(HttpUtils.DeleteFavoriteTask));
        params.put("arg2",  String.valueOf(DELETE_FAVORITE));
        HttpRequestTask task = null;
        try {
            task = new HttpRequestTask(m_interfaceTasks, HttpMethodUtils.deleteFavoriteMethod);
            task.startTask(params);
        }
        catch (Exception e)
        {
            if(task != null)
            {
                task.stopTask();
            }
        }
    }

}
