package com.hzmct.gpio;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.qmuiteam.qmui.arch.QMUISwipeBackActivityManager;

/**
 * @author Woong on 3/17/21
 * @website http://woong.cn
 */
public class App extends Application {
    @SuppressLint("StaticFieldLeak") private static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        QMUISwipeBackActivityManager.init(this);
    }
}
