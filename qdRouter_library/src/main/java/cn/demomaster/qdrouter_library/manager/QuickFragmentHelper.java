package cn.demomaster.qdrouter_library.manager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

import cn.demomaster.qdlogger_library.QDLogger;
import cn.demomaster.qdrouter_library.R;
import cn.demomaster.qdrouter_library.base.OnReleaseListener;
import cn.demomaster.qdrouter_library.base.fragment.NavigationInterface;
import cn.demomaster.qdrouter_library.base.fragment.QuickFragment;
import cn.demomaster.qdrouter_library.base.fragment.ViewLifecycle;

/**
 * @author squirrel桓
 * @date 2019/1/10.
 * description：
 */
public class QuickFragmentHelper implements NavigationInterface, OnReleaseListener {
    AppCompatActivity activity;
    public static FragmentManager fragmentManager;
    List<Fragment> fragments;
    static FragmentManager.OnBackStackChangedListener onBackStackChangedListener = new FragmentManager.OnBackStackChangedListener() {
        @Override
        public void onBackStackChanged() {
            /*StringBuilder stringBuilder = new StringBuilder();
            int count = 0;
            if (fragmentManager != null) {
                count = fragmentManager.getBackStackEntryCount();
            }
            stringBuilder.append("onBackStackChanged fragment count:" + getFragments().size() + ",stack count" + count + "\n\r");
            for (Fragment fragment : getFragments()) {
                stringBuilder.append(fragment.getClass().getSimpleName() + ",hash:" + fragment.hashCode() + ",visiable:" + fragment.isVisible() + "\n\r");
            }
            QDLogger.println(stringBuilder.toString());*/
        }
    };
    public QuickFragmentHelper(AppCompatActivity activity) {
        this.activity = activity;
        fragmentManager = activity.getSupportFragmentManager();
        fragmentManager.removeOnBackStackChangedListener(onBackStackChangedListener);
        fragmentManager.addOnBackStackChangedListener(onBackStackChangedListener);
        fragments = new ArrayList<>();
    }

