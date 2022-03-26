package cn.demomaster.qdrouter_library.quickview;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.demomaster.qdlogger_library.QDLogger;
import cn.demomaster.qdrouter_library.R;
import cn.demomaster.qdrouter_library.view.QuickFloatDragView;

public class QuickViewManager {

    static QuickViewManager instance;
    public static QuickViewManager getInstance() {
        if(instance==null){
            instance = new QuickViewManager();
        }
        return instance;
    }

    private QuickViewManager() {
        mActivityViews = new HashMap<>();
    }

    /**
     * 每个Activity中dokitView的集合
     */
    private Map<Activity, Map<String, ViewInfo>> mActivityViews;
    public void init(Activity activity){
        //得到activity window中的根布局
        FrameLayout mDecorView = (FrameLayout) activity.getWindow().getDecorView();
        if(mDecorView!=null&&mDecorView.findViewById(R.id.quick_float_contentview_id)==null) {
            QuickFloatDragView frameLayout = new QuickFloatDragView(activity);
            frameLayout.setOnLayoutChangedListener(new QuickFloatDragView.OnLayoutChangedListener() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                @Override
                public void onViewPositionChanged(View changedView, int left, int top) {
                    /*ViewInfo viewInfo = findViewInfo((Activity) changedView.getContext(),changedView.getId());
                    if(viewInfo!=null&&viewInfo.getLayoutParams() instanceof FrameLayout.LayoutParams) {
                        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewInfo.getLayoutParams();
                        layoutParams.leftMargin = left;
                        layoutParams.topMargin = top;
                        viewInfo.setLayoutParams(layoutParams);
                        updataViewLayout((Activity) changedView.getContext(), viewInfo);
                    }*/
                    if (mActivityViews != null) {
                        for (Map.Entry entry : mActivityViews.entrySet()) {
                            Activity activity1 = (Activity) entry.getKey();
                            Map<String, ViewInfo> viewInfoMap = (Map<String, ViewInfo>)entry.getValue();
                            for (Map.Entry entry2: viewInfoMap.entrySet()) {
                                ViewInfo viewInfo1 = (ViewInfo) entry2.getValue();
                                if(viewInfo1.getLayoutParams() instanceof FrameLayout.LayoutParams) {
                                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewInfo1.getLayoutParams();
                                    layoutParams.leftMargin = left;
                                    layoutParams.topMargin = top;
                                    viewInfo1.setLayoutParams(layoutParams);
                                }
                                updataViewInfo(activity1,viewInfo1);
                            }
                        }
                    }
                }
            });
            frameLayout.setId(R.id.quick_float_contentview_id);
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mDecorView.addView(frameLayout,layoutParams);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void setViewInfo(Activity activity, ViewInfo viewInfo){
        Map<String, ViewInfo> viewInfoMap = null;
        if(mActivityViews.containsKey(activity)){
            viewInfoMap = mActivityViews.get(activity);
        }else {
            viewInfoMap = new HashMap<>();
        }
        viewInfoMap.put(viewInfo.getTag(),viewInfo);
        mActivityViews.put(activity,viewInfoMap);
        notifyForeground(activity);
    }

    public ViewInfo findViewInfo(Activity activity,int id){
        if(mActivityViews!=null&&mActivityViews.containsKey(activity)){
            Map<String, ViewInfo> viewInfoMap = mActivityViews.get(activity);
            if(viewInfoMap!=null){
               for(Map.Entry entry:viewInfoMap.entrySet()){
                    ViewInfo info = (ViewInfo) entry.getValue();
                    if(info.getId()==id){
                        return info;
                    }
                }
            }
        }
        return null;
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void addViewToWindow(Activity activity, ViewInfo viewInfo){
        //得到activity window中的根布局
        final FrameLayout mDecorView = (FrameLayout) activity.getWindow().getDecorView();
        //往DecorView的子RootView中添加dokitView
        if (mDecorView!=null) {
            FrameLayout frameLayout = mDecorView.findViewById(R.id.quick_float_contentview_id);
            if(frameLayout!=null){
                if(frameLayout.findViewById(viewInfo.getId())==null) {
                    frameLayout.addView(viewInfo.getView(activity), viewInfo.getLayoutParams());
                }else {
                    updataViewLayout(activity,viewInfo);
                }
            }
        }
    }

    /**
     * 更新view显示位置和大小
     * @param activity
     * @param viewInfo
     */
    public void updataViewLayout(Activity activity, ViewInfo viewInfo){
        updataViewInfo(activity,viewInfo);
        //得到activity window中的根布局
        final FrameLayout mDecorView = (FrameLayout) activity.getWindow().getDecorView();
        if (mDecorView!=null) {
            QuickFloatDragView frameLayout = mDecorView.findViewById(R.id.quick_float_contentview_id);
            if(frameLayout!=null){
                View view = frameLayout.findViewById(viewInfo.getId());
                if(view!=null) {
                    view.setLayoutParams(viewInfo.getLayoutParams());
                }
            }
        }
    }

    /**
     * 更新view节点数据
     * @param activity
     * @param viewInfo
     */
    public void updataViewInfo(Activity activity, ViewInfo viewInfo){
        Map<String, ViewInfo> viewInfoMap = mActivityViews.get(activity);
        if(viewInfoMap==null){
            viewInfoMap = new HashMap<>();
        }
        viewInfoMap.put(viewInfo.getTag(),viewInfo);
        mActivityViews.put(activity,viewInfoMap);
        //QDLogger.println("更新位置："+activity.getClass().getName()+",position="+viewInfo.getLayoutParams().topMargin);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void notifyForeground(Activity activity) {
        init(activity);
        if (mActivityViews == null) {
            return;
        }
        Set<Activity> activitySet =  mActivityViews.keySet();
        List<Activity> activityList = new ArrayList<>(activitySet);
        int count = activityList.size();
        for (int i=0;i<count;i++) {
            Map<String, ViewInfo> viewInfoMap=mActivityViews.get(activityList.get(i));
            if(viewInfoMap!=null) {
                for (ViewInfo viewInfo : viewInfoMap.values()) {
                    addViewToWindow(activity, viewInfo);
                }
            }
        }
        for (Map<String, ViewInfo> viewInfoMap : mActivityViews.values()) {
            for (ViewInfo viewInfo : viewInfoMap.values()) {
                addViewToWindow(activity, viewInfo);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void onActivityResumed(Activity activity) {
        QDLogger.println("onActivityResumed");
        notifyForeground(activity);
    }
    public void onActivityDestroy(Activity activity) {
        if (mActivityViews == null) {
            return;
        }
        mActivityViews.remove(activity);
    }

}
