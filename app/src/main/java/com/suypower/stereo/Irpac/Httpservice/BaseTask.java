package com.suypower.stereo.Irpac.Httpservice;


import com.suypower.stereo.Irpac.System.UserInfo;
import com.suypower.stereo.suypowerview.Http.AjaxHttp;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Stereo on 16/4/15.
 */
public abstract class BaseTask {

    public static final int SUCCESS = 0;//成功
    public static final int FAILED = -1;//失败
    public static final int PASSWORD_ERROR = -2;//密码错误
    public static final int DOWNLOAD_FAIL = -3;//下载失败
    public static final int DOWNLOAD_FINISH_ALL = 1;//全部下载完成
    public static final int DOWNLOAD_FINISH_SINGLE = 2;//单个下载完成
    public static final int CREATEGROUP_FAIL = -4;//群聊创建错误
    public static final int SENDMSG_FAIL = -5;//群聊创建错误
    public static final int REMOVEGROUP_FAIL = -5;//群聊创建错误


    //    public static final int CommonTask = 0;//通用任务
    public static final int LoginTask = 1;//登录
    public static final int UserInfoTask = 2;//获取联系人信息
    public static final int CHATTASK = 3;//获取联系人信息
    //    public static final int DownloadFILETask = 3;//文件下载
//    public static final int IMTask = 4;//群聊任务
    public static final int FileTask = 5;//文件上传任务
    //    public static final int PublishNotics = 6;//发布公告任务
//    public static final int USERINFO=7;//用户信息更新任务
    public static final int MobileTask = 6;//E伴业务
    public static final int PhoneBookTask = 7;//通讯录
    public static final int ScoreTask = 8;//积分


    ExecutorService m_ThreadPool = null;
    AjaxHttp m_httpClient = null;


    public BaseTask() {
        m_httpClient = new AjaxHttp();
        m_ThreadPool = Executors.newFixedThreadPool(50);

    }

    public abstract void startTask();

    public abstract void stopTask();

}