    /**
     * 获取fragment列表，注意过滤掉fragment中嵌套的fragment。
     *
     * @return
     */
    public List<Fragment> getFragments() {
        List<Fragment> fragmentList = new ArrayList<>();
        if (getFragmentManager().getFragments() != null) {
            List<Fragment> fragmentList2 = getFragmentManager().getFragments();
            for (Fragment fragment : fragmentList2) {
                if (fragment instanceof QuickFragment) {
                    fragmentList.add(fragment);
                }
            }
        }
        return fragmentList;
    }

    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }

    public boolean onKeyDown(Context context, int keyCode, KeyEvent event) {
        if (fragmentManager!=null&&keyCode == KeyEvent.KEYCODE_BACK) {
            QDLogger.d("FragmentHelper", "size=" + getFragments().size() + ",BackStackEntryCount=" + getFragmentManager().getBackStackEntryCount());
            int count = fragmentManager.getBackStackEntryCount();
            if (count > 0) {
                FragmentManager.BackStackEntry backStackEntry = fragmentManager.getBackStackEntryAt(count-1);
                Fragment fragment = fragmentManager.findFragmentByTag(backStackEntry.getName());
                if (fragment != null && fragment instanceof ViewLifecycle) {
                    boolean ret = ((ViewLifecycle) fragment).onKeyDown(keyCode, event);
                    return ret;
                }
            }
        }
        return false;
    }

    //判断fragment 是否是根fragment
    public boolean isRootFragment(Fragment fragment) {
        if (fragment != null && fragmentManager != null && fragmentManager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry backStackEntry = fragmentManager.getBackStackEntryAt(0);
            Fragment fragment1 = fragmentManager.findFragmentByTag(backStackEntry.getName());
            return fragment.equals(fragment1);
        }
        return false;
    }

    public void removeFragment(FragmentActivity activity, Fragment fragment) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            transaction.setCustomAnimations(animation1, animation2, animation3, animation4);
        }
        transaction.remove(fragment);
        transaction.commit();
        //fragment 被移除后 回退栈种可能还存在
        popBackStack(fragment);
    }

    /**
     * 获取当前激活状态的fragment，即最顶层的fragment活动页面
     *
     * @return
     */
    public Fragment getCurrentFragment() {
        List<Fragment> fragmentList = getFragments();
        if (fragmentList != null && fragmentList.size() > 0) {
            return fragmentList.get(fragmentList.size() - 1);
        }
        return null;
    }

    public void hideFragment(Fragment fragment) {
        QDLogger.d("hideFragment:" + fragment);
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.hide(fragment);
        transaction.commit();
    }

    int containerViewId;
    /* int animation1 = R.anim.slide_in_right;
     int animation2 = R.anim.slide_out_left;
     int animation3 = R.anim.slide_in_left;
     int animation4 = R.anim.slide_out_right;*/
    int animation1 = R.anim.h5_slide_in_right;
    int animation2 = R.anim.h5_slide_out_left;
    int animation3 = R.anim.h5_slide_in_left;
    int animation4 = R.anim.h5_slide_out_right;
    public void startFragment(QuickFragment fragment) {
        startFragment(fragment, containerViewId);
    }
    public void startFragment(QuickFragment fragment, int containerViewId) {
        fragment.setFragmentHelper(this);
        this.containerViewId = containerViewId;
        addFragment(fragment, containerViewId);
    }

    public void addFragment(QuickFragment fragment, int containerViewId) {
        Fragment currentFragment = getCurrentFragment();
        QDLogger.i("添加:" + getFragmentTag(fragment) + ",Fragment Size=" + getFragments().size());
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //这里注意动画要优先添加到事物列表中，否则会出现动画异常
        if (fragmentManager.getBackStackEntryCount() > 0) {
            transaction.setCustomAnimations(animation1, animation2, animation3, animation4);
        }
        transaction.add(containerViewId, fragment, getFragmentTag(fragment))//R.id.qd_fragment_content_view
                .addToBackStack(getFragmentTag(fragment));
        if (currentFragment != null) {//replace模式会有动画，add模式要通过hide方法才能有动画显示
            transaction.hide(currentFragment);
        }
        transaction.commitAllowingStateLoss();//.commit();会报错，commitAllowingStateLoss不会报错，activity状态可能会丢失
        if (currentFragment != null && currentFragment instanceof QuickFragment) {
            ((QuickFragment) currentFragment).doPause();
        }
    }

    /**
     * 拼接fragment的Tag
     *
     * @param fragment
     * @return
     */
    private String getFragmentTag(Fragment fragment) {
        return fragment.getClass().getSimpleName() + "-" + fragment.hashCode();
    }

    public void replaceFragment(Fragment fragment, int containerViewId) {
        QDLogger.println("replaceFragment:" + getFragmentTag(fragment) + ",containerViewId=" + containerViewId + ",getFragments()=" + getFragments().size());
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            transaction.setCustomAnimations(animation1, animation2,
                    animation3, animation4);
        }
        transaction.replace(containerViewId, fragment, getFragmentTag(fragment))//R.id.qd_fragment_content_view
                .addToBackStack(getFragmentTag(fragment))
                .commit();
    }

 /*   public static View getContentView(Activity activity) {
        ViewGroup view = (ViewGroup) activity.getWindow().getDecorView();
        FrameLayout content = view.findViewById(android.R.id.content);
        return content.getChildAt(0);
    }*/

    public void onDestroy() {
        if (fragments != null) {
            fragments = null;
        }
        if (fragmentManager != null) {
            fragmentManager.removeOnBackStackChangedListener(this);
            fragmentManager = null;
        }
    }

    @Override
    public void navigate(FragmentActivity context, QuickFragment fragment, int containerViewId, Intent intent) {
        navigateForResult(context, null, fragment, containerViewId, intent, 0);
    }

    @Override
    public void navigateForResult(FragmentActivity context, ViewLifecycle qdFragmentInterface, QuickFragment fragment, int containerViewId, Intent intent, int requestCode) {
        fragment.setIntent(intent);
        if (qdFragmentInterface != null) {
            (fragment).setRequestCode(qdFragmentInterface, requestCode);
        }
        startFragment(fragment, containerViewId);
    }

    //重定向
    @Override
    public void redirect(FragmentActivity context, QuickFragment fragment, int containerViewId) {
        Fragment fragment1 = getCurrentFragment();
        //先移除当前的
        removeFragment(context, fragment1);
        // 注意这里 上上个可能会重新变为可视状态
        //再打开新的
        startFragment(fragment, containerViewId);
    }

    Builder builder;
    public Builder build(Context context, String classPath) {
        if (builder == null) {
            builder = new Builder(context, classPath, this);
        } else {
            builder.setClassPath(classPath);
        }
        return builder;
    }

    /*public void OnBackPressed() {
        //QDLogger.println("OnBackPressed1 " + getCurrentFragment());
        if (!fragmentManager.isStateSaved() && fragmentManager.popBackStackImmediate()) {
            Fragment currentFragment = getCurrentFragment();
            if (currentFragment != null && currentFragment instanceof QuickFragment) {
                QDLogger.i("强制 resume:" + getFragmentTag(currentFragment));
                ((QuickFragment) currentFragment).doResume();
            }
            return;
        }
    }*/

    public void popBackStack(Fragment fragment) {
        if (fragment != null) {
            try {
                if(!fragmentManager.isStateSaved()) {
                    fragmentManager.popBackStack(getFragmentTag(fragment), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    //返回根fragment
    public void backToRoot() {
        if (fragmentManager != null && fragmentManager.getBackStackEntryCount() > 1) {
            QDLogger.e("getBackStackEntryCount="+fragmentManager.getBackStackEntryCount()+",fragments="+getFragments().size());
            FragmentManager.BackStackEntry firstStackEntry = fragmentManager.getBackStackEntryAt(0);
            /*popBackStack(String tag,int flags)
            tag可以为null或者相对应的tag，flags只有0和1(POP_BACK_STACK_INCLUSIVE)两种情况
            如果tag为null，flags为0时，弹出回退栈中最上层的那个fragment。
            如果tag为null ，flags为1时，弹出回退栈中所有fragment。
            如果tag不为null，那就会找到这个tag所对应的fragment，flags为0时，弹出该
            fragment以上的Fragment，如果是1，弹出该fragment（包括该fragment）以*/
            try {
                if(!fragmentManager.isStateSaved()) {
                    fragmentManager.popBackStack(firstStackEntry.getId(), 0);
                }
            }catch (Exception e){
                QDLogger.e(e);
            }
            Fragment currentFragment = fragmentManager.findFragmentByTag(firstStackEntry.getName());
            if (currentFragment != null && currentFragment instanceof QuickFragment) {
                QDLogger.i("强制 resume:" +getFragmentTag(currentFragment));
                ((QuickFragment) currentFragment).doResume();
            }
        }else {
            QDLogger.i("当前已是根fragment:"+getCurrentFragment());
        }
    }

    /**
     * 回退到某个fragment
     *
     * @param fragmentClass
     */
    /*public void backTo1(Class fragmentClass) {
        int count = fragmentManager.getBackStackEntryCount();
        for (int i = count - 1; i >= 0; i--) {
            FragmentManager.BackStackEntry backStackEntry = fragmentManager.getBackStackEntryAt(i);
            if(backStackEntry!=null) {
                //QDLogger.d(backStackEntry.getName());
                if (backStackEntry.getName().startsWith(fragmentClass.getSimpleName() + "-")) {
                    QuickFragment currentFragment = (QuickFragment) fragmentManager.findFragmentByTag(backStackEntry.getName());
                    if (currentFragment != null) {
                        QDLogger.i("强制 resume:" + currentFragment.getClass().getSimpleName() + "-" + currentFragment.hashCode());
                        currentFragment.doResume();
                    }
                    return;
                } else {
                    //QDLogger.println("移除fragment " + backStackEntry);
                    fragmentManager.popBackStack(backStackEntry.getId(), 1);
                }
            }
        }
    }*/

    /*释放*/
    @Override
    public void onRelease() {

    }

    /**
     * 弹出其他栈，仅保留某class集合内的activity
     *
     * @param classList 要保留的 activity 类
     */
    /*public void popOtherFragmentExceptList(List<Class> classList) {
        if (classList == null) {
            return;
        }
        String classStr = "";
        for (int i = 0; i < classList.size(); i++) {
            classStr += classList.get(i).getName() + ",";
        }
        int count = fragmentManager.getBackStackEntryCount();
        for (int i = 0; i < count; i++) {
            FragmentManager.BackStackEntry backStackEntry = fragmentManager.getBackStackEntryAt(i);
            QDLogger.d("backStackEntry name=" + backStackEntry.getName());
            if (!classStr.contains("." + backStackEntry.getName())) {
                QDLogger.d("移除fragment " + backStackEntry);
                fragmentManager.popBackStack(backStackEntry.getId(), 1);
            }
        }
    }*/
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Fragment fragment = getCurrentFragment();
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

/*
    public void onActivityResult(QDFragmentInterface fragment,int mRequestCode, int mResultCode, Intent mResultData) {
        //onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        fragment.onActivityResult();
    }*/

    public static class Builder {
        FragmentActivity context;
        int containerViewId = android.R.id.content;
        Bundle bundle;
        String classPath;
        QuickFragmentHelper fragmentHelper;

        public Builder(Context context, String classPath, QuickFragmentHelper fragmentHelper) {
            this.fragmentHelper = fragmentHelper;
            this.context = (FragmentActivity) context;
            this.classPath = classPath;
        }

        public Builder setClassPath(String classPath) {
            this.classPath = classPath;
            return this;
        }

        public Builder putExtras(Bundle extras) {
            this.bundle = extras;
            return this;
        }

        public Builder putExtra(String key, Object objValue) {
            return this;
        }

        public Builder setContainerViewId(int containerViewId) {
            this.containerViewId = containerViewId;
            return this;
        }

        //跳转到指定页面
        public void redirect() {
            try {
                Class fragmentClass = Class.forName(classPath);
                QuickFragment fragment = (QuickFragment) fragmentClass.newInstance();
                Intent intent = new Intent();
                intent.putExtras(bundle);
                fragment.setIntent(intent);
                fragmentHelper.redirect(context, fragment, containerViewId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //跳转到指定页面
        public void navigation() {
            navigation(null, false, 0);
        }

        //跳转到指定fragment
        public void navigation(ViewLifecycle viewLifecycle, int requestCode) {
            navigation(viewLifecycle, true, requestCode);
        }

        public void navigation(ViewLifecycle viewLifecycle, boolean isForResult, int requestCode) {
            try {
                Class fragmentClass = Class.forName(classPath);
                QuickFragment fragment = (QuickFragment) fragmentClass.newInstance();
                Intent intent = new Intent();
                if (bundle != null) {
                    intent.putExtras(bundle);
                }
                if (isForResult) {
                    fragmentHelper.navigateForResult(context, viewLifecycle, fragment, containerViewId, intent, requestCode);
                } else {
                    fragmentHelper.navigate(context, fragment, containerViewId, intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
