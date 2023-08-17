package cn.demomaster.qdrouter_library.actionbar;

import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;

import cn.demomaster.qdrouter_library.base.OnReleaseListener;
import cn.demomaster.qdrouter_library.view.ImageTextView;

public interface ActionBarTool extends OnReleaseListener {

    //View mContentView;
     ActionBarLayout getActionBarLayout();

    // ACTIONBAR_TYPE actionbarType = NORMAL;

    /**
     * 设置导航栏样式
     *
     * @param actionbarType
     */
    void setActionBarType(ACTIONBAR_TYPE actionbarType);
    //private boolean hasStatusBar = true;
    //private boolean hasActionBar = true;
   /* public void setHasStatusBar(boolean hasStatusBar) {
        builder.setHasStatusBar(hasStatusBar);
        if (actionBarLayout != null) {
            actionBarLayout.setHasStatusBar(hasStatusBar);
        }
    }*/

   /* public void setHasActionBar(boolean hasActionBar) {
        builder.setHasActionBar(hasActionBar);
        if (actionBarLayout != null) {
            actionBarLayout.setHasActionBar(hasActionBar);
        }
    }*/
    void setActionBarPaddingTop(ActionBarLayout.PaddingWith actionBarPaddingTop);
    /**
     * 设置是否合并导航栏和状态栏
     *
     * @param mixStatusActionBar
     */
     void setMixStatusActionBar(boolean mixStatusActionBar);

   /* public View inflateView() {
        actionBarLayout = builder
                .setContentViewPaddingTop(actionBarPaddingTop)
                .setMixStatusActionBar(mixStatusActionBar)
                .creat();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            actionBarLayout.setId(View.generateViewId());
        }
        return actionBarLayout;
    }*/

    /**
     * 设置颜色模式
     *
     * @param lightModle
     */
    void setLightModle(boolean lightModle);
    void setActionBarThemeColors(int lightColor, int darkColor);
    int getTextColor();
    /**
     * 重置文本顔色
     * @param view
     */
    void resetTextColor(View view);
    View getActionBarTool() ;
    void setActionBarTipType(ACTIONBARTIP_TYPE actionbartip_type);
    ImageTextView getRightView() ;
    void setRightOnClickListener(View.OnClickListener onClickListener) ;
    ImageTextView getTitleView() ;

    void setHeaderBackgroundColor(int color);

    void setTitle(CharSequence string) ;
    ImageTextView getLeftView() ;
    /*@Override
    public void setStateBarColorAuto(boolean b) {

    }*/

    void setLeftOnClickListener(View.OnClickListener onClickListener);

    AppCompatImageView findViewById(int id);

    //void onAttachView(ActionBarLayout mActionBarLayout);
}
