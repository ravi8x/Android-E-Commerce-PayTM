package info.androidhive.paytmgateway.app;

import android.app.Application;

import info.androidhive.paytmgateway.BuildConfig;
import timber.log.Timber;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
