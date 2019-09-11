package com.gorgexec.mvvmcore.activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.gorgexec.mvvmcore.notification.INotificationHandler;
import com.gorgexec.mvvmcore.notification.INotificationHandlerFactory;

public abstract class ViewNotificationAbstractDispatcher {

    protected final INotificationHandlerFactory factory;
    protected final LifecycleOwner owner;

    public ViewNotificationAbstractDispatcher(@NonNull INotificationHandlerFactory factory, @NonNull LifecycleOwner owner) {
        this.factory = factory;
        this.owner = owner;
    }

    protected <T> INotificationHandler<T> getGlobalHandler(Class<T> notificationClass) {

        try {
            //global handlers
            return factory.create(notificationClass);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void dispose(){
        unsubscribe();
    }

    public abstract void subscribe();
    public abstract void unsubscribe();
}
