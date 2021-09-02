package cn.demomaster.qdrouter_library.actionbar;

import android.app.Activity;
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

public class ActionBarTool implements ActionBarLayoutInterface {
    final Activity activity;
    public ActionBarTool(Activity activity) {
        this.activity = activity;
        init();
    }

    public ActionBarTool(Fragment fragment) {
        this.activity = fragment.getActivity();
        init();
    }
    ActionBarLayout.Builder builder;
    private void init() {
        builder = new ActionBarLayout.Builder(activity);
        int statusHeight = DisplayUtil.getStatusBarHeight(activity);
        builder.setStatusHeight(statusHeight);
    }

    //View mActionView;
    //View mContentView;
    ActionBarLayout actionBarLayout;
    public ActionBarLayout getActionBarLayout() {
        return actionBarLayout;
    }

    public void setContentView(int contentViewId) {
        this.setContentView(getLayoutInflater().inflate(contentViewId, null));
    }

    public void setContentView(View contentView) {
        builder.setContentView(contentView);
        //mContentView = contentView;
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
        //mActionView = actionView;
        builder.setActionBarView(actionView);
    }

   // ACTIONBAR_TYPE actionbarType = NORMAL;

    /**
     * 设置导航栏样式
     * @param actionbarType
     */
    public void setActionBarType(ACTIONBAR_TYPE actionbarType) {
        //this.actionbarType = actionbarType;
        builder.setActionbarType(actionbarType);
        if (actionBarLayout != null) {
            actionBarLayout.setActionBarType(actionbarType);
        }
    }

    private boolean mixStatusActionBar = true;//状态栏和导航栏融合
    //private boolean hasStatusBar = true;
    //private boolean hasActionBar = true;
    private ActionBarLayout.PaddingWith actionBarPaddingTop = ActionBarLayout.PaddingWith.none;

    public void setHasStatusBar(boolean hasStatusBar) {
        builder.setHasStatusBar(hasStatusBar);
        if (actionBarLayout != null) {
            actionBarLayout.setHasStatusBar(hasStatusBar);
        }
    }

    public void setHasActionBar(boolean hasActionBar) {
        builder.setHasActionBar(hasActionBar);
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

    public View inflateView() {
            actionBarLayout = builder
                    .setContentViewPaddingTop(actionBarPaddingTop)
                    .setMixStatusActionBar(mixStatusActionBar)
                    .creat();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                actionBarLayout.setId(View.generateViewId());
            }
        return actionBarLayout;
    }

    @Override
    public ImageTextView getRightView() {
        if (actionBarLayout != null) {
            return actionBarLayout.findViewById(R.id.it_actionbar_common_right);
        }
        return null;
    }

    public ImageTextView getTitleView() {
        return actionBarLayout.findViewById(R.id.it_actionbar_common_title);
    }

   /* public void setHeaderBackgroundColor(int color) {
        if (actionBarLayout.getActionBarView() != null) {
            actionBarLayout.getActionBarView().setBackgroundColor(color);
        }
    }
*/
    @Override
    public void setTitle(CharSequence string) {
        if (actionBarLayout != null) {
            actionBarLayout.setTitle(string);
        }
    }

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
        resetTextColor(actionBarLayout.getActionBarView());
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
        return actionBarLayout.getActionBarView();
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

}
