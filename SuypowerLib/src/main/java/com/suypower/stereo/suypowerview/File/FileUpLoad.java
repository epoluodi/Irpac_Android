package com.suypower.stereo.suypowerview.File;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import com.suypower.stereo.suypowerview.Base.Init;
import com.suypower.stereo.suypowerview.Common.BaseUserInfo;
import com.suypower.stereo.suypowerview.Common.Common;
import com.suypower.stereo.suypowerview.Common.ImageCore;
import com.suypower.stereo.suypowerview.R;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 * 上传照片 任务类
 *
 * @author YXG
 */
public class FileUpLoad  {


    public static final int UPLOADFILE = 1;//文件上传
    private static final String TAG = "uploadFile";
    private static final int TIME_OUT = 60 * 1000;   //超时时间
    private static final String CHARSET = "utf-8"; //设置编码

    private String fileid;
    private String filetype = "";
    private String uploadurl;
    private String uploadType,subType;


    public FileUpLoad(String url,String filetype, String fileid,String uploadtype,String subtype) {
        super();
        this.filetype=filetype;
        this.fileid=fileid;
        this.uploadurl=url;
        uploadType=uploadtype;
        subType=subtype;
    }

    public FileUpLoad(String url,String mediaType,String subtype) {
        super();
        this.uploadurl=url;
        uploadType=mediaType;
        subType=subtype;

        /**
         *       conn.setRequestProperty("mediaType", uploadType);
         conn.setRequestProperty("imageType", subType);
         */
    }



    /**
     * 上传
     * @return
     */
    public String uploadfile() {


        InputStream serverins = Init.getContext().getResources().openRawResource(R.raw.server);
        InputStream clinetins = Init.getContext().getResources().openRawResource(R.raw.client);

        X509HostnameVerifier hostnameVerifier = SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;


        SSLContext context=null;
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Certificate ca;
            try {
                ca = cf.generateCertificate(serverins);
            } finally {
                serverins.close();
            }
            KeyStore trustStore = KeyStore.getInstance("PKCS12");
            trustStore.load(null, null);
            trustStore.setCertificateEntry("ca", ca);

            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(trustStore);

            context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);
        }
        catch (Exception e)
        {e.printStackTrace();}


        String PREFIX = "--", LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data";   //内容类型
        String BOUNDARY = UUID.randomUUID().toString();  //边界标识   随机生成
        try {
            URL url = new URL(uploadurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true);  //允许输入流
            conn.setDoOutput(true); //允许输出流
            conn.setUseCaches(false);  //不允许使用缓存
            conn.setRequestMethod("POST");  //请求方式

            conn.setRequestProperty("Charset", CHARSET);  //设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
            conn.setRequestProperty("token", Common.Token);
            conn.setRequestProperty("deviceID", Common.DeviceID);
            conn.setRequestProperty("deviceType", "2");
            conn.setRequestProperty("mediaType", uploadType);
            conn.setRequestProperty("imageType", subType);
//            conn.setHostnameVerifier(hostnameVerifier);
//            conn.setSSLSocketFactory(context.getSocketFactory());




            ByteArrayOutputStream baos = null;
            baos = new ByteArrayOutputStream();
            Bitmap bitmap=null;
            if (uploadType.equals("02")) {
                if (subType.equals("01") || subType.equals("02")) {
                    bitmap = ImageCore.decodeBitmap1024(fileid);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                    bitmap.recycle();
                }else if (subType.equals("03") )
                {
                    bitmap = BitmapFactory.decodeFile(Init.getContext().getCacheDir() + File.separator+ fileid + ".jpg");
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    bitmap.recycle();
                }else if (subType.equals("04") )
                {
                    bitmap = BitmapFactory.decodeFile(Init.getContext().getCacheDir() + File.separator+ fileid + ".jpg");
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    bitmap.recycle();
                }
            }else if (uploadType.equals("05"))
            {
                FileInputStream fileInputStream=new FileInputStream(new File(Init.getContext().getCacheDir()
                        + File.separator+ fileid + ".aac"));
                byte[] bytes = new byte[fileInputStream.available()];
                fileInputStream.read(bytes);
                fileInputStream.close();
                baos.write(bytes);
            }
            /**
             * 当文件不为空，把文件包装并且上传
             */
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            StringBuffer sb = new StringBuffer();
            sb.append(PREFIX);
            sb.append(BOUNDARY);
            sb.append(LINE_END);
            /**
             * 这里重点注意：
             * name里面的值为服务器端需要key   只有这个key 才可以得到对应的文件
             * filename是文件的名字，包含后缀名的   比如:abc.png
             */
            sb.append("Content-Disposition: form-data; name=\"" + fileid + filetype + "\"; filename=\"" + fileid + "\"" + LINE_END);
            sb.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINE_END);
            sb.append(LINE_END);
            dos.write(sb.toString().getBytes());
            dos.write(baos.toByteArray());
            baos.close();
            dos.write(LINE_END.getBytes());
            byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
            dos.write(end_data);
            dos.flush();
            dos.close();
            /**
             * 获取响应码  200=成功
             * 当响应成功，获取响应的流
             */
            int res = conn.getResponseCode();
            Log.e(TAG, "response code:" + res);
            Log.e(TAG, "request success");
            InputStream input = conn.getInputStream();
            BufferedReader reader_post = new BufferedReader(new InputStreamReader(
                    input, "utf-8"));
            String result = reader_post.readLine();
            Log.e(TAG, "result : " + result);
            input.close();
            reader_post.close();
            System.gc();
            return (result==null)?"":result;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

}
