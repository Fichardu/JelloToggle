package me.fichardu.jellotoggle;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.Scroller;

/**
 * Created by xf on 15/4/14.
 */
public class JelloToggle extends FrameLayout {

    private static final int DEFAULT_DURATION = 500;
    private static final int UNCHECKED_JELLO_COLOR = 0xffadadad;
    private static final int CHECKED_JELLO_COLOR = 0xffff0000;

    private Rect mJelloRect;
    private Paint mJelloPaint;
    private Scroller mScroller;
    private Path mJelloPath;
    private TimeInterpolator mInterpolator;
    private OnCheckedChangeListener mListener;
    private Drawable mCheckedDrawable;
    private Drawable mOnCheckDrawable;
    private Drawable mUnCheckedDrawable;
    private Drawable mDrawable;
    private boolean mChecked = false;

    private int mTouchStartX;
    private int mScrollOffset;
    private int mJelloSize;
    private int mDragLimit;
    private int mJelloMax;
    private int mJelloOffset;

    private long mStartTime;
    private long mDuration;
    private int mCheckedColor = CHECKED_JELLO_COLOR;

    public JelloToggle(Context context) {
        super(context);
        init();
    }

    public JelloToggle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public JelloToggle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mJelloPaint = new Paint();
        mJelloPaint.setAntiAlias(true);
        mCheckedDrawable = getResources().getDrawable(R.drawable.checked);
        mOnCheckDrawable = getResources().getDrawable(R.drawable.check_on);
        mUnCheckedDrawable = getResources().getDrawable(R.drawable.uncheck);
        setJelloState();

        mJelloRect = new Rect();
        mScroller = new Scroller(getContext());
        mJelloPath = new Path();
        mInterpolator = new EaseOutElasticInterpolator();
        mDuration = DEFAULT_DURATION;


    }

    private void calPath() {
        mJelloPath.rewind();
        mJelloPath.moveTo(mJelloRect.right, 0);
        mJelloPath.lineTo(mJelloRect.left, 0);
        mJelloPath.cubicTo(mJelloRect.left, mJelloSize / 2, mJelloRect.left + mJelloOffset -
                        mJelloSize / 3, mJelloSize * 3 / 4,
                mJelloRect.left, mJelloSize);
        mJelloPath.lineTo(mJelloRect.right, mJelloRect.bottom);
        mJelloPath.close();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mDragLimit = getMeasuredWidth() / 4;
        mJelloSize = getMeasuredHeight();
        mJelloRect.set(getMeasuredWidth() - mJelloSize, 0, getMeasuredWidth() + mDragLimit,
                mJelloSize);
        mCheckedDrawable.setBounds(mJelloRect.left, mJelloRect.top, mJelloRect.left + mJelloSize,
                mJelloSize);
        mOnCheckDrawable.setBounds(mJelloRect.left, mJelloRect.top, mJelloRect.left + mJelloSize,
                mJelloSize);
        mUnCheckedDrawable.setBounds(mJelloRect.left, mJelloRect.top, mJelloRect.left + mJelloSize,
                mJelloSize);
        calPath();
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(mScrollOffset, 0);
        super.dispatchDraw(canvas);
        canvas.restore();
        canvas.save();
        canvas.translate(mScrollOffset / 2, 0);
        canvas.drawPath(mJelloPath, mJelloPaint);
        mDrawable.draw(canvas);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean ret = true;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.forceFinished(true);
                }
                mTouchStartX = (int) event.getX();
                mDrawable = mOnCheckDrawable;
                break;
            case MotionEvent.ACTION_MOVE:
                int dragLen = Math.min(0, (int) event.getX() - mTouchStartX);
                mScrollOffset = Math.max(-mDragLimit, dragLen);
                mJelloOffset = dragLen;
                calPath();
                postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (mScrollOffset < 0) {
                    mScroller.startScroll(mScrollOffset, 0, -mScrollOffset, 0);
                    mJelloMax = mJelloOffset;
                    if (mJelloOffset <= -mDragLimit) {
                        mChecked = !mChecked;
                        if (mListener != null) {
                            mListener.onCheckedChange(mChecked);
                        }
                    }
                    setJelloState();
                    postInvalidate();
                    startJello();
                }
                break;
        }

        return ret;
    }

    private void setJelloState() {
        if (mChecked) {
            mJelloPaint.setColor(mCheckedColor);
            mDrawable = mCheckedDrawable;
        } else {
            mJelloPaint.setColor(UNCHECKED_JELLO_COLOR);
            mDrawable = mUnCheckedDrawable;
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            mScrollOffset = mScroller.getCurrX();
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void setJelloDuration(long duration) {
        if (duration <= 0) {
            duration = DEFAULT_DURATION;
        }
        mDuration = duration;
    }

    public void setCheckedJelloColor(int color) {
        mCheckedColor = color;
        setJelloState();
        postInvalidate();
    }

    public void setCheckedDrawable(Drawable drawable) {
        mCheckedDrawable = drawable;
    }

    public void setOnCheckDrawable(Drawable drawable) {
        mOnCheckDrawable = drawable;
    }

    public void setUnCheckedDrawable(Drawable drawable) {
        mUnCheckedDrawable = drawable;
    }


    private void startJello() {
        mStartTime = AnimationUtils.currentAnimationTimeMillis();
        post(mJelloRunnable);
    }

    private Runnable mJelloRunnable = new Runnable() {
        @Override
        public void run() {
            long playTime = AnimationUtils.currentAnimationTimeMillis() - mStartTime;
            if (playTime < mDuration) {
                float fraction = playTime / (float) mDuration;
                mJelloOffset = (int) (mJelloMax * (1 - mInterpolator.getInterpolation
                        (fraction)));
                calPath();
                ViewCompat.postInvalidateOnAnimation(JelloToggle.this);
                post(this);
            } else {
                mJelloOffset = 0;
                calPath();
                ViewCompat.postInvalidateOnAnimation(JelloToggle.this);
            }
        }
    };

    public interface OnCheckedChangeListener {
        void onCheckedChange(boolean checked);
    }

    public void setCheckedChangeListener(OnCheckedChangeListener listener) {
        mListener = listener;
    }
}
