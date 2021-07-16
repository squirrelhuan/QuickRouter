package cn.demomaster.qdrouter_library.paper;

import android.app.Activity;
import android.view.KeyEvent;
import android.view.ViewGroup;

import java.util.LinkedHashMap;
import java.util.Map;

public class PaperManager {
    LinkedHashMap<String, Paper> payerMap = new LinkedHashMap();
    Activity activity;
    int containerViewId;

    public Activity getActivity() {
        return activity;
    }

    public int getContainerViewId() {
        return containerViewId;
    }

    public PaperManager(Activity activity, int containerId) {
        this.activity = activity;
        this.containerViewId = containerId;
    }

    public void addElement(Object myPayer) {
        add((Paper) myPayer, myPayer.getClass().getName()+myPayer.hashCode());
    }

    public void add(Paper myPayer, String name) {
        Paper paper = getTopPaper();
        if(paper!=null){
            paper.onPause();
        }

        myPayer.setPayerManager(this);
        payerMap.put(name, myPayer);

        //添加视图
        myPayer.initLifecycle();
    }

    public void removePayer(int index) {
        int i = 0;
        for (Map.Entry entry : payerMap.entrySet()) {
            if (i == index) {
                removePayer((String) entry.getKey());
            }
            i++;
        }
    }

    public ViewGroup getContainerView() {
        return ((ViewGroup) activity.findViewById(containerViewId));
    }

    public void removePayer(String name) {
        Paper myPayer = payerMap.get(name);
        if(myPayer==null){
            return;
        }
        myPayer.onDestroyView();
        payerMap.remove(name);
        //移除视图
       // getContainerView().removeView();

        Paper paper = getTopPaper();
        if(paper!=null){
            paper.onResume();
        }
    }

    public boolean onKeyDown(Activity activity, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if(payerMap.size()>1) {
                removeTop();
                return true;
            }
        }
        return false;
    }

    private void removeTop() {
        String key = null;
        for (Map.Entry entry : payerMap.entrySet()) {
            key = (String) entry.getKey();
        }

        removePayer(key);
        return;
    }

   public Paper getTopPaper(){
        String key = null;
        for (Map.Entry entry : payerMap.entrySet()) {
            key = (String) entry.getKey();
        }
        return payerMap.get(key);
    }

    public void onDestroy() {
    }
}
