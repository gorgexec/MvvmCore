package com.gorgexec.mvvmcore.stub;

import android.content.Intent;

import androidx.annotation.Nullable;

import com.gorgexec.mvvmcore.activity.ActivityCore;
import com.gorgexec.mvvmcore.activity.IActivityResultHandler;
import com.gorgexec.mvvmcore.annotations.ActivityResultHandler;

import javax.inject.Inject;

@ActivityResultHandler(-1)
public class ActivityResultHandlerStub implements IActivityResultHandler {
    @Inject
    public ActivityResultHandlerStub() {
    }

    @Override
    public void onActivityResult(ActivityCore activity, int resultCode, @Nullable Intent data) {
        //empty stub
    }
}
