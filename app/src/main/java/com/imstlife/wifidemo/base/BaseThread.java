package com.imstlife.wifidemo.base;

/**
 * Created by ADan on 2017/12/7.
 */

public class BaseThread extends Thread {

    private BaseRunnable runnable;

    public BaseThread(BaseRunnable target) {
        super(target);
        runnable = target;
    }

    @Override
    public synchronized void start() {
        super.start();
    }

    public synchronized void end() {
        runnable.stop();
        interrupt();
    }

    public synchronized boolean isRun() {
        return runnable.isRun() || !isInterrupted();
    }
}
