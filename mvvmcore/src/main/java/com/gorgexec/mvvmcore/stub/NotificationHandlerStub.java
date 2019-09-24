package com.gorgexec.mvvmcore.stub;

import com.gorgexec.mvvmcore.activity.ActivityCore;
import com.gorgexec.mvvmcore.notification.INotificationHandler;

import javax.inject.Inject;

public class NotificationHandlerStub implements INotificationHandler<ViewNotificationStub> {
    @Inject
    public NotificationHandlerStub() {
    }

    @Override
    public void handle(ActivityCore activity, ViewNotificationStub notification) {
        //empty stub
    }
}
