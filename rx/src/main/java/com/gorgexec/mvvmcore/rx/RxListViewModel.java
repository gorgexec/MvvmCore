package com.gorgexec.mvvmcore.rx;

import com.gorgexec.mvvmcore.viewModel.ListViewModel;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class RxListViewModel<T> extends ListViewModel<T> {

    protected CompositeDisposable disposable = new CompositeDisposable();

    protected synchronized void rx(Disposable d){

        if(disposable==null || disposable.isDisposed()){
            disposable = new CompositeDisposable();
        }

        disposable.add(d);
    }

    protected synchronized void rxDispose(){
        if(disposable!=null && !disposable.isDisposed())
            disposable.dispose();

    }

    @Override
    protected void onCleared() {
        rxDispose();
        super.onCleared();
    }

    @Override
    public void onPause() {
        rxDispose();
        super.onPause();
    }
}
