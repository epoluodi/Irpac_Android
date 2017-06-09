package com.suypower.stereo.suypowerview.Camera;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;


import com.suypower.stereo.suypowerview.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 拍照功能原生调用
 * @author YXG
 */
public class CameraHelper {

    public static final int JSCallCamera = 100;//js 调用 拍照插件
    public static final int JSCallPre  = 101;//js 调用 拍照插件
    public static String photopath;



    /**
     * 获得系统照片存储路径
     * @return
     */
    private static Uri getOutputMediaFile()
    {
        File mediaStorageDir = null;
        try
        {
            mediaStorageDir = new File(CameraCore.SAVEPPHOTOPATH);

            Log.i("path","Successfully created Dir: "+ mediaStorageDir);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.d("path", "Error in Creating mediaStorageDir: "+ mediaStorageDir);
            return null;
        }


        if (!mediaStorageDir.exists())
            mediaStorageDir.mkdirs();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");
        photopath = mediaFile.getAbsolutePath();
        return Uri.fromFile(mediaFile);

    }


    /**
     * 调用原生拍照
     * @param activity 调用窗口上下文
     * @return ture 调用成功，false 失败
     */
    public  static Boolean takePhoto(Activity activity)
    {
        photopath="";
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // create a file to save the image
        Uri photouri =getOutputMediaFile();
        if (photouri == null)
            return false;

        intent.putExtra(MediaStore.EXTRA_OUTPUT,photouri);
        activity.startActivityForResult(intent, JSCallCamera);
        activity.overridePendingTransition(R.anim.slide_in_from_bottom,R.anim.alpha_exit);
        return true;
    }




}
