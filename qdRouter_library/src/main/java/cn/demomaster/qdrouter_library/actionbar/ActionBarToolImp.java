package cn.demomaster.qdrouter_library.actionbar;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import cn.demomaster.qdrouter_library.R;
import cn.demomaster.qdrouter_library.util.DisplayUtil;
import cn.demomaster.qdrouter_library.view.ImageTextView;

public class ActionBarToolImp implements ActionBarTool {
    final Activity activity;

    public ActionBarToolImp(Activity activity,ActionBarLayout contentView) {
        this.activity = activity;
        this.actionBarLayout = contentView;
        init();
    }

    public ActionBarToolImp(Fragment fragment,ActionBarLayout contentView) {
        this.activity = fragment.getActivity();
        this.actionBarLayout = contentView;
        init();
    }

    //ActionBarLayout.Builder builder;
    private void init() {
        //builder = new ActionBarLayout.Builder(activity);
        int statusHeight = DisplayUtil.getStatusBarHeight(activity);
        //builder.setStatusHeight(statusHeight);
    }

    //View mActionView;
    //View mContentView;
    ActionBarLayout actionBarLayout;
    public ActionBarLayout getActionBarLayout() {
        return actionBarLayout;
    }

  /*  public void setContentView(int contentViewId) {
        this.setContentView(getLayoutInflater().inflate(contentViewId, null));
    }

    public void setContentView(View contentView) {
        builder.setContentView(contentView);
        //mContentView = contentView;
    }*/

    /*public LayoutInflater getLayoutInflater() {
        return activity.getLayoutInflater();
    }*/

   /*  public void setActionView(int actionbarViewId) {
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        View view = layoutInflater.inflate(actionbarViewId, null);
        setActionView(view);
    }

   public void setActionView(View actionView) {
        //mActionView = actionView;
        builder.setActionBarView(actionView);
    }*/

    // ACTIONBAR_TYPE actionbarType = NORMAL;

    /**
     * 设置导航栏样式
     *
     * @param actionbarType
     */
    public void setActionBarType(ACTIONBAR_TYPE actionbarType) {
        if (actionBarLayout != null) {
            actionBarLayout.setActionBarType(actionbarType);
        }
    }

    public void setActionBarPaddingTop(ActionBarLayout.PaddingWith actionBarPaddingTop) {
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
        //状态栏和导航栏融合
        if (actionBarLayout != null) {
            actionBarLayout.setMixStatusActionBar(mixStatusActionBar);
        }
    }

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
    public void resetTextColor(View view) {
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
                } else
                if (viewGroup.getChildAt(i) instanceof ViewGroup) {
                    resetTextColor(viewGroup.getChildAt(i));
                } else if (viewGroup.getChildAt(i) instanceof TextView) {
                    ((TextView) viewGroup.getChildAt(i)).setTextColor(color);
                }
            }
        }
    }

    public View getActionBarTool() {
        return actionBarLayout.getActionBarView();
    }

    public void setActionBarTipType(ACTIONBARTIP_TYPE actionbartip_type) {
    }

    public ImageTextView getRightView() {
        if (actionBarLayout != null) {
            return actionBarLayout.findViewById(R.id.it_actionbar_common_right);
        }
        return null;
    }
    public void setRightOnClickListener(View.OnClickListener onClickListener) {
        if(getRightView()!=null) {
            getRightView().setOnClickListener(onClickListener);
        }
    }
    public ImageTextView getTitleView() {
        if (actionBarLayout != null) {
            return actionBarLayout.findViewById(R.id.it_actionbar_common_title);
        }
        return null;
    }

    public void setHeaderBackgroundColor(int color) {
        if (actionBarLayout!=null&&actionBarLayout.getActionBarView() != null) {
            actionBarLayout.getActionBarView().setBackgroundColor(color);
        }
    }
    
    public void setTitle(CharSequence string) {
        if (actionBarLayout != null&&actionBarLayout.findViewById(R.id.it_actionbar_common_title) instanceof ImageTextView) {
            ((ImageTextView) actionBarLayout.findViewById(R.id.it_actionbar_common_title)).setText(TextUtils.isEmpty(string)?"":string.toString());
        }
    }
    public ImageTextView getLeftView() {
        if (actionBarLayout != null) {
            return actionBarLayout.findViewById(R.id.it_actionbar_common_left);
        }
        return null;
    }
    /*@Override
    public void setStateBarColorAuto(boolean b) {

    }*/

    public void setLeftOnClickListener(View.OnClickListener onClickListener) {
        if (actionBarLayout!=null&&actionBarLayout.findViewById(R.id.it_actionbar_common_left) != null) {
            actionBarLayout.findViewById(R.id.it_actionbar_common_left).setOnClickListener(onClickListener);
        }
    }

    @Override
    public AppCompatImageView findViewById(int id) {
       if(actionBarLayout!=null){
           return actionBarLayout.findViewById(id);
       }
       return null;
    }

    @Override
    public void onRelease(Object self) {
        actionBarLayout = null;
    }
}
