package cn.demomaster.qdrouter_library.base.fragment;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import org.greenrobot.eventbus.EventBus;

import cn.demomaster.qdlogger_library.QDLogger;
import cn.demomaster.qdrouter_library.R;
import cn.demomaster.qdrouter_library.actionbar.ActionBarTool;
import cn.demomaster.qdrouter_library.base.activity.QuickActivity;
import cn.demomaster.qdrouter_library.manager.QDActivityManager;
import cn.demomaster.qdrouter_library.manager.QuickFragmentHelper;
import cn.demomaster.qdrouter_library.view.ImageTextView;

import static android.view.KeyEvent.ACTION_DOWN;
import static android.view.KeyEvent.ACTION_UP;
import static android.view.KeyEvent.KEYCODE_BACK;

/**
 * Created by Squirrel桓 on 2019/1/3.
 */
public abstract class QuickFragment extends Fragment implements ViewLifecycle {

    public AppCompatActivity mContext;

    //是否使用自定义导航
    @Override
    public boolean isUseActionBarLayout() {
        return true;
    }

    @Override
    public boolean isTransparencyBar() {
        return true;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = (AppCompatActivity) this.getContext();
        QDLogger.i("onCreate$" + hashCode() + "-" + getClass().getSimpleName());
        //StatusBarUtil.transparencyBar(new WeakReference<>(mContext));
    }
    private View fragmentView;
    public <T extends View> T findViewById(int id) {
        if (fragmentView == null) {
            return null;
        }
        return fragmentView.findViewById(id);
    }
    //private int headlayoutResID = R.layout.qd_activity_actionbar_common;
    public int getHeadlayoutResID() {
        return R.layout.qd_activity_actionbar_common;
    }

    //是否可以被点击 false点击穿透
    private boolean clickable = true;

    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

    private String mTitle;
    public void setTitle(String title) {
        this.mTitle = title;
        if (getActionBarTool() != null) {
            getActionBarTool().setTitle(getTitle());
        }
    }

    public String getTitle() {
        return mTitle;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view = onGenerateView(inflater, container, savedInstanceState);
        if (isUseActionBarLayout()) {
            /*ActionBarTool.Builder builder = new ActionBarTool.Builder(getActivity());
            fragmentView = builder.setContentView(view)
                    .setActionView(getHeadlayoutResID())
                    .inflateView();*/
            getActionBarTool().setContentView(view);
            getActionBarTool().setActionView(getHeadlayoutResID());
            //生成最終的view
            fragmentView = getActionBarTool().inflateView();
            getActionBarTool().setTitle(getTitle());
            ImageTextView imageTextView = getActionBarTool().getLeftView();
            if (imageTextView != null) {
                imageTextView.setOnClickListener(v -> onClickBack());
            }
            setThemeColor();
        } else {
            fragmentView = view;
        }
        fragmentView.setClickable(clickable);// 防止点击穿透，底层的fragment响应上层点击触摸事件
        return fragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        QDLogger.d("onViewCreated");
        initCreatView(view);
    }

    public void setThemeColor() {
        if (fragmentView.getBackground() == null) {//设置默认颜色
            //QDLogger.e("getBackground="+fragmentView.getBackground());
            //ColorDrawable colorDrawable = (ColorDrawable) fragmentView.getBackground();
            TypedArray array = getContext().getTheme().obtainStyledAttributes(new int[]{
                    android.R.attr.colorBackground,
                    android.R.attr.textColorPrimary,
                    android.R.attr.colorPrimary,
                    android.R.attr.colorPrimaryDark,
                    android.R.attr.colorAccent,
            });
            int backgroundColor = array.getColor(0, 0x00FF00FF);
            fragmentView.setBackgroundColor(backgroundColor);
            /*int textColor = array.getColor(1, 0xFF00FF);
            int colorPrimary = array.getColor(2, getResources().getColor(R.color.colorPrimary));
            int colorPrimaryDark = array.getColor(3, getResources().getColor(R.color.colorPrimaryDark));
            int colorAccent = array.getColor(4, getResources().getColor(R.color.colorAccent));*/
            array.recycle();
        }
        /*else if(fragmentView.getBackground() instanceof ColorDrawable){
            QDLogger.e("getBackground ColorDrawable="+fragmentView.getBackground());
        }*/
    }

    public void initCreatView(View mView) {
        initView(mView);
    }

    public void onClickBack() {
        //模拟点击
        int eventCode = KEYCODE_BACK;//ACTION_DOWN
        long now = SystemClock.uptimeMillis();
        KeyEvent down = new KeyEvent(now, now, ACTION_DOWN, eventCode, 0);
        down.setSource(257);
        down = KeyEvent.changeFlags(down,8);

        if(getActivity()==null){
            return;
        }
        getActivity().dispatchKeyEvent(down);
        if(getActivity()==null){
            return;
        }
        now = SystemClock.uptimeMillis();
        KeyEvent up = new KeyEvent(now, now, ACTION_UP, eventCode, 0);
        up.setSource(257);
        up = KeyEvent.changeFlags(down,520);
        getActivity().dispatchKeyEvent(up);
        //getActivity().onKeyUp(eventCode, up);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        QDLogger.println("$" + hashCode() + "-" + getClass().getSimpleName() + ":" + (hidden ? "隐藏" : "显示") + ",isVisible=" + isVisible());
        if (!hidden && isAdded() && getActivity() != null) {
            doResume();
        }
    }

