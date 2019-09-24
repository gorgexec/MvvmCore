package com.gorgexec.mvvmcore.examples.app.dagger;

import android.content.Context;

import com.gorgexec.mvvmcore.app.AppCoreConfig;
import com.gorgexec.mvvmcore.dagger.ActivityCoreComponent;
import com.gorgexec.mvvmcore.dagger.AppCoreComponent;

import dagger.BindsInstance;
import dagger.Component;

@Component(modules = {CoreBindingsModule.class})
public interface AppComponent extends AppCoreComponent, ActivityCoreComponent {
    @Component.Factory
    interface Factory {
        AppComponent create(@BindsInstance Context context, @BindsInstance AppCoreConfig appCoreConfig);
    }
}
