package com.gorgexec.mvvmcore.activity;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ActivityResultDispatcher {

    private final IActivityResultHandlerFactory factory;

    public ActivityResultDispatcher(@NonNull IActivityResultHandlerFactory factory) {
        this.factory = factory;
    }

    public void dispatch(int requestCode, int resultCode, @Nullable Intent data) {

        IActivityResultHandler handler = factory.create(requestCode);
        if (handler != null) {
            new Thread(() -> handler.onActivityResult(requestCode, resultCode, data)).start();

        }
    }

}
