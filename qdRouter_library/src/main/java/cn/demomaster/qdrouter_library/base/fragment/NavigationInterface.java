package cn.demomaster.qdrouter_library.base.fragment;

import android.content.Intent;

import androidx.fragment.app.FragmentActivity;

public interface NavigationInterface {
    void navigate(FragmentActivity context, QuickFragment fragment, int containerViewId, Intent intent);

    void navigateForResult(FragmentActivity context, ViewLifecycle qdFragmentInterface, QuickFragment fragment, int containerViewId, Intent intent, int requestCode);

    void redirect(FragmentActivity context, QuickFragment fragment, int containerViewId);
    
   }
