package com.suypower.stereo.suypowerview.Scan;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.suypower.stereo.suypowerview.Base.Init;
import com.suypower.stereo.suypowerview.CordovaPlugin.BaseViewPlugin;
import com.suypower.stereo.suypowerview.R;


import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.util.Hashtable;

/**
 * 一维二维扫描
 *
 * @author YXG 2015-04-13
 */
public class ScanPlugin extends BaseViewPlugin {

    public static final String TAG = "ScanPlugin";




    BaseViewPlugin baseViewPlugin = null;



    public ScanPlugin()
    {}


    public void showScanActivity(Activity activity)
    {
        Intent intent = new Intent(activity,ScanActivity.class);
        activity.startActivityForResult(intent, ScanActivity.SCANRESULTREQUEST);
        activity.overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.alpha_exit);
    }

    /**
     * 创建二维码
     * @param str
     * @param widthAndHeight
     * @return
     * @throws WriterException
     */
    public static Bitmap createQRCode(String str, int widthAndHeight)
            throws WriterException {
        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        BitMatrix matrix = new MultiFormatWriter().encode(str,
                BarcodeFormat.QR_CODE, widthAndHeight, widthAndHeight);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        boolean isFirstBlackPoint = false;
        int startX = 0;
        int startY = 0;
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    if (isFirstBlackPoint == false)
                    {
                        isFirstBlackPoint = true;
                        startX = x;
                        startY = y;
                    }
                    pixels[y * width + x] = 0xff000000;
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        int x1 = startX ;
        int y1 = startY ;
        if (x1 < 0 || y1 < 0) return bitmap;
        int w1 = width - x1 * 2;
        int h1 = height - y1 * 2;
        Bitmap bitmapQR = Bitmap.createBitmap(bitmap, x1, y1, w1, h1);
//        Bitmap bitmap = Bitmap.createBitmap(width, height,
//                Bitmap.Config.ARGB_8888);
//        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        bitmap.recycle();
        matrix.clear();
        return bitmapQR;
    }


    @Override
    public void loadWebUrl(String Url) {

    }

    @Override
    public void showOptionMenu(View v, int menutype) {

    }

    @Override
    public void onCordovaMessage(String id, Object data) {

    }

    @Override
    public int getMenuList(JSONArray menujson) {
        return 0;
    }
}
