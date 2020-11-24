package com.xokundevs.cardsvshumanity.presenter.callback;

import com.xokundevs.cardsvshumanity.javaConCod.Connection;
import com.xokundevs.cardsvshumanity.presenter.EraseUserPresenter;
import com.xokundevs.cardsvshumanity.utils.ServiceError;
import com.xokundevs.cardsvshumanity.utils.baseutils.BaseCompletableCallback;

import io.reactivex.rxjava3.annotations.NonNull;

public class EraseUserCallback extends BaseCompletableCallback<EraseUserPresenter.View> {

    public EraseUserCallback(EraseUserPresenter.View _view) {
        super(_view);
    }

    @Override
    public void onComplete() {
        view.hideProgress();
        view.onEraseUserSuccess();
    }

    @Override
    public void onError(@NonNull Throwable e) {
        view.hideProgress();
        if (e instanceof ServiceError) {
            view.onEraseUserError(((ServiceError) e).getErrorCode());
        }
        else{
            view.onEraseUserError(Connection.UNKNOWN_ERROR);
        }
    }
}
