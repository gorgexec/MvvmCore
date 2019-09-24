package com.gorgexec.mvvmcore.activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.gorgexec.mvvmcore.liveData.Event;
import com.gorgexec.mvvmcore.notification.ILocalNotificationHandler;
import com.gorgexec.mvvmcore.notification.INotificationHandler;
import com.gorgexec.mvvmcore.notification.INotificationHandlerFactory;
import com.gorgexec.mvvmcore.viewModel.ViewModelCore;

import java.util.HashMap;
import java.util.Map;

public class ViewModelNotificationDispatcher extends ViewNotificationAbstractDispatcher {

    private Map<Class<?>, ILocalNotificationHandler<?>> localHandlers = new HashMap<>();

    private final ActivityCore activity;
    private final ViewModelCore model;

    public ViewModelNotificationDispatcher(@NonNull INotificationHandlerFactory factory, @NonNull LifecycleOwner owner, @NonNull ActivityCore activity, @NonNull ViewModelCore model) {
        super(factory, owner);
        this.activity = activity;
        this.model = model;
    }

    public <T> void addHandler(Class<T> notificationClass, ILocalNotificationHandler<T> handler){
        localHandlers.put(notificationClass, handler);
    }

    @SuppressWarnings("unchecked")
    public void subscribe() {
        model.notifications().observe(owner, Event.handle(notification -> {
            //local handlers
            ILocalNotificationHandler localHandler = localHandlers.get(notification.getClass());
            if(localHandler != null){
                localHandler.handle(notification);
            }
            else {
                //global handler
                INotificationHandler handler =  getGlobalHandler(notification.getClass());
                if(handler!=null){
                    handler.handle(activity, notification);
                }
            }
        }));
    }

    public void unsubscribe(){
        model.notifications().removeObservers(owner);
    }

    @Override
    public void dispose(){
        super.dispose();
        localHandlers.clear();
    }
}
