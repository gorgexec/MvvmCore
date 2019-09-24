package com.gorgexec.mvvmcore.activity;

import android.content.Intent;

import androidx.annotation.Nullable;

public interface IActivityResultHandler {
    void onActivityResult(ActivityCore activity, int resultCode, @Nullable Intent data);
}
