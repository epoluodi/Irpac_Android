package com.suypower.stereo.Irpac.System.signaturepad.views;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


import com.suypower.stereo.Irpac.R;
import com.suypower.stereo.Irpac.System.signaturepad.utils.Bezier;
import com.suypower.stereo.Irpac.System.signaturepad.utils.ControlTimedPoints;
import com.suypower.stereo.Irpac.System.signaturepad.utils.TimedPoint;

import java.util.ArrayList;
import java.util.List;


/**
 * 签名程序view
 */

public class SignaturePad extends View
{
    //View state
    private List<TimedPoint> mPoints;
    private boolean mIsEmpty;
    private float mLastTouchX;
    private float mLastTouchY;
    private float mLastVelocity;
    private float mLastWidth;
    private RectF mDirtyRect;


    private float mMinWidth;
    private float mMaxWidth;
    private float mVelocityFilterWeight;
    private OnSignedListener mOnSignedListener;

    private Paint mPaint = new Paint();
    private Path mPath = new Path();
    private Bitmap mSignatureBitmap = null;
    private Canvas mSignatureBitmapCanvas = null;

    public SignaturePad(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.SignaturePad,
                0, 0);

        //设置签名 线条 宽度
        try {
            mMinWidth = a.getFloat(R.styleable.SignaturePad_minWidth, 3f);
            mMaxWidth = a.getFloat(R.styleable.SignaturePad_maxWidth, 7f);
            mVelocityFilterWeight = a.getFloat(R.styleable.SignaturePad_velocityFilterWeight, 0.9f);
            mPaint.setColor(a.getColor(R.styleable.SignaturePad_penColor, Color.BLACK));
        } finally {
            a.recycle();
        }


        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);

        mDirtyRect = new RectF();

        clear();

    }

    /**
     * 设置颜色
     * @param colorRes
     */
    public void setPenColorRes(int colorRes) {
        try {
            setPenColor(getResources().getColor(colorRes));
        } catch (Resources.NotFoundException ex) {
            setPenColor(getResources().getColor(R.color.black));
        }
    }
    @Override protected void onDraw(Canvas canvas) {
        if(mSignatureBitmap != null) {
            canvas.drawBitmap(mSignatureBitmap, 0, 0, mPaint);
        }
    }

    public void setPenColor(int color) {
        mPaint.setColor(color);
    }

    //最小宽度
    public void setMinWidth(float minWidth) {
        mMinWidth = minWidth;
    }


    //最大宽度
    public void setMaxWidth(float maxWidth) {
        mMaxWidth = maxWidth;
    }


    public void setVelocityFilterWeight(float velocityFilterWeight) {
        mVelocityFilterWeight = velocityFilterWeight;
    }

    public void clear()
    {
        mPoints = new ArrayList<TimedPoint>();
        mLastVelocity = 0;
        mLastWidth = (mMinWidth + mMaxWidth) / 2;
        mPath.reset();

        if( mSignatureBitmap != null ) {
            mSignatureBitmap = null;
            ensureSignatureBitmap();
        }

        setIsEmpty(true);

        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        float eventX = event.getX();
        float eventY = event.getY();

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                mPoints.clear();
                mPath.moveTo(eventX, eventY);
                mLastTouchX = eventX;
                mLastTouchY = eventY;
                addPoint(new TimedPoint(eventX, eventY));

            case MotionEvent.ACTION_MOVE:
                resetDirtyRect(eventX, eventY);
                addPoint(new TimedPoint(eventX, eventY));
                break;

            case MotionEvent.ACTION_UP:
                resetDirtyRect(eventX, eventY);
                addPoint(new TimedPoint(eventX, eventY));
                getParent().requestDisallowInterceptTouchEvent(true);
                setIsEmpty(false);
                break;

            default:
                return false;
        }


        invalidate(
                (int) (mDirtyRect.left - mMaxWidth),
                (int) (mDirtyRect.top - mMaxWidth),
                (int) (mDirtyRect.right + mMaxWidth),
                (int) (mDirtyRect.bottom + mMaxWidth));

        return true;
    }


    public void setOnSignedListener(OnSignedListener listener) {
        mOnSignedListener = listener;
    }

    public boolean isEmpty() {
        return mIsEmpty;
    }

    public Bitmap getSignatureBitmap() {
        Bitmap originalBitmap = getTransparentSignatureBitmap();
        Bitmap whiteBgBitmap = Bitmap.createBitmap(originalBitmap.getWidth(), originalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(whiteBgBitmap);
//        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(originalBitmap, 0, 0, null);
        return whiteBgBitmap;
    }

    public void setSignatureBitmap(Bitmap signature) {
        clear();
        ensureSignatureBitmap();

        RectF tempSrc = new RectF();
        RectF tempDst = new RectF();

        int dwidth = signature.getWidth();
        int dheight = signature.getHeight();
        int vwidth = getWidth();
        int vheight = getHeight();


        tempSrc.set(0, 0, dwidth, dheight);
        tempDst.set(0, 0, vwidth, vheight);

        Matrix drawMatrix = new Matrix();
        drawMatrix.setRectToRect(tempSrc, tempDst, Matrix.ScaleToFit.CENTER);

        Canvas canvas = new Canvas(mSignatureBitmap);
        canvas.drawBitmap(signature, drawMatrix, null);
        setIsEmpty(false);
        invalidate();
    }

    public Bitmap getTransparentSignatureBitmap() {
        ensureSignatureBitmap();
        return mSignatureBitmap;
    }

    private void addPoint(TimedPoint newPoint) {
        mPoints.add(newPoint);
        if (mPoints.size() > 2) {

            if (mPoints.size() == 3) mPoints.add(0, mPoints.get(0));

            ControlTimedPoints tmp = calculateCurveControlPoints(mPoints.get(0), mPoints.get(1), mPoints.get(2));
            TimedPoint c2 = tmp.c2;
            tmp = calculateCurveControlPoints(mPoints.get(1), mPoints.get(2), mPoints.get(3));
            TimedPoint c3 = tmp.c1;
            Bezier curve = new Bezier(mPoints.get(1), c2, c3, mPoints.get(2));

            TimedPoint startPoint = curve.startPoint;
            TimedPoint endPoint = curve.endPoint;

            float velocity = endPoint.velocityFrom(startPoint);
            velocity = Float.isNaN(velocity) ? 0.0f : velocity;

            velocity = mVelocityFilterWeight * velocity
                    + (1 - mVelocityFilterWeight) * mLastVelocity;


            float newWidth = strokeWidth(velocity);

           //添加 贝塞尔 平滑 签名
            addBezier(curve, mLastWidth, newWidth);

            mLastVelocity = velocity;
            mLastWidth = newWidth;


            mPoints.remove(0);
        }
    }

    private void addBezier(Bezier curve, float startWidth, float endWidth) {
        ensureSignatureBitmap();
        float originalWidth = mPaint.getStrokeWidth();
        float widthDelta = endWidth - startWidth;
        float drawSteps = (float) Math.floor(curve.length());

        for (int i = 0; i < drawSteps; i++) {
            // Calculate the Bezier (x, y) coordinate for this step.
            float t = ((float) i) / drawSteps;
            float tt = t * t;
            float ttt = tt * t;
            float u = 1 - t;
            float uu = u * u;
            float uuu = uu * u;

            float x = uuu * curve.startPoint.x;
            x += 3 * uu * t * curve.control1.x;
            x += 3 * u * tt * curve.control2.x;
            x += ttt * curve.endPoint.x;

            float y = uuu * curve.startPoint.y;
            y += 3 * uu * t * curve.control1.y;
            y += 3 * u * tt * curve.control2.y;
            y += ttt * curve.endPoint.y;

            // Set the incremental stroke width and draw.
            mPaint.setStrokeWidth(startWidth + ttt * widthDelta);
            mSignatureBitmapCanvas.drawPoint(x, y, mPaint);
            expandDirtyRect(x, y);
        }

        mPaint.setStrokeWidth(originalWidth);
    }

    private ControlTimedPoints calculateCurveControlPoints(TimedPoint s1, TimedPoint s2 ,TimedPoint s3) {
        float dx1 = s1.x - s2.x;
        float dy1 = s1.y - s2.y;
        float dx2 = s2.x - s3.x;
        float dy2 = s2.y - s3.y;

        TimedPoint m1 = new TimedPoint((s1.x + s2.x) / 2.0f, (s1.y + s2.y) / 2.0f);
        TimedPoint m2 = new TimedPoint((s2.x + s3.x) / 2.0f, (s2.y + s3.y) / 2.0f);

        float l1 = (float) Math.sqrt(dx1 * dx1 + dy1 * dy1);
        float l2 = (float) Math.sqrt(dx2 * dx2 + dy2 * dy2);

        float dxm = (m1.x - m2.x);
        float dym = (m1.y - m2.y);
        float k = l2 / (l1 + l2);
        TimedPoint cm = new TimedPoint(m2.x + dxm*k, m2.y + dym*k);

        float tx = s2.x - cm.x;
        float ty = s2.y - cm.y;

        return new ControlTimedPoints(new TimedPoint(m1.x + tx, m1.y + ty), new TimedPoint(m2.x + tx, m2.y + ty));
    }

    private float strokeWidth(float velocity) {
        return Math.max(mMaxWidth / (velocity + 1), mMinWidth);
    }


    private void expandDirtyRect(float historicalX, float historicalY) {
        if (historicalX < mDirtyRect.left) {
            mDirtyRect.left = historicalX;
        } else if (historicalX > mDirtyRect.right) {
            mDirtyRect.right = historicalX;
        }
        if (historicalY < mDirtyRect.top) {
            mDirtyRect.top = historicalY;
        } else if (historicalY > mDirtyRect.bottom) {
            mDirtyRect.bottom = historicalY;
        }
    }


    private void resetDirtyRect(float eventX, float eventY) {

        // The mLastTouchX and mLastTouchY were set when the ACTION_DOWN motion event occurred.
        mDirtyRect.left = Math.min(mLastTouchX, eventX);
        mDirtyRect.right = Math.max(mLastTouchX, eventX);
        mDirtyRect.top = Math.min(mLastTouchY, eventY);
        mDirtyRect.bottom = Math.max(mLastTouchY, eventY);
    }

    private void setIsEmpty(boolean newValue) {
        if(mIsEmpty != newValue) {
            mIsEmpty = newValue;

            if(mOnSignedListener != null) {
                if(mIsEmpty) {
                    mOnSignedListener.onClear();
                } else {
                    mOnSignedListener.onSigned();
                }
            }
        }
    }

    private void ensureSignatureBitmap() {
        if (mSignatureBitmap == null) {
            mSignatureBitmap = Bitmap.createBitmap(getWidth(), getHeight(),
                    Bitmap.Config.ARGB_8888);
            mSignatureBitmapCanvas = new Canvas(mSignatureBitmap);
        }
    }

    /**
     * 接口 通知 清空和 已经签名
     */
    public interface OnSignedListener {
        public void onSigned();
        public void onClear();
    }
}