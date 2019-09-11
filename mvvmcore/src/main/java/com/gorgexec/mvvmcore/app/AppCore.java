package com.gorgexec.mvvmcore.app;

import android.app.Application;

import com.gorgexec.mvvmcore.dagger.ActivityCoreComponent;
import com.gorgexec.mvvmcore.dagger.AppCoreComponent;

public abstract class AppCore extends Application {

    public abstract AppCoreComponent getCoreAppComponent();
    public abstract ActivityCoreComponent getCoreActivityComponent();
}
