package cn.demomaster.qdrouter_library.manager;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import cn.demomaster.qdlogger_library.QDLogger;

public class QuickFragmentLifecycleCallbacks extends FragmentManager.FragmentLifecycleCallbacks {

    @Override
    public void onFragmentAttached(FragmentManager fm, Fragment fragment, Context context) {
        super.onFragmentAttached(fm, fragment, context);
        QDLogger.println("onFragmentAttached: " + fragment);
    }

    @Override
    public void onFragmentDetached(FragmentManager fm, Fragment fragment) {
        super.onFragmentDetached(fm, fragment);
        QDLogger.println("onFragmentDetached: " + fragment);
    }
}