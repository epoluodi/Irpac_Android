package com.suypower.stereo.suypowerview.Menu;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.suypower.stereo.suypowerview.R;


/**
 * 菜单定义
 *
 * @author YXG
 */
public class Menu_Custom extends PopupWindow {

    private RelativeLayout relativeLayout;
    private LinearLayout linearLayout;
    private View menuview;
    private IMenu iMenu;
    private Context context;
    private Object viewGroup;
    private View Backview;

    public Menu_Custom(Context context, IMenu iMenu) {
        menuview = LayoutInflater.from(context).inflate(R.layout.menu_dialog, null);
        this.setContentView(menuview);
        linearLayout = (LinearLayout) menuview.findViewById(R.id.menu_container);
        this.context = context;
        this.iMenu = iMenu;
        Backview = new View(context);

    }


    public void setBackViewSuffer(Object object) {
        viewGroup = object;
        Log.i("类型" ,object.getClass().getSimpleName());
        if (object.getClass().getSimpleName().equals("FrameLayout"))
        {
            FrameLayout.LayoutParams layoutParams=new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            Backview.setLayoutParams(layoutParams);
            Backview.setBackgroundColor(context.getResources().getColor(R.color.blackTransparent5));

        }
        else if (object.getClass().getSimpleName().equals("RelativeLayout"))
        {
            RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            Backview.setLayoutParams(layoutParams);
            Backview.setBackgroundColor(context.getResources().getColor(R.color.blackTransparent5));

        }
    }



    @Override
    public void dismiss() {
        super.dismiss();
        if (viewGroup.getClass().getSimpleName().equals("FrameLayout"))
        {
            ((FrameLayout)viewGroup).removeView(Backview);
        }
        else if (viewGroup.getClass().getSimpleName().equals("RelativeLayout"))
        {
            ((RelativeLayout)viewGroup).removeView(Backview);
        }
    }

    /**
     * 添加菜单
     *
     * @param imgid
     * @param menuitmename
     * @param itemid       菜单id
     */
    public void addMenuItem(int imgid, String menuitmename, int itemid) {


        relativeLayout = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.menu_item, null);
        relativeLayout.setTag(itemid);
        ((TextView) relativeLayout.findViewById(R.id.item)).setText(menuitmename);
        ((ImageView) relativeLayout.findViewById(R.id.menu_img)).setBackground(context.getResources().getDrawable(imgid));
        relativeLayout.setOnClickListener(onClickListeneritem);
        linearLayout.addView(relativeLayout);
    }

    public void addMenuItem(int imgid, String menuitmename, int itemid, Boolean isTopOrBottom) {

        addMenuItem(imgid, menuitmename, itemid);
        View item = LayoutInflater.from(context).inflate(R.layout.menu_item, null);
        item.setTag(itemid);
        ((TextView) item.findViewById(R.id.item)).setText(menuitmename);
        ((ImageView) item.findViewById(R.id.menu_img)).setBackground(context.getResources().getDrawable(imgid));
        item.setOnClickListener(onClickListeneritem);
        linearLayout.addView(item);
    }

    public void clearMenu() {
        linearLayout.removeAllViews();
    }

    View.OnClickListener onClickListeneritem = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            iMenu.ClickMenu((Integer) view.getTag());
            dismiss();
        }
    };


    /**
     * 显示菜单
     *
     * @param v
     */
    public void ShowMenu(View v) {
        if (viewGroup.getClass().getSimpleName().equals("FrameLayout"))
        {
            ((FrameLayout)viewGroup).addView(Backview);
        }
        else if (viewGroup.getClass().getSimpleName().equals("RelativeLayout"))
        {
            ((RelativeLayout)viewGroup).addView(Backview);
        }
        this.setAnimationStyle(R.style.AnimationMenu);
        this.setBackgroundDrawable(new BitmapDrawable());

        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setOutsideTouchable(false);
        this.setFocusable(true);
        this.showAsDropDown(v, dip2px(context, -95), dip2px(context, 10));
//        this.showAsDropDown(v,-300,50,Gravity.LEFT);



    }


    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    public interface IMenu {
        void ClickMenu(int itemid);
    }

}
