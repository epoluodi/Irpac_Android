package com.suypower.stereo.suypowerview.Share;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.suypower.stereo.suypowerview.R;
import com.suypower.stereo.suypowerview.WXCore.WXCore;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;

import org.json.JSONObject;

/**
 * Created by Stereo on 2017/4/28.
 */

public class ShareView extends Dialog {

    private View shareview;
    private LinearLayout item1;
    private LinearLayout item2;
    private LinearLayout item3;
    private LinearLayout item4;

    private RelativeLayout selectitem1;
    private RelativeLayout selectitem2;
    private RelativeLayout selectitem3;
    private RelativeLayout selectitem4;

    private Button btn_cancel;
    private String title, url, desc, thum;
    private Context context;

    public ShareView(Context context, JSONObject jsonObject) {

        super(context, R.style.dialog);
        this.context=context;
        setCanceledOnTouchOutside(true);    //设置点击Dialog外部任意区域不能关闭Dialog
        setCancelable(true);        // 设置为false，按返回键不能退出

        shareview = LayoutInflater.from(context).inflate(R.layout.share_view, null);
        setContentView(shareview);
        item1 = (LinearLayout) shareview.findViewById(R.id.item_wxfriend);
        item2 = (LinearLayout) shareview.findViewById(R.id.item_wxgroup);
        item3 = (LinearLayout) shareview.findViewById(R.id.item_copy);
        item4 = (LinearLayout) shareview.findViewById(R.id.item_sendsms);

        btn_cancel = (Button)shareview.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        selectitem1 = (RelativeLayout) item1.findViewById(R.id.selectitem);
        selectitem1.setBackground(context.getResources().getDrawable(R.drawable.wxfriend_btn_selector));
        selectitem1.setOnClickListener(onClickListenerwxfriend);
        selectitem2 = (RelativeLayout) item2.findViewById(R.id.selectitem);
        selectitem2.setOnClickListener(onClickListenerwxgroup);
        selectitem2.setBackground(context.getResources().getDrawable(R.drawable.wxgroup_btn_selector));
        selectitem3 = (RelativeLayout) item3.findViewById(R.id.selectitem);
        selectitem3.setOnClickListener(onClickListenercopy);
        selectitem3.setBackground(context.getResources().getDrawable(R.drawable.copy_btn_selector));
        selectitem4 = (RelativeLayout) item4.findViewById(R.id.selectitem);
        selectitem4.setOnClickListener(onClickListenersendsms);
        selectitem4.setBackground(context.getResources().getDrawable(R.drawable.sms_btn_selector));

//        ((ImageView) item1.findViewById(R.id.item_img)).
//                setBackground(context.getResources().getDrawable(R.drawable.wxfriend));
//        ((ImageView) item2.findViewById(R.id.item_img)).
//                setBackground(context.getResources().getDrawable(R.drawable.wxgroup));
//        ((ImageView) item3.findViewById(R.id.item_img)).
//                setBackground(context.getResources().getDrawable(R.drawable.sharecopy));
//        ((ImageView) item4.findViewById(R.id.item_img)).
//                setBackground(context.getResources().getDrawable(R.drawable.sms));

        ((TextView) item1.findViewById(R.id.item_title)).setText("微信朋友");
        ((TextView) item2.findViewById(R.id.item_title)).setText("朋友圈");
        ((TextView) item3.findViewById(R.id.item_title)).setText("复制");
        ((TextView) item4.findViewById(R.id.item_title)).setText("发送短信");
        try {
            url = jsonObject.getString("url");
            title = jsonObject.getString("title");
            desc = jsonObject.getString("desc");
            thum = jsonObject.getString("thum");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void show() {

        Window win = this.getWindow();
        WindowManager.LayoutParams params = win.getAttributes();
        WindowManager wm =  (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        int cxScreen =wm.getDefaultDisplay().getWidth();
        int cyScreen =wm.getDefaultDisplay().getHeight();
        win.setWindowAnimations(R.style.Animationbottomwindows);
        params.x = 0;
        params.y = cyScreen - params.height;
        params.width = cxScreen;
        super.show();


    }

    /**
     * 发送微信朋友
     */
    private View.OnClickListener onClickListenerwxfriend = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            dismiss();
            WXCore.sendWebLink(url,title,desc,null, SendMessageToWX.Req.WXSceneSession);
        }
    };

    /**
     * 朋友圈
     */
    private View.OnClickListener onClickListenerwxgroup = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
            WXCore.sendWebLink(url,title,desc,null, SendMessageToWX.Req.WXSceneTimeline);
        }
    };


    /**
     * 拷贝信息
     */
    private View.OnClickListener onClickListenercopy = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            ClipboardManager clipboard = (ClipboardManager)
                    context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("text",String.format("%1$s\n%2$s", title,url));
            clipboard.setPrimaryClip(clip);
            dismiss();
        }
    };


    /**
     * 发送短信
     */
    private View.OnClickListener onClickListenersendsms = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"));
            intent.putExtra("sms_body", String.format("%1$s\n%2$s", title,url));
            context.startActivity(intent);
            dismiss();
        }
    };
}
