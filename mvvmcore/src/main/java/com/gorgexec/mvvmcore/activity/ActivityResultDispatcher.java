package com.gorgexec.mvvmcore.activity;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ActivityResultDispatcher {

    private final IActivityResultHandlerFactory factory;

    public ActivityResultDispatcher(@NonNull IActivityResultHandlerFactory factory) {
        this.factory = factory;
    }

    public void dispatch(ActivityCore activity, int requestCode, int resultCode, @Nullable Intent data) {

        IActivityResultHandler handler = factory.create(requestCode);
        if (handler != null) {
            handler.onActivityResult(activity, resultCode, data);

        }
    }

}
