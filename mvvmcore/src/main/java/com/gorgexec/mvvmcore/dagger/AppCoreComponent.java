package com.gorgexec.mvvmcore.dagger;

import android.content.Context;

import com.gorgexec.mvvmcore.app.AppCoreConfig;

public interface AppCoreComponent {
    Context context();
    AppCoreConfig appConfig();
}
