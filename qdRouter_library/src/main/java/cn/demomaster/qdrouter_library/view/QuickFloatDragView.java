package cn.demomaster.qdrouter_library.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import java.util.LinkedHashMap;

import cn.demomaster.qdlogger_library.QDLogger;
import cn.demomaster.qdrouter_library.quickview.QuickViewChangeListener;
import cn.demomaster.qdrouter_library.util.DisplayUtil;

/**
 * app内悬浮布局
 * 可拖动子布局
 */
public class QuickFloatDragView extends FrameLayout implements QuickViewChangeListener {

    private static final String TAG = QuickFloatDragView.class.getSimpleName();

    private QuickViewDragHelper mViewDragHelper;

    private QuickViewDragHelper.Callback mDragCallback;

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
        //在这里初始化
        initDragCallBack();
        mViewDragHelper = QuickViewDragHelper.create(this, 1.0f, mDragCallback);
    }

    LinkedHashMap<View, Rect> linkedHashMap;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //updataTop();
        // 设置为可以捕获屏幕左边的滑动
        // mViewDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
    }

    private void updataTop() {
        int count = getChildCount();
        linkedHashMap = new LinkedHashMap<>();
        QDLogger.println("updataTop ");
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            linkedHashMap.put(view, new Rect(view.getLeft(), view.getTop(),view.getRight(),view.getBottom()));
            //QDLogger.println("onLayout [" + i + "] left" + view.getLeft() + ",Top=" + view.getTop());
        }
        if (linkedHashMap != null) {
            int count2 = getChildCount();
            for (int i = 0; i < count2; i++) {
                View view = getChildAt(i);
                Rect point = linkedHashMap.get(view);
                if (point != null) {
                    int top = view.getTop();
                    int left = view.getLeft();
                    int dx = point.left - left;
                    int dy = point.top - top;
                    int w1 = point.left+ view.getMeasuredWidth()+ point.right;
                    int h1 = point.top+ view.getHeight()+ point.top;
                    QDLogger.println("onLayout [" + i + "] w1=" + w1 + ",h1=" +h1);
                    QDLogger.println("onLayout [" + i + "] MeasuredWidth=" + getMeasuredWidth() + ",MeasuredHeight=" +getMeasuredHeight());
                    /*view.setTop(point.y);
                    view.setLeft(point.x);*/
                    view.offsetLeftAndRight(dx);
                    view.offsetTopAndBottom(dy);
                }
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // 锁屏后再点亮屏幕会调用一下onLayout，不加判断会让布局还原(为什么???)
        //QDLogger.println("onLayout");
        super.onLayout(changed, l, t, r, b);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            layoutChildren2(l,t,r,b,false);
        }*/
        //layoutChildren(left, top, right, bottom, false /* no force left gravity */);
        if (linkedHashMap != null) {
            int count2 = getChildCount();
            for (int i = 0; i < count2; i++) {
                View view = getChildAt(i);
                //view.setLayoutParams(view.getLayoutParams());
                Rect point = linkedHashMap.get(view);
                FrameLayout.LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
                if (point != null&&(layoutParams.topMargin != point.top&&layoutParams.leftMargin != point.left)) {
                    layoutParams.topMargin = point.top;
                    layoutParams.leftMargin = point.left;
                    view.setLayoutParams(layoutParams);
                    if (onLayoutChangedListener != null) {
                        onLayoutChangedListener.onViewPositionChanged(view,point.left, point.top);
                    }
                    QDLogger.println("onLayout [" + i + "] MeasuredWidth=" + getMeasuredWidth() + ",MeasuredHeight=" +getMeasuredHeight());
                }
            }
            linkedHashMap.clear();
        }
    }
    int finalLeft;
    int finalTop = 100;
    View captureView;
    private void initDragCallBack() {
        mDragCallback = new QuickViewDragHelper.Callback() {
            /**
             * 返回true，表示传入的View可以被拖动
             */
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                captureView = child;
                // 只允许第一个被拖动
                QDLogger.println(TAG, "tryCaptureView=" + child.getTag());
                // child.equals(getChildAt(0));
                return true;
            }

            /**
             * 传入View即将到达的位置(left)，返回值为真正到达的位置
             */
            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                //QDLogger.println(TAG, "clampViewPositionHorizontal=" + child.getTag());
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
                //QDLogger.println(TAG, "clampViewPositionVertical=" + child.getTag());
                return top;
            }

            /**
             * 返回横向能拖动的长度，默认返回0，如果被拖动的View设置了点击事件，返回0会不响应点击事件
             */
            @Override
            public int getViewHorizontalDragRange(View child) {
                int dh = getMeasuredWidth() - child.getMeasuredWidth();
                return dh;
            }

            /**
             * 返回纵向能拖动的长度，默认返回0，如果被拖动的View设置了点击事件，返回0会不响应点击事件
             */
            @Override
            public int getViewVerticalDragRange(View child) {
                //QDLogger.println(TAG, "getViewVerticalDragRange=" + child.getTag());
                int dv = getMeasuredHeight() - child.getMeasuredHeight();
                return dv;
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
                QDLogger.println(TAG, "onViewReleased xvel=" + xvel + ",yvel=" + yvel);
            }

            /**
             * 当某一个View在动的时候的回调，不管是用户手动滑动，还是使用settleCapturedViewAt或者smoothSlideViewTo，都会回调这里
             */
            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                FrameLayout.LayoutParams layoutParams = (LayoutParams) changedView.getLayoutParams();
                /*QDLogger.println(TAG, "mtop=" + changedView.getTop() + ",mleft=" + changedView.getLeft()
                        +",leftMargin="+layoutParams.leftMargin
                        +",topMargin="+layoutParams.topMargin);*/
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
            }
        };
    }

    OnLayoutChangedListener onLayoutChangedListener;

    public void setOnLayoutChangedListener(OnLayoutChangedListener onLayoutChangedListener) {
        this.onLayoutChangedListener = onLayoutChangedListener;
    }

    public interface OnLayoutChangedListener {
        void onViewPositionChanged(View changedView, int left, int top);
    }

    /*
    View mVdhView;
    int mVdhXOffset;
    int mVdhYOffset;
    @Override
    public void computeScroll() {
        if (mViewDragHelper.continueSettling(true)) {
            postInvalidateOnAnimation();
        } else {
            mVdhXOffset = mVdhView.getLeft();
            mVdhYOffset = mVdhView.getTop();
        }
    }*/

    @Override
    public void computeScroll() {
        //QDLogger.println("computeScroll");
        if (mViewDragHelper != null && mViewDragHelper.continueSettling(true)) {
            invalidate();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(ev.getAction()==MotionEvent.ACTION_UP) {
            linkedHashMap = new LinkedHashMap<>();
            for (int i = 0; i < getChildCount(); i++) {
                View view = getChildAt(i);
                linkedHashMap.put(view, new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom()));
                //onLayoutChangedListener.onViewPositionChanged(view, layoutParams.leftMargin, layoutParams.topMargin);
                //QDLogger.println("onInterceptTouchEvent top="+view.getTop());
            }
        }
        boolean b = mViewDragHelper == null ? false : mViewDragHelper.shouldInterceptTouchEvent(ev);
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
