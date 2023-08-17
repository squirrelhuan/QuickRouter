package cn.demomaster.qdrouter_library.manager;

import static android.content.Context.ACTIVITY_SERVICE;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import cn.demomaster.qdlogger_library.QDLogger;
import cn.demomaster.qdrouter_library.base.lifecycle.LifecycleType;
import cn.demomaster.qdrouter_library.quickview.QuickViewManager;

public class QuickActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

    /**
     * fragment 生命周期回调
     */
    private FragmentManager.FragmentLifecycleCallbacks sFragmentLifecycleCallbacks;
    QuickActivityLifecycleCallbacks(){
        sFragmentLifecycleCallbacks = new QuickFragmentLifecycleCallbacks();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (activity instanceof FragmentActivity) {
            //注册fragment生命周期回调
            ((FragmentActivity) activity).getSupportFragmentManager().registerFragmentLifecycleCallbacks(sFragmentLifecycleCallbacks, true);
        }

        ActivityManager activityManager = (ActivityManager) activity.getSystemService(ACTIVITY_SERVICE);
        //最大分配内存
        int memory = activityManager.getMemoryClass();
        System.out.println("memory: " + memory);
        //最大分配内存获取方法2
        float maxMemory = (float) (Runtime.getRuntime().maxMemory() * 1.0 / (1024 * 1024));
        //当前分配的总内存
        float totalMemory = (float) (Runtime.getRuntime().totalMemory() * 1.0 / (1024 * 1024));
        //剩余内存
        float freeMemory = (float) (Runtime.getRuntime().freeMemory() * 1.0 / (1024 * 1024));
        System.out.println("maxMemory: " + maxMemory);
        System.out.println("totalMemory: " + totalMemory);
        System.out.println("freeMemory: " + freeMemory);

        QDActivityManager.getInstance().pushActivity(activity);
        record(LifecycleType.onActivityCreated, activity);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onActivityStarted(Activity activity) {
        QuickViewManager.getInstance().notifyForeground(activity);
        record(LifecycleType.onActivityStarted, activity);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onActivityResumed(Activity activity) {
        //QDLogger.println("onActivityResumed="+activity);
        QuickViewManager.getInstance().onActivityResumed(activity);
        QDActivityManager.getInstance().onActivityResumed(activity);
        record(LifecycleType.onActivityResumed, activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        QDActivityManager.getInstance().onActivityPaused(activity);
        record(LifecycleType.onActivityPaused, activity);
    }
    
    @Override
    public void onActivityStopped(Activity activity) {
        QDActivityManager.getInstance().onActivityStopped(activity);
        record(LifecycleType.onActivityStopped, activity);
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        record(LifecycleType.onActivitySaveInstanceState, activity);
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        QuickViewManager.getInstance().onActivityDestroy(activity);
        QDActivityManager.getInstance().removeActivityFormStack(activity);
        record(LifecycleType.onActivityDestroyed, activity);
    }

    private void record(LifecycleType lifecycleType, Activity activity) {
        QDLogger.println(lifecycleType + "(" + activity + ")");
        //LifecycleRecorder.record(lifecycleType, activity);
    }
}
