package cn.demomaster.qdrouter_library.quickview;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.RequiresApi;

public class ViewInfo {
    private int id;
    private String tag;
    private int resId= Resources.ID_NULL;
    private int x;
    private int y;
    private FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    public FrameLayout.LayoutParams getLayoutParams() {
        return layoutParams;
    }

    public void setLayoutParams(FrameLayout.LayoutParams layoutParams) {
        this.layoutParams = layoutParams;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public View getView(Activity activity) {
        View view = null;
        if(resId!= Resources.ID_NULL){
           view = activity.getLayoutInflater().inflate(resId,null);
           if(view.getId()==View.NO_ID){
               if(id==View.NO_ID) {
                   id = View.generateViewId();
               }
               view.setId(id);
           }else {
               id=view.getId();
           }
        }
        return view;
    }
}
