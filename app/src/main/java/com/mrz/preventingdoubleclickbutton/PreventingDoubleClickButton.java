package com.mrz.preventingdoubleclickbutton;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by zhengpeng on 2016/12/7.
 */
public class PreventingDoubleClickButton extends Button {
    Context context;
    private final int INTERVAL_TIME = 500;

    public PreventingDoubleClickButton(Context context) {
        super(context);
        this.context = context;
    }

    public PreventingDoubleClickButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public PreventingDoubleClickButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    private static long lastClickTime;// 避免按钮被重复点击

    /**
     * 是否多次点击
     *
     * @return true 是重复点击
     */
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < INTERVAL_TIME) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    @Override
    public void setOnClickListener(final OnClickListener l) {
        ClassLoader classLoader = l.getClass().getClassLoader();
        OnClickListener o = (OnClickListener) Proxy.newProxyInstance(classLoader, new Class[]{OnClickListener.class}, new InvocationHandler() {
            /**
             * @param o proxy:　　代理对象
             * @param method  method:　　指代的是我们所要调用真实对象的某个方法的Method对象
             * @param objects   args:　　指代的是调用真实对象某个方法时接受的参数
             * @return
             * @throws Throwable
             */
            @Override
            public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                if (isFastDoubleClick()) {
                    Toast.makeText(context, "兄弟，你飞机打得太快了！", Toast.LENGTH_SHORT).show();
                    Log.d("MainActivity", "invoke===isFastDoubleClick");
                } else {
                    method.invoke(l, objects[0]);
                    Log.d("MainActivity", "invoke===Click");
                }
                return o;
            }
        });
        super.setOnClickListener(o);
    }
}
