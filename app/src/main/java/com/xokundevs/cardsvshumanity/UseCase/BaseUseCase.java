package com.xokundevs.cardsvshumanity.UseCase;

import android.app.job.JobScheduler;

import com.xokundevs.cardsvshumanity.presenter.calback.BaseCallback;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public abstract class BaseUseCase<E,S> {

    private BaseCallback<S> callback;

    public BaseUseCase(BaseCallback<S> callback){
        this.callback = callback;
    }

    void execute(Observable<S> entrada){
        entrada.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<S>() {
            S item;
            @Override
            public void onNext(@NonNull S s) {
                item = s;
            }

            @Override
            public void onError(@NonNull Throwable e) {
                callback.onError(0);
            }

            @Override
            public void onComplete() {
                callback.onSuccess(item);
                dispose();
            }
        });
    }

}
