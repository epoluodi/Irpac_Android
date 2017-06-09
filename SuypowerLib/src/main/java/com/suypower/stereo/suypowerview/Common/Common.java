package com.suypower.stereo.suypowerview.Common;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;

import com.suypower.stereo.suypowerview.Base.Init;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 通用类
 * Created by Stereo on 16/7/7.
 */
public class Common {
    public static String Token = "";
    public static String DeviceID = getUuid();



    /**
     * 获得系统时间
     *
     * @return
     */
    public static String GetSysTime() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sDateFormat.format(new java.util.Date());
        return date;
    }

    /**
     * 获取本机uuid
     *
     * @return
     */
    public static String getUuid() {
        return Settings.Secure.getString(
                Init.getContext().getContentResolver(), android.provider.
                        Settings.Secure.ANDROID_ID);

    }

    /**
     * 播放声音
     *
     * @param context
     */
    public static void PlaysoundScan(Context context) {
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);//系统自带提示音

        Ringtone rt = RingtoneManager.getRingtone(context, uri);
        rt.play();
    }


    /**
     * 震动
     *
     * @param context
     * @param time
     */
    public static void Vibrator(Context context, long time) {
        ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(time);
    }


    /**
     * 检查是否为URL抵制
     *
     * @param barcode
     * @return
     */
    public static String CheckGetWebURL(String barcode) {
        Pattern pattern = Pattern.compile("((http|https)://)([A-Za-z0-9-]+.)+[A-Za-z]{2,}(:[0-9]+)?");

        Matcher m = pattern.matcher(barcode);
        if (!m.find()) {
            return "";
        }
        return m.group();
    }


    /**
     * 通过格式化获得系统时间
     *
     * @param format
     * @return
     */
    public static String GetSysTime(String format) {
        SimpleDateFormat sDateFormat = new SimpleDateFormat(format);
        String date = sDateFormat.format(new java.util.Date());
        return date;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(float dpValue) {
        final float scale = Init.getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(float pxValue) {
        final float scale = Init.getContext().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    /**
     * 判断时间间隔是否小于1分钟
     *
     * @param dt1
     * @param dt2
     * @return
     */
    public static Boolean isInOneMin(String dt1, String dt2) {
        try {

//            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            Date date1 = sDateFormat.parse(dt1);
//            Date date2 = sDateFormat.parse(dt2);
            Date date1 = new Date(Long.valueOf(dt1));
            Date date2 = new Date(Long.valueOf(dt2));
            long l = date2.getTime() - date1.getTime();
            if (l > 60 * 1000)
                return true;
            else
                return false;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 检查文件是否存在
     *
     * @param filename
     * @return
     */
    public static Boolean checkCacheIsExits(String filename, String filetype) {
        File file = new File(Init.getContext().getCacheDir(), filename + filetype);
        return file.exists();
    }

    public static Boolean checkFileIsExits(String filename) {
        File file = new File(Init.getContext().getFilesDir(), filename);
        return file.exists();
    }

    /**
     * 删除文件
     * @param filename
     */
    public static void delFile(String filename)
    {
        File file = new File(Init.getContext().getCacheDir(), filename);
        file.delete();
    }
    /**
     * 计算MD5 值
     *
     * @param bitmap
     * @return
     */
    public static String imgToMD5(Bitmap bitmap) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] buffer = baos.toByteArray();
            messageDigest.update(buffer);
            byte[] bytemd5 = messageDigest.digest();
            StringBuilder strbuf = new StringBuilder();
            for (int i = 0; i < bytemd5.length; i++) {
                if (Integer.toHexString(0xff & bytemd5[i]).length() == 1) {
                    strbuf.append("0").append(Integer.toHexString(0xff & bytemd5[i]));
                } else {
                    strbuf.append(Integer.toHexString(0xff & bytemd5[i]));
                }
            }
            return strbuf.toString();
        } catch (Exception e) {
            e.printStackTrace();

        }
        return "";
    }

    /**
     * 获取图片MD5
     *
     * @param buffer
     * @return
     */
    public static String imgToMD5(byte[] buffer) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(buffer);
            byte[] bytemd5 = messageDigest.digest();
            StringBuilder strbuf = new StringBuilder();
            for (int i = 0; i < bytemd5.length; i++) {
                if (Integer.toHexString(0xff & bytemd5[i]).length() == 1) {
                    strbuf.append("0").append(Integer.toHexString(0xff & bytemd5[i]));
                } else {
                    strbuf.append(Integer.toHexString(0xff & bytemd5[i]));
                }
            }
            return strbuf.toString();
        } catch (Exception e) {
            e.printStackTrace();

        }
        return "";
    }


    /**
     * 检查服务运行
     *
     * @param classname
     * @return
     */
    public static boolean isCoreServiceRunning(String classname) {

        ActivityManager manager = (ActivityManager) Init.getContext().getSystemService(Service.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))

        {

            if (classname.equals(service.service.getClassName())) {
//                Log.i("发现核心服务", service.service.getClassName());
                return true;
            }

        }
        return false;
    }


    /**
     * Bitmap缩小放大
     *
     * @param scale 缩放比率
     */
    private static Bitmap ConvetBitmapForScale(Bitmap bitmap, float scale) {
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale); //长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizeBmp;
    }


    /**
     * 遮盖图片
     *
     * @param bitmap
     * @param mMaskSource
     * @return
     */
    public static Bitmap MaskImage(Bitmap bitmap, Bitmap mMaskSource) {

        // 获得图片的宽高
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) mMaskSource.getWidth()) / width;
        float scaleHeight = ((float) mMaskSource.getHeight()) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newMask = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        bitmap.recycle();
        Log.i("图片宽度", String.valueOf(bitmap.getWidth()));
        Log.i("图片高度", String.valueOf(bitmap.getHeight()));
        Log.i("遮盖图片宽度", String.valueOf(mMaskSource.getWidth()));
        Log.i("遮盖图片高度", String.valueOf(mMaskSource.getHeight()));
        Bitmap result = Bitmap.createBitmap(mMaskSource.getWidth(), mMaskSource.getHeight(), Bitmap.Config.ARGB_8888);
        //将遮罩层的图片放到画布中
        Canvas mCanvas = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));//叠加重复的部分，显示下面的
        mCanvas.drawBitmap(newMask, 0, 0, null);
        mCanvas.drawBitmap(mMaskSource, 0, 0, paint);
        paint.setXfermode(null);
        return result;
    }


    /**
     * 文件拷贝
     *
     * @param Raw      资源文件
     * @param filename 目标文件名称
     */
    public static void CopyDb(int Raw, String filename) {
        InputStream inputStream;
        try {
            inputStream = Init.getContext().getResources().openRawResource(Raw);
            byte[] bytebuff = new byte[inputStream.available()];
            inputStream.read(bytebuff);
            File file = new File(Init.getContext().getFilesDir() + filename);
            if (file.exists())
                file.delete();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(bytebuff);
            fileOutputStream.close();

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    //获取文件大小
    public static int getFileSize(String file) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            int size = fileInputStream.available();
            fileInputStream.close();
            return size;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    public static String getTimeInMillis(String dt) {
        try {
            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date1 = sDateFormat.parse(dt);
            return String.valueOf(date1.getTime());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "";
        }
    }


    public static String getCustomDtForLongDt(String dt)
    {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("MM月dd日");
        try
        {
            Date date = new Date(Long.valueOf(dt)); //sDateFormat.parse(dt);
            String strdate = sDateFormat.format(date);
            return strdate;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "";
        }
    }

    public static String GetSysTimeWithFormat(String dt) {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strdate = "";
        try {
            Date date = new Date(Long.valueOf(dt)); //sDateFormat.parse(dt);

            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal1.setTime(date);
            if (cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)) {
                sDateFormat = new SimpleDateFormat("HH:mm");
                strdate = sDateFormat.format(date);
                return strdate;
            }

            if (isSameWeekDates(new Date(), date)) {
                sDateFormat = new SimpleDateFormat("HH:mm");
                strdate = sDateFormat.format(date);
                strdate = getWeekStr(date) + " " + strdate;
                return strdate;

            } else {
                sDateFormat = new SimpleDateFormat("MM-dd HH:mm");
                strdate = sDateFormat.format(date);
            }


            return strdate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 判断二个时间是否在同一个周
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isSameWeekDates(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        int subYear = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
        if (0 == subYear) {
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
                return true;
        } else if (1 == subYear && 11 == cal2.get(Calendar.MONTH)) {
// 如果12月的最后一周横跨来年第一周的话则最后一周即算做来年的第一周
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
                return true;
        } else if (-1 == subYear && 11 == cal1.get(Calendar.MONTH)) {
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
                return true;
        }
        return false;
    }


    /**
     * 根据一个日期，返回是星期几的字符串
     *
     * @param sdate
     * @return
     */

    public static String getWeekStr(Date sdate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(sdate);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        String today = "";
        if (day == 2) {
            today = "星期一";
        } else if (day == 3) {
            today = "星期二";
        } else if (day == 4) {
            today = "星期三";
        } else if (day == 5) {
            today = "星期四";
        } else if (day == 6) {
            today = "星期五";
        } else if (day == 7) {
            today = "星期六";
        } else if (day == 1) {
            today = "星期日";
        }
        return today;
    }




    /**
     * 将字符串中的中文转化为拼音,其他字符不变
     * 花花大神->huahuadashen
     * @param inputString
     * @return
     */
    public static String getPingYin(String inputString) {


        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();

        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);

        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

        format.setVCharType(HanyuPinyinVCharType.WITH_V);

        char[] input = inputString.trim().toCharArray();

        String output = "";

        try {

            for (char curchar : input) {

                if (java.lang.Character.toString(curchar).matches("[\\u4E00-\\u9FA5]+")) {

                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(curchar, format);

                    output += temp[0];

                } else

                output += java.lang.Character.toString(curchar);

            }

        } catch (BadHanyuPinyinOutputFormatCombination e) {

            e.printStackTrace();

        }

        return output;

    }

            /**

             * 汉字转换为汉语拼音首字母，英文字符不变
             * 花花大神->hhds
             * @param chinese
             *            汉字
             * @return 拼音
             */
    public static String getFirstSpell(String chinese) {

        StringBuffer pybf = new StringBuffer();

        char[] arr = chinese.toCharArray();

        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();

        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);

        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

        for (char curchar : arr) {

            if (curchar > 128) {

                try {

                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(curchar, defaultFormat);

                    if (temp != null) {

                        pybf.append(temp[0].charAt(0));

                    }

                } catch (BadHanyuPinyinOutputFormatCombination e) {

                    e.printStackTrace();

                }

            } else {

                pybf.append(curchar);

            }

        }

        return pybf.toString().replaceAll("\\W", "").trim();

    }



    /**
     * 是否为wifi
     * @param mContext
     * @return
     */
    public static boolean IsWifi(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }










}
