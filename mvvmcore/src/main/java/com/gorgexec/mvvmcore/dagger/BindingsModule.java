package com.gorgexec.mvvmcore.dagger;

import androidx.lifecycle.ViewModelProvider;

import com.gorgexec.mvvmcore.activity.ActivityResultHandlerFactory;
import com.gorgexec.mvvmcore.activity.IActivityResultHandlerFactory;
import com.gorgexec.mvvmcore.notification.INotificationHandlerFactory;
import com.gorgexec.mvvmcore.notification.NotificationHandlerFactory;
import com.gorgexec.mvvmcore.viewModel.ViewModelFactory;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class BindingsModule {
    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory viewModelFactory);

    @Binds
    abstract INotificationHandlerFactory bindNotificationHandlerFactory(NotificationHandlerFactory factory);

    @Binds
    abstract IActivityResultHandlerFactory bindActivityResultHandlerFactory(ActivityResultHandlerFactory factory);

}