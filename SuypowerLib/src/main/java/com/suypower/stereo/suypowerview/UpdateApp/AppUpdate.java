package com.suypower.stereo.suypowerview.UpdateApp;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.suypower.stereo.suypowerview.Base.Init;

/**
 * Created by Stereo on 16/7/19.
 */
public class AppUpdate {

    private DownloadCompleteReceiver downloadCompleteReceiver;
    private DownloadServer downloadServer;
    private DownloadManager downloadManager;
    private IUpdateResult iUpdateResult;
    public Boolean IsClose=true;



    public AppUpdate(IUpdateResult iUpdateResult)
    {
        this.iUpdateResult=iUpdateResult;
    }

    public class DownloadServer {


        /**
         * 初始化下载器 *
         */
        public DownloadServer(DownloadCompleteReceiver downloadCompleteReceiver1) {
            downloadCompleteReceiver = downloadCompleteReceiver1;

        }

        public DownloadManager initDownloadServer(final String updateurl) {

            try {
                downloadManager = (DownloadManager) Init.getContext().getSystemService(Context.DOWNLOAD_SERVICE);

                //设置下载地址
                DownloadManager.Request down = new DownloadManager.Request(
                        Uri.parse(updateurl));
                down.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE
                        | DownloadManager.Request.NETWORK_WIFI);
                down.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                down.setVisibleInDownloadsUi(true);
                down.setDestinationInExternalFilesDir(Init.getContext(),
                        Environment.DIRECTORY_DOWNLOADS, "suehome.apk");
                downloadManager.enqueue(down);

                return downloadManager;
            } catch (Exception e) {
                return null;
            }

        }


    }

    public class DownloadCompleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("下载 返回的状态", intent.getAction());
            //判断是否下载完成的广播
            if (intent.getAction().equals(
                    DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                //获取下载的文件id
                long downId = intent.getLongExtra(
                        DownloadManager.EXTRA_DOWNLOAD_ID, -1);

                //自动安装apk
                context.unregisterReceiver(downloadCompleteReceiver);

                installAPK(downloadManager.getUriForDownloadedFile(downId));
            }


        }

        /**
         * 安装apk文件
         */
        private void installAPK(Uri apk) {

            // 通过Intent安装APK文件
            if (apk == null) {

                Toast.makeText(Init.getContext(), "下载更新失败，请重新尝试", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intents = new Intent();
            intents.setAction("android.intent.action.VIEW");
            intents.addCategory("android.intent.category.DEFAULT");
            intents.setType("application/vnd.android.package-archive");
            intents.setData(apk);
            intents.setDataAndType(apk, "application/vnd.android.package-archive");
            intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Init.getContext().startActivity(intents);
            if (IsClose)
                System.exit(0);

        }

    }


    public void downloadAPK(String updateurl) {

        if (downloadCompleteReceiver != null)
            Init.getContext().unregisterReceiver(downloadCompleteReceiver);
        downloadCompleteReceiver = new DownloadCompleteReceiver();
        DownloadServer downloadServer = new DownloadServer(downloadCompleteReceiver);
        Init.getContext().registerReceiver(downloadCompleteReceiver, new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        downloadManager = downloadServer.initDownloadServer(updateurl);
        if (downloadManager == null) {
            Init.getContext().unregisterReceiver(downloadCompleteReceiver);
            Toast.makeText(Init.getContext(), "更新失败", Toast.LENGTH_SHORT).show();
            if (iUpdateResult != null)
                iUpdateResult.DownloadResult(-1, "");
        }

    }


    public interface IUpdateResult {

        void DownloadResult(int state, String msg);
    }


}
