package cn.demomaster.qdrouter_library.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;

import androidx.annotation.AnimRes;
import androidx.annotation.AnimatorRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

import cn.demomaster.qdlogger_library.QDLogger;
import cn.demomaster.qdrouter_library.R;
import cn.demomaster.qdrouter_library.base.fragment.QuickFragment;
import cn.demomaster.qdrouter_library.base.fragment.ViewLifecycle;

/**
 * @author squirrel桓
 * @date 2019/1/10.
 * description：
 */
public class QuickFragmentHelper {
    public FragmentManager fragmentManager;
    List<Fragment> fragments;
    static FragmentManager.OnBackStackChangedListener onBackStackChangedListener = () -> {
        /*StringBuilder stringBuilder = new StringBuilder();
        int count = 0;
        if (fragmentManager != null) {
            count = fragmentManager.getBackStackEntryCount();
        }
        stringBuilder.append("onBackStackChanged fragment count:" + ",stack count" + count + "\n\r");
        for (Fragment fragment : ) {
            stringBuilder.append(fragment.getClass().getSimpleName() + ",hash:" + fragment.hashCode() + ",visiable:" + fragment.isVisible() + "\n\r");
        }
        QDLogger.println(stringBuilder.toString());*/
    };
    //int containerViewId;
    public QuickFragmentHelper(AppCompatActivity activity, int containerViewId) {
        //this.containerViewId = containerViewId;
        fragmentManager = activity.getSupportFragmentManager();
        //fragmentManager.removeOnBackStackChangedListener(onBackStackChangedListener);
        //fragmentManager.addOnBackStackChangedListener(onBackStackChangedListener);
        fragments = new ArrayList<>();
    }

    /**
     * 获取fragment列表，注意过滤掉fragment中嵌套的fragment。
     *
     * @return
     * @param fragmentActivity
     */
    public List<Fragment> getFragments(FragmentActivity fragmentActivity) {
        List<Fragment> fragmentList = new ArrayList<>();
        List<Fragment> fragmentList2 = fragmentManager.getFragments();
        for (Fragment fragment : fragmentList2) {
            if (fragment instanceof QuickFragment) {
                fragmentList.add(fragment);
            }
        }
        return fragmentList;
    }

