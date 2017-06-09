package com.suypower.stereo.suypowerview.Camera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;


import com.suypower.stereo.suypowerview.Base.Init;
import com.suypower.stereo.suypowerview.Common.Common;
import com.suypower.stereo.suypowerview.R;
import com.suypower.stereo.suypowerview.Server.StereoService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

/**
 * 显示拍照功能
 * 拍照
 * 预览
 * 照片上传
 *
 * @author YXG 2015-04-13
 */
public class CameraCore {

        public final static String SAVEPPHOTOPATH =  Environment.getExternalStorageDirectory()+"/DCIM/Camera/";
//    public final static String SAVEPPHOTOPATH = Init.getContext().getCacheDir() + "/";
    public static final String TAG = "CameraPlugin";
    private int chooseimages = 0;

    private Activity activity;


    /**
     * 拍照构造
     *
     * @param activity
     */
    public CameraCore(Activity activity) {
        this.activity = activity;
    }


    public void openPreviewPhotoForNavtive(int precounts) {
        chooseimages = precounts;
        Intent intent = new Intent(activity, PreviewPhotoViewPlugin.class);
        intent.putExtra("precounts", precounts);
        activity.startActivityForResult(intent, PreviewPhotoViewPlugin.JSCallPreviewPhtoto);
        activity.overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.alpha_exit);
    }

    public Boolean takePictureForNaative() {




        return CameraHelper.takePhoto(activity);
    }


    /**
     * 打开预览界面
     *
     * @param context
     * @param stringList
     * @param mediaid
     * @param isdelete   是否提供删除
     */
    public void openPreviewUrlPhoto(Context context, List<String> stringList, String mediaid, Boolean isdelete) {
        Intent intent = new Intent(context, PreviewPhotoViewPager.class);
        Bundle bundle = new Bundle();

        String[] urlfiles = new String[stringList.size()];
        int pages = 0;
        for (int i = 0; i < stringList.size(); i++) {
            urlfiles[i] = stringList.get(i);
            if (mediaid.equals(stringList.get(i)))
                pages = i;
        }
        bundle.putStringArray("mediaids", urlfiles);
        bundle.putInt("src", pages);
        bundle.putBoolean("isdelete", isdelete);
        intent.putExtras(bundle);
        activity.startActivityForResult(intent, 1);
        ((Activity) context).overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.alpha_exit);

    }


    public void openPreviewUrlPhoto(Context context, String mediaid) {
        Intent intent = new Intent(context, PreviewPhotoViewPager.class);
        Bundle bundle = new Bundle();

        String[] urlfiles = new String[1];
        urlfiles[0] = mediaid;
        if (!Common.checkCacheIsExits(mediaid, ".jpg")) {
            bundle.putInt("model", 3);//全部照片预览模式
            urlfiles[0] = mediaid;
        } else {
            bundle.putInt("model", 4);//本地浏览
        }
        bundle.putStringArray("urls", urlfiles);
        bundle.putString("src", "");
        intent.putExtras(bundle);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.scale, R.anim.alpha_exit);

    }

}
