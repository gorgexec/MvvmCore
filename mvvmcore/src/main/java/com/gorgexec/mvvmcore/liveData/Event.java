package com.gorgexec.mvvmcore.liveData;

public class Event<T> {

    private T content;

    private boolean handled;
    public boolean isHandled() {
        return handled;
    }

    public T getContentIfNotHandled(){

        T res = null;
        if(!handled){
            handled = true;
            res = content;
        }

        return res;
    }

    public T peekContent(){
        return content;
    }


    public static <T> Event<T> create(T content){
        Event<T> res = new Event<>();
        res.content = content;
        return res;
    }

    public static <T> EventObserver<T> map(IEventHandler<T> eventHandler){
        return new EventObserver<>(eventHandler);
    }

}
