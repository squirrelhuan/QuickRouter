package cn.demomaster.qdrouter_library.base;

/**
 * activity/fragment结束 资源释放触发
 * 请使用public修饰，否则父类中的变量无法被释放
 */
public interface OnReleaseListener {
    void onRelease(Object self);
}
