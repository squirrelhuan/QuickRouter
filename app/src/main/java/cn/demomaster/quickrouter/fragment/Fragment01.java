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
import cn.demomaster.quickrouter.R;

public class Fragment01 extends QuickFragment {
    TextView text;
    @Override
    public View onGenerateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_01, container,false);

        text = view.findViewById(R.id.text);
        text.setText(getClass().getSimpleName()+"-"+hashCode());
        Button btn_startfragment = view.findViewById(R.id.btn_startfragment);
        btn_startfragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentHelper().build(getActivity(),Fragment02.class.getName()).setContainerViewId(((ViewGroup) view.getParent()).getId()).putExtras(new Bundle())
                        .putExtra("password", 666666)
                        .putExtra("name", "小三").navigation();
            }
        });
        return view;
    }

    @Override
    public void initView(View rootView) {

    }

    @Override
    public boolean isUseActionBarLayout() {
        return false;
    }
}
