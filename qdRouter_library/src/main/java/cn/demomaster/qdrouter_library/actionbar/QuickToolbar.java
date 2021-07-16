package cn.demomaster.qdrouter_library.actionbar;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

public class QuickToolbar extends Toolbar {
    public QuickToolbar(@NonNull Context context) {
        super(context);
    }

    public QuickToolbar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    
    public QuickToolbar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
