package com.gorgexec.mvvmcore.examples.app;

import com.gorgexec.mvvmcore.app.AppCoreConfig;


public class AppConfig implements AppCoreConfig {
    @Override
    public int getDefaultModelBR() {
        return BR.model;
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
