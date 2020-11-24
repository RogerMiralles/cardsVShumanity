package com.xokundevs.cardsvshumanity.presenter.callback;

import com.xokundevs.cardsvshumanity.javaConCod.Connection;
import com.xokundevs.cardsvshumanity.presenter.CreateUserPresenter;
import com.xokundevs.cardsvshumanity.utils.ServiceError;
import com.xokundevs.cardsvshumanity.utils.baseutils.BaseCompletableCallback;

import io.reactivex.rxjava3.annotations.NonNull;

public class CreateUserCallback extends BaseCompletableCallback<CreateUserPresenter.View> {

    public CreateUserCallback(CreateUserPresenter.View _view) {
        super(_view);
    }

    @Override
    public void onComplete() {
        view.hideProgress();
        view.onCreateUserSuccess();
    }

    @Override
    public void onError(@NonNull Throwable e) {
        view.hideProgress();
        if(e instanceof ServiceError) {
            view.onCreateUserError(((ServiceError) e).getErrorCode());
        }
        else{
            view.onCreateUserError(Connection.UNKNOWN_ERROR);
        }
    }
}
