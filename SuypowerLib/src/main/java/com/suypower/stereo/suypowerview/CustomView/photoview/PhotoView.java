/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.suypower.stereo.suypowerview.CustomView.photoview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.widget.ImageView;

import com.suypower.stereo.suypowerview.Base.Init;
import com.suypower.stereo.suypowerview.File.FileDownload;
import com.suypower.stereo.suypowerview.Http.AjaxHttp;
import com.suypower.stereo.suypowerview.Server.StereoService;

import org.apache.http.HttpEntity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * A zoomable {@link ImageView}. See {@link PhotoViewAttacher} for most of the details on how the zooming
 * is accomplished
 */
public class PhotoView extends ImageView {

    private Bitmap bitmap;
    private PhotoViewAttacher attacher;

    public PhotoView(Context context) {
        this(context, null);
    }

    public PhotoView(Context context, AttributeSet attr) {
        this(context, attr, 0);
    }

    public PhotoView(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
        init();
    }

    @TargetApi(21)
    public PhotoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        attacher = new PhotoViewAttacher(this);
        //We always pose as a Matrix scale type, though we can change to another scale type
        //via the attacher
        super.setScaleType(ScaleType.MATRIX);
    }

    /**
     * Get the current {@link PhotoViewAttacher} for this view. Be wary of holding on to references
     * to this attacher, as it has a reference to this view, which, if a reference is held in the
     * wrong place, can cause memory leaks.
     *
     * @return the attacher.
     */
    public PhotoViewAttacher getAttacher() {
        return attacher;
    }

    @Override
    public ScaleType getScaleType() {
        return attacher.getScaleType();
    }

    @Override
    public Matrix getImageMatrix() {
        return attacher.getImageMatrix();
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
        attacher.setOnLongClickListener(l);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        attacher.setOnClickListener(l);
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        attacher.setScaleType(scaleType);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        // setImageBitmap calls through to this method
        attacher.update();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        attacher.update();
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        attacher.update();
    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        boolean changed = super.setFrame(l, t, r, b);
        if (changed) {
            attacher.update();
        }
        return changed;
    }


    public void downloadMediaid(final  String mediaid)
    {

        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = String.format("%1$smedia/download", StereoService.AppUrl);
                FileDownload fileDownload = new FileDownload(url, mediaid);
                fileDownload.imgamub = "";
                fileDownload.mediatype = ".jpg";
                Boolean r = fileDownload.streamDownLoadFile();
                if (r) {//获得突破


                    BitmapDrawable bitmapDrawable=new BitmapDrawable(Init.getContext().getResources(),
                            Init.getContext().getCacheDir()+"/" +mediaid + ".jpg");
                    Message message=handler.obtainMessage();
                    message.obj=bitmapDrawable;
                    handler.sendMessage(message);

                }
                System.gc();
            }
        }).start();

        return ;
    }


    public void downloadUrl(final  String url)
    {

        new Thread(new Runnable() {
            @Override
            public void run() {
                AjaxHttp ajaxHttp=new AjaxHttp();
                ajaxHttp.openRequest(url,AjaxHttp.REQ_METHOD_GET);
                if (ajaxHttp.sendRequest())
                {

                    HttpEntity httpEntity = ajaxHttp.getHttpResponse().getEntity();
                    if (httpEntity == null)
                        return ;
                    InputStream inStream;
                    ByteArrayOutputStream outStream;
                    byte[] bufferfile = null;
                    try {
                        inStream = httpEntity.getContent();
                        outStream = new ByteArrayOutputStream();
                        Log.i("下载文件大小inStream:", String.valueOf(inStream.available()));
                        int maxbuff = 1024 * 5000;
                        byte[] buffer = new byte[maxbuff];
                        int len = 0;

                        if (inStream == null) {
                            return ;
                        }
                        System.gc();
                        while ((len = inStream.read(buffer)) != -1) {
                            outStream.write(buffer, 0, len);
                        }
                        bufferfile = outStream.toByteArray();
                        bitmap=BitmapFactory.decodeByteArray(bufferfile, 0, bufferfile.length);
                        BitmapDrawable bitmapDrawable=new BitmapDrawable(getResources(),bitmap);
                        Message message=handler.obtainMessage();
                        message.obj=bitmapDrawable;
                        handler.sendMessage(message);

                        outStream.close();
                        inStream.close();
                        if (bufferfile == null)
                            return ;
                        if (bufferfile.length <=50) {
                            return ;

                        }
                        Log.i("下载文件大小:", String.valueOf(bufferfile.length));


                        String filename = url.substring(url.lastIndexOf("/")+1,url.length());
                        Log.e("下载的图片文件名称",filename);

                        File file = new File(Init.getContext().getCacheDir(),
                                filename );
                        if (file.exists())
                        {
                            file.delete();
                        }

                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                        fileOutputStream.write(bufferfile);
                        fileOutputStream.close();


                        return ;
                    } catch (Exception e) {
                        e.printStackTrace();
                        ajaxHttp.closeRequest();
                        return ;
                    }
                }
            }
        }).start();

        return ;
    }

    Handler handler=new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            setImageDrawable((BitmapDrawable)msg.obj);
            setScale(1);
        }
    };

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setRotationTo(float rotationDegree) {
        attacher.setRotationTo(rotationDegree);
    }

    public void setRotationBy(float rotationDegree) {
        attacher.setRotationBy(rotationDegree);
    }

    @Deprecated
    public boolean isZoomEnabled() {
        return attacher.isZoomEnabled();
    }

    public boolean isZoomable() {
        return attacher.isZoomable();
    }

    public void setZoomable(boolean zoomable) {
        attacher.setZoomable(zoomable);
    }

    public RectF getDisplayRect() {
        return attacher.getDisplayRect();
    }

    public void getDisplayMatrix(Matrix matrix) {
        attacher.getDisplayMatrix(matrix);
    }

    public boolean setDisplayMatrix(Matrix finalRectangle) {
        return attacher.setDisplayMatrix(finalRectangle);
    }

    public float getMinimumScale() {
        return attacher.getMinimumScale();
    }

    public float getMediumScale() {
        return attacher.getMediumScale();
    }

    public float getMaximumScale() {
        return attacher.getMaximumScale();
    }

    public float getScale() {
        return attacher.getScale();
    }

    public void setAllowParentInterceptOnEdge(boolean allow) {
        attacher.setAllowParentInterceptOnEdge(allow);
    }

    public void setMinimumScale(float minimumScale) {
        attacher.setMinimumScale(minimumScale);
    }

    public void setMediumScale(float mediumScale) {
        attacher.setMediumScale(mediumScale);
    }

    public void setMaximumScale(float maximumScale) {
        attacher.setMaximumScale(maximumScale);
    }

    public void setScaleLevels(float minimumScale, float mediumScale, float maximumScale) {
        attacher.setScaleLevels(minimumScale, mediumScale, maximumScale);
    }

    public void setOnMatrixChangeListener(OnMatrixChangedListener listener) {
        attacher.setOnMatrixChangeListener(listener);
    }

    public void setOnPhotoTapListener(OnPhotoTapListener listener) {
        attacher.setOnPhotoTapListener(listener);
    }

    public void setOnOutsidePhotoTapListener(OnOutsidePhotoTapListener listener) {
        attacher.setOnOutsidePhotoTapListener(listener);
    }

    public void setOnViewTapListener(OnViewTapListener listener) {
        attacher.setOnViewTapListener(listener);
    }

    public void setScale(float scale) {
        attacher.setScale(scale);
    }

    public void setScale(float scale, boolean animate) {
        attacher.setScale(scale, animate);
    }

    public void setScale(float scale, float focalX, float focalY, boolean animate) {
        attacher.setScale(scale, focalX, focalY, animate);
    }

    public void setZoomTransitionDuration(int milliseconds) {
        attacher.setZoomTransitionDuration(milliseconds);
    }

    public void setOnDoubleTapListener(GestureDetector.OnDoubleTapListener onDoubleTapListener) {
        attacher.setOnDoubleTapListener(onDoubleTapListener);
    }

    public void setOnScaleChangeListener(OnScaleChangedListener onScaleChangedListener) {
        attacher.setOnScaleChangeListener(onScaleChangedListener);
    }

    public void setOnSingleFlingListener(OnSingleFlingListener onSingleFlingListener) {
        attacher.setOnSingleFlingListener(onSingleFlingListener);
    }
}
