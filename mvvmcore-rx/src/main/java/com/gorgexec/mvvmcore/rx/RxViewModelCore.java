package com.gorgexec.mvvmcore.rx;

import com.gorgexec.mvvmcore.viewModel.ViewModelCore;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class RxViewModelCore extends ViewModelCore {

    protected CompositeDisposable disposable = new CompositeDisposable();

    protected void rx(Disposable d){

        if(disposable==null || disposable.isDisposed()){
            disposable = new CompositeDisposable();
        }

        disposable.add(d);
    }

    protected void rxDispose(){
        if(disposable!=null && !disposable.isDisposed())
            disposable.dispose();

    }

    @Override
    protected void onCleared() {
        super.onCleared();
        rxDispose();
    }


}
