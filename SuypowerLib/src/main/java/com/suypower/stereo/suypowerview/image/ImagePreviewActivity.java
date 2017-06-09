package com.suypower.stereo.suypowerview.image;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.suypower.stereo.suypowerview.Common.Common;
import com.suypower.stereo.suypowerview.CustomView.ImageViewEx;
import com.suypower.stereo.suypowerview.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bingdor on 2017/2/21.
 */

public class ImagePreviewActivity extends Activity {

    private Button saveBtn;
    private Button imgTip;
    private ImageViewEx imageView;
    private RelativeLayout relativeLayout;
    private RelativeLayout imageBox;
    private View imgBgView;
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //隐藏状态栏
        //定义全屏参数
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //获得当前窗体对象
        Window window = this.getWindow();
        window.setBackgroundDrawable(new BitmapDrawable());
        //设置当前窗体为全屏显示
        window.setFlags(flag, flag);
        setContentView(R.layout.img_preview_layout);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Window window = this.getWindow();
        imgTip = (Button) findViewById(R.id.img_tip);
        saveBtn = (Button) findViewById(R.id.save_btn);
        relativeLayout = (RelativeLayout) findViewById(R.id.preview_view);
        imageBox = (RelativeLayout) findViewById(R.id.img_box);
        imgBgView = (View) findViewById(R.id.img_bg);
        viewPager = (ViewPager) findViewById(R.id.photo_viewpager);
        viewPager.setAdapter(pagerAdapter);

        imageView = new ImageViewEx(this);

        String data = getIntent().getStringExtra("data");
        Log.i(this.getClass().getName(), data);
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(data);
            String current = jsonObject.getString("current");
            String posX = jsonObject.getString("posX");
            String posY = jsonObject.getString("posY");
            String width = jsonObject.getString("width");
            String height = jsonObject.getString("height");
            Object images = jsonObject.get("images");
            JSONArray arr = (JSONArray) images;

            imageView.setSource(arr.getString(Integer.parseInt(current)));

            imgTip.setText((Integer.parseInt(current) + 1) + "/" + arr.length());
            final int imgWidth = Common.dip2px(Integer.parseInt(width));
            final int imgHeight = Common.dip2px(Integer.parseInt(height));
//            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(imgWidth, imgHeight);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(imgWidth, imgHeight);
            Float left = Float.parseFloat(posX);
            Float top = Float.parseFloat(posY);

            final int screenWidth = window.getWindowManager().getDefaultDisplay().getWidth();
            final int screenHeight = window.getWindowManager().getDefaultDisplay().getHeight();

            imageView.setLayoutParams(layoutParams);
            imageView.setX(Common.dip2px(left));
            imageView.setY(Common.dip2px(top + 60));
            imageBox.addView(imageView, 0);
            final float orginX = imageView.getX();
            final float orginY = imageView.getY();

            Map<String, Float> map = new HashMap<String, Float>();
            map.put("orginX", orginX);
            map.put("orginY", orginY);
            imageView.setTag(map);

            ObjectAnimator.ofFloat(imageView, "translationX", orginX, (screenWidth - imgWidth) / 2).setDuration(400).start();
            ObjectAnimator.ofFloat(imageView, "translationY", orginY, (screenHeight - imgHeight) / 2).setDuration(400).start();
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(imgBgView, "alpha", 0, 1).setDuration(400);
            objectAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                            Common.dip2px(imageView.getActualWidth()), Common.dip2px(imageView.getActualHeight()));
                    imageView.setLayoutParams(layoutParams);

                    /*ScaleAnimation scaleAnimation = new ScaleAnimation(orginX, 0, orginY, 0, Animation.RELATIVE_TO_SELF, 2f, Animation.RELATIVE_TO_SELF, 2f);
                    scaleAnimation.setFillAfter(true);
                    scaleAnimation.setStartOffset(400);
                    imageView.startAnimation(scaleAnimation);*/
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            objectAnimator.start();

//            ScaleAnimation scaleAnimation = new ScaleAnimation(orginX,0,orginY,0,Animation.RELATIVE_TO_SELF,2f,Animation.RELATIVE_TO_SELF,2f);
//            scaleAnimation.setFillAfter(true);
//            scaleAnimation.setStartOffset(400);
//            imageView.startAnimation(scaleAnimation);

            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finishPreview(orginX, orginY);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void finishPreview(float orginX, float orginY) {
        imgTip.setVisibility(View.GONE);
        saveBtn.setVisibility(View.GONE);
        ObjectAnimator.ofFloat(imgBgView, "alpha", 1, 0).setDuration(300).start();
        ObjectAnimator.ofFloat(imageView, "translationX", imageView.getX(), orginX).setDuration(400).start();
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(imageView, "translationY", imageView.getY(), orginY).setDuration(400);
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                relativeLayout.setAlpha(0);
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        objectAnimator.start();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            Map<String, Float> map = (Map<String, Float>) imageView.getTag();
            finishPreview(map.get("orginX"), map.get("orginY"));
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
}
