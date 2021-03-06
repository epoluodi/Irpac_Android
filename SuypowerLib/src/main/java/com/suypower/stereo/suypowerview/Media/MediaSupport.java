package com.suypower.stereo.suypowerview.Media;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;


import com.suypower.stereo.suypowerview.Base.Init;
import com.suypower.stereo.suypowerview.R;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by Bingdor on 2016/6/1.
 */
public class MediaSupport {
    private MediaRecorder recorder;
    private String audioFilePath;
    private static MediaSupport mediaSupport;
    private Boolean isRecord = false;
    private Activity activity;
    private PopupWindow popupWindow;
    private View popview;
    private ImageView imageViewsingle;
    private TextView txt;
    private IRecordCallBack iRecordCallBack;
    private final int times = 60 * 1000;
    private long starttime;
    private String audioname;
    private Boolean isCancel = false;
    private static MediaPlayer mPlayer;
    public static boolean IsSpeak = true;
    public static Object object;
    public static final String AUDIO_CACHE_PATH = Init.getContext().getCacheDir().getAbsolutePath();


    public static MediaSupport getInstance() {
        if (mediaSupport == null) {
            mediaSupport = new MediaSupport(null, null);
        }
        return mediaSupport;
    }

    public MediaSupport(Context context, IRecordCallBack iRecordCallBack) {
        activity = (Activity) context;
        this.iRecordCallBack = iRecordCallBack;
    }

    public Boolean isRecording() {
        return this.isRecord;
    }


