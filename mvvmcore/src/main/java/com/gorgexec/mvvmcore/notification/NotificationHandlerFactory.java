package com.gorgexec.mvvmcore.notification;

import com.gorgexec.mvvmcore.common.MapValuesFactory;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

public class NotificationHandlerFactory extends MapValuesFactory<Class<?>, INotificationHandler<?>> implements INotificationHandlerFactory {
    @Inject
    public NotificationHandlerFactory(Map<Class<?>, Provider<INotificationHandler<?>>> creators) {
       super(creators);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends INotificationHandler<E>, E> T create(Class<E> aClass) throws RuntimeException {
        return (T)super.create(aClass);
    }


}
