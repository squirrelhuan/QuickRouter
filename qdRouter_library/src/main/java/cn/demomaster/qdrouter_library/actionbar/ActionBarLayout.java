package cn.demomaster.qdrouter_library.actionbar;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import cn.demomaster.qdrouter_library.R;
import cn.demomaster.qdrouter_library.view.ImageTextView;

public class ActionBarLayout extends FrameLayout implements ActionBarInterface {

    public ActionBarLayout(@NonNull Context context) {
        super(context);
    }

    public ActionBarLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ActionBarLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ActionBarLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /*View statusbarView;
    View actionBarView;
    View contentView;*/
    Builder mBuilder;

    public ActionBarLayout(Builder builder) {
        super(builder.activity);
        mBuilder = builder;
        update();
    }

    public View getStatusbarView() {
        return mBuilder == null ? null : mBuilder.statusbarView;
    }

    public View getActionBarView() {
        return mBuilder == null ? null : mBuilder.actionBarView;
    }

    public View getContentView() {
        return mBuilder == null ? null : mBuilder.contentView;
    }

    /**
     * 获取导航栏底部 到屏幕顶端的距离
     * @return
     */
    public int getActionBarPositionY() {
        View actionBarView = mBuilder.actionBarView;
        if (actionBarView != null) {
            int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                    getMeasuredWidth(), MeasureSpec.AT_MOST);

            int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                    getMeasuredHeight(), MeasureSpec.AT_MOST);
            actionBarView.measure(childWidthMeasureSpec, childHeightMeasureSpec);

