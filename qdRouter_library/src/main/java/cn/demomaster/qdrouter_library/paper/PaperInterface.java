package cn.demomaster.qdrouter_library.paper;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public interface PaperInterface {
    View onCreatView(LayoutInflater inflater,ViewGroup container);
    boolean onKeyDown(int keyCode, KeyEvent event);
}
