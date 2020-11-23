package com.xokundevs.cardsvshumanity.presenter.callback;

import com.xokundevs.cardsvshumanity.javaConCod.Connection;
import com.xokundevs.cardsvshumanity.presenter.CreateGamePresenter;
import com.xokundevs.cardsvshumanity.serviceoutput.ServiceSimpleDeckInfoListOutput;
import com.xokundevs.cardsvshumanity.utils.ServiceError;
import com.xokundevs.cardsvshumanity.utils.baseutils.BaseObservableCallback;

import io.reactivex.rxjava3.annotations.NonNull;

public class CreateGameGetDeckObservableCallback extends BaseObservableCallback<ServiceSimpleDeckInfoListOutput, CreateGamePresenter.View> {

    public CreateGameGetDeckObservableCallback(@NonNull CreateGamePresenter.View view) {
        super(view);
    }

    @Override
    public void onNext(@NonNull ServiceSimpleDeckInfoListOutput serviceSimpleDeckInfoListOutput) {
        view.hideProgress();
        view.onGetSimpleDeckSuccess(serviceSimpleDeckInfoListOutput);
    }

    @Override
    public void onError(@NonNull Throwable e) {
        view.hideProgress();
        if(e instanceof ServiceError){
            view.onGetSimpleDeckError(((ServiceError) e).getErrorCode());
        }
        else {
            view.onGetSimpleDeckError(Connection.UNKOWN_ERROR);
        }
    }
}
