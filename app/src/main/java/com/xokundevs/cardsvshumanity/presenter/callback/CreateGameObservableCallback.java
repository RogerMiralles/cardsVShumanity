package com.xokundevs.cardsvshumanity.presenter.callback;

import com.xokundevs.cardsvshumanity.javaConCod.Connection;
import com.xokundevs.cardsvshumanity.presenter.CreateGamePresenter;
import com.xokundevs.cardsvshumanity.serviceoutput.ServiceCreateGameOutput;
import com.xokundevs.cardsvshumanity.utils.ServiceError;
import com.xokundevs.cardsvshumanity.utils.baseutils.BaseObservableCallback;

import io.reactivex.rxjava3.annotations.NonNull;

public class CreateGameObservableCallback extends BaseObservableCallback<ServiceCreateGameOutput, CreateGamePresenter.View> {

    public CreateGameObservableCallback(@androidx.annotation.NonNull CreateGamePresenter.View view) {
        super(view);
    }

    @Override
    public void onNext(@NonNull ServiceCreateGameOutput output) {
        view.onCreateGameSuccess(output);
        view.hideProgress();
    }

    @Override
    public void onError(@NonNull Throwable e) {
        if (e instanceof ServiceError) {
            view.onCreateGameError(((ServiceError) e).getErrorCode());
        }
        else{
            view.onCreateGameError(Connection.UNKOWN_ERROR);
        }
        view.hideProgress();
    }
}
