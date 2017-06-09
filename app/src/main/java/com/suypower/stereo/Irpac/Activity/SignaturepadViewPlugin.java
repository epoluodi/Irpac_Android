package com.suypower.stereo.Irpac.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;


import com.suypower.stereo.Irpac.R;
import com.suypower.stereo.Irpac.System.signaturepad.views.SignaturePad;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

/**
 * 签名版view
 * @author YXG
 */
public class SignaturepadViewPlugin extends Activity {

    public static String TAG ="SignaturepadViewPlugin";
    public static int SIGNATUREPADRESULTREQUEST = 20;
    Button buttonclear;
    Button buttonok;
    SignaturePad signaturePad;

    String uuid;
    Thread thread;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.signaturepad_activity);

        signaturePad = (SignaturePad)findViewById(R.id.signpad);
        signaturePad.setMinWidth(12);
        signaturePad.setMaxWidth(18);

        buttonclear = (Button)findViewById(R.id.btncanecl);
        buttonclear.setOnClickListener(onClickListenerclear);
        buttonok = (Button)findViewById(R.id.btnok);
        buttonok.setOnClickListener(onClickListenerok);

        uuid = UUID.randomUUID().toString();

    }

    View.OnClickListener onClickListenerclear = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            signaturePad.clear();
        }
    };

    View.OnClickListener onClickListenerok = new View.OnClickListener() {
        @Override
        public void onClick(View view) {


            thread = new Thread(runnable);
            thread.start();
        }
    };


    Handler handler=new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            switch (msg.what)
            {
                case 1:

                    bundle.putString("uuid", uuid );
                    intent.putExtras(bundle);
                    setResult(1, intent);
                    finish();
                    break;
                case 0:

                    setResult(0);
                    finish();
                    break;
            }

        }
    };

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try
            {
                Bitmap signatureBitmap = signaturePad.getSignatureBitmap();
                String pngname = String.format("%1$s.jpg",uuid);
                File file = new File(getCacheDir(),pngname);
                saveBitmapToJPG(signatureBitmap, file);
                System.gc();

                handler.sendEmptyMessage(1);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                handler.sendEmptyMessage(0);
            }
        }
    };




    //存png
    public void saveBitmapToJPG(Bitmap bitmap, File photo) throws IOException {
//        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(newBitmap);
//        canvas.drawColor(Color.TRANSPARENT);
//        canvas.drawBitmap(bitmap, 0, 0, null);
        OutputStream stream = new FileOutputStream(photo);
        bitmap=zoomImg(bitmap,754/4,306/4);
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
        stream.flush();
        stream.close();

    }


    public static Bitmap zoomImg(Bitmap bm, int newWidth ,int newHeight){
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }


}