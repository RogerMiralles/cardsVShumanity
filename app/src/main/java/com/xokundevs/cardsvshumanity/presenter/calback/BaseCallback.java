package com.xokundevs.cardsvshumanity.presenter.calback;

import com.xokundevs.cardsvshumanity.presenter.CcaPresenter;

import java.util.Observable;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableObserver;

public abstract class BaseCallback<S> {
    CcaPresenter.CcaView view;

    public BaseCallback(CcaPresenter.CcaView view){
        this.view = view;
    }

    public abstract void onSuccess(S success);

    public abstract void onError(int errorCode);
}
