package com.gorgexec.mvvmcore.examples.template.view;

import android.os.Bundle;

import com.gorgexec.mvvmcore.activity.ActivityCore;
import com.gorgexec.mvvmcore.examples.template.R;

public class MainActivity extends ActivityCore<MainViewModel> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main, MainViewModel.class);
    }
}
