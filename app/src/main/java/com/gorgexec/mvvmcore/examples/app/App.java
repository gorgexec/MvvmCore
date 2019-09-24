package com.gorgexec.mvvmcore.examples.app;

import com.gorgexec.mvvmcore.annotations.MvvmCoreApp;
import com.gorgexec.mvvmcore.app.AppCore;
import com.gorgexec.mvvmcore.examples.app.dagger.AppComponent;
import com.gorgexec.mvvmcore.examples.app.dagger.DaggerAppComponent;


@MvvmCoreApp
public class App extends AppCore<AppComponent> {
    @Override
    public void onCreate() {
        super.onCreate();
        setAppComponent(DaggerAppComponent.factory().create(this, new AppConfig()));
    }

}
