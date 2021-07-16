package cn.demomaster.quickrouter.payer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import cn.demomaster.qdrouter_library.paper.Paper;
import cn.demomaster.quickrouter.R;

public class MyPayer03 extends Paper {

    @Override
    public View onCreatView(LayoutInflater inflater, ViewGroup container) {
        final View view = inflater.inflate(R.layout.fragment_03, container, false);
        Button btn_startfragment = view.findViewById(R.id.btn_startfragment);
       /* btn_startfragment.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getMyPayerManager().add();
            }
        });*/
        return view;
    }
}
