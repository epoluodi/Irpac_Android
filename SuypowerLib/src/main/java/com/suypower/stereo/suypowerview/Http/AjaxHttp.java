package com.suypower.stereo.suypowerview.Http;


import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;

import com.suypower.stereo.suypowerview.Base.Init;
import com.suypower.stereo.suypowerview.Common.Common;
import com.suypower.stereo.suypowerview.Common.ImageCore;
import com.suypower.stereo.suypowerview.R;


import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Ajax Http 调用 类
 *
 * @author YXG
 */
public class AjaxHttp {
    /**
     * get请求
     */
    public final static int REQ_METHOD_GET = 0;
    /**
     * post请求
     */
    public final static int REQ_METHOD_POST = 1;


    private HttpGet m_httpGet = null;
    private HttpPost m_httpPost = null;
    private HttpResponse m_httpResp = null;

    List<NameValuePair> pairList;

    private static final String USER_AGENT = "Android SUYPOWER";
    /**
     * http请求对象
     */
    private HttpClient m_httpClient;


    public AjaxHttp() {
        initHttp();

    }

    /**
     * 初始化http
     *
     * @return
     */
    public HttpClient initHttp() {
        try {

//            AssetManager am = Init.getContext().getAssets();
//            InputStream serverins = am.open("tomcat.cer");
//            InputStream clinetins = am.open("mykey.p12");

            InputStream serverins = Init.getContext().getResources().openRawResource(R.raw.server);
            InputStream clinetins = Init.getContext().getResources().openRawResource(R.raw.client);

//            //服务器
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Certificate ca;
            try{
                ca = cf.generateCertificate(serverins);
            }finally {
                serverins.close();
            }
//            String keyStoreType = KeyStore.getDefaultType();
            KeyStore trustStore = KeyStore.getInstance("PKCS12");
            trustStore.load(null,null);
//            trustStore.load(serverins, "suypower".toCharArray());
            trustStore.setCertificateEntry("ca",ca);


            //客户端
//            cf = CertificateFactory.getInstance("X.509");
//            try{
//                ca = cf.generateCertificate(clinetins);
//            }finally {
//                clinetins.close();
//            }
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(clinetins, "suypower".toCharArray());
//            keyStore.load(null,null);
//            keyStore.setCertificateEntry("clinet",ca);
//
            SSLSocketFactory sf = new SSLSocketFactory(keyStore,"suypower",trustStore);
//            SSLSocketFactory sf = new SSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            // 设置一些基本参数
            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
            HttpProtocolParams.setUseExpectContinue(params, false);
            HttpProtocolParams.setUserAgent(params, USER_AGENT);
            // 超时设置
            // ConnManagerParams.setTimeout(params, 1000);
            HttpConnectionParams.setConnectionTimeout(params, 10000);// 连接超时(单位：毫秒)
            // HttpConnectionParams.setSoTimeout(params, 30*1000); //
            // 读取超时(单位：毫秒)

//            SchemeRegistry schReg = new SchemeRegistry();
//            schReg.register(new Scheme("http", PlainSocketFactory
//                    .getSocketFactory(), 80));
//            schReg.register(new Scheme("https", sf, 443));

            // 设置我们的HttpClient支持HTTP和HTTPS两种模式
            SchemeRegistry schReg = new SchemeRegistry();
            schReg.register(new Scheme("http",
                    PlainSocketFactory.getSocketFactory(), 80));
            schReg.register(new Scheme("https", sf, 443));
            // 使用线程安全的连接管理来创建HttpClient
            ClientConnectionManager connectionMgr = new ThreadSafeClientConnManager(
                    params, schReg);


            m_httpClient = new DefaultHttpClient(params);


            return m_httpClient;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }

    public void closeRequest() {
        if (m_httpGet != null)
            m_httpGet.abort();

        if (m_httpPost != null)
            m_httpPost.abort();
        m_httpResp = null;
        m_httpGet = null;
        m_httpPost = null;
    }


    /**
     * 打开http
     *
     * @param url
     * @param nReqMethod
     * @return
     */
    public boolean openRequest(String url, int nReqMethod) {
        closeRequest();
        if (nReqMethod == REQ_METHOD_GET) {
            m_httpGet = new HttpGet(url);
            m_httpGet.addHeader("token", Common.Token);
            m_httpGet.addHeader("deviceID", Common.DeviceID);
            m_httpGet.addHeader("deviceType", "2");

        } else if (nReqMethod == REQ_METHOD_POST) {
            m_httpPost = new HttpPost(url);
            m_httpPost.addHeader("deviceID", Common.DeviceID);
            m_httpPost.addHeader("token", Common.Token);
            m_httpPost.addHeader("deviceType", "2");
            pairList = new ArrayList<>();

        } else {
            return false;
        }

        return true;
    }


    /**
     * 打开一个http
     *
     * @param url
     * @param nReqMethod
     * @param Token
     * @return
     */
    public boolean openRequest(String url, int nReqMethod, String Token) {
        closeRequest();


        if (nReqMethod == REQ_METHOD_GET) {
            m_httpGet = new HttpGet(url);
            m_httpGet.addHeader("token", Token);
            m_httpGet.addHeader("deviceID", Common.DeviceID);
            m_httpGet.addHeader("deviceType", "02");

        } else if (nReqMethod == REQ_METHOD_POST) {
            m_httpPost = new HttpPost(url);
            m_httpPost.addHeader("deviceID", Common.DeviceID);
            m_httpPost.addHeader("token", Token);
            m_httpPost.addHeader("deviceType", "02");
            pairList = new ArrayList<>();

        } else {
            return false;
        }

        return true;
    }

    /**
     * 添加头
     *
     * @param name
     * @param value
     */
    public void addHeader(String name, String value) {
        if (m_httpGet != null) {
            m_httpGet.addHeader(name, value);
        } else if (m_httpPost != null) {
            m_httpPost.addHeader(name, value);
        }
    }

    public void setEntity(UrlEncodedFormEntity entity) {
        if (m_httpPost != null) {
            m_httpPost.setEntity(entity);
        }
    }

    public void setPostValuesForKey(String Key, String value) {
        BasicNameValuePair basicNameValuePair = new BasicNameValuePair(Key, value);
        pairList.add(basicNameValuePair);


    }

    public UrlEncodedFormEntity getPostData() {
        try {
            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(pairList, HTTP.UTF_8);
            return urlEncodedFormEntity;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Boolean sendRequest() {
        if (null == m_httpClient)
            return false;

        try {
            if (m_httpGet != null) {

                m_httpResp = m_httpClient.execute(m_httpGet);

                return true;
            } else if (m_httpPost != null) {
                m_httpResp = m_httpClient.execute(m_httpPost);
                return true;
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
//			Logger.e(TAG, e);
            return false;
        } catch (IOException e) {
            e.printStackTrace();
//			Logger.e(TAG, e);
            return false;

        }
        return false;
    }

    public int getRespCode() {
        if (m_httpResp != null)
            return m_httpResp.getStatusLine().getStatusCode();
        else
            return 0;
    }

    public Header[] getRespHeader() {
        if (m_httpResp != null)
            return m_httpResp.getAllHeaders();
        else
            return null;
    }

    public List<Cookie> getCookies() {
        if (m_httpClient != null)
            return ((DefaultHttpClient) m_httpClient).getCookieStore().getCookies();
        else
            return null;
    }

    public HttpResponse getHttpResponse() {
        try {
            return m_httpResp;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public InputStream getRespBodyDataInputStream() {
        try {
            if (m_httpResp != null) {
                InputStream is = m_httpResp.getEntity().getContent();

                return is;
            }
        } catch (IllegalStateException e) {

        } catch (IOException e) {

        }

        return null;
    }


    /**
     * 读取服务器返回数据
     *
     * @return
     */
    public byte[] getRespBodyData() {
        try {
            if (m_httpResp != null) {
                InputStream is = m_httpResp.getEntity().getContent();
                byte[] bytData = InputStreamToByte(is);
                is.close();
                return bytData;
            }
        } catch (IllegalStateException e) {

        } catch (IOException e) {

        }

        return null;
    }


    public HttpClient getHttpClient() {
        return m_httpClient;
    }

    private byte[] InputStreamToByte(InputStream is) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int ch;
        byte[] buf = new byte[1024 * 4];
        byte data[] = null;

        try {
            while ((ch = is.read(buf)) != -1) {
                out.write(buf, 0, ch);
            }
            data = out.toByteArray();
            out.close();
        } catch (IOException e) {

        } finally {

        }

        return data;
    }


    /**
     * post 请求
     *
     * @param jsonArray
     * @return
     */
    public UrlEncodedFormEntity postUrlString(JSONArray jsonArray) {

        JSONObject jsonObject;
        UrlEncodedFormEntity urlEncodedFormEntity;
        BasicNameValuePair basicNameValuePair;
        List<NameValuePair> pairList;
        try {

            jsonObject = jsonArray.getJSONObject(0);
            Iterator<String> stringIterator = jsonObject.keys();
            String key;
            pairList = new ArrayList<NameValuePair>();
            while (stringIterator.hasNext()) {
                key = stringIterator.next();
                basicNameValuePair = new BasicNameValuePair(key,
                        jsonObject.getString(key));
                pairList.add(basicNameValuePair);
            }


            urlEncodedFormEntity = new UrlEncodedFormEntity(pairList, HTTP.UTF_8);
            return urlEncodedFormEntity;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 传输流
     *
     * @return
     */
    public static String OutputStream(String strurl, String json) {


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




        String CONTENT_TYPE = "application/octet-stream";   //内容类型

        try {
            URL url = new URL(strurl);
//            HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(100000);
            conn.setDoInput(true);  //允许输入流
            conn.setDoOutput(true); //允许输出流
            conn.setUseCaches(false);  //不允许使用缓存
            conn.setRequestMethod("POST");  //请求方式
//            conn.setHostnameVerifier(hostnameVerifier);
//            conn.setSSLSocketFactory(context.getSocketFactory());


            conn.setRequestProperty("Charset", "utf-8");  //设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE);
            conn.setRequestProperty("token", Common.Token);
            conn.setRequestProperty("deviceID", Common.DeviceID);
            conn.setRequestProperty("deviceType", "2");

            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            dos.write(json.getBytes());
            dos.flush();
            dos.close();

            int res = conn.getResponseCode();
            Log.e("http stream", "response code:" + res);
            Log.e("http stream", "request success");
            InputStream input = conn.getInputStream();
            BufferedReader reader_post = new BufferedReader(new InputStreamReader(
                    input, "utf-8"));
            String result = reader_post.readLine();
            Log.e("http stream", "result : " + result);
            input.close();
            reader_post.close();
            System.gc();
            return (result == null) ? "" : result;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }







}