    /**
     * 开始录音
     *
     * @return
     */
    public void startAudioRecord(View view) throws Exception {
        if (!isRecord) {
            showPopwindows(view);

            this.recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioChannels(1);
            recorder.setAudioSamplingRate(4000);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            File cacheDir = new File(AUDIO_CACHE_PATH);
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            audioname = UUID.randomUUID().toString().replace("-", "");
            File audioFile = new File(AUDIO_CACHE_PATH + File.separator + audioname + ".aac");
            audioFile.createNewFile();
            this.audioFilePath = audioFile.getAbsolutePath();
            recorder.setOutputFile(this.audioFilePath);
            recorder.prepare();
            recorder.start();
            this.isRecord = true;
            starttime = System.currentTimeMillis();
            new Thread(runnable).start();

            return;
        }
        throw new Exception("录音已启动");
    }


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                while (isRecord) {
                    Message message = handler.obtainMessage();
                    if (System.currentTimeMillis() - starttime > times) {
                        message.what = 2;
                        handler.sendMessage(message);
                        return;
                    }

                    message.what = 1;

                    message.arg1 = recorder.getMaxAmplitude();
                    handler.sendMessage(message);
                    Thread.sleep(500);
                }
            } catch (Exception e) {
                e.printStackTrace();
                handler.sendEmptyMessage(0);
            }
        }
    };

    public MediaRecorder getMediaRecorder() {
        return this.recorder;
    }

    /**
     * 结束录音
     *
     * @return
     */
    public void stopAudioRecord() {
        if (isRecord) {
            isRecord = false;
            closePopWindos();
            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        Thread.sleep(600);
                        recorder.stop();
                        recorder.reset();   // You can reuse the object by going back to setAudioSource() step
                        recorder.release(); // Now the object cannot be reused
                        handler.sendEmptyMessage(3);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }


    /**
     * 获取录音文件
     *
     * @return
     */
    public File getAudioFile() {
        File file = new File(this.audioFilePath);
        return file.exists() ? file : null;
    }

    /**
     * 删除录音文件
     */
    public void destoryAudioFile() {
        File file = new File(this.audioFilePath);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 播放录音文件
     *
     * @param file
     */
    public static void playAudio(String file, final IPlayCallBack iPlayCallBack) {
        MediaSupport.setSpeakerphoneOn(IsSpeak);
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
        }
        try {
            mPlayer.stop();
            mPlayer.reset();
            mPlayer.setDataSource(file);
            mPlayer.prepare();
            mPlayer.start();

            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mPlayer.release();
                    mPlayer.setOnCompletionListener(null);
                    mPlayer = null;
                    iPlayCallBack.OnPlayFinish();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            mPlayer.release();
            mPlayer.setOnCompletionListener(null);
            mPlayer = null;
            iPlayCallBack.OnPlayFinish();
        }
    }

    public static void stopAudio() {
        if (mPlayer == null) {
            return;
        }
        mPlayer.setOnCompletionListener(null);
        mPlayer.stop();
        mPlayer.release();
        mPlayer = null;
    }


    /**
     * 获得录音时长
     *
     * @param file
     * @return
     */
    public static int getMediaDuration(String file) {
        MediaPlayer _mPlayer = new MediaPlayer();
        try {

            _mPlayer.setDataSource(file);
            _mPlayer.prepare();
            int i = _mPlayer.getDuration();
            _mPlayer.release();
            _mPlayer = null;
            return i / 1000;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 设置扬声器打开或者关闭
     *
     * @param on
     */
    public static void setSpeakerphoneOn(boolean on) {
        AudioManager audioManager = (AudioManager)
                Init.getContext().getSystemService(Context.AUDIO_SERVICE);
        IsSpeak = on;
        if (!on) {
            //把声音设定成Earpiece（听筒）出来，设定为正在通话中
            audioManager.setMode(AudioManager.MODE_IN_CALL);
        } else
            audioManager.setMode(AudioManager.MODE_NORMAL);
        audioManager.setSpeakerphoneOn(on);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 1:
                    if (imageViewsingle == null)
                        return;
                    Log.i("分贝", String.valueOf(msg.arg1));
                    int voiceValue = msg.arg1 / 10;
                    if (voiceValue < 600) {
                        imageViewsingle.setImageResource(R.drawable.v1);
                    } else if (voiceValue > 600 && voiceValue < 1200) {
                        imageViewsingle.setImageResource(R.drawable.v2);
                    } else if (voiceValue > 1200 && voiceValue < 2000.0) {
                        imageViewsingle.setImageResource(R.drawable.v3);
                    } else if (voiceValue > 2000.0 && voiceValue < 2600) {
                        imageViewsingle.setImageResource(R.drawable.v4);
                    } else if (voiceValue > 2600 && voiceValue < 3200) {
                        imageViewsingle.setImageResource(R.drawable.v5);
                    } else if (voiceValue > 3200 && voiceValue < 3800) {
                        imageViewsingle.setImageResource(R.drawable.v6);
                    } else if (voiceValue > 4000) {
                        imageViewsingle.setImageResource(R.drawable.v7);
                    }
                    break;
                case 2:

                    isRecord = false;
                    isCancel = false;
                    stopAudioRecord();

                    break;
                case 3:
                    if (isCancel) {
                        File file = getAudioFile();
                        file.delete();
                        iRecordCallBack.OnRecordCancel();
                    } else {

                        int t = (int) (System.currentTimeMillis() - starttime);
                        if (t < 1200) {
                            File file = getAudioFile();
                            file.delete();
                            iRecordCallBack.OnRecordCancel();
                            return;
                        }

                        t = MediaSupport.getMediaDuration(AUDIO_CACHE_PATH + File.separator + audioname + ".aac");

                        iRecordCallBack.OnRecordFinish(audioname, t );
                    }
                    break;

            }


        }
    };

    /**
     * 显示popwindows
     */
    public void showPopwindows(View view) {
        System.gc();
        popview = activity.getLayoutInflater().inflate(R.layout.popwindowforrecord, null);
        imageViewsingle = (ImageView) popview.findViewById(R.id.fb);
        txt = (TextView) popview.findViewById(R.id.txt);
        txt.setText("向上滑动，取消录音");
        popupWindow = new PopupWindow();
        popupWindow.setContentView(popview);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(false);
        popupWindow.setAnimationStyle(R.style.Animationpopwindows);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);


    }

    public void setOutSide() {
        txt.setText("取消发送");
        txt.setTextColor(activity.getResources().getColor(R.color.red));
        isCancel = true;
    }

    public void setInSide() {
        txt.setText("向上滑动，取消录音");
        txt.setTextColor(activity.getResources().getColor(R.color.white));
        isCancel = false;
    }

    /**
     * 关闭pop
     */
    public void closePopWindos() {
        if (popupWindow != null) {
            popupWindow.dismiss();
            popupWindow = null;
            imageViewsingle = null;


        }
    }


    /**
     * 录音回调接口
     */
    public interface IRecordCallBack {
        void OnRecordFinish(String recordname, int times);

        void OnRecordCancel();

    }

    /**
     * 播放声音回调接口
     */
    public interface IPlayCallBack {
        void OnPlayFinish();
    }

}
