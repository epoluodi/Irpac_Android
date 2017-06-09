package com.suypower.stereo.suypowerview.Common;

import android.content.Context;

import com.suypower.stereo.suypowerview.Base.Init;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by Stereo on 16/7/20.
 */
public class FileCommon {


    /**
     * 复制raw资源文件
     * @param Raw
     * @param filename
     */
    public static void CopyRaw(int Raw, String filename) {
        InputStream inputStream;
        try {
            inputStream = Init.getContext().getResources().openRawResource(Raw);
            byte[] bytebuff = new byte[inputStream.available()];
            inputStream.read(bytebuff);
            File file = new File(Init.getContext().getFilesDir() , filename );
            if (file.exists())
                file.delete();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(bytebuff);
            fileOutputStream.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }


    /**
     * 文件拷贝
     */
    public static void CopyFIle(String srcFile,String destFile) {
        InputStream inputStream;
        try {
            File srcfile=new File(srcFile);

            inputStream = new FileInputStream(srcfile);
            byte[] bytebuff = new byte[inputStream.available()];
            inputStream.read(bytebuff);
            File file = new File(Init.getContext().getCacheDir(),  destFile );
            if (file.exists())
                file.delete();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(bytebuff);
            srcfile.delete();
            fileOutputStream.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }


    /**
     *  删除文件
     * @param filepath
     */
    public static void DeleteFile(String filepath)
    {
        File file=new File(filepath);
        file.delete();
    }

}
