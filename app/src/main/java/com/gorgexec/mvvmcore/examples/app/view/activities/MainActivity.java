package com.gorgexec.mvvmcore.examples.app.view.activities;

import android.os.Bundle;

import com.gorgexec.mvvmcore.activity.BindableActivityCore;
import com.gorgexec.mvvmcore.examples.app.R;
import com.gorgexec.mvvmcore.examples.app.databinding.ActivityMainBinding;

public class MainActivity extends BindableActivityCore<MainViewModel, ActivityMainBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind(R.layout.activity_main, MainViewModel.class);
    }
}
