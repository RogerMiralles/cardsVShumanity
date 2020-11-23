package com.xokundevs.cardsvshumanity.utils.baseutils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public abstract class BaseCompletableUseCase<E> {
    private CompositeDisposable callbackList;
    private E param;

    public BaseCompletableUseCase(){
        callbackList = new CompositeDisposable();
    }

    public void execute(BaseCompletableCallback<?> callback){
        buildObservableUseCase(param).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(callback);
        callbackList.add(callback);
    }

    protected abstract Completable buildObservableUseCase(E param);

    public void dispose(){
        if(!callbackList.isDisposed())
            callbackList.dispose();
    }

    public BaseCompletableUseCase<E> setParameters(E param){
        this.param = param;
        return this;
    }
}
