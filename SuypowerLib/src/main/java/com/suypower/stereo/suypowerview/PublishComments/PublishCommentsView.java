package com.suypower.stereo.suypowerview.PublishComments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.suypower.stereo.suypowerview.Base.Init;
import com.suypower.stereo.suypowerview.CustomLayout.LinearLayoutYXG;
import com.suypower.stereo.suypowerview.R;

/**
 * Created by Stereo on 2016/9/27.
 */

public class PublishCommentsView {


    private LinearLayout linearLayout;
    private LinearLayout btncomments;
    private ImageView btnreturn, btnfav, btnlike;
    private LinearLayoutYXG linearLayoutYXG;
    private IComments _iComments;
    private LinearLayout linearlayoutcontent;
    private ImageView btncancel,btnok;
    private TextView contenttitle,content;
    private TextView commentsnum;
    private int likestate=2;
    private int favstate=2;
    private RelativeLayout relativeLayout_comment;
    private Animation animation1;
    private Animation animation2;

    public PublishCommentsView(IComments iComments) {
        _iComments = iComments;
        linearLayout = (LinearLayout) LayoutInflater.from(Init.getContext()).inflate(R.layout.commentsbar, null);
        btncomments = (LinearLayout) linearLayout.findViewById(R.id.btncomments);
        btnreturn = (ImageView) linearLayout.findViewById(R.id.btnreturn);
        btnreturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _iComments.ClickReturn();
            }
        });
        btnfav = (ImageView) linearLayout.findViewById(R.id.btnfav);
        btnlike = (ImageView) linearLayout.findViewById(R.id.btnlike);
        relativeLayout_comment = (RelativeLayout)linearLayout.findViewById(R.id.relativeLayout_comment);
        commentsnum = (TextView)linearLayout.findViewById(R.id.commentsnum);
        relativeLayout_comment.setOnClickListener(onClickListenerfavandlike);
        commentsnum.setOnClickListener(onClickListenerfavandlike);
        commentsnum.setText("0");
        btnfav.setOnClickListener(onClickListenerfavandlike);
        btnlike.setOnClickListener(onClickListenerfavandlike);
        btncomments.setOnClickListener(onClickListenercomments);





        //输入内容view
        linearlayoutcontent = (LinearLayout)LayoutInflater.from(Init.getContext()).inflate(R.layout.comments, null);
        btncancel = (ImageView)linearlayoutcontent.findViewById(R.id.btn_cancel);
        btnok = (ImageView)linearlayoutcontent.findViewById(R.id.btn_ok);
        contenttitle = (TextView)linearlayoutcontent.findViewById(R.id.contenttitle);
        content = (TextView)linearlayoutcontent.findViewById(R.id.content);

        btncancel .setOnClickListener(onClickListenercancel);
        btnok .setOnClickListener(onClickListenerok);
    }



    /**
     * 点击点赞和收藏
     */
    View.OnClickListener onClickListenerfavandlike = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (v.getId() ==R.id.btnfav)
            {
                _iComments.ClickFav(favstate);
            }else if (v.getId() == R.id.btnlike)
            {
                _iComments.ClickLike(likestate);
            }else if (v.getId() == R.id.relativeLayout_comment)
            {
                _iComments.ClickNum();
            }

        }
    };


    /**
     *     点击评论打开评论view
     */

    View.OnClickListener onClickListenercomments = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            linearLayout.setVisibility(View.GONE);
            linearLayoutYXG.addView(linearlayoutcontent);
            _iComments.ClickCommentsView(true);
            content.setText("");
            content.requestFocus();
            InputMethodManager im = (InputMethodManager) Init.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            im.toggleSoftInputFromWindow(content.getWindowToken(), 1, 0);

        }
    };


    /**
     * 点击评论ok
     */
    View.OnClickListener onClickListenerok = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            _iComments.ClickCommentsView(false);
            InputMethodManager im = (InputMethodManager) Init.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(content.getWindowToken(), 0);
            linearLayoutYXG.removeView(linearlayoutcontent);

            linearLayout.setVisibility(View.VISIBLE);
            if (content.getText().toString().trim().equals(""))
            {
                Toast.makeText(Init.getContext(),"请填写评论内容",Toast.LENGTH_SHORT).show();
                return;
            }
            _iComments.submitComments(content.getText().toString());


        }
    };

    /**
     * 点击评论取消
     */
    View.OnClickListener onClickListenercancel = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            _iComments.ClickCommentsView(false);
            InputMethodManager im = (InputMethodManager) Init.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(content.getWindowToken(), 0);
            linearLayoutYXG.removeView(linearlayoutcontent);
            linearLayout.setVisibility(View.VISIBLE);


        }
    };


    public void CloseView()
    {
        InputMethodManager im = (InputMethodManager) Init.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(content.getWindowToken(), 0);
        linearLayoutYXG.removeView(linearlayoutcontent);
        linearLayout.setVisibility(View.VISIBLE);

    }


    public void setCommentsNum(int state,String count)
    {
        if (state == 0)
            commentsnum.setVisibility(View.GONE);
        else
            commentsnum.setVisibility(View.VISIBLE);
        commentsnum.setText(count);
    }
    /**
     * 设置收藏
     * @param state
     */
    public void setFavState(int state) {

        if (favstate == state)
            return;
        favstate = state;
        if (state == 0)
            btnfav.setVisibility(View.GONE);
        else if (state == 1) {

            btnfav.setVisibility(View.VISIBLE);
            animation1 = AnimationUtils.loadAnimation(Init.getContext(), R.anim.comment_scale_1);
            animation2 = AnimationUtils.loadAnimation(Init.getContext(), R.anim.comment_scale_2);
            btnfav.startAnimation(animation1);
            animation1.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    btnfav.setBackground(Init.getContext().getResources().getDrawable(R.drawable.icon_fav2));
                    btnfav.startAnimation(animation2);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

        } else if (state == 2) {

            btnfav.setVisibility(View.VISIBLE);
            animation1 = AnimationUtils.loadAnimation(Init.getContext(), R.anim.comment_scale_1);
            animation2 = AnimationUtils.loadAnimation(Init.getContext(), R.anim.comment_scale_2);
            btnfav.startAnimation(animation1);

            animation1.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    btnfav.setBackground(Init.getContext().getResources().getDrawable(R.drawable.icon_fav1));
                    btnfav.startAnimation(animation2);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });


        }
    }

    /**
     * 设置点赞
     * @param state
     */
    public void setLikeState(int state) {

        if (likestate == state)
            return;
        likestate=state;

        if (state == 0)
            btnlike.setVisibility(View.GONE);
        else if (state == 1) {

            btnlike.setVisibility(View.VISIBLE);
            animation1 = AnimationUtils.loadAnimation(Init.getContext(), R.anim.comment_scale_1);
            animation2 = AnimationUtils.loadAnimation(Init.getContext(), R.anim.comment_scale_2);
            btnlike.startAnimation(animation1);
            animation1.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    btnlike.setBackground(Init.getContext().getResources().getDrawable(R.drawable.icon_like2));
                    btnlike.startAnimation(animation2);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });


        } else if (state == 2) {

            btnlike.setVisibility(View.VISIBLE);
            animation1 = AnimationUtils.loadAnimation(Init.getContext(), R.anim.comment_scale_1);
            animation2 = AnimationUtils.loadAnimation(Init.getContext(), R.anim.comment_scale_2);
            btnlike.startAnimation(animation1);
            animation1.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    btnlike.setBackground(Init.getContext().getResources().getDrawable(R.drawable.icon_like1));
                    btnlike.startAnimation(animation2);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }

    }


    /**
     * 设置是否评论
     * @param state
     */
    public void setLinearComments(Boolean state) {
        if (state)
            btncomments.setVisibility(View.VISIBLE);
        else
            btncomments.setVisibility(View.INVISIBLE);
    }

    public LinearLayout getLinearLayout() {
        return linearLayout;
    }


    /**
     * 设置外围容器
     * @param linearLayoutYXG
     */
    public void setLinearLayoutYXG(LinearLayoutYXG linearLayoutYXG) {

        this.linearLayoutYXG = linearLayoutYXG;
    }


    /**
     * 接口
     */
    public interface IComments {
        void ClickReturn();
        void ClickLike(int state);
        void ClickFav(int state);
        void ClickNum();
        void submitComments(String content);
        void ClickCommentsView(Boolean open);

    }
}