    private ActionBarTool actionBarTool;

    //获取自定义导航
    public ActionBarTool getActionBarTool() {
        if (actionBarTool == null) {
            actionBarTool = new ActionBarTool(this);
        }
        return actionBarTool;
    }

    QuickFragmentHelper mFragmentHelper;

    public void setFragmentHelper(QuickFragmentHelper fragmentHelper) {
        mFragmentHelper = fragmentHelper;
    }

    public QuickFragmentHelper getFragmentHelper() {
        if (mFragmentHelper == null&&getActivity()!=null&&getActivity() instanceof QuickActivity) {
            mFragmentHelper = ((QuickActivity)getActivity()).getFragmentHelper();
        }
        return mFragmentHelper;
    }

    public void startFragment(QuickFragment fragment, int parentId,Intent intent) {
        getFragmentHelper().navigate(mContext,fragment, parentId,intent);
    }

    int mResultCode;
    Intent mResultData;
    public void setResult(int resultCode, Intent data) {
        mResultCode = resultCode;
        mResultData = data;
    }

    int mRequestCode;
    ViewLifecycle fromFragment;

    public void setRequestCode(ViewLifecycle qdFragmentInterface, int requestCode) {
        mRequestCode = requestCode;
        fromFragment = qdFragmentInterface;
    }

    Intent mIntent;

    @Override
    public void setIntent(Intent intent) {
        mIntent = intent;
        if(intent!=null) {
            setArguments(intent.getExtras());
        }
    }

    public Intent getIntent() {
        return mIntent;
    }

    @Override
    public void onStart() {
        super.onStart();
        //QDLogger.i("onStart$"+hashCode()+"-"+getClass().getSimpleName());
    }

    @Override
    public void onFragmentStart() {

    }

    boolean isResumed;

    @Override
    public void doResume() {
        QDLogger.i("doResume isResumed=" + isResumed + ",hide=" + isHidden());
        if (!isResumed) {
            onResume();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //QDLogger.i("onResume$"+hashCode()+"-"+getClass().getSimpleName()+",isHidden="+isHidden()+",isVisible="+isVisible());
        isResumed = true;
        isPaused = false;
        if (!isHidden()) {
            onFragmentResume();
        }
    }

    @Override
    public void onFragmentResume() {
        QDLogger.i("onFragmentResume$" + hashCode() + "-" + getClass().getSimpleName());
    }

    boolean isPaused;

    @Override
    public void doPause() {
        if (!isPaused) {
            onPause();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //QDLogger.i( "onPause$"+hashCode()+"-"+getClass().getSimpleName()+",isHidden="+isHidden()+",isVisible="+isVisible());
        isPaused = true;
        isResumed = false;
        if (!isHidden()) {
            onFragmentPause();
        }
    }

    @Override
    public void onFragmentPause() {
        QDLogger.i("onFragmentPause$" + hashCode() + "-" + getClass().getSimpleName());
    }

    @Override
    public void onStop() {
        super.onStop();
        //QDLogger.i("onStop$"+hashCode()+"-"+getClass().getSimpleName()+",isHidden="+isHidden()+",isVisible="+isVisible());
        if (!isHidden()) {
            onFragmentStop();
        }
    }

    @Override
    public void onFragmentStop() {
        QDLogger.i("onFragmentStop$" + hashCode() + "-" + getClass().getSimpleName());
    }

    public void finish() {
        // onClickBack();
        getFragmentHelper().popBackStack(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        QDLogger.i("onDetach$" + hashCode() + "-" + getClass().getSimpleName());
        if (fromFragment != null) {
            fromFragment.onActivityResult(mRequestCode, mResultCode, mResultData);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        QDLogger.i("onDestroyView$" + hashCode() + "-" + getClass().getSimpleName());
        QDActivityManager.destroyObject(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        QDActivityManager.destroyObject(this);
    }

    public boolean isRootFragment() {
        return getFragmentHelper().isRootFragment(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        QDLogger.e(getClass().getName()+"-onKeyDown");
        if (keyCode == KeyEvent.KEYCODE_BACK) {//默认只处理回退事件
            if (!isRootFragment()) {
                //getActivity().onBackPressed();
                finish();
            } else {//已经是根fragment了
                QDLogger.e(getClass().getName()+"-已经是根fragment了");
            }
            return true;//当返回true时，表示已经完整地处理了这个事件，并不希望其他的回调方法再次进行处理，而当返回false时，表示并没有完全处理完该事件，更希望其他回调方法继续对其进行处理
        } else {//其他事件自行处理
            return false;
        }
    }
}
