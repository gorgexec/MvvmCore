package com.gorgexec.mvvmcore.dagger;

import androidx.lifecycle.ViewModelProvider;

import com.gorgexec.mvvmcore.activity.IActivityResultHandlerFactory;
import com.gorgexec.mvvmcore.notification.INotificationHandlerFactory;


public interface ActivityCoreComponent {
    ViewModelProvider.Factory viewModels();
    INotificationHandlerFactory notificationHandlers();
    IActivityResultHandlerFactory activityResultHandlers();
}
