package com.gorgexec.mvvmcore.notification;

import com.gorgexec.mvvmcore.activity.ActivityCore;

public interface INotificationHandler<T> {
    void handle(ActivityCore activity, T notification);
}
