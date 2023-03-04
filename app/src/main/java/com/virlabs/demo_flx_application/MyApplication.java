package com.virlabs.demo_flx_application;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;
import com.google.android.exoplayer2.util.Util;
import com.orhanobut.hawk.Hawk;

public class MyApplication extends MultiDexApplication {
    private static MyApplication instance;

    protected String mUserAgent;

    @Override
    public void onCreate() {
        MultiDex.install(this);
        Hawk.init(this).build();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        super.onCreate();
        instance = this;
        initLogger();
        mUserAgent = Util.getUserAgent(this, "MyApplication");
    }

    private void initLogger() {
        if (BuildConfig.DEBUG) {

        }
    }
    public static MyApplication getInstance ()
    {
        return instance;
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }
    public boolean checkIfHasNetwork() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService( Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
    public static boolean hasNetwork ()
    {
        return instance.checkIfHasNetwork();
    }

}
