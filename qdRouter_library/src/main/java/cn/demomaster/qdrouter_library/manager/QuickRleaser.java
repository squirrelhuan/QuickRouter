package cn.demomaster.qdrouter_library.manager;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;

import cn.demomaster.qdlogger_library.QDLogger;
import cn.demomaster.qdrouter_library.base.OnReleaseListener;

public class QuickRleaser {
    public static void release(Object obj) {
        if(obj==null){
            return;
        }

        if (obj instanceof OnReleaseListener) {
            QDLogger.e("释放："+obj);
            ((OnReleaseListener) obj).onRelease(obj);
        }else {
            releaseObjectField(obj);
        }
    }

    private static void releaseObjectField(Object obj) {
        Field[] fields = obj.getClass().getDeclaredFields();
        if (fields != null) {
            //QDLogger.e(obj+"属性个数：" + fields.length);
            for (Field field : fields) {
                // 对于每个属性，获取属性名
                //String varName = field.getName();
                //QDLogger.e(obj+".属性：" + varName);
                try {
                    // 获取原来的访问控制权限
                    boolean accessFlag = field.isAccessible();
                    // 修改访问控制权限
                    field.setAccessible(true);
                    // 获取在对象f中属性fields[i]对应的对象中的变量
                    Object o = field.get(obj);
                    if (o != null) {
                        //QDLogger.println("变量：" + varName + " = " + o);
                        if (o instanceof OnReleaseListener) {
                            //QDLogger.println("释放 OnReleaseListener：" + varName);
                            ((OnReleaseListener) o).onRelease(o);
                        } else if (o instanceof Handler) {
                            //QDLogger.println("释放handler：" + varName);
                            ((Handler) o).removeCallbacksAndMessages(null);
                        } else if (o instanceof Dialog) {
                            // QDLogger.println("释放dialog：" + varName);
                            if (((Dialog) o).isShowing()) {
                                ((Dialog) o).dismiss();
                            }
                        } else if (o instanceof PopupWindow) {
                            //QDLogger.println("释放PopupWindow：" + varName);
                            ((PopupWindow) o).dismiss();
                        } else if (o instanceof Bitmap) {
                            //QDLogger.println("释放Bitmap：" + varName);
                            if (!((Bitmap) o).isRecycled()) {
                                ((Bitmap) o).recycle();
                            }
                            //System.gc();
                        } else if (o instanceof View) {
                            //QDLogger.println("释放View：" + varName);
                            ((View) o).setOnLongClickListener(null);
                            ((View) o).setOnClickListener(null);
                            ((View) o).setOnTouchListener(null);
                            ((View) o).clearAnimation();
                            if(o instanceof EditText){
                                ((EditText) o).setCustomSelectionActionModeCallback(null);
                               // ((EditText) o).addTextChangedListener(null);
                            }
                        } else if (o instanceof Animator) {
                            //QDLogger.println("释放Animator：" + varName);
                            ((Animator) o).removeAllListeners();
                            ((Animator) o).cancel();
                            if (o instanceof ValueAnimator) {
                                ((ValueAnimator) o).removeAllUpdateListeners();
                            }
                        } else if (o instanceof Animation) {
                            ((Animation) o).setAnimationListener(null);
                            ((Animation) o).cancel();
                        }
                    }
                    // 恢复访问控制权限
                    field.setAccessible(accessFlag);
                } catch (IllegalArgumentException | IllegalAccessException ex) {
                    QDLogger.e(ex);
                }
            }
        }
    }
}
