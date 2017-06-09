package com.suypower.stereo.suypowerview.Common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.Base64;
import android.util.Log;

import com.suypower.stereo.suypowerview.Base.Init;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by Stereo on 16/7/22.
 */
public class ImageCore {


    /**
     * 缩放按照固定比例
     * @param fileid
     * @return
     */
    public static Bitmap decodeBitmap1024(String fileid) {
        String realbitmap = Init.getContext().getCacheDir() +  "/" +  fileid + ".jpg";
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(realbitmap,options);//此时返回bm为空
        options.inJustDecodeBounds = false;
        int w = options.outWidth;
        int h = options.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 600;//这里设置高度为800f
        float ww = 800;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (options.outWidth / ww);
        } else if (w < h && h > ww) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (options.outHeight / ww);
        }
        if (be <= 0)
            be = 1;
        options.inSampleSize = (int)(be);
        Bitmap bitmap = BitmapFactory.decodeFile(realbitmap, options);
        return bitmap;
    }


    //缩略图
    public static Bitmap decodeBitmap(String realbitmap, int inSampleSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(realbitmap,options);//此时返回bm为空



        options.inJustDecodeBounds = false;
        int w = options.outWidth;
        int h = options.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (options.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (options.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        if (inSampleSize==0)
            options.inSampleSize = (int)(be*0.9);
        else
            options.inSampleSize =inSampleSize;
        Bitmap bitmap = BitmapFactory.decodeFile(realbitmap, options);
        return bitmap;
    }



    public static Bitmap reSizeImage(Bitmap bitmap, int newWidth, int newHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        // 计算出缩放比
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 矩阵缩放bitmap
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }


    /**
     * 从media 获得图片对象
     * @param mediaid
     * @return
     */
    public static Bitmap bitbmpfrommediaLocal(String mediaid, int inSampleSize)
    {
        String realbitmap = Init.getContext().getCacheDir() +  "/" +  mediaid + ".jpg";


        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(realbitmap,options);//此时返回bm为空
        options.inJustDecodeBounds = false;
        int w = options.outWidth;
        int h = options.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800 / 5;//这里设置高度为800f
        float ww = 480 /5 ;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (options.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (options.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        if (inSampleSize==0)
            options.inSampleSize = (int)(be/2);
        else
            options.inSampleSize =inSampleSize;
        Bitmap bitmap = BitmapFactory.decodeFile(realbitmap, options);
        return bitmap;
    }

    /**
     * 图片转base64
     * @param filepath
     * @return
     */
    public static String GetPhotoBase64(String filepath)
    {
        try {


            Bitmap bitmap = decodeBitmap(filepath,5);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            byte[] buffer = baos.toByteArray();
            bitmap.recycle();
            return Base64.encodeToString(buffer, Base64.DEFAULT);

        }
        catch (Exception e)
        {e.printStackTrace();
            return "";}
    }

    /**
     * 复制文件
     * @param filepath
     * @return
     */
    public static String copyCacheFile(String filepath)
    {
        try {


            UUID uuid= UUID.randomUUID();

            File fIle = new File(filepath);
            InputStream inputStream = new FileInputStream(fIle);
            byte[] bytebuff = new byte[inputStream.available()];
            inputStream.read(bytebuff);
            inputStream.close();
//            String uuid = CommonPlugin.imgToMD5(bytebuff);

            File outfile = new File(Init.getContext().getCacheDir()  +
                    File.separator + uuid.toString().replace("-","") + ".jpg");
            OutputStream outputStream = new FileOutputStream(outfile);
            outputStream.write(bytebuff);
            outputStream.close();
            Log.i("文件",outfile.getAbsolutePath());
            Log.i("file",outfile.exists()==true? "1":"0");
            return uuid.toString().replace("-","");

        }
        catch (Exception e)
        {e.printStackTrace();
            return "";}
    }

    public static String copyCacheFile(byte[] bytebuff)
    {
        try {

            String uuid = UUID.randomUUID().toString(); //CommonPlugin.imgToMD5(bytebuff);

            File outfile = new File(Init.getContext().getCacheDir()  +
                    File.separator + uuid.toString().replace("-","") + ".jpg");
            OutputStream outputStream = new FileOutputStream(outfile);
            outputStream.write(bytebuff);
            outputStream.close();
            Log.i("文件",outfile.getAbsolutePath());
            Log.i("file",outfile.exists()==true? "1":"0");
            return uuid.toString().replace("-","");
        }
        catch (Exception e)
        {e.printStackTrace();
            return "";}
    }

    public static Bitmap createRoundConerImage(Bitmap source)
    {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap target = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(target);
        RectF rect = new RectF(0, 0, source.getWidth(), source.getHeight());
        canvas.drawRoundRect(rect, 6, 6, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, 0, 0, paint);
        return target;
    }
}
