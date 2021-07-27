package cn.demomaster.qdrouter_library.actionbar;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import cn.demomaster.qdrouter_library.R;
import cn.demomaster.qdrouter_library.util.DisplayUtil;
import cn.demomaster.qdrouter_library.view.ImageTextView;

import static cn.demomaster.qdrouter_library.actionbar.ACTIONBAR_TYPE.NORMAL;

public class ActionBarTool implements ActionBarLayoutInterface {
    Activity activity;
    public ActionBarTool(Activity activity) {
        this.activity = activity;
    }
    public ActionBarTool(Fragment fragment) {
        this.activity = fragment.getActivity();
    }

    View mActionView;
    View mContentView;
    ActionBarLayout actionBarLayout;
    public ActionBarLayout getActionBarLayout() {
        return actionBarLayout;
    }

    public void setContentView(int contentViewId) {
        this.setContentView(getLayoutInflater().inflate(contentViewId, null));
    }

    public void setContentView(View contentView) {
        mContentView = contentView;
    }

    public LayoutInflater getLayoutInflater() {
        return activity.getLayoutInflater();
    }

    public void setActionView(int actionbarViewId) {
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        View view = layoutInflater.inflate(actionbarViewId, null);
        setActionView(view);
    }

    public void setActionView(View actionView) {
        mActionView = actionView;
    }

    ACTIONBAR_TYPE actionbarType = NORMAL;

    /**
     * 设置导航栏样式
     *
     * @param actionbarType
     */
    public void setActionBarType(ACTIONBAR_TYPE actionbarType) {
        this.actionbarType = actionbarType;
        if (actionBarLayout != null) {
            actionBarLayout.setActionBarType(actionbarType);
        }
    }

    private boolean mixStatusActionBar = true;//状态栏和导航栏融合
    private boolean hasStatusBar = true;
    private boolean hasActionBar = true;
    private ActionBarLayout.PaddingWith actionBarPaddingTop = ActionBarLayout.PaddingWith.none;

    public void setHasStatusBar(boolean hasStatusBar) {
        this.hasStatusBar = hasStatusBar;
        if (actionBarLayout != null) {
            actionBarLayout.setHasStatusBar(hasStatusBar);
        }
    }

    public void setHasActionBar(boolean hasActionBar) {
        this.hasActionBar = hasActionBar;
        if (actionBarLayout != null) {
            actionBarLayout.setHasActionBar(hasActionBar);
        }
    }

    public void setActionBarPaddingTop(ActionBarLayout.PaddingWith actionBarPaddingTop) {
        this.actionBarPaddingTop = actionBarPaddingTop;
        if (actionBarLayout != null) {
            actionBarLayout.setContentViewPaddingTop(actionBarPaddingTop);
        }
    }

    /**
     * 设置是否合并导航栏和状态栏
     *
     * @param mixStatusActionBar
     */
    public void setMixStatusActionBar(boolean mixStatusActionBar) {
        this.mixStatusActionBar = mixStatusActionBar;
        if (actionBarLayout != null) {
            actionBarLayout.setMixStatusActionBar(mixStatusActionBar);
        }
    }
    boolean isUseActionBarLayout;
    public void setUseActionBarLayout(boolean useActionBarLayout) {
        isUseActionBarLayout = useActionBarLayout;
    }