            //int[] location = new int[2];
            //actionBarView.getLocationInWindow(location); //获取在当前窗口内的绝对坐标
            //actionBarView.getLocationOnScreen(location);//获取在整个屏幕内的绝对坐标
            int h = actionBarView.getMeasuredHeight();
            int top = actionBarView.getTop();
            return top + h;
        }
        return 0;
    }

    //private final ArrayList<View> mMatchParentChildren = new ArrayList<>(1);

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren2(mBuilder.contentView);
    }

    protected void measureChildren2(View view) {
        int[] location = new int[2];
        int h = 0;
        int actionBarHeight = 0;
        if (mBuilder.actionBarView != null) {
            actionBarHeight = mBuilder.actionBarView.getMeasuredHeight();
        }
        if (mBuilder.mixStatusActionBar) {
            actionBarHeight -= mBuilder.statusHeight;
        }
        switch (mBuilder.actionbarType) {
            case NORMAL:
                h = mBuilder.statusHeight + actionBarHeight;
                break;
            case NO_STATUS:
                h = actionBarHeight;
                break;
            case NO_ACTION_BAR:
                h += mBuilder.statusHeight;
                break;
            case ACTION_STACK:
            //case ACTION_TRANSPARENT:
            case ACTION_STACK_NO_ACTION:
            case ACTION_STACK_NO_STATUS:
            case NO_ACTION_BAR_NO_STATUS:
                h = 0;
                break;
        }
        
        if (view!=null) {
            view.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth(),
                    MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
                    getMeasuredHeight() - location[1] - h, MeasureSpec.EXACTLY));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        // super.onLayout(changed, left, top, right, bottom);
        layoutChildren1(left, top, right, bottom  /* no force left gravity */);
    }

    private static final int DEFAULT_CHILD_GRAVITY = Gravity.TOP | Gravity.START;
    void layoutChildren1(int left, int top, int right, int bottom) {
        //按顺序
        layoutChildren2(mBuilder.statusbarView, left, top, right, bottom, false);
        layoutChildren2(mBuilder.actionBarView, left, top, right, bottom, false);
        layoutChildren2(mBuilder.contentView, left, top, right, bottom, false);

        final int count = getChildCount();
        for (int i = count - 1; i > 0; i--) {
            final View child = getChildAt(i);
            if (child == mBuilder.statusbarView || child == mBuilder.actionBarView || child == mBuilder.contentView) {
                continue;
            }
            layoutChildren2(mBuilder.contentView, left, top, right, bottom, false);
        }
    }

    void layoutChildren2(final View child, int left, int top, int right, int bottom, boolean forceLeftGravity) {
        if (child == null) {
            return;
        }
        final int parentLeft = getPaddingLeft();
        final int parentRight = right - left - getPaddingRight();
        final int parentTop = getPaddingTop();
        final int parentBottom = bottom - top - getPaddingBottom();

        int statusBarHeight = mBuilder.statusHeight;
        //Log.e("action",child.toString());
        if (child != null && child.getVisibility() != GONE) {
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (lp == null) {
                lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            }

            final int width = child.getMeasuredWidth();
            final int height = child.getMeasuredHeight();

            int gravity = lp.gravity;
            if (gravity == -1) {
                gravity = DEFAULT_CHILD_GRAVITY;
            }

            int childLeft;
            int childTop;
            int layoutDirection = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                layoutDirection = getLayoutDirection();
            }
            final int absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection);
            final int verticalGravity = gravity & Gravity.VERTICAL_GRAVITY_MASK;
            switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
                case Gravity.CENTER_HORIZONTAL:
                    childLeft = parentLeft + (parentRight - parentLeft - width) / 2 +
                            lp.leftMargin - lp.rightMargin;
                    break;
                case Gravity.RIGHT:
                    if (!forceLeftGravity) {
                        childLeft = parentRight - width - lp.rightMargin;
                        break;
                    }
                case Gravity.LEFT:
                default:
                    childLeft = parentLeft + lp.leftMargin;
            }

            switch (verticalGravity) {
                case Gravity.CENTER_VERTICAL:
                    childTop = parentTop + (parentBottom - parentTop - height) / 2 +
                            lp.topMargin - lp.bottomMargin;
                    break;
                case Gravity.BOTTOM:
                    childTop = parentBottom - height - lp.bottomMargin;
                    break;
                case Gravity.TOP:
                default:
                    childTop = parentTop + lp.topMargin;
            }

            if (mBuilder.hasActionBar&&child == mBuilder.actionBarView) {
                if(!mBuilder.mixStatusActionBar){
                    childTop += statusBarHeight;
                }
            } else if (child == mBuilder.contentView) {
                childTop = getContentViewTop();
                /*//融合导航栏模式 需要把状态栏高度附加到actionbar的内边距paddingTop上*/
                setContentViewPaddingTop();
            }
            child.layout(childLeft, childTop, childLeft + width, childTop + height);
        }
    }
    //获取内容区域到屏幕顶部的距离
    private int getContentViewTop() {
        int statusBarHeight = mBuilder.statusHeight;
        int actionBarBottom = getActionBarPositionY();
        int childTop = 0;
        switch (mBuilder.actionbarType) {
            case NORMAL:
            case NO_STATUS:
                childTop = actionBarBottom;
                break;
            case NO_ACTION_BAR:
                childTop = statusBarHeight;
                break;
            case ACTION_STACK:
            //case ACTION_TRANSPARENT:
            case ACTION_STACK_NO_ACTION:
            case ACTION_STACK_NO_STATUS:
            case NO_ACTION_BAR_NO_STATUS:
                childTop = 0;
                break;
        }
        return childTop;
    }

    //获取导航栏高度
    private int getActionBarHeight() {
        int actionBarHeight = 0;
        if (mBuilder.hasActionBar && mBuilder.actionBarView != null) {
            actionBarHeight = mBuilder.actionBarView.getMeasuredHeight();
            if (mBuilder.mixStatusActionBar) {
                int statusBarHeight = mBuilder.statusHeight;
                actionBarHeight -= statusBarHeight;
            }
        }
        return actionBarHeight;
    }
    //设置内边距
    private void setContentViewPaddingTop() {
        if(mBuilder.contentView==null){
            return;
        }

        int statusBarHeight = mBuilder.statusHeight;
        int actionBarHeight = getActionBarHeight();

        int contentViewPaddingTop = 0;
        switch (mBuilder.contentViewPaddingTop) {
            case none:
                contentViewPaddingTop = 0;
                break;
            case statusBar_actionBar:
                contentViewPaddingTop = actionBarHeight + statusBarHeight;
                break;
            case actionBar:
                contentViewPaddingTop = actionBarHeight;
                break;
            case statusBar:
                contentViewPaddingTop = statusBarHeight;
                break;
        }
        View view = mBuilder.contentView;
        //fix bug 1在这里设置padding 当页面跟布局是RelativeLayout时会出现显示不完全的bug,位置调整到layout后面
        view.setPadding(view.getPaddingLeft(), contentViewPaddingTop, view.getPaddingRight(), view.getPaddingBottom());
    }

    /**
     * 设置导航栏样式
     *
     * @param actionbarType
     */
    public void setActionBarType(ACTIONBAR_TYPE actionbarType) {
        mBuilder.setActionbarType(actionbarType);
        update();
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        if (child != null && child.getParent() == null) {
            super.addView(child, params);
        }
    }

    @Override
    public void removeView(View view) {
        if (view != null && view.getParent() != null) {
            super.removeView(view);
        }
    }

    public void update() {
        if (mBuilder.contentView != null && mBuilder.contentView.getParent() == null) {
            addView(mBuilder.contentView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }

        if (mBuilder.hasActionBar) {//有无导航栏
            addView(mBuilder.actionBarView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        } else {
            removeView(mBuilder.actionBarView);
        }

        if (mBuilder.hasStatusBar && !mBuilder.mixStatusActionBar) {//有无状态栏
            if (mBuilder.statusbarView == null) {
                mBuilder.statusbarView = new FrameLayout(mBuilder.activity);
            }
            addView(mBuilder.statusbarView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mBuilder.statusHeight));
        } else {
            removeView(mBuilder.statusbarView);
        }

        if (mBuilder.actionBarView != null) {
            int paddingTop;
            if (mBuilder.mixStatusActionBar && mBuilder.hasStatusBar) {
                paddingTop = mBuilder.statusHeight + mBuilder.actionBarPaddingTop_Last;
            } else {
                paddingTop = mBuilder.actionBarPaddingTop_Last;
            }
            if (mBuilder.actionBarView.getPaddingTop() != paddingTop) {
                mBuilder.actionBarView.setPadding(mBuilder.actionBarView.getPaddingLeft(), paddingTop, mBuilder.actionBarView.getPaddingRight(), mBuilder.actionBarView.getPaddingBottom());
            }
        }
        requestLayout();
       /* if(hasRequestLayout){
            requestLayout();
        }*/
    }

    @Override
    public void setTitle(CharSequence string) {
        ImageTextView imageTextView = findViewById(R.id.it_actionbar_common_title);
        if (imageTextView != null) {
            imageTextView.setText((String) string);
        }
    }

    public enum PaddingWith {
        statusBar_actionBar,
        statusBar,
        actionBar,
        none
    }

    public void setHasActionBar(boolean hasActionBar) {
        mBuilder.hasActionBar = hasActionBar;
        //QDLogger.d("setHasActionBar");
    }

    public void setHasStatusBar(boolean hasStatusBar) {
        mBuilder.hasStatusBar = hasStatusBar;
        //QDLogger.d("setHasStatusBar");
    }

    public void setContentViewPaddingTop(PaddingWith paddingWith) {
        mBuilder.contentViewPaddingTop = paddingWith;
        update();
    }

    public void setStatusHeight(int statusHeight) {
        mBuilder.statusHeight = statusHeight;
        //QDLogger.d("setStatusHeight");
        update();
    }

    public void setMixStatusActionBar(boolean mixStatusActionBar) {
        mBuilder.mixStatusActionBar = mixStatusActionBar;
        //QDLogger.d("setMixStatusActionBar");
        update();
    }

    public static class Builder {
        private ACTIONBAR_TYPE actionbarType = ACTIONBAR_TYPE.NORMAL;
        private int statusHeight;//状态栏高度
        private int actionBarPaddingTop_Last = 0;
        private boolean mixStatusActionBar = true;//是否使用融合模式，即actionbar padding 状态栏高度,优先级高于hasStatusBar
        private boolean hasStatusBar = true;
        private boolean hasActionBar = true;
        private PaddingWith contentViewPaddingTop = PaddingWith.none;
        private View statusbarView;
        private View actionBarView;
        private View contentView;
        private final Activity activity;

        public Builder(Activity activity) {
            this.activity = activity;
            setActionbarType(actionbarType);
        }

        public void setActionbarType(ACTIONBAR_TYPE actionbarType) {
            this.actionbarType = actionbarType;
            switch (actionbarType) {
                case NORMAL:
                case ACTION_STACK:
                    //case ACTION_TRANSPARENT:
                    hasStatusBar = true;
                    hasActionBar = true;
                    break;
                case NO_ACTION_BAR:
                    hasStatusBar = true;
                    hasActionBar = false;
                    break;
                case NO_ACTION_BAR_NO_STATUS:
                    hasStatusBar = false;
                    hasActionBar = false;
                    break;
                case NO_STATUS:
                case ACTION_STACK_NO_STATUS:
                    hasStatusBar = false;
                    hasActionBar = true;
                    break;
                case ACTION_STACK_NO_ACTION:
                    hasStatusBar = true;
                    hasActionBar = false;
                    break;
            }
        }

        public Builder setStatusHeight(int statusHeight) {
            this.statusHeight = statusHeight;
            return this;
        }

        public Builder setActionBarView(View actionBarView) {
            this.actionBarView = actionBarView;
            actionBarPaddingTop_Last = actionBarView.getPaddingTop();
            return this;
        }

        public Builder setContentView(View contentView) {
            this.contentView = contentView;
            return this;
        }

        public Builder setHasStatusBar(boolean hasStatusBar) {
            this.hasStatusBar = hasStatusBar;
            return this;
        }

        public Builder setContentViewPaddingTop(PaddingWith contentViewPaddingTop) {
            this.contentViewPaddingTop = contentViewPaddingTop;
            return this;
        }

        public Builder setHasActionBar(boolean hasActionBar) {
            this.hasActionBar = hasActionBar;
            return this;
        }

        public Builder setMixStatusActionBar(boolean mixStatusActionBar) {
            this.mixStatusActionBar = mixStatusActionBar;
            return this;
        }

        public ActionBarLayout creat() {
            return new ActionBarLayout(this);
        }
    }
}
