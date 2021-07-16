package cn.demomaster.qdrouter_library.paper;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

public abstract class Paper implements PaperInterface {


    private Context mContext;
    public Context getContext() {
        return mContext;
    }

    AppCompatActivity activity;
    public Activity getActivity() {
        return activity;
    }
    Bundle arguments;
    public void setArguments(Bundle arguments){
        this.arguments = arguments;
    }
    public Bundle getArguments(){
        return null;
    }

    public Resources getResources(){
       return activity.getResources();
    }
    public String getString(int id){
       return activity.getResources().getString(id);
    }
    public LayoutInflater getLayoutInflater(){
        return  activity.getLayoutInflater();
    }

    public FragmentManager getFragmentManager(){
        return activity.getSupportFragmentManager();
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults){}

    public void onResume() {
        Log.i(this.getClass().getSimpleName(),"【"+this.hashCode()+"] onResume");
    }
    public void onPause() {
        Log.i(this.getClass().getSimpleName(),"【"+this.hashCode()+"] onPause");
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {

    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return null;
    }

    public void onStop() {
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    PaperManager myPayerManager;
    public PaperManager getMyPayerManager() {
        return myPayerManager;
    }

    public void setPayerManager(PaperManager payerManager) {
        myPayerManager = payerManager;
    }
    private View mView;
    private ViewGroup mContainerView;
    void performCreateView(ViewGroup containerView) {
        mContainerView = containerView;
        View mView = onCreatView(getLayoutInflater(),containerView);
        setContainerView(mView);
        onResume();
    }

    public void setContainerView(View view){
        if(view!=null && view.getParent()==null) {
            mView = view;
            mContainerView.addView(mView);
        }
    }

    public void onDestroy() {

    }
    public void onDestroyView(){
        Log.i(this.getClass().getSimpleName(),"【"+this.hashCode()+"] onDestroyView");
        if(mView!=null) {
            ViewParent viewParent = mView.getParent();
            if (viewParent != null) {
                ((ViewGroup) viewParent).removeView(mView);
            }
        }
    }

    public void initLifecycle() {
        ViewGroup containerView = myPayerManager.getContainerView();
        mContext = containerView.getContext();
        activity = (AppCompatActivity) mContext;
        Bundle savedInstanceState = new Bundle();
        onCreate(savedInstanceState);
        performCreateView(containerView);
    }

    public View findViewById(int id){
       return mView.findViewById(id);
    }
}