    public View inflateView() {
            ActionBarLayout.Builder builder = new ActionBarLayout.Builder(activity, actionbarType);
            int statusHeight = DisplayUtil.getStatusBarHeight(activity);
            actionBarLayout = builder.setStatusHeight(statusHeight)
                    .setHasStatusBar(hasStatusBar)
                    .setHasActionBar(hasActionBar)
                    .setContentView(mContentView)
                    .setContentViewPaddingTop(actionBarPaddingTop)
                    .setMixStatusActionBar(mixStatusActionBar)
                    .setActionBarView(mActionView)
                    .creat();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                actionBarLayout.setId(View.generateViewId());
            }
        return actionBarLayout;
    }

    @Override
    public ImageTextView getRightView() {
        if (actionBarLayout == null) {
            return null;
        }
        return actionBarLayout.findViewById(R.id.it_actionbar_common_right);
    }

    public ImageTextView getTitleView() {
        return actionBarLayout.findViewById(R.id.it_actionbar_common_title);
    }

    public void setHeaderBackgroundColor(int color) {
        if (mActionView != null) {
            mActionView.setBackgroundColor(color);
        }
    }

    @Override
    public void setTitle(CharSequence string) {
        if (actionBarLayout != null) {
            actionBarLayout.setTitle(string);
        }
    }

    /*@Override
    public void setStateBarColorAuto(boolean b) {

    }*/

    @Override
    public void setLeftOnClickListener(View.OnClickListener onClickListener) {
        if (getLeftView() != null)
            getLeftView().setOnClickListener(onClickListener);
    }

    boolean isLightModle = false;//亮色

    /**
     * 设置颜色模式
     *
     * @param lightModle
     */
    public void setLightModle(boolean lightModle) {
        isLightModle = lightModle;
        resetTextColor(actionBarLayout);
    }

    public int textLightColor = Color.WHITE;
    public int textDarkColor = Color.BLACK;

    @Override
    public void setActionBarThemeColors(int lightColor, int darkColor) {
        textLightColor = lightColor;
        textDarkColor = darkColor;
        resetTextColor(mActionView);
    }

    public int getTextColor() {
        return isLightModle ? textLightColor : textDarkColor;
    }

    /**
     * 重置文本顔色
     *
     * @param view
     */
    private void resetTextColor(View view) {
        if (view == null) {
            return;
        }
        int color = getTextColor();
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                if (viewGroup.getChildAt(i) instanceof ImageView) {
                    Drawable drawable = (((ImageView) viewGroup.getChildAt(i)).getDrawable());
                    if (drawable != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            drawable.setTint(color);
                        }
                    }
                }
                if (viewGroup.getChildAt(i) instanceof ImageTextView) {
                    ((ImageTextView) viewGroup.getChildAt(i)).setTextColor(color);
                } else if (viewGroup.getChildAt(i) instanceof ViewGroup) {
                    resetTextColor(viewGroup.getChildAt(i));
                } else if (viewGroup.getChildAt(i) instanceof TextView) {
                    ((TextView) viewGroup.getChildAt(i)).setTextColor(color);
                }
            }
        }
    }

    @Override
    public void setRightOnClickListener(View.OnClickListener onClickListener) {
        getRightView().setOnClickListener(onClickListener);
    }

    public View getActionBarTool() {
        return mActionView;
    }

    public ActionBarTip getActionBarTip() {
        return null;
    }

    public void setActionBarTipType(ACTIONBARTIP_TYPE actionbartip_type) {
    }

    public ImageTextView getLeftView() {
        if (actionBarLayout != null) {
            return actionBarLayout.findViewById(R.id.it_actionbar_common_left);
        }
        return null;
    }

    public static class Builder {
        Activity activity;
        View actionView;
        View contentView;

        ACTIONBAR_TYPE actionbarType = NORMAL;
        private boolean mixStatusActionBar = true;//状态栏和导航栏融合
        private boolean hasStatusBar = true;
        private boolean hasActionBar = true;
        private ActionBarLayout.PaddingWith actionBarPaddingTop = ActionBarLayout.PaddingWith.none;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        public Builder setActionView(View actionView) {
            this.actionView = actionView;
            return this;
        }

        public Builder setContentView(View contentView) {
            this.contentView = contentView;
            return this;
        }

        public Builder setActionView(int actionbarViewId) {
            LayoutInflater layoutInflater = activity.getLayoutInflater();
            View view = layoutInflater.inflate(actionbarViewId, null);
            return setActionView(view);
        }

        public ActionBarLayout inflateView(){
            ActionBarLayout.Builder builder = new ActionBarLayout.Builder(activity, actionbarType);
            int statusHeight = DisplayUtil.getStatusBarHeight(activity);
            ActionBarLayout actionBarLayout = builder.setStatusHeight(statusHeight)
                    .setHasStatusBar(hasStatusBar)
                    .setHasActionBar(hasActionBar)
                    .setContentView(contentView)
                    .setContentViewPaddingTop(actionBarPaddingTop)
                    .setMixStatusActionBar(mixStatusActionBar)
                    .setActionBarView(actionView)
                    .creat();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                actionBarLayout.setId(View.generateViewId());
            }
            return actionBarLayout;
        }
    }
}
