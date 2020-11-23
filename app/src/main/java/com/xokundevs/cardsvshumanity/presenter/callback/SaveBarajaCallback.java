package com.xokundevs.cardsvshumanity.presenter.callback;

import com.xokundevs.cardsvshumanity.presenter.ModifyDeckPresenter;
import com.xokundevs.cardsvshumanity.utils.ServiceError;
import com.xokundevs.cardsvshumanity.utils.baseutils.BaseCompletableCallback;

import io.reactivex.rxjava3.annotations.NonNull;

public class SaveBarajaCallback extends BaseCompletableCallback<ModifyDeckPresenter.View> {

    public SaveBarajaCallback(ModifyDeckPresenter.View _view) {
        super(_view);
    }

    @Override
    public void onComplete() {
        view.hideProgress();
        view.onSaveDeckSuccess();
    }

    @Override
    public void onError(@NonNull Throwable e) {
        view.hideProgress();
        if (e instanceof ServiceError) {
            view.onSaveDeckError(((ServiceError) e).getErrorCode());
        }
    }
}
