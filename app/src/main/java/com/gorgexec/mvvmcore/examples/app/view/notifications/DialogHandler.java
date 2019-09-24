package com.gorgexec.mvvmcore.examples.app.view.notifications;

import com.gorgexec.mvvmcore.activity.ActivityCore;
import com.gorgexec.mvvmcore.notification.INotificationHandler;

import javax.inject.Inject;

public class DialogHandler implements INotificationHandler<Dialog> {
    @Inject
    public DialogHandler() {
    }

    @Override
    public void handle(ActivityCore activity, Dialog notification) {
        notification.show(activity);
    }
}
