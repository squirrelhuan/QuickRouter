package cn.demomaster.qdrouter_library.paper;

import android.view.View;

public interface ActionBarInterface {

   void setTitle(String title);

   void setLeftOnClickListener(View.OnClickListener onClickListener);

   void setRightOnClickListener(View.OnClickListener onClickListener);

   View getRightView(View.OnClickListener onClickListener);

}
