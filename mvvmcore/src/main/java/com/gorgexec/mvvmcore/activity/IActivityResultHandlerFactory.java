package com.gorgexec.mvvmcore.activity;


public interface IActivityResultHandlerFactory {
    <T extends IActivityResultHandler> T create(int requestCode);
}
