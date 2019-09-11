package com.gorgexec.mvvmcore.liveData;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;


public class EventObserver<T> implements Observer<Event<T>> {

    private IEventHandler<T> eventHandler;
    public EventObserver(IEventHandler<T> eventHandler){
        this.eventHandler = eventHandler;
    }

    @Override
    public void onChanged(@Nullable Event<T> tEvent) {
        if(tEvent!=null){
           T value = tEvent.getContentIfNotHandled();
           if(value!=null){
               onEventUnhandledContent(value);
           }
        }
    }

    public void onEventUnhandledContent(T value){ this.eventHandler.onEventUnhandledContent(value);}
}
