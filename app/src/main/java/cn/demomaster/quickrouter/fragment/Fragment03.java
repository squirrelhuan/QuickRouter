package cn.demomaster.quickrouter.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.demomaster.qdrouter_library.base.fragment.QuickFragment;
import cn.demomaster.qdrouter_library.base.fragment.ViewLifecycle;
import cn.demomaster.qdrouter_library.manager.QuickFragmentHelper;
import cn.demomaster.quickrouter.MainActivity3;
import cn.demomaster.quickrouter.R;

public class Fragment03 extends QuickFragment {
    TextView text;

    @Override
    public View onGenerateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_03, container,false);

        text = view.findViewById(R.id.text);
        text.setText(getClass().getSimpleName()+"-"+hashCode());
        Button button = view.findViewById(R.id.btn_startactivity);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, MainActivity3.class));
            }
        });
        return view;
    }

    @Override
    public void initView(View rootView) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }
}
