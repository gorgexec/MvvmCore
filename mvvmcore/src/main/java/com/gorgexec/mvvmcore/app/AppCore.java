package com.gorgexec.mvvmcore.app;

import android.app.Application;

import com.gorgexec.mvvmcore.dagger.ActivityCoreComponent;
import com.gorgexec.mvvmcore.dagger.AppCoreComponent;

public abstract class AppCore<TAppComponent extends AppCoreComponent> extends Application {

    private TAppComponent appComponent;

    public void setAppComponent(TAppComponent appComponent) {
        this.appComponent = appComponent;
    }

    public TAppComponent getAppComponent() {
        return appComponent;
    }

    public ActivityCoreComponent getActivityComponent() {
        return (ActivityCoreComponent) appComponent;
    }
}
