package com.xokundevs.cardsvshumanity.utils.baseutils;

import androidx.annotation.NonNull;

import io.reactivex.rxjava3.observers.DisposableObserver;

public abstract class BaseObservableCallback<S, View extends BasePresenter.View> extends DisposableObserver<S> {

    protected View view;

    public BaseObservableCallback(@NonNull View view){
        this.view = view;
    }

    @Override
    public void onComplete() {

    }
}
