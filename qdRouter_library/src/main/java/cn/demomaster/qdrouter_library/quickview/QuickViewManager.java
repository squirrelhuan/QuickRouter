package cn.demomaster.qdrouter_library.quickview;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.RequiresApi;

import java.util.HashMap;
import java.util.Map;

import cn.demomaster.qdrouter_library.R;
import cn.demomaster.qdrouter_library.view.QuickFloatDragView;

public class QuickViewManager {

    static QuickViewManager instance;

    public static QuickViewManager getInstance() {
        if (instance == null) {
            instance = new QuickViewManager();
        }
        return instance;
    }

    private QuickViewManager() {
        viewCollection = new ViewCollection();
        mActivityMap = new HashMap<>();
    }

    ViewCollection viewCollection;
    /**
     * 每个Activity中dokitView的集合
     */
    private Map<Activity, String> mActivityMap;

    public void init(Activity activity) {
        //得到activity window中的根布局
        FrameLayout mDecorView = (FrameLayout) activity.getWindow().getDecorView();
        if (mDecorView != null && mDecorView.findViewById(R.id.quick_float_contentview_id) == null) {
            QuickFloatDragView quickFloatDragView = new QuickFloatDragView(activity);
            quickFloatDragView.setOnLayoutChangedListener(new QuickFloatDragView.OnLayoutChangedListener() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                @Override
                public void onViewPositionChanged(View changedView, int left, int top) {
                    //QDLogger.println("id=" + changedView.getId() + ",top = " + top + ",left=" + left);
                    //changedView.setBackgroundColor(Color.RED);
                    if (mActivityMap != null) {
                        int count = viewCollection.size();
                        for (int i = 0; i < count; i++) {
                            QuickView quickView1 = viewCollection.get(i);
                            if(quickView1.getId()==changedView.getId()) {
                                if (quickView1.getLayoutParams() instanceof FrameLayout.LayoutParams) {
                                    //quickView1.setLayoutParams(layoutParams);
                                    FrameLayout.LayoutParams layoutParams =  (FrameLayout.LayoutParams) changedView.getLayoutParams();
                                    quickView1.setTopMargin(top);
                                    quickView1.setLeftMargin(left);
                                   // QDLogger.println("LayoutParams top="+top+",left="+left+",topMargin= "+layoutParams.topMargin+",leftMargin="+layoutParams.leftMargin);
                                    //FrameLayout.LayoutParams layoutParams = quickView1.getLayoutParams();
                                    /*layoutParams.leftMargin = left;
                                    layoutParams.topMargin = top;*/
                                    quickView1.setLayoutParams(layoutParams);
                                }
                                updataViewLayout((Activity) changedView.getContext(), quickView1);
                               // updataViewInfo((Activity) changedView.getContext(), quickView1);
                            }
                        }
                    }
                }
            });
            quickFloatDragView.setId(R.id.quick_float_contentview_id);
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mDecorView.addView(quickFloatDragView, layoutParams);
        }
    }

    public void addViewInfo(Activity activity, QuickView quickView){
        viewCollection.put(quickView);
        mActivityMap.put(activity, "");
        notifyForeground(activity);
    }

    public QuickView findViewInfo(Activity activity, int id) {
        if (mActivityMap != null && mActivityMap.containsKey(activity)) {
            for (QuickView info : viewCollection) {
                if (info.getId() == id) {
                    return info;
                }
            }
        }
        return null;
    }

    public QuickView findViewByTag(String tag) {
        if (viewCollection != null) {
            for (QuickView info : viewCollection) {
                if (info.getTag().equals(tag)) {
                    return info;
                }
            }
        }
        return null;
    }

    public void removeViewByTag(Activity activity, String tag) {
        QuickView quickView = viewCollection.getByTag(tag);
        final FrameLayout mDecorView = (FrameLayout) activity.getWindow().getDecorView();
        //往DecorView的子RootView中添加dokitView
        if (mDecorView != null) {
            FrameLayout frameLayout = mDecorView.findViewById(R.id.quick_float_contentview_id);
            if (frameLayout != null) {
                View v = frameLayout.findViewById(quickView.getId());
                frameLayout.removeView(v);
            }
        }
        viewCollection.remove(quickView);
    }

    public void addViewToWindow(Activity activity, QuickView quickView) {
        //得到activity window中的根布局
        final FrameLayout mDecorView = (FrameLayout) activity.getWindow().getDecorView();
        //往DecorView的子RootView中添加View
        if (mDecorView != null) {
            FrameLayout frameLayout = mDecorView.findViewById(R.id.quick_float_contentview_id);
            if (frameLayout != null) {
                if (quickView.getId() == View.NO_ID || frameLayout.findViewById(quickView.getId()) == null) {
                    //QDLogger.println("addViewToWindow first "+",tag="+quickView.getTag());
                    //FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    View view = quickView.getView(activity);
                    frameLayout.addView(view, quickView.getLayoutParams());
                    if(quickView.onViewLoad!=null){
                        quickView.onViewLoad.onInflatView(frameLayout,view);
                    }
                    //view.setTop(quickView.getTop());
                    //view.setLeft(quickView.getLeft());
                    //QDLogger.i("vid="+view.getId()+",top="+quickView.getTop()+",left="+quickView.getLeft());
                    frameLayout.requestLayout();
                } else {
                    //View view = frameLayout.findViewById(quickView.getId());
                    //QDLogger.println("addViewToWindow unfirst"+quickView.getId()+",view="+view+",tag="+quickView.getTag());
                    updataViewLayout(activity, quickView);
                }
            }
        }
    }

    /**
     * 更新view显示位置和大小
     *
     * @param activity
     * @param quickView
     */
    public void updataViewLayout(Activity activity, QuickView quickView) {
        updataViewInfo(activity, quickView);
        final FrameLayout mDecorView = (FrameLayout) activity.getWindow().getDecorView();
        QuickFloatDragView quickFloatDragView = mDecorView.findViewById(R.id.quick_float_contentview_id);
        //quickFloatDragView.setBackgroundColor(activity.getResources().getColor(R.color.transparent_dark_33));
        if (quickFloatDragView != null) {
            View view = quickFloatDragView.findViewById(quickView.getId());
            if (view != null) {
                //QDLogger.i("updataViewLayout");
                FrameLayout.LayoutParams layoutParams = quickView.getLayoutParams();
                layoutParams.leftMargin = quickView.getLeftMargin();
                layoutParams.topMargin = quickView.getTopMargin();
                view.setLayoutParams(layoutParams);
                updataViewInfo(activity, quickView);
            }
        }
    }

    /**
     * 更新view节点数据
     *
     * @param activity
     * @param quickView
     */
    public void updataViewInfo(Activity activity, QuickView quickView) {
        viewCollection.put(quickView.getTag(), quickView);
        mActivityMap.put(activity, "");
        //QDLogger.println("更新位置："+activity.getClass().getName()+",position="+viewInfo.getLayoutParams().topMargin);
    }

    public void notifyForeground(Activity activity) {
        //QDLogger.println("notifyForeground "+activity);
        init(activity);
        if (mActivityMap == null) {
            return;
        }

        int c1 = viewCollection.size();
        for (int i = 0; i < c1; i++) {
            QuickView quickView = viewCollection.get(i);
            addViewToWindow(activity, quickView);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void onActivityResumed(Activity activity) {
        //QDLogger.println("onActivityResumed");
        notifyForeground(activity);
    }

    public void onActivityDestroy(Activity activity) {
        if (mActivityMap == null) {
            return;
        }
        int count = viewCollection.size();
        for (int i = 0; i < count; i++) {
            QuickView quickView = viewCollection.get(i);
            quickView.unBindActivity(activity);
        }
        mActivityMap.remove(activity);
    }
}
