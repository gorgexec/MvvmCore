package com.gorgexec.mvvmcore.examples.template;

import com.gorgexec.mvvmcore.app.AppCoreConfig;

public class AppConfig implements AppCoreConfig {
    @Override
    public int getDefaultModelBR() {
        throw new UnsupportedOperationException("AppConfig must return actual id to ViewModel BR resource");
    }

    @Override
    public int getDefaultProgressBR() {
        return 0;
    }

    @Override
    public int getDefaultItemsBR() {
        return 0;
    }

    @Override
    public int getDefaultItemsToAddBR() {
        return 0;
    }

    @Override
    public int getDefaultItemsToUpdateBR() {
        return 0;
    }

    @Override
    public int getDefaultItemToRemoveBR() {
        return 0;
    }

    @Override
    public int getDefaultItemToAddOrUpdateBR() {
        return 0;
    }

    @Override
    public int getDefaultEmptyListTextBR() {
        return 0;
    }
}
