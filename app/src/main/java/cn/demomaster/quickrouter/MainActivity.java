package cn.demomaster.quickrouter;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import cn.demomaster.qdlogger_library.QDLogger;
import cn.demomaster.qdrouter_library.base.activity.QuickActivity;
import cn.demomaster.qdrouter_library.manager.QuickFragmentHelper;
import cn.demomaster.quickrouter.fragment.Fragment01;
import cn.demomaster.quickrouter.fragment.Fragment03;

public class MainActivity extends QuickActivity {

    ViewGroup frameLayout01;
    Button btn_add,btn_replace,btn_remove,btn_redirect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        frameLayout01 = findViewById(R.id.frameLayout01);
        btn_add = findViewById(R.id.btn_add);
        btn_replace = findViewById(R.id.btn_replace);
        btn_remove = findViewById(R.id.btn_remove);
        btn_redirect = findViewById(R.id.btn_redirect);
        QDLogger.init(this,"/test/log");
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtras(new Bundle());

                getFragmentHelper().build(MainActivity.this,Fragment01.class.getName()).setContainerViewId(frameLayout01.getId()).putExtras(new Bundle())
                        .putExtra("password", 666666)
                        .putExtra("name", "小三").navigation();
            }
        });
        btn_replace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentHelper().replaceFragment(new Fragment03(), frameLayout01.getId());
            }
        });
        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentHelper().removeFragment(mContext,getFragmentHelper().getCurrentFragment());
            }
        });
        btn_redirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtras(new Bundle());
                getFragmentHelper().build(MainActivity.this,Fragment01.class.getName()).setContainerViewId(frameLayout01.getId()).putExtras(new Bundle())
                        .putExtra("password", 666666)
                        .putExtra("name", "小三").redirect();
            }
        });
    }

}