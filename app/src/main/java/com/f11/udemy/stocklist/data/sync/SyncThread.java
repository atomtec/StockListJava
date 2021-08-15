package com.f11.udemy.stocklist.data.sync;

import android.os.Handler;
import android.os.HandlerThread;

public class SyncThread extends HandlerThread {

    private Handler mHandler;

    public SyncThread(String name) {
        super(name);
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        mHandler = new Handler(getLooper());
    }

    public Handler getHandler() {
        return mHandler;
    }
}
