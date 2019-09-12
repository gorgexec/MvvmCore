package com.gorgexec.mvvmcore.rx;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class RxEnabled {

    protected CompositeDisposable disposable = new CompositeDisposable();
    protected void rx(Disposable d){

        if (disposable == null || disposable.isDisposed()) {
            disposable = new CompositeDisposable();
        }

        disposable.add(d);
    }

    public void dispose(){
        if( !disposable.isDisposed())
            disposable.dispose();
    }
}
