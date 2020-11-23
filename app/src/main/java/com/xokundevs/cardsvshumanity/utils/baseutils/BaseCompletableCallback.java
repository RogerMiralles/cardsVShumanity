package com.xokundevs.cardsvshumanity.utils.baseutils;

import io.reactivex.rxjava3.observers.DisposableCompletableObserver;

public abstract class BaseCompletableCallback<View extends BasePresenter.View> extends DisposableCompletableObserver {
    protected View view;

    public BaseCompletableCallback(View _view){
        view = _view;
    }
}
