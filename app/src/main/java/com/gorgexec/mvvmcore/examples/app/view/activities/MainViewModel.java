package com.gorgexec.mvvmcore.examples.app.view.activities;

import com.gorgexec.mvvmcore.examples.app.R;
import com.gorgexec.mvvmcore.examples.app.view.notifications.Dialog;
import com.gorgexec.mvvmcore.viewModel.ViewModelCore;

import javax.inject.Inject;

public class MainViewModel extends ViewModelCore {

    @Inject
    public MainViewModel() {
    }

    @Override
    public void onResume() {
        notifyView(Dialog.create()
                .title(R.string.mainDialogTitle)
                .message(R.string.mainDialogMessage));
    }

    public void onShowDialogClicked(){
        notifyView(Dialog.create()
                .title(R.string.mainDialogTitle)
                .message(R.string.mainDialogMessage2));
    }
}
