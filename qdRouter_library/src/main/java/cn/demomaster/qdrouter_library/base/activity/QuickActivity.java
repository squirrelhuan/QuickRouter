package cn.demomaster.qdrouter_library.base.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;

import cn.demomaster.qdlogger_library.QDLogger;
import cn.demomaster.qdrouter_library.R;
import cn.demomaster.qdrouter_library.actionbar.ActionBarLayout;
import cn.demomaster.qdrouter_library.actionbar.ActionBarTool;
import cn.demomaster.qdrouter_library.actionbar.ActionBarToolImp;
import cn.demomaster.qdrouter_library.base.fragment.QuickFragment;
import cn.demomaster.qdrouter_library.manager.QuickFragmentHelper;
import cn.demomaster.qdrouter_library.manager.QuickRleaser;
import cn.demomaster.qdrouter_library.util.DisplayUtil;
import cn.demomaster.qdrouter_library.util.QdThreadHelper;
import cn.demomaster.qdrouter_library.util.StatusBarUtil;

public class QuickActivity extends AppCompatActivity implements QDActivityInterface {
    public static String TAG = QuickActivity.class.getName();
    public QuickActivity mContext;
    
    @Override
    public void onClickBack() {
        finish();
    }
    ///是否使用自定义导航
    @Override
    public boolean isUseActionBarLayout() {
        return true;
    }
    
    @Override
    public boolean isTransparencyBar() {
        return true;
    }
    
    /**
     * 设置自定义导航栏
     * @return
     */
    public View getHeaderlayout() {
        return View.inflate(this,R.layout.qd_activity_actionbar_common,null);
    }
    
    private ActionBarTool actionBarTool;
    //获取自定义导航
    public ActionBarTool getActionBarTool() {
        if (actionBarTool == null&&mActionBarLayout!=null) {
            actionBarTool = new ActionBarToolImp(this,mActionBarLayout);
        }
        return actionBarTool;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        actionBarTool = null;
        fragmentHelper = null;
        if(isTransparencyBar()){
            StatusBarUtil.transparencyBar(new WeakReference<>(mContext));
        }
        super.onCreate(savedInstanceState);
        QDLogger.i("onCreate-"+getClass().getSimpleName()+"-"+hashCode());

        /*if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }*/
        //setSupportActionBar(new QuickToolbar(this));
    }
    
    @Override
    public void setContentView(int layoutResID) {
        View view = getLayoutInflater().inflate(layoutResID, null);
        setContentView(view);
    }

    @Override
    public void setContentView(View view) {
        View view1 = decorateView(view);
        super.setContentView(view1);
        //bind在setContentView之后 ButterKnife.bind(this);
        initContentView();
    }

    ActionBarLayout mActionBarLayout;
    /**
     * 对传递过来的view 再次包装
     * @param view
     * @return
     */
    public View decorateView(View view) {
        if (isUseActionBarLayout()) {//是否使用自定义导航栏
            ActionBarLayout.Builder builder = getActivityLayoutBuilder(mContext);
            mActionBarLayout = builder
                    .setContentView(view)
                    .creat();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                mActionBarLayout.setId(View.generateViewId());
            }
            getActionBarTool().setLeftOnClickListener(v -> finish());
            return mActionBarLayout;
        } else {
            return view;
        }
    }

    /**
     * 自定义导航构建者
     * @param mContext
     * @return
     */
    public ActionBarLayout.Builder getActivityLayoutBuilder(QuickActivity mContext) {
        ActionBarLayout.Builder builder = new ActionBarLayout.Builder(mContext)
                .setActionBarView(getHeaderlayout())
                .setHasStatusBar(true)
                .setStatusHeight(DisplayUtil.getStatusBarHeight(mContext))
                .setContentViewPaddingTop(ActionBarLayout.PaddingWith.none)
                .setMixStatusActionBar(true);
        return builder;
    }

    public void initContentView() {
        
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //QDLogger.i("onNewIntent-"+getClass().getSimpleName()+"-"+hashCode());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        QDLogger.i("onSaveInstanceState-"+getClass().getSimpleName()+"-"+hashCode());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        QDLogger.i("onRestoreInstanceState-"+getClass().getSimpleName()+"-"+hashCode());
    }
    @Override
    protected void onStart() {
        super.onStart();
        QDLogger.i("onStart-"+getClass().getSimpleName()+"-"+hashCode());
    }

    @Override
    protected void onResume() {
        super.onResume();
        QDLogger.i("onResume-"+getClass().getSimpleName()+"-"+hashCode());
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        if (actionBarTool != null) {
            actionBarTool.setTitle(title);
        }
    }

    @Override
    public void finish() {
        //QDLogger.e("isMainThread:"+QdThreadHelper.isMainThread());
        //QuickRleaser.release(this);
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(actionBarTool!=null){
            actionBarTool.onRelease(this);
        }
        QDLogger.i("onDestroy-"+getClass().getSimpleName()+"-"+hashCode());
        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        QDLogger.e("onLowMemory-" +getClass().getSimpleName()+"-"+hashCode());
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        QDLogger.e("onTrimMemory- level:"+level +getClass().getSimpleName()+"-"+hashCode());
    }

    public int getContentViewId() {
        //QDLogger.e("getDelegate()"+getDelegate()+",android.R.id.content="+android.R.id.content);
        return android.R.id.content;//R.id.qd_fragment_content_view;
    }
    public QuickFragmentHelper fragmentHelper;
    public QuickFragmentHelper getFragmentHelper() {
        if (fragmentHelper == null) {
            fragmentHelper = new QuickFragmentHelper(mContext,getContentViewId());
        }
        return fragmentHelper;
    }

    public void startFragment(QuickFragment fragment, int containerViewId,Intent intent) {
        //getFragmentHelper().navigate(mContext,fragment, parentId,intent);
        QuickFragmentHelper.Builder builder = new QuickFragmentHelper.Builder(mContext,fragment,getFragmentHelper());
        builder.setIntent(intent);
        builder.setContainerViewId(containerViewId);
        builder.navigation();
    }

    public void startActivity(Class<?> clazz) {
        startActivity(clazz, null);
    }

    public void startActivity(Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
        mContext.overridePendingTransition(R.anim.translate_from_right_to_left_enter, R.anim.anim_null);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        QDLogger.d( "onBackPressed" );
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        //QDLogger.d("onKeyUp=" + keyCode + ",event=" + event);
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //QDLogger.d("keyCode1=" + keyCode + ",event=" + event);
        if (fragmentHelper != null) {
            if(fragmentHelper.onKeyDown(mContext, keyCode, event)) {
                //QDLogger.d("点击事件已被fragment"+getClass().getName()+"消费 keyCode=" + keyCode + ",event=" + event);
                return true;
            }
        }
        //QDLogger.d( "keyCode2="+keyCode);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getApplicationInfo().targetSdkVersion
                    >= Build.VERSION_CODES.ECLAIR) {
                event.startTracking();
                //QDLogger.d( "keyCode3");
            } else {
                onBackPressed();
                //QDLogger.d( "keyCode4");
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 判断Activity是否Destroy
     * @param mActivity
     * @return true:已销毁
     */
    public static boolean isDestroy(Activity mActivity) {
        return mActivity == null ||
                mActivity.isFinishing() ||
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && mActivity.isDestroyed());
    }


}
