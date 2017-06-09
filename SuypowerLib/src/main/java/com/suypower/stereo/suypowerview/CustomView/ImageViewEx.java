package com.suypower.stereo.suypowerview.CustomView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.suypower.stereo.suypowerview.Base.Init;
import com.suypower.stereo.suypowerview.Common.Common;
import com.suypower.stereo.suypowerview.File.FileDownload;
import com.suypower.stereo.suypowerview.Server.StereoService;

import java.net.URI;

/**
 * Created by Stereo on 2017/2/20.
 */

public class ImageViewEx extends ImageView {
    private String mediaid;
    private String subtype;
    private String _url;
    private int actualWidth;
    private int actualHeight;

    public ImageViewEx(Context context) {
        super(context);
    }

    public ImageViewEx(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public ImageViewEx(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
    }


    public void setMediaId(String mediaId, String type) {
        this.mediaid = mediaId;
        subtype = type;
        if (!Common.checkCacheIsExits(mediaId, ".jpg")) {
            new Thread(runnable).start();
        } else {
            handler.sendEmptyMessage(0);
        }
    }


    public int getActualWidth() {
        return actualWidth;
    }

    public int getActualHeight() {
        return actualHeight;
    }

    public void setSource(String url) {
        _url = url;
        subtype = "";
        mediaid = url.substring(url.lastIndexOf("/"), url.lastIndexOf("."));
        if (!Common.checkCacheIsExits(mediaid, ".jpg")) {
            new Thread(runnableUrl).start();
        } else {
            handler.sendEmptyMessage(0);
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            String url = String.format("%1$smedia/download", StereoService.AppUrl);
            FileDownload fileDownload = new FileDownload(url, mediaid);
            fileDownload.imgamub = subtype;
            fileDownload.mediatype = ".jpg";
            Boolean r = fileDownload.streamDownLoadFile();
            if (r) {//获得突破
                handler.sendEmptyMessage(0);
            }
            System.gc();
        }
    };


    Runnable runnableUrl = new Runnable() {
        @Override
        public void run() {

            FileDownload fileDownload = new FileDownload(_url, mediaid);
            fileDownload.imgamub = subtype;
            fileDownload.mediatype = ".jpg";
            Boolean r = fileDownload.streamDownLoadFile();
            if (r) {//获得突破
                handler.sendEmptyMessage(0);
            }
            System.gc();
        }
    };


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 0:
                    Bitmap bitmap = BitmapFactory.decodeFile(Init.getContext().getCacheDir() + "/" + mediaid + ".jpg"); //将图片的长和宽缩小味原来的1/2
                    actualWidth = bitmap.getWidth();
                    actualHeight = bitmap.getHeight();
                    if (bitmap != null) {
                        try {
                            setImageBitmap(bitmap);
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.gc();
                        }
                    }
                    break;
            }
        }
    };

}
