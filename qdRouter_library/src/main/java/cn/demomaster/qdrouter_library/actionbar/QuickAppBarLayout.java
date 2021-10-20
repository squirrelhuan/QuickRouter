package cn.demomaster.qdrouter_library.actionbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.ObjectsCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import cn.demomaster.qdrouter_library.R;
import cn.demomaster.qdrouter_library.util.DisplayUtil;
import cn.demomaster.qdrouter_library.util.StatusBarUtil;

public class QuickAppBarLayout extends FrameLayout {
    public QuickAppBarLayout(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public QuickAppBarLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public QuickAppBarLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public QuickAppBarLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    View view;

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.QDActionBarLayout);
            int layoutId = typedArray.getResourceId(R.styleable.QDActionBarLayout_android_layout, R.layout.qd_activity_actionbar_common);
            typedArray.recycle();
            view = LayoutInflater.from(context).inflate(layoutId, null, false);
            addView(view);
            setPadding(getPaddingLeft(), DisplayUtil.getStatusBarHeight(context), getPaddingRight(), getPaddingBottom());
        }
        
        ViewCompat.setOnApplyWindowInsetsListener(
                this,
                new androidx.core.view.OnApplyWindowInsetsListener() {
                    @Override
                    public WindowInsetsCompat onApplyWindowInsets(
                            View v, @NonNull WindowInsetsCompat insets) {
                        return onWindowInsetChanged(insets);
                    }
                });
    }

    @Nullable
    WindowInsetsCompat lastInsets;

    WindowInsetsCompat onWindowInsetChanged(@NonNull final WindowInsetsCompat insets) {
        WindowInsetsCompat newInsets = null;

        if (ViewCompat.getFitsSystemWindows(this)) {
            // If we're set to fit system windows, keep the insets
            newInsets = insets;
        }

        // If our insets have changed, keep them and invalidate the scroll ranges...
        if (!ObjectsCompat.equals(lastInsets, newInsets)) {
            lastInsets = newInsets;
            requestLayout();
        }

        setPadding(getPaddingLeft(), lastInsets.getSystemWindowInsetTop(), getPaddingRight(), getPaddingBottom());
        // Consume the insets. This is done so that child views with fitSystemWindows=true do not
        // get the default padding functionality from View
        return insets.consumeSystemWindowInsets();
    }
}
