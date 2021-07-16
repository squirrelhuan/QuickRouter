package cn.demomaster.qdrouter_library.base.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;

import cn.demomaster.qdlogger_library.QDLogger;
import cn.demomaster.qdrouter_library.R;
import cn.demomaster.qdrouter_library.actionbar.ActionBarTool;
import cn.demomaster.qdrouter_library.actionbar.QuickToolbar;
import cn.demomaster.qdrouter_library.base.fragment.QuickFragment;
import cn.demomaster.qdrouter_library.manager.QDActivityManager;
import cn.demomaster.qdrouter_library.manager.QuickFragmentHelper;
import cn.demomaster.qdrouter_library.util.StatusBarUtil;
import cn.demomaster.qdrouter_library.view.ImageTextView;

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

    public View getHeaderlayout() {
        return View.inflate(this,R.layout.qd_activity_actionbar_common,null);
    }

    private ActionBarTool actionBarTool;

    //获取自定义导航
    public ActionBarTool getActionBarTool() {
        if (actionBarTool == null) {
            actionBarTool = new ActionBarTool(this);
        }
        return actionBarTool;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        super.onCreate(savedInstanceState);
        QDLogger.i("onCreate-"+getClass().getSimpleName()+"-"+hashCode());
        /*if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }*/
        //getSupportActionBar();
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
        //bind在setContentView之后
        //ButterKnife.bind(this);
        initContentView();
    }

    /**
     * 对传递过来的view 再次包装
     * @param view
     * @return
     */
    public View decorateView(View view) {
        if (isUseActionBarLayout()) {//是否使用自定义导航栏
            StatusBarUtil.transparencyBar(new WeakReference<Activity>(mContext));
            //actionBarLayout = getActionBarLayout(view);
            getActionBarTool().setContentView(view);
            getActionBarTool().setActionView(getHeaderlayout());
            View view1 = getActionBarTool().inflateView();
            ImageTextView imageTextView = getActionBarTool().getLeftView();
            if (imageTextView != null) {
                imageTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
            }
            return view1;
        } else {
            return view;
        }
    }

    public void initContentView() {

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
        QDActivityManager.destroyObject(this);
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        QDLogger.i("onDestroy-"+getClass().getSimpleName()+"-"+hashCode());
        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    public QuickFragmentHelper fragmentHelper;
    public QuickFragmentHelper getFragmentHelper() {
        if (fragmentHelper == null) {
            fragmentHelper = new QuickFragmentHelper(mContext);
        }
        return fragmentHelper;
    }

    public void startFragment(QuickFragment fragment, int parentId) {
        getFragmentHelper().startFragment(fragment, parentId);
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //QDLogger.d("getAction=" + keyCode + ",event=" + event);
        if (fragmentHelper != null) {
            if (fragmentHelper.onKeyDown(mContext, keyCode, event)) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
