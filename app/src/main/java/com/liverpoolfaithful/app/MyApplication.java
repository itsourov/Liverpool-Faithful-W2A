package com.liverpoolfaithful.app;

import android.app.Application;

import com.google.android.gms.ads.MobileAds;

import papaya.in.admobopenads.AppOpenManager;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MobileAds.initialize(this);
        new AppOpenManager(this, getResources().getString(R.string.app_open_ad_id));

    }
}
