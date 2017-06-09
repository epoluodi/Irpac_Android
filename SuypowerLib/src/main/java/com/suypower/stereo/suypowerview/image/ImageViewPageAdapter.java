package com.suypower.stereo.suypowerview.image;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bingdor on 2017/2/23.
 */

public class ImageViewPageAdapter extends PagerAdapter {

    private List<RelativeLayout> viewList;

    @Override
    public int getCount() {

        return 0;
    }


    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {

        return arg0 == arg1;
    }


    @Override
    public int getItemPosition(Object object) {

        return super.getItemPosition(object);
    }

    @Override
    public CharSequence getPageTitle(int position) {

        return super.getPageTitle(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        viewList = new ArrayList<RelativeLayout>();
        return super.instantiateItem(container, position);
    }
}
