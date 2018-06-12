package com.imstlife.wifidemo.base;

/**
 * Created by ADan on 2017/12/7.
 */

public abstract class BaseRunnable implements Runnable {

    private boolean isRun;

    @Override
    public void run() {
        isRun = true;
        beforeRun();
        while (isRun) {
            whileRun();
        }
    }

    public void beforeRun() {

    }

    public void afterRun() {

    }

    public abstract void whileRun();

    public void stop() {
        isRun = false;
        afterRun();
    }

    public boolean isRun() {
        return isRun;
    }
}
