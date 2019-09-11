package com.gorgexec.mvvmcore.notification;

public interface INotificationHandler<T> {
    void handle(T notification);
}
