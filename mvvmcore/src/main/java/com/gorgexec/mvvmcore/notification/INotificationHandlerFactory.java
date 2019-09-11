package com.gorgexec.mvvmcore.notification;

public interface INotificationHandlerFactory {
    <T extends INotificationHandler<E>, E> T create(Class<E> clazz);
}
