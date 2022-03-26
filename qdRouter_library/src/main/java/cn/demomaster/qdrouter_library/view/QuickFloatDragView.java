package cn.demomaster.qdrouter_library.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.customview.widget.ViewDragHelper;

import cn.demomaster.qdrouter_library.quickview.QuickViewChangeListener;
import cn.demomaster.qdrouter_library.util.DisplayUtil;

/**
 * app内悬浮布局
 * 可拖动子布局
 */
public class QuickFloatDragView extends FrameLayout implements QuickViewChangeListener {

    private static final String TAG = QuickFloatDragView.class.getSimpleName();

    private ViewDragHelper mViewDragHelper;

    private ViewDragHelper.Callback mDragCallback;

    public QuickFloatDragView(Context context) {
        this(context, null);
    }

    public QuickFloatDragView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QuickFloatDragView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        initDragCallBack();
        mViewDragHelper = ViewDragHelper.create(this, 1.0f, mDragCallback);
        // 设置为可以捕获屏幕左边的滑动
        // mViewDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // 锁屏后再点亮屏幕会调用一下onLayout，不加判断会让布局还原(为什么???)
            super.onLayout(changed, l, t, r, b);
    }

    int finalLeft;
    int finalTop = 100;
    private void initDragCallBack() {
        mDragCallback = new ViewDragHelper.Callback() {
            /**
             * 返回true，表示传入的View可以被拖动
             */
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                // 只允许第一个被拖动
                return child.equals(getChildAt(0));
            }

            /**
             * 传入View即将到达的位置(left)，返回值为真正到达的位置
             */
            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                /*LayoutParams lp = (LayoutParams) child.getLayoutParams();
                int leftBorder = getPaddingLeft() + lp.leftMargin;
                int rightBorder = getMeasuredWidth() - getPaddingRight() - child.getMeasuredWidth() - lp.rightMargin;
                return Math.min(Math.max(leftBorder, left), rightBorder);*/
                return left;
            }

            /**
             * 传入View即将到达的位置(top)，返回值为真正到达的位置
             */
            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                return top;
            }

            /**
             * 返回横向能拖动的长度，默认返回0，如果被拖动的View设置了点击事件，返回0会不响应点击事件
             */
            @Override
            public int getViewHorizontalDragRange(View child) {
                return getMeasuredWidth() - child.getMeasuredWidth();
            }

            /**
             * 返回纵向能拖动的长度，默认返回0，如果被拖动的View设置了点击事件，返回0会不响应点击事件
             */
            @Override
            public int getViewVerticalDragRange(View child) {
                return getMeasuredHeight() - child.getMeasuredHeight();
            }

            @Override
            public void onEdgeDragStarted(int edgeFlags, int pointerId) {
                Log.i(TAG, "onEdgeDragStarted;" + edgeFlags);
                // 当从屏幕左边开始滑动的时候，开始滑动第一个子控件
                mViewDragHelper.captureChildView(getChildAt(0), pointerId);
            }

            /**
             * 当手指离开以后的回调
             *
             * @param releasedChild 子View
             * @param xvel X轴的速度
             * @param yvel Y轴的速度
             */
            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                if (releasedChild.equals(getChildAt(0))) {
                    Log.i(TAG, "xvel = " + xvel + ",yvel=" + yvel);
                    // 手指松开以后自动回到原始位置
                    //mViewDragHelper.settleCapturedViewAt(finalLeft, finalTop);
                    invalidate();
                }
            }

            /**
             * 当某一个View在动的时候的回调，不管是用户手动滑动，还是使用settleCapturedViewAt或者smoothSlideViewTo，都会回调这里
             */
            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                if (onLayoutChangedListener != null) {
                    onLayoutChangedListener.onViewPositionChanged(changedView, left, top);
                }
                finalTop = top;
                int screenWidth = DisplayUtil.getScreenWidth(getContext());
                if (getChildCount() > 0) {
                    int width = getChildAt(0).getMeasuredWidth();
                    int w1 = 0;
                    finalLeft = (left < screenWidth / 2) ? w1 : (screenWidth - width - w1);
                }
                // Log.i(TAG, "left=" + left + ",top=" + top + ",dx=" + dx + ",dy=" + dy);
            }
        };
    }

    OnLayoutChangedListener onLayoutChangedListener;

    public void setOnLayoutChangedListener(OnLayoutChangedListener onLayoutChangedListener) {
        this.onLayoutChangedListener = onLayoutChangedListener;
    }

    public static interface OnLayoutChangedListener {

        void onViewPositionChanged(View changedView, int left, int top);
    }

    @Override
    public void computeScroll() {
        if (mViewDragHelper != null && mViewDragHelper.continueSettling(true)) {
            invalidate();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean b = mViewDragHelper == null ? false : mViewDragHelper.shouldInterceptTouchEvent(ev);
        //QDLogger.println("b="+b);
        return b;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //QDLogger.println("onTouchEvent");
        if (mViewDragHelper != null) {
            mViewDragHelper.processTouchEvent(event);
        }
        return false;
    }
}
