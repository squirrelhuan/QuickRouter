package cn.demomaster.quickrouter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.KeyEvent;

import cn.demomaster.qdrouter_library.paper.PaperManager;
import cn.demomaster.quickrouter.payer.MyPayer01;

public class MainActivity2 extends AppCompatActivity {
    PaperManager myPayerManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        myPayerManager = new PaperManager(this,R.id.frameLayout01);
        myPayerManager.addElement(new MyPayer01());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(myPayerManager.onKeyDown(this,keyCode, event)){
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}