package com.xokundevs.cardsvshumanity.utils.baseutils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public abstract class BaseObservableUseCase<I,O> {

    private CompositeDisposable callbackList;
    private I param;

    public BaseObservableUseCase(){
        callbackList = new CompositeDisposable();
    }

    public void execute(BaseObservableCallback<O,?> callback){
        buildObservableUseCase(param).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(callback);
        callbackList.add(callback);
    }

    protected abstract Observable<O> buildObservableUseCase(I param);

    public void dispose(){
        if(!callbackList.isDisposed())
            callbackList.dispose();
    }

    public BaseObservableUseCase<I, O> setParameters(I param){
        this.param = param;
        return this;
    }
}
