package com.imstlife.wifidemo;

import android.app.Application;

/**
 * Created by hengweiyu on 2018/6/11.
 */

public class MyApplication extends Application {
    private static MyApplication application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }

    public static MyApplication getInstances() {
        return application;
    }
}
