package com.suypower.stereo.suypowerview.CustomView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.suypower.stereo.suypowerview.R;


/**
 * Created by Stereo on 2017/1/18.
 */

public class BackViewEmpty {


    RelativeLayout relativeLayout;
    TextView textView ;

    public BackViewEmpty(Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        relativeLayout = (RelativeLayout) layoutInflater.inflate(R.layout.backviewempty, null);
        RelativeLayout.LayoutParams layoutParams= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        relativeLayout.setLayoutParams(layoutParams);
        textView = (TextView)relativeLayout.findViewById(R.id.info);


    }


    /**
     * 获得背景view
     * @return
     */
    public RelativeLayout getRelativeLayout() {
        return relativeLayout;
    }
}
