package com.lovoctech.yakshanaada;

import android.app.Application;

public class YakshaNaadaApplication extends Application {

    private RxBus bus;

    @Override
    public void onCreate() {
        super.onCreate();
        bus = new RxBus();
    }

    public RxBus bus() {
        return bus;
    }
}
