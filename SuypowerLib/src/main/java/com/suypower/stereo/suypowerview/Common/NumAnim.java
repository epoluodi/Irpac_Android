package com.suypower.stereo.suypowerview.Common;

/**
 * Created by Bingdor on 2016/8/4.
 */
import java.util.LinkedList;
import java.util.Random;
import android.widget.TextView;

public class NumAnim {
    //每秒刷新多少次
    private static final int COUNTPERS = 100;

    public static void startAnim(TextView textV, int num) {
        startAnim(textV, num, 500);
    }
    public static void startAnim(TextView textV, int num, long time) {
        if (num == 0) {
            textV.setText(String.valueOf(num));
            return;
        }
        Float[] nums = splitnum(num, (int)((time/1000f)*COUNTPERS));
        Counter counter = new Counter(textV, nums, time);
        textV.removeCallbacks(counter);
        textV.post(counter);
    }

    private static Float[] splitnum(float num, int count) {
        Random random = new Random();
        float numtemp = num;
        float sum = 0;
        LinkedList<Float> nums = new LinkedList<Float>();
        nums.add(0f);
        while (true) {
            float nextFloat = (random.nextFloat()*num*2f)/(float)count;
            if (numtemp - nextFloat >= 0) {
                sum += nextFloat;
                nums.add(sum);
                numtemp -= nextFloat;
            } else {
                nums.add(num);
                return nums.toArray(new Float[0]);
            }
        }
    }

    static class Counter implements Runnable {

        private final TextView view;
        private Float[] nums;
        private long pertime;

        private int i = 0;

        Counter(TextView view,Float[] nums,long time) {
            this.view = view;
            this.nums = nums;
            this.pertime = time/nums.length;
        }

        @Override
        public void run() {
            if (i>nums.length-1) {
                view.removeCallbacks(Counter.this);
                return;
            }
            view.setText(String.valueOf(nums[i++].intValue()));
            view.removeCallbacks(Counter.this);
            view.postDelayed(Counter.this, pertime);
        }
    }
}