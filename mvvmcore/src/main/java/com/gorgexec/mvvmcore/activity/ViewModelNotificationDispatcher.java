package com.gorgexec.mvvmcore.activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.gorgexec.mvvmcore.liveData.Event;
import com.gorgexec.mvvmcore.notification.INotificationHandler;
import com.gorgexec.mvvmcore.notification.INotificationHandlerFactory;
import com.gorgexec.mvvmcore.viewModel.ViewModelCore;

import java.util.HashMap;
import java.util.Map;

public class ViewModelNotificationDispatcher extends ViewNotificationAbstractDispatcher {

    private Map<Class<?>, INotificationHandler<?>> localHandlers = new HashMap<>();

    private final ViewModelCore model;

    public ViewModelNotificationDispatcher(@NonNull INotificationHandlerFactory factory, @NonNull LifecycleOwner owner, @NonNull ViewModelCore model) {
        super(factory, owner);
        this.model = model;
    }

    public <T> void addHandler(Class<T> notificationClass, INotificationHandler<T> handler){
        localHandlers.put(notificationClass, handler);
    }

    @SuppressWarnings("unchecked")
    public void subscribe() {
        model.notifications().observe(owner, Event.map(notification -> {
            //local handlers
            INotificationHandler handler = localHandlers.get(notification.getClass());
            if (handler == null) {
                //global handler
                handler = getGlobalHandler(notification.getClass());
            }
            if(handler!=null){
                handler.handle(notification);
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
