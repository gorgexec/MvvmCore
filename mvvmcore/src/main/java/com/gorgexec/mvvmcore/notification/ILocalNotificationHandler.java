package com.gorgexec.mvvmcore.notification;

public interface ILocalNotificationHandler<T> {
    void handle(T notification);
}