    public boolean onKeyDown(Context context, int keyCode, KeyEvent event) {
        if (fragmentManager != null) {//&&
            int count = fragmentManager.getBackStackEntryCount();
            //QDLogger.d( "keyCode="+keyCode+",size=" + fragmentManager..size() + ",BackStackEntryCount=" + count);
            if (count > 0) {
                FragmentManager.BackStackEntry backStackEntry = fragmentManager.getBackStackEntryAt(count - 1);
                Fragment fragment = fragmentManager.findFragmentByTag(backStackEntry.getName());
                if (fragment instanceof ViewLifecycle) {
                    QDLogger.println("fragment=" + fragment.getClass().getName());
                    boolean ret = ((ViewLifecycle) fragment).onKeyDown(keyCode, event);
                    return ret;
                }else {
                    QDLogger.println("fragment2=" + fragment);
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
            transaction.setCustomAnimations(anima_enter, anima_exit, anima_popEnter, anima_popExit);
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
    public Fragment getCurrentFragment(FragmentActivity fragmentActivity) {
        List<Fragment> fragmentList = getFragments(fragmentActivity);
        if (fragmentList != null && fragmentList.size() > 0) {
            for(int i=fragmentList.size()-1;i>-1;i--){
                Fragment fragment = fragmentList.get(i);
                if(fragment.getActivity() == fragmentActivity ) {
                    return fragment;
                }
            }
        }
        return null;
    }

    public static void hideFragment(Fragment fragment) {
        QDLogger.i("hideFragment:" + fragment);
        FragmentTransaction transaction = fragment.getFragmentManager().beginTransaction();
        transaction.hide(fragment);
        transaction.commit();
    }

    public static void showFragment(Fragment fragment) {
        QDLogger.i("showFragment:" + fragment);
        FragmentTransaction transaction = fragment.getFragmentManager().beginTransaction();
        transaction.show(fragment);
        transaction.commit();
    }

    /* int animation1 = R.anim.slide_in_right;
     int animation2 = R.anim.slide_out_left;
     int animation3 = R.anim.slide_in_left;
     int animation4 = R.anim.slide_out_right;*/
    int anima_enter = R.anim.h5_slide_in_right;
    int anima_exit = R.anim.h5_slide_out_left;
    int anima_popEnter = R.anim.h5_slide_in_left;
    int anima_popExit = R.anim.h5_slide_out_right;

    /*private void startFragment(QuickFragment fragment) {
        QDLogger.i("containerViewId=" + containerViewId);
        startFragment(fragment, containerViewId);
    }*/

    private void startFragment(FragmentActivity fragmentActivity,QuickFragment fragment, int containerViewId) {
        //fragment.setFragmentHelper(this);
        startFragment(fragmentActivity,fragment, containerViewId, true);
    }

    private void startFragment(FragmentActivity fragmentActivity,QuickFragment fragment, int containerViewId, boolean withAnimation) {
        addFragment(fragmentActivity,fragment, containerViewId, withAnimation);
    }

    public void addFragment(FragmentActivity fragmentActivity,QuickFragment fragment, int containerViewId, boolean withAnimation) {
        fragment.containerViewId = containerViewId;
        Fragment currentFragment = getCurrentFragment(fragmentActivity);
        QDLogger.println("添加:" + getFragmentTag(fragment) + ",current=" + currentFragment+",containerViewId="+containerViewId);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (withAnimation) {
            //这里注意动画要优先添加到事物列表中，否则会出现动画异常
            if (fragmentManager.getBackStackEntryCount() > 0) {
                transaction.setCustomAnimations(anima_enter, anima_exit, anima_popEnter, anima_popExit);
            }else {
               // transaction.setCustomAnimations(R.anim.anim_null,R.anim.anim_null, anima_popEnter, anima_popExit);
            }
//            if (currentFragment == null) {
//                transaction.replace(containerViewId, fragment, getFragmentTag(fragment))//R.id.qd_fragment_content_view
//                        .addToBackStack(getFragmentTag(fragment));
//            }else {
                transaction.add(containerViewId, fragment, getFragmentTag(fragment))//R.id.qd_fragment_content_view
                        .addToBackStack(getFragmentTag(fragment));
            //}
//            transaction.add(containerViewId, fragment, getFragmentTag(fragment))//R.id.qd_fragment_content_view
//                    .addToBackStack(getFragmentTag(fragment));
            if (currentFragment != null) {//replace模式会有动画，add模式要通过hide方法才能有动画显示
                transaction.hide(currentFragment);
            }
            transaction.commitAllowingStateLoss();//.commit();会报错，commitAllowingStateLoss不会报错，activity状态可能会丢失
            if (currentFragment instanceof QuickFragment) {
                ((QuickFragment) currentFragment).doPause();
            }
        } else {
            transaction.add(containerViewId, fragment, getFragmentTag(fragment))//R.id.qd_fragment_content_view
                    .addToBackStack(getFragmentTag(fragment));
            transaction.commitAllowingStateLoss();//.commit();会报错，commitAllowingStateLoss不会报错，activity状态可能会丢失
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
        QDLogger.println("replaceFragment:" + getFragmentTag(fragment) + ",containerViewId=" + containerViewId);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            transaction.setCustomAnimations(anima_enter, anima_exit,
                    anima_popEnter, anima_popExit);
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

    public void destroy(Object obj) {
        if (obj instanceof Activity) {
            if (fragments != null) {
                fragments.clear();
            }
            if (fragmentManager != null) {
                fragmentManager.removeOnBackStackChangedListener(onBackStackChangedListener);
            }
        } else if (obj instanceof Fragment) {
            if (fragments != null) {
                fragments.remove(obj);
            }
        }
    }

    public void navigate(FragmentActivity context, QuickFragment fragment, int containerViewId, Intent intent) {
        navigateForResult(context, null, fragment, containerViewId, intent, 0);
    }
    public void navigate(FragmentActivity context, QuickFragment fragment, int containerViewId, Intent intent, boolean withAnimation) {
        navigateForResult(context, null, fragment, containerViewId, intent, 0,withAnimation);
    }
    public void navigateForResult(FragmentActivity context, ViewLifecycle qdFragmentInterface, QuickFragment fragment, int containerViewId, Intent intent, int requestCode) {
        navigateForResult(context, null, fragment, containerViewId, intent, 0, false);
    }

    public void navigateForResult(FragmentActivity context, ViewLifecycle qdFragmentInterface, QuickFragment fragment, int containerViewId, Intent intent, int requestCode, boolean withAnimation) {
        fragment.setIntent(intent);
        if (qdFragmentInterface != null) {
            (fragment).setRequestCode(qdFragmentInterface, requestCode);
        }
        startFragment(context,fragment, containerViewId,withAnimation);
    }

    //重定向
    public void redirect(FragmentActivity context, QuickFragment fragment, int containerViewId) {
        Fragment fragment1 = getCurrentFragment(context);
        //先移除当前的
        removeFragment(context, fragment1);
        // 注意这里 上上个可能会重新变为可视状态
        //再打开新的
        startFragment(context,fragment, containerViewId);
    }

    public Builder build(Context context, String classPath) {
        Builder builder = new Builder(context, classPath, this);
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
                if (!fragmentManager.isStateSaved()) {
                    fragmentManager.popBackStack(getFragmentTag(fragment), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //返回根fragment
    public void backToRoot(FragmentActivity fragmentActivity) {
        if (fragmentManager != null && fragmentManager.getBackStackEntryCount() > 1) {
            QDLogger.e("getBackStackEntryCount=" + fragmentManager.getBackStackEntryCount() + ",fragments=" + fragmentManager.getFragments().size());
            FragmentManager.BackStackEntry firstStackEntry = fragmentManager.getBackStackEntryAt(0);
            /*popBackStack(String tag,int flags)
            tag可以为null或者相对应的tag，flags只有0和1(POP_BACK_STACK_INCLUSIVE)两种情况
            如果tag为null，flags为0时，弹出回退栈中最上层的那个fragment。
            如果tag为null ，flags为1时，弹出回退栈中所有fragment。
            如果tag不为null，那就会找到这个tag所对应的fragment，flags为0时，弹出该
            fragment以上的Fragment，如果是1，弹出该fragment（包括该fragment）以*/
            try {
                if (!fragmentManager.isStateSaved()) {
                    fragmentManager.popBackStack(firstStackEntry.getId(), 0);
                }
            } catch (Exception e) {
                QDLogger.e(e);
            }
            Fragment currentFragment = fragmentManager.findFragmentByTag(firstStackEntry.getName());
            if (currentFragment instanceof QuickFragment) {
                QDLogger.i("强制 resume:" + getFragmentTag(currentFragment));
                ((QuickFragment) currentFragment).doResume();
            }
        } else {
            QDLogger.i("当前已是根fragment:" + getCurrentFragment(fragmentActivity));
        }
    }

    /*
     * 回退到某个fragment
     *
     * @param
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

    /**
     * 弹出其他栈，仅保留某class集合内的activity
     *
     * @param
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
    public void onActivityResult(FragmentActivity fragmentActivity,int requestCode, int resultCode, Intent data) {
        Fragment fragment = getCurrentFragment(fragmentActivity);
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
        QuickFragment fragment;
        boolean withAnimation = true;
        int enter = R.anim.h5_slide_in_right;
        int exit =R.anim.h5_slide_out_left;
        int popEnter = R.anim.h5_slide_in_left;
        int popExit = R.anim.h5_slide_out_right;
        Intent intent;

        
        public Builder(Context context, String classPath, QuickFragmentHelper fragmentHelper) {
            this.fragmentHelper = fragmentHelper;
            this.context = (FragmentActivity) context;
            this.classPath = classPath;
        }

        public Builder(Context context, QuickFragment fragment, QuickFragmentHelper fragmentHelper) {
            this.fragmentHelper = fragmentHelper;
            this.context = (FragmentActivity) context;
            this.fragment = fragment;
        }

        public Builder setClassPath(String classPath) {
            this.classPath = classPath;
            return this;
        }

        public Builder putExtras(Bundle extras) {
            this.bundle = extras;
            return this;
        }
        
        public Builder setIntent(Intent intent) {
            this.intent = intent;
            return this;
        }

        public Builder putExtra(String key, Object objValue) {
            return this;
        }

        public Builder setContainerViewId(int containerViewId) {
            this.containerViewId = containerViewId;
            return this;
        }

        public Builder setWithAnimation(boolean withAnimation) {
            this.withAnimation = withAnimation;
            return this;
        }

        public Builder setCustomAnimations(@AnimatorRes @AnimRes int enter,
                            @AnimatorRes @AnimRes int exit, @AnimatorRes @AnimRes int popEnter,
                            @AnimatorRes @AnimRes int popExit){
            this.exit = exit;
            this.enter = enter;
            this.popEnter = popEnter;
            this.popExit = popExit;
            return this;
        }

        //跳转到指定页面
        public void redirect() {
            try {
                if (this.fragment == null && !TextUtils.isEmpty(classPath)) {
                    Class fragmentClass = Class.forName(classPath);
                    fragment = (QuickFragment) fragmentClass.newInstance();
                }
                if(intent==null) {
                    intent = new Intent();
                }
                intent.putExtras(bundle);
                fragment.setArguments(bundle);
                fragment.setIntent(intent);
                fragment.setArguments(bundle);
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
                if (this.fragment == null && !TextUtils.isEmpty(classPath)) {
                    Class fragmentClass = Class.forName(classPath);
                    fragment = (QuickFragment) fragmentClass.newInstance();
                }
                if(intent==null) {
                    intent = new Intent();
                }
                if (bundle != null) {
                    intent.putExtras(bundle);
                    fragment.setArguments(bundle);
                }
                fragmentHelper.anima_enter = this.enter;
                fragmentHelper.anima_exit = this.exit;
                fragmentHelper.anima_popEnter = this.popEnter;
                fragmentHelper.anima_popExit = this.popExit;
                if (isForResult) {
                    fragmentHelper.navigateForResult(context, viewLifecycle, fragment, containerViewId, intent, requestCode, withAnimation);
                } else {
                    fragmentHelper.navigate(context, fragment, containerViewId, intent, withAnimation);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
