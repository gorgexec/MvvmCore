package com.gorgexec.mvvmcore.activity;

import com.gorgexec.mvvmcore.common.MapValuesFactory;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

public class ActivityResultHandlerFactory extends MapValuesFactory<Integer, IActivityResultHandler> implements IActivityResultHandlerFactory {

    @Inject
    public ActivityResultHandlerFactory(Map<Integer, Provider<IActivityResultHandler>> creators) {
        super(creators);
    }

    @Override
    public <T extends IActivityResultHandler> T create(int requestCode) {
        return (T) super.create(requestCode);
    }
}
