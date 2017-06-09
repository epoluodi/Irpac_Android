package com.suypower.stereo.Irpac;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.suypower.stereo.Irpac.System.APP;
import com.suypower.stereo.Irpac.System.AppContextLauncher;
import com.suypower.stereo.suypowerview.Base.LibConfig;
import com.suypower.stereo.suypowerview.PopWindowInfo.CustomPopWindowPlugin;

import org.apache.cordova.App;

public class SplashActivity extends AppCompatActivity {

    public static final String ACTION_ADD_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";


    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        addShortcut("廉洁从医");
//        imageView = (ImageView) findViewById(R.id.backimg);
//
//
//        String imgid = LibConfig.getKeyShareVarForString("splashimg");
//        if (!imgid.equals("null")) {
//
//            BitmapDrawable bitmapDrawable = (BitmapDrawable) BitmapDrawable.createFromPath(getCacheDir() + "/" + imgid + ".jpg");
//            if (bitmapDrawable == null)
//                imageView.setBackground(getResources().getDrawable(R.mipmap.splashold));
//            else
//                imageView.setBackground(bitmapDrawable);
//        } else
//            imageView.setBackground(getResources().getDrawable(R.mipmap.splashold));



        AppContextLauncher appContextLauncher = new AppContextLauncher(this, new AppContextLauncher.AppLaucherCallback() {
            @Override
            public void onInitOver(Intent intent) {
                finish();
                startActivity(intent);
//                overridePendingTransition(R.anim.alpha_enter,R.anim.alpha_exit_2);
            }

            @Override
            public void StartUpdateApp() {
//                CustomPopWindowPlugin.ShowPopWindow(imageView,getLayoutInflater(),"更新APP");
            }
        });
    }


    private void addShortcut(String name) {
        Intent addShortcutIntent = new Intent(ACTION_ADD_SHORTCUT);

        // 不允许重复创建
        addShortcutIntent.putExtra("duplicate", false);// 经测试不是根据快捷方式的名字判断重复的
        // 应该是根据快链的Intent来判断是否重复的,即Intent.EXTRA_SHORTCUT_INTENT字段的value
        // 但是名称不同时，虽然有的手机系统会显示Toast提示重复，仍然会建立快链
        // 屏幕上没有空间时会提示
        // 注意：重复创建的行为MIUI和三星手机上不太一样，小米上似乎不能重复创建快捷方式

        // 名字
        addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);

        // 图标
        addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(SplashActivity.this,
                        R.mipmap.logo512));

        // 设置关联程序
        Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
        launcherIntent.setClass(SplashActivity.this, SplashActivity.class);
        launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        addShortcutIntent
                .putExtra(Intent.EXTRA_SHORTCUT_INTENT, launcherIntent);

        // 发送广播
        sendBroadcast(addShortcutIntent);
    }


}
